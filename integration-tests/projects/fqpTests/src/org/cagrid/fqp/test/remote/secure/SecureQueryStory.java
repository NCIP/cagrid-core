package org.cagrid.fqp.test.remote.secure;

import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Collections;
import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.ServiceContainerSource;
import org.cagrid.fqp.test.remote.secure.steps.DelegatedCredentialQueryStep;
import org.cagrid.gaards.cds.client.ClientConstants;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.common.Utils;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;


/**
 * SecureQueryStory Invokes FQP queries using a delegated credential
 * 
 * @author David
 */
public class SecureQueryStory extends Story {
    public static final int PROXY_DELEGATION_LIFETIME = 10; // minutes
    public static final String USER_CERT_FILENAME = "user_cert.pem";
    public static final String USER_KEY_FILENAME = "user_key.pem";
    public static final String HOST_CERT_FILENAME = "localhost_cert.pem";
    public static final String HOST_KEY_FILENAME = "localhost_key.pem";
    public static final String DATA_SERVICE_NAME_BASE = "cagrid/ExampleSdkService";

    private ServiceContainerSource[] dataContainerSources = null;
    private ServiceContainerSource cdsContainerSource = null;
    private ServiceContainerSource fqpContainerSource = null;


    public SecureQueryStory(ServiceContainerSource[] dataContainers, ServiceContainerSource cdsContainer,
        ServiceContainerSource fqpContainer) {
        this.dataContainerSources = dataContainers;
        this.cdsContainerSource = cdsContainer;
        this.fqpContainerSource = fqpContainer;
    }


    public String getName() {
        return "Secure FQP with CDS Query Story";
    }


    public String getDescription() {
        return "Invokes FQP queries using a delegated credential";
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        // figure out the URLs of the test services
        String[] serviceUrls = new String[dataContainerSources.length];
        for (int i = 0; i < dataContainerSources.length; i++) {
            ServiceContainer container = dataContainerSources[i].getServiceContainer();
            try {
                String base = container.getContainerBaseURI().toString();
                serviceUrls[i] = base + DATA_SERVICE_NAME_BASE + String.valueOf(i + 1);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error creating data service URL: " + ex.getMessage());
            }
        }
        
        // delegate a credential to CDS
        DelegatedCredentialReference credentialRef = delegateCredential();
        
        ServiceContainer fqpContainer = fqpContainerSource.getServiceContainer();
        EndpointReferenceType fqpEPR = null;
        try {
            fqpEPR = fqpContainer.getServiceEPR("cagrid/FederatedQueryProcessor");
        } catch (MalformedURIException ex) {
            ex.printStackTrace();
            fail("Error obtaining FQP's EPR: " + ex.getMessage());
        }
        
        steps.add(new DelegatedCredentialQueryStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            fqpEPR, credentialRef, serviceUrls, (SecureContainer) fqpContainer));

        return steps;
    }
    
    
    private DelegatedCredentialReference delegateCredential() {
        DelegatedCredentialReference credentialRef = null;
        
        // get the service containers
        ServiceContainer cdsContainer = cdsContainerSource.getServiceContainer();
        ServiceContainer fqpContainer = fqpContainerSource.getServiceContainer();
        
        // get the EPR of the CDS
        EndpointReferenceType cdsEPR = null;
        try {
            cdsEPR = cdsContainer.getServiceEPR("cagrid/CredentialDelegationService");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining CDS EPR: " + ex.getMessage());
        }
        
        // Load the testing proxy cert
        GlobusCredential proxyCredential = null;
        try {
            File userCertFile = new File(((SecureContainer) fqpContainer).getCertificatesDirectory(), USER_CERT_FILENAME);
            File userKeyFile = new File(((SecureContainer) fqpContainer).getCertificatesDirectory(), USER_KEY_FILENAME);
            System.out.println("Loading user cert from " + userCertFile.getAbsolutePath());
            System.out.println("Loading user key from " + userKeyFile.getAbsolutePath());
            proxyCredential = new GlobusCredential(userCertFile.getAbsolutePath(), userKeyFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining client proxy: " + ex.getMessage());
        }

        // Set up the lifetime of delegation
        ProxyLifetime delegationLifetime = new ProxyLifetime();
        delegationLifetime.setHours(0);
        delegationLifetime.setMinutes(PROXY_DELEGATION_LIFETIME);
        delegationLifetime.setSeconds(0);

        // path length only needs to be 1
        int delegationPathLength = 1;

        // Set up lifetime when delegated credential is issued to a service
        ProxyLifetime issuedCredentialLifetime = new ProxyLifetime();
        issuedCredentialLifetime.setHours(0);
        issuedCredentialLifetime.setMinutes(PROXY_DELEGATION_LIFETIME);
        issuedCredentialLifetime.setSeconds(0);

        // credential may not be further delegated
        int issuedCredentialPathLength = 0;

        // Key length of the credential
        int keySize = ClientConstants.DEFAULT_KEY_SIZE;
        
        // Load the FQP service's host cert
        GlobusCredential fqpHostCert = null;
        try {
            File fqpHostCertFile = new File(((SecureContainer) fqpContainer).getCertificatesDirectory(), HOST_CERT_FILENAME);
            File fqpHostKeyFile = new File(((SecureContainer) fqpContainer).getCertificatesDirectory(), HOST_KEY_FILENAME);
            System.out.println("Loading fqp host cert from " + fqpHostCertFile.getAbsolutePath());
            System.out.println("Loading fqp host key from " + fqpHostKeyFile.getAbsolutePath());
            fqpHostCert = new GlobusCredential(fqpHostCertFile.getAbsolutePath(), fqpHostKeyFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error obtaining FQP's host credential: " + ex.getMessage());
        }
        
        // policy stating FQP is allowed to obtain the delegated credential
        String fqpIdentity = fqpHostCert.getIdentity();
        IdentityDelegationPolicy policy = Utils.createIdentityDelegationPolicy(
            Collections.singletonList(fqpIdentity));

        try {
            // use the DelegationUserClient API
            DelegationUserClient client = new DelegationUserClient(
                cdsEPR.getAddress().toString(), proxyCredential);

            // delegate the credential
            credentialRef = client.delegateCredential(delegationLifetime, delegationPathLength, policy,
                issuedCredentialLifetime, issuedCredentialPathLength, keySize);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error delegating credential via CDS: " + ex.getMessage());
        }
        
        return credentialRef;
    }
}
