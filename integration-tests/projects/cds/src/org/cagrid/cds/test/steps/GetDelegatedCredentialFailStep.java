/*
 * Created on Jul 14, 2006
 */
package org.cagrid.cds.test.steps;


import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.cds.test.util.DelegatedCredential;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;

public class GetDelegatedCredentialFailStep extends Step {
	private GridCredentialRequestStep credential;

	private DelegatedCredential delegatedCredential;
	private String expectedError;

	public GetDelegatedCredentialFailStep(
			DelegatedCredential delegatedCredential, GridCredentialRequestStep credential,
			String expectedError) {
		this.credential = credential;
		this.delegatedCredential = delegatedCredential;
		this.expectedError = expectedError;
	}

	@Override
	public void runStep() throws Throwable {
		assertNotNull(this.credential);
		assertNotNull(this.credential.getGridCredential());
		assertNotNull(this.delegatedCredential);
		assertNotNull(this.delegatedCredential
				.getDelegatedCredentialReference());
		assertNotNull(this.expectedError);
		DelegatedCredentialUserClient client = new DelegatedCredentialUserClient(
				this.delegatedCredential.getDelegatedCredentialReference(),
				this.credential.getGridCredential());
		try {
			client.getDelegatedCredential();
			fail("Should not be able to get delegated credential.");
		} catch (Exception e) {
			String error = Utils.getExceptionMessage(e);
			if (error.indexOf(expectedError)==-1) {
				fail("Unexpected error encountered:\nEXPECTED:" + expectedError
						+ "\n RECEIVED:" + error);
			}
		}

	}

}
