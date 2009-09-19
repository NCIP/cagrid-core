package gov.nih.nci.cagrid.sdkinstall;

import gov.nih.nci.cagrid.sdkinstall.description.InstallationDescription;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;

/** 
 *  Version321DeployPropertiesManager
 *  Manages the deploy.properties file for SDK version 3.2.1
 * 
 * @author David Ervin
 * 
 * @created Jun 13, 2007 2:25:49 PM
 * @version $Id: Version321DeployPropertiesManager.java,v 1.7 2007-06-20 16:16:47 dervin Exp $ 
 */
public class Version321DeployPropertiesManager extends DeployPropertiesManager {
    // general properties
    public static final String PROPERTY_ENABLE_WRITABLE_API = "disable_writable_api_generation";
    public static final String PROPERTY_PROJECT_NAME = "project_name";
    public static final String PROPERTY_WEB_PROJECT_NAME = "webservice_name";
    public static final String PROPERTY_JAVA_HOME = "java_home";
    
    // jboss related constants
    public static final String PROPERTY_J2SE_CONTAINER_HOME = "j2se_container_home";
    public static final String PROPERTY_WEB_SERVER_PORT = "web_server_port";
    
    // mysql stuff
    public static final String PROPERTY_MYSQL_HOME = "mysql_home";
    public static final String PROPERTY_MYSQL_SERVER_NAME = "db_server_name";
    // db user
    public static final String PROPERTY_MYSQL_USER = "db_user";
    public static final String PROPERTY_MYSQL_PASSWD = "db_password";
    public static final String PROPERTY_MYSQL_CREATE_USER = "create_mysql_user";
    // db schema
    public static final String PROPERTY_MYSQL_SCHEMA = "schema_name";
    public static final String PROPERTY_MYSQL_SCHEMA_FILENAME = "ddl_filename";
    public static final String PROPERTY_MYSQL_CREATE_SCHEMA = "create_schema";
    public static final String PROPERTY_MYSQL_IMPORT_DATA = "import_data";
    public static final String PROPERTY_MYSQL_DATA_FILENAME = "datadump_name";    
    
    private PropertiesPreservingComments deployProperties;

    public Version321DeployPropertiesManager(InstallationDescription description, File sdkDir) {
        super(description, sdkDir);
    }


