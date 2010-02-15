package gov.nih.nci.cagrid.fqp.processor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.dcqlresult.DCQLResult;

/**
 * Simple util to aggregate a DCQLQueryResultsCollection
 * as a single CQLQueryResults instance
 * 
 * @deprecated
 * @author ervin
 */
public class DCQLAggregator {

    private DCQLAggregator() {
        // prevents instantiation
    }
    
    
    public static CQLQueryResults aggregateDCQLResults(
        DCQLQueryResultsCollection dcqlResults, String targetClassname) {
        List<CQLObjectResult> objectResults = new LinkedList<CQLObjectResult>();
        for (DCQLResult result : dcqlResults.getDCQLResult()) {
            CQLObjectResult[] objects = result.getCQLQueryResultCollection().getObjectResult();
            if (objects != null && objects.length != 0) {
                Collections.addAll(objectResults, objects);
            }
        }
        
        // generate the aggregate query result
        CQLQueryResults aggregate = new CQLQueryResults();
        CQLObjectResult[] resultArray = new CQLObjectResult[objectResults.size()];
        objectResults.toArray(resultArray);
        aggregate.setObjectResult(resultArray);
        aggregate.setTargetClassname(targetClassname);
        
        return aggregate;
    }
}
