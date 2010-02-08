package gov.nih.nci.cagrid.fqp.processor2;

import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.cql2.results.ExtendedCQLResult;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.data.dcql.results.DCQLResult;

/**
 * Simple util to aggregate a DCQLQueryResultsCollection
 * as a single CQLQueryResults instance
 * 
 * @author ervin
 */
public class DCQL2Aggregator {

    private DCQL2Aggregator() {
        // prevents instantiation
    }
    
    
    public static CQLQueryResults aggregateDCQLResults(
        DCQLQueryResultsCollection dcqlResults, String targetClassname) throws FederatedQueryProcessingException {
        // may have either object, attribute, aggregation, or extension results.  Not some combination of those.
        // if Aggregation results, must perform a final aggregation ourselves here
        List<CQLObjectResult> objectResults = new LinkedList<CQLObjectResult>();
        List<CQLAttributeResult> attributeResults = new LinkedList<CQLAttributeResult>();
        List<CQLAggregateResult> aggregateResults = new LinkedList<CQLAggregateResult>();
        List<ExtendedCQLResult> extendedResults = new LinkedList<ExtendedCQLResult>();
        for (DCQLResult result : dcqlResults.getDCQLResult()) {
            CQLQueryResults cqlResults = result.getCQLQueryResults();
            if (cqlResults.getObjectResult() != null) {
                Collections.addAll(objectResults, cqlResults.getObjectResult());
            }
            if (cqlResults.getAttributeResult() != null) {
                Collections.addAll(attributeResults, cqlResults.getAttributeResult());
            }
            if (cqlResults.getAggregationResult() != null) {
                Collections.addAll(aggregateResults, cqlResults.getAggregationResult());
            }
            if (cqlResults.getExtendedResult() != null) {
                // TODO: I think this is going to be an error... can only have one extended result in CQLQueryResults
                extendedResults.add(cqlResults.getExtendedResult());
            }
        }
        
        // generate the aggregate query results while checking 
        // only one of the results lists has anything in it
        CQLQueryResults aggregate = new CQLQueryResults();
        aggregate.setTargetClassname(targetClassname);
        boolean resultsFound = false;
        if (objectResults.size() != 0) {
            aggregate.setObjectResult(objectResults.toArray(new CQLObjectResult[0]));
            resultsFound = true;
        }
        if (attributeResults.size() != 0) {
            if (resultsFound) {
                throw new FederatedQueryProcessingException("Found more than one type of results during aggregation!");
            }
            aggregate.setAttributeResult(attributeResults.toArray(new CQLAttributeResult[0]));
            resultsFound = true;
        }
        if (aggregateResults.size() != 0) {
            if (resultsFound) {
                throw new FederatedQueryProcessingException("Found more than one type of results during aggregation!");
            }
            aggregate.setAggregationResult(combineAggregations(aggregateResults));
            resultsFound = true;
        }
        if (extendedResults.size() != 0) {
            if (resultsFound) {
                throw new FederatedQueryProcessingException("Found more than one type of results during aggregation!");
            }
            if (extendedResults.size() > 1) {
                throw new FederatedQueryProcessingException("Found multiple extended results; cannot aggregate!");
            }
            aggregate.setExtendedResult(extendedResults.get(0));
            resultsFound = true;
        }
        
        return aggregate;
    }
    
    
    private static CQLAggregateResult combineAggregations(List<CQLAggregateResult> aggregates) throws FederatedQueryProcessingException {
        CQLAggregateResult combined = new CQLAggregateResult();
        Aggregation operation = null;
        String attributeName = null;
        LinkedList<String> values = new LinkedList<String>();
        for (CQLAggregateResult agg : aggregates) {
            if (operation == null) {
                operation = agg.getAggregation();
            } else if (!operation.equals(agg.getAggregation())) {
                throw new FederatedQueryProcessingException("Aggregations of different types found in CQL aggregate results!");   
            }
            if (attributeName == null) {
                attributeName = agg.getAttributeName();
            } else if (!attributeName.equals(agg.getAttributeName())) {
                throw new FederatedQueryProcessingException("Aggregations on different attributes found in CQL aggregate results!");
            }
            values.add(agg.getValue());
        }
        combined.setAggregation(operation);
        combined.setAttributeName(attributeName);
        if (operation.equals(Aggregation.COUNT)) {
            long count = 0;
            for (String val : values) {
                count += Long.valueOf(val).longValue();
            }
            combined.setValue(String.valueOf(count));
        }
        if (operation.equals(Aggregation.MAX)) {
            Collections.sort(values);
            combined.setValue(values.getLast());
        }
        if (operation.equals(Aggregation.MIN)) {
            Collections.sort(values);
            combined.setValue(values.getFirst());
        }
        return combined;
    }
}
