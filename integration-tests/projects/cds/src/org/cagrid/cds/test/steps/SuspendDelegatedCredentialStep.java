/*
 * Created on Jul 14, 2006
 */
package org.cagrid.cds.test.steps;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.cds.test.util.DelegatedCredential;
import org.cagrid.cds.test.util.GridCredential;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;
import org.globus.gsi.GlobusCredential;

public class SuspendDelegatedCredentialStep extends Step implements
		GridCredential {

	private GridCredentialRequestStep credential;

	private DelegatedCredential delegatedCredential;

	private GlobusCredential proxy = null;

	public SuspendDelegatedCredentialStep(
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
