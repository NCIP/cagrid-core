package org.cagrid.gaards.ui.dorian;

import gov.nih.nci.cagrid.common.Utils;

import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.ui.common.ServiceHandle;
import org.cagrid.grape.configuration.ServiceDescriptor;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;

public class AuthenticationServiceHandle extends ServiceHandle {

	private Set<QName> authenticationProfiles;

	public AuthenticationServiceHandle(ServiceDescriptor des) {
		super(des);
	}

	public AuthenticationClient getAuthenticationClient() throws Exception {
		if (getServiceDescriptor().getServiceURL() != null) {
			AuthenticationClient client = new AuthenticationClient(
					getServiceDescriptor().getServiceURL());
			if (Utils.clean(getServiceDescriptor().getServiceIdentity()) != null) {
				IdentityAuthorization auth = new IdentityAuthorization(
						getServiceDescriptor().getServiceIdentity());
				client.setAuthorization(auth);
			}
			return client;
		}
		return null;
	}

	public Set<QName> getAuthenticationProfiles() {
		return authenticationProfiles;
	}

	public void setAuthenticationProfiles(Set<QName> authenticationProfiles) {
		this.authenticationProfiles = authenticationProfiles;
	}

}
