package org.cagrid.grape;

import gov.nih.nci.cagrid.common.ThreadManager;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.axis.utils.XMLUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.configuration.TargetGridsConfiguration;
import org.cagrid.grape.model.Application;
import org.cagrid.grape.model.Component;
import org.cagrid.grape.model.Configuration;
import org.cagrid.grape.utils.ErrorDialog;
import org.cagrid.grape.utils.IconUtils;
import org.cagrid.ivy.Discover;
import org.cagrid.ivy.Retrieve;
import org.globus.wsrf.encoding.ObjectDeserializer;

public class GAARDSApplication extends GridApplication{
	
	private static String GAARDS_CONFIG_DIR = Utils.getCaGridUserHome().getAbsolutePath() + File.separator + "gaards";
	
	private static String targetGrid = null;
	
	private static URL ivySettingsURL = null;
	private static URL ivyURL = null;
	private static URL targetGridURL = null;
		
    public GAARDSApplication(Application app) throws Exception {
        super();

		ErrorDialog.setOwnerFrame(this);
		this.app = app;
		LookAndFeel.setApplicationLogo(this.app.getApplicationLogo());
		this.threadManager = new ThreadManager();
		this.context = new ApplicationContext(this);
		initialize();
    }

    public static void main(String[] args) {
    	loadDefaultConfigurationFiles();
   	
        try {
        	Application app = null;
        	    			
        	processCommandLineParameters(args);
        	
        	app = loadApplicationSettings();
             
            GridApplication applicationInstance = GAARDSApplication.getInstance(app);

            try {
                applicationInstance.pack();
            } catch (Exception e) {
                applicationInstance.setIconImage(null);
                applicationInstance.pack();
            }
            if (app.getDimensions() != null) {
                Dimension d = new Dimension(app.getDimensions().getWidth(), app.getDimensions().getHeight());
                applicationInstance.setSize(d);
            }

            applicationInstance.setVisible(true);
            applicationInstance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private static void loadDefaultConfigurationFiles() {
		Throwable obj = new Throwable();
    	ivySettingsURL = obj.getClass().getResource("/commandline-ivysettings.xml");
    	ivyURL = obj.getClass().getResource("/commandline-ivy.xml");
    	targetGridURL = obj.getClass().getResource("/target-grid-configuration.xml");
	}

	private static Application loadApplicationSettings() throws Exception,
			IOException {
		Application app;
		InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("security-ui.xml");
		app = (Application) deserializeInputStream(inputStream, Application.class);
		inputStream.close();
		return app;
	}
	
	public static GridApplication getInstance(Application app) throws Exception {
		if (application == null) {
			application = new GAARDSApplication(app);
			application.startPostInitializer();
			return application;
		} else {
			throw new Exception(
					"An instance of the Grid Application has already been created.");
		}
	}
	
	protected void initialize() throws Exception {

        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.out.println("Failed to set system look and feel.");
		}
		
		startPreInitializer();
			
		targetGridsConfiguration();
        
		String syncClass = app.getConfigurationSynchronizerClass();

		ConfigurationSynchronizer cs = null;
		if (syncClass != null) {
			cs = (ConfigurationSynchronizer) Class.forName(syncClass)
					.newInstance();
		}
		
		String configurationDirectory = GAARDS_CONFIG_DIR;
		configurationManager = createConfigurationManager(app
				.getConfiguration(), cs, configurationDirectory);
		
        getTargetGrids();
        configureGlobusCertificates();

		List<Component> toolbarComponents = new ArrayList<Component>();
		this.setJMenuBar(getJJMenuBar(toolbarComponents));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getJScrollPane(), BorderLayout.CENTER);
		this.getContentPane().add(getToolBar(toolbarComponents),
				BorderLayout.NORTH);

		this.setTitle(app.getName());

		if (app.getIcon() != null) {
			ImageIcon icon = IconUtils.loadIcon(app.getIcon());
			if (icon != null) {
				this.setIconImage(icon.getImage());
			}
		}

	}
	
	private ConfigurationManager createConfigurationManager(
			Configuration conf, ConfigurationSynchronizer cs, String configurationDirectory) throws Exception {
		return new ConfigurationManager(conf, cs, configurationDirectory);
	}
	
	private void configureGlobusCertificates () throws Exception {
		File globusCertificateDir = new File(System.getProperty("user.home") + File.separator + ".globus" + File.separator + "certificates");
		if (!globusCertificateDir.exists()) {
			globusCertificateDir.mkdirs();
			File gridTargetCertsDir = new File(GAARDS_CONFIG_DIR + File.separator + targetGrid + File.separator + "certificates");

			Utils.copyDirectory(gridTargetCertsDir, globusCertificateDir);

			SyncGTSDefault.setServiceSyncDescriptionLocation(GAARDS_CONFIG_DIR + File.separator + targetGrid + File.separator + "sync-description.xml");
			
			SyncDescription description = SyncGTSDefault.getSyncDescription();

			SyncGTS sync = SyncGTS.getInstance();
			sync.syncOnce(description);
		}
	}
		
	private Configuration loadConfiguration() throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("/configuration.xml");
		
