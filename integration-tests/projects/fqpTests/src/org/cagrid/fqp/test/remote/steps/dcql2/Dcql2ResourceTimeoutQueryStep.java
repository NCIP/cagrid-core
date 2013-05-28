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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.dcql2.BaseDcql2QueryExecutionStep;

public class Dcql2ResourceTimeoutQueryStep extends BaseDcql2QueryExecutionStep {
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    private static Log LOG = LogFactory.getLog(Dcql2ResourceTimeoutQueryStep.class);
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] serviceUrls;
    private long timeoutMills;
    
    
    public Dcql2ResourceTimeoutQueryStep(String queryFilename, String goldFilename,
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
        FederatedQueryResultsRetrievalClient resultsClient = null;
        try {
            resultsClient = fqpClient.queryAsynchronously(query, null, null);
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
            fail("Unable to replace URL placeholders in DCQL 2 query: " + ex.getMessage());
        }
        return replaced;
    }
}
