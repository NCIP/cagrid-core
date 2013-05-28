/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.tests.data.styles.cacore44.integration;

import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  SDK44ServiceStyleSystemTestConstants
 *  Constants for the SDK 4.4 with ISO 21090 types Data Service style system tests
 * 
 * @author David Ervin
 */
public class SDK44ServiceStyleSystemTestConstants {
    // the service style's internal name
    public static final String STYLE_NAME = "caCORE SDK v 4.4";
    public static final String STYLE_VERSION = "1.4";
    
    // system property to locate the Introduce base directory
    public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";
    
    // system property to locate the SDK 4.3 data service style zip
    public static final String STYLE_ZIP_PROPERTY = "style.zip.location";

    // test service naming
    public static final String SERVICE_PACKAGE = "org.cagrid.sdkquery44.test";
    public static final String SERVICE_NAME = "TestSDK44WithIsoTypesStyleDataService";
    public static final String SERVICE_NAMESPACE = "http://" + SERVICE_PACKAGE + "/" + SERVICE_NAME;
    
    // dirs
    public static final String TESTS_BASEDIR_PROPERTY = "sdk44.tests.base.dir";
    
    public static SDK44TestServiceInfo getTestServiceInfo() {
        String suffix = String.valueOf(System.currentTimeMillis());
        return new SDK44TestServiceInfo(suffix);
    }
    
    
    private static class SDK44TestServiceInfo extends DataTestCaseInfo {
        private String dirSuffix;
        
        public SDK44TestServiceInfo(String dirSuffix) {
            this.dirSuffix = dirSuffix;
        }
        
        
        public String getServiceDirName() {
            return SDK44ServiceStyleSystemTestConstants.SERVICE_NAME + "_" + dirSuffix;
        }


        public String getName() {
            return SDK44ServiceStyleSystemTestConstants.SERVICE_NAME;
        }


        public String getNamespace() {
            return SDK44ServiceStyleSystemTestConstants.SERVICE_NAMESPACE;
        }


        public String getPackageName() {
            return SDK44ServiceStyleSystemTestConstants.SERVICE_PACKAGE;
        }


        public String getExtensions() {
            return "cagrid_metadata," + super.getExtensions();
        }
    }
}