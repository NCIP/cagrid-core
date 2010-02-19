package org.cagrid.identifiers.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.cagrid.identifiers.test.system.IdentifiersTestInfo;


public class CreateDatabasesStep extends Step {
    
	private IdentifiersTestInfo testInfo;

    public CreateDatabasesStep(IdentifiersTestInfo info) {
        this.testInfo = info;
    }

    @Override
    public void runStep() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
        Class.forName("com.mysql.jdbc.Driver");
        File dbPropertiesFile = new File(IdentifiersTestInfo.GRIDSVC_NA_PROPERTIES);

        assertNotNull(dbPropertiesFile);
        assertTrue("Couldn't read the  properties file (" + dbPropertiesFile.getCanonicalPath() + ")", dbPropertiesFile
            .canRead());

        Properties props = new Properties();
        FileInputStream inStream = new FileInputStream(dbPropertiesFile);
        props.load(inStream);

        String dbname = props.getProperty("cagrid.na.db.name");
        assertNotNull("Couldn't find the jdbc database name in the properties file", dbname);
        String url = props.getProperty("cagrid.na.db.url");
        assertNotNull("Couldn't find the jdbc connection url in the properties file", url);
        int dbnameindex = url.lastIndexOf("/");
        assertTrue("Could parse out root database connection", dbnameindex > 0);
        url = url.substring(0, dbnameindex + 1);

        String user = props.getProperty("cagrid.na.db.username");
        assertNotNull("Couldn't find the jdbc connection username in the properties file", user);
        String password = props.getProperty("cagrid.na.db.password");
        assertNotNull("Couldn't find the jdbc connection password in the properties file", password);

        Connection con = DriverManager.getConnection(url, user, password);

        try {
            Statement stmt = con.createStatement();
            
            // grid service and web app target same db (dbname)
            stmt.executeUpdate("drop database if exists " + dbname);
            stmt.executeUpdate("create database " + dbname);
            
            // a separate one (purls) is dedicated to PURLZ
            stmt.executeUpdate("drop database if exists " + IdentifiersTestInfo.PURLZ_DB);
            stmt.executeUpdate("create database " + IdentifiersTestInfo.PURLZ_DB);
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }
}