    public void configureDeployment() throws DeploymentConfigurationException {
        File deployPropertiesFile = new File(getSdkDirectory().getAbsolutePath() 
            + File.separator + "conf" + File.separator + "deploy.properties");
        if (!deployPropertiesFile.exists()) {
            throw new DeploymentConfigurationException(
                "Config file " + deployPropertiesFile.getAbsolutePath() + " does not exist!");
        }
        if (!deployPropertiesFile.canRead()) {
            throw new DeploymentConfigurationException(
                "Config file " + deployPropertiesFile.getAbsolutePath() + " cannot be read!");
        }
        deployProperties = new PropertiesPreservingComments();
        try {
            deployProperties.load(deployPropertiesFile);
        } catch (IOException ex) {
            throw new DeploymentConfigurationException(
                "Error loading deployment properties file: " + ex.getMessage(), ex);
        }
        
        setGeneralParameters();
        setJBossParameters();
        setMysqlParameters();
        
        try {
            FileOutputStream out = new FileOutputStream(deployPropertiesFile);
            deployProperties.write(out);
            out.flush();
            out.close();
        } catch (IOException ex) {
            throw new DeploymentConfigurationException(
                "Error storing edited deployment properties file: " + ex.getMessage(), ex);
        }
    }
    
    
    private void setGeneralParameters() {
        String projectName = getInstallationDescription().getApplicationName();
        if (projectName != null) {
            String projectWebName = projectName + "Service";
            deployProperties.setProperty(PROPERTY_PROJECT_NAME, projectName);
            deployProperties.setProperty(PROPERTY_WEB_PROJECT_NAME, projectWebName);
        }
        
        // bass-ackwards
        Boolean enableWritable = getInstallationDescription().getEnableWritableApi();
        if (enableWritable != null) {
            String disableWritable = getInstallationDescription()
                .getEnableWritableApi().booleanValue() ? "no" : "yes";
            deployProperties.setProperty(PROPERTY_ENABLE_WRITABLE_API, disableWritable);
        }
        
        String javaHome = System.getenv("JAVA_HOME");
        deployProperties.setProperty(PROPERTY_JAVA_HOME, javaHome);
    }
    
    
    private void setJBossParameters() {
        String jbossHome = null;
        if (getInstallationDescription().getJBossDescription().getExistingInstallation() != null) {
            jbossHome = getInstallationDescription().getJBossDescription()
                .getExistingInstallation().getJbossLocation();
        } else {
            File jbossUnpackDir = new File(getInstallationDescription().getJBossDescription()
                .getNewInstallation().getUnpackDirectory());
            File[] dirs = jbossUnpackDir.listFiles(new FileFilter() {
                public boolean accept(File path) {
                    return path.isDirectory();
                }
            });
            jbossHome = dirs[0].getAbsolutePath().replace(File.separatorChar, '/');
        }
        deployProperties.setProperty(PROPERTY_J2SE_CONTAINER_HOME, jbossHome);
        Integer jbossWebPort = getInstallationDescription().getJBossDescription().getJbossPort();
        if (jbossWebPort != null) {
            deployProperties.setProperty(PROPERTY_WEB_SERVER_PORT, jbossWebPort.toString());
        }
    }
    
    
    private void setMysqlParameters() {
        String mysqlHome = getInstallationDescription().getMySQLDescription().getMysqlLocation();
        String mysqlHost = getInstallationDescription().getMySQLDescription().getServerInformation().getHostname();
        deployProperties.setProperty(PROPERTY_MYSQL_HOME, mysqlHome);
        deployProperties.setProperty(PROPERTY_MYSQL_SERVER_NAME, mysqlHost);
        
        String dbUser = getInstallationDescription().getMySQLDescription().getServerInformation().getUsername();
        if (dbUser != null) {
            deployProperties.setProperty(PROPERTY_MYSQL_USER, dbUser);
        }
        String passwd = getInstallationDescription().getMySQLDescription().getServerInformation().getPassword();
        if (passwd != null) {
            deployProperties.setProperty(PROPERTY_MYSQL_PASSWD, passwd);
        }
        Boolean createUser = getInstallationDescription().getMySQLDescription().getServerInformation().getCreateUser();
        if (createUser != null) {
            String createUserValue = createUser.booleanValue() ? "yes" : "no";
            deployProperties.setProperty(PROPERTY_MYSQL_CREATE_USER, createUserValue);
        }
        String schemaName = getInstallationDescription().getMySQLDescription().getDataOptions().getSchemaName();
        deployProperties.setProperty(PROPERTY_MYSQL_SCHEMA, schemaName);
        Boolean createSchema = getInstallationDescription().getMySQLDescription().getDataOptions().getCreateSchema();
        if (createSchema != null) {
            String createSchemaValue = createSchema.booleanValue() ? "yes" : "no";
            deployProperties.setProperty(PROPERTY_MYSQL_CREATE_SCHEMA, createSchemaValue);
        }
        String schemaFileName = getInstallationDescription().getMySQLDescription().getDataOptions().getSchemaFilename();
        if (schemaFileName != null) {
            deployProperties.setProperty(PROPERTY_MYSQL_SCHEMA_FILENAME, schemaFileName);            
        }
        Boolean importData = getInstallationDescription().getMySQLDescription().getDataOptions().getLoadData();
        if (importData != null) {
            String importDataValue = importData.booleanValue() ? "yes" : "no";
            deployProperties.setProperty(PROPERTY_MYSQL_IMPORT_DATA, importDataValue);
        }
        String dataDumpFile = getInstallationDescription().getMySQLDescription().getDataOptions().getDataFilename();
        if (dataDumpFile != null) {
            deployProperties.setProperty(PROPERTY_MYSQL_DATA_FILENAME, dataDumpFile);
        }
    }
}
