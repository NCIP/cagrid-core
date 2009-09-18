package org.cagrid.gaards.authentication.test.system;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeleteServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.OneTimePassword;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.authentication.test.AuthenticationProperties;
import org.cagrid.gaards.authentication.test.system.steps.AuthenticationStep;
import org.cagrid.gaards.authentication.test.system.steps.CopyConfigurationStep;
import org.cagrid.gaards.authentication.test.system.steps.DeprecatedAuthenticationStep;
import org.cagrid.gaards.authentication.test.system.steps.InvalidAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.SuccessfullAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.ValidateSupportedAuthenticationProfilesStep;

public class AuthenticationServiceTest extends ServiceStoryBase {

	private File configuration;
	private AuthenticationProperties properties;
	private File tempService;

	public AuthenticationServiceTest(ServiceContainer container,
			File configuration) {
		super(container);
		this.configuration = configuration;
	}
	
	

	@Override
	public String getName() {
		return "Authentication Service System Test";
	}



	public String getDescription() {
		return "Authentication Service Test";
	}

	protected Vector<Step> steps() {
		Vector<Step> steps = new Vector<Step>();
		try {
			steps.add(new UnpackContainerStep(getContainer()));
			steps.add(new CopyConfigurationStep(tempService,
					this.configuration, this.properties.getPropertiesFile()));

			steps.add(new DeployServiceStep(getContainer(), this.tempService
					.getAbsolutePath()));
			steps.add(new StartContainerStep(getContainer()));
			String serviceURL = getContainer().getContainerBaseURI().toString()
			+ "cagrid/AuthenticationService";
			// Test Get supported authentication types

			Set<QName> expectedProfiles = new HashSet<QName>();
			expectedProfiles.add(AuthenticationProfile.BASIC_AUTHENTICATION);
			steps.add(new ValidateSupportedAuthenticationProfilesStep(
					serviceURL, expectedProfiles));

			SuccessfullAuthentication success = new SuccessfullAuthentication(
					"jdoe", "John", "Doe", "jdoe@doe.com", properties
							.getSigningCertificate());

			// Test Successful authentication
			BasicAuthentication cred = new BasicAuthentication();
			cred.setUserId("jdoe");
			cred.setPassword("password");
			steps.add(new AuthenticationStep(serviceURL, success, cred));

			// Test successful deprecated authentication

			Credential cred2 = new Credential();
			BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
			bac.setUserId("jdoe");
			bac.setPassword("password");
			cred2.setBasicAuthenticationCredential(bac);

			steps.add(new DeprecatedAuthenticationStep(serviceURL, success,
					cred2));

			// Test invalid authentication, bad password
			BasicAuthentication cred3 = new BasicAuthentication();
			cred3.setUserId("jdoe");
			cred3.setPassword("badpassword");
			steps.add(new AuthenticationStep(serviceURL,
					new InvalidAuthentication("Invalid password specified!!!",
							InvalidCredentialFault.class), cred3));

			// Test invalid deprecated authentication, bad password

			Credential cred4 = new Credential();
			BasicAuthenticationCredential bac2 = new BasicAuthenticationCredential();
			bac2.setUserId("jdoe");
			bac2.setPassword("badpassword");
			cred4.setBasicAuthenticationCredential(bac2);
			steps
					.add(new DeprecatedAuthenticationStep(
							serviceURL,
							new InvalidAuthentication(
									"Invalid password specified!!!",
									gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault.class),
							cred4));

			// Test invalid authentication, unsupported credential
			OneTimePassword cred5 = new OneTimePassword();
			cred5.setUserId("jdoe");
			cred5.setOneTimePassword("oneTimePassword");
			steps
					.add(new AuthenticationStep(
							serviceURL,
							new InvalidAuthentication(
									"The credential provided is not accepted by this service.",
									CredentialNotSupportedFault.class), cred5));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return steps;
	}

	protected boolean storySetUp() throws Throwable {
		this.properties = new AuthenticationProperties();
		this.tempService = new File("tmp/authentication-service");
		File asLocation = new File(
				"../../../caGrid/projects/authentication-service");
		CopyServiceStep copyService = new CopyServiceStep(asLocation,
				tempService);
		copyService.runStep();
		// this.tempService = copyService.getServiceDirectory();
		return true;
	}

	protected void storyTearDown() throws Throwable {

		try {
			this.properties.cleanup();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			if (this.tempService != null) {
				new DeleteServiceStep(tempService).runStep();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

		StopContainerStep step2 = new StopContainerStep(getContainer());
		try {
			step2.runStep();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		DestroyContainerStep step3 = new DestroyContainerStep(getContainer());
		try {
			step3.runStep();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
