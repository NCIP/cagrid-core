package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.common.FQPConstants;
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.fqp.execution.TargetDataServiceQueryBehavior;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;
import org.oasis.wsrf.faults.BaseFaultType;

public class MaxRetriesStep extends BaseQueryExecutionStep {
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    private static Log LOG = LogFactory.getLog(MaxRetriesStep.class);
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] serviceUrls;
    private int maxRetries;
    
    
    public MaxRetriesStep(String queryFilename, String goldFilename,
        FederatedQueryProcessorClient fqpClient, String[] serviceUrls, int maxRetries) {
        super(queryFilename, goldFilename);
        this.fqpClient = fqpClient;
        this.serviceUrls = serviceUrls;
        this.maxRetries = maxRetries;
    }
    

    public void runStep() throws Throwable {
        // get the query set up
        LOG.debug("Testing with query " + getQueryFilename());
        DCQLQuery query = getCompletedQuery();
        testParameterizedQuery(query);
    }
    
    
    private void testParameterizedQuery(DCQLQuery query) {
        // construct parameters
        QueryExecutionParameters params = new QueryExecutionParameters();
        TargetDataServiceQueryBehavior targetBehavior = new TargetDataServiceQueryBehavior();
        targetBehavior.setFailOnFirstError(Boolean.FALSE);
        targetBehavior.setRetries(Integer.valueOf(maxRetries));
        targetBehavior.setTimeoutPerRetry(FQPConstants.DEFAULT_TARGET_QUERY_BEHAVIOR.getTimeoutPerRetry());
        params.setTargetDataServiceQueryBehavior(targetBehavior);
        try {
            fqpClient.query(query, null, params);
            fail("Query accepted, despite " 
                + params.getTargetDataServiceQueryBehavior().getRetries() 
                + " retries setting");
        } catch (Exception ex) {
            // query processing exception expected, others not so much
            if (!(ex instanceof BaseFaultType && isFqpException((BaseFaultType) ex))) {
                ex.printStackTrace();
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
