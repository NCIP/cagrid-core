package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.client.AuthenticationClient;

public class AuthenticationStep extends BaseAuthenticationStep {

	private Credential credential;

	public AuthenticationStep(String serviceURL,
			AuthenticationOutcome outcome, Credential credential) {
		super(serviceURL, outcome);
		this.credential = credential;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	public SAMLAssertion authenticate() throws Exception {
		AuthenticationClient client = new AuthenticationClient(getServiceURL());
		return client.authenticate(this.credential);
	}

}
