package org.cagrid.tests.data.styles.cacore42.steps;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.tests.data.styles.cacore42.DatabaseProperties;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.database.DatabaseException;

import gov.nih.nci.cagrid.testing.system.haste.Step;

public abstract class AbstractDatabaseStep extends Step {
    
    private static Log LOG = LogFactory.getLog(AbstractDatabaseStep.class);
    
    private Database db = null;

    protected Database getDatabase() {
        if (db == null) {
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
            try {
                db = new Database(host, port, user, passwd, database);
            } catch (DatabaseException ex) {
                String message = "Error instantiating database: " + ex.getMessage();
                LOG.error(message, ex);
                fail(message);
            }
        }
        return db;
    }
}
