package org.cagrid.fqp.test.common;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.fqp.test.common.steps.StandardQueryStep;

public class QueryStory extends Story {
    public static final String SERVICE_NAME_BASE = "cagrid/ExampleSdkService";
    
    private ServiceContainerSource[] dataContainers = null;
    private FederatedQueryProcessorHelper queryHelper = null;
    
    public QueryStory(ServiceContainerSource[] dataServiceContainers,
        FederatedQueryProcessorHelper queryHelper) {
        this.dataContainers = dataServiceContainers;
        this.queryHelper = queryHelper;
    }
    
    
    public String getName() {
        return "DCQL Query Tests";
    }
    

    public String getDescription() {
        return "Tests standard DCQL query operation";
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
        
        steps.add(new StandardQueryStep(FQPTestingConstants.QUERIES_LOCATION + "exampleDistributedJoin1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleDistributedJoin1_gold.xml",
            queryHelper, serviceUrls));
        return steps;
    }
}
