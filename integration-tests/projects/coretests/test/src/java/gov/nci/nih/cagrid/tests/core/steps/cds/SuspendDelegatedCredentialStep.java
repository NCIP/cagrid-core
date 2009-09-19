/*
 * Created on Jul 14, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps.cds;

import gov.nci.nih.cagrid.tests.core.DelegatedCredential;
import gov.nci.nih.cagrid.tests.core.GridCredential;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.globus.gsi.GlobusCredential;

public class SuspendDelegatedCredentialStep extends Step implements
		GridCredential {

	private GridCredential credential;

	private DelegatedCredential delegatedCredential;

	private GlobusCredential proxy = null;

	public SuspendDelegatedCredentialStep(
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
		client.suspend();

		try {
			client.getDelegatedCredential();
			fail("Should not be able to get a delegated credential that was suspended.");
		} catch (Exception e) {
			if (Utils.getExceptionMessage(e).indexOf(
					"org.globus.wsrf.NoSuchResourceException") == -1) {
				FaultUtil.printFault(e);
				fail("Should get a NoSuchResourceException when trying to get a suspended credential.");
			}
		}
	}

	public GlobusCredential getCredential() {
		return this.proxy;
	}

}
