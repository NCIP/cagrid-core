package org.cagrid.tests.data.styles.cacore42.steps;

import java.io.IOException;

import org.cagrid.tests.data.styles.cacore42.DatabaseProperties;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.database.DatabaseException;

import gov.nih.nci.cagrid.testing.system.haste.Step;

public class SdkDatabaseStep extends Step {
    
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
            ex.printStackTrace();
            fail("Error determining database configuration: " + ex.getMessage());
        }
        // set up the DB instance
        Database db = null;
        try {
            db = new Database(host, port, user, passwd, database);
        } catch (DatabaseException ex) {
            ex.printStackTrace();
            fail("Error instantiating database: " + ex.getMessage());
        }
        return db;
    }
    
    
    public void runStep() {
        try {
            switch (operation) {
                case CREATE:
                    getDatabase().createDatabaseIfNeeded();
                    break;
                case DESTROY:
                    getDatabase().destroyDatabase();
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error performing database operation " + operation.name() + ": " + ex.getMessage());
        }
    }
    
    
    public static enum DatabaseOperation {
        CREATE, DESTROY
    }
}
