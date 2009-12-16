package org.cagrid.cds.test;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeleteServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import javax.xml.namespace.QName;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.cagrid.cds.test.steps.CopyCdsConfigurationStep;
import org.cagrid.cds.test.steps.CleanupCdsStep;
import org.cagrid.cds.test.steps.DelegateCredentialStep;
import org.cagrid.cds.test.steps.DorianAddTrustedCAStep;
import org.cagrid.cds.test.steps.DorianApproveRegistrationStep;
import org.cagrid.cds.test.steps.DorianAuthenticateStep;
import org.cagrid.cds.test.steps.DorianDestroyDefaultProxyStep;
import org.cagrid.cds.test.steps.DorianSubmitRegistrationStep;
import org.cagrid.cds.test.steps.FindCredentialsDelegatedToClientStep;
import org.cagrid.cds.test.steps.FindMyDelegatedCredentialsStep;
import org.cagrid.cds.test.steps.GetDelegatedCredentialFailStep;
import org.cagrid.cds.test.steps.GetDelegatedCredentialStep;
import org.cagrid.cds.test.steps.ProxyActiveStep;
import org.cagrid.cds.test.steps.SleepStep;
import org.cagrid.cds.test.steps.SuspendDelegatedCredentialStep;
import org.cagrid.cds.test.util.Constants;
import org.cagrid.cds.test.util.DelegationIdentifierReference;
import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.authentication.test.system.steps.AuthenticationStep;
import org.cagrid.gaards.authentication.test.system.steps.SuccessfullAuthentication;
import org.cagrid.gaards.authentication.test.system.steps.ValidateSupportedAuthenticationProfilesStep;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.common.Errors;
import org.cagrid.gaards.cds.common.ExpirationStatus;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.dorian.idp.CountryCode;
import org.cagrid.gaards.dorian.idp.LocalUserRole;
import org.cagrid.gaards.dorian.idp.LocalUserStatus;
import org.cagrid.gaards.dorian.idp.StateCode;
import org.cagrid.gaards.dorian.test.system.steps.CleanupDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.ConfigureGlobusToTrustDorianStep;
import org.cagrid.gaards.dorian.test.system.steps.CopyConfigurationStep;
import org.cagrid.gaards.dorian.test.system.steps.FindLocalUserStep;
import org.cagrid.gaards.dorian.test.system.steps.GetAsserionSigningCertificateStep;
import org.cagrid.gaards.dorian.test.system.steps.GridCredentialRequestStep;
import org.cagrid.gaards.dorian.test.system.steps.SuccessfullGridCredentialRequest;
import org.cagrid.gaards.dorian.test.system.steps.UpdateLocalUserStatusStep;

public class DelegateCredentialTest extends ServiceStoryBase {
	private File dorianServiceDir;
	private File cdsServiceDir;
	private File caFile;
	private ServiceContainer dorianContainer;
	private ServiceContainer cdsContainer;
	private ConfigureGlobusToTrustDorianStep trust;
	public static File DORIAN_PROPERTIES_FILE = new File("../dorian/resources/dorian.properties");
	
	private final static int SHORT_LIFETIME_SECONDS = 30;

	public DelegateCredentialTest() {
		super();
	}
	
	public DelegateCredentialTest(ServiceContainer container) {
		super(container);
		this.cdsContainer = container;
	}

	public String getName() {
		return "Credential Delegation Service (CDS) Story";
	}

	protected boolean storySetUp() throws Throwable {
		
		System.out.println("Setting up CDS Test...");
		
		// copy Dorian Service
		System.out.println("Copy Dorian Service");
		this.dorianServiceDir = new File("tmp/dorian");
        File dorianLocation = new File("../../../caGrid/projects/dorian");
        CopyServiceStep copyDorianService = new CopyServiceStep(dorianLocation, dorianServiceDir);
        copyDorianService.runStep();
		
        
        
		// copy CDS service
        System.out.println("Copy CDS Service");
		this.cdsServiceDir = new File("tmp/cds");
        File cdsLocation = new File("../../../caGrid/projects/cds");
        CopyServiceStep copyCDSService = new CopyServiceStep(cdsLocation, cdsServiceDir);
        copyCDSService.runStep();
        
        System.out.println("Setting up CDS Test complete.");
        
		return true;
	}

