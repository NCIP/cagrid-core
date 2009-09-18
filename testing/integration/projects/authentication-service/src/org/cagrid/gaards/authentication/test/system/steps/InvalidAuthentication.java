package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

public class InvalidAuthentication extends BaseAuthenticationOutcome implements
		AuthenticationOutcome {

	private String errorMessage;
	private Class error;

	public InvalidAuthentication(String errorMessage, Class error) {
		super();
		this.errorMessage = errorMessage;
		this.error = error;
	}

	public void check(SAMLAssertion saml, Exception e) throws Exception {
		if (e == null) {
			throw new Exception(
					"No error was received when one was expected!!!");
		}
		if ((!error.equals(e.getClass()))) {
			throw new Exception(
					"Did not receive the expected error type in authenticating.\nThe error expected was:\n"
							+ error.getName()
							+ "\nThe error received was:\n"
							+ e.getClass().getName());

		} else if ((this.errorMessage != null)
				&& (Utils.getExceptionMessage(e).indexOf(this.errorMessage) == -1)) {
			throw new Exception(
					"Did not receive the expected error message in authenticating.\nThe error expected was:\n"
							+ this.errorMessage
							+ "\nThe error received was:\n"
							+ Utils.getExceptionMessage(e));
		}
	}
}
