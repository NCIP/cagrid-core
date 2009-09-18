package org.cagrid.gaards.dorian.test.system;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.authentication.test.system.steps.AuthenticationStep;
import org.cagrid.gaards.authentication.test.system.steps.InvalidAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.SuccessfullAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.ValidateSupportedAuthenticationProfilesStep;
import org.cagrid.gaards.dorian.federation.AutoApprovalPolicy;
import org.cagrid.gaards.dorian.federation.GridUserStatus;
import org.cagrid.gaards.dorian.federation.TrustedIdPStatus;
import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.dorian.idp.CountryCode;
import org.cagrid.gaards.dorian.idp.LocalUserRole;
import org.cagrid.gaards.dorian.idp.LocalUserStatus;
import org.cagrid.gaards.dorian.idp.StateCode;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.test.system.steps.ChangeLocalUserPasswordStep;
import org.cagrid.gaards.dorian.test.system.steps.CleanupDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.ConfigureGlobusToTrustDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.CopyConfigurationStep;
import org.cagrid.gaards.dorian.test.system.steps.FindGridUserStep;
import org.cagrid.gaards.dorian.test.system.steps.FindLocalUserStep;
import org.cagrid.gaards.dorian.test.system.steps.GetAsserionSigningCertificateStep;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;
import org.cagrid.gaards.dorian.test.system.steps.InvalidGridCredentialRequest;
import org.cagrid.gaards.dorian.test.system.steps.RegisterUserWithDorianIdentityProviderStep;
import org.cagrid.gaards.dorian.test.system.steps.SuccessfullGridCredentialRequest;
import org.cagrid.gaards.dorian.test.system.steps.UpdateGridUserStatusStep;
import org.cagrid.gaards.dorian.test.system.steps.UpdateLocalUserStatusStep;
import org.cagrid.gaards.dorian.test.system.steps.VerifyTrustedIdPStep;


public class DorianLocaIdentityProviderTest extends ServiceStoryBase {

    private File configuration;
    private File properties;
    private File tempService;
    private ConfigureGlobusToTrustDorianStep trust;

	public DorianLocaIdentityProviderTest() {
		super();
	}

    public DorianLocaIdentityProviderTest(ServiceContainer container) {
        this(container, null, null);
    }


    public DorianLocaIdentityProviderTest(ServiceContainer container, File properties) {
        this(container, null, properties);
    }


    public DorianLocaIdentityProviderTest(ServiceContainer container, File configuration, File properties) {
        super(container);
        this.configuration = configuration;
        this.properties = properties;
    }


    @Override
    public String getName() {
        return "Dorian Local Identity Provider System Test";
    }


