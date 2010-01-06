package org.cagrid.cql.utilities;

import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.TargetAttribute;

/**
 * Utility to convert CQL 1 to CQL 2 query results
 * 
 * @author David
 */
public class CQL1ResultsToCQL2ResultsConverter {

    private CQL1ResultsToCQL2ResultsConverter() {
        // prevents instantiation
    }
    
    
    public static CQLQueryResults convertResults(gov.nih.nci.cagrid.cqlresultset.CQLQueryResults cqlResults) {
        CQLQueryResults newResults = new CQLQueryResults();
        newResults.setTargetClassname(cqlResults.getTargetClassname());
        if (cqlResults.getObjectResult() != null) {
            CQLObjectResult[] newObjs = new CQLObjectResult[cqlResults.getObjectResult().length];
            for (int i = 0; i < cqlResults.getObjectResult().length; i++) {
                newObjs[i] = convertObjectResult(cqlResults.getObjectResult(i));
            }
            newResults.setObjectResult(newObjs);
        } else if (cqlResults.getAttributeResult() != null) {
            CQLAttributeResult[] newATs = new CQLAttributeResult[cqlResults.getAttributeResult().length];
            for (int i = 0; i < cqlResults.getAttributeResult().length; i++) {
                newATs[i] = convertAttributeResult(cqlResults.getAttributeResult(i));
            }
            newResults.setAttributeResult(newATs);
        } else if (cqlResults.getCountResult() != null) {
            CQLAggregateResult newAgg = new CQLAggregateResult();
            newAgg.setAggregation(Aggregation.COUNT);
            newAgg.setAttributeName("id");
            newAgg.setValue(String.valueOf(cqlResults.getCountResult().getCount()));
            newResults.setAggregationResult(newAgg);
        }
        return newResults;
    }
    
    
    private static CQLObjectResult convertObjectResult(gov.nih.nci.cagrid.cqlresultset.CQLObjectResult oldObjectResult) {
        CQLObjectResult newObjectResult = new CQLObjectResult();
        newObjectResult.set_any(oldObjectResult.get_any());
        return newObjectResult;
    }
    
    
    private static CQLAttributeResult convertAttributeResult(gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult oldAttributeResult) {
        CQLAttributeResult newAttributeResult = new CQLAttributeResult();
        if (oldAttributeResult.getAttribute() != null) {
            TargetAttribute[] newTAs = new TargetAttribute[oldAttributeResult.getAttribute().length];
            for (int i = 0; i < oldAttributeResult.getAttribute().length; i++) {
                newTAs[i] = convertTargetAttribute(oldAttributeResult.getAttribute(i));
            }
            newAttributeResult.setAttribute(newTAs);
        }        
        return newAttributeResult;
    }
    
    
    private static TargetAttribute convertTargetAttribute(gov.nih.nci.cagrid.cqlresultset.TargetAttribute oldTa) {
        TargetAttribute newTa = new TargetAttribute(oldTa.getName(), oldTa.getValue());
        return newTa;
    }
}
