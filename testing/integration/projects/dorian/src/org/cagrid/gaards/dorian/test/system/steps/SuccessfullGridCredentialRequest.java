package org.cagrid.gaards.dorian.test.system.steps;

import org.globus.gsi.GlobusCredential;

public class SuccessfullGridCredentialRequest implements
		GridCredentialRequestOutcome {

	private String expectedGridIdentity;

	public SuccessfullGridCredentialRequest() {
	}

	public SuccessfullGridCredentialRequest(String gridIdentity) {
		this.expectedGridIdentity = gridIdentity;
	}

	public void check(GlobusCredential credential, Throwable error)
			throws Exception {
		if (error != null) {
			throw new Exception(
					"The following error was received when one was NOT expected: "
							+ error.getMessage(), error);
		}
		if (credential == null) {
			throw new Exception(
					"No grid credential received when one was expected.");
		}
		if ((expectedGridIdentity != null)
				&& (!credential.getIdentity().equals(expectedGridIdentity))) {
			throw new Exception("A credential with the identity "
					+ credential.getIdentity()
					+ " was received when a credential with the identity "
					+ expectedGridIdentity + " was expected.");
		}
		credential.verify();
	}
}
