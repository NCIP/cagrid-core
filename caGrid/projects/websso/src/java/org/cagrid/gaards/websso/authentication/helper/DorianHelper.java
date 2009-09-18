package org.cagrid.gaards.websso.authentication.helper;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.cagrid.gaards.websso.beans.DorianInformation;
import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;
import org.cagrid.gaards.websso.exception.AuthenticationErrorException;
import org.globus.gsi.GlobusCredential;

public interface DorianHelper {

	public GlobusCredential obtainProxy(SAMLAssertion samlAssertion,
			DorianInformation dorianInformation)
			throws AuthenticationConfigurationException,
			AuthenticationErrorException;
}
