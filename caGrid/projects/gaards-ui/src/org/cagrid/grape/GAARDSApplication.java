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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.axis.utils.XMLUtils;
import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.configuration.TargetGridsConfiguration;
import org.cagrid.grape.model.Application;
import org.cagrid.grape.model.Component;
import org.cagrid.grape.model.Configuration;
import org.cagrid.grape.utils.ErrorDialog;
import org.cagrid.grape.utils.IconUtils;
import org.cagrid.ivy.Retrieve;
import org.globus.wsrf.encoding.ObjectDeserializer;

public class GAARDSApplication extends GridApplication{
	
	private static String GAARDS_CONFIG_DIR = Utils.getCaGridUserHome().getAbsolutePath() + File.separator + "gaards";
	
	private static String targetGrid = null;
	
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
    	
        try {
        	Application app = null;
        	
        	File file = null;
    		if (args.length == 0) {
    			InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("security-ui.xml");
    			
    			app = (Application) deserializeInputStream(inputStream, Application.class);
    			inputStream.close();
    			
    		} else {
    			file = new File(args[0]);
    			if (!file.exists()) {
    				System.out.println("Invalid configuration file supplied");
        			System.exit(1);
    			}
    			app = (Application) Utils.deserializeDocument(file
    					.getAbsolutePath(), Application.class);
    		}
             

            // launch the portal with the passed config
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
		for (int counter = 0; counter < grids.length; counter++) {
			Retrieve ivy = new Retrieve();
			ivy.execute("commandline-ivysettings.xml", "commandline-ivy.xml", configDirName, "caGrid", "target_grid", grids[counter].getSystemName());
			configurationManager.addConfiguration(loadConfiguration(), grids[counter]);
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
			InputStream inputStream = this.getClass().getResourceAsStream("/target-grid-configuration.xml");

			try {
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

}
