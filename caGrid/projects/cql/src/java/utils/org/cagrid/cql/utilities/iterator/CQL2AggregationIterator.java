package org.cagrid.cql.utilities.iterator;

import gov.nih.nci.cagrid.common.Utils;

import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;

public class CQL2AggregationIterator implements Iterator<Object> {
    
    public static final QName CQL2_AGGREGATE_RESULT_QNAME = 
        new QName("http://CQL.caBIG/2/org.cagrid.cql2.results", "CQLAggregateResult");
    
    private Iterator<Object> iter;
    
    CQL2AggregationIterator(CQLAggregateResult result, boolean xmlOnly) {
        Object item = null;
        if (xmlOnly) {
            StringWriter writer = new StringWriter();
            try {
                Utils.serializeObject(result, CQL2_AGGREGATE_RESULT_QNAME, writer);
            } catch (Exception ex) {
                throw new RuntimeException("Error serializing count result: " + ex.getMessage(), ex);
            }
            item = writer.getBuffer().toString();
        } else {
            // regular object result
            Aggregation agg = result.getAggregation();
            if (Aggregation.COUNT.equals(agg)) {
                item = Long.valueOf(result.getValue());
            } else if (Aggregation.MAX.equals(agg) || Aggregation.MIN.equals(agg)) {
                item = result.getValue();
            }
        }
        List<Object> tmp = Collections.singletonList(item);
        iter = tmp.iterator();
    }
    
    
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
    }
    
    
    public boolean hasNext() {
        return iter.hasNext();
    }
    
    
    public Object next() {
        return iter.next();
    }
}