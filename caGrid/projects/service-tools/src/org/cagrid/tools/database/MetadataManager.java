package org.cagrid.tools.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class MetadataManager {
	private Database db;

	private boolean dbBuilt = false;

	private String table;
	private Log log;

	public MetadataManager(Database db, String table) {
		this.db = db;
		this.table = table;
		this.log = LogFactory.getLog(this.getClass().getName());
	}

	public boolean exists(String name) throws DatabaseException {
		this.buildDatabase();
		Connection c = null;
		boolean exists = false;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select count(*) from "
					+ table + " where name= ?");
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					exists = true;
				}
			}
			rs.close();
			s.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			DatabaseException de = new DatabaseException(e.getMessage(), e);
			throw de;
		} finally {
			db.releaseConnection(c);
		}
		return exists;
	}

	public synchronized void insert(Metadata metadata) throws DatabaseException {
		this.buildDatabase();
		Connection c = null;
		try {
			if (!exists(metadata.getName())) {
				c = db.getConnection();
				PreparedStatement s = c.prepareStatement("INSERT INTO " + table
						+ " SET NAME= ?, DESCRIPTION= ?, VALUE= ?");
				s.setString(1, metadata.getName());
				s.setString(2, metadata.getDescription());
				s.setString(3, metadata.getValue());
				s.execute();
			} else {
				DatabaseException fault = new DatabaseException(
						"Could not insert the metadata " + metadata.getName()
								+ " because it already exists.");
				throw fault;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			DatabaseException fault = new DatabaseException(
					"Unexpected Database Error, could insert  metadata!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}

	public synchronized void update(Metadata metadata) throws DatabaseException {
		this.buildDatabase();
		Connection c = null;
		try {
			if (exists(metadata.getName())) {
				c = db.getConnection();
				PreparedStatement s = c.prepareStatement("UPDATE " + table
						+ " SET DESCRIPTION= ?, VALUE= ? WHERE NAME= ?");

				s.setString(1, metadata.getDescription());
				s.setString(2, metadata.getValue());
				s.setString(3, metadata.getName());
				s.execute();
			} else {
				insert(metadata);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			DatabaseException fault = new DatabaseException(
					"Unexpected Database Error, could update metadata!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}

	public synchronized void remove(String name) throws DatabaseException {
		this.buildDatabase();
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("DELETE FROM " + table
					+ " WHERE NAME= ?");
			s.setString(1, name);
			s.execute();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			DatabaseException fault = new DatabaseException(
					"Unexpected Database Error, could remove metadata!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}

	public Metadata get(String name) throws DatabaseException {
		this.buildDatabase();
		Connection c = null;

		String value = null;
		String description = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c
					.prepareStatement("select DESCRIPTION,VALUE from " + table
							+ " where name= ?");
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				value = rs.getString("VALUE");
				description = rs.getString("DESCRIPTION");
			}
			rs.close();
			s.close();

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			DatabaseException fault = new DatabaseException(
					"Unexpected Database Error, obtain the metadata " + name
							+ ".");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}

		if (value == null) {
			return null;
		} else {
			Metadata metadata = new Metadata();
			metadata.setName(name);
			metadata.setValue(value);
			metadata.setDescription(description);
			return metadata;
		}
	}

	public void clearDatabase() throws DatabaseException {
		this.buildDatabase();
		try {
			db.update("DELETE FROM " + table);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			DatabaseException fault = new DatabaseException(
					"Unexpected Database Error.");
			throw fault;
		}
	}

	private void buildDatabase() throws DatabaseException {
		try {
			if (!dbBuilt) {
				if (!this.db.tableExists(table)) {
					String applications = "CREATE TABLE " + table + " ("
							+ "NAME VARCHAR(255) NOT NULL PRIMARY KEY,"
							+ "DESCRIPTION TEXT," + "VALUE TEXT NOT NULL,"
							+ "INDEX document_index (NAME));";
					db.update(applications);
				}
				this.dbBuilt = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			DatabaseException fault = new DatabaseException(
					"Unexpected Database Error.");
			throw fault;
		}
	}
}