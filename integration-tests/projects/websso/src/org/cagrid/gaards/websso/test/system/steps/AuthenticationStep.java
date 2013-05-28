/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.websso.test.system.steps;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.authentication.test.system.steps.AuthenticationOutcome;

public class AuthenticationStep extends BaseAuthenticationStep {

	private Credential credential;

	public AuthenticationStep(String serviceURL,
			AuthenticationOutcome outcome, Credential credential) {
		super(serviceURL, outcome);
		this.credential = credential;
	}

	@Override
	public String getName() {
		return super.getName();
	}

	public SAMLAssertion authenticate() throws Exception {
		AuthenticationClient client = new AuthenticationClient(getServiceURL());
		return client.authenticate(this.credential);
	}
}
