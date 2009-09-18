package gov.nih.nci.cagrid.gts.common;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import org.projectmobius.db.ConnectionManager;
import org.projectmobius.db.DatabaseException;


public class DerbyDatabase extends Database {

	private String derbyEncryptionPassword = "";

	private String dbLocation;

	private ConnectionManager cm;

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private boolean dbBuilt = false;


	public DerbyDatabase(String db, String derbyEncryptionPassword) {
		super(db);
		this.derbyEncryptionPassword = derbyEncryptionPassword;
		File dbHome = new File(Constants.getGTSUserHome().getAbsolutePath() + File.separator + "gtsdbs");
		dbHome.mkdirs();
		this.dbLocation = dbHome.getAbsolutePath() + File.separator + db;
	}


	protected ConnectionManager getConnectionManager() throws DatabaseException {
		if (cm == null) {
			String url = "jdbc:derby:" + dbLocation + ";create=true;dataEncryption=true;bootPassword="
				+ derbyEncryptionPassword;
			cm = new ConnectionManager("GTS", url, driver, null, null);
		}
		return cm;
	}


	public void createDatabase() throws DatabaseException {
		if (!dbBuilt) {
			try {
				Class.forName(driver).newInstance();
			} catch (Exception e) {

			}
			Connection c = getConnectionManager().getConnection();
			getConnectionManager().releaseConnection(c);
			dbBuilt = true;
		}

	}


	public void destroyDatabase() throws DatabaseException {
		getConnectionManager().closeAllUnusedConnections();
		File f = new File(dbLocation);
		if (f.exists()) {

			boolean gotSQLExc = false;

			try {
				Class.forName(driver).newInstance();
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			} catch (Exception se) {

			}

			if (!deleteDir(f)) {
				throw new DatabaseException("Error removing that database at " + dbLocation);
			}
		}
		dbBuilt = false;
	}


	public boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		// The directory is now empty so delete it
		return dir.delete();
	}


	public boolean tableExists(String tableName) throws DatabaseException {
		return super.tableExists(tableName.toUpperCase());
	}
}
