package org.cagrid.gaards.websso.beans;

import java.io.Serializable;
import java.util.Set;

import javax.xml.namespace.QName;

public class AuthenticationServiceInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String authenticationServiceName = null;
	private String authenticationServiceURL = null;
	private String authenticationServiceIdentity = null;
	private Set<QName> authenticationServiceProfiles;

	
	public AuthenticationServiceInformation(String authenticationServiceName,
			String authenticationServiceURL,
			String authenticationServiceIdentity) {
		super();
		this.authenticationServiceIdentity = authenticationServiceIdentity;
		this.authenticationServiceName = authenticationServiceName;
		this.authenticationServiceURL = authenticationServiceURL;
	}

	public Set<QName> getAuthenticationServiceProfiles() {
		return authenticationServiceProfiles;
	}

	public void setAuthenticationServiceProfiles(
			Set<QName> authenticationServiceProfiles) {
		this.authenticationServiceProfiles = authenticationServiceProfiles;
	}

	public String getAuthenticationServiceName() {
		return authenticationServiceName;
	}

	public String getAuthenticationServiceURL() {
		return authenticationServiceURL;
	}

	public String getAuthenticationServiceIdentity() {
		return authenticationServiceIdentity;
	}
	
	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof AuthenticationServiceInformation) {
			if (this.authenticationServiceURL
					.equals(((AuthenticationServiceInformation) arg0).getAuthenticationServiceURL()))
				return true;
		}
		return false;
	}
}
