package org.cagrid.cql.utilities;

import org.apache.axis.message.MessageElement;
import org.cagrid.cql2.Aggregation;

import gov.nih.nci.cagrid.cqlresultset.CQLAttributeResult;
import gov.nih.nci.cagrid.cqlresultset.CQLCountResult;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.cqlresultset.TargetAttribute;

public class CQL2ResultsToCQL1ResultsConverter {

    private CQL2ResultsToCQL1ResultsConverter() {
        // no instantiation
    }
    
    
    public static CQLQueryResults convertResults(org.cagrid.cql2.results.CQLQueryResults newResults) throws ResultsConversionException {
        CQLQueryResults oldResults = new CQLQueryResults();
        oldResults.setTargetClassname(newResults.getTargetClassname());
        if (newResults.getObjectResult() != null) {
            CQLObjectResult[] oldObjs = new CQLObjectResult[newResults.getObjectResult().length];
            for (int i = 0; i < newResults.getObjectResult().length; i++) {
                oldObjs[i] = convertObjectResult(newResults.getObjectResult(i));
            }
            oldResults.setObjectResult(oldObjs);
        } else if (newResults.getAttributeResult() != null) {
            CQLAttributeResult[] oldAtts = new CQLAttributeResult[newResults.getAttributeResult().length];
            for (int i = 0; i < newResults.getAttributeResult().length; i++) {
                oldAtts[i] = convertAttribute(newResults.getAttributeResult(i));
            }
            oldResults.setAttributeResult(oldAtts);
        } else if (newResults.getAggregationResult() != null) {
            if (newResults.getAggregationResult().getAggregation().equals(Aggregation.COUNT)) {
                oldResults.setCountResult(new CQLCountResult(
                    Long.parseLong(newResults.getAggregationResult().getValue())));
            } else {
                throw new ResultsConversionException("No conversion for CQL 2 Aggregation " 
                    + newResults.getAggregationResult().getAggregation().getValue());
            }
        }
        return oldResults;
    }
    
    
    private static CQLObjectResult convertObjectResult(org.cagrid.cql2.results.CQLObjectResult newObject) throws ResultsConversionException {
        CQLObjectResult oldObject = new CQLObjectResult();
        MessageElement elem = newObject.get_any()[0];
        // TODO: verify no associations populated        
        oldObject.set_any(new MessageElement[] {elem});
        return oldObject;
    }
    
    
    private static CQLAttributeResult convertAttribute(org.cagrid.cql2.results.CQLAttributeResult newAtt) {
        CQLAttributeResult oldAtt = new CQLAttributeResult();
        TargetAttribute[] oldTas = new TargetAttribute[newAtt.getAttribute().length];
        for (int i = 0; i < newAtt.getAttribute().length; i++) {
            oldTas[i] = convertTargetAttribute(newAtt.getAttribute(i));
        }
        oldAtt.setAttribute(oldTas);
        return oldAtt;
    }
    
    
    private static TargetAttribute convertTargetAttribute(org.cagrid.cql2.results.TargetAttribute newTa) {
        TargetAttribute oldTa = new TargetAttribute();
        oldTa.setName(newTa.getName());
        oldTa.setValue(newTa.getValue());
        return oldTa;
    }
}
