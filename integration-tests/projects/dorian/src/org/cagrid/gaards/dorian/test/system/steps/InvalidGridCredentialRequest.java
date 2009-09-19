package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;

import org.globus.gsi.GlobusCredential;

public class InvalidGridCredentialRequest implements
		GridCredentialRequestOutcome {

	private String errorMessage;
	private Class error;

	public InvalidGridCredentialRequest(String errorMessage, Class error) {
		super();
		this.errorMessage = errorMessage;
		this.error = error;
	}

	public void check(GlobusCredential credential, Throwable e)
			throws Exception {
		if (e == null) {
			throw new Exception(
					"No error was received when one was expected!!!");
		}
		if ((!error.equals(e.getClass()))) {
			throw new Exception(
					"Did not receive the expected error type in request a grid credential.\nThe error expected was:\n"
							+ error.getName()
							+ "\nThe error received was:\n"
							+ e.getClass().getName());

		} else if ((this.errorMessage != null)
				&& (Utils.getExceptionMessage(e).indexOf(this.errorMessage) == -1)) {
			throw new Exception(
					"Did not receive the expected error message in requesting a grid credential.\nThe error expected was:\n"
							+ this.errorMessage
							+ "\nThe error received was:\n"
							+ Utils.getExceptionMessage(e));
		}
	}
}
