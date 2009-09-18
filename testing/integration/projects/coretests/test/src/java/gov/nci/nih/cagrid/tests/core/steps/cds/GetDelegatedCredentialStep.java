/*
 * Created on Jul 14, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps.cds;

import gov.nci.nih.cagrid.tests.core.DelegatedCredential;
import gov.nci.nih.cagrid.tests.core.GridCredential;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.globus.gsi.GlobusCredential;

public class GetDelegatedCredentialStep extends Step implements
		GridCredential {

	private GridCredential credential;

	private DelegatedCredential delegatedCredential;

	private GlobusCredential proxy = null;

	public GetDelegatedCredentialStep(
			DelegatedCredential delegatedCredential, GridCredential credential) {
		this.credential = credential;
		this.delegatedCredential = delegatedCredential;
	}

	@Override
	public void runStep() throws Throwable {
		assertNotNull(this.credential);
		assertNotNull(this.credential.getCredential());
		assertNotNull(this.delegatedCredential);
		assertNotNull(this.delegatedCredential
				.getDelegatedCredentialReference());
		DelegatedCredentialUserClient client = new DelegatedCredentialUserClient(
				this.delegatedCredential.getDelegatedCredentialReference(),
				this.credential.getCredential());
		this.proxy = client.getDelegatedCredential();

	}

	public GlobusCredential getCredential() {
		return this.proxy;
	}

}
