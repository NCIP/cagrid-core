package org.cagrid.tests.data.styles.cacore42;

import java.io.File;

import junit.framework.Assert;

public class ExampleProjectInfo {
    
    public static final String PROPERTY_SDK_BASE = "sdk.unpack.dir";
    
    public static final String EXAMPLE_PROJECT_DIR = "sdk-toolkit" + File.separator + "example-project";


    private ExampleProjectInfo() {
        
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
    
    
    public static File getMysqlDatabaseInstallFile() {
        return new File(getExampleProjectDir(), "db" + File.separator + "db-install" + File.separator + "mysql" + File.separator + "SDKTestSchema-mysql.sql");
    }
}
