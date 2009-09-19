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
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.authentication.test.AuthenticationProperties;
import org.cagrid.gaards.authentication.test.system.steps.AuthenticationStep;
import org.cagrid.gaards.authentication.test.system.steps.SuccessfullAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.ValidateSupportedAuthenticationProfilesStep;
import org.cagrid.gaards.dorian.federation.AutoApprovalPolicy;
import org.cagrid.gaards.dorian.federation.GridUserStatus;
import org.cagrid.gaards.dorian.federation.SAMLAttributeDescriptor;
import org.cagrid.gaards.dorian.federation.SAMLAuthenticationMethod;
import org.cagrid.gaards.dorian.federation.TrustedIdP;
import org.cagrid.gaards.dorian.federation.TrustedIdPStatus;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.test.system.steps.AddTrustedIdPStep;
import org.cagrid.gaards.dorian.test.system.steps.CleanupDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.ConfigureGlobusToTrustDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.CopyConfigurationStep;
import org.cagrid.gaards.dorian.test.system.steps.FindGridUserStep;
import org.cagrid.gaards.dorian.test.system.steps.GetAsserionSigningCertificateStep;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;
import org.cagrid.gaards.dorian.test.system.steps.InvalidGridCredentialRequest;
import org.cagrid.gaards.dorian.test.system.steps.SuccessfullGridCredentialRequest;
import org.cagrid.gaards.dorian.test.system.steps.UpdateTrustedIdPStatusStep;
import org.cagrid.gaards.dorian.test.system.steps.VerifyTrustedIdPMetadataStep;
import org.cagrid.gaards.dorian.test.system.steps.VerifyTrustedIdPStep;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.saml.encoding.SAMLConstants;


public class DorianRemoteIdentityProviderTest extends ServiceStoryBase {

    private File dorianConfiguration;
    private File dorianProperties;
    private File authenticationServiceConfiguration;
    private AuthenticationProperties authenticationProperties;
    private File authenticationTempService;
    private File dorianTempService;
    private ConfigureGlobusToTrustDorianStep trust;

    public DorianRemoteIdentityProviderTest() {
    	super();
    }

    public DorianRemoteIdentityProviderTest(ServiceContainer container, File dorianConfiguration,
        File dorianProperties, File authenticationServiceConfiguration) {
        super(container);
        this.dorianConfiguration = dorianConfiguration;
        this.dorianProperties = dorianProperties;
        this.authenticationServiceConfiguration = authenticationServiceConfiguration;
    }


    @Override
    public String getName() {
        return "Dorian Remote Identity Provider System Test";
    }


