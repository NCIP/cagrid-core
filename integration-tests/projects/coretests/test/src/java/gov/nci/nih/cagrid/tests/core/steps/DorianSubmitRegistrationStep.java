/*
 * Created on Jul 14, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

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
