package org.cagrid.gaards.cds.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.common.CertificateChain;
import org.cagrid.gaards.cds.common.ClientDelegationFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditRecord;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.common.DelegationRequest;
import org.cagrid.gaards.cds.common.DelegationSigningRequest;
import org.cagrid.gaards.cds.common.DelegationSigningResponse;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.common.Errors;
import org.cagrid.gaards.cds.common.PublicKey;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.InvalidPolicyFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.tools.groups.Group;
import org.cagrid.tools.groups.GroupException;
import org.cagrid.tools.groups.GroupManager;

public class DelegationManager {

	public static final String ADMINISTRATORS = "administrators";
	private DelegatedCredentialManager dcm;
	private PropertyManager properties;
	private Group administrators;
	private GroupManager groupManager;
	private Log log;

	public DelegationManager(PropertyManager properties,
			DelegatedCredentialManager dcm, GroupManager groupManager)
			throws CDSInternalFault {
		this.dcm = dcm;
		this.log = LogFactory.getLog(this.getClass().getName());
		this.properties = properties;
		this.groupManager = groupManager;
		try {
			if (!this.groupManager.groupExists(ADMINISTRATORS)) {
				this.groupManager.addGroup(ADMINISTRATORS);
				this.administrators = this.groupManager
						.getGroup(ADMINISTRATORS);
			} else {
				this.administrators = this.groupManager
						.getGroup(ADMINISTRATORS);
			}
		} catch (GroupException e) {
			log.error(e.getMessage(), e);
			CDSInternalFault fault = new CDSInternalFault();
			fault
					.setFaultString("An unexpected error occurred in setting up the administrators group.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CDSInternalFault) helper.getFault();
			throw fault;
		}

	}

	public DelegationSigningRequest initiateDelegation(String callerIdentity,
			DelegationRequest req) throws CDSInternalFault, InvalidPolicyFault,
			DelegationFault, PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		return this.dcm.initiateDelegation(callerIdentity, req);
	}

