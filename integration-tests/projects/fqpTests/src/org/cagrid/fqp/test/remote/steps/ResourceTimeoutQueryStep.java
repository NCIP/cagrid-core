package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;

public class ResourceTimeoutQueryStep extends BaseQueryExecutionStep {
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    private static Log LOG = LogFactory.getLog(ResourceTimeoutQueryStep.class);
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] serviceUrls;
    private long timeoutMills;
    
    
    public ResourceTimeoutQueryStep(String queryFilename, String goldFilename,
        FederatedQueryProcessorClient fqpClient, String[] serviceUrls, long timeoutMills) {
        super(queryFilename, goldFilename);
        this.fqpClient = fqpClient;
        this.serviceUrls = serviceUrls;
        this.timeoutMills = timeoutMills;
    }
    

    public void runStep() throws Throwable {
        // get the query set up
        LOG.debug("Testing with query " + getQueryFilename());
        DCQLQuery query = getCompletedQuery();
        // query asynchronously
        FederatedQueryResultsClient resultsClient = null;
        try {
            resultsClient = fqpClient.query(query, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to query: " + ex.getMessage());
        }
        // wait for the resource to go away
        try {
            LOG.debug("Sleeping " + timeoutMills + " to allow resource to expire.");
            Thread.sleep(timeoutMills);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Interrupted while sleeping " + timeoutMills + 
                " ms to allow resource to expire!");
        }
        // try out an operation on the resource
        try {
            LOG.debug("Checking resource");
            resultsClient.getResults();
            fail("Should have been unable to retrieve results on " +
                    "an expired resource, but succeded somehow.");
        } catch (Exception ex) {
            LOG.debug("Attempt to retrieve resource threw exception " +
                    "as expected of type " + ex.getClass().getSimpleName());
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
}
