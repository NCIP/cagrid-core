package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.common.PropertiesPreservingComments;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.cagrid.tests.data.styles.cacore42.DatabaseProperties;
import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;

public class ConfigureExampleProjectStep extends Step {
    // codegen properties
    public static final String NAMESPACE_PREFIX_PROPERTY = "NAMESPACE_PREFIX";
    public static final String NAMESPACE_PREFIX_VALUE = "gme://caCORE.caCORE/4.2";
    
    // csm codegen properties
    public static final String ENABLE_SECURITY = "ENABLE_SECURITY";
    public static final String ENABLE_SECURITY_VALUE = "true";
    public static final String ENABLE_INSTANCE_LEVEL_SECURITY = "ENABLE_INSTANCE_LEVEL_SECURITY";
    public static final String ENABLE_INSTANCE_LEVEL_SECURITY_VALUE = "true";
    
    // install properties
    public static final String APPLICATION_BASE_PATH_LINUX = "application.base.path.linux";
    public static final String APPLICATION_BASE_PATH_WINDOWS = "application.base.path.windows";
    public static final String SERVER_TYPE = "SERVER_TYPE";
    public static final String SERVER_TYPE_VALUE = "tomcat";
    public static final String EXCLUDE_DATABASE = "exclude.database";
    
    // csm install properties
    public static final String CSM_DB_INSTALL_LIST = "db.install.create.mysql.file.list";
    public static final String CSM_DB_INSTALL_LIST_VALUE = "SDKTestSchema-mysql.sql,SDKTestCSMSchema-mysql-template.sql";
    
    // public static final String EXCLUDE_DATABASE_VALUE = "false";
    public static final String INSTALL_CONTAINER = "INSTALL_CONTAINER";
    public static final String INSTALL_CONTAINER_VALUE = "false";
    public static final String DB_TYPE = "DB_TYPE";
    public static final String DB_TYPE_VALUE = "mysql";
    public static final String DB_SERVER = "DB_SERVER";
    public static final String DB_SERVER_PORT = "DB_SERVER_PORT";
    public static final String DB_NAME = "DB_NAME";
    public static final String DB_USERNAME = "DB_USERNAME";
    public static final String DB_PASSWORD = "DB_PASSWORD";
    
    private File tempApplicationDir = null;
    private boolean enableCsm = false;
    
    public ConfigureExampleProjectStep(File tempApplicationDir, boolean enableCsm) {
        super();
        this.tempApplicationDir = tempApplicationDir;
        this.enableCsm = enableCsm;
    }


    public void runStep() throws Throwable {
        // edit the codegen properties
        try {
            FileInputStream codegenIn = new FileInputStream(ExampleProjectInfo.getCodegenPropertiesFile());
            PropertiesPreservingComments codegenProps = new PropertiesPreservingComments();
            codegenProps.load(codegenIn);
            codegenIn.close();
            codegenProps.setProperty(NAMESPACE_PREFIX_PROPERTY , NAMESPACE_PREFIX_VALUE);
            if (enableCsm) {
                codegenProps.setProperty(ENABLE_SECURITY, ENABLE_SECURITY_VALUE);
                codegenProps.setProperty(ENABLE_INSTANCE_LEVEL_SECURITY, ENABLE_INSTANCE_LEVEL_SECURITY_VALUE);
            }
            FileOutputStream codegenOut = new FileOutputStream(ExampleProjectInfo.getCodegenPropertiesFile());
            codegenProps.store(codegenOut);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error editing codegen properties: " + ex.getMessage());
        }
        // edit the install properties
        try {
            FileInputStream installIn = new FileInputStream(ExampleProjectInfo.getInstallPropertiesFile());
            PropertiesPreservingComments installProps = new PropertiesPreservingComments();
            installProps.load(installIn);
            installIn.close();
            // If you want to SDK to setup the database then "comment" the exclude.database property
            //      -- Satish
            installProps.commentOutProperty(EXCLUDE_DATABASE);
            if (isWindowsOs()) {
                installProps.setProperty(APPLICATION_BASE_PATH_WINDOWS, tempApplicationDir.getAbsolutePath());
            } else {
                installProps.setProperty(APPLICATION_BASE_PATH_LINUX, tempApplicationDir.getAbsolutePath());
            }
            installProps.setProperty(SERVER_TYPE, SERVER_TYPE_VALUE);
            installProps.setProperty(INSTALL_CONTAINER, INSTALL_CONTAINER_VALUE);
            installProps.setProperty(DB_TYPE, DB_TYPE_VALUE);
            installProps.setProperty(DB_SERVER, DatabaseProperties.getServer());
            installProps.setProperty(DB_SERVER_PORT, DatabaseProperties.getPort());
            installProps.setProperty(DB_USERNAME, DatabaseProperties.getUsername());
            installProps.setProperty(DB_PASSWORD, DatabaseProperties.getPassword());
            installProps.setProperty(DB_NAME, DatabaseProperties.getSchemaName());
            if (enableCsm) {
                installProps.setProperty(CSM_DB_INSTALL_LIST, CSM_DB_INSTALL_LIST_VALUE);
            }
            FileOutputStream installOut = new FileOutputStream(ExampleProjectInfo.getInstallPropertiesFile());
            installProps.store(installOut, "Edited by " + getClass().getName());
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error editing install properties: " + ex.getMessage());
        }
    }
    
    
    private boolean isWindowsOs() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }
}