    public String getDescription() {
        return "Dorian Local Identity Provider System Test";
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
            AuthenticationStep adminAuth = new AuthenticationStep(serviceURL, success, cred);
            steps.add(adminAuth);

            // Get Admin's Grid Credentials

            GridCredentialRequestStep admin = new GridCredentialRequestStep(serviceURL, adminAuth,
                new SuccessfullGridCredentialRequest());
            steps.add(admin);

            // Test that the Dorian Idp is properly registered.

            VerifyTrustedIdPStep idp = new VerifyTrustedIdPStep(serviceURL, admin, "Dorian");
            idp.setDisplayName("Dorian");
            idp.setStatus(TrustedIdPStatus.Active);
            idp.setUserPolicyClass(AutoApprovalPolicy.class.getName());
            idp.setAuthenticationServiceURL(serviceURL);
            steps.add(idp);

            // Create Users
            List<Application> users = new ArrayList<Application>();
            for (int i = 0; i < 3; i++) {
                Application a = new Application();
                a.setUserId("jdoe" + i);
                a.setPassword("K00lM0N$$" + i);
                a.setFirstName("John" + i);
                a.setLastName("Doe" + i);
                a.setEmail(a.getUserId() + "@cagrid.org");
                a.setOrganization("cagrid.org");
                a.setAddress("123" + i + " Grid Way");
                a.setCity("Columbus");
                a.setState(StateCode.OH);
                a.setCountry(CountryCode.US);
                a.setZipcode("43210");
                a.setPhoneNumber("(555) 555-555" + i);
                users.add(a);
                steps.add(new RegisterUserWithDorianIdentityProviderStep(serviceURL, a));
            }

            // Test that the user accounts were create correctly
            for (int i = 0; i < 3; i++) {
                steps.add(new FindLocalUserStep(serviceURL, admin, users.get(i), LocalUserStatus.Pending,
                    LocalUserRole.Non_Administrator));
            }

            // Test that the users cannot authenticate until they are approved.
            for (int i = 0; i < users.size(); i++) {
                BasicAuthentication auth = new BasicAuthentication();
                auth.setUserId(users.get(i).getUserId());
                auth.setPassword(users.get(i).getPassword());
                steps
                    .add(new AuthenticationStep(serviceURL, new InvalidAuthentication(
                        "The application for this account has not yet been reviewed.", InvalidCredentialFault.class),
                        auth));
            }

            // Approve the user accounts

            for (int i = 0; i < users.size(); i++) {
                steps.add(new UpdateLocalUserStatusStep(serviceURL, admin, users.get(i).getUserId(),
                    LocalUserStatus.Active));
            }

            // Test that the user accounts were approved correctly
            for (int i = 0; i < users.size(); i++) {
                steps.add(new FindLocalUserStep(serviceURL, admin, users.get(i), LocalUserStatus.Active,
                    LocalUserRole.Non_Administrator));
            }

            // Test successful Authentication
            List<GridCredentialRequestStep> userCredentials = new ArrayList<GridCredentialRequestStep>();

            for (int i = 0; i < users.size(); i++) {
                SuccessfullAuthentication sa = new SuccessfullAuthentication(users.get(i).getUserId(), users.get(i)
                    .getFirstName(), users.get(i).getLastName(), users.get(i).getEmail(), signingCertStep);

                // Test Successful authentication
                BasicAuthentication ba = new BasicAuthentication();
                ba.setUserId(users.get(i).getUserId());
                ba.setPassword(users.get(i).getPassword());
                AuthenticationStep userAuth = new AuthenticationStep(serviceURL, sa, ba);
                steps.add(userAuth);
                GridCredentialRequestStep proxy = new GridCredentialRequestStep(serviceURL, userAuth,
                    new SuccessfullGridCredentialRequest());
                steps.add(proxy);
                userCredentials.add(proxy);

                FindGridUserStep gridUser = new FindGridUserStep(serviceURL, admin, proxy);
                gridUser.setExpectedEmail(users.get(i).getEmail());
                gridUser.setExpectedFirstName(users.get(i).getFirstName());
                gridUser.setExpectedLastName(users.get(i).getLastName());
                gridUser.setExpectedLocalUserId(users.get(i).getUserId());
                gridUser.setExpectedStatus(GridUserStatus.Active);
                steps.add(gridUser);
            }

            // Test Suspending Accounts Locally

            for (int i = 0; i < users.size(); i++) {
                steps.add(new UpdateLocalUserStatusStep(serviceURL, admin, users.get(i).getUserId(),
                    LocalUserStatus.Suspended));
                steps.add(new FindLocalUserStep(serviceURL, admin, users.get(i), LocalUserStatus.Suspended,
                    LocalUserRole.Non_Administrator));
                BasicAuthentication auth = new BasicAuthentication();
                auth.setUserId(users.get(i).getUserId());
                auth.setPassword(users.get(i).getPassword());
                steps.add(new AuthenticationStep(serviceURL, new InvalidAuthentication(
                    "The account has been suspended.", InvalidCredentialFault.class), auth));
                steps.add(new UpdateLocalUserStatusStep(serviceURL, admin, users.get(i).getUserId(),
                    LocalUserStatus.Active));
                steps.add(new FindLocalUserStep(serviceURL, admin, users.get(i), LocalUserStatus.Active,
                    LocalUserRole.Non_Administrator));
            }

            // Test suspending grid accounts

            for (int i = 0; i < users.size(); i++) {
                steps.add(new UpdateGridUserStatusStep(serviceURL, admin, userCredentials.get(i),
                    GridUserStatus.Suspended));

                FindGridUserStep gridUser = new FindGridUserStep(serviceURL, admin, userCredentials.get(i));
                gridUser.setExpectedEmail(users.get(i).getEmail());
                gridUser.setExpectedFirstName(users.get(i).getFirstName());
                gridUser.setExpectedLastName(users.get(i).getLastName());
                gridUser.setExpectedLocalUserId(users.get(i).getUserId());
                gridUser.setExpectedStatus(GridUserStatus.Suspended);
                steps.add(gridUser);

                SuccessfullAuthentication sa = new SuccessfullAuthentication(users.get(i).getUserId(), users.get(i)
                    .getFirstName(), users.get(i).getLastName(), users.get(i).getEmail(), signingCertStep);
                BasicAuthentication ba = new BasicAuthentication();
                ba.setUserId(users.get(i).getUserId());
                ba.setPassword(users.get(i).getPassword());
                AuthenticationStep userAuth = new AuthenticationStep(serviceURL, sa, ba);
                steps.add(userAuth);
                GridCredentialRequestStep proxy = new GridCredentialRequestStep(serviceURL, userAuth,
                    new InvalidGridCredentialRequest("The account has been suspended.", PermissionDeniedFault.class));
                steps.add(proxy);

                steps
                    .add(new UpdateGridUserStatusStep(serviceURL, admin, userCredentials.get(i), GridUserStatus.Active));

                FindGridUserStep gridUser2 = new FindGridUserStep(serviceURL, admin, userCredentials.get(i));
                gridUser2.setExpectedEmail(users.get(i).getEmail());
                gridUser2.setExpectedFirstName(users.get(i).getFirstName());
                gridUser2.setExpectedLastName(users.get(i).getLastName());
                gridUser2.setExpectedLocalUserId(users.get(i).getUserId());
                gridUser2.setExpectedStatus(GridUserStatus.Active);
                steps.add(gridUser2);
            }

            // Test that the user can change there password

            for (int i = 0; i < users.size(); i++) {
                String newPassword = "K00lM0N##" + i;
                steps.add(new ChangeLocalUserPasswordStep(serviceURL, users.get(i), newPassword));
            }

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
