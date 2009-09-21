package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.SecurityServiceProvider;
import gov.nih.nci.security.authorization.domainobjects.Application;
import gov.nih.nci.security.authorization.domainobjects.Privilege;
import gov.nih.nci.security.dao.ApplicationSearchCriteria;
import gov.nih.nci.security.dao.PrivilegeSearchCriteria;
import gov.nih.nci.security.system.ApplicationSessionFactory;

import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;

public class CSM {

	public CSM() {
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

			AuthorizationManager am = CSMInitializer.getAuthorizationManager();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
