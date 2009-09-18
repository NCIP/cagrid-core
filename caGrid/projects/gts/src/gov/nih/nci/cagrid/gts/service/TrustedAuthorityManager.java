package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.gts.bean.Lifetime;
import gov.nih.nci.cagrid.gts.bean.Status;
import gov.nih.nci.cagrid.gts.bean.TrustLevels;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthority;
import gov.nih.nci.cagrid.gts.bean.TrustedAuthorityFilter;
import gov.nih.nci.cagrid.gts.common.Database;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.TrustedAuthorityTable;
import gov.nih.nci.cagrid.gts.service.db.TrustedAuthorityTrustLevelsTable;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustedAuthorityFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidTrustedAuthorityFault;

import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.pki.CertUtil;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: TrustedAuthorityManager.java,v 1.1 2006/03/08 19:48:46 langella
 *          Exp $
 */
public class TrustedAuthorityManager {

	private Log log;

	private boolean dbBuilt = false;

	private Database db;

	private String gtsURI;

	private TrustLevelLookup lookup;

	private DBManager dbManager;


	public TrustedAuthorityManager(String gtsURI, TrustLevelLookup lookup, DBManager dbManager) {
		log = LogFactory.getLog(this.getClass().getName());
		this.gtsURI = gtsURI;
		this.dbManager = dbManager;
		this.db = dbManager.getDatabase();
		this.lookup = lookup;
	}


