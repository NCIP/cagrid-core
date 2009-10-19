package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.tests.data.styles.cacore42.DatabaseProperties;
import org.cagrid.tests.data.styles.cacore42.ExampleProjectInfo;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.database.DatabaseException;

public class SdkDatabaseStep extends Step {
    
    private static Log LOG = LogFactory.getLog(SdkDatabaseStep.class);
    
    private DatabaseOperation operation = null;

    public SdkDatabaseStep(DatabaseOperation operation) {
        super();
        this.operation = operation;
    }
    
    
    protected Database getDatabase() {
        // get DB configuration parameters
        String host = null;
        int port = 0;
        String user = null;
        String passwd = null;
        String database = null;
        try {
            host = DatabaseProperties.getServer();
            port = Integer.parseInt(DatabaseProperties.getPort());
            user = DatabaseProperties.getUsername();
            passwd = DatabaseProperties.getPassword();
            database = DatabaseProperties.getSchemaName();
        } catch (IOException ex) {
            String message = "Error determining database configuration: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
        // set up the DB instance
        Database db = null;
        try {
            db = new Database(host, port, user, passwd, database);
        } catch (DatabaseException ex) {
            String message = "Error instantiating database: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
        return db;
    }
    
    
    public void runStep() {
        try {
            switch (operation) {
                case CREATE:
                    LOG.debug("CREATING caCORE SDK DATABASE");
                    getDatabase().createDatabaseIfNeeded();
                    break;
                case INSTALL:
                    LOG.debug("INSTALLING caCORE SDK DATABASE");
                    List<String> creationStatements = getExampleDatabaseCreationStatements();
                    for (String statement : creationStatements) {
                        LOG.debug("-- EXECUTING DB CREATION STATEMENT --");
                        LOG.debug(statement);
                        getDatabase().update(statement);
                    }
                    break;
                case DESTROY:
                    LOG.debug("DESTROYING caCORE SDK DATABASE");
                    getDatabase().destroyDatabase();
                    break;
            }
        } catch (Exception ex) {
            String message = "Error performing database operation " + operation.name() + ": " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
    }
    
    
    private List<String> getExampleDatabaseCreationStatements() {
        File dbScript = ExampleProjectInfo.getMysqlDatabaseInstallFile();
        List<String> statements = new LinkedList<String>();
        try {
            FileReader fileReader = new FileReader(dbScript);
            BufferedReader scriptReader = new BufferedReader(fileReader);
            String line = null;
            StringBuffer currentStatement = new StringBuffer();
            while ((line = scriptReader.readLine()) != null) {
                if (!line.startsWith("/*") && !line.startsWith("-") && line.trim().length() != 0) {
                    currentStatement.append(line);
                }
                if (currentStatement.length() != 0 && line.endsWith(";")) {
                    statements.add(currentStatement.toString());
                    currentStatement = new StringBuffer();
                }
            }
        } catch (IOException ex) {
            String message = "Error reading database creation script: " + ex.getMessage();
            LOG.error(message, ex);
            fail(message);
        }
        return statements;
    }
    
    
    public static enum DatabaseOperation {
        CREATE, INSTALL, DESTROY
    }
}
