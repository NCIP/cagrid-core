package gov.nih.nci.cagrid.gts.test;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.gts.common.MySQLDatabase;
import gov.nih.nci.cagrid.gts.service.Configuration;
import gov.nih.nci.cagrid.gts.service.SimpleResourceManager;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.mysql.MySQLManager;

import java.io.InputStream;

import junit.framework.TestCase;

import org.jdom.Document;
import org.projectmobius.db.ConnectionManager;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: Utils.java,v 1.13 2009-01-08 20:23:10 langella Exp $
 */
public class Utils {

	private static final String DB = "test_gts";

	private static DBManager dbManager = null;


	public static DBManager getDBManager() throws Exception {
		return getMySQLManager();
	}


	public static DBManager getMySQLManager() throws Exception {
		if (dbManager == null) {
			InputStream resource = TestCase.class.getResourceAsStream(GTSConstants.DB_CONFIG);
			Document doc = XMLUtilities.streamToDocument(resource);
			ConnectionManager cm = new ConnectionManager(doc.getRootElement());
			MySQLDatabase db = new MySQLDatabase(cm, DB);
			// db.destroyDatabase();
			// db.createDatabase();
			dbManager = new MySQLManager(db);
		}
		return dbManager;
	}


	public static Configuration getGTSConfiguration() throws Exception {
		InputStream in = TestCase.class.getResourceAsStream(GTSConstants.GTS_CONFIG);
		SimpleResourceManager srm = new SimpleResourceManager(in);
		return (Configuration) srm.getResource(Configuration.RESOURCE);
	}
}
