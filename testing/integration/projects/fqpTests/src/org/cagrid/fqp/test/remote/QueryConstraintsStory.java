package org.cagrid.fqp.test.remote;

import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.ServiceContainerSource;
import org.cagrid.fqp.test.remote.steps.ChangeFqpServiceProperties;
import org.cagrid.fqp.test.remote.steps.MaxRetriesStep;
import org.cagrid.fqp.test.remote.steps.MaxRetryTimeoutStep;
import org.cagrid.fqp.test.remote.steps.MaxTargetServicesStep;
import org.cagrid.fqp.test.remote.steps.ResourceTimeoutQueryStep;

/**
 * QueryConstraintsStory
 * Tests what happens if queries which exceed the FQP's configured
 * query constraints are fired off.
 * 
 * @author David
 */
public class QueryConstraintsStory extends Story {
    
    public static final String SERVICE_NAME_BASE = "cagrid/ExampleSdkService";
    
    private ServiceContainerSource[] dataContainers = null;
    private ServiceContainerSource fqpContainerSource = null;
    private File fqpServiceDir = null;
    private Properties fqpServiceProperties = null;

    public QueryConstraintsStory(ServiceContainerSource[] dataContainers,
        ServiceContainerSource fqpContainer, File fqpServiceDir) {
        super();
        this.dataContainers = dataContainers;
        this.fqpContainerSource = fqpContainer;
        this.fqpServiceDir = fqpServiceDir;
    }
    
    
    public String getName() {
        return "FQP Query Constraints Test";
    }


    public String getDescription() {
        return "Tests what happens if queries which exceed the FQP's " +
                "configured query constraints are fired off.";
    }
    
    
    public boolean storySetUp() {
        fqpServiceProperties = new Properties();
        File propertiesFile = new File(fqpServiceDir, "service.properties");
        try {
            FileInputStream input = new FileInputStream(propertiesFile);
            fqpServiceProperties.load(input);
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error loading FQP service properties: " + ex.getMessage());
            return false;
        }
        return true;
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();

        // figure out the URLs of the test services
        String[] serviceUrls = new String[dataContainers.length];
        for (int i = 0; i < dataContainers.length; i++) {
            ServiceContainer container = dataContainers[i].getServiceContainer();
            try {
                String base = container.getContainerBaseURI().toString();
                serviceUrls[i] = base + SERVICE_NAME_BASE + String.valueOf(i + 1);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error creating data service URL: " + ex.getMessage());
            }
        }
        // create a new FQP client from the FQP service container
        FederatedQueryProcessorClient fqpClient = null;
        try {
            fqpClient = new FederatedQueryProcessorClient(
                fqpContainerSource.getServiceContainer().getContainerBaseURI().toString() + 
                "cagrid/FederatedQueryProcessor");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating FQP client: " + ex.getMessage());
        }
        
        // get the timeout we need
        int leaseTimeMinutes = Integer.parseInt(
            fqpServiceProperties.getProperty(ChangeFqpServiceProperties.LEASE_TIME));
        long expirationSleepTime = leaseTimeMinutes * 60 * 1000;
        expirationSleepTime *= 1.5d;
        
        // test resource timeout
        steps.add(new ResourceTimeoutQueryStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            fqpClient, serviceUrls, expirationSleepTime));
        
        // get max target services
        int maxServices = Integer.parseInt(
            fqpServiceProperties.getProperty(ChangeFqpServiceProperties.MAX_TARGET_SERVICES));
        
        // test max target services
        steps.add(new MaxTargetServicesStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            fqpClient, serviceUrls, maxServices + 1));
        
        // get max retry timeout
        int maxRetryTimeout = Integer.parseInt(
            fqpServiceProperties.getProperty(ChangeFqpServiceProperties.MAX_RETRY_TIMEOUT));
        
        // test max retry timeout
        steps.add(new MaxRetryTimeoutStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            fqpClient, serviceUrls, maxRetryTimeout + 1));
        
        // get max retries
        int maxRetries = Integer.parseInt(
            fqpServiceProperties.getProperty(ChangeFqpServiceProperties.MAX_RETRIES));
        
        // test max retries
        steps.add(new MaxRetriesStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            fqpClient, serviceUrls, maxRetries + 1));
        
        return steps;
    }
}
