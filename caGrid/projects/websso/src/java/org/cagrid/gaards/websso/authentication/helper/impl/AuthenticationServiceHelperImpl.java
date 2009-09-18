package org.cagrid.gaards.websso.authentication.helper.impl;

import gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.websso.authentication.helper.AuthenticationServiceHelper;
import org.cagrid.gaards.websso.exception.AuthenticationErrorException;
import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;

public class AuthenticationServiceHelperImpl implements
		AuthenticationServiceHelper {

	public AuthenticationServiceHelperImpl() {
		super();
	}

	public SAMLAssertion authenticate(String authenticationServiceURL,
			Credential credential)
			throws AuthenticationErrorException,
			AuthenticationConfigurationException {
		SAMLAssertion samlAssertion = null;

		AuthenticationClient authenticationClient;
		try {
			authenticationClient = new AuthenticationClient(authenticationServiceURL);
		} catch (MalformedURIException e) {
			throw new AuthenticationConfigurationException(
					"Invalid Authentication Service URL",e);
		} catch (RemoteException e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Authentication Service",e);
		}
		try {
			samlAssertion = authenticationClient.authenticate(credential);
		} catch (InvalidCredentialFault e) {
			throw new AuthenticationErrorException("Invalid Credentials : "
					+ FaultUtil.printFaultToString(e));
		} catch (InsufficientAttributeFault e) {
			throw new AuthenticationConfigurationException(
					"Insufficient Attribute configured for the Authentication Service : "
							+ FaultUtil.printFaultToString(e));
		} catch (AuthenticationProviderFault e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Authentication Provider : "
							+ FaultUtil.printFaultToString(e));
		} catch (RemoteException e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Authentication Service : "
							+ FaultUtil.printFaultToString(e));
		}
		return samlAssertion;
	}
}
