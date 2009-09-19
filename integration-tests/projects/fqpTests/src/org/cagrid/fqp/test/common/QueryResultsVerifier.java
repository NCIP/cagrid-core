package org.cagrid.fqp.test.common;

import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.dcqlresult.DCQLResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;

public class QueryResultsVerifier extends Assert {

    public static void verifyDcqlResults(DCQLQueryResultsCollection test, DCQLQueryResultsCollection gold) {
        // check number of DCQL results
        assertEquals("Unexpected number of DCQL results", 
            gold.getDCQLResult().length, test.getDCQLResult().length);
        
        // pile the results all together as CQL query results
        CQLQueryResults testCqlResults = aggregateDcqlResults(test);
        CQLQueryResults goldCqlResults = aggregateDcqlResults(gold);
        
        // verify them as CQL results
        verifyCqlResults(testCqlResults, goldCqlResults);
    }
    
    
    public static void verifyCqlResults(CQLQueryResults test, CQLQueryResults gold) {
        // iterators to extract objects from the results
        CQLQueryResultsIterator testIter = new CQLQueryResultsIterator(
            test, QueryResultsVerifier.class.getResourceAsStream(FQPTestingConstants.CLIENT_WSDD));
        CQLQueryResultsIterator goldIter = new CQLQueryResultsIterator(
            gold, QueryResultsVerifier.class.getResourceAsStream(FQPTestingConstants.CLIENT_WSDD));
        
        // turn results into lists
        List<Object> testItems = new LinkedList<Object>();
        List<Object> goldItems = new LinkedList<Object>();
        while (testIter.hasNext()) {
            testItems.add(testIter.next());
        }
        while (goldIter.hasNext()) {
            goldItems.add(goldIter.next());
        }
        
        // compare
        assertEquals("Incorrect number of results", goldItems.size(), testItems.size());
        
        for (Object testObject : testItems) {
            assertTrue("Unexpected Test data object not found in gold data", goldItems.contains(testObject));
        }
        
        for (Object goldObject : testItems) {
            assertTrue("Expected Gold data object not found in test data", testItems.contains(goldObject));
        }
    }
    
    
    private static CQLQueryResults aggregateDcqlResults(DCQLQueryResultsCollection results) {
        CQLQueryResults cqlResults = new CQLQueryResults();
        List<CQLObjectResult> allObjectResults = new LinkedList<CQLObjectResult>();
        String targetName = null;
        for (DCQLResult result : results.getDCQLResult()) {
            CQLQueryResults singleCqlResult = result.getCQLQueryResultCollection();
            if (targetName == null) {
                targetName = singleCqlResult.getTargetClassname();
            } else {
                assertEquals("Unexpected result type found in DCQL", targetName, singleCqlResult.getTargetClassname());
            }
            CQLObjectResult[] objectResults = singleCqlResult.getObjectResult();
            assertNotNull("Data service " + result.getTargetServiceURL() + " returned non-object results!", objectResults);
            Collections.addAll(allObjectResults, objectResults);
        }
        
        // generate the aggregate query result
        CQLQueryResults aggregate = new CQLQueryResults();
        CQLObjectResult[] resultArray = new CQLObjectResult[allObjectResults.size()];
        allObjectResults.toArray(resultArray);
        aggregate.setObjectResult(resultArray);
        aggregate.setTargetClassname(targetName);
        return cqlResults;
    }
}
