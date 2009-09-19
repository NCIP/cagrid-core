package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.FileReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Runs an asynchronous DCQL query against an FQP service,
 * and the query is expected to fail 
 */
public class AsynchronousQueryExecutionFailsStep extends Step {
    
    private static Log LOG = LogFactory.getLog(AsynchronousQueryExecutionFailsStep.class);
    
    public static final int FAILURE_TIMEOUT = 20; // secconds before failure waiting... fails...
        
    private FederatedQueryProcessorClient fqpClient = null;
    private String queryFilename = null;
    
    public AsynchronousQueryExecutionFailsStep(
        FederatedQueryProcessorClient client, String queryFilename) {
        this.fqpClient = client;
        this.queryFilename = queryFilename;
    }

    
    public void runStep() throws Throwable {
        // grab the query
        DCQLQuery query = getQuery();
        
        // execute it asynchronously
        FederatedQueryResultsClient resultsClient = null;
        try {
            resultsClient = fqpClient.executeAsynchronously(query);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error starting asynchronous query: " + ex.getMessage());
        }
        
        long start = System.currentTimeMillis();
        boolean complete = false;
        // wait for the query to complete / fail
        while (!complete && 
            (System.currentTimeMillis() - start) < (FAILURE_TIMEOUT * 1000)) {
            complete = resultsClient.isProcessingComplete();
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                // ?
            }
        }
        
        // should have at least completed...
        assertTrue("Query did not complete in the allotted time (" 
            + FAILURE_TIMEOUT + " sec)", complete);
        
        // see if we can get a result, or if an exception comes back...
        try {
            resultsClient.getResults();
            fail("Query execution should have thrown an exception, but returned results!");
        } catch (Exception ex) {
            LOG.debug("This exception was thrown when trying to " +
                    "get results after running a query which " +
                    "SHOULD have failed.", ex);
        }
    }
    
    
    private DCQLQuery getQuery() {
        DCQLQuery query = null;
        try {
            FileReader reader = new FileReader(queryFilename);
            query = (DCQLQuery) Utils.deserializeObject(reader, DCQLQuery.class);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading DCQL query (" + queryFilename + "): " + ex.getMessage());
        }
        return query;
    }
}
