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
package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

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
