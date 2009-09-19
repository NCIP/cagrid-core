package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

public interface AuthenticationOutcome {
	public abstract void check(SAMLAssertion saml, Exception error) throws Exception;
}
