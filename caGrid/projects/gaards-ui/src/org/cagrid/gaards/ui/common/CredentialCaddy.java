package org.cagrid.gaards.ui.common;

import org.globus.gsi.GlobusCredential;


public class CredentialCaddy {
	private GlobusCredential proxy;

	private String identity;


	public CredentialCaddy(GlobusCredential cred) {
		this.identity = cred.getIdentity();
		this.proxy = cred;
	}


	public CredentialCaddy(String identity, GlobusCredential cred) {
		this.identity = identity;
		this.proxy = cred;
	}


	public void setIdentity(String identity) {
		this.identity = identity;
	}


	public void setProxy(GlobusCredential proxy) {
		this.proxy = proxy;
	}


	public String getIdentity() {
		return identity;
	}


	public GlobusCredential getProxy() {
		return proxy;
	}


	public String toString() {
		return identity;
	}


	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		CredentialCaddy caddy = (CredentialCaddy) obj;
		if (this.identity.equals(caddy.getIdentity())) {
			return true;
		} else {
			return false;
		}
	}

}