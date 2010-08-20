package org.cagrid.gaards.websso.authentication.helper.impl;

import org.cagrid.gaards.authentication.faults.AuthenticationProviderFault;
import org.cagrid.gaards.authentication.faults.InsufficientAttributeFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import gov.nih.nci.cagrid.common.FaultUtil;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.websso.authentication.helper.AuthenticationServiceHelper;
import org.cagrid.gaards.websso.exception.AuthenticationErrorException;
import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;

public class AuthenticationServiceHelperImpl
		implements
			AuthenticationServiceHelper {
	
	private final Log log = LogFactory.getLog(getClass());
	
	public AuthenticationServiceHelperImpl() {
		super();
	}

	public SAMLAssertion authenticate(String authenticationServiceURL,
			Credential credential) throws AuthenticationErrorException,
			AuthenticationConfigurationException {

		try {
			AuthenticationClient authenticationClient = new AuthenticationClient(
					authenticationServiceURL);
			SAMLAssertion samlAssertion = authenticationClient
					.authenticate(credential);
			log.debug("authentication successful url"
					+ authenticationServiceURL);
			return samlAssertion;
		} catch (Exception e) {
			handleException(e);
		}
		return null;
	}

	private void handleException(Exception e)
			throws AuthenticationErrorException,
			AuthenticationConfigurationException {
		if (e instanceof MalformedURIException) {
			log.error(e);
			throw new AuthenticationConfigurationException(
					"Invalid Authentication Service URL");
		}
		log.error(FaultUtil.printFaultToString(e));
		if (e instanceof InvalidCredentialFault) {
			String faultString = ((InvalidCredentialFault) e).getFaultString();
			throw new AuthenticationErrorException(faultString);
		}
		if (e instanceof InsufficientAttributeFault) {
			String faultString = ((InsufficientAttributeFault) e)
					.getFaultString();
			throw new AuthenticationConfigurationException(faultString);
		}
		if (e instanceof AuthenticationProviderFault) {
			String faultString = ((AuthenticationProviderFault) e)
					.getFaultString();
			throw new AuthenticationConfigurationException(faultString);
		}
		if (e instanceof RemoteException) {
			log.error(e);
			throw new AuthenticationConfigurationException(
					"Error accessing the Authentication Service");
		}
	}
}
