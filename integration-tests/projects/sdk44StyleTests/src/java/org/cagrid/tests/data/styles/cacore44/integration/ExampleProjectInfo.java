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

import java.io.File;

import junit.framework.Assert;

public class ExampleProjectInfo {
    
    public static final String PROPERTY_SDK_BASE = "sdk.unpack.dir";
    
    public static final String EXAMPLE_PROJECT_DIR = "iso-example-project";
    public static final String EXAMPLE_PROJECT_NAME = "isoExample";
    public static final String EXAMPLE_PROJECT_VERSION = "4.4";


    private ExampleProjectInfo() {
        // nothing to see here
    }
    
    
    public static File getSdkDir() {
        String dirName = System.getProperty(PROPERTY_SDK_BASE);
        Assert.assertNotNull("System property " + PROPERTY_SDK_BASE + " not defined or empty", dirName);
        return new File(dirName);
    }
    
    
    public static File getExampleProjectDir() {
        return new File(getSdkDir(), EXAMPLE_PROJECT_DIR);
    }
    
    
    public static File getCodegenPropertiesFile() {
        return new File(getExampleProjectDir(), "build" + File.separator + "codegen.properties");
    }
    
    
    public static File getInstallPropertiesFile() {
        return new File(getExampleProjectDir(), "build" + File.separator + "install.properties");
    }
}
