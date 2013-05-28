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
package org.cagrid.iso21090.tests.integration.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.cagrid.iso21090.tests.integration.DatabaseProperties;
import org.cagrid.iso21090.tests.integration.ExampleProjectInfo;

public class ConfigureExampleProjectStep extends Step {
    // sysproperty for temp dir location
    public static final String APPLICATION_TEMP_DIR_PROP = "sdk.temp.dir";
    
    // codegen properties
    public static final String NAMESPACE_PREFIX_PROPERTY = "NAMESPACE_PREFIX";
    public static final String NAMESPACE_PREFIX_VALUE = "gme://caCORE.caCORE/3.2/";
    public static final String PROJECT_NAME_PROPERTY = "PROJECT_NAME";
    
    // install properties
    public static final String APPLICATION_BASE_PATH_LINUX = "application.base.path.linux";
    public static final String APPLICATION_BASE_PATH_WINDOWS = "application.base.path.windows";
    public static final String SERVER_TYPE = "SERVER_TYPE";
    public static final String SERVER_TYPE_VALUE = "tomcat";
    public static final String EXCLUDE_DATABASE = "exclude.database";
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
    
    public ConfigureExampleProjectStep() {
        super();
    }


    public void runStep() throws Throwable {
        // create a temp directory for the SDK to build with
        String tempName = System.getProperty(APPLICATION_TEMP_DIR_PROP);
        assertNotNull(tempName);
        File tempDir = new File(tempName);
        if (!tempDir.exists()) {
            assertTrue("Unable to create temp dir", tempDir.mkdirs());
        }
        
        // edit the codegen properties
        try {
            FileInputStream codegenIn = new FileInputStream(ExampleProjectInfo.getCodegenPropertiesFile());
            Properties codegenProps = new Properties();
            codegenProps.load(codegenIn);
            codegenIn.close();
            codegenProps.setProperty(NAMESPACE_PREFIX_PROPERTY , NAMESPACE_PREFIX_VALUE);
            codegenProps.setProperty(PROJECT_NAME_PROPERTY, ExampleProjectInfo.EXAMPLE_PROJECT_NAME);
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
            // If you want to SDK to setup the database then "comment" the exclude.database property
            //      -- Satish
            // installProps.commentOutProperty(EXCLUDE_DATABASE);
            installProps.remove(EXCLUDE_DATABASE);
            if (isWindowsOs()) {
                installProps.setProperty(APPLICATION_BASE_PATH_WINDOWS, tempDir.getAbsolutePath());
            } else {
                installProps.setProperty(APPLICATION_BASE_PATH_LINUX, tempDir.getAbsolutePath());
            }
            installProps.setProperty(SERVER_TYPE, SERVER_TYPE_VALUE);
            installProps.setProperty(INSTALL_CONTAINER, INSTALL_CONTAINER_VALUE);
            installProps.setProperty(DB_TYPE, DB_TYPE_VALUE);
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
    
    
    private boolean isWindowsOs() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }
}
