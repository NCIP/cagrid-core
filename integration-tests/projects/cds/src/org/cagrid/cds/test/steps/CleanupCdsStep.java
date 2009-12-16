package org.cagrid.cds.test.steps;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.cagrid.tools.database.Database;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class CleanupCdsStep extends Step {

	private ServiceContainer container;
	
	private static final String DB_HOST = "gaards.cds.db.host";
	private static final String DB_PORT = "gaards.cds.db.port";
	private static final String DB_USER = "gaards.cds.db.user";
	private static final String DB_PASS = "gaards.cds.db.password";
	private static final String DB_NAME = "gaards.cds.name";

	public CleanupCdsStep(ServiceContainer container) {
		this.container = container;
	}

	public void runStep() throws Throwable {
		String propsFile = this.container.getProperties()
				.getContainerDirectory().getAbsolutePath()
				+ File.separator
				+ "webapps"
				+ File.separator
				+ "wsrf"
				+ File.separator
				+ "WEB-INF"
				+ File.separator
				+ "etc"
				+ File.separator
				+ "cagrid_CredentialDelegationService"
				+ File.separator
				+ "cds.properties";
		
		FileInputStream propsFIS = new FileInputStream(propsFile);
		
		Properties cdsProps = new Properties();
		cdsProps.load(propsFIS);
		
		String host = cdsProps.getProperty(DB_HOST);
		int port = Integer.parseInt(cdsProps.getProperty(DB_PORT));
		String user = cdsProps.getProperty(DB_USER);
		String password = cdsProps.getProperty(DB_PASS);
		String database = cdsProps.getProperty(DB_NAME);
		
		//String host, int port, String user, String password,String database
		Database db = new Database (host, port, user, password, database);
		db.destroyDatabase();
	}
}
