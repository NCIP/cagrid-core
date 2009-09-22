package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;

import java.util.HashMap;

public class CSM {
	
	private CSMProperties conf;

	public CSM() {
		//this.conf = conf;
		HashMap<String, String> connectionProperties = new HashMap<String, String>();
		connectionProperties.put("hibernate.connection.url",
				"jdbc:mysql://localhost:3306/csmupt");
		connectionProperties.put("hibernate.connection.username", "root");
		connectionProperties.put("hibernate.connection.password", "");
		connectionProperties.put("hibernate.dialect",
				"org.hibernate.dialect.MySQLDialect");
		connectionProperties.put("hibernate.connection.driver_class",
				"org.gjt.mm.mysql.Driver");
		// SessionFactory sf =
		// ApplicationSessionFactory.getSessionFactory(GRID_CSM_CONTEXT,
		// connectionProperties);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			CSM csm = new CSM();
			AuthorizationManager am = CSMInitializer.getAuthorizationManager();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
