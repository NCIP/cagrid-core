package gov.nih.nci.cagrid.gridgrouper.testutils;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import junit.framework.TestCase;

import org.jdom.Document;
import org.projectmobius.db.ConnectionManager;
import org.projectmobius.db.DatabaseException;
import org.projectmobius.db.Query;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: MysqlDBUtil.java,v 1.8 2009-01-09 21:21:27 langella Exp $
 */
public class MysqlDBUtil {

	private static final String DB = "grouper_test";

	public static final String DB_CONFIG = "/db-config.xml";

	private ConnectionManager root;

	private ConnectionManager core = null;

	private boolean dbBuilt = false;


	public MysqlDBUtil(ConnectionManager rootConnectionManager) {
		this.root = rootConnectionManager;
	}


	public void createDatabaseIfNeeded() throws GridGrouperRuntimeFault {

		try {
			if (!dbBuilt) {
				if (!databaseExists(DB)) {
					Query.update(this.root, "create database " + DB);
				}
				if (core == null) {
					core = new ConnectionManager(DB, root.getUrlPrefix(), root.getDriver(), root.getHost(), root
						.getPort(), root.getUsername(), root.getPassword());
				}
				dbBuilt = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("An error occured while trying to create the Dorian database (" + DB + ")");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		}

	}


	public void destroyDatabase() throws GridGrouperRuntimeFault {
		try {
			if (databaseExists(DB)) {
				Query.update(this.root, "drop database if exists " + DB);
			}
			if (core != null) {
				core.destroy();
			}
			if (root != null) {
				root.closeAllUnusedConnections();
			}
			core = null;
			dbBuilt = false;
		} catch (Exception e) {
			e.printStackTrace();
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("An error occured while trying to destroy the Grid Grouper database (" + DB + ")");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		}
	}


	public boolean tableExists(String tableName) throws GridGrouperRuntimeFault {
		boolean exists = false;
		Connection c = null;
		try {
			c = core.getConnection();
			DatabaseMetaData dbMetadata = c.getMetaData();
			String[] names = {"TABLE"};
			names[0] = tableName;
			ResultSet tables = dbMetadata.getTables(null, "%", tableName, names);
			if (tables.next()) {
				exists = true;
			}
			tables.close();
			core.releaseConnection(c);
		} catch (Exception e) {
			core.releaseConnection(c);
			e.printStackTrace();
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Unexpected Database Error");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		}
		return exists;
	}


	public void update(String sql) throws GridGrouperRuntimeFault {
		try {
			Query.update(core, sql);
		} catch (Exception e) {
			e.printStackTrace();
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Unexpected Database Error");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			throw (GridGrouperRuntimeFault) helper.getFault();
		}
	}


	public long insertGetId(String sql) throws GridGrouperRuntimeFault {
		try {
			return Query.insertGetId(core, sql);
		} catch (Exception e) {
			// logError(e.getMessage(), e);
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Unexpected Database Error");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			throw (GridGrouperRuntimeFault) helper.getFault();
		}
	}


	public void releaseConnection(Connection c) {
		this.core.releaseConnection(c);
	}


	public Connection getConnection() throws DatabaseException {
		return this.core.getConnection();
	}


	private boolean databaseExists(String db) throws GridGrouperRuntimeFault {
		boolean exists = false;
		Connection c = null;
		if (core == root) {
			return true;
		}
		try {
			c = this.root.getConnection();
			DatabaseMetaData dbMetadata = c.getMetaData();

			ResultSet dbs = dbMetadata.getCatalogs();
			while (dbs.next()) {
				if (dbs.getString(1).equalsIgnoreCase(db)) {
					exists = true;
				}
			}
			dbs.close();
		} catch (Exception e) {
			this.root.releaseConnection(c);
			e.printStackTrace();
			GridGrouperRuntimeFault fault = new GridGrouperRuntimeFault();
			fault.setFaultString("Unexpected Database Error");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (GridGrouperRuntimeFault) helper.getFault();
			throw fault;
		}
		this.root.releaseConnection(c);
		return exists;
	}


	public int getUsedConnectionCount() {
		return this.core.getUsedConnectionCount();
	}


	public int getRootUsedConnectionCount() {
		return this.root.getUsedConnectionCount();
	}


	public static MysqlDBUtil getDB() throws Exception {
		InputStream resource = TestCase.class.getResourceAsStream(DB_CONFIG);
		Document doc = XMLUtilities.streamToDocument(resource);
		ConnectionManager cm = new ConnectionManager(doc.getRootElement());
		MysqlDBUtil db = new MysqlDBUtil(cm);
		// db.destroyDatabase();
		db.createDatabaseIfNeeded();
		return db;
	}


	public static void main(String[] args) {
		try {
			MysqlDBUtil.getDB();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}