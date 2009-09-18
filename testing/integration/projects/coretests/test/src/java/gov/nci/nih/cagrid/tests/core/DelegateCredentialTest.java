package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.DorianAddTrustedCAStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianApproveRegistrationStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianAuthenticateStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianConfigureStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianDestroyDefaultProxyStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianSubmitRegistrationStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusDeployServiceStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusInstallSecurityDescriptorStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.steps.ProxyActiveStep;
import gov.nci.nih.cagrid.tests.core.steps.SleepStep;
import gov.nci.nih.cagrid.tests.core.steps.cds.CleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.cds.DelegateCredentialStep;
import gov.nci.nih.cagrid.tests.core.steps.cds.FindCredentialsDelegatedToClientStep;
import gov.nci.nih.cagrid.tests.core.steps.cds.FindMyDelegatedCredentialsStep;
import gov.nci.nih.cagrid.tests.core.steps.cds.GetDelegatedCredentialFailStep;
import gov.nci.nih.cagrid.tests.core.steps.cds.GetDelegatedCredentialStep;
import gov.nci.nih.cagrid.tests.core.steps.cds.SuspendDelegatedCredentialStep;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.common.Errors;
import org.cagrid.gaards.cds.common.ExpirationStatus;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.dorian.idp.CountryCode;
import org.cagrid.gaards.dorian.idp.StateCode;

public class DelegateCredentialTest extends Story {
	private GlobusHelper globus;
	private File dorianServiceDir;
	private File cdsServiceDir;
	private File caFile;

	private final static int SHORT_LIFETIME_SECONDS = 30;

	public DelegateCredentialTest() {
		super();
	}

	public String getName() {
		return "Credential Delegation Service (CDS) Story";
	}

	protected boolean storySetUp() throws Throwable {
		return true;
	}

	protected void storyTearDown() throws Throwable {
		this.caFile.delete();

		if (this.globus != null) {
			this.globus.stopGlobus();
			this.globus.cleanupTempGlobus();
		}
		new DorianDestroyDefaultProxyStep().runStep();
		new DorianCleanupStep().runStep();
		new CleanupStep().runStep();
	}

	protected Vector steps() {
		this.globus = new GlobusHelper(true);

		this.dorianServiceDir = new File(System.getProperty("dorian.dir", ".."
				+ File.separator + ".." + File.separator + ".."
				+ File.separator + "caGrid" + File.separator + "projects"
				+ File.separator + "dorian"));

		this.cdsServiceDir = new File(System.getProperty("cds.dir", ".."
				+ File.separator + ".." + File.separator + ".."
				+ File.separator + "caGrid" + File.separator + "projects"
				+ File.separator + "cds"));
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
			fail("Unable to get the Dorian URL:" + e.getMessage());
		}

		String cdsURL = null;
		try {
			cdsURL = this.globus.getServiceEPR(
					"cagrid/CredentialDelegationService").getAddress()
					.toString();
		} catch (MalformedURIException e) {
			e.printStackTrace();
			fail("Unable to get the CDS URL:" + e.getMessage());
		}

		// initialize
		steps.add(new GlobusCreateStep(this.globus));
		steps.add(new GlobusInstallSecurityDescriptorStep(this.globus));
		steps.add(new GlobusDeployServiceStep(this.globus,
				this.dorianServiceDir));
		steps.add(new GlobusDeployServiceStep(this.globus, this.cdsServiceDir));
		steps.add(new DorianConfigureStep(this.globus));
		steps.add(new GlobusStartStep(this.globus));

		// successful authenticate
		DorianAuthenticateStep admin = new DorianAuthenticateStep("dorian",
				Constants.DORIAN_ADMIN_PASSWORD, dorianURL, 12);
		steps.add(admin);
		steps.add(new DorianAddTrustedCAStep(this.caFile, dorianURL));
		steps.add(new DorianDestroyDefaultProxyStep());
		Application leonardoApp = getApplication("leonardo", "Leonardo",
				"Turtle");
		Application donatelloApp = getApplication("donatello", "Donatello",
				"Turtle");
		System.out.println(leonardoApp.getPassword());
		steps.add(new DorianSubmitRegistrationStep(leonardoApp, dorianURL));
		steps.add(new DorianSubmitRegistrationStep(donatelloApp, dorianURL));

