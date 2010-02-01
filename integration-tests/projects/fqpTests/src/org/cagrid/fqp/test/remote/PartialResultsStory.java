package org.cagrid.fqp.test.remote;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.dcqlresult.DCQLResult;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.testing.system.deployment.ContainerPorts;
import gov.nih.nci.cagrid.testing.system.deployment.NoAvailablePortException;
import gov.nih.nci.cagrid.testing.system.deployment.PortFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.results.metadata.ResultsRange;
import org.cagrid.fqp.results.metadata.ServiceConnectionStatus;
import org.cagrid.fqp.results.metadata.TargetServiceStatus;
import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.ServiceContainerSource;
import org.cagrid.fqp.test.remote.steps.PartialResultsQueryStep;

/**
 * PartialResultsStory
 * Story exercices the partial-results functionality of the FQP service
 *  
 * @author ervin
 */
public class PartialResultsStory extends Story {
    
    public static final String SERVICE_NAME_BASE = "cagrid/ExampleSdkService";
    public static final String NON_CONNECT_SERVICE_HOSTNAME = "localhost";
    public static final String NON_CONNECT_SERVICE_LOCATION = "non-existant/data/service";
    
    private ServiceContainerSource[] dataContainers = null;
    private ServiceContainerSource fqpContainerSource = null;

    public PartialResultsStory(ServiceContainerSource[] dataServiceContainers, ServiceContainerSource fqpContainerSource) {
        this.dataContainers = dataServiceContainers;
        this.fqpContainerSource = fqpContainerSource;
    }


    public String getDescription() {
        return "Exercices the partial-results functionality of the FQP service";
    }
    
    
    public String getName() {
        return "FQP Partial Results";
    }


    protected Vector<?> steps() {
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
                serviceUrls[i] = base + SERVICE_NAME_BASE + String.valueOf(i + 1);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error creating data service URL: " + ex.getMessage());
            }
        }
        
        // create a data service URL that won't connect to anything
        ContainerPorts nonConnectPorts = null;
        try {
            nonConnectPorts = PortFactory.getContainerPorts();
        } catch (NoAvailablePortException ex) {
            ex.printStackTrace();
            fail("Could not obtain an unused network port!");
        }
        String nonConnectUrl = "http://" + NON_CONNECT_SERVICE_HOSTNAME 
            + ":" + nonConnectPorts.getPort().toString() + "/" + NON_CONNECT_SERVICE_LOCATION;
        // array of all URLs involved in the query
        String[] serviceUrlsNonConnect = (String[]) Utils.appendToArray(serviceUrls, nonConnectUrl);
        // array of URLs of target data services
        String[] targetUrlsNonConnect = new String[] {serviceUrls[0], nonConnectUrl};
        
        TargetServiceStatus okStatus = new TargetServiceStatus();
        okStatus.setConnectionStatus(ServiceConnectionStatus.OK);
        int resultsCount = getResultsCount(FQPTestingConstants.GOLD_LOCATION + File.separator + "exampleDistributedJoin1_gold.xml");
        ResultsRange range = new ResultsRange(resultsCount - 1, 0);
        okStatus.setResultsRange(range);
        TargetServiceStatus nonConnectStatus = new TargetServiceStatus();
        nonConnectStatus.setConnectionStatus(ServiceConnectionStatus.Could_Not_Connect);
        
        steps.add(new PartialResultsQueryStep(FQPTestingConstants.QUERIES_LOCATION + File.separator + "exampleDistributedJoin1_partial.xml",
            FQPTestingConstants.GOLD_LOCATION + File.separator + "exampleDistributedJoin1_gold.xml", 
            fqpClient, serviceUrlsNonConnect, targetUrlsNonConnect,
            ProcessingStatus.Complete_With_Error, new TargetServiceStatus[] {okStatus, nonConnectStatus}));
        
        return steps;
    }
    
    
    private int getResultsCount(String dcqlResultsFilename) {
        DCQLQueryResultsCollection results = null;
        try {
            results = Utils.deserializeDocument(
                dcqlResultsFilename, DCQLQueryResultsCollection.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing DCQL gold query results: " + ex.getMessage());
        }
        int count = 0;
        for (DCQLResult result : results.getDCQLResult()) {
            count += result.getCQLQueryResultCollection().getObjectResult().length;
        }
        return count;
    }
}
