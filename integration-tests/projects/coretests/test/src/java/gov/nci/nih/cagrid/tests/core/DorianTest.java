/*
 * Created on Jul 24, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.DorianAddTrustedCAStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianApproveRegistrationStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianAuthenticateFailStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianAuthenticateStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianConfigureStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianDestroyDefaultProxyStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianSubmitRegistrationStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusDeployServiceStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusInstallSecurityDescriptorStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.idp.Application;

/**
 * This is an integration test that tests some of the major functionality of
 * Dorian. It syncs GTS and deploys the dorian service and an echo service to
 * globus. It authenticates the dorian user and then insures some authentication
 * failures. It then adds the dorian CA to the globus trusted CAs. An
 * application for a user account is submitted, approved, and the new user is
 * authenticated.
 * 
 * @testType integration
 * @steps GlobusCreateStep, GTSSyncOnceStep, GlobusDeployServiceStep,
 *        DorianConfigureStep, GlobusStartStep
 * @steps DorianAuthenticateStep, DorianDestroyDefaultProxyStep,
 *        DorianAuthenticateFailStep
 * @steps DorianAddTrustedCAStep, DorianSubmitRegistrationStep,
 *        DorianApproveRegistrationStep
 * @steps GlobusStopStep, GlobusCleanupStep
 * @steps DorianCleanupStep
 * @author Patrick McConnell
 */
public class DorianTest extends Story {
	private GlobusHelper globus;
	private File serviceDir;
	private File caFile;

	public DorianTest() {
		super();
	}

	@Override
	public String getName() {
		return "Dorian Story";
	}

	@Override
	protected boolean storySetUp() throws Throwable {
		return true;
	}

	@Override
	protected void storyTearDown() throws Throwable {
		this.caFile.delete();

		if (this.globus != null) {
			this.globus.stopGlobus();
			this.globus.cleanupTempGlobus();
		}
		new DorianDestroyDefaultProxyStep().runStep();
		new DorianCleanupStep().runStep();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Vector steps() {
		this.globus = new GlobusHelper(true);

		this.serviceDir = new File(System.getProperty("dorian.dir", ".."
				+ File.separator + ".." + File.separator + ".."
				+ File.separator + "caGrid" + File.separator + "projects"
				+ File.separator + "dorian"));
		this.caFile = new File(System.getProperty("user.home"), ".globus"
				+ File.separator + "certificates" + File.separator
				+ "DorianTest_ca.1");

		Vector steps = new Vector();

		String dorianURL = null;
		try {
			dorianURL = this.globus.getServiceEPR("cagrid/Dorian").getAddress()
					.toString();
		} catch (MalformedURIException e) {
			e.printStackTrace();
			fail("Unable to get dorian URL:" + e.getMessage());
		}

		// initialize
		steps.add(new GlobusCreateStep(this.globus));
		steps.add(new GlobusInstallSecurityDescriptorStep(this.globus));
		steps.add(new GlobusDeployServiceStep(this.globus, this.serviceDir));
		steps.add(new DorianConfigureStep(this.globus));
		steps.add(new GlobusStartStep(this.globus));

		// successful authenticate
		steps.add(new DorianAuthenticateStep("dorian",
				Constants.DORIAN_ADMIN_PASSWORD, dorianURL));
		steps.add(new DorianDestroyDefaultProxyStep());

		// failed authenticate
		steps.add(new DorianAuthenticateFailStep("junk", "junk", dorianURL));
		steps.add(new DorianAuthenticateFailStep("dorian", "junk", dorianURL));
		steps
				.add(new DorianAuthenticateFailStep("junk", "password",
						dorianURL));

		// add trusted ca
		steps.add(new DorianAuthenticateStep("dorian",
				Constants.DORIAN_ADMIN_PASSWORD, dorianURL));
		steps.add(new DorianAddTrustedCAStep(this.caFile, dorianURL));
		steps.add(new DorianDestroyDefaultProxyStep());

		// new users
		File[] files = new File("test", "resources" + File.separator
				+ "userApplications").listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".xml");
			}
		});
		for (File file : files) {
			try {
				Application application = (Application) Utils
						.deserializeDocument(file.toString(), Application.class);
				// submit registration
				steps.add(new DorianSubmitRegistrationStep(application,
						dorianURL));

				// approve registration
				DorianAuthenticateStep auth = new DorianAuthenticateStep(
						"dorian", Constants.DORIAN_ADMIN_PASSWORD, dorianURL);
				steps.add(auth);
				steps.add(new DorianApproveRegistrationStep(application,
						dorianURL, auth));
				steps.add(new DorianDestroyDefaultProxyStep());

				// check that we can authenticate
				steps.add(new DorianAuthenticateStep(application.getUserId(),
						application.getPassword(), dorianURL));
				steps.add(new DorianAuthenticateFailStep(application
						.getUserId(), application.getPassword().toUpperCase(),
						dorianURL));
				steps.add(new DorianDestroyDefaultProxyStep());
			} catch (Exception e) {
				throw new RuntimeException("unable add new user steps", e);
			}
		}

		return steps;
	}

	@Override
	public String getDescription() {
		return "DorianTest";
	}

	/**
	 * used to make sure that if we are going to use a junit testsuite to test
	 * this that the test suite will not error out looking for a single
	 * test......
	 */
	public void testDummy() throws Throwable {
	}

	/**
	 * Convenience method for running all the Steps in this Story.
	 */
	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(DorianTest.class));
		System.exit(result.errorCount() + result.failureCount());
	}

}
