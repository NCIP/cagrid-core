package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;
import org.oasis.wsrf.faults.BaseFaultType;

public class MaxTargetServicesStep extends BaseQueryExecutionStep {
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    private static Log LOG = LogFactory.getLog(MaxTargetServicesStep.class);
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] serviceUrls;
    private int maxServices;
    
    
    public MaxTargetServicesStep(String queryFilename, String goldFilename,
        FederatedQueryProcessorClient fqpClient, String[] serviceUrls, int maxServices) {
        super(queryFilename, goldFilename);
        this.fqpClient = fqpClient;
        this.serviceUrls = serviceUrls;
        this.maxServices = maxServices;
    }
    

    public void runStep() throws Throwable {
        // get the query set up
        LOG.debug("Testing with query " + getQueryFilename());
        DCQLQuery query = getCompletedQuery();
        testParameterizedQuery(query);
        testStandardQuery(query);
        testAggregateQuery(query);
        testAsynchronousQuery(query);
    }
    
    
    private void testParameterizedQuery(DCQLQuery query) {
        try {
            fqpClient.query(query, null, null);
            fail("Query accepted, despite " + query.getTargetServiceURL().length 
                + " target service URLs");
        } catch (Exception ex) {
            // query processing exception expected, others not so much
            if (!(ex instanceof BaseFaultType && isFqpException((BaseFaultType) ex))) {
                fail("Unexpected exception type caught: " + ex.getClass().getSimpleName());
            }
        }
    }
    
    
    private boolean isFqpException(BaseFaultType fault) {
        if (fault instanceof FederatedQueryProcessingFault) {
            return true;
        }
        for (BaseFaultType cause : fault.getFaultCause()) {
            if (isFqpException(cause)) {
                return true;
            }
        }
        return false;
    }
    
    
    private void testStandardQuery(DCQLQuery query) {
        try {
            fqpClient.execute(query);
            fail("Query accepted, despite " + query.getTargetServiceURL().length 
                + " target service URLs");
        } catch (Exception ex) {
            // query processing exception expected, others not so much
            if (!(ex instanceof BaseFaultType && isFqpException((BaseFaultType) ex))) {
                fail("Unexpected exception type caught: " + ex.getClass().getSimpleName());
            }
        }
    }
    
    
    private void testAggregateQuery(DCQLQuery query) {
        try {
            fqpClient.executeAndAggregateResults(query);
            fail("Query accepted, despite " + query.getTargetServiceURL().length 
                + " target service URLs");
        } catch (Exception ex) {
            // query processing exception expected, others not so much
            if (!(ex instanceof BaseFaultType && isFqpException((BaseFaultType) ex))) {
                fail("Unexpected exception type caught: " + ex.getClass().getSimpleName());
            }
        }
    }
    
    
    private void testAsynchronousQuery(DCQLQuery query) {
        try {
            fqpClient.executeAsynchronously(query);
            fail("Query accepted, despite " + query.getTargetServiceURL().length 
                + " target service URLs");
        } catch (Exception ex) {
            // query processing exception expected, others not so much
            if (!(ex instanceof BaseFaultType && isFqpException((BaseFaultType) ex))) {
                fail("Unexpected exception type caught: " + ex.getClass().getSimpleName());
            }
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
        // pump up number of target services
        List<String> targetUrls = new ArrayList<String>();
        Collections.addAll(targetUrls, replaced.getTargetServiceURL());
        int extraServicesCount = (maxServices - replaced.getTargetServiceURL().length) + 1;
        for (int i = 0; i < extraServicesCount; i++) {
            String url = serviceUrls[0] + "_" + i;
            targetUrls.add(url);
        }
        String[] urls = new String[targetUrls.size()];
        targetUrls.toArray(urls);
        replaced.setTargetServiceURL(urls);
        return replaced;
    }
}
