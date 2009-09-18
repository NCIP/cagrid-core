package org.cagrid.tools.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class Database {

	private BasicDataSource root;

	private BasicDataSource coreDB = null;

	private String database;

	private boolean dbBuilt = false;

	// private DatabaseConfiguration conf;

	private Log log;

	private String dbHost;
	private int dbPort;
	private String dbUser;
	private String dbPassword;
	private String driver;

	public Database(DatabaseConfiguration conf, String database)
			throws DatabaseException {
		this(conf.getHost(), conf.getPort(), conf.getUsername(), conf
				.getPassword(), database);
	}

	public Database(String host, int port, String user, String password,
			String database) throws DatabaseException {
		log = LogFactory.getLog(this.getClass().getName());
		this.database = database;
		this.dbHost = host;
		this.dbPort = port;
		this.dbUser = user;
		this.dbPassword = password;
		this.driver = "com.mysql.jdbc.Driver";
		String dbURL = "jdbc:mysql://" + host + ":" + port + "/";
		root = new BasicDataSource();
		root.setDriverClassName(driver);
		root.setUsername(user);
		root.setPassword(password);
		root.setUrl(dbURL);
		root.setValidationQuery("select 1");
		root.setTestOnBorrow(true);
		String coreDBURL = "jdbc:mysql://" + this.dbHost + ":" + this.dbPort
				+ "/" + database;
		coreDB = new BasicDataSource();
		coreDB.setDriverClassName(driver);
		coreDB.setUsername(dbUser);
		coreDB.setPassword(dbPassword);
		coreDB.setUrl(coreDBURL);
		coreDB.setValidationQuery("select 1");
		coreDB.setTestOnBorrow(true);

	}

	private void update(BasicDataSource source, String sql)
			throws DatabaseException {
		Connection c = null;
		Statement s = null;
		try {
			c = source.getConnection();
			s = c.createStatement();
			s.executeUpdate(sql);
			s.close();

		} catch (SQLException e) {
			String err = "Unexpected Database Error: " + e.getMessage();
			log.error(err, e);
			throw new DatabaseException(err, e);
		} finally {
			try {
				s.close();
			} catch (Exception e) {
			}
			try {
				c.close();
			} catch (Exception e) {
			}
		}
	}

	public void createDatabaseIfNeeded() throws DatabaseException {

		try {
			if (!dbBuilt) {
				if (!databaseExists(database)) {
					// Query.update(this.root, "create database " + database+ "
					// COLLATE ascii_bin");
					update(this.root, "create database " + database);
				}
				dbBuilt = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DatabaseException(e.getMessage(), e);
		}

	}

	public void destroyDatabase() throws DatabaseException {
		try {
			if (databaseExists(database)) {
				update(this.root, "drop database if exists " + database);
			}
			if (coreDB != null) {
				coreDB.close();
			}
			if (root != null) {
				root.close();
			}
			//coreDB = null;
			dbBuilt = false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DatabaseException(e.getMessage(), e);
		}
	}

	public boolean tableExists(String tableName) throws DatabaseException {
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet results = null;
		try {
			c = this.coreDB.getConnection();
			stmt = c.prepareStatement("SELECT COUNT(*) FROM " + tableName
					+ " WHERE 1 = 2");
			results = stmt.executeQuery();
			return true; // if table does exist, no rows will ever be
			// returned
		} catch (SQLException e) {
			return false; // if table does not exist, an exception will be
			// thrown
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DatabaseException(e.getMessage(), e);
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
				this.coreDB.close();
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
			}

		}
	}

	public void update(String sql) throws DatabaseException {
		update(coreDB, sql);
	}

	public long getLastAutoId(Connection connection) throws DatabaseException {
		long id = -1;
		StringBuffer query = new StringBuffer();
		query.append("select LAST_INSERT_ID()");
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			// Execute the query
			rs = stmt.executeQuery(query.toString());
			// Examine the result set
			if (rs.next()) {
				id = rs.getLong(1);
			}
		} catch (SQLException e) {
			String err = "Unexpected Database Error: " + e.getMessage();
			log.error(e.getMessage(), e);
			throw new DatabaseException(err, e);
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
			}
			try {
				stmt.close();
			} catch (Exception e) {
			}

		}
		return id;
	}

	private long insertGetId(BasicDataSource source, String sql)
			throws DatabaseException {
		long id = -2;
		Connection c = null;
		Statement s = null;
		synchronized (c) {
			try {
				c = source.getConnection();
				s = c.createStatement();
				s.executeUpdate(sql);
				id = getLastAutoId(c);
				s.close();
			} catch (SQLException e) {
				String err = "Unexpected Database Error: " + e.getMessage();
				log.error(err, e);
				throw new DatabaseException(err, e);
			} finally {
				try {
					s.close();
				} catch (Exception e) {
				}
				try {
					c.close();
				} catch (Exception e) {
				}
			}
		}
		return id;
	}

	public boolean exists(String table, String field, String value)
			throws DatabaseException {
		boolean exists = false;
		Connection c = null;
		try {
			c = getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("select count(*) from " + table
					+ " where " + field + "='" + value + "'");
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					exists = true;
				}
			}
			rs.close();
			s.close();

		} catch (Exception e) {
			throw new DatabaseException(
					"An unexpected database error occurred.", e);
		} finally {
			releaseConnection(c);
		}

		return exists;
	}

	public boolean exists(String table, String field, long value)
			throws DatabaseException {
		boolean exists = false;
		Connection c = null;
		try {
			c = getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("select count(*) from " + table
					+ " where " + field + "=" + value);
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					exists = true;
				}
			}
			rs.close();
			s.close();

		} catch (Exception e) {
			throw new DatabaseException(
					"An unexpected database error occurred.", e);
		} finally {
			releaseConnection(c);
		}

		return exists;
	}

	public long insertGetId(String sql) throws DatabaseException {
		return insertGetId(coreDB, sql);
	}

	public void releaseConnection(Connection c) {
		try {
			c.close();
		} catch (Exception e) {
		}
	}

	public Connection getConnection() throws DatabaseException {
		Connection c = null;
		try {
			c = this.coreDB.getConnection();
			return c;
		} catch (Exception e) {
			try {
				c.close();
			} catch (Exception ex) {
			}
			String err = "Unexpected Database Error: " + e.getMessage();
			log.error(err, e);
			throw new DatabaseException(err, e);
		}
	}

	private boolean databaseExists(String db) throws DatabaseException {
		boolean exists = false;
		Connection c = null;
		ResultSet dbs = null;
		if (coreDB == root) {
			return true;
		}
		try {
			c = this.root.getConnection();
			DatabaseMetaData dbMetadata = c.getMetaData();

			dbs = dbMetadata.getCatalogs();
			while (dbs.next()) {
				if (dbs.getString(1).equalsIgnoreCase(db)) {
					exists = true;
				}
			}
			dbs.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			try {
				dbs.close();
			} catch (Exception e) {
			}
			try {
				c.close();
			} catch (Exception e) {
			}
		}
		return exists;
	}

	public int getUsedConnectionCount() {
		return this.coreDB.getNumActive();
	}

	public int getRootUsedConnectionCount() {
		return this.root.getNumActive();
	}

	public String getDatabaseName() {
		return database;
	}

}