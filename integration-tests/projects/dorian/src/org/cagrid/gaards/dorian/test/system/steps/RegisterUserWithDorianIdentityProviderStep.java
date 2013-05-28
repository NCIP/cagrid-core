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
package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.dorian.client.LocalUserClient;
import org.cagrid.gaards.dorian.idp.Application;

public class RegisterUserWithDorianIdentityProviderStep extends Step {

	private Application application;
	private String serviceURL;

	public RegisterUserWithDorianIdentityProviderStep(String serviceURL,
			Application app) {
		this.serviceURL = serviceURL;
		this.application = app;
	}

	public void runStep() throws Throwable {
		LocalUserClient client = new LocalUserClient(serviceURL);
		assertFalse(client.doesUserExist(this.application.getUserId()));
		client.register(this.application);
		assertTrue(client.doesUserExist(this.application.getUserId()));
	}

}
