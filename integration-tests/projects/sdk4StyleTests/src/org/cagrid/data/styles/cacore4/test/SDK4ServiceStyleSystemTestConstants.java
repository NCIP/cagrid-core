package org.cagrid.data.styles.cacore4.test;

import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  SDK4ServiceStyleSystemTestConstants
 *  Constants for the SDK 4.0 Data Service style system tests
 * 
 * @author David Ervin
 * 
 * @created Feb 1, 2008 7:58:01 AM
 * @version $Id: SDK4ServiceStyleSystemTestConstants.java,v 1.1 2008-06-05 18:02:23 dervin Exp $ 
 */
public class SDK4ServiceStyleSystemTestConstants {
    // the service style's internal name
    public static final String STYLE_NAME = "caCORE SDK v 4.0";
    
    // system property to locate the Introduce base directory
    public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";

    // test service naming
    public static final String SERVICE_PACKAGE = "org.cagrid.sdkquery4.test";
    public static final String SERVICE_NAME = "TestSDK4StyleDataService";
    public static final String SERVICE_NAMESPACE = "http://" + SERVICE_PACKAGE + "/" + SERVICE_NAME;
    
    public static DataTestCaseInfo SERVICE_TEST_CASE_INFO = 
        new SDK4TestServiceInfo();
    
    private static class SDK4TestServiceInfo extends DataTestCaseInfo {
        public String getServiceDirName() {
            return SDK4ServiceStyleSystemTestConstants.SERVICE_NAME;
        }


        public String getName() {
            return SDK4ServiceStyleSystemTestConstants.SERVICE_NAME;
        }


        public String getNamespace() {
            return SDK4ServiceStyleSystemTestConstants.SERVICE_NAMESPACE;
        }


        public String getPackageName() {
            return SDK4ServiceStyleSystemTestConstants.SERVICE_PACKAGE;
        }


        public String getExtensions() {
            return "cagrid_metadata," + super.getExtensions();
        }
    }
}