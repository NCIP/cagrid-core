package org.cagrid.gaards.cds.service.policy;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.InvalidPolicyFault;
import org.cagrid.tools.database.Database;

public class IdentityPolicyHandler implements PolicyHandler {

	private final static String TABLE = "identity_policies";
	private final static String DELEGATION_ID = "delegation_id";
	private final static String GRID_IDENTITY = "grid_identity";

	private boolean dbBuilt = false;

	private Database db;
	private Log log;

	public IdentityPolicyHandler(Database db) {
		this.log = LogFactory.getLog(this.getClass().getName());
		this.db = db;
	}

	public void removePolicy(DelegationIdentifier id) throws CDSInternalFault {
		buildDatabase();
		Connection c = null;
		try {
			c = this.db.getConnection();
			PreparedStatement s = c.prepareStatement("DELETE FROM " + TABLE
					+ "  WHERE " + DELEGATION_ID + "= ?");
			s.setLong(1, id.getDelegationId());
			s.execute();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected Database Error.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		} finally {
			this.db.releaseConnection(c);
		}
	}

	public DelegationPolicy getPolicy(DelegationIdentifier id)
			throws CDSInternalFault, InvalidPolicyFault {
		if (policyExists(id)) {
			List<String> parties = new ArrayList<String>();
			Connection c = null;
			try {
				c = this.db.getConnection();
				PreparedStatement s = c.prepareStatement("select "
						+ GRID_IDENTITY + " from " + TABLE + " WHERE "
						+ DELEGATION_ID + "= ? ");
				s.setLong(1, id.getDelegationId());
				ResultSet rs = s.executeQuery();
				while (rs.next()) {
					parties.add(rs.getString(1));
				}
				rs.close();
				s.close();
				IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
				AllowedParties ap = new AllowedParties();
				String[] identities = new String[parties.size()];
				ap.setGridIdentity(parties.toArray(identities));
				policy.setAllowedParties(ap);
				return policy;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				CDSInternalFault f = new CDSInternalFault();
				f.setFaultString("Unexpected Database Error.");
				FaultHelper helper = new FaultHelper(f);
				helper.addFaultCause(e);
				f = (CDSInternalFault) helper.getFault();
				throw f;
			} finally {
				this.db.releaseConnection(c);
			}
		} else {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString("The requested policy does not exist.");
			throw f;

		}
	}

	public boolean isAuthorized(DelegationIdentifier id, String gridIdentity)
			throws CDSInternalFault {
		boolean isAuthorized = false;
		Connection c = null;
		try {
			c = this.db.getConnection();
			PreparedStatement s = c.prepareStatement("select count(*) "
					+ " from " + TABLE + " WHERE " + DELEGATION_ID + "= ? AND "
					+ GRID_IDENTITY + "= ?");
			s.setLong(1, id.getDelegationId());
			s.setString(2, gridIdentity);
			ResultSet rs = s.executeQuery();
			if (rs.next()) {
				int count = rs.getInt(1);
				if (count > 0) {
					isAuthorized = true;
				}
			}
			rs.close();
			s.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected Database Error.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		} finally {
			this.db.releaseConnection(c);
		}
		return isAuthorized;
	}

	public void storePolicy(DelegationIdentifier id, DelegationPolicy pol)
			throws CDSInternalFault, InvalidPolicyFault {
		this.buildDatabase();
		if (!isSupported(pol.getClass().getName())) {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString("The policy handler " + getClass().getName()
					+ " does not support the policy "
					+ pol.getClass().getName() + ".");
			throw f;

		}
		if (this.policyExists(id)) {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString("A policy already exists for the delegation "
					+ id.getDelegationId());
			throw f;
		}

		IdentityDelegationPolicy policy = (IdentityDelegationPolicy) pol;

		Connection c = null;
		boolean policyStored = false;
		try {
			c = this.db.getConnection();

			AllowedParties ap = policy.getAllowedParties();

			if (ap != null) {
				String[] parties = ap.getGridIdentity();
				if (parties != null) {
					for (int i = 0; i < parties.length; i++) {
						PreparedStatement s = c.prepareStatement("INSERT INTO "
								+ TABLE + " SET " + DELEGATION_ID + "= ?, "
								+ GRID_IDENTITY + "= ?");
						s.setLong(1, id.getDelegationId());
						s.setString(2, parties[i]);
						s.execute();
						s.close();
						policyStored = true;
					}
				}
			}

		} catch (Exception e) {
			try {
				this.removePolicy(id);
			} catch (Exception ex) {

			}
			log.error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected Database Error.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		} finally {
			this.db.releaseConnection(c);
		}

		if (!policyStored) {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString("No allowed parties provided.");
			throw f;
		}
	}

	public void removeAllStoredPolicies() throws CDSInternalFault {
		buildDatabase();
		try {
			this.db.update("DELETE FROM " + TABLE);
			dbBuilt = false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected Database Error.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		}

	}

	public boolean isSupported(String policyClassName) {
		if (policyClassName.equals(IdentityDelegationPolicy.class.getName())) {
			return true;
		} else {
			return false;
		}
	}

	public boolean policyExists(DelegationIdentifier id)
			throws CDSInternalFault {
		buildDatabase();
		try {
			return db.exists(TABLE, DELEGATION_ID, id.getDelegationId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected Database Error.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		}
	}

	private void buildDatabase() throws CDSInternalFault {
		if (!dbBuilt) {
			try {
				if (!this.db.tableExists(TABLE)) {
					String table = "CREATE TABLE " + TABLE + " ("
							+ DELEGATION_ID + " BIGINT NOT NULL,"
							+ GRID_IDENTITY
							+ " VARCHAR(255) NOT NULL, INDEX document_index ("
							+ DELEGATION_ID + "));";
					this.db.update(table);
				}
				dbBuilt = true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				CDSInternalFault f = new CDSInternalFault();
				f.setFaultString("Unexpected Database Error.");
				FaultHelper helper = new FaultHelper(f);
				helper.addFaultCause(e);
				f = (CDSInternalFault) helper.getFault();
				throw f;
			}

		}
	}

}
