package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis.utils.StringUtils;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.cagrid.grape.configuration.Grid;
import org.cagrid.grape.configuration.ServiceConfiguration;
import org.cagrid.grape.configuration.TargetGridsConfiguration;
import org.cagrid.grape.model.Configuration;
import org.cagrid.ivy.Discover;
import org.cagrid.ivy.Retrieve;
import org.globus.wsrf.encoding.ObjectDeserializer;

public class TargetGridsManager {
	private static Log log = LogFactory.getLog(TargetGridsManager.class);
	
	private File configurationDirectory = null;
	private ConfigurationManager configurationManager = null;
	private URL ivyURL = null;
	private URL ivySettingsURL = null;
	
	public TargetGridsManager(String configurationDirectory, ConfigurationManager configurationManager) {
		this(new File(configurationDirectory), configurationManager);
	}
	
	public TargetGridsManager(File configurationDirectory, ConfigurationManager configurationManager) {
		this.configurationDirectory = configurationDirectory;
		
		this.configurationManager = configurationManager;
		ivyURL = this.getClass().getResource("/ivy-default.xml");
		ivySettingsURL = this.getClass().getResource("/ivysettings-default.xml");
	}

	public boolean findNewGrids(TargetGridsConfiguration targetGridsConfiguration, Grid[] grids) throws Exception {
		boolean discoveredNewGrids = false;

		List<String> settings = new ArrayList<String>();
		for (Grid grid : grids) {
			if (!settings.contains(grid.getIvySettings())) {
				settings.add(grid.getIvySettings());
				if ("local".equals(grid.getIvySettings())) {
					continue;
				}
				String repositoryName = "default";
				URL settingsURL = null;
				File settingsFile = new File(configurationDirectory, grid.getIvySettings());
				if (settingsFile.exists()) {
					settingsURL = settingsFile.toURI().toURL();
					repositoryName = settingsFile.getName().substring(13, settingsFile.getName().length()-4);
				} else {
					settingsURL = ivySettingsURL;
				}
				
				Retrieve gridRetriever = new Retrieve(settingsURL, configurationDirectory.getAbsolutePath() + File.separator + "cache");
				Discover gridDiscover = new Discover(settingsURL, configurationDirectory.getAbsolutePath() + File.separator + "cache");

				ModuleRevisionId[] mrids = gridDiscover.execute("caGrid", "target_grid", "*");

				for (ModuleRevisionId moduleRevisionId : mrids) {
					String systemName = moduleRevisionId.getRevision();

					if (discoveredNewGrid(grids, systemName)) {
						Grid discoveredGrid = new Grid();

						discoveredGrid.setDisplayName(gridDiscover.getDisplayName(moduleRevisionId));
						discoveredGrid.setSystemName(systemName);
						discoveredGrid.setIvySettings(settingsURL.toURI().toString());
						discoveredGrid.setVersion(moduleRevisionId.getRevision());
						gridRetriever.execute(ivyURL, configurationDirectory.getAbsolutePath(), "caGrid", "target_grid", discoveredGrid);

						Grid[] newGrid = new Grid[grids.length + 1];

						System.arraycopy(grids, 0, newGrid, 0, grids.length);
						newGrid[grids.length] = discoveredGrid;

						configurationManager.addConfiguration(loadConfiguration(), discoveredGrid);
						targetGridsConfiguration.setGrid(newGrid);
						grids = newGrid;
						discoveredNewGrids = true;
					}
				}
			}
		}

		return discoveredNewGrids;
	}
	
	public void getGridsFromRepository(File ivySettings, TargetGridsConfiguration targetGridsConfiguration) throws Exception {
				
		Retrieve gridRetriever = new Retrieve(ivySettings.toURI().toURL(), configurationDirectory.getAbsolutePath() + File.separator + "cache");
		Discover gridDiscover = new Discover(ivySettings.toURI().toURL(), configurationDirectory.getAbsolutePath() + File.separator + "cache");

		ModuleRevisionId[] mrids = gridDiscover.execute("caGrid", "target_grid", "*");

		for (ModuleRevisionId moduleRevisionId : mrids) {
			Grid[] grids = targetGridsConfiguration.getGrid();

			String systemName = moduleRevisionId.getRevision();

			Grid discoveredGrid = new Grid();

			discoveredGrid.setDisplayName(gridDiscover.getDisplayName(moduleRevisionId));
			discoveredGrid.setSystemName(systemName);
			discoveredGrid.setIvySettings(ivySettings.getName());
			discoveredGrid.setVersion(moduleRevisionId.getRevision());
			gridRetriever.execute(ivyURL, configurationDirectory.getAbsolutePath(), "caGrid", "target_grid", discoveredGrid);

			Grid[] newGrid = new Grid[grids.length + 1];

			System.arraycopy(grids, 0, newGrid, 0, grids.length);
			newGrid[grids.length] = discoveredGrid;

			configurationManager.addConfiguration(loadConfiguration(), discoveredGrid);
			targetGridsConfiguration.setGrid(newGrid);
		}
	}		


