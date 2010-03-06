package org.cagrid.fqp.test.remote.steps.dcql2;

import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.resultsretrieval.client.FederatedQueryResultsRetrievalClient;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.FileReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.utilities.DCQL2SerializationUtil;
import org.cagrid.data.dcql.DCQLQuery;

/**
 * Runs an asynchronous DCQL 2 query against an FQP service,
 * and the query is expected to fail 
 */
public class Dcql2AsynchronousQueryExecutionFailsStep extends Step {
    
    private static Log LOG = LogFactory.getLog(Dcql2AsynchronousQueryExecutionFailsStep.class);
    
    public static final int FAILURE_TIMEOUT = 20; // seconds before failure waiting... fails...
        
    private FederatedQueryProcessorClient fqpClient = null;
    private String queryFilename = null;
    
    public Dcql2AsynchronousQueryExecutionFailsStep(
        FederatedQueryProcessorClient client, String queryFilename) {
        this.fqpClient = client;
        this.queryFilename = queryFilename;
    }

    
    public void runStep() throws Throwable {
        // grab the query
        DCQLQuery query = getQuery();
        
        // execute it asynchronously
        FederatedQueryResultsRetrievalClient resultsClient = null;
        try {
            resultsClient = fqpClient.queryAsynchronously(query, null, null);
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
            query = DCQL2SerializationUtil.deserializeDcql2Query(reader);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading DCQL 2 query (" + queryFilename + "): " + ex.getMessage());
        }
        return query;
    }
}
