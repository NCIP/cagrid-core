package org.cagrid.tests.data.styles.cacore42.steps;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.utilities.CQL1toCQL2Converter;
import org.cagrid.cql.utilities.CQL2ResultsToCQL1ResultsConverter;
import org.cagrid.cql.utilities.CQL2SerializationUtil;
import org.cagrid.cql.utilities.QueryConversionException;
import org.cagrid.cql.utilities.ResultsConversionException;
import org.cagrid.data.test.creation.DataTestCaseInfo;

public class InvokeCql2DataServiceUsingConversionStep extends InvokeDataServiceStep {
    
    private static Log LOG = LogFactory.getLog(InvokeCql2DataServiceUsingConversionStep.class);

    public InvokeCql2DataServiceUsingConversionStep(DataTestCaseInfo testInfo, ServiceContainer container) {
        super(testInfo, container);
    }
    
    
    protected CQLQueryResults invokeValidQuery(CQLQuery query) {
        // convert to CQL 2
        org.cagrid.cql2.CQLQuery cql2 = null;
        try {
            cql2 = convertToCql2(query);
        } catch (QueryConversionException ex) {
            ex.printStackTrace();
            fail("Error converting CQL 2 to CQL 1: " + ex.getMessage());
        }
        DataServiceClient client = getServiceClient();
        org.cagrid.cql2.results.CQLQueryResults queryResults = null;
        boolean java6SdkFailure = false;
        try {
            queryResults = client.executeQuery(cql2);
            // If this fails, we need to still be able to cleanly exit
        } catch (Exception ex) {
            if (isJava6() && isSpringJava6Error(ex)) {
                // some of the datatypes don't play nice with JDK 6
                java6SdkFailure = true;
                LOG.info("Query failed due to caCORE SDK incompatibility with JDK 6", ex);
            } else {
                // that's a real failure
                ex.printStackTrace();
                fail("Query failed to execute: " + ex.getMessage());
            }
        }
        CQLQueryResults cql1results = null;
        if (!java6SdkFailure) {
            // convert CQL 2 results back to CQL 1
            try {
                cql1results = CQL2ResultsToCQL1ResultsConverter.convertResults(queryResults);
            } catch (ResultsConversionException ex) {
                ex.printStackTrace();
                fail("Error converting CQL 2 results to CQL 1 results: " + ex.getMessage());
            }
        }
        return cql1results;
    }
    
    
    protected void invokeInvalidQuery(CQLQuery query) {
        // convert the query to CQL 2
        org.cagrid.cql2.CQLQuery cql2 = null;
        try {
            cql2 = convertToCql2(query);
        } catch (QueryConversionException ex) {
            // since this is an invalid query, it's possible the query doesn't convert
            LOG.debug("Caught query conversion exception when converting invalid query; this might be expected", ex);
        }
        if (cql2 != null) {
            if (LOG.isDebugEnabled()) {
                try {
                    LOG.debug("Converted CQL 1 to CQL 2: " + CQL2SerializationUtil.serializeCql2Query(cql2));
                } catch (Exception ex) {
                    LOG.error("Error serializing CQL 2 for debug: " + ex.getMessage(), ex);
                }
            }
            // run the query, expect an exception
            DataServiceClient client = getServiceClient();
            try {
                client.executeQuery(cql2);
                fail("Query returned results, should have failed");
            } catch (Exception ex) {
                // expected
            }
        }
    }
    
    
    private org.cagrid.cql2.CQLQuery convertToCql2(CQLQuery query) throws QueryConversionException {
        DomainModel model = null;
        try {
            model = MetadataUtils.getDomainModel(getServiceClient().getEndpointReference());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error retrieving domain model: " + ex.getMessage());
        }
        CQL1toCQL2Converter converter = new CQL1toCQL2Converter(model);
        org.cagrid.cql2.CQLQuery cql2Query = converter.convertToCql2Query(query);
        return cql2Query;
    }
}
