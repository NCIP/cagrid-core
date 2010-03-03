package org.cagrid.fqp.test.common.steps.dcql2;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.fqp.test.common.FederatedQueryProcessorHelper;
import org.cagrid.fqp.test.common.QueryResultsVerifier;
import org.cagrid.fqp.test.common.UrlReplacer;

public class Dcql2StandardQueryStep extends BaseDcql2QueryExecutionStep {
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    private static Log LOG = LogFactory.getLog(Dcql2StandardQueryStep.class);
    
    private FederatedQueryProcessorHelper fqpHelper;
    private String[] serviceUrls;
    
    
    public Dcql2StandardQueryStep(String queryFilename, String goldFilename,
        FederatedQueryProcessorHelper fqpHelper, String[] serviceUrls) {
        super(queryFilename, goldFilename);
        this.fqpHelper = fqpHelper;
        this.serviceUrls = serviceUrls;
    }
    

    public void runStep() throws Throwable {
        LOG.debug("Testing with query " + getQueryFilename());
        DCQLQuery query = getCompletedQuery();
        DCQLQueryResultsCollection result = fqpHelper.executeQuery(query);
        LOG.debug("Verifying against " + getGoldFilenname());
        DCQLQueryResultsCollection gold = loadGoldDcqlResults();
        QueryResultsVerifier.verifyDcql2Results(result, gold);
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
