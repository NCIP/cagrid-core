/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.fqp.test.remote.steps.dcql2;

import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.resultsretrieval.client.FederatedQueryResultsRetrievalClient;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.QueryResultsVerifier;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.dcql2.BaseDcql2QueryExecutionStep;
import org.oasis.wsrf.lifetime.Destroy;
import org.oasis.wsrf.lifetime.SetTerminationTime;
import org.oasis.wsrf.lifetime.SetTerminationTimeResponse;

public class Dcql2AsynchronousQueryExecutionStep extends BaseDcql2QueryExecutionStep {
    
    private static Log LOG = LogFactory.getLog(Dcql2AsynchronousQueryExecutionStep.class);
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] serviceUrls;
    
    public Dcql2AsynchronousQueryExecutionStep(String queryFilename, String goldFilename, 
        FederatedQueryProcessorClient client, String[] serviceUrls) {
        super(queryFilename, goldFilename);
        this.fqpClient = client;
        this.serviceUrls = serviceUrls;
    }

    
    public void runStep() throws Throwable {
        // get the query with URLs substituted
        DCQLQuery query = getCompletedQuery();
        
        // run the query
        FederatedQueryResultsRetrievalClient resultsClient = 
            fqpClient.queryAsynchronously(query, null, null);
        
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
            fail("DCQL 2 Results resource should have been destroyed, but is still available!");
        } catch (RemoteException e) {
            // expected
        }
        
        try {
            resultsClient.getResults();
            fail("DCQL 2 Results resource should have been destroyed, but is still available!");
        } catch (RemoteException ex) {
            // expected
        }
        
        // run the query again to create a new resource
        resultsClient = 
            fqpClient.queryAsynchronously(query, null, null);
        
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
            fail("DCQL 2 Results resource should have been destroyed, but is still available!");
        } catch (RemoteException e) {
            // expected
        }
        
        try {
            resultsClient.getResults();
            fail("DCQL 2 Results resource should have been destroyed, but is still available!");
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
            fail("Unable to replace URL placeholders in DCQL 2 query: " + ex.getMessage());
        }
        return replaced;
    }
    
    
    private void verifyQueryResults(DCQLQueryResultsCollection testResults, DCQLQueryResultsCollection goldResults) {
        LOG.debug("Verifying DCQL 2 query results against gold");
        QueryResultsVerifier.verifyDcql2Results(testResults, goldResults);
    }
}
