package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axis.message.MessageElement;
import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLResult;
import org.cagrid.cql2.results.TargetAttribute;

public class CQL1ResultsIteratorToCQL2ResultsIterator implements Iterator<CQLResult> {

    private Iterator<?> cqlResultsIterator = null;
    private QName targetQName = null;
    private QueryModifier queryModifier = null;
        
    
    public CQL1ResultsIteratorToCQL2ResultsIterator(Iterator<?> resultsIterator, QName targetQName, QueryModifier queryModifier) {
        this.cqlResultsIterator = resultsIterator;
        this.targetQName = targetQName;
        this.queryModifier = queryModifier;
    }


    public boolean hasNext() {
        return cqlResultsIterator.hasNext();
    }


    public CQLResult next() {
        CQLResult result = null;
        if (queryModifier == null) {
            // object result
            MessageElement elem = new MessageElement(targetQName, cqlResultsIterator.next());
            result = new CQLObjectResult(new MessageElement[] {elem});            
        } else {
            if (queryModifier.isCountOnly()) {
                String value = String.valueOf(cqlResultsIterator.next());
                result = new CQLAggregateResult(Aggregation.COUNT, "id", value);
            } else {
                // distinct or multiple attribute results
                String[] attributeNames = queryModifier.getDistinctAttribute() != null 
                    ? new String[] {queryModifier.getDistinctAttribute()} : queryModifier.getAttributeNames();
                Object[] values = (Object[]) cqlResultsIterator.next();
                TargetAttribute[] tas = new TargetAttribute[attributeNames.length];
                for (int i = 0; i < attributeNames.length; i++) {
                    tas[i] = new TargetAttribute(attributeNames[i], String.valueOf(values[i]));
                }
                result = new CQLAttributeResult(tas);
            }
        }
        return result;
    }


    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
    }
}
