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
/*
 * Created on Jul 14, 2006
 */
package org.cagrid.cds.test.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.dorian.client.LocalUserClient;
import org.cagrid.gaards.dorian.idp.Application;

/**
 * This step submits to dorian an application for user account registration.
 * 
 * @author Patrick McConnell
 */
public class DorianSubmitRegistrationStep extends Step {
	private String serviceURL;
	private Application application;

	public DorianSubmitRegistrationStep(Application application,
			String serviceURL) { 
		super();

		this.application = application;
		this.serviceURL = serviceURL;
	}

	@Override
	public void runStep() throws Throwable {
		LocalUserClient client = new LocalUserClient(this.serviceURL);
		client.register(this.application);
	}
}
