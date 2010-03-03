package org.cagrid.fqp.test.common;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import org.cagrid.fqp.test.common.steps.AggregationStep;
import org.cagrid.fqp.test.common.steps.dcql2.Dcql2AggregationStep;

/** 
 *  AggregationStory
 *  Tests the aggregation capabilities of the Federated Query Engine
 * 
 * @author David Ervin
 * 
 * @created Jun 30, 2008 12:48:50 PM
 * @version $Id: AggregationStory.java,v 1.5 2008-09-03 17:28:15 dervin Exp $ 
 */
public class AggregationStory extends Story {
    
    public static final String SERVICE_NAME_BASE = "cagrid/ExampleSdkService";
    
    private ServiceContainerSource[] dataContainers = null;
    private FederatedQueryProcessorHelper queryHelper = null;
    
    public AggregationStory(ServiceContainerSource[] dataServiceContainers,
        FederatedQueryProcessorHelper queryHelper) {
        this.dataContainers = dataServiceContainers;
        this.queryHelper = queryHelper;
    }
    
    
    public String getName() {
        return "FQP Aggregation";
    }
    

    public String getDescription() {
        return "Tests the aggregation capabilities of the Federated Query Engine";
    }


    protected Vector<?> steps() {
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
        
        // DCQL 1 (gets converted in the query processor to DCQL 2 before execution)
        steps.add(new AggregationStep(FQPTestingConstants.QUERIES_LOCATION + "exampleAggregation1.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleAggregation1_gold.xml",
            queryHelper, serviceUrls));
        steps.add(new AggregationStep(FQPTestingConstants.QUERIES_LOCATION + "exampleAggregation2.xml",
            FQPTestingConstants.GOLD_LOCATION + "exampleAggregation2_gold.xml",
            queryHelper, serviceUrls));
        steps.add(new AggregationStep(FQPTestingConstants.QUERIES_LOCATION + "emptyResultsAggregation.xml",
            FQPTestingConstants.GOLD_LOCATION + "emptyResultsAggregation_gold.xml",
            queryHelper, serviceUrls));
        
        // DCQL 2
        steps.add(new Dcql2AggregationStep(FQPTestingConstants.DCQL2_QUERIES_LOCATION + "exampleAggregation1.xml",
            FQPTestingConstants.DCQL2_GOLD_LOCATION + "exampleAggregation1_gold.xml",
            queryHelper, serviceUrls));
        steps.add(new Dcql2AggregationStep(FQPTestingConstants.DCQL2_QUERIES_LOCATION + "exampleAggregation2.xml",
            FQPTestingConstants.DCQL2_GOLD_LOCATION + "exampleAggregation2_gold.xml",
            queryHelper, serviceUrls));
        steps.add(new Dcql2AggregationStep(FQPTestingConstants.DCQL2_QUERIES_LOCATION + "emptyResultsAggregation.xml",
            FQPTestingConstants.DCQL2_GOLD_LOCATION + "emptyResultsAggregation_gold.xml",
            queryHelper, serviceUrls));
        return steps;
    }
}
