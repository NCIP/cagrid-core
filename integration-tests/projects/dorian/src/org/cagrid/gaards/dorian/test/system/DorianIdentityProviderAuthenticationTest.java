package org.cagrid.gaards.dorian.test.system;

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
import org.cagrid.gaards.authentication.test.system.steps.AuthenticationStep;
import org.cagrid.gaards.authentication.test.system.steps.DeprecatedAuthenticationStep;
import org.cagrid.gaards.authentication.test.system.steps.InvalidAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.SuccessfullAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.ValidateSupportedAuthenticationProfilesStep;
import org.cagrid.gaards.dorian.test.system.steps.CleanupDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.ConfigureGlobusToTrustDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.CopyConfigurationStep;
import org.cagrid.gaards.dorian.test.system.steps.GetAsserionSigningCertificateStep;
import org.cagrid.gaards.dorian.test.system.steps.SleepStep;


public class DorianIdentityProviderAuthenticationTest extends ServiceStoryBase {

    private File configuration;
    private File properties;
    private File tempService;
    private ConfigureGlobusToTrustDorianStep trust;


    public DorianIdentityProviderAuthenticationTest() {
    	super();
    }
    
    
    public DorianIdentityProviderAuthenticationTest(ServiceContainer container) {
        this(container, null, null);
    }


    public DorianIdentityProviderAuthenticationTest(ServiceContainer container, File properties) {
        this(container, null, properties);
    }


    public DorianIdentityProviderAuthenticationTest(ServiceContainer container, File configuration, File properties) {
        super(container);
        this.configuration = configuration;
        this.properties = properties;
    }


    @Override
    public String getName() {
        return "Dorian Authentication System Test";
    }


    public String getDescription() {
        return "Dorian Authentication System Test";
    }


    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        try {
            steps.add(new UnpackContainerStep(getContainer()));
            steps.add(new CopyConfigurationStep(tempService, this.configuration, this.properties));

            steps.add(new DeployServiceStep(getContainer(), this.tempService.getAbsolutePath()));

            trust = new ConfigureGlobusToTrustDorianStep(getContainer());
            steps.add(trust);

            steps.add(new StartContainerStep(getContainer()));

            GetAsserionSigningCertificateStep signingCertStep = new GetAsserionSigningCertificateStep(getContainer());
            steps.add(signingCertStep);

            String serviceURL = getContainer().getContainerBaseURI().toString() + "cagrid/Dorian";

            // Test Get supported authentication types

            Set<QName> expectedProfiles = new HashSet<QName>();
            expectedProfiles.add(AuthenticationProfile.BASIC_AUTHENTICATION);
            steps.add(new ValidateSupportedAuthenticationProfilesStep(serviceURL, expectedProfiles));

            SuccessfullAuthentication success = new SuccessfullAuthentication("dorian", "Mr.", "Administrator",
                "dorian@dorian.org", signingCertStep);

            // Test Successful authentication
            BasicAuthentication cred = new BasicAuthentication();
            cred.setUserId("dorian");
            cred.setPassword("DorianAdmin$1");
            steps.add(new AuthenticationStep(serviceURL, success, cred));

            // Test successful deprecated authentication

            Credential cred2 = new Credential();
            BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
            bac.setUserId("dorian");
            bac.setPassword("DorianAdmin$1");
            cred2.setBasicAuthenticationCredential(bac);

            steps.add(new DeprecatedAuthenticationStep(serviceURL, success, cred2));

            // Test invalid authentication, bad password
            BasicAuthentication cred3 = new BasicAuthentication();
            cred3.setUserId("dorian");
            cred3.setPassword("badpassword");
            steps.add(new AuthenticationStep(serviceURL, new InvalidAuthentication("The uid or password is incorrect.",
                InvalidCredentialFault.class), cred3));

            // Test invalid deprecated authentication, bad password

            Credential cred4 = new Credential();
            BasicAuthenticationCredential bac2 = new BasicAuthenticationCredential();
            bac2.setUserId("dorian");
            bac2.setPassword("badpassword");
            cred4.setBasicAuthenticationCredential(bac2);
            steps.add(new DeprecatedAuthenticationStep(serviceURL, new InvalidAuthentication(
                "The uid or password is incorrect.",
                gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault.class), cred4));

            // Test password lockout
            // One more lockout to get to three

            steps.add(new AuthenticationStep(serviceURL, new InvalidAuthentication("The uid or password is incorrect.",
                InvalidCredentialFault.class), cred3));

            // Now it should be locked

            steps
                .add(new AuthenticationStep(
                    serviceURL,
                    new InvalidAuthentication(
                        "This account has been temporarily locked because the maximum number of consecutive invalid logins has been exceeded.",

                        InvalidCredentialFault.class), cred));

            // Sleep till lock expires
            steps.add(new SleepStep(60));

            // Now should be unlocked

            steps.add(new AuthenticationStep(serviceURL, success, cred));

            // Test invalid authentication, unsupported credential
            OneTimePassword cred5 = new OneTimePassword();
            cred5.setUserId("dorian");
            cred5.setOneTimePassword("oneTimePassword");

            steps.add(new AuthenticationStep(serviceURL, new InvalidAuthentication(
                "The credential provided is not supported.", CredentialNotSupportedFault.class), cred5));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        this.tempService = new File("tmp/dorian");
        File asLocation = new File("../../../caGrid/projects/dorian");
        CopyServiceStep copyService = new CopyServiceStep(asLocation, tempService);
        copyService.runStep();
        // this.tempService = copyService.getServiceDirectory();
        return true;
    }


    protected void storyTearDown() throws Throwable {
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

        CleanupDorianStep cleanup = new CleanupDorianStep(getContainer(), trust);
        try {
            cleanup.runStep();
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
