package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.common.Database;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.TrustLevelTable;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustLevelFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustLevelFault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedAuthorityManager.java,v 1.1 2006/03/08 19:48:46 langella
 *          Exp $
 */
public class TrustLevelManager {

	private Log log;

	private boolean dbBuilt = false;

	private Database db;

	private TrustedAuthorityLevelRemover status;

	private String gtsURI;

	private DBManager dbManager;


	public TrustLevelManager(String gtsURI, TrustedAuthorityLevelRemover status, DBManager dbManager) {
		log = LogFactory.getLog(this.getClass().getName());
		this.dbManager = dbManager;
		this.db = dbManager.getDatabase();
		this.status = status;
		this.gtsURI = gtsURI;
	}


	public synchronized void addTrustLevel(TrustLevel level) throws GTSInternalFault, IllegalTrustLevelFault {
		addTrustLevel(level, true);
	}


	public synchronized void addTrustLevel(TrustLevel level, boolean internal) throws GTSInternalFault,
		IllegalTrustLevelFault {
		this.buildDatabase();
		if (Utils.clean(level.getName()) == null) {
			IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
			fault.setFaultString("Cannot add trust level, no name specified.");
			throw fault;
		}

		if (doesTrustLevelExist(level.getName())) {
			IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
			fault.setFaultString("The Trust Level " + level.getName() + " cannot be added, it already exists.");
			throw fault;
		}

		if (level.getDescription() == null) {
			level.setDescription("");
		}

		if (internal) {
			level.setIsAuthority(Boolean.TRUE);
			level.setAuthorityGTS(gtsURI);
			level.setSourceGTS(gtsURI);

		} else {
			if ((level.getIsAuthority() == null)) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The trust level " + level.getName()
					+ ", cannot be added because it does not specify whether or not this GTS is the authority of it.");
				throw fault;
			}