	protected void storyTearDown() throws Throwable {

		//Dorian
		StopContainerStep stopDorian = new StopContainerStep(this.dorianContainer);
        try {
        	stopDorian.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        CleanupDorianStep cleanupDorian = new CleanupDorianStep(this.dorianContainer, trust);
        try {
        	cleanupDorian.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        DestroyContainerStep destroyDorianContainer = new DestroyContainerStep(this.dorianContainer);
        try {
        	destroyDorianContainer.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        DeleteServiceStep destroyDorianService = new DeleteServiceStep (this.dorianServiceDir);
        try {
        	destroyDorianService.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        //CDS
        StopContainerStep stopCDS = new StopContainerStep(this.cdsContainer);
        try {
        	stopCDS.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        CleanupCdsStep dropCdsDB = new CleanupCdsStep(this.cdsContainer);
        try {
        	dropCdsDB.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        DestroyContainerStep destroyCDSContainer = new DestroyContainerStep(this.cdsContainer);
        try {
        	destroyCDSContainer.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
        DeleteServiceStep destroyCdsService = new DeleteServiceStep (this.cdsServiceDir);
        try {
        	destroyCdsService.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }

	}

	protected Vector steps() {
		Vector<Step> steps = new Vector<Step>();
		try {
			// setup Dorian container
			dorianContainer = ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER);
			steps.add(new UnpackContainerStep(dorianContainer));
			List<String> args = Arrays.asList(new String[] {
		            "-Dno.deployment.validation=true", "-Dperform.index.service.registration=false"});
			// copy configuration and properties to service dir
	        steps.add(new CopyConfigurationStep(this.dorianServiceDir, null, DORIAN_PROPERTIES_FILE));
	        steps.add(new DeployServiceStep(dorianContainer, this.dorianServiceDir.getAbsolutePath(), args));
	        trust = new ConfigureGlobusToTrustDorianStep(dorianContainer);
	        steps.add(trust);
			
	        //setup CDS container
			if (this.cdsContainer == null) {
				cdsContainer = ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER);
			}
			steps.add(new UnpackContainerStep(cdsContainer));
	        steps.add(new DeployServiceStep(cdsContainer, this.cdsServiceDir.getAbsolutePath(), args));
	        
	        // Start Dorian container, get URL
	        steps.add(new StartContainerStep(dorianContainer));
	        String dorianServiceURL = this.dorianContainer.getServiceEPR("cagrid/Dorian").getAddress().toString();
	        
	        //Copy Dorian CA File into CDS 
	        this.caFile = new File(cdsContainer.getProperties().getContainerDirectory()
					+ File.separator + "certificates" + File.separator + "ca" + File.separator
					+ "DorianTest_ca.1");
	        steps.add(new DorianAddTrustedCAStep(this.caFile, dorianServiceURL));
	        
	        // Start CDS container
	        steps.add(new StartContainerStep(cdsContainer));
	        String cdsServiceURL = this.cdsContainer.getServiceEPR("cagrid/CredentialDelegationService").getAddress().toString();
	        
	        //
	        GetAsserionSigningCertificateStep signingCertStep = new GetAsserionSigningCertificateStep(this.dorianContainer);
            steps.add(signingCertStep);
			
			// Test Get supported authentication types
            Set<QName> expectedProfiles = new HashSet<QName>();
            expectedProfiles.add(AuthenticationProfile.BASIC_AUTHENTICATION);
            steps.add(new ValidateSupportedAuthenticationProfilesStep(dorianServiceURL, expectedProfiles));

            //Authenticate Dorian Admin
            SuccessfullAuthentication adminSuccess = new SuccessfullAuthentication("dorian", "Mr.", "Administrator",
                    "dorian@dorian.org", signingCertStep);
            BasicAuthentication adminCred = new BasicAuthentication(Constants.DORIAN_ADMIN_PASSWORD, "dorian");
            AuthenticationStep adminAuth = new AuthenticationStep(dorianServiceURL, adminSuccess, adminCred);
            steps.add(adminAuth);
			
            // Get Admin's Grid Credentials
            GridCredentialRequestStep admin = new GridCredentialRequestStep(dorianServiceURL, adminAuth,
                new SuccessfullGridCredentialRequest());
            steps.add(admin);
			
			// find delegated credentials for admin
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin));	
	       
			// Register, verify and approve "Leonardo" as non-administrator
			Application leonardoApp = getApplication("leonardo", "Leonardo", "Turtle");
			steps.add(new DorianSubmitRegistrationStep(leonardoApp, dorianServiceURL));
			steps.add(new FindLocalUserStep(dorianServiceURL, admin, leonardoApp, LocalUserStatus.Pending,
                    LocalUserRole.Non_Administrator));
			steps.add(new UpdateLocalUserStatusStep(dorianServiceURL, admin, leonardoApp.getUserId(),
                    LocalUserStatus.Active));	
	       
			// Authenticate leonardo
			SuccessfullAuthentication leoSuccess = new SuccessfullAuthentication(leonardoApp.getUserId(), 
					leonardoApp.getFirstName(), leonardoApp.getLastName(), leonardoApp.getEmail(),
                    signingCertStep);
			BasicAuthentication leoCred = new BasicAuthentication(leonardoApp.getPassword(), leonardoApp.getUserId());
            AuthenticationStep leoAuth = new AuthenticationStep(dorianServiceURL, leoSuccess, leoCred);
            steps.add(leoAuth);
            steps.add(new DorianDestroyDefaultProxyStep());
            
			// Get Leonardo's Grid Credentials
            GridCredentialRequestStep leoCredRequest = new GridCredentialRequestStep(dorianServiceURL, leoAuth,
                new SuccessfullGridCredentialRequest());
            steps.add(leoCredRequest);
	
			// Get Leonardo's delegated credentials
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, leoCredRequest));	
			
			// Delegate admin credential to Leonardo
			List<GridCredentialRequestStep> allowedParties = new ArrayList<GridCredentialRequestStep>();
			allowedParties.add(leoCredRequest);
			ProxyLifetime lifetime = new ProxyLifetime();
			lifetime.setHours(4);
			DelegateCredentialStep delegateAdmin = new DelegateCredentialStep(
					cdsServiceURL, admin, allowedParties, lifetime);
			steps.add(delegateAdmin);
        
			// Create filter for Valid delegations
			DelegationRecordFilter valid = new DelegationRecordFilter();
			valid.setExpirationStatus(ExpirationStatus.Valid);
			
			DelegationRecordFilter expired = new DelegationRecordFilter();
			expired.setExpirationStatus(ExpirationStatus.Expired);			
			
			DelegationRecordFilter approved = new DelegationRecordFilter();
			approved.setDelegationStatus(DelegationStatus.Approved);
	
			DelegationRecordFilter suspended = new DelegationRecordFilter();
			suspended.setDelegationStatus(DelegationStatus.Suspended);
			
			// Find delegated credentials
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin,
					delegateAdmin));
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, valid,
					delegateAdmin));
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, expired));
        
			// Get admin credential that was delegated to Leonardo
			GetDelegatedCredentialStep admin2 = new GetDelegatedCredentialStep(
					delegateAdmin, leoCredRequest);
			steps.add(admin2);

			// Register, verify and approve "Donatello"
			Application donatelloApp = getApplication("donatello", "Donatello","Turtle");
			steps.add(new DorianSubmitRegistrationStep(donatelloApp, dorianServiceURL));
			steps.add(new FindLocalUserStep(dorianServiceURL, admin, donatelloApp, LocalUserStatus.Pending,
                    LocalUserRole.Non_Administrator));
			steps.add(new UpdateLocalUserStatusStep(dorianServiceURL, admin, donatelloApp.getUserId(),
                    LocalUserStatus.Active));	
			
			// authenticate Donatello and get delegated credentials
			SuccessfullAuthentication donSuccess = new SuccessfullAuthentication(donatelloApp.getUserId(), 
					donatelloApp.getFirstName(), donatelloApp.getLastName(), donatelloApp.getEmail(),
                    signingCertStep);
			BasicAuthentication donCred = new BasicAuthentication( donatelloApp.getPassword(), donatelloApp.getUserId());
            AuthenticationStep donAuth = new AuthenticationStep(dorianServiceURL, donSuccess, donCred);
            steps.add(donAuth);
			steps.add(new DorianDestroyDefaultProxyStep());
			
			// Get Leonardos's Grid Credentials
            GridCredentialRequestStep donCredRequest = new GridCredentialRequestStep(dorianServiceURL, donAuth,
                new SuccessfullGridCredentialRequest());
            steps.add(donCredRequest);
			steps.add(new DorianDestroyDefaultProxyStep());
	
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, donCredRequest));
			steps.add(new GetDelegatedCredentialFailStep(delegateAdmin, donCredRequest,
				Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL));
			
			// Test finding credentials delegated to a client
			List<DelegationIdentifierReference> clientExpected = new ArrayList<DelegationIdentifierReference>();
			clientExpected.add(delegateAdmin);
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, admin));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, leoCredRequest,
					delegateAdmin));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, donCredRequest));
			
			// Now we want to disable the credential and test
			SuspendDelegatedCredentialStep suspendAdmin = new SuspendDelegatedCredentialStep(
					delegateAdmin, admin);
			steps.add(suspendAdmin);
			
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, approved));	
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, suspended,
					delegateAdmin));
			
			// Test finding credentials delegated to a client
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, admin));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, leoCredRequest));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, donCredRequest));			
			
			ProxyLifetime delegationLifetime = new ProxyLifetime();
			delegationLifetime.setSeconds(SHORT_LIFETIME_SECONDS);
			ProxyLifetime delegatedCredentialsLifetime = new ProxyLifetime();
			delegatedCredentialsLifetime.setSeconds((SHORT_LIFETIME_SECONDS / 2));			
			
			DelegateCredentialStep delegateAdminShort = new DelegateCredentialStep(
					cdsServiceURL, admin, allowedParties, delegationLifetime,
					delegatedCredentialsLifetime);
			steps.add(delegateAdminShort);

			// Test finding credentials delegated to a client
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, admin));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, leoCredRequest,
					delegateAdminShort));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, donCredRequest));	
			
			GetDelegatedCredentialStep adminShort = new GetDelegatedCredentialStep(
					delegateAdminShort, leoCredRequest);
			steps.add(adminShort);
			
			steps.add(new ProxyActiveStep(adminShort, true));
			steps.add(new GetDelegatedCredentialFailStep(delegateAdminShort,
					donCredRequest, Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL));
			long sleepTime = ((SHORT_LIFETIME_SECONDS / 2) * 1000) + 100;
			steps.add(new SleepStep(sleepTime));
			steps.add(new ProxyActiveStep(adminShort, false));
			steps.add(new SleepStep(sleepTime));
			steps.add(new GetDelegatedCredentialFailStep(delegateAdminShort,
					leoCredRequest, "org.globus.wsrf.NoSuchResourceException"));
			
			List<DelegationIdentifierReference> expected = new ArrayList<DelegationIdentifierReference>();
			expected.add(delegateAdmin);
			expected.add(delegateAdminShort);
			
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, expected));
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, valid,
					delegateAdmin));
			steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, expired,
					delegateAdminShort));
			
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, admin));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, leoCredRequest));
			steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, donCredRequest));		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