	public DelegationRecord[] findCredentialsDelegatedToClient(
			String callerIdentity, ClientDelegationFilter filter)
			throws CDSInternalFault, PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		return this.dcm
				.findCredentialsDelegatedToClient(callerIdentity, filter);
	}

	public DelegationIdentifier approveDelegation(String callerIdentity,
			DelegationSigningResponse res) throws CDSInternalFault,
			DelegationFault, PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		return this.dcm.approveDelegation(callerIdentity, res);
	}

	public CertificateChain getDelegatedCredential(String gridIdentity,
			DelegationIdentifier id, PublicKey publicKey)
			throws CDSInternalFault, DelegationFault, PermissionDeniedFault {
		verifyAuthenticated(gridIdentity);
		return this.dcm.getDelegatedCredential(gridIdentity, id, publicKey);
	}

	public void suspendDelegatedCredential(String callerIdentity,
			DelegationIdentifier id) throws CDSInternalFault, DelegationFault,
			PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		DelegationRecord r = this.dcm.getDelegationRecord(id);
		if (r.getGridIdentity().equals(callerIdentity)) {
			this.dcm.updateDelegatedCredentialStatus(callerIdentity, id,
					DelegationStatus.Suspended);
		} else {
			throw Errors.getPermissionDeniedFault();
		}
	}

	public void updateDelegatedCredentialStatus(String callerIdentity,
			DelegationIdentifier id, DelegationStatus status)
			throws CDSInternalFault, DelegationFault, PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		if (isAdmin(callerIdentity)) {
			this.dcm
					.updateDelegatedCredentialStatus(callerIdentity, id, status);
		} else {
			DelegationRecord r = this.dcm.getDelegationRecord(id);
			if (r.getGridIdentity().equals(callerIdentity)) {
				if ((r.getDelegationStatus().equals(DelegationStatus.Approved))
						&& (status.equals(DelegationStatus.Suspended))) {
					this.dcm.updateDelegatedCredentialStatus(callerIdentity,
							id, status);
				} else {
					throw Errors.getPermissionDeniedFault();
				}
			} else {
				throw Errors.getPermissionDeniedFault();
			}
		}
	}

	public DelegationRecord[] findDelegatedCredentials(String callerIdentity,
			DelegationRecordFilter f) throws CDSInternalFault,
			PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		if (f == null) {
			f = new DelegationRecordFilter();
		}
		if (isAdmin(callerIdentity)) {
			return this.dcm.findDelegatedCredentials(f);
		} else {
			f.setGridIdentity(callerIdentity);
			return this.dcm.findDelegatedCredentials(f);
		}
	}

	public void addAdmin(String callerIdentity, String gridIdentity)
			throws CDSInternalFault, PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		verifyAdmin(callerIdentity);
		try {
			this.administrators.addMember(gridIdentity);
		} catch (GroupException e) {
			log.error(e.getMessage(), e);
			CDSInternalFault fault = new CDSInternalFault();
			fault
					.setFaultString("An unexpected error occurred in adding the user as a administrator.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CDSInternalFault) helper.getFault();
			throw fault;
		}
	}

	public void removeAdmin(String callerIdentity, String gridIdentity)
			throws CDSInternalFault, PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		verifyAdmin(callerIdentity);
		try {
			this.administrators.removeMember(gridIdentity);
		} catch (GroupException e) {
			log.error(e.getMessage(), e);
			CDSInternalFault fault = new CDSInternalFault();
			fault
					.setFaultString("An unexpected error occurred in removing the user from the administrators group.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CDSInternalFault) helper.getFault();
			throw fault;
		}
	}

	public String[] getAdmins(String callerIdentity) throws CDSInternalFault,
			PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		verifyAdmin(callerIdentity);
		try {
			List<String> list = this.administrators.getMembers();
			return list.toArray(new String[list.size()]);
		} catch (GroupException e) {
			log.error(e.getMessage(), e);
			CDSInternalFault fault = new CDSInternalFault();
			fault
					.setFaultString("An unexpected error occurred in obtaining a list of administrators.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CDSInternalFault) helper.getFault();
			throw fault;
		}
	}

	public void deleteDelegatedCredential(String callerIdentity,
			DelegationIdentifier id) throws CDSInternalFault,
			PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		verifyAdmin(callerIdentity);
		this.dcm.delete(id);
	}

	private void verifyAdmin(String gridIdentity) throws CDSInternalFault,
			PermissionDeniedFault {
		if (!isAdmin(gridIdentity)) {
			throw Errors.getPermissionDeniedFault(Errors.ADMIN_REQUIRED);
		}
	}

	private void verifyAuthenticated(String callerIdentity)
			throws PermissionDeniedFault {
		if (Utils.clean(callerIdentity) == null) {
			throw Errors
					.getPermissionDeniedFault(Errors.AUTHENTICATION_REQUIRED);
		}
	}

	private boolean isAdmin(String gridIdentity) throws CDSInternalFault {
		try {
			if (this.administrators.isMember(gridIdentity)) {
				return true;
			} else {
				return false;
			}
		} catch (GroupException e) {
			log.error(e.getMessage(), e);
			CDSInternalFault fault = new CDSInternalFault();
			fault
					.setFaultString("An unexpected error occurred in determining if the user is an administrator.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CDSInternalFault) helper.getFault();
			throw fault;
		}
	}

	public void clear() throws CDSInternalFault {
		dcm.clearDatabase();
		properties.clearAllProperties();
		try {
			groupManager.clearDatabase();
		} catch (GroupException e) {
			log.error(e.getMessage(), e);
			CDSInternalFault fault = new CDSInternalFault();
			fault
					.setFaultString("An unexpected error occurred in removing all groups.");
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CDSInternalFault) helper.getFault();
			throw fault;
		}
	}

	public DelegatedCredentialManager getDelegatedCredentialManager() {
		return this.dcm;
	}

	public DelegatedCredentialAuditRecord[] searchDelegatedCredentialAuditLog(
			String callerIdentity, DelegatedCredentialAuditFilter f)
			throws CDSInternalFault, DelegationFault, PermissionDeniedFault {
		verifyAuthenticated(callerIdentity);
		if (f == null) {
			f = new DelegatedCredentialAuditFilter();
		}
		if (isAdmin(callerIdentity)) {
			return this.dcm.searchAuditLog(f);
		} else {
			if (f.getDelegationIdentifier() == null) {
				throw Errors
						.getPermissionDeniedFault(Errors.PERMISSION_DENIED_NO_DELEGATED_CREDENTIAL_SPECIFIED);
			} else {
				DelegationRecord r = this.dcm.getDelegationRecord(f
						.getDelegationIdentifier());
				if (!r.getGridIdentity().equals(callerIdentity)) {
					throw Errors
							.getPermissionDeniedFault(Errors.PERMISSION_DENIED_TO_AUDIT);
				} else {
					return this.dcm.searchAuditLog(f);
				}
			}
		}
	}
}
