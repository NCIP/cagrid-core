package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.authentication.test.system.steps.AuthenticationStep;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.globus.gsi.GlobusCredential;

public class GridCredentialRequestStep extends Step {

	private String serviceURL;
	private AuthenticationStep auth;
	private GlobusCredential gridCredential;
	private GridCredentialRequestOutcome outcome;

	public GridCredentialRequestStep(String serviceURL,
			AuthenticationStep auth, GridCredentialRequestOutcome outcome) {
		this.serviceURL = serviceURL;
		this.auth = auth;
		this.outcome = outcome;
	}

	public void runStep() throws Throwable {
		Throwable error = null;
		try {
			CertificateLifetime lifetime = new CertificateLifetime();
			lifetime.setHours(12);
			GridUserClient client = new GridUserClient(this.serviceURL);
			this.gridCredential = client.requestUserCertificate(this.auth.getSAML(),
					lifetime);
		} catch (Throwable e) {
			error = e;
		}
		this.outcome.check(this.gridCredential, error);
	}

	public GlobusCredential getGridCredential() {
		return gridCredential;
	}
}
