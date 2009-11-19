package org.cagrid.tests.data.styles.cacore42;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import junit.framework.Assert;

public class ExampleProjectInfo {
    
    public static final String PROPERTY_SDK_BASE = "sdk.unpack.dir";
    
    public static final String EXAMPLE_PROJECT_DIR = "sdk-toolkit" + File.separator + "example-project";
    public static final String EXAMPLE_PROJECT_NAME = "example";
    public static final String EXAMPLE_PROJECT_VERSION = "4.2";


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
    
    
    public static File[] getMysqlDatabaseInstallFiles() throws IOException {
        // load up the install.properties
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(getInstallPropertiesFile());
        props.load(fis);
        fis.close();
        String installFileList = props.getProperty("db.install.create.mysql.file.list");
        File dbInstallDir = new File(getExampleProjectDir(), "db" + File.separator + "db-install" + File.separator + "mysql");
        StringTokenizer fileTokenizer = new StringTokenizer(installFileList, ",");
        File[] files = new File[fileTokenizer.countTokens()];
        int index = 0;
        while (fileTokenizer.hasMoreTokens()) {
            files[index] = new File(dbInstallDir, fileTokenizer.nextToken().trim());
            index++;
        }
        return files;
    }
}
