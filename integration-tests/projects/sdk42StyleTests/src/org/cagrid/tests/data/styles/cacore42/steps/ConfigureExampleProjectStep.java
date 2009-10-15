package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.cagrid.tests.data.styles.cacore42.DatabaseProperties;
import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;

public class ConfigureExampleProjectStep extends Step {
    // codegen properties
    public static final String NAMESPACE_PREFIX_PROPERTY = "NAMESPACE_PREFIX";
    public static final String NAMESPACE_PREFIX_VALUE = "gme://caCORE.caCORE/4.2";
    
    // install properties
    public static final String SERVER_TYPE = "SERVER_TYPE";
    public static final String SERVER_TYPE_VALUE = "tomcat";
    public static final String EXCLUDE_DATABASE = "exclude.database";
    public static final String EXCLUDE_DATABASE_VALUE = "false";
    public static final String INSTALL_CONTAINER = "INSTALL_CONTAINER";
    public static final String INSTALL_CONTAINER_VALUE = "false";
    public static final String DB_TYPE = "DB_TYPE";
    public static final String DB_TYPE_VALUE = "mysql";
    public static final String DB_SERVER = "DB_SERVER";
    public static final String DB_SERVER_PORT = "DB_SERVER_PORT";
    public static final String DB_NAME = "DB_NAME";
    public static final String DB_USERNAME = "DB_USERNAME";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    
    public ConfigureExampleProjectStep() {
        super();
    }


    public void runStep() throws Throwable {
        // edit the codegen properties
        try {
            FileInputStream codegenIn = new FileInputStream(ExampleProjectInfo.getCodegenPropertiesFile());
            Properties codegenProps = new Properties();
            codegenProps.load(codegenIn);
            codegenIn.close();
            codegenProps.setProperty(NAMESPACE_PREFIX_PROPERTY , NAMESPACE_PREFIX_VALUE);
            FileOutputStream codegenOut = new FileOutputStream(ExampleProjectInfo.getCodegenPropertiesFile());
            codegenProps.store(codegenOut, "Edited by " + getClass().getName());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error editing codegen properties: " + ex.getMessage());
        }
        // edit the install properties
        try {
            FileInputStream installIn = new FileInputStream(ExampleProjectInfo.getInstallPropertiesFile());
            Properties installProps = new Properties();
            installProps.load(installIn);
            installIn.close();
            installProps.setProperty(SERVER_TYPE, SERVER_TYPE_VALUE);
            installProps.setProperty(EXCLUDE_DATABASE, EXCLUDE_DATABASE_VALUE);
            installProps.setProperty(INSTALL_CONTAINER, INSTALL_CONTAINER_VALUE);
            installProps.setProperty(DB_SERVER, DatabaseProperties.getServer());
            installProps.setProperty(DB_SERVER_PORT, DatabaseProperties.getPort());
            installProps.setProperty(DB_USERNAME, DatabaseProperties.getUsername());
            installProps.setProperty(DB_PASSWORD, DatabaseProperties.getPassword());
            installProps.setProperty(DB_NAME, DatabaseProperties.getSchemaName());
            FileOutputStream installOut = new FileOutputStream(ExampleProjectInfo.getInstallPropertiesFile());
            installProps.store(installOut, "Edited by " + getClass().getName());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error editing install properties: " + ex.getMessage());
        }
    }
}
