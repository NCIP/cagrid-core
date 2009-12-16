package org.cagrid.cds.test.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.cds.test.util.DelegatedCredential;
import org.cagrid.cds.test.util.GridCredential;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;
import org.globus.gsi.GlobusCredential;

public class GetDelegatedCredentialStep extends Step implements
		GridCredential {

	private GridCredentialRequestStep credential;

	private DelegatedCredential delegatedCredential;

	private GlobusCredential proxy = null;

	public GetDelegatedCredentialStep(
			DelegatedCredential delegatedCredential, GridCredentialRequestStep credential) {
		this.credential = credential;
		this.delegatedCredential = delegatedCredential;
	}

	@Override
	public void runStep() throws Throwable {
		assertNotNull(this.credential);
		assertNotNull(this.credential.getGridCredential());
		assertNotNull(this.delegatedCredential);
		assertNotNull(this.delegatedCredential
				.getDelegatedCredentialReference());
		DelegatedCredentialUserClient client = new DelegatedCredentialUserClient(
				this.delegatedCredential.getDelegatedCredentialReference(),
				this.credential.getGridCredential());
		this.proxy = client.getDelegatedCredential();

	}

	public GlobusCredential getCredential() {
		return this.proxy;
	}

}
