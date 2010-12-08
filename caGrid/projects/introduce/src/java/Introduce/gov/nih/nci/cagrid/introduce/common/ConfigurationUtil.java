package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.configuration.IntroducePortalConfiguration;
import gov.nih.nci.cagrid.introduce.beans.configuration.IntroduceServiceDefaults;
import gov.nih.nci.cagrid.introduce.beans.extension.Properties;
import gov.nih.nci.cagrid.introduce.beans.extension.PropertiesProperty;

import org.cagrid.grape.ConfigurationManager;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.model.Application;


/**
 * Class for accessing the configuration files.
 * 
 * @author hastings
 */
public class ConfigurationUtil {

    private static ConfigurationUtil util = null;
    private static ConfigurationManager configurationManager = null;


    private ConfigurationUtil() throws Exception {
        if (GridApplication.getContext() != null) {
            configurationManager = GridApplication.getContext().getConfigurationManager();
        } else {
            Application app = null;
            app = (Application) Utils.deserializeDocument(IntroducePropertiesManager.getIntroduceConfigurationFile(),
                Application.class);
            configurationManager = new ConfigurationManager(app.getConfiguration(),null);
        }

    }


    private static synchronized void load() throws Exception {
        if (util == null) {
            util = new ConfigurationUtil();
        }
    }


    public static synchronized  ConfigurationUtil getInstance() throws Exception {
        load();
        return util;
    }

    public static synchronized  void saveConfiguration() throws Exception {
        getInstance().configurationManager.saveAll();
    }

    public static synchronized IntroducePortalConfiguration getIntroducePortalConfiguration() {
        try {
            return (IntroducePortalConfiguration) getInstance().configurationManager
                .getConfigurationObject("introducePortal");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static synchronized IntroduceServiceDefaults getIntroduceServiceDefaults() {
        try {
            return (IntroduceServiceDefaults) getInstance().configurationManager
                .getConfigurationObject("introduceServiceDefaults");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static synchronized Properties getGlobalExtensionProperties() {
        try {
            return (Properties) getInstance().configurationManager
                .getConfigurationObject("introduceGlobalExtensionProperties");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static synchronized PropertiesProperty getGlobalExtensionProperty(String key) throws Exception {
        getInstance();
        if (getGlobalExtensionProperties() != null && getGlobalExtensionProperties().getProperty() != null) {
            for (int i = 0; i < getGlobalExtensionProperties().getProperty().length; i++) {
                if (getGlobalExtensionProperties().getProperty()[i].getKey().equals(key)) {
                    return getGlobalExtensionProperties().getProperty(i);
                }
            }
        }
        return null;
    }

}
