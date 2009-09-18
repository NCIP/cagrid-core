package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

public class DeprecatedAuthenticationStep extends BaseAuthenticationStep {

	private Credential credential;

	public DeprecatedAuthenticationStep(String serviceURL,
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
		AuthenticationClient client = new AuthenticationClient(getServiceURL(),
				this.credential);
		return client.authenticate();
	}

}