        org.w3c.dom.Document doc = XMLUtils.newDocument(inputStream);
        Object obj = ObjectDeserializer.toObject(doc.getDocumentElement(), Configuration.class);
        inputStream.close();
        return Configuration.class.cast(obj);

	}
			
	private void getTargetGrids() throws Exception {
		String configDirName = Utils.getCaGridUserHome().getAbsolutePath() + File.separator + "gaards";
		File targetGridConfFile = new File(configDirName + File.separator + "target-grid-conf.xml");
		if (!targetGridConfFile.exists()) {
			targetGridConfFile = new File(configDirName + File.separator + "target-grid-configuration.xml");			
		}
		
		TargetGridsConfiguration targetGridsConfiguration = null;
		try {
			targetGridsConfiguration = (TargetGridsConfiguration) Utils.deserializeDocument(targetGridConfFile
					.getAbsolutePath(), TargetGridsConfiguration.class);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		if (targetGridsConfiguration == null)
			return;
		
		targetGrid = targetGridsConfiguration.getActiveGrid();
		
		Grid[] grids = targetGridsConfiguration.getGrid();
		Retrieve ivy = new Retrieve();
		for (int counter = 0; counter < grids.length; counter++) {
			ivy.execute(ivySettingsURL, ivyURL, configDirName, "caGrid", "target_grid", grids[counter].getSystemName());
			configurationManager.addConfiguration(loadConfiguration(), grids[counter]);
		}
		Discover discover = new Discover();
		ModuleRevisionId[] mrids = discover.execute("caGrid", "target_grid", ivySettingsURL);
		
		for (int counter = 0; counter < mrids.length; counter++) {
			String systemName = mrids[counter].getRevision();
			//configurationManager.addConfiguration(loadConfiguration(), grids[counter]);
			if ("nci_stage-1.3".equals(systemName)) {
				Grid grid = new Grid();
				grid.setDisplayName(systemName);
				grid.setSystemName(systemName);
				ivy.execute(ivySettingsURL, ivyURL, configDirName, "caGrid", "target_grid", systemName);

				Grid[] newGrid = new Grid[grids.length + 1];
				

				System.arraycopy(grids, 0, newGrid, 0, grids.length);
				newGrid[grids.length] = grid;
				
				configurationManager.addConfiguration(loadConfiguration(), grid);	
//				configurationManager.saveAll();
			}
		}
		
		configurationManager.setActiveConfiguration(targetGrid);
	}
		
	private void targetGridsConfiguration() {
		File configurationDir = new File(Utils.getCaGridUserHome()
				.getAbsolutePath()+ File.separator+ "gaards");
		File targetGridsConfigurationFile = new File(configurationDir, "target-grid-configuration.xml");
		
		if (!configurationDir.exists()) {
			configurationDir.mkdirs();
		}
		
		if (!targetGridsConfigurationFile.exists()) {
			try {
				InputStream inputStream = targetGridURL.openStream();

				FileOutputStream fos = new FileOutputStream(targetGridsConfigurationFile);
				while (inputStream.available() > 0) {
					fos.write(inputStream.read());
				}
				fos.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(ERROR);
			}
		}
	}
	
	private static <T> T deserializeInputStream(InputStream inputStream, Class<T> objectType) throws Exception {
        org.w3c.dom.Document doc = XMLUtils.newDocument(inputStream);
        Object obj = ObjectDeserializer.toObject(doc.getDocumentElement(), objectType);
        return objectType.cast(obj);
    }
	
	@SuppressWarnings("static-access")
	private static void processCommandLineParameters(String[] args) {
		Option ivysettingsfile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given ivysettings").create("ivysettings");
		Option ivyfile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given ivy").create("ivy");
		Option targetgridfile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given targetgrid").create("targetgrid");
		Option targetgrid = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given grid").create("grid");
		
		Options options = new Options();

		options.addOption(ivysettingsfile);
		options.addOption(ivyfile);
		options.addOption(targetgridfile);
		options.addOption(targetgrid);
		
	    CommandLineParser parser = new GnuParser();
	    CommandLine line = null;
	    try {
			line = parser.parse( options, args );
		} catch (org.apache.commons.cli.ParseException e) {
			System.out.println("Unable to parse the command line options");
			System.exit(1);
		}
		
		ivySettingsURL = getConfigurationFiles(line, "ivysettings", ivySettingsURL);
		ivyURL = getConfigurationFiles(line, "ivy", ivyURL);
		targetGridURL = getConfigurationFiles(line, "targetgrid", targetGridURL);
		if (line.hasOption("grid")) {
			targetGrid = line.getOptionValue("grid");
		}
	}

	private static URL getConfigurationFiles(CommandLine line, String option, URL configurationURL) {
		try {
			if (line.hasOption(option)) {
				String optionFilename = line.getOptionValue(option);
				File optionFile = new File(optionFilename);
				if (optionFile.exists()) {
					return optionFile.toURL();
				} else {
					System.out.println("Invalid " + option + " file supplied");
					System.exit(1);
				}
			}
		} catch (Exception e) {
			System.out.println("Invalid " + option + " file supplied");
			System.exit(1);
		}
		return configurationURL;
	}
	
	public static String getTargetGrid() {
		return targetGrid;
	}
	
	public static void setTargetGrid(String grid) {
		targetGrid = grid;
	}

}
