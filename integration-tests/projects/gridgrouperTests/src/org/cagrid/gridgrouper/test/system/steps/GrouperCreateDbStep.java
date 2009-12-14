package org.cagrid.gridgrouper.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GrouperCreateDbStep extends Step {
	private String hostname;
	private String port;
	private String user;
	private String password;

	public GrouperCreateDbStep() {
		this("localhost", "3306", "root", "");
	}

	public GrouperCreateDbStep(String hostname, String port, String user, String password) {
		super();

		this.hostname = hostname;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	@Override
	public void runStep() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + hostname + ":" + port + "/";
		Connection con = DriverManager.getConnection(url, user, password);

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
