package org.cagrid.gaards.cds.service.policy;

import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.InvalidPolicyFault;

public interface PolicyHandler {
	public void removeAllStoredPolicies() throws CDSInternalFault;
	
	public boolean isSupported(String policyClassName);

	public void storePolicy(DelegationIdentifier id, DelegationPolicy policy)
			throws CDSInternalFault, InvalidPolicyFault;

	public void removePolicy(DelegationIdentifier id) throws CDSInternalFault;

	public DelegationPolicy getPolicy(DelegationIdentifier id)
			throws CDSInternalFault, InvalidPolicyFault;

	public boolean isAuthorized(DelegationIdentifier id, String gridIdentity)
			throws CDSInternalFault;
}