	public synchronized TrustedAuthority[] findTrustAuthorities(TrustedAuthorityFilter filter) throws GTSInternalFault {

		this.buildDatabase();
		Connection c = null;
		List authorities = new ArrayList();
		TrustedAuthoritySelectStatement select = new TrustedAuthoritySelectStatement();
		select.addSelectField("*");
		try {
			if (filter != null) {

				if (filter.getName() != null) {
					select.addWhereField(TrustedAuthorityTable.NAME, "=", filter.getName());
				}

				if (filter.getCertificateDN() != null) {
					select.addWhereField(TrustedAuthorityTable.CERTIFICATE_DN, "=", filter.getCertificateDN());
				}

				if (filter.getStatus() != null) {
					select.addWhereField(TrustedAuthorityTable.STATUS, "=", filter.getStatus().getValue());
				}

				if (filter.getIsAuthority() != null) {
					select.addWhereField(TrustedAuthorityTable.IS_AUTHORITY, "=", String.valueOf(filter
						.getIsAuthority()));
				}

				if (filter.getAuthorityGTS() != null) {
					select.addWhereField(TrustedAuthorityTable.AUTHORITY_GTS, "=", filter.getAuthorityGTS());
				}

				if (filter.getSourceGTS() != null) {
					select.addWhereField(TrustedAuthorityTable.SOURCE_GTS, "=", filter.getSourceGTS());
				}

				if (filter.getLifetime() != null) {
					if (filter.getLifetime().equals(Lifetime.Valid)) {
						Calendar cal = new GregorianCalendar();
						long time = cal.getTimeInMillis();
						select.addClause("(" + TrustedAuthorityTable.EXPIRES + "=0 OR " + TrustedAuthorityTable.EXPIRES
							+ ">" + time + ")");
					} else if (filter.getLifetime().equals(Lifetime.Expired)) {
						Calendar cal = new GregorianCalendar();
						long time = cal.getTimeInMillis();
						select.addClause("(" + TrustedAuthorityTable.EXPIRES + "<>0 AND "
							+ TrustedAuthorityTable.EXPIRES + "<" + time + ")");
					}
				}

			}

			c = db.getConnection();
			PreparedStatement s = select.prepareStatement(c);
			ResultSet rs = s.executeQuery();

			while (rs.next()) {
				String name = rs.getString(TrustedAuthorityTable.NAME);
				TrustLevels levels = filter.getTrustLevels();
				boolean okToAdd = true;
				if (levels != null) {
					String[] tl = levels.getTrustLevel();
					if (tl != null) {
						for (int i = 0; i < tl.length; i++) {
							if (!this.hasTrustLevels(name, tl[i])) {
								okToAdd = false;
								break;
							}
						}
					}
				}
				if (okToAdd) {
					TrustedAuthority ta = new TrustedAuthority();
					ta.setName(name);
					ta.setTrustLevels(getTrustLevels(name));
					ta.setStatus(Status.fromValue(rs.getString(TrustedAuthorityTable.STATUS)));
					ta.setIsAuthority(Boolean.valueOf(rs.getBoolean(TrustedAuthorityTable.IS_AUTHORITY)));
					ta.setAuthorityGTS(rs.getString(TrustedAuthorityTable.AUTHORITY_GTS));
					ta.setSourceGTS(rs.getString(TrustedAuthorityTable.SOURCE_GTS));
					ta.setExpires(rs.getLong(TrustedAuthorityTable.EXPIRES));
					ta.setLastUpdated(rs.getLong(TrustedAuthorityTable.LAST_UPDATED));
					ta.setCertificate(new gov.nih.nci.cagrid.gts.bean.X509Certificate(rs
						.getString(TrustedAuthorityTable.CERTIFICATE)));
					String crl = rs.getString(TrustedAuthorityTable.CRL);
					if ((crl != null) && (crl.trim().length() > 0)) {
						ta.setCRL(new gov.nih.nci.cagrid.gts.bean.X509CRL(crl));
					}
					authorities.add(ta);
				}

			}
			rs.close();
			s.close();

			TrustedAuthority[] list = new TrustedAuthority[authorities.size()];
			for (int i = 0; i < authorities.size(); i++) {
				list[i] = (TrustedAuthority) authorities.get(i);
			}
			return list;

		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in finding trusted authorities: " + e.getMessage(), e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error occurred in finding Trusted Authorities");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	public synchronized void updateTrustedAuthority(TrustedAuthority ta) throws GTSInternalFault,
		IllegalTrustedAuthorityFault, InvalidTrustedAuthorityFault {
		updateTrustedAuthority(ta, true);
	}


	public synchronized void updateTrustedAuthority(TrustedAuthority ta, boolean internal) throws GTSInternalFault,
		IllegalTrustedAuthorityFault, InvalidTrustedAuthorityFault {

		TrustedAuthority curr = this.getTrustedAuthority(ta.getName());
		StringBuffer sql = new StringBuffer();
		boolean needsUpdate = false;
		UpdateStatement update = new UpdateStatement(TrustedAuthorityTable.TABLE_NAME);
		if (internal) {
			if (!curr.getAuthorityGTS().equals(gtsURI)) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The Trusted Authority cannot be updated, the GTS (" + gtsURI
					+ ") is not its authority!!!");
				throw fault;
			}

			if ((clean(ta.getAuthorityGTS()) != null) && (!ta.getAuthorityGTS().equals(curr.getAuthorityGTS()))) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The authority trust service for a Trusted Authority cannot be changed");
				throw fault;
			}

			if (ta.getCertificate() != null) {
				if ((clean(ta.getCertificate().getCertificateEncodedString()) != null)
					&& (!ta.getCertificate().equals(curr.getCertificate()))) {
					IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
					fault.setFaultString("The certificate for a Trusted Authority cannot be changed");
					throw fault;
				}
			}

			if ((clean(ta.getSourceGTS()) != null) && (!ta.getSourceGTS().equals(curr.getSourceGTS()))) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The source trust service for a Trusted Authority cannot be changed");
				throw fault;
			}

		} else {

			if ((curr.getIsAuthority().booleanValue()) && (!ta.getAuthorityGTS().equals(gtsURI))) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The Trusted Authority " + ta.getName()
					+ " cannot be updated, a conflict was detected, this gts (" + gtsURI
					+ ") was specified as its authority, however the URI of another GTS ( " + ta.getAuthorityGTS()
					+ ") was specified.");
				throw fault;
			}

			if (!ta.getAuthorityGTS().equals(curr.getAuthorityGTS())) {
				update.addField(TrustedAuthorityTable.AUTHORITY_GTS, ta.getAuthorityGTS());
				needsUpdate = true;
			}

			if (ta.getCertificate() != null) {
				if ((clean(ta.getCertificate().getCertificateEncodedString()) != null)
					&& (!ta.getCertificate().equals(curr.getCertificate()))) {
					X509Certificate cert = checkAndExtractCertificate(ta);
					if ((!ta.getName().equals(cert.getSubjectDN().toString()))) {
						IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
						fault
							.setFaultString("The Trusted Authority Name must match the subject of the Trusted Authority's certificate");
						throw fault;
					}

					update.addField(TrustedAuthorityTable.CERTIFICATE, ta.getCertificate()
						.getCertificateEncodedString());
					needsUpdate = true;
				}
			}

			if (!ta.getSourceGTS().equals(curr.getSourceGTS())) {
				update.addField(TrustedAuthorityTable.SOURCE_GTS, ta.getSourceGTS());
				needsUpdate = true;
			}

			if (ta.getExpires() != curr.getExpires()) {
				update.addField(TrustedAuthorityTable.EXPIRES, Long.valueOf(ta.getExpires()));
				needsUpdate = true;
			}

		}

		if ((ta.getIsAuthority() != null) && (!ta.getIsAuthority().equals(curr.getIsAuthority()))) {
			IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
			fault.setFaultString("The authority trust service for a Trusted Authority cannot be changed");
			throw fault;
		}
		
		

		if (ta.getCRL() != null) {
			if ((clean(ta.getCRL().getCrlEncodedString()) != null) && (!ta.getCRL().equals(curr.getCRL()))) {
				X509Certificate cert = checkAndExtractCertificate(ta);
				checkAndExtractCRL(ta, cert);
				update.addField(TrustedAuthorityTable.CRL, ta.getCRL().getCrlEncodedString());
				needsUpdate = true;
			}
		}else{
			if(!internal){
				if(curr.getCRL()!=null){
					update.addField(TrustedAuthorityTable.CRL, "");
					needsUpdate = true;
				}
			}
		}

		if ((ta.getStatus() != null) && (!ta.getStatus().equals(curr.getStatus()))) {
			update.addField(TrustedAuthorityTable.STATUS, ta.getStatus().getValue());
			needsUpdate = true;
		}
		boolean updateTrustLevels = false;

		if ((ta.getTrustLevels() != null)
			&& (!this.areTrustLevelEquals(ta.getTrustLevels().getTrustLevel(), curr.getTrustLevels().getTrustLevel()))) {
			needsUpdate = true;
			updateTrustLevels = true;
		}

		if (!ta.equals(curr)) {
			if (needsUpdate) {
				Connection c = null;
				try {
					Calendar cal = new GregorianCalendar();
					ta.setLastUpdated(cal.getTimeInMillis());
					update.addField(TrustedAuthorityTable.LAST_UPDATED, Long.valueOf(ta.getLastUpdated()));
					update.addWhereField(TrustedAuthorityTable.NAME, "=", ta.getName());
					c = db.getConnection();
					PreparedStatement s = update.prepareUpdateStatement(c);
					s.execute();
					s.close();
				} catch (Exception e) {
					this.log.error("Unexpected database error incurred in updating " + ta.getName()
						+ ", the following statement generated the error: \n" + sql.toString() + "\n", e);
					GTSInternalFault fault = new GTSInternalFault();
					fault.setFaultString("Unexpected error occurred in updating " + ta.getName() + ".");
					throw fault;
				} finally {
					if (c != null) {
						db.releaseConnection(c);
					}
				}
				if (updateTrustLevels) {
					this.addTrustLevels(ta.getName(), ta.getTrustLevels());
				}
			}
		}

	}


	private String clean(String s) {
		if ((s == null) || (s.trim().length() == 0)) {
			return null;
		} else {
			return s;
		}
	}


	public synchronized TrustedAuthority getTrustedAuthority(String name) throws GTSInternalFault,
		InvalidTrustedAuthorityFault {
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select * from " + TrustedAuthorityTable.TABLE_NAME + " where "
				+ TrustedAuthorityTable.NAME + "= ?");
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				TrustedAuthority ta = new TrustedAuthority();
				ta.setName(rs.getString(TrustedAuthorityTable.NAME));
				ta.setTrustLevels(getTrustLevels(name));
				ta.setStatus(Status.fromValue(rs.getString(TrustedAuthorityTable.STATUS)));
				ta.setIsAuthority(Boolean.valueOf(rs.getBoolean(TrustedAuthorityTable.IS_AUTHORITY)));
				ta.setAuthorityGTS(rs.getString(TrustedAuthorityTable.AUTHORITY_GTS));
				ta.setSourceGTS(rs.getString(TrustedAuthorityTable.SOURCE_GTS));
				ta.setExpires(rs.getLong(TrustedAuthorityTable.EXPIRES));
				ta.setLastUpdated(rs.getLong(TrustedAuthorityTable.LAST_UPDATED));
				ta.setCertificate(new gov.nih.nci.cagrid.gts.bean.X509Certificate(rs
					.getString(TrustedAuthorityTable.CERTIFICATE)));
				String crl = rs.getString(TrustedAuthorityTable.CRL);
				if ((crl != null) && (crl.trim().length() > 0)) {
					ta.setCRL(new gov.nih.nci.cagrid.gts.bean.X509CRL(crl));
				}
				return ta;
			}
			rs.close();
			s.close();
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in obtaining the Trusted Authority, " + name + ":\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error obtaining the TrustedAuthority " + name);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		InvalidTrustedAuthorityFault fault = new InvalidTrustedAuthorityFault();
		fault.setFaultString("The TrustedAuthority " + name + " does not exist.");
		throw fault;
	}


	public synchronized boolean doesTrustedAuthorityExist(String name) throws GTSInternalFault {
		this.buildDatabase();
		Connection c = null;
		boolean exists = false;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select count(*) from " + TrustedAuthorityTable.TABLE_NAME
				+ " where " + TrustedAuthorityTable.NAME + "= ?");
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
			this.log.error("Unexpected database error incurred in odetermining if the TrustedAuthority name: \n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in determining if the TrustedAuthority " + name + " exists.");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
		return exists;
	}


	public synchronized void removeTrustedAuthority(String name) throws GTSInternalFault, InvalidTrustedAuthorityFault {
		if (doesTrustedAuthorityExist(name)) {
			Connection c = null;
			try {
				this.removeTrustedAuthoritysTrustLevels(name);
				c = db.getConnection();
				PreparedStatement s = c.prepareStatement("delete FROM " + TrustedAuthorityTable.TABLE_NAME + " where "
					+ TrustedAuthorityTable.NAME + "= ?");
				s.setString(1, name);
				s.execute();
				s.close();

			} catch (Exception e) {
				this.log.error("Unexpected database error incurred in removing the Trusted Authority, " + name
					+ ", the following statement generated the error: \n", e);
				GTSInternalFault fault = new GTSInternalFault();
				fault.setFaultString("Unexpected error removing the TrustedAuthority " + name);
				throw fault;
			} finally {
				db.releaseConnection(c);
			}
		} else {
			InvalidTrustedAuthorityFault fault = new InvalidTrustedAuthorityFault();
			fault.setFaultString("The TrustedAuthority " + name + " does not exist.");
			throw fault;
		}
	}


	public synchronized void removeLevelFromTrustedAuthorities(String level) throws GTSInternalFault {
		buildDatabase();
		Connection c = null;

		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("delete FROM " + TrustedAuthorityTrustLevelsTable.TABLE_NAME
				+ " where " + TrustedAuthorityTrustLevelsTable.TRUST_LEVEL + "= ?");
			s.setString(1, level);
			s.execute();
			s.close();
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in removing the trust level " + level
				+ " from the Trusted Authorities, the following statement generated the error: \n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error the trust level, " + level + " from the trusted authorites!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	private synchronized void insertTrustedAuthority(TrustedAuthority ta, X509Certificate cert, X509CRL crl)
		throws GTSInternalFault {
		StringBuffer insert = new StringBuffer();
		buildDatabase();
		Connection c = null;
		try {

			Calendar cal = new GregorianCalendar();
			ta.setLastUpdated(cal.getTimeInMillis());
			insert.append("INSERT INTO " + TrustedAuthorityTable.TABLE_NAME + " SET " + TrustedAuthorityTable.NAME
				+ "= ?" + "," + TrustedAuthorityTable.CERTIFICATE_DN + "= ?," + TrustedAuthorityTable.STATUS + "= ?,"
				+ TrustedAuthorityTable.IS_AUTHORITY + "= ?," + TrustedAuthorityTable.AUTHORITY_GTS + "= ?,"
				+ TrustedAuthorityTable.SOURCE_GTS + "= ?," + TrustedAuthorityTable.EXPIRES + "= ?,"
				+ TrustedAuthorityTable.LAST_UPDATED + "= ?," + TrustedAuthorityTable.CERTIFICATE + "= ?");

			if (crl != null) {
				insert.append("," + TrustedAuthorityTable.CRL + "= ?");
			}
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement(insert.toString());
			s.setString(1, ta.getName());
			s.setString(2, cert.getSubjectDN().toString());
			s.setString(3, ta.getStatus().getValue());
			s.setString(4, String.valueOf(ta.getIsAuthority().booleanValue()));
			s.setString(5, ta.getAuthorityGTS());
			s.setString(6, ta.getSourceGTS());
			s.setLong(7, ta.getExpires());
			s.setLong(8, ta.getLastUpdated());
			s.setString(9, ta.getCertificate().getCertificateEncodedString());
			if (crl != null) {
				s.setString(10, ta.getCRL().getCrlEncodedString());
			}
			s.execute();
			s.close();
			this.addTrustLevels(ta.getName(), ta.getTrustLevels());
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in adding the Trusted Authority, " + ta.getName()
				+ ", the following statement generated the error: \n" + insert.toString() + "\n", e);
			try {
				this.removeTrustedAuthority(ta.getName());
			} catch (Exception ex) {
				this.log.error(e.getMessage(), e);
			}
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error adding the Trusted Authority, " + ta.getName() + "!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	public synchronized TrustedAuthority addTrustedAuthority(TrustedAuthority ta) throws GTSInternalFault,
		IllegalTrustedAuthorityFault {
		return this.addTrustedAuthority(ta, true);
	}


	public synchronized TrustedAuthority addTrustedAuthority(TrustedAuthority ta, boolean internal)
		throws GTSInternalFault, IllegalTrustedAuthorityFault {
		this.buildDatabase();
		X509Certificate cert = checkAndExtractCertificate(ta);
		if ((ta.getName() != null) && (!ta.getName().equals(cert.getSubjectDN().toString()))) {
			IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
			fault
				.setFaultString("The Trusted Authority Name must match the subject of the Trusted Authority's certificate");
			throw fault;
		} else {
			ta.setName(cert.getSubjectDN().toString());
		}

		if (this.doesTrustedAuthorityExist(ta.getName())) {
			IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
			fault.setFaultString("The Trusted Authority " + ta.getName() + " already exists.");
			throw fault;
		}

		X509CRL crl = checkAndExtractCRL(ta, cert);

		if (ta.getTrustLevels() != null) {
			if (ta.getTrustLevels().getTrustLevel() != null) {
				for (int i = 0; i < ta.getTrustLevels().getTrustLevel().length; i++) {
					if (!lookup.doesTrustLevelExist(ta.getTrustLevels().getTrustLevel()[i])) {
						IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
						fault.setFaultString("The Trusted Authority " + ta.getName()
							+ " could not be added, the trust level " + ta.getTrustLevels().getTrustLevel()[i]
							+ " does not exist.");
						throw fault;
					}
				}
			}
		}
		if (ta.getStatus() == null) {
			IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
			fault.setFaultString("No status specified for the Trusted Authority!!!");
			throw fault;
		}
		if (internal) {
			ta.setIsAuthority(Boolean.TRUE);
			ta.setAuthorityGTS(gtsURI);
			ta.setSourceGTS(gtsURI);
			ta.setExpires(0);
		} else {
			if ((ta.getIsAuthority() == null)) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The Trusted Authority " + ta.getName()
					+ " cannot be added because it does not specify whether or not this GTS is the authority of it.");
				throw fault;
			}

			if (ta.getAuthorityGTS() == null) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The Trusted Authority " + ta.getName()
					+ " cannot be added because it does not specify an authority trust service.");
				throw fault;

			}

			if (ta.getSourceGTS() == null) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The Trusted Authority " + ta.getName()
					+ " cannot be added because it does not specify an source trust service.");
				throw fault;
			}

			if ((!ta.getIsAuthority().booleanValue()) && (ta.getExpires() <= 0)) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The Trusted Authority " + ta.getName()
					+ " cannot be added because it does not specify an expiration.");
				throw fault;
			}

			if ((ta.getIsAuthority().booleanValue()) && (!ta.getAuthorityGTS().equals(gtsURI))) {
				IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
				fault.setFaultString("The Trusted Authority " + ta.getName()
					+ " cannot be added, a conflict was detected, this gts (" + gtsURI
					+ ") was specified as its authority, however the URI of another GTS ( " + ta.getAuthorityGTS()
					+ ") was specified.");
				throw fault;
			}

		}
		insertTrustedAuthority(ta, cert, crl);
		return ta;
	}


	private boolean areTrustLevelEquals(String[] levels1, String[] levels2) {
		if ((levels1 == null) && (levels2 == null)) {
			return true;
		} else if ((levels1 != null) && (levels2 == null)) {
			return false;
		} else if ((levels1 == null) && (levels2 != null)) {
			return false;
		} else if (levels1.length != levels2.length) {
			return false;
		} else {
			Set s = new HashSet();
			for (int i = 0; i < levels1.length; i++) {
				s.add(levels1[i]);
			}
			for (int i = 0; i < levels2.length; i++) {
				s.remove(levels2[i]);
			}
			if (s.size() == 0) {
				return true;
			} else {
				return false;
			}
		}
	}


	public boolean hasTrustLevels(String name, String level) throws GTSInternalFault, InvalidTrustedAuthorityFault {
		Connection c = null;
		try {
			boolean hasLevel = false;
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select count(*) from "
				+ TrustedAuthorityTrustLevelsTable.TABLE_NAME + " where " + TrustedAuthorityTrustLevelsTable.NAME
				+ "= ? AND " + TrustedAuthorityTrustLevelsTable.TRUST_LEVEL + "= ?");
			s.setString(1, name);
			s.setString(2, level);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					hasLevel = true;
				}
			}
			rs.close();
			s.close();
			return hasLevel;
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in determining if the Trusted Authority, " + name
				+ " has the trust level " + level + ":\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected database error incurred in determining if the Trusted Authority, " + name
				+ " has the trust level " + level + "!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	public synchronized TrustLevels getTrustLevels(String name) throws GTSInternalFault, InvalidTrustedAuthorityFault {
		Connection c = null;
		try {
			List list = new ArrayList();
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("select * from " + TrustedAuthorityTrustLevelsTable.TABLE_NAME
				+ " where " + TrustedAuthorityTrustLevelsTable.NAME + "= ?");
			s.setString(1, name);
			ResultSet rs = s.executeQuery();
			while (rs.next()) {
				list.add(rs.getString(TrustedAuthorityTrustLevelsTable.TRUST_LEVEL));
			}
			rs.close();
			s.close();
			TrustLevels tl = new TrustLevels();
			String[] levels = new String[list.size()];
			for (int i = 0; i < levels.length; i++) {
				levels[i] = (String) list.get(i);
			}
			tl.setTrustLevel(levels);
			return tl;
		} catch (Exception e) {
			this.log.error("Unexpected database error incurred in getting the trust levels for the Trusted Authority, "
				+ name + ":\n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault
				.setFaultString("Unexpected database error incurred in getting the trust levels for the Trusted Authority, "
					+ name + "!!!");
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	public synchronized void addTrustLevels(String name, TrustLevels tl) throws GTSInternalFault,
		InvalidTrustedAuthorityFault, IllegalTrustedAuthorityFault {
		if (tl != null) {
			String[] levels = tl.getTrustLevel();
			if ((levels != null) && (levels.length > 0)) {
				for (int i = 0; i < levels.length; i++) {
					if (!lookup.doesTrustLevelExist(levels[i])) {
						IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
						fault.setFaultString("The trust levels for the Trusted Authority " + name
							+ " could not be updated, the trust level " + levels[i] + " does not exist.");
						throw fault;
					}
				}
			}
			removeTrustedAuthoritysTrustLevels(name);
			if ((levels != null) && (levels.length > 0)) {

				Connection c = null;
				try {
					c = db.getConnection();
					for (int i = 0; i < levels.length; i++) {
						PreparedStatement s = c.prepareStatement("INSERT INTO "
							+ TrustedAuthorityTrustLevelsTable.TABLE_NAME + " SET "
							+ TrustedAuthorityTrustLevelsTable.NAME + "= ?, "
							+ TrustedAuthorityTrustLevelsTable.TRUST_LEVEL + "= ?");
						s.setString(1, name);
						s.setString(2, levels[i]);
						s.execute();
						s.close();
					}
				} catch (Exception e) {
					this.log.error(
						"Unexpected database error incurred in adding the trust levels for the Trusted Authority, "
							+ name + ": " + e.getMessage(), e);
					try {
						this.removeTrustedAuthoritysTrustLevels(name);
					} catch (Exception ex) {
						this.log.error(ex.getMessage(), ex);
					}
					GTSInternalFault fault = new GTSInternalFault();
					fault.setFaultString("Unexpected error removing the TrustedAuthority " + name);
					throw fault;
				} finally {
					db.releaseConnection(c);
				}
			}
		}
	}


	public synchronized void removeTrustedAuthoritysTrustLevels(String name) throws GTSInternalFault,
		InvalidTrustedAuthorityFault {
		Connection c = null;
		try {
			c = db.getConnection();
			PreparedStatement s = c.prepareStatement("delete FROM " + TrustedAuthorityTrustLevelsTable.TABLE_NAME
				+ " where " + TrustedAuthorityTrustLevelsTable.NAME + "= ?");
			s.setString(1, name);
			s.execute();
			s.close();
		} catch (Exception e) {
			this.log.error(
				"Unexpected database error incurred in removing the trust levels for the Trusted Authority, " + name
					+ ", the following statement generated the error: \n", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error removing the TrustedAuthority " + name);
			throw fault;
		} finally {
			db.releaseConnection(c);
		}
	}


	private X509Certificate checkAndExtractCertificate(TrustedAuthority ta) throws IllegalTrustedAuthorityFault {
		if (ta.getCertificate() == null) {
			IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
			fault.setFaultString("No certificate specified!!!");
			throw fault;
		}

		if (ta.getCertificate().getCertificateEncodedString() == null) {
			IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
			fault.setFaultString("No certificate specified!!!");
			throw fault;
		}

		try {
			return CertUtil.loadCertificate(ta.getCertificate().getCertificateEncodedString());
		} catch (Exception ex) {
			IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
			fault.setFaultString("Invalid certificate Provided!!!");
			throw fault;
		}
	}


	private X509CRL checkAndExtractCRL(TrustedAuthority ta, X509Certificate signer) throws IllegalTrustedAuthorityFault {
		X509CRL crl = null;
		if (ta.getCRL() != null) {

			if (ta.getCRL().getCrlEncodedString() != null) {
				try {
					crl = CertUtil.loadCRL(ta.getCRL().getCrlEncodedString());
				} catch (Exception ex) {
					IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
					fault.setFaultString("Invalid CRL provided!!!");
					throw fault;
				}
				try {
					crl.verify(signer.getPublicKey());
				} catch (Exception e) {
					IllegalTrustedAuthorityFault fault = new IllegalTrustedAuthorityFault();
					fault.setFaultString("The CRL provided is not signed by the Trusted Authority!!!");
					throw fault;
				}

			}
		}

		return crl;
	}


	public synchronized void buildDatabase() throws GTSInternalFault {
		if (!dbBuilt) {
			try {
				db.createDatabase();
				if (!this.db.tableExists(TrustedAuthorityTable.TABLE_NAME)) {
					String sql = dbManager.getTrustedAuthorityTable().getCreateTableSQL();
					db.update(sql);
				}

				if (!this.db.tableExists(TrustedAuthorityTrustLevelsTable.TABLE_NAME)) {
					String sql = dbManager.getTrustedAuthorityTrustLevelsTable().getCreateTableSQL();
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
			buildDatabase();
			db.update("delete FROM " + TrustedAuthorityTable.TABLE_NAME);
			db.update("delete FROM " + TrustedAuthorityTrustLevelsTable.TABLE_NAME);
		} catch (Exception e) {
			this.log.error("Unexpected error in removing the database.", e);
			GTSInternalFault fault = new GTSInternalFault();
			fault.setFaultString("Unexpected error in removing the database.");
			throw fault;
		}
	}
}