    public String getDescription() {
        return "Dorian Remote Identity Provider System Test";
    }


    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        try {
            steps.add(new UnpackContainerStep(getContainer()));
            steps.add(new CopyConfigurationStep(dorianTempService, this.dorianConfiguration, this.dorianProperties));
            steps.add(new org.cagrid.gaards.authentication.test.system.steps.CopyConfigurationStep(
                authenticationTempService, this.authenticationServiceConfiguration, this.authenticationProperties
                    .getPropertiesFile()));
            steps.add(new DeployServiceStep(getContainer(), this.dorianTempService.getAbsolutePath()));
            steps.add(new DeployServiceStep(getContainer(), this.authenticationTempService.getAbsolutePath()));
            trust = new ConfigureGlobusToTrustDorianStep(getContainer());
            steps.add(trust);

            steps.add(new StartContainerStep(getContainer()));

            GetAsserionSigningCertificateStep signingCertStep = new GetAsserionSigningCertificateStep(getContainer());
            steps.add(signingCertStep);

            String dorianURL = getContainer().getContainerBaseURI().toString() + "cagrid/Dorian";
            String asURL = getContainer().getContainerBaseURI().toString() + "cagrid/AuthenticationService";

            // Test Get supported authentication types
            Set<QName> dorianExpectedProfiles = new HashSet<QName>();
            dorianExpectedProfiles.add(AuthenticationProfile.BASIC_AUTHENTICATION);
            steps.add(new ValidateSupportedAuthenticationProfilesStep(dorianURL, dorianExpectedProfiles));

            SuccessfullAuthentication dorianSuccess = new SuccessfullAuthentication("dorian", "Mr.", "Administrator",
                "dorian@dorian.org", signingCertStep);

            // Test Successful authentication
            BasicAuthentication cred = new BasicAuthentication();
            cred.setUserId("dorian");
            cred.setPassword("DorianAdmin$1");
            AuthenticationStep adminAuth = new AuthenticationStep(dorianURL, dorianSuccess, cred);
            steps.add(adminAuth);

            // Get Admin's Grid Credentials

            GridCredentialRequestStep admin = new GridCredentialRequestStep(dorianURL, adminAuth,
                new SuccessfullGridCredentialRequest());
            steps.add(admin);

            Set<QName> asExpectedProfiles = new HashSet<QName>();
            asExpectedProfiles.add(AuthenticationProfile.BASIC_AUTHENTICATION);
            steps.add(new ValidateSupportedAuthenticationProfilesStep(asURL, asExpectedProfiles));

            SuccessfullAuthentication success = new SuccessfullAuthentication("jdoe", "John", "Doe", "jdoe@doe.com",
                authenticationProperties.getSigningCertificate());

            // Test Successful authentication
            BasicAuthentication asUser = new BasicAuthentication();
            asUser.setUserId("jdoe");
            asUser.setPassword("password");
            AuthenticationStep user = new AuthenticationStep(asURL, success, asUser);
            steps.add(user);

            // Test that the Dorian Idp is properly registered.

            VerifyTrustedIdPStep localIdP = new VerifyTrustedIdPStep(dorianURL, admin, "Dorian");
            localIdP.setDisplayName("Dorian");
            localIdP.setStatus(TrustedIdPStatus.Active);
            localIdP.setUserPolicyClass(AutoApprovalPolicy.class.getName());
            localIdP.setAuthenticationServiceURL(dorianURL);
            steps.add(localIdP);

            VerifyTrustedIdPMetadataStep localIdPMetadata = new VerifyTrustedIdPMetadataStep(dorianURL, "Dorian");
            localIdPMetadata.setDisplayName("Dorian");
            localIdPMetadata.setAuthenticationServiceURL(dorianURL);
            steps.add(localIdP);
            steps.add(localIdPMetadata);

            TrustedIdP idp = new TrustedIdP();
            idp.setName("OSU");
            idp.setDisplayName("Ohio State University");
            idp.setStatus(TrustedIdPStatus.Active);
            idp.setUserPolicyClass(AutoApprovalPolicy.class.getName());
            idp.setAuthenticationServiceURL(asURL);
            SAMLAttributeDescriptor uid = new SAMLAttributeDescriptor();
            uid.setNamespaceURI(SAMLConstants.UID_ATTRIBUTE_NAMESPACE);
            uid.setName(SAMLConstants.UID_ATTRIBUTE);
            idp.setUserIdAttributeDescriptor(uid);

            SAMLAttributeDescriptor firstName = new SAMLAttributeDescriptor();
            firstName.setNamespaceURI(SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE);
            firstName.setName(SAMLConstants.FIRST_NAME_ATTRIBUTE);
            idp.setFirstNameAttributeDescriptor(firstName);

            SAMLAttributeDescriptor lastName = new SAMLAttributeDescriptor();
            lastName.setNamespaceURI(SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE);
            lastName.setName(SAMLConstants.LAST_NAME_ATTRIBUTE);
            idp.setLastNameAttributeDescriptor(lastName);

            SAMLAttributeDescriptor email = new SAMLAttributeDescriptor();
            email.setNamespaceURI(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE);
            email.setName(SAMLConstants.EMAIL_ATTRIBUTE);
            idp.setEmailAttributeDescriptor(email);

            idp.setIdPCertificate(CertUtil.writeCertificate(authenticationProperties.getSigningCertificate()));
            SAMLAuthenticationMethod[] methods = new SAMLAuthenticationMethod[1];
            methods[0] = SAMLAuthenticationMethod.fromValue("urn:oasis:names:tc:SAML:1.0:am:unspecified");
            idp.setAuthenticationMethod(methods);

            steps.add(new AddTrustedIdPStep(dorianURL, admin, idp));

            VerifyTrustedIdPStep remoteIdP = new VerifyTrustedIdPStep(dorianURL, admin, idp.getName());
            remoteIdP.setDisplayName(idp.getDisplayName());
            remoteIdP.setStatus(TrustedIdPStatus.Active);
            remoteIdP.setUserPolicyClass(AutoApprovalPolicy.class.getName());
            remoteIdP.setAuthenticationServiceURL(asURL);
            remoteIdP.setAuthenticationServiceIdentity(idp.getAuthenticationServiceIdentity());
            steps.add(remoteIdP);

            VerifyTrustedIdPMetadataStep remoteIdPMetadata = new VerifyTrustedIdPMetadataStep(dorianURL, idp.getName());
            remoteIdPMetadata.setDisplayName(idp.getDisplayName());
            remoteIdPMetadata.setAuthenticationServiceURL(asURL);
            remoteIdPMetadata.setAuthenticationServiceIdentity(idp.getAuthenticationServiceIdentity());
            steps.add(remoteIdPMetadata);

            GridCredentialRequestStep remoteUser = new GridCredentialRequestStep(dorianURL, user,
                new SuccessfullGridCredentialRequest());
            steps.add(remoteUser);

            FindGridUserStep gridUser = new FindGridUserStep(dorianURL, admin, remoteUser);
            gridUser.setExpectedEmail(success.getExpectedEmail());
            gridUser.setExpectedFirstName(success.getExpectedFirstName());
            gridUser.setExpectedLastName(success.getExpectedLastName());
            gridUser.setExpectedLocalUserId(success.getExpectedUserId());
            gridUser.setExpectedStatus(GridUserStatus.Active);
            steps.add(gridUser);

 
            steps.add(new UpdateTrustedIdPStatusStep(dorianURL, admin, idp.getName(), TrustedIdPStatus.Suspended));

            VerifyTrustedIdPStep remoteIdP2 = new VerifyTrustedIdPStep(dorianURL, admin, idp.getName());
            remoteIdP2.setDisplayName(idp.getDisplayName());
            remoteIdP2.setStatus(TrustedIdPStatus.Suspended);
            remoteIdP2.setUserPolicyClass(AutoApprovalPolicy.class.getName());
            remoteIdP2.setAuthenticationServiceURL(asURL);
            remoteIdP2.setAuthenticationServiceIdentity(idp.getAuthenticationServiceIdentity());
            steps.add(remoteIdP2);
            
            steps.add(new VerifyTrustedIdPMetadataStep(dorianURL,idp.getName(),false));
            steps.add(new GridCredentialRequestStep(dorianURL, user, new InvalidGridCredentialRequest(
                "Access for your Identity Provider has been suspended!!!", PermissionDeniedFault.class)));

            steps.add(new UpdateTrustedIdPStatusStep(dorianURL, admin, idp.getName(), TrustedIdPStatus.Active));


            steps.add(new GridCredentialRequestStep(dorianURL, user, new SuccessfullGridCredentialRequest()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        this.authenticationProperties = new AuthenticationProperties();
        this.dorianTempService = new File("tmp/dorian");
        this.authenticationTempService = new File("tmp/authentication-service");
        File dorianLocation = new File("../../../caGrid/projects/dorian");
        CopyServiceStep copyService = new CopyServiceStep(dorianLocation, dorianTempService);
        copyService.runStep();

        File asLocation = new File("../../../caGrid/projects/authentication-service");
        CopyServiceStep copyService2 = new CopyServiceStep(asLocation, authenticationTempService);
        copyService2.runStep();
        return true;
    }


    protected void storyTearDown() throws Throwable {

        try {
            this.authenticationProperties.cleanup();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (this.authenticationTempService != null) {
                new DeleteServiceStep(authenticationTempService).runStep();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (this.dorianTempService != null) {
                new DeleteServiceStep(dorianTempService).runStep();
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
