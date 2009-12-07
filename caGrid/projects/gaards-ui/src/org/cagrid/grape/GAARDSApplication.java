package org.cagrid.grape;

import gov.nih.nci.cagrid.common.ThreadManager;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import org.cagrid.gaards.ui.dorian.ServicesManager;
import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.configuration.TargetGridsConfiguration;
import org.cagrid.grape.model.Application;
import org.cagrid.grape.model.Component;
import org.cagrid.grape.model.Configuration;
import org.cagrid.grape.utils.BusyDialogRunnable;
import org.cagrid.grape.utils.ErrorDialog;
import org.cagrid.grape.utils.IconUtils;
import org.globus.wsrf.encoding.ObjectDeserializer;

public class GAARDSApplication extends GridApplication{
	
	private static File gaardsConfigurationDirectory = new File(Utils.getCaGridUserHome(), "gaards");
	
	private static String targetGrid = null;
	
	private static URL ivySettingsURL = null;
	private static URL targetGridURL = null;
		
    public GAARDSApplication(Application app) throws Exception {
        super();

		ErrorDialog.setOwnerFrame(this);
		YesNoDialog.setOwnerFrame(this);
		
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
        	
        	setGAARDSConfigurationDirectory(); 
        	GAARDSApplication applicationInstance = (GAARDSApplication) GAARDSApplication.getInstance(app);

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
            applicationInstance.gridInitialization();
            applicationInstance.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

	private static void loadDefaultConfigurationFiles() {
		Throwable obj = new Throwable();
    	targetGridURL = obj.getClass().getResource("/target-grid-configuration.xml");
	}

	private static Application loadApplicationSettings() {
		Application app = null;
		try {
			Throwable obj = new Throwable();
			InputStream inputStream = obj.getClass().getResourceAsStream("/security-ui.xml");
			app = (Application) deserializeInputStream(inputStream, Application.class);
			inputStream.close();
		} catch (Exception e) {
			System.out.println("Failed to load the security configuration");
			e.printStackTrace();
			System.exit(1);
		}
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
			
		setupTargetGridsConfigurationFile();
        
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
	
	private void configureGlobusCertificates() throws Exception {
		File globusCertificateDir = Utils.getTrustedCerificatesDirectory();
		File gridTargetCertsDir = new File(gaardsConfigurationDirectory, targetGrid + File.separator + "certificates");

		Utils.copyDirectory(gridTargetCertsDir, globusCertificateDir);

		SyncGTSDefault.setServiceSyncDescriptionLocation(gaardsConfigurationDirectory.getAbsolutePath()
				+ File.separator + targetGrid + File.separator
				+ "sync-description.xml");

		SyncDescription description = SyncGTSDefault.getSyncDescription();

		SyncGTS sync = SyncGTS.getInstance();
		sync.syncOnce(description);
	}
					
	private void getTargetGrids() throws Exception {
		TargetGridsConfiguration targetGridsConfiguration = (TargetGridsConfiguration) configurationManager.getConfigurationObjectByConfiguration("target-grid", "default");
		
		targetGrid = targetGridsConfiguration.getActiveGrid();
		
		Grid[] grids = targetGridsConfiguration.getGrid();
		
		TargetGridsManager gridManager = new TargetGridsManager(gaardsConfigurationDirectory, configurationManager);
		
		boolean updatedGridConfiguration = gridManager.updateGridConfigurationFiles(grids);
		boolean discoveredNewGrids = gridManager.findNewGrids(targetGridsConfiguration, grids);
		
		configurationManager.setActiveConfiguration(targetGrid);
		
		if (updatedGridConfiguration || discoveredNewGrids) {
			configurationManager.save("target-grid", false);
		}
	}

		
	private void setupTargetGridsConfigurationFile() {
		File targetGridsConfigurationFile = new File(gaardsConfigurationDirectory, "target-grid-configuration.xml");
		
		if (!gaardsConfigurationDirectory.exists()) {
			gaardsConfigurationDirectory.mkdirs();
		}
		
		if (!targetGridsConfigurationFile.exists()) {
			try {
				Utils.stringBufferToFile(Utils.inputStreamToStringBuffer(targetGridURL.openStream()), targetGridsConfigurationFile);
			} catch (IOException e) {
				System.out.println("Unable to write to file: " + targetGridsConfigurationFile.getName());
				e.printStackTrace();
				System.exit(1);
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
		Option targetgridfile = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given targetgrid").create("targetgrid");
		Option targetgrid = OptionBuilder.withArgName("file").hasArg()
				.withDescription("use given grid").create("grid");
		
		Options options = new Options();

		options.addOption(ivysettingsfile);
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
					return optionFile.toURI().toURL();
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
	

	
	public void gridInitialization() throws Exception {
		BusyDialogRunnable r = new BusyDialogRunnable(GridApplication
				.getContext().getApplication(), "Configuring Target Grids") {
			@Override
			public void process() {
				try {
					setProgressText("Configuring Target Grids");
					String syncClass = app.getConfigurationSynchronizerClass();

					ConfigurationSynchronizer cs = null;
					if (syncClass != null) {
						cs = (ConfigurationSynchronizer) Class.forName(
								syncClass).newInstance();
					}

					configurationManager = createConfigurationManager(app
							.getConfiguration(), cs, gaardsConfigurationDirectory.getAbsolutePath());

					getTargetGrids();
					configureGlobusCertificates();
					ServicesManager.getInstance();

				} catch (Exception ex) {
					ex.printStackTrace();
					setErrorMessage("Error: " + ex.getMessage());
					return;
				}
			}
		};

		Thread th = new Thread(r);
		th.start();
		
	}
	
	
	private static void setGAARDSConfigurationDirectory() {
		String version = null;
		Properties props = new Properties(); 
		try {
			props.load(props.getClass().getResourceAsStream("/project.properties"));
			version = (String) props.get("project.version");
		} catch (Exception e) {
			System.out.println("Unable to determine the version of the GAARDS UI.");
			version = "";
		}
		
		version = (version == null || version.length() == 0) ? "" : "-" + version;
		
		gaardsConfigurationDirectory = new File(Utils.getCaGridUserHome(), "gaards" + version);
	}
	
	public static File getGAARDSConfigurationDirectory() {
		return gaardsConfigurationDirectory;
	}

}
