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
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;
import org.globus.gsi.GlobusCredential;

public class FindMyDelegatedCredentialsStep extends Step implements
		GridCredential {

	private GridCredentialRequestStep credential;

	private GlobusCredential proxy = null;
	private String uri;
	private DelegationRecordFilter filter;
	private List<DelegationIdentifierReference> expected;

	public FindMyDelegatedCredentialsStep(String uri,
			GridCredentialRequestStep credential) {
		this(uri, credential, null, (List<DelegationIdentifierReference>) null);
	}

	public FindMyDelegatedCredentialsStep(String uri,
			GridCredentialRequestStep credential, DelegationRecordFilter filter) {
		this(uri, credential, filter,
				(List<DelegationIdentifierReference>) null);
	}

	public FindMyDelegatedCredentialsStep(String uri,
			GridCredentialRequestStep credential,
			List<DelegationIdentifierReference> expected) {
		this(uri, credential, new DelegationRecordFilter(), expected);
	}

	public FindMyDelegatedCredentialsStep(String uri,
			GridCredentialRequestStep credential, DelegationIdentifierReference ref) {
		this(uri, credential, new DelegationRecordFilter(), ref);
	}

	public FindMyDelegatedCredentialsStep(String uri,
			GridCredentialRequestStep credential, DelegationRecordFilter f,
			DelegationIdentifierReference ref) {
		this.uri = uri;
		this.credential = credential;
		this.filter = f;
		this.expected = new ArrayList<DelegationIdentifierReference>();
		this.expected.add(ref);
	}

	public FindMyDelegatedCredentialsStep(String uri,
			GridCredentialRequestStep credential, DelegationRecordFilter f,
			List<DelegationIdentifierReference> expected) {
		this.uri = uri;
		this.credential = credential;
		this.filter = f;
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
		List<DelegationRecord> records = client
				.findMyDelegatedCredentials(filter);
		assertEquals(expected.size(), records.size());
		for (int i = 0; i < records.size(); i++) {
			boolean found = false;
			for (int j = 0; j < expected.size(); j++) {
				if (records.get(i).getDelegationIdentifier().equals(
						expected.get(j).getDelegationIdentifier())) {
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
