package org.cagrid.tests.data.styles.cacore42.steps;

import java.io.PrintWriter;

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
        org.cagrid.cql2.CQLQuery cql2 = convertToCql2(query);
        try {
            System.out.println("Converted CQL 1 to this CQL 2:");
            CQL2SerializationUtil.serializeCql2Query(cql2, new PrintWriter(System.out));
        } catch (Exception ex) {
            LOG.error("Error serializing CQL 2 for debug: " + ex.getMessage(), ex);
        }
        DataServiceClient client = getServiceClient();
        org.cagrid.cql2.results.CQLQueryResults queryResults = null;
        try {
            queryResults = client.executeQuery(cql2);
            // If this fails, we need to still be able to cleanly exit
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Query failed to execute: " + ex.getMessage());
        }
        // convert CQL 2 results back to CQL 1
        CQLQueryResults cql1results = null;
        try {
            cql1results = CQL2ResultsToCQL1ResultsConverter.convertResults(queryResults);
        } catch (ResultsConversionException ex) {
            ex.printStackTrace();
            fail("Error converting CQL 2 results to CQL 1 results: " + ex.getMessage());
        }
        return cql1results;
    }
    
    
    protected void invokeInvalidQuery(CQLQuery query) {
        // convert the query to CQL 2
        org.cagrid.cql2.CQLQuery cql2 = convertToCql2(query);
        try {
            System.out.println("Converted CQL 1 to this CQL 2:");
            CQL2SerializationUtil.serializeCql2Query(cql2, new PrintWriter(System.out));
        } catch (Exception ex) {
            LOG.error("Error serializing CQL 2 for debug: " + ex.getMessage(), ex);
        }
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
    
    
    private org.cagrid.cql2.CQLQuery convertToCql2(CQLQuery query) {
        DomainModel model = null;
        try {
            model = MetadataUtils.getDomainModel(getServiceClient().getEndpointReference());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error retrieving domain model: " + ex.getMessage());
        }
        CQL1toCQL2Converter converter = new CQL1toCQL2Converter(model);
        org.cagrid.cql2.CQLQuery cql2Query = null;
        try {
            cql2Query = converter.convertToCql2Query(query);
        } catch (QueryConversionException ex) {
            ex.printStackTrace();
            fail("Error converting CQL 1 to CQL 2 query: " + ex.getMessage());
        }
        return cql2Query;
    }
}
