package org.cagrid.gaards.websso.authentication.helper;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.websso.exception.AuthenticationErrorException;
import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;

public interface AuthenticationServiceHelper {

	public SAMLAssertion authenticate(String authenticationServiceURL,Credential credential)
			throws AuthenticationErrorException,
			AuthenticationConfigurationException;
}
