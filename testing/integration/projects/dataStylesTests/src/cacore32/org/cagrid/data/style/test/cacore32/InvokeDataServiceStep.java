package org.cagrid.data.style.test.cacore32;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileReader;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.data.test.creation.DataTestCaseInfo;

public class InvokeDataServiceStep extends Step {
    
    private DataTestCaseInfo testInfo = null;
    private ServiceContainer container = null;
    
    public InvokeDataServiceStep(DataTestCaseInfo testInfo, ServiceContainer container) {
        this.testInfo = testInfo;
        this.container = container;
    }
    
    
    public void runStep() throws Throwable {
        // get the service URL
        EndpointReferenceType serviceEPR = container.getServiceEPR("cagrid/" + testInfo.getName());
        DataServiceClient client = new DataServiceClient(serviceEPR);
        executeQuery(client, "attributePredicates.xml");
        executeQuery(client, "countAllOfType.xml");
        // TODO: validate results
    }
    
    
    private CQLQueryResults executeQuery(DataServiceClient client, String queryName) {
        CQLQueryResults results = null;
        CQLQuery query = getQuery(queryName);
        try {
            results = client.query(query);
        } catch (MalformedQueryExceptionType ex) {
            ex.printStackTrace();
            fail("Query is not well formed: " + ex.getMessage());
        } catch (QueryProcessingExceptionType ex) {
            ex.printStackTrace();
            fail("Query processing error on server side: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error while processing the query: " + ex.getMessage());
        }
        return results;
    }
    
    
    private CQLQuery getQuery(String name) {
        CQLQuery query = null;
        File queryFile = new File(Sdk32TestConstants.QUERIES_DIR, name);
        assertTrue("Query file " + queryFile.getAbsolutePath() + " not found", queryFile.exists());
        try {
            FileReader reader = new FileReader(queryFile);
            query = (CQLQuery) Utils.deserializeObject(reader, CQLQuery.class);
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing test query: " + ex.getMessage());
        }
        return query;
    }
}
