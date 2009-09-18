package gov.nih.nci.cagrid.introduce.common;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.codegen.SyncTools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class IntroducePropertiesManager {
    
    private static final Logger logger = Logger.getLogger(IntroducePropertiesManager.class);
    
    private IntroducePropertiesManager(){
        
    }


    public static String getIntroduceVersion() {
        return getIntroducePropertyValue(IntroduceConstants.INTRODUCE_VERSION_PROPERTY);
    }


    public static String getIntroducePatchVersion() {
        return getIntroducePropertyValue(IntroduceConstants.INTRODUCE_PATCH_VERSION_PROPERTY);
    }

    public static String getIntroduceConfigurationFile() {
        return getIntroducePropertyValue(IntroduceConstants.INTRODUCE_CONFIGURATION_FILE);
    }

    public static String getIntroducePropertyValue(String propertyKey) {
        Properties engineProps = new Properties();
        try {
            engineProps.load(new FileInputStream(IntroduceConstants.INTRODUCE_PROPERTIES));
            return engineProps.getProperty(propertyKey);
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
    }

    
}
