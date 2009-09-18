package org.cagrid.grape;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.cagrid.grape.model.Configuration;
import org.cagrid.grape.model.ConfigurationDescriptor;
import org.cagrid.grape.model.ConfigurationDescriptors;
import org.cagrid.grape.model.ConfigurationGroup;
import org.cagrid.grape.model.ConfigurationGroups;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @created Oct 14, 2004
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class ConfigurationManager {

	private static final String GRAPE_USER_HOME = Utils.getCaGridUserHome().getAbsolutePath() 
        + File.separator + "grape";

	private Map<String, ConfigurationDescriptor> confsByName = null;

	private Map<String, Object> objectsByName = null;

	private Logger log;

	private Configuration configuration;
	
	private ConfigurationSynchronizer synchronizer;


	public ConfigurationManager(Configuration configuration, ConfigurationSynchronizer synchronizer) throws Exception {
		confsByName = new HashMap<String, ConfigurationDescriptor>();
		objectsByName = new HashMap<String, Object>();
		this.configuration = configuration;
		this.synchronizer = synchronizer;
		log = Logger.getLogger(this.getClass().getName());
		if (configuration != null) {
			File f = new File(GRAPE_USER_HOME);
			f.mkdirs();
			this.processConfigurationGroups(configuration.getConfigurationGroups());
			this.processConfigurationDescriptors(configuration.getConfigurationDescriptors());
		}
	}


	public Configuration getConfiguration() {
		return configuration;
	}


	private void processConfigurationGroups(ConfigurationGroups list) throws Exception {
		if (list != null) {
			ConfigurationGroup[] group = list.getConfigurationGroup();
			if (group != null) {
				for (int i = 0; i < group.length; i++) {
					this.processConfigurationGroup(group[i]);
				}

			}
		}
	}


	private void processConfigurationDescriptors(ConfigurationDescriptors list) throws Exception {
		if (list != null) {
			ConfigurationDescriptor[] des = list.getConfigurationDescriptor();
			if (des != null) {
				for (int i = 0; i < des.length; i++) {
					this.processConfigurationDescriptor(des[i]);
				}

			}
		}
	}


	private void processConfigurationGroup(ConfigurationGroup des) throws Exception {
		if (des != null) {
			processConfigurationDescriptors(des.getConfigurationDescriptors());
		}

	}


	private void processConfigurationDescriptor(final ConfigurationDescriptor des) throws Exception {
		if (confsByName.containsKey(des.getSystemName())) {
			throw new Exception(
				"Error configuring the application, more than one configuration was specified with the system name "
					+ des.getSystemName() + "!!!");
		} else {
			Object obj = null;
			File conf = new File(GRAPE_USER_HOME + File.separator + des.getSystemName() + "-conf.xml");
			if (!conf.exists()) {
				File template = new File(des.getDefaultFile());
				if (!template.exists()) {
					throw new Exception(
						"Error configuring the application,the default file specified for the configuration "
							+ des.getSystemName() + " does not exist!!!\n" + template.getAbsolutePath()
							+ " not found!!!");
				} else {
					obj = Utils.deserializeDocument(template.getAbsolutePath(), Class.forName(des.getModelClassname()));
					log.info("Loading configuration for " + des.getDisplayName() + " from "
						+ template.getAbsolutePath());
				}
			} else {
				obj = Utils.deserializeDocument(conf.getAbsolutePath(), Class.forName(des.getModelClassname()));
				log.info("Loading configuration for " + des.getDisplayName() + " from " + conf.getAbsolutePath());
			}

			confsByName.put(des.getSystemName(), des);
			objectsByName.put(des.getSystemName(), obj);
		}
	}


	public ConfigurationDescriptor getConfigurationDescriptor(String systemName) throws Exception {
		if (confsByName.containsKey(systemName)) {
			return confsByName.get(systemName);
		} else {
			throw new Exception("The configuration " + systemName + " does not exist!!!");
		}
	}


	public Object getConfigurationObject(String systemName) throws Exception {
		if (objectsByName.containsKey(systemName)) {
			return objectsByName.get(systemName);
		} else {
			throw new Exception("The configuration " + systemName + " does not exist!!!");
		}
	}


	public void saveAll() throws Exception {
		Iterator itr = objectsByName.keySet().iterator();
		while (itr.hasNext()) {
			save((String) itr.next(),false);
		}
		if(synchronizer!=null){
            synchronizer.syncronize(); 
         }
	}


	public void save(String systemName, boolean sync) throws Exception {
		try {
			ConfigurationDescriptor des = getConfigurationDescriptor(systemName);
			Object obj = objectsByName.get(systemName);
			File conf = new File(GRAPE_USER_HOME + File.separator + des.getSystemName() + "-conf.xml");
			QName ns = new QName(des.getQname().getNamespace(), des.getQname().getName());
			Utils.serializeDocument(conf.getAbsolutePath(), obj, ns);
			
			if((sync) && (synchronizer!=null)){
			   synchronizer.syncronize(); 
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception("Error saving the configuration " + systemName + ":\n" + e.getMessage());
		}
	}
}
