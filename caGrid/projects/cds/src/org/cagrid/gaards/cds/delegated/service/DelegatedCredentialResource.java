package org.cagrid.gaards.cds.delegated.service;

import gov.nih.nci.cagrid.common.Utils;

import org.cagrid.gaards.cds.common.CertificateChain;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.Errors;
import org.cagrid.gaards.cds.common.PublicKey;
import org.cagrid.gaards.cds.service.DelegationManager;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.globus.wsrf.RemoveCallback;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.security.SecurityManager;

public class DelegatedCredentialResource implements Resource, RemoveCallback {

	private DelegationManager cds;
	private DelegationIdentifier id;

	public DelegatedCredentialResource(DelegationManager cds,
			DelegationIdentifier id) {
		this.cds = cds;
		this.id = id;
	}

	public CertificateChain getDelegatedCredential(PublicKey publicKey)
			throws CDSInternalFault, DelegationFault, PermissionDeniedFault {
		return this.cds.getDelegatedCredential(getCallerIdentity(), id,
				publicKey);
	}

	public void remove() throws ResourceException {
		try {
			this.cds.suspendDelegatedCredential(getCallerIdentity(), id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceException(Utils.getExceptionMessage(e), e);
		}
	}

	private String getCallerIdentity() throws PermissionDeniedFault {
		String caller = SecurityManager.getManager().getCaller();
		if ((caller == null) || (caller.equals("<anonymous>"))) {
			PermissionDeniedFault fault = new PermissionDeniedFault();
			fault.setFaultString(Errors.AUTHENTICATION_REQUIRED);
			throw fault;
		}
		return caller;
	}
}