//		// Test finding credentials delegated to a client
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, admin));
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, leonardo));
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, donatello));
//
//		ProxyLifetime delegationLifetime = new ProxyLifetime();
//		delegationLifetime.setSeconds(SHORT_LIFETIME_SECONDS);
//		ProxyLifetime delegatedCredentialsLifetime = new ProxyLifetime();
//		delegatedCredentialsLifetime.setSeconds((SHORT_LIFETIME_SECONDS / 2));
//
//		DelegateCredentialStep delegateAdminShort = new DelegateCredentialStep(
//				cdsServiceURL, admin, allowedParties, delegationLifetime,
//				delegatedCredentialsLifetime);
//		steps.add(delegateAdminShort);
//
//		// Test finding credentials delegated to a client
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, admin));
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, leonardo,
//				delegateAdminShort));
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, donatello));
//
//		GetDelegatedCredentialStep adminShort = new GetDelegatedCredentialStep(
//				delegateAdminShort, leonardo);
//		steps.add(adminShort);
//
//		steps.add(new ProxyActiveStep(adminShort, true));
//		steps.add(new GetDelegatedCredentialFailStep(delegateAdminShort,
//				donatello, Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL));
//		long sleepTime = ((SHORT_LIFETIME_SECONDS / 2) * 1000) + 100;
//		steps.add(new SleepStep(sleepTime));
//		steps.add(new ProxyActiveStep(adminShort, false));
//		steps.add(new SleepStep(sleepTime));
//		steps.add(new GetDelegatedCredentialFailStep(delegateAdminShort,
//				leonardo, "org.globus.wsrf.NoSuchResourceException"));
//		List<DelegationIdentifierReference> expected = new ArrayList<DelegationIdentifierReference>();
//		expected.add(delegateAdmin);
//		expected.add(delegateAdminShort);
//		steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, expected));
//		steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, valid,
//				delegateAdmin));
//		steps.add(new FindMyDelegatedCredentialsStep(cdsServiceURL, admin, expired,
//				delegateAdminShort));
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, admin));
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, leonardo));
//		steps.add(new FindCredentialsDelegatedToClientStep(cdsServiceURL, donatello));
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



}
