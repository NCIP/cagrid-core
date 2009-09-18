package org.cagrid.gaards.websso.utils;

import gov.nih.nci.cagrid.common.Utils;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.websso.beans.AuthenticationServiceInformation;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;

public class AuthenticationServiceHandle{

	private AuthenticationServiceInformation authenticationServiceInformation;
	
	public AuthenticationServiceHandle(
			AuthenticationServiceInformation authenticationServiceInformation) {
		super();
		this.authenticationServiceInformation = authenticationServiceInformation;
	}

	public AuthenticationClient getAuthenticationClient()
			throws MalformedURIException, RemoteException {
		AuthenticationClient client;
		client = new AuthenticationClient(authenticationServiceInformation.getAuthenticationServiceURL());
		if (Utils.clean(authenticationServiceInformation.getAuthenticationServiceIdentity()) != null) {
			IdentityAuthorization auth = new IdentityAuthorization(
					authenticationServiceInformation.getAuthenticationServiceIdentity());
			client.setAuthorization(auth);
		}
		return client;
	}
	
	public AuthenticationServiceInformation getAuthenticationServiceInformation() {
		return authenticationServiceInformation;
	}
}