			if (level.getAuthorityGTS() == null) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The trust level " + level.getName()
					+ ", cannot be added because it does not specify an authority trust service.");
				throw fault;

			}

			if ((level.getIsAuthority().booleanValue()) && (!level.getAuthorityGTS().equals(gtsURI))) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The trust level " + level.getName()
					+ " cannot be added, a conflict was detected, this gts (" + gtsURI
					+ ") was specified as its authority, however the URI of another GTS ( " + level.getAuthorityGTS()
					+ ") was specified.");
				throw fault;
			}

			if (level.getSourceGTS() == null) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The trust level " + level.getName()
					+ ", cannot be added because it does not specify a source trust service.");
				throw fault;
			}
		}
		Calendar cal = new GregorianCalendar();
		level.setLastUpdated(cal.getTimeInMillis());
		StringBuffer insert = new StringBuffer();
		insert.append("INSERT INTO " + TrustLevelTable.TABLE_NAME + " SET " + TrustLevelTable.NAME + "= ?,"
			+ TrustLevelTable.DESCRIPTION + "= ?," + TrustLevelTable.IS_AUTHORITY + "= ?,"
			+ TrustLevelTable.AUTHORITY_GTS + "= ?," + TrustLevelTable.SOURCE_GTS + "= ?,"
			+ TrustLevelTable.LAST_UPDATED + "= ?");
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement(insert.toString());
			s.setString(1, level.getName());
			s.setString(2, level.getDescription());
			s.setString(3, String.valueOf(level.getIsAuthority()));
			s.setString(4, level.getAuthorityGTS());
			s.setString(5, level.getSourceGTS());
			s.setLong(6, level.getLastUpdated());
			s.execute();
			s.close();
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in adding the Trust Level, " + level.getName()
				+ ", the following statement generated the error: \n" + insert.toString() + "\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error adding the Trust Level, " + level.getName() + "!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	public synchronized TrustLevel[] getTrustLevels() throws GTSInternalFault {
		this.buildDatabase();
		Connection c = null;
		List levels = new ArrayList();
		StringBuffer sql = new StringBuffer();
		try {
			c = db.getConnection();
			Statement s = c.createStatement();

			sql.append("select * from " + TrustLevelTable.TABLE_NAME);

			ResultSet rs = s.executeQuery(sql.toString());
			while (rs.next()) {
				TrustLevel level = new TrustLevel();
				level.setName(rs.getString(TrustLevelTable.NAME));
				level.setDescription(rs.getString(TrustLevelTable.DESCRIPTION));
				level.setIsAuthority(new Boolean(rs.getBoolean(TrustLevelTable.IS_AUTHORITY)));
				level.setAuthorityGTS(rs.getString(TrustLevelTable.AUTHORITY_GTS));
				level.setSourceGTS(rs.getString(TrustLevelTable.SOURCE_GTS));
				level.setLastUpdated(rs.getLong(TrustLevelTable.LAST_UPDATED));
				levels.add(level);
			}
			rs.close();
			s.close();

			TrustLevel[] list = new TrustLevel[levels.size()];
			for (int i = 0; i < list.length; i++) {
				list[i] = (TrustLevel) levels.get(i);
			}
			return list;

		} catch (Exception e) {
			this.log.error(
				"Unexpected database error incurred in getting trust levels, the following statement generated the error: \n"
					+ sql.toString() + "\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error occurred in getting trust levels.");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	public synchronized TrustLevel[] getTrustLevels(String gtsSourceURI) throws GTSInternalFault {
		this.buildDatabase();
		Connection c = null;
		List levels = new ArrayList();
		StringBuffer sql = new StringBuffer();
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select * from " + TrustLevelTable.TABLE_NAME + " WHERE "
				+ TrustLevelTable.SOURCE_GTS + "= ?");
			s.setString(1, gtsSourceURI);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				TrustLevel level = new TrustLevel();
				level.setName(rs.getString(TrustLevelTable.NAME));
				level.setDescription(rs.getString(TrustLevelTable.DESCRIPTION));
				level.setIsAuthority(new Boolean(rs.getBoolean(TrustLevelTable.IS_AUTHORITY)));
				level.setAuthorityGTS(rs.getString(TrustLevelTable.AUTHORITY_GTS));
				level.setSourceGTS(rs.getString(TrustLevelTable.SOURCE_GTS));
				level.setLastUpdated(rs.getLong(TrustLevelTable.LAST_UPDATED));
				levels.add(level);
			}
			rs.close();
			s.close();

			TrustLevel[] list = new TrustLevel[levels.size()];
			for (int i = 0; i < list.length; i++) {
				list[i] = (TrustLevel) levels.get(i);
			}
			return list;

		} catch (Exception e) {
			this.log.error(
				"Unexpected database error incurred in getting trust levels, the following statement generated the error: \n"
					+ sql.toString() + "\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error occurred in getting trust levels.");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	public synchronized TrustLevel getTrustLevel(String name) throws GTSInternalFault, InvalidTrustLevelFault {
		String sql = "select * from " + TrustLevelTable.TABLE_NAME + " where " + TrustLevelTable.NAME + "= ? ";
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement(sql);
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				TrustLevel level = new TrustLevel();
				level.setName(rs.getString(TrustLevelTable.NAME));
				level.setDescription(rs.getString(TrustLevelTable.DESCRIPTION));
				level.setIsAuthority(new Boolean(rs.getBoolean(TrustLevelTable.IS_AUTHORITY)));
				level.setAuthorityGTS(rs.getString(TrustLevelTable.AUTHORITY_GTS));
				level.setSourceGTS(rs.getString(TrustLevelTable.SOURCE_GTS));
				level.setLastUpdated(rs.getLong(TrustLevelTable.LAST_UPDATED));
				return level;
			}
			rs.close();
			s.close();
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in obtaining the trust level, " + name
				+ ", the following statement generated the error: \n" + sql + "\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error obtaining the trust level " + name);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		InvalidTrustLevelFault fault = new InvalidTrustLevelFault();
		fault.setFaultString("The trust level " + name + " does not exist.");
		throw fault;
	}


	public synchronized void updateTrustLevel(TrustLevel level) throws GTSInternalFault, InvalidTrustLevelFault,
		IllegalTrustLevelFault {
		updateTrustLevel(level, true);
	}


	public synchronized void updateTrustLevel(TrustLevel level, boolean internal) throws GTSInternalFault,
		InvalidTrustLevelFault, IllegalTrustLevelFault {
		TrustLevel curr = this.getTrustLevel(level.getName());
		boolean needsUpdate = false;
		UpdateStatement update = new UpdateStatement(TrustLevelTable.TABLE_NAME);
		if (internal) {
			if (!curr.getAuthorityGTS().equals(gtsURI)) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The trust level cannot be updated, the GTS (" + gtsURI
					+ ") is not its authority!!!");
				throw fault;
			}

			if ((Utils.clean(level.getAuthorityGTS()) != null)
				&& (!level.getAuthorityGTS().equals(curr.getAuthorityGTS()))) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The authority trust service for a trust level cannot be changed");
				throw fault;
			}

			if ((Utils.clean(level.getSourceGTS()) != null) && (!level.getSourceGTS().equals(curr.getSourceGTS()))) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The source trust service for a trust level cannot be changed");
				throw fault;
			}

		} else {

			if ((curr.getIsAuthority().booleanValue()) && (!level.getAuthorityGTS().equals(gtsURI))) {
				IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
				fault.setFaultString("The trust level " + level.getName()
					+ " cannot be updated, a conflict was detected, this gts (" + gtsURI
					+ ") was specified as its authority, however the URI of another GTS ( " + level.getAuthorityGTS()
					+ ") was specified.");
				throw fault;
			}

			if (!curr.getAuthorityGTS().equals(level.getAuthorityGTS())) {
				update.addField(TrustLevelTable.AUTHORITY_GTS, level.getAuthorityGTS());
				needsUpdate = true;
			}

			if (!curr.getSourceGTS().equals(level.getSourceGTS())) {
				update.addField(TrustLevelTable.SOURCE_GTS, level.getSourceGTS());
				needsUpdate = true;
			}
		}

		if ((level.getIsAuthority() != null) && (!level.getIsAuthority().equals(curr.getIsAuthority()))) {
			IllegalTrustLevelFault fault = new IllegalTrustLevelFault();
			fault.setFaultString("Whether or not this GTS is the authority for a level cannot be changed!!!");
			throw fault;
		}

		if (level.getDescription() != null) {
			if ((Utils.clean(level.getDescription()) != null)
				&& (!level.getDescription().equals(curr.getDescription()))) {
				update.addField(TrustLevelTable.DESCRIPTION, level.getDescription());
				needsUpdate = true;
			}
		}
		Connection c = null;
		try {
			if (!level.equals(curr)) {
				if (needsUpdate) {
					c = db.getConnection();
					Calendar cal = new GregorianCalendar();
					level.setLastUpdated(cal.getTimeInMillis());
					update.addField(TrustLevelTable.LAST_UPDATED, Long.valueOf(level.getLastUpdated()));
					update.addWhereField(TrustLevelTable.NAME, "=", level.getName());
					PreparedStatement s = update.prepareUpdateStatement(c);
					s.execute();
					s.close();
				}
			}
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in updating the trust level, " + level.getName()
				+ ", the following statement generated the error: \n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error occurred in updating the trust level, " + level.getName() + ".");
			throw fault;
		} finally {
			if (c != null) {
				db.releaseConnection(c);
			}
		}
	}


	public synchronized void removeTrustLevel(String name) throws GTSInternalFault, InvalidTrustLevelFault,
		IllegalTrustLevelFault {
		if (doesTrustLevelExist(name)) {
			this.status.removeAssociatedTrustedAuthorities(name);
			String sql = "delete FROM " + TrustLevelTable.TABLE_NAME + " where " + TrustLevelTable.NAME + "= ?";
			Connection c = null;
			try {
				c = db.getConnection();
				PreparedStatement s = c.prepareStatement(sql);
				s.setString(1, name);
				s.execute();
				s.close();
			} catch (Exception e) {
				this.log.error("Unexpected database error incurred in removing the trust level, " + name
					+ ", the following statement generated the error: \n" + sql + "\n", e);
				GTSInternalFault fault = new GTSInternalFault();
				fault.setFaultString("Unexpected error removing the trust level " + name);
				throw fault;
			} finally {
				db.releaseConnection(c);
			}
		} else {
			InvalidTrustLevelFault fault = new InvalidTrustLevelFault();
			fault.setFaultString("The trust level " + name + " does not exist.");
			throw fault;
		}
	}


	public synchronized boolean doesTrustLevelExist(String name) throws GTSInternalFault {
		this.buildDatabase();
		String sql = "select count(*) from " + TrustLevelTable.TABLE_NAME + " where " + TrustLevelTable.NAME + "= ?";
		Connection c = null;
		boolean exists = false;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement(sql);
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
			this.log.error("Unexpected database error incurred in determining if the TrustLevel " + name
				+ " exists, the following statement generated the error: \n" + sql + "\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in determining if the Trust Level " + name + " exists.");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return exists;
	}


	public synchronized void buildDatabase() throws GTSInternalFault {
		if (!dbBuilt) {
			try {
				db.createDatabase();
				if (!this.db.tableExists(TrustLevelTable.TABLE_NAME)) {
					String sql = dbManager.getTrustLevelTable().getCreateTableSQL();
					db.update(sql);
				}
				dbBuilt = true;
			} catch (Exception e) {
				this.log.error("Unexpected error in creating the database.", e);
				GTSInternalFault fault = new GTSInternalFault();
				fault.setFaultString("Unexpected error in creating the database.");
				throw fault;
			}
		}
	}


	public synchronized void clearDatabase() throws GTSInternalFault {
		try {
			this.buildDatabase();
			db.update("delete FROM " + TrustLevelTable.TABLE_NAME);
		} catch (Exception e) {
			this.log.error("Unexpected error in removing the database.", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in removing the database.");
			throw fault;
		}
	}

}