		steps.add(new DorianApproveRegistrationStep(leonardoApp, dorianURL,
				admin));

		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin));

		DorianAuthenticateStep leonardo = new DorianAuthenticateStep(
				leonardoApp.getUserId(), leonardoApp.getPassword(), dorianURL);
		steps.add(leonardo);
		steps.add(new DorianDestroyDefaultProxyStep());

		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, leonardo));

		List<GridCredential> allowedParties = new ArrayList<GridCredential>();
		allowedParties.add(leonardo);
		ProxyLifetime lifetime = new ProxyLifetime();
		lifetime.setHours(4);
		DelegateCredentialStep delegateAdmin = new DelegateCredentialStep(
				cdsURL, admin, allowedParties, lifetime);
		steps.add(delegateAdmin);

		DelegationRecordFilter valid = new DelegationRecordFilter();
		valid.setExpirationStatus(ExpirationStatus.Valid);

		DelegationRecordFilter expired = new DelegationRecordFilter();
		expired.setExpirationStatus(ExpirationStatus.Expired);

		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin,
				delegateAdmin));
		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin, valid,
				delegateAdmin));
		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin, expired));

		GetDelegatedCredentialStep admin2 = new GetDelegatedCredentialStep(
				delegateAdmin, leonardo);
		steps.add(admin2);

		steps.add(new DorianApproveRegistrationStep(donatelloApp, dorianURL,
				admin2));

		DorianAuthenticateStep donatello = new DorianAuthenticateStep(
				donatelloApp.getUserId(), donatelloApp.getPassword(), dorianURL);
		steps.add(donatello);
		steps.add(new DorianDestroyDefaultProxyStep());
		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, donatello));
		steps.add(new GetDelegatedCredentialFailStep(delegateAdmin, donatello,
				Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL));

		// Test finding credentials delegated to a client
		List<DelegationIdentifierReference> clientExpected = new ArrayList<DelegationIdentifierReference>();
		clientExpected.add(delegateAdmin);
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, admin));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, leonardo,
				delegateAdmin));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, donatello));

		// /Now we want to disable the credential and test
		SuspendDelegatedCredentialStep suspendAdmin = new SuspendDelegatedCredentialStep(
				delegateAdmin, admin);
		steps.add(suspendAdmin);

		DelegationRecordFilter approved = new DelegationRecordFilter();
		approved.setDelegationStatus(DelegationStatus.Approved);

		DelegationRecordFilter suspended = new DelegationRecordFilter();
		suspended.setDelegationStatus(DelegationStatus.Suspended);

		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin, approved));
		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin, suspended,
				delegateAdmin));

		// Test finding credentials delegated to a client
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, admin));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, leonardo));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, donatello));

		ProxyLifetime delegationLifetime = new ProxyLifetime();
		delegationLifetime.setSeconds(SHORT_LIFETIME_SECONDS);
		ProxyLifetime delegatedCredentialsLifetime = new ProxyLifetime();
		delegatedCredentialsLifetime.setSeconds((SHORT_LIFETIME_SECONDS / 2));

		DelegateCredentialStep delegateAdminShort = new DelegateCredentialStep(
				cdsURL, admin, allowedParties, delegationLifetime,
				delegatedCredentialsLifetime);
		steps.add(delegateAdminShort);

		// Test finding credentials delegated to a client
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, admin));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, leonardo,
				delegateAdminShort));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, donatello));

		GetDelegatedCredentialStep adminShort = new GetDelegatedCredentialStep(
				delegateAdminShort, leonardo);
		steps.add(adminShort);

		steps.add(new ProxyActiveStep(adminShort, true));
		steps.add(new GetDelegatedCredentialFailStep(delegateAdminShort,
				donatello, Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL));
		long sleepTime = ((SHORT_LIFETIME_SECONDS / 2) * 1000) + 100;
		steps.add(new SleepStep(sleepTime));
		steps.add(new ProxyActiveStep(adminShort, false));
		steps.add(new SleepStep(sleepTime));
		steps.add(new GetDelegatedCredentialFailStep(delegateAdminShort,
				leonardo, "org.globus.wsrf.NoSuchResourceException"));
		List<DelegationIdentifierReference> expected = new ArrayList<DelegationIdentifierReference>();
		expected.add(delegateAdmin);
		expected.add(delegateAdminShort);
		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin, expected));
		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin, valid,
				delegateAdmin));
		steps.add(new FindMyDelegatedCredentialsStep(cdsURL, admin, expired,
				delegateAdminShort));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, admin));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, leonardo));
		steps.add(new FindCredentialsDelegatedToClientStep(cdsURL, donatello));
		return steps;
	}

	public Application getApplication(String userId, String firstName,
			String lastName) {
		Application a = new Application();
		a.setUserId(userId);
		a.setPassword(Constants.DORIAN_ADMIN_PASSWORD);
		a.setOrganization("XYZ Inc.");
		a.setFirstName(firstName);
		a.setLastName(lastName);
		a.setAddress("555 Dorian Street");
		a.setCity("Columbus");
		a.setState(StateCode.OH);
		a.setZipcode("43210");
		a.setCountry(CountryCode.US);
		a.setEmail(firstName + "." + lastName + "@xyxinc.com");
		a.setPhoneNumber("(555) 555-5555");
		return a;
	}

	@Override
	public String getDescription() {
		return "Credential Delegation Service (CDS) System Test";
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
		TestResult result = runner.doRun(new TestSuite(
				DelegateCredentialTest.class));
		System.exit(result.errorCount() + result.failureCount());
	}

}
