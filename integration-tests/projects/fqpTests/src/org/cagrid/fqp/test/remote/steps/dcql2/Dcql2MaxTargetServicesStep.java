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
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.dcql2.BaseDcql2QueryExecutionStep;
import org.oasis.wsrf.faults.BaseFaultType;

public class Dcql2MaxTargetServicesStep extends BaseDcql2QueryExecutionStep {
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    private static Log LOG = LogFactory.getLog(Dcql2MaxTargetServicesStep.class);
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] serviceUrls;
    private int maxServices;
    
    
    public Dcql2MaxTargetServicesStep(String queryFilename, String goldFilename,
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
    }
    
    
    private void testParameterizedQuery(DCQLQuery query) {
        try {
            fqpClient.queryAsynchronously(query, null, null);
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
            fqpClient.executeQuery(query);
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
            fqpClient.executeQueryAndAggregate(query);
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
            fail("Unable to replace URL placeholders in DCQL 2 query: " + ex.getMessage());
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
