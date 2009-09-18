package org.cagrid.fqp.test.remote;

import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.ServiceContainerSource;
import org.cagrid.fqp.test.remote.steps.NotificationClientSetupStep;
import org.cagrid.fqp.test.remote.steps.TransferQueryExecutionStep;

/**
 * TransferExecutionStory
 * Story exercices the transfer support of the FQP service
 *  
 * @author ervin
 */
public class TransferExecutionStory extends Story {
    
    public static final String DATA_SERVICE_NAME_BASE = "cagrid/ExampleSdkService";
    
    private ServiceContainerSource[] dataContainers = null;
    private ServiceContainerSource fqpContainerSource = null;

    public TransferExecutionStory(ServiceContainerSource[] dataServiceContainers, ServiceContainerSource fqpContainerSource) {
        this.dataContainers = dataServiceContainers;
        this.fqpContainerSource = fqpContainerSource;
    }


    public String getDescription() {
        return "Exercices the transfer support of the FQP service";
    }
    
    
    public String getName() {
        return "FQP Transfer Execution";
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        
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
        
        // figure out the URLs of the test services
        String[] serviceUrls = new String[dataContainers.length];
        for (int i = 0; i < dataContainers.length; i++) {
            ServiceContainer container = dataContainers[i].getServiceContainer();
            try {
                String base = container.getContainerBaseURI().toString();
                serviceUrls[i] = base + DATA_SERVICE_NAME_BASE + String.valueOf(i + 1);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error creating data service URL: " + ex.getMessage());
            }
        }
        
        steps.add(new NotificationClientSetupStep());
        
        steps.add(new TransferQueryExecutionStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            fqpClient, serviceUrls));
        
        return steps;
    }
}
