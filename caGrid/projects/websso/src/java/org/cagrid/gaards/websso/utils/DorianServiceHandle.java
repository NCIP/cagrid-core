package org.cagrid.gaards.websso.utils;

import java.rmi.RemoteException;
import java.util.List;

import gov.nih.nci.cagrid.common.Utils;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.websso.beans.DorianInformation;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;

public class DorianServiceHandle{
	private List<AuthenticationServiceHandle> authenticationServices;
	private DorianInformation dorianInformation;
	
	public DorianServiceHandle(DorianInformation dorianInformation) {
		this.dorianInformation = dorianInformation;
	}

	public void setAuthenticationServices(
			List<AuthenticationServiceHandle> authenticationServices) {
		this.authenticationServices = authenticationServices;
	}
	
	public List<AuthenticationServiceHandle> getAuthenticationServices() {
		return authenticationServices;
	}
	
	public DorianInformation getDorianInformation() {
		return dorianInformation;
	}
	
    public GridUserClient getUserClient() throws MalformedURIException, RemoteException{
		GridUserClient client = new GridUserClient(dorianInformation.getDorianServiceURL());
		if (Utils.clean(dorianInformation.getServiceIdentity()) != null) {
			IdentityAuthorization auth = new IdentityAuthorization(dorianInformation.getServiceIdentity());
			client.setAuthorization(auth);
		}
		return client;
	}
}
