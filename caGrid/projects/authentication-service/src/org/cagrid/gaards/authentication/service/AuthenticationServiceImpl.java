package org.cagrid.gaards.authentication.service;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.AuthenticationProfiles;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class AuthenticationServiceImpl extends AuthenticationServiceImplBase {

	private AuthenticationManager auth;

	public AuthenticationServiceImpl() throws RemoteException {
		super();
		try {
			String configFile = AuthenticationServiceConfiguration
					.getConfiguration().getAuthenticationConfiguration();
			String propertiesFile = AuthenticationServiceConfiguration
					.getConfiguration().getAuthenticationProperties();
			this.auth = new AuthenticationManager(new File(propertiesFile),
					new File(configFile));
			Set<QName> set = this.auth.getSupportedAuthenticationProfiles();
			QName[] list = new QName[set.size()];
			list = set.toArray(list);
			AuthenticationProfiles profiles = new AuthenticationProfiles();
			profiles.setProfile(list);
			getResourceHome().getAddressedResource().setAuthenticationProfiles(
					profiles);
		} catch (Exception ex) {
			throw new RemoteException(
					"Error instantiating AuthenticationProvider: "
							+ ex.getMessage(), ex);
		}
	}

  public gov.nih.nci.cagrid.authentication.bean.SAMLAssertion authenticate(gov.nih.nci.cagrid.authentication.bean.Credential credential) throws RemoteException, gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault, gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault, gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault {
		return this.auth.authenticate(credential);
	}

  public gov.nih.nci.cagrid.opensaml.SAMLAssertion authenticateUser(org.cagrid.gaards.authentication.Credential credential) throws RemoteException, org.cagrid.gaards.authentication.faults.AuthenticationProviderFault, org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault, org.cagrid.gaards.authentication.faults.InsufficientAttributeFault, org.cagrid.gaards.authentication.faults.InvalidCredentialFault {
		return this.auth.authenticate(credential);
	}

}
