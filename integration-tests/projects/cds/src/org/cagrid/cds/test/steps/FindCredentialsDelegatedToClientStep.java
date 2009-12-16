/*
 * Created on Jul 14, 2006
 */
package org.cagrid.cds.test.steps;


import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.ArrayList;
import java.util.List;

import org.cagrid.cds.test.util.DelegationIdentifierReference;
import org.cagrid.cds.test.util.GridCredential;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.DelegationDescriptor;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.Utils;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;
import org.globus.gsi.GlobusCredential;

public class FindCredentialsDelegatedToClientStep extends Step implements
		GridCredential {

	private GridCredentialRequestStep credential;

	private GlobusCredential proxy = null;
	private String uri;
	private List<DelegationIdentifierReference> expected;

	public FindCredentialsDelegatedToClientStep(String uri,
			GridCredentialRequestStep credential) {
		this(uri, credential, (List<DelegationIdentifierReference>) null);
	}

	public FindCredentialsDelegatedToClientStep(String uri,
			GridCredentialRequestStep credential, DelegationIdentifierReference ref) {
		this.uri = uri;
		this.credential = credential;
		this.expected = new ArrayList<DelegationIdentifierReference>();
		this.expected.add(ref);
	}

	public FindCredentialsDelegatedToClientStep(String uri,
			GridCredentialRequestStep credential,
			List<DelegationIdentifierReference> expected) {
		this.uri = uri;
		this.credential = credential;
		this.expected = expected;
		if (expected == null) {
			this.expected = new ArrayList<DelegationIdentifierReference>();
		}
	}

	@Override
	public void runStep() throws Throwable {
		assertNotNull(uri);
		assertNotNull(this.credential);
		assertNotNull(this.credential.getGridCredential());
		DelegationUserClient client = new DelegationUserClient(uri,
				this.credential.getGridCredential());
		List<DelegationDescriptor> results = client
				.findCredentialsDelegatedToClient();
		assertEquals(expected.size(), results.size());
		for (int i = 0; i < results.size(); i++) {
			boolean found = false;
			for (int j = 0; j < expected.size(); j++) {
				DelegationIdentifier id = Utils.getDelegationIdentifier(results
						.get(i).getDelegatedCredentialReference());
				if (id.equals(expected.get(j).getDelegationIdentifier())) {
					found = true;
					break;
				}

			}
			assertTrue(found);
		}

	}

	public GlobusCredential getCredential() {
		return this.proxy;
	}

}
