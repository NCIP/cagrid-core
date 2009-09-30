package org.cagrid.grape;

import gov.nih.nci.cagrid.common.ThreadManager;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.syncgts.bean.SyncDescription;
import gov.nih.nci.cagrid.syncgts.core.SyncGTS;
import gov.nih.nci.cagrid.syncgts.core.SyncGTSDefault;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.apache.axis.utils.XMLUtils;
import org.cagrid.gaards.tasks.DownloadGridsTask;
import org.cagrid.grape.model.Application;
import org.cagrid.grape.model.Component;
import org.cagrid.grape.model.Configuration;
import org.cagrid.grape.utils.ErrorDialog;
import org.cagrid.grape.utils.IconUtils;
import org.globus.wsrf.encoding.ObjectDeserializer;

/**
 * User: kherm
 *
 * @author kherm manav.kher@semanticbits.com
 */
public class GAARDSApplication extends GridApplication{
	
	private static String GAARDS_CONFIG_DIR = Utils.getCaGridUserHome().getAbsolutePath() + File.separator + "gaards";
	
	private String targetGrid = null;
	private String targetGridUrl = null;

    public GAARDSApplication(Application app) throws Exception {
        super();
        configureTargetGrid();
        retrieveTargetGridConfigurationFiles();
        configureGlobusCertificates();

		ErrorDialog.setOwnerFrame(this);
		this.app = app;
		LookAndFeel.setApplicationLogo(this.app.getApplicationLogo());
		this.threadManager = new ThreadManager();
		this.context = new ApplicationContext(this);
		initialize();
    }

    public static void main(String[] args) {
    	
        try {
        	File file = null;
    		if (args.length == 0) {
    			System.out.println("No configuration file supplied");    
    			System.exit(1);
    		} else {
    			file = new File(args[0]);
    			if (!file.exists()) {
    				System.out.println("Invalid configuration file supplied");
        			System.exit(1);
    			}
    		}
            
			Application app = (Application) Utils.deserializeDocument(file
					.getAbsolutePath(), Application.class);

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

	private void configureTargetGrid() throws Exception {
		File configurationDir = new File(Utils.getCaGridUserHome()
				.getAbsolutePath()
				+ File.separator + "gaards");
		String defaultGrid = "training-1.3";
		String defaultTargetGridUrl = "http://software.cagrid.org/repository-1.3.0.1/caGrid/target_grid";
		if (!configurationDir.exists()) {
			Properties gaardsPropertyFile = null;
			configurationDir.mkdirs();
			gaardsPropertyFile = new Properties();
			gaardsPropertyFile.setProperty("target.grid", defaultGrid);
			gaardsPropertyFile.setProperty("target.url", defaultTargetGridUrl);
			try {
				gaardsPropertyFile.store(new FileOutputStream(configurationDir + File.separator 
						+ "gaards.properties"), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			targetGrid = defaultGrid;
			targetGridUrl = defaultTargetGridUrl;

		} else {
			Properties gaardsPropertyFile = new Properties();
			try {
				gaardsPropertyFile.load(new FileInputStream(configurationDir + File.separator 
						+ "gaards.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			targetGrid = gaardsPropertyFile.getProperty("target.grid", defaultGrid);
			targetGridUrl = gaardsPropertyFile.getProperty("target.url", defaultGrid);
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
		String syncClass = app.getConfigurationSynchronizerClass();

		ConfigurationSynchronizer cs = null;
		if (syncClass != null) {
			cs = (ConfigurationSynchronizer) Class.forName(syncClass)
					.newInstance();
		}
		
		String configurationDirectory = GAARDS_CONFIG_DIR + File.separator + targetGrid;
		configurationManager = createConfigurationManager(app
				.getConfiguration(), cs, configurationDirectory);

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
	
	private void retrieveTargetGridConfigurationFiles() throws Exception {
		DownloadGridsTask task = new DownloadGridsTask(targetGridUrl, GAARDS_CONFIG_DIR);
		task.execute();
	}
		
}
