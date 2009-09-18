/*
 * Created on Sep 21, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class GrouperCreateDbStep extends Step {
    private String dbUrl;
    private String user;
    private String password;


    public GrouperCreateDbStep() {
        this("jdbc:mysql://localhost/mysql", "root", "");
    }


    public GrouperCreateDbStep(String dbUrl, String user, String password) {
        super();

        this.dbUrl = dbUrl;
        this.user = user;
        this.password = password;
    }


    @Override
    public void runStep() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(dbUrl, user, password);

        try {
            Statement stmt = con.createStatement();

            try {
                stmt.executeUpdate("create database if not exists grouper");
            } catch (SQLException e) {
                System.out.println("SQLException=" + e.getMessage().toLowerCase());
                if (e.getMessage().toLowerCase().indexOf("exists") == -1) {
                    throw e;
                }
                stmt.executeUpdate("drop database grouper");
                stmt.executeUpdate("create database grouper");
            }
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }
}
