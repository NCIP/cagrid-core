package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.QueryResultsVerifier;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;
import org.oasis.wsrf.lifetime.Destroy;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;

public class AsynchronousQueryExecutionStep extends BaseQueryExecutionStep {
    
    private static Log LOG = LogFactory.getLog(AsynchronousQueryExecutionStep.class);
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] serviceUrls;
    
    public AsynchronousQueryExecutionStep(String queryFilename, String goldFilename, 
        FederatedQueryProcessorClient client, String[] serviceUrls) {
        super(queryFilename, goldFilename);
        this.fqpClient = client;
        this.serviceUrls = serviceUrls;
    }

    
    public void runStep() throws Throwable {
        // get the query with URLs substituted
        DCQLQuery query = getCompletedQuery();
        
        // run the query
        FederatedQueryResultsClient resultsClient = 
            fqpClient.executeAsynchronously(query);
        
        // wait for processing to wrap up
        int retries = 0;
        boolean processingComplete = false;
        System.out.print("Waiting for query to complete: ");
        while (retries < FQPTestingConstants.PROCESSING_WAIT_RETRIES && !(processingComplete = resultsClient.isProcessingComplete())) {
            Thread.sleep(FQPTestingConstants.PROCESSING_RETRY_DELAY);
            System.out.print(".");
            retries++;
        }
        System.out.println();
        assertTrue("Query processing did not complete after " + FQPTestingConstants.PROCESSING_WAIT_RETRIES + 
            " retries of " + FQPTestingConstants.PROCESSING_RETRY_DELAY + "ms", processingComplete);
        
        // get the results
        LOG.debug("Retrieving results");
        DCQLQueryResultsCollection testResults = resultsClient.getResults();
        DCQLQueryResultsCollection goldResults = loadGoldDcqlResults();
        verifyQueryResults(testResults, goldResults);
        
        // schedule a destroy for the results resource
        LOG.debug("Scheduling resource termination");
        int terminateAfterSecs = 5;
        SetTerminationTime termTime = new SetTerminationTime();
        Calendar terminateAt = Calendar.getInstance();
        terminateAt.add(Calendar.SECOND, terminateAfterSecs);
        termTime.setRequestedTerminationTime(terminateAt);

        SetTerminationTimeResponse response = resultsClient.setTerminationTime(termTime);

		LOG.debug("Current time " + response.getCurrentTime().getTime());
        LOG.debug("Requested termination time " + terminateAt.getTime());
        LOG.debug("Scheduled termination time " + response.getNewTerminationTime().getTime());
        LOG.debug("Should terminate in:"
            + (response.getNewTerminationTime().getTimeInMillis() - Calendar.getInstance().getTimeInMillis()) / 1000
            + " seconds.");

        // wait for it to be expired (twice the termination time delay) + resource sweeper interval
        long sleepTime = (1000 * terminateAfterSecs * 2) + FQPTestingConstants.RESOURCE_SWEEPER_DELAY;
        long currentTime = System.currentTimeMillis();
        LOG.debug("Sleeping current thread for " + sleepTime + " ms");
        Thread.sleep(sleepTime);
        LOG.debug((System.currentTimeMillis() - currentTime) + " ms have passed while this thread slept");

        // make sure its gone
        try {
            resultsClient.isProcessingComplete();
            fail("DCQL Results resource should have been destroyed, but is still available!");
        } catch (RemoteException e) {
            // expected
        }
        
        try {
            resultsClient.getResults();
            fail("DCQL Results resource should have been destroyed, but is still available!");
        } catch (RemoteException ex) {
            // expected
        }
        
        // run the query again to create a new resource
        resultsClient = 
            fqpClient.executeAsynchronously(query);
        
        // wait for processing to wrap up
        retries = 0;
        processingComplete = false;
        while (retries < FQPTestingConstants.PROCESSING_WAIT_RETRIES && !(processingComplete = resultsClient.isProcessingComplete())) {
            Thread.sleep(FQPTestingConstants.PROCESSING_RETRY_DELAY);
            System.out.println(".");
            retries++;
        }
        assertTrue("Query processing did not complete after " + FQPTestingConstants.PROCESSING_WAIT_RETRIES + 
            " retries of " + FQPTestingConstants.PROCESSING_RETRY_DELAY + "ms", processingComplete);
        
        // explicitly destroy the resource
        resultsClient.destroy(new Destroy());
        
        // Sleep the current thread briefly
        Thread.sleep(FQPTestingConstants.RESOURCE_SWEEPER_DELAY * 2);
        
        // make sure its gone
        try {
            resultsClient.isProcessingComplete();
            fail("DCQL Results resource should have been destroyed, but is still available!");
        } catch (RemoteException e) {
            // expected
        }
        
        try {
            resultsClient.getResults();
            fail("DCQL Results resource should have been destroyed, but is still available!");
        } catch (RemoteException ex) {
            // expected
        }
    }
    
    
    private DCQLQuery getCompletedQuery() {
        assertEquals("Unexpected number of service urls", QUERY_URL_PLACEHOLDERS.length, serviceUrls.length);
        LOG.debug("Filling placeholder URLs with real ones");
        DCQLQuery original = deserializeQuery();
        Map<String, String> urlReplacements = new HashMap<String, String>();
        for (int i = 0; i < QUERY_URL_PLACEHOLDERS.length; i++) {
            urlReplacements.put(QUERY_URL_PLACEHOLDERS[i], serviceUrls[i]);
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
    
    
    private void verifyQueryResults(DCQLQueryResultsCollection testResults, DCQLQueryResultsCollection goldResults) {
        LOG.debug("Verifying DCQL query results against gold");
        QueryResultsVerifier.verifyDcqlResults(testResults, goldResults);
    }
}
