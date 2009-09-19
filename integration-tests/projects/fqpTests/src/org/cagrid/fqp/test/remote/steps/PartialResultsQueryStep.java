package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;
import gov.nih.nci.cagrid.fqp.results.common.FederatedQueryResultsConstants;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.fqp.execution.TargetDataServiceQueryBehavior;
import org.cagrid.fqp.results.metadata.FederatedQueryExecutionStatus;
import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.results.metadata.TargetServiceStatus;
import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.QueryResultsVerifier;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.properties.GetResourcePropertyResponse;


/**
 * PartialResultsQueryStep Executes a query allowing for partial results from
 * the target data services
 * 
 * @author ervin
 */
public class PartialResultsQueryStep extends BaseQueryExecutionStep {

    private static final Log LOG = LogFactory.getLog(PartialResultsQueryStep.class);

    public static final String[] QUERY_URL_PLACEHOLDERS = { 
        "DATA_SERVICE_1", "DATA_SERVICE_2", "DATA_SERVICE_BAD"
    };
    
    public static final String[] TARGET_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    

    private FederatedQueryProcessorClient fqpClient = null;
    private String[] queryServiceUrls = null;
    private String[] targetServiceUrls = null;
    private ProcessingStatus expectedStatus = null;
    private TargetServiceStatus[] expectedTargetStatuses = null;

    public PartialResultsQueryStep(String queryFilename, String goldFilename, FederatedQueryProcessorClient fqpClient,
        String[] queryServiceUrls, String[] targetServiceUrls, 
        ProcessingStatus expectedStatus, TargetServiceStatus[] expectedConnections) {
        super(queryFilename, goldFilename);
        this.fqpClient = fqpClient;
        this.queryServiceUrls = queryServiceUrls;
        this.targetServiceUrls = targetServiceUrls;
        this.expectedStatus = expectedStatus;
        this.expectedTargetStatuses = expectedConnections;
    }


    public void runStep() throws Throwable {
        QueryExecutionParameters executionParameters = getExecutionParameters();

        DCQLQuery query = getCompletedQuery();

        FederatedQueryResultsClient fqpResultsClient = fqpClient.query(query, null, executionParameters);

        // wait for processing to wrap up
        int retries = 0;
        boolean processingComplete = false;
        System.out.print("Waiting for query to complete: ");
        while (retries < FQPTestingConstants.PROCESSING_WAIT_RETRIES
            && !(processingComplete = fqpResultsClient.isProcessingComplete())) {
            Thread.sleep(FQPTestingConstants.PROCESSING_RETRY_DELAY);
            System.out.print(".");
            retries++;
        }
        System.out.println();
        assertTrue("Query processing did not complete after " + FQPTestingConstants.PROCESSING_WAIT_RETRIES
            + " retries of " + FQPTestingConstants.PROCESSING_RETRY_DELAY + "ms", processingComplete);
        
        verifyStatusResourceProperty(fqpResultsClient);
        
        // get the results and verify them against gold
        DCQLQueryResultsCollection results = null;
        try {
            results = fqpResultsClient.getResults();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error retrieving DCQL query results: " + ex.getMessage());
        }
        DCQLQueryResultsCollection goldResults = loadGoldDcqlResults();
        verifyQueryResults(results, goldResults);
    }
    
    
    private void verifyStatusResourceProperty(FederatedQueryResultsClient client) {
        GetResourcePropertyResponse response = null;
        try {
            response = client.getResourceProperty(
                FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            fail("Error retrieving resource property " 
                + FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS 
                + ": " + ex.getMessage());
        }
        
        String metadataDocument = null;
        try {
            metadataDocument = AnyHelper.toSingleString(response.get_any());
            System.out.println("GOT STATUS DOCUMENT:");
            System.out.println(metadataDocument);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error reading resource property as string: " + ex.getMessage());
        }
        
        FederatedQueryExecutionStatus status = null;
        try {
            status = (FederatedQueryExecutionStatus) Utils.deserializeObject(
                new StringReader(metadataDocument), FederatedQueryExecutionStatus.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing status document: " + ex.getMessage());
        }
        
        // verify status
        assertEquals("Final processing status different than expected", 
            expectedStatus.getValue(), status.getCurrentStatus().getValue());
        TargetServiceStatus[] expected = getCompletedTargetStatuses();
        assertEquals("Unexpected number of target service statuses", 
            expectedTargetStatuses.length, status.getTargetServiceStatus().length);
        for (TargetServiceStatus expect : expected) {
            boolean found = false;
            for (TargetServiceStatus test : status.getTargetServiceStatus()) {
                if (expect.getServiceURL().equals(test.getServiceURL())) {
                    found = true;
                    assertEquals("Status of service " + test.getServiceURL() + " did not match expected status", 
                        expect.getConnectionStatus(), test.getConnectionStatus());
                    break;
                }
            }
            if (!found) {
                fail("Status of expected target data service " + expect.getServiceURL() + " not found");
            }
        }
    }


    private QueryExecutionParameters getExecutionParameters() {
        QueryExecutionParameters params = new QueryExecutionParameters();
        params.setAlwaysAuthenticate(Boolean.FALSE);
        TargetDataServiceQueryBehavior targetBehavior = new TargetDataServiceQueryBehavior();
        targetBehavior.setFailOnFirstError(Boolean.FALSE);
        targetBehavior.setRetries(Integer.valueOf(0));
        targetBehavior.setTimeoutPerRetry(Integer.valueOf(5));
        params.setTargetDataServiceQueryBehavior(targetBehavior);
        return params;
    }


    private DCQLQuery getCompletedQuery() {
        assertEquals("Unexpected number of service urls", QUERY_URL_PLACEHOLDERS.length, queryServiceUrls.length);
        LOG.debug("Filling placeholder URLs with real ones");
        DCQLQuery original = deserializeQuery();
        Map<String, String> urlReplacements = new HashMap<String, String>();
        for (int i = 0; i < QUERY_URL_PLACEHOLDERS.length; i++) {
            urlReplacements.put(QUERY_URL_PLACEHOLDERS[i], queryServiceUrls[i]);
        }
        DCQLQuery replaced = null;
        try {
            replaced = UrlReplacer.replaceUrls(original, urlReplacements);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to replace URL placeholders in DCQL query: " + ex.getMessage());
        }
        return replaced;
    }
    
    
    private TargetServiceStatus[] getCompletedTargetStatuses() {
        assertEquals("Unexpected number of target statuses", 
            TARGET_URL_PLACEHOLDERS.length, expectedTargetStatuses.length);
        LOG.debug("Filling target status URLs with real ones");
        try {
            for (int i = 0; i < targetServiceUrls.length; i++) {
                expectedTargetStatuses[i].setServiceURL(new URI(targetServiceUrls[i]));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting service URL of target service status: " + ex.getMessage());
        }
        return expectedTargetStatuses;
    }
    
    
    private void verifyQueryResults(DCQLQueryResultsCollection testResults, DCQLQueryResultsCollection goldResults) {
        LOG.debug("Verifying DCQL query results against gold");
        QueryResultsVerifier.verifyDcqlResults(testResults, goldResults);
    }
}
