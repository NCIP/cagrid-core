/*
 * Created on Jul 24, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;

/**
 * This step attempts to authenticate to dorian and insures that the
 * authentication failed due to a permission denied fault.
 * 
 * @author Patrick McConnell
 */
public class DorianAuthenticateFailStep extends DorianAuthenticateStep {
	public DorianAuthenticateFailStep(String serviceURL) {
		super(serviceURL);
	}

	public DorianAuthenticateFailStep(String userId, String password,
			String serviceURL) {
		super(userId, password, serviceURL, 12);
	}

	public DorianAuthenticateFailStep(String userId, String password,
			String serviceURL, int hours, int delegationPathLength) {
		super(userId, password, serviceURL, hours);
	}

	@Override
	public void runStep() throws Throwable {
		Exception exception = null;
		try {
			super.runStep();
		} catch (Exception e) {
			exception = e;
		}
		assertNotNull(exception);
		assertTrue(exception instanceof InvalidCredentialFault);
	}
}
