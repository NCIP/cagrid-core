package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gts.bean.AuthorityGTS;
import gov.nih.nci.cagrid.gts.bean.AuthorityPrioritySpecification;
import gov.nih.nci.cagrid.gts.bean.AuthorityPriorityUpdate;
import gov.nih.nci.cagrid.gts.bean.TimeToLive;
import gov.nih.nci.cagrid.gts.common.Database;
import gov.nih.nci.cagrid.gts.service.db.AuthorityTable;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidAuthorityFault;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */

public class GTSAuthorityManager {
	private Log log;

	private boolean dbBuilt = false;

	private Database db;

	private DBManager dbManager;

	private String gtsURI;

	private AuthoritySyncTime syncTime;


	public GTSAuthorityManager(String gtsURI, AuthoritySyncTime syncTime, DBManager dbManager) {
		log = LogFactory.getLog(this.getClass().getName());
		this.dbManager = dbManager;
		this.db = dbManager.getDatabase();
		this.gtsURI = gtsURI;
		this.syncTime = syncTime;
	}


	public synchronized AuthorityGTS getAuthority(String gtsURI) throws GTSInternalFault, InvalidAuthorityFault {
		this.buildDatabase();
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select * from " + AuthorityTable.TABLE_NAME + " where "
				+ AuthorityTable.GTS_URI + "= ?");
			s.setString(1, gtsURI);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				AuthorityGTS gts = new AuthorityGTS();
				gts.setServiceURI(rs.getString(AuthorityTable.GTS_URI));
				gts.setPerformAuthorization(rs.getBoolean(AuthorityTable.PERFORM_AUTH));
				gts.setPriority(rs.getInt(AuthorityTable.PRIORITY));
				gts.setServiceIdentity(Utils.clean(rs.getString(AuthorityTable.GTS_IDENTITY)));
				gts.setSyncTrustLevels(rs.getBoolean(AuthorityTable.SYNC_TRUST_LEVELS));
				TimeToLive ttl = new TimeToLive();
				ttl.setHours(rs.getInt(AuthorityTable.TTL_HOURS));
				ttl.setMinutes(rs.getInt(AuthorityTable.TTL_MINUTES));
				ttl.setSeconds(rs.getInt(AuthorityTable.TTL_SECONDS));
				gts.setTimeToLive(ttl);
				return gts;
			}
			rs.close();
			s.close();
		} catch (Exception e) {
			this.log.fatal("Unexpected database error incurred in obtaining the authority, " + gtsURI
				+ ", the following statement generated the error: \n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error obtaining the authority " + gtsURI);
			throw fault;
		} finally {
			try {
				db.releaseConnection(c);
			} catch (Exception exception) {
				this.log.error(exception.getMessage(), exception);
			}
		}
		InvalidAuthorityFault fault = new InvalidAuthorityFault();
		fault.setFaultString("The authority " + gtsURI + " does not exist.");
		throw fault;
	}


	public synchronized void updateAuthorityPriorities(AuthorityPriorityUpdate update) throws GTSInternalFault,
		IllegalAuthorityFault {

		AuthorityGTS[] auths = this.getAuthorities();
		// Create HashMap
		Map map = new HashMap();
		for (int i = 0; i < auths.length; i++) {
			map.put(auths[i].getServiceURI(), auths[i]);
		}
		// Verfiy that all authorities are accounted for
		AuthorityPrioritySpecification[] specs = update.getAuthorityPrioritySpecification();
		for (int i = 0; i < specs.length; i++) {
			map.remove(specs[i].getServiceURI());
		}

		if (map.size() > 0) {
			StringBuffer error = new StringBuffer();
			error
				.append("Cannot update the authority priorities, an incomplete authority list was provided.\n The provided list was missing the following authorities:\n");
			Iterator itr = map.keySet().iterator();
			while (itr.hasNext()) {
				error.append((String) itr.next() + "\n");
			}
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString(error.toString());
			throw fault;
		}
		// Validate priorities
		int count = this.getAuthorityCount();
		for (int i = 1; i <= count; i++) {
			int found = 0;
			for (int j = 0; j < specs.length; j++) {
				if (i == specs[j].getPriority()) {
					found = found + 1;
				}
			}
			if (found < 1) {
				IllegalAuthorityFault fault = new IllegalAuthorityFault();
				fault
					.setFaultString("Cannot update the authority priorities, no authority specified with the priority "
						+ i + ", each authority must be assigned a unique priority between 1 and " + count + "!!!");
				throw fault;
			} else if (found > 1) {
				IllegalAuthorityFault fault = new IllegalAuthorityFault();
				fault
					.setFaultString("Cannot update the authority priorities, multiple authorities specified with the priority "
						+ i + ", each authority must be assigned a unique priority between 1 and " + count + "!!!");
				throw fault;
			}
		}

		Connection c = null;
		try {
			c = db.getConnection();
			c.setAutoCommit(false);
			for (int i = 0; i < specs.length; i++) {
				updateAuthorityPriority(c, specs[i].getServiceURI(), specs[i].getPriority());
			}
			c.commit();
		} catch (Exception e) {
			if (c != null) {
				try {
					c.rollback();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			this.log.error("Unexpected database error incurred in updating the authority priorities!!!", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in updating the authority priorities!!!");
			throw fault;
		} finally {
			try {
				if (c != null) {
					c.setAutoCommit(true);
				}
			} catch (Exception e) {

			}
			try {
				db.releaseConnection(c);
			} catch (Exception exception) {
				this.log.error(exception.getMessage(), exception);
			}
		}

	}


	protected synchronized void updateAuthorityPriority(Connection c, String uri, int priority)
		throws GTSInternalFault, InvalidAuthorityFault {
		this.buildDatabase();
		if (!doesAuthorityExist(uri)) {

		} else {
			try {
				PreparedStatement update = c.prepareStatement("UPDATE " + AuthorityTable.TABLE_NAME + " SET "
					+ AuthorityTable.PRIORITY + " = ? WHERE " + AuthorityTable.GTS_URI + " = ?");
				update.setInt(1, priority);
				update.setString(2, uri);
				update.executeUpdate();

			} catch (Exception e) {
				this.log.error("Unexpected database error incurred in updating the priority for the authority, " + uri
					+ ".", e);
				GTSInternalFault fault = new GTSInternalFault();
				fault.setFaultString("Unexpected error occurred in updating the priority for the authority, " + uri
					+ ".");
				throw fault;
			}
		}

	}


	public synchronized void updateAuthority(AuthorityGTS gts) throws GTSInternalFault, IllegalAuthorityFault,
		InvalidAuthorityFault {
		this.buildDatabase();
		if (Utils.clean(gts.getServiceURI()) == null) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority cannot be updated, no service URI specified!!!");
			throw fault;
		}

		validateTimeToLive(gts.getTimeToLive());

		if ((gts.isPerformAuthorization()) && (Utils.clean(gts.getServiceIdentity()) == null)) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority, " + gts.getServiceURI()
				+ " cannot be updated, when authorization is required a service identity must be specified!!!");
			throw fault;
		}

		AuthorityGTS curr = getAuthority(gts.getServiceURI());
		if (curr.getPriority() != gts.getPriority()) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault
				.setFaultString("The Authority, "
					+ gts.getServiceURI()
					+ " cannot be updated, priorities cannot be updated using this method, use the update priorities method!!!");
			throw fault;
		}

		Connection c = null;
		try {
			c = db.getConnection();
			if (!gts.equals(curr)) {
				PreparedStatement update = c.prepareStatement("UPDATE " + AuthorityTable.TABLE_NAME + " SET "
					+ AuthorityTable.PRIORITY + " = ?, " + AuthorityTable.SYNC_TRUST_LEVELS + " = ?, "
					+ AuthorityTable.TTL_HOURS + " = ?, " + AuthorityTable.TTL_MINUTES + " = ?, "
					+ AuthorityTable.TTL_SECONDS + " = ?, " + AuthorityTable.PERFORM_AUTH + " = ?, "
					+ AuthorityTable.GTS_IDENTITY + " = ? WHERE " + AuthorityTable.GTS_URI + " = ?");
				update.setInt(1, gts.getPriority());
				update.setString(2, String.valueOf(gts.isSyncTrustLevels()));
				update.setInt(3, gts.getTimeToLive().getHours());
				update.setInt(4, gts.getTimeToLive().getMinutes());
				update.setInt(5, gts.getTimeToLive().getSeconds());
				update.setString(6, String.valueOf(gts.isPerformAuthorization()));
				update.setString(7, gts.getServiceIdentity());
				update.setString(8, gts.getServiceURI());
				update.executeUpdate();
			}
		} catch (Exception e) {

			this.log.error("Unexpected database error incurred in updating the authority " + gts.getServiceURI()
				+ "!!!", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in updating the authority " + gts.getServiceURI() + "!!!");
			throw fault;
		} finally {
			try {
				db.releaseConnection(c);
			} catch (Exception exception) {
				this.log.error(exception.getMessage(), exception);
			}
		}

	}


	private synchronized AuthorityGTS[] getAuthoritiesEqualToOrAfter(int priority) throws GTSInternalFault {
		this.buildDatabase();

		Connection c = null;
		List list = new ArrayList();
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select " + AuthorityTable.GTS_URI + " from "
				+ AuthorityTable.TABLE_NAME + " WHERE " + AuthorityTable.PRIORITY + ">= ? ORDER BY "
				+ AuthorityTable.PRIORITY + "");
			s.setInt(1, priority);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				list.add(rs.getString(AuthorityTable.GTS_URI));
			}
			rs.close();
			s.close();

			AuthorityGTS[] gts = new AuthorityGTS[list.size()];
			for (int i = 0; i < gts.length; i++) {
				String uri = (String) list.get(i);
				gts[i] = this.getAuthority(uri);
			}
			return gts;

		} catch (Exception e) {
			this.log
				.error(
					"Unexpected database error incurred in getting the authorities, the following statement generated the error: \n",
					e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error occurred in getting the authorities.");
			throw fault;
		} finally {
			try {
				db.releaseConnection(c);
			} catch (Exception exception) {
				this.log.error(exception.getMessage(), exception);
			}
		}

	}


	public synchronized AuthorityGTS[] getAuthorities() throws GTSInternalFault {
		this.buildDatabase();

		Connection c = null;
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		try {
			c = db.getConnection();
			Statement s = c.createStatement();
			sql.append("select " + AuthorityTable.GTS_URI + " from " + AuthorityTable.TABLE_NAME + " ORDER BY "
				+ AuthorityTable.PRIORITY + "");
			ResultSet rs = s.executeQuery(sql.toString());
			while (rs.next()) {
				list.add(rs.getString(AuthorityTable.GTS_URI));
			}
			rs.close();
			s.close();

			AuthorityGTS[] gts = new AuthorityGTS[list.size()];
			for (int i = 0; i < gts.length; i++) {
				String uri = (String) list.get(i);
				gts[i] = this.getAuthority(uri);
			}
			return gts;

		} catch (Exception e) {
			this.log.error(
				"Unexpected database error incurred in getting the authorities, the following statement generated the error: \n"
					+ sql.toString() + "\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error occurred in getting the authorities.");
			throw fault;
		} finally {
			try {
				db.releaseConnection(c);
			} catch (Exception exception) {
				this.log.error(exception.getMessage(), exception);
			}
		}

	}


	public synchronized int getAuthorityCount() throws GTSInternalFault {
		this.buildDatabase();
		Connection c = null;
		StringBuffer sql = new StringBuffer();
		try {
			c = db.getConnection();
			Statement s = c.createStatement();
			sql.append("select COUNT(*) from " + AuthorityTable.TABLE_NAME);
			ResultSet rs = s.executeQuery(sql.toString());
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			s.close();
			return count;
		} catch (Exception e) {
			this.log.error(
				"Unexpected database error incurred in getting the authority count, the following statement generated the error: \n"
					+ sql.toString() + "\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error occurred in getting the authority count.");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}

	}


	public synchronized void addAuthority(AuthorityGTS gts) throws GTSInternalFault, IllegalAuthorityFault {
		this.buildDatabase();
		if (Utils.clean(gts.getServiceURI()) == null) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority cannot be added, no service URI specified!!!");
			throw fault;
		}

		if (gts.getServiceURI().equals(gtsURI)) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority cannot be added, a GTS cannot be its own authority!!!");
			throw fault;
		}

		validateTimeToLive(gts.getTimeToLive());

		if ((gts.isPerformAuthorization()) && (Utils.clean(gts.getServiceIdentity()) == null)) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority, " + gts.getServiceURI()
				+ " cannot be added, when authorization is required a service identity must be specified!!!");
			throw fault;
		}

		if (doesAuthorityExist(gts.getServiceURI())) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority, " + gts.getServiceURI() + " cannot be added, it already exists!!!");
			throw fault;
		}

		// Validate the Priority (greater than 1 and not more than the count)
		int count = this.getAuthorityCount() + 1;
		if ((gts.getPriority() < 1) || (gts.getPriority() > count)) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority, " + gts.getServiceURI()
				+ " cannot be added, invalid priority specified the priority must be between 1 and " + count + "!!!");
			throw fault;
		}

		Connection c = null;
		try {
			c = db.getConnection();
			c.setAutoCommit(false);
			// Get the current list of Authorities
			AuthorityGTS[] list = this.getAuthoritiesEqualToOrAfter(gts.getPriority());
			for (int i = 0; i < list.length; i++) {
				this.updateAuthorityPriority(c, list[i].getServiceURI(), (list[i].getPriority() + 1));
			}

			PreparedStatement insert = c.prepareStatement("INSERT INTO " + AuthorityTable.TABLE_NAME + " SET "
				+ AuthorityTable.GTS_URI + " = ?, " + AuthorityTable.PRIORITY + " = ?, "
				+ AuthorityTable.SYNC_TRUST_LEVELS + " = ?, " + AuthorityTable.TTL_HOURS + " = ?, "
				+ AuthorityTable.TTL_MINUTES + " = ?, " + AuthorityTable.TTL_SECONDS + " = ?, "
				+ AuthorityTable.PERFORM_AUTH + " = ?, " + AuthorityTable.GTS_IDENTITY + " = ?");
			insert.setString(1, gts.getServiceURI());
			insert.setInt(2, gts.getPriority());
			insert.setString(3, String.valueOf(gts.isSyncTrustLevels()));
			insert.setInt(4, gts.getTimeToLive().getHours());
			insert.setInt(5, gts.getTimeToLive().getMinutes());
			insert.setInt(6, gts.getTimeToLive().getSeconds());
			insert.setString(7, String.valueOf(gts.isPerformAuthorization()));
			insert.setString(8, gts.getServiceIdentity());
			insert.executeUpdate();
			c.commit();
		} catch (Exception e) {
			if (c != null) {
				try {
					c.rollback();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			this.log.error("Unexpected database error incurred in adding the authority " + gts.getServiceURI() + "!!!",
				e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in adding the authority " + gts.getServiceURI() + "!!!");
			throw fault;
		} finally {
			try {
				if (c != null) {
					c.setAutoCommit(true);
				}
			} catch (Exception e) {

			}
			db.releaseConnection(c);
		}

	}


	public synchronized void removeAuthority(String uri) throws GTSInternalFault, InvalidAuthorityFault {
		this.buildDatabase();
		AuthorityGTS gts = getAuthority(uri);
		Connection c = null;
		try {
			c = db.getConnection();
			c.setAutoCommit(false);
			// Get the current list of Authorities
			AuthorityGTS[] list = this.getAuthoritiesEqualToOrAfter(gts.getPriority());
			for (int i = 0; i < list.length; i++) {
				this.updateAuthorityPriority(c, list[i].getServiceURI(), (list[i].getPriority() - 1));
			}

			PreparedStatement ps = c.prepareStatement("DELETE FROM " + AuthorityTable.TABLE_NAME + " WHERE "
				+ AuthorityTable.GTS_URI + " = ?");
			ps.setString(1, uri);
			ps.executeUpdate();
			c.commit();
		} catch (Exception e) {
			if (c != null) {
				try {
					c.rollback();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			this.log.error("Unexpected database error incurred in deleting the authority " + uri + "!!!", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in deleting the authority " + uri + "!!!");
			throw fault;
		} finally {
			try {
				if (c != null) {
					c.setAutoCommit(true);
				}
			} catch (Exception e) {

			}
			db.releaseConnection(c);
		}

	}


	public synchronized boolean doesAuthorityExist(String gtsURI) throws GTSInternalFault {
		this.buildDatabase();
		Connection c = null;
		boolean exists = false;
		try {
			c = db.getConnection();

			PreparedStatement s = c.prepareStatement("select count(*) from " + AuthorityTable.TABLE_NAME + " where "
				+ AuthorityTable.GTS_URI + "= ?");
			s.setString(1, gtsURI);
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
			this.log.error("Unexpected database error incurred in determining if the Authority GTS " + gtsURI
				+ " exists, the following statement generated the error: \n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in determining if the Authority GTS " + gtsURI + " exists.");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return exists;
	}


	public synchronized void clearDatabase() throws GTSInternalFault {
		try {
			this.buildDatabase();
			db.update("delete FROM " + AuthorityTable.TABLE_NAME);
		} catch (Exception e) {
			this.log.error("Unexpected error in destroying the database.", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in destroying the database.");
			throw fault;
		}
	}


	public synchronized void buildDatabase() throws GTSInternalFault {
		if (!dbBuilt) {
			try {
				db.createDatabase();
				if (!this.db.tableExists(AuthorityTable.TABLE_NAME)) {
					String trust = dbManager.getAuthorityTable().getCreateTableSQL();
					db.update(trust);
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


	private void validateTimeToLive(TimeToLive ttl) throws IllegalAuthorityFault {
		if (ttl == null) {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault.setFaultString("The Authority cannot be added, no time to live specified!!!");
			throw fault;
		}
		if (syncTime != null) {
			Calendar c = new GregorianCalendar();
			Calendar c2 = new GregorianCalendar();
			c2.setTimeInMillis(c.getTimeInMillis());
			c.add(Calendar.HOUR, syncTime.getHours());
			c.add(Calendar.MINUTE, syncTime.getMinutes());
			c.add(Calendar.SECOND, syncTime.getSeconds());

			c2.add(Calendar.HOUR, ttl.getHours());
			c2.add(Calendar.MINUTE, ttl.getMinutes());
			c2.add(Calendar.SECOND, ttl.getSeconds());

			if (c2.before(c)) {
				IllegalAuthorityFault fault = new IllegalAuthorityFault();
				fault
					.setFaultString("The time to live ("
						+ ttl.getHours()
						+ " hour(s), "
						+ ttl.getMinutes()
						+ " minute(s), and "
						+ ttl.getSeconds()
						+ " second(s)"
						+ "), is shorter than how often the GTS syncs with its authorities.\n The gts syncs withs authorities every "
						+ syncTime.getHours() + " hour(s), " + syncTime.getMinutes() + " minute(s), and "
						+ syncTime.getSeconds() + " second(s).");
				throw fault;
			}
		} else {
			IllegalAuthorityFault fault = new IllegalAuthorityFault();
			fault
				.setFaultString("The Authority cannot be added, this GTS is not configured to sync with authorities!!!");
			throw fault;
		}

	}
}
