package org.cagrid.gaards.cds.service.policy;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.GroupDelegationPolicy;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.InvalidPolicyFault;
import org.cagrid.tools.database.Database;

public class GroupPolicyHandler implements PolicyHandler {
	private final static String TABLE = "group_policies";
	private final static String DELEGATION_ID = "delegation_id";
	private final static String GRID_GROUPER_URL = "grid_grouper_url";
	private final static String GROUP_SYSTEM_NAME = "group_name";

	private boolean dbBuilt = false;

	private Database db;

	private Log log;

	public GroupPolicyHandler(Database db) {
		this.log = LogFactory.getLog(this.getClass().getName());
		this.db = db;
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

	public DelegationPolicy getPolicy(DelegationIdentifier id)
			throws CDSInternalFault, InvalidPolicyFault {
		if (policyExists(id)) {
			Connection c = null;
			try {
				GroupDelegationPolicy policy = new GroupDelegationPolicy();
				c = this.db.getConnection();
				PreparedStatement s = c.prepareStatement("select *  from "
						+ TABLE + " WHERE " + DELEGATION_ID + "= ? ");
				s.setLong(1, id.getDelegationId());
				ResultSet rs = s.executeQuery();
				if (rs.next()) {
					policy.setGridGrouperServiceURL(rs
							.getString(GRID_GROUPER_URL));
					policy.setGroupName(rs.getString(GROUP_SYSTEM_NAME));
				}
				rs.close();
				s.close();
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
		try {
			GroupDelegationPolicy policy = (GroupDelegationPolicy) getPolicy(id);
			GridGrouper grouper = new GridGrouper(policy
					.getGridGrouperServiceURL());
			return grouper.isMemberOf(gridIdentity, policy.getGroupName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			CDSInternalFault f = new CDSInternalFault();
			f.setFaultString("Unexpected Error.");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (CDSInternalFault) helper.getFault();
			throw f;
		}
	}

	public boolean isSupported(String policyClassName) {
		if (policyClassName.equals(GroupDelegationPolicy.class.getName())) {
			return true;
		} else {
			return false;
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

		GroupDelegationPolicy policy = (GroupDelegationPolicy) pol;
		if (Utils.clean(policy.getGridGrouperServiceURL()) == null) {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString("Invalid Grid Grouper Service URL specified.");
			throw f;
		}

		if (Utils.clean(policy.getGroupName()) == null) {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString("Invalid Group Name specified.");
			throw f;
		}

		try {
			GridGrouper grouper = new GridGrouper(policy
					.getGridGrouperServiceURL());
			grouper.findGroup(policy.getGroupName());
		} catch (Exception e) {
			InvalidPolicyFault f = new InvalidPolicyFault();
			f.setFaultString("Could not resolve the group "
					+ policy.getGroupName() + " on the Grid Grouper "
					+ policy.getGridGrouperServiceURL() + ".");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (InvalidPolicyFault) helper.getFault();
			throw f;
		}

		Connection c = null;

		try {
			c = this.db.getConnection();
			PreparedStatement s = c.prepareStatement("INSERT INTO " + TABLE
					+ " SET " + DELEGATION_ID + "= ?, " + GRID_GROUPER_URL
					+ "= ?," + GROUP_SYSTEM_NAME + "= ?");
			s.setLong(1, id.getDelegationId());
			s.setString(2, policy.getGridGrouperServiceURL());
			s.setString(3, policy.getGroupName());
			s.execute();
			s.close();

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
	}

	private void buildDatabase() throws CDSInternalFault {
		if (!dbBuilt) {
			try {
				if (!this.db.tableExists(TABLE)) {
					String table = "CREATE TABLE " + TABLE + " ("
							+ DELEGATION_ID + " BIGINT NOT NULL,"
							+ GRID_GROUPER_URL + " VARCHAR(255) NOT NULL,"
							+ GROUP_SYSTEM_NAME
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
