package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.GridCredential;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class ProxyActiveStep extends Step {
	private GridCredential credential;
	private boolean active;

	public ProxyActiveStep(GridCredential credential, boolean active) {
		this.credential = credential;
		this.active = active;
	}

	@Override
	public void runStep() throws Throwable {
		assertNotNull(this.credential);
		assertNotNull(this.credential.getCredential());
		if (active) {
			assertTrue(this.credential.getCredential().getTimeLeft() > 0);
		} else {
			assertEquals(0, this.credential.getCredential().getTimeLeft());
		}

	}
}
