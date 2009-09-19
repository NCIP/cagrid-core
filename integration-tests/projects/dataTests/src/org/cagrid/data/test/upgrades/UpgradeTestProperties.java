package org.cagrid.data.test.upgrades;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  UpgradeTestProperties
 *  Controls system properties for the upgrade tests
 * 
 * @author David Ervin
 * 
 * @created May 29, 2008 12:23:04 PM
 * @version $Id: UpgradeTestProperties.java,v 1.2 2008-05-29 18:53:17 dervin Exp $ 
 */
public class UpgradeTestProperties {
    
    private static final Log logger = LogFactory.getLog(UpgradeTestProperties.class);
    
    // system properties
    public static final String UPGRADE_SERVICES_ZIP_DIR = "upgrade.services.zip.dir";
    
    // defaults
    public static final String DEFAULT_UPGRADE_SERVICES_ZIP_DIR = "resources/services";
    public static final String DEFAULT_UPGRADE_SERVICES_EXTRACT_DIR = "test/services";
    
    private UpgradeTestProperties() {
        // prevents instantiation
    }
    
    
    public static String getUpgradeServicesZipDir() {
        return getValue(UPGRADE_SERVICES_ZIP_DIR, DEFAULT_UPGRADE_SERVICES_ZIP_DIR);
    }
    
    
    public static String getUpgradeServicesExtractDir() {
        return getValue(DataTestCaseInfo.TEST_SERVICE_BASE_DIR_PROPERTY, DEFAULT_UPGRADE_SERVICES_EXTRACT_DIR);
    }
    
    
    private static String getValue(String property, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null) {
            logger.debug("System property " + property + " not set, using default");
            System.out.println("System property " + property + " not set, using default");
            value = defaultValue;
        }
        return value;
    }
}
