package gov.nih.nci.cagrid.gts.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.projectmobius.db.ConnectionManager;
import org.projectmobius.db.DatabaseException;
import org.projectmobius.db.Query;


public abstract class Database {

	private String databaseName;

	protected Log log;


	public Database(String db) {
		this.databaseName = db;
		log = LogFactory.getLog(this.getClass().getName());
	}


	protected abstract ConnectionManager getConnectionManager() throws DatabaseException;


	public abstract void destroyDatabase() throws DatabaseException;


	public abstract void createDatabase() throws DatabaseException;


	public void update(String sql) throws DatabaseException {
		Query.update(getConnectionManager(), sql);
	}


	public long insertGetId(String sql) throws DatabaseException {
		return Query.insertGetId(getConnectionManager(), sql);
	}


	public void releaseConnection(Connection c) {
		try {
			getConnectionManager().releaseConnection(c);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}


	public Connection getConnection() throws DatabaseException {
		return getConnectionManager().getConnection();
	}


	public String getDatabaseName() {
		return databaseName;
	}


	public int getUsedConnectionCount() throws DatabaseException {
		return getConnectionManager().getUsedConnectionCount();
	}


	public boolean tableExists(final String tableName) throws DatabaseException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet results = null;
		try {
			c = this.getConnectionManager().getConnection();
			stmt = c.prepareStatement("SELECT COUNT(*) FROM " + tableName + " WHERE 1 = 2");
			results = stmt.executeQuery();
			return true; // if table does exist, no rows will ever be
			// returned
		} catch (SQLException e) {
			return false; // if table does not exist, an exception will be
			// thrown
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DatabaseException(e.getMessage());
		} finally {
			try {
				if (results != null) {
					results.close();
				}
				if (stmt != null) {
					stmt.close();
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}
			try {
				this.getConnectionManager().releaseConnection(c);
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}

		}
	}

}
