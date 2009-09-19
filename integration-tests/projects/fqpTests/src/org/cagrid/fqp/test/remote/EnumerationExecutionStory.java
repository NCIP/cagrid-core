package org.cagrid.fqp.test.remote;

import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.ServiceContainerSource;
import org.cagrid.fqp.test.remote.steps.EnumerationQueryExecutionStep;
import org.cagrid.fqp.test.remote.steps.NotificationClientSetupStep;

/** 
 *  EnumerationExecutionStory
 *  Tests the WS-Enumeration query support of the Federated Query Service
 * 
 * @author David Ervin
 * 
 * @created Jun 30, 2008 12:48:50 PM
 * @version $Id: EnumerationExecutionStory.java,v 1.2 2008-10-07 17:33:49 dervin Exp $ 
 */
public class EnumerationExecutionStory extends Story {
    
    public static final String SERVICE_NAME_BASE = "cagrid/ExampleSdkService";
    
    private ServiceContainerSource[] dataContainers = null;
    private ServiceContainerSource fqpContainerSource = null;

    public EnumerationExecutionStory(ServiceContainerSource[] dataServiceContainers, ServiceContainerSource fqpContainer) {
        this.dataContainers = dataServiceContainers;
        this.fqpContainerSource = fqpContainer;
    }
    
    
    public String getName() {
        return "FQP Enumeration Execution";
    }
    

    public String getDescription() {
        return "Tests the WS-Enumeration query support of the Federated Query Service";
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
        
        // set up for notification
        steps.add(new NotificationClientSetupStep());
        
        // run some queries
        steps.add(new EnumerationQueryExecutionStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            fqpClient, serviceUrls));
        return steps;
    }
}
