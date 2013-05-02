/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.gts.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import org.projectmobius.db.ConnectionManager;
import org.projectmobius.db.DatabaseException;
import org.projectmobius.db.Query;


public class MySQLDatabase extends Database {

	private ConnectionManager root;

	private ConnectionManager dbCM = null;

	private boolean dbBuilt = false;


	public MySQLDatabase(ConnectionManager cm, String db) {
		super(db);
		this.root = cm;
	}


	protected ConnectionManager getConnectionManager() throws DatabaseException {
		if (dbCM == null) {
			dbCM = new ConnectionManager(getDatabaseName(), root.getUrlPrefix(), root.getDriver(), root.getHost(), root
				.getPort(), root.getUsername(), root.getPassword());
		}
		return dbCM;
	}


	public void createDatabase() throws DatabaseException {
		try {
			if (!dbBuilt) {
				if (!databaseExists(getDatabaseName())) {
					Query.update(this.root, "create database " + getDatabaseName());
				}
				dbBuilt = true;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DatabaseException("An error occured while trying to create the database (" + getDatabaseName()
				+ ")");
		}
	}


	public void destroyDatabase() throws DatabaseException {
		try {
			if (databaseExists(getDatabaseName())) {
				Query.update(this.root, "drop database if exists " + getDatabaseName());
			}
			if (dbCM != null) {
				dbCM.destroy();
			}
			if (root != null) {
				root.closeAllUnusedConnections();
			}
			dbCM = null;
			dbBuilt = false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DatabaseException("An error occured while trying to destroy the database (" + getDatabaseName()
				+ ")");
		}

	}


	private boolean databaseExists(String db) throws DatabaseException {
		boolean exists = false;
		Connection c = null;
		if (getConnectionManager() == root) {
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
			log.error(e.getMessage(), e);
			throw new DatabaseException(e.getMessage());
		}
		this.root.releaseConnection(c);
		return exists;
	}
}
