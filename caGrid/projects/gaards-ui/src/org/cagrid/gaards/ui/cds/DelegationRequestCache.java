package org.cagrid.gaards.ui.cds;

import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.globus.gsi.GlobusCredential;

public class DelegationRequestCache {

	private CDSHandle cds;

	private GlobusCredential credential;

	private ProxyLifetime delegationLifetime;

	private int delegationPathLength;

	private ProxyLifetime issuedCredentialLifetime;

	private int issuedCredentialPathLength;

	private DelegationPolicy policy;

	public ProxyLifetime getDelegationLifetime() {
		return delegationLifetime;
	}

	public void setDelegationLifetime(ProxyLifetime delegationLifetime) {
		this.delegationLifetime = delegationLifetime;
	}

	public int getDelegationPathLength() {
		return delegationPathLength;
	}

	public void setDelegationPathLength(int delegationPathLength) {
		this.delegationPathLength = delegationPathLength;
	}

	public CDSHandle getDelegationHandle() {
		return this.cds;
	}

	public void setDelegationHandle(CDSHandle cds) {
		this.cds = cds;
	}

	public ProxyLifetime getIssuedCredentialLifetime() {
		return issuedCredentialLifetime;
	}

	public void setIssuedCredentialLifetime(
			ProxyLifetime issuedCredentialLifetime) {
		this.issuedCredentialLifetime = issuedCredentialLifetime;
	}

	public int getIssuedCredentialPathLength() {
		return issuedCredentialPathLength;
	}

	public void setIssuedCredentialPathLength(int issuedCredentialPathLength) {
		this.issuedCredentialPathLength = issuedCredentialPathLength;
	}

	public DelegationPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(DelegationPolicy policy) {
		this.policy = policy;
	}

	public GlobusCredential getCredential() {
		return credential;
	}

	public void setCredential(GlobusCredential proxy) {
		this.credential = proxy;
	}

}
