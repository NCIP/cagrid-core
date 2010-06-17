package org.cagrid.iso21090.tests.integration;

import java.io.File;

import junit.framework.Assert;

public class ExampleProjectInfo {
    
    public static final String PROPERTY_SDK_BASE = "sdk.unpack.dir";
    
    public static final String EXAMPLE_PROJECT_DIR = "sdk-toolkit" + File.separator + "iso-example-project";
    public static final String EXAMPLE_PROJECT_NAME = "example";
    public static final String EXAMPLE_PROJECT_VERSION = "4.3";


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