	public boolean updateGridConfigurationFiles(Grid[] grids) throws Exception {
		boolean updatedGridConfiguration = false;

		for (int counter = 0; counter < grids.length; counter++) {
			URL settingsURL = null;
			String repositoryName = "default";
			if ("local".equals(grids[counter].getIvySettings())) {
				// Grid added by interface
				// Nothing to retrieve
				continue;
			} else if (!StringUtils.isEmpty(grids[counter].getIvySettings())) {
				File settingsFile = new File(configurationDirectory, grids[counter].getIvySettings());
				if (settingsFile.exists()) {
					settingsURL = settingsFile.toURI().toURL();
					repositoryName = settingsFile.getName().substring(13, settingsFile.getName().length()-4);
				} else {
					settingsURL = ivySettingsURL;
				}
			}
			Retrieve gridRetriever = new Retrieve(settingsURL, configurationDirectory.getAbsolutePath() + File.separator + "cache");

			int retrieved = gridRetriever.execute(ivyURL, configurationDirectory.getAbsolutePath(), "caGrid", "target_grid", grids[counter]);
			if (retrieved > 0) {
				updatedGridConfiguration = true;
				Discover gridDiscover = new Discover(settingsURL, configurationDirectory.getAbsolutePath() + File.separator + "cache");
				String newDisplayName = gridDiscover.getDisplayName("caGrid", "target_grid", grids[counter].getVersion());
				grids[counter].setDisplayName(newDisplayName);
			}
			
			File gridConfigurationDir = new File(configurationDirectory, grids[counter].getSystemName());
			deleteDuplicateConfFiles(gridConfigurationDir);
			if (updatedGridConfiguration && conflictingConfs(gridConfigurationDir)) {
				YesNoDialog.showChoice("The updated configuration files for Grid: "+grids[counter].getSystemName()+", conflict with your locally modified versions.  Do you wish to keep your current version or delete it and use the updated version.", gridConfigurationDir.getAbsolutePath());
			}

			configurationManager.addConfiguration(loadConfiguration(), grids[counter]);
		}
		return updatedGridConfiguration;
	}

	private boolean discoveredNewGrid(Grid[] grids, String gridName) {
		for (int i = 0; i < grids.length; i++) {
			if (grids[i].getSystemName().equalsIgnoreCase(gridName)) {
				return false;
			}
		}
		return true;
	}

	private void deleteDuplicateConfFiles(File configurationDir) {
		int SAVED_CONFIGFILE_SUFFIX_LENGTH = 9;
		
		if (!configurationDir.exists()) {
			return;
		}
		
		FilenameFilter conf = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith("conf.xml"))
					return true;
				else
					return false;
			}
		};
		File[] confFiles = configurationDir.listFiles(conf);
				
		for (int i = 0; i < confFiles.length; i++) {
			String configurationFileName = confFiles[i].getAbsolutePath().substring(0, confFiles[i].getAbsolutePath().length() - SAVED_CONFIGFILE_SUFFIX_LENGTH) + "-services-configuration.xml";

			try {
				ServiceConfiguration configurationSC = loadServiceConfiguration(new File(configurationFileName));
				ServiceConfiguration confSC = loadServiceConfiguration(confFiles[i]);
				if (confSC.equals(configurationSC)) {
					confFiles[i].delete();
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	private boolean conflictingConfs(File configurationDir) {
		if (!configurationDir.exists()) {
			return false;
		}
		
		FilenameFilter conf = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith("conf.xml"))
					return true;
				else
					return false;
			}
		};
		File[] confFiles = configurationDir.listFiles(conf);
		
		if (confFiles != null && confFiles.length >= 1) {
			return true;
		}
		
		return false;
	}
	
	private Configuration loadConfiguration() throws Exception {
		InputStream inputStream = this.getClass().getResourceAsStream("/configuration.xml");
		
        org.w3c.dom.Document doc = XMLUtils.newDocument(inputStream);
        Object obj = ObjectDeserializer.toObject(doc.getDocumentElement(), Configuration.class);
        inputStream.close();
        return Configuration.class.cast(obj);

	}

	private ServiceConfiguration loadServiceConfiguration(File serviceFile) throws Exception {
		return (ServiceConfiguration) Utils.deserializeDocument(serviceFile
				.getAbsolutePath(), ServiceConfiguration.class);
	}


}
