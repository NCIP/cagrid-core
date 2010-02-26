package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.QueryModifier;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.cagrid.cql2.Aggregation;
import org.cagrid.cql2.results.CQLAggregateResult;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLResult;
import org.cagrid.cql2.results.TargetAttribute;
import org.exolab.castor.types.AnyNode;

public class CQL1ResultsIteratorToCQL2ResultsIterator implements Iterator<CQLResult> {

    private Iterator<?> cqlResultsIterator = null;
    private QName targetQName = null;
    private QueryModifier queryModifier = null;
    private ByteArrayInputStream wsddStream = null;
    
    public CQL1ResultsIteratorToCQL2ResultsIterator(Iterator<?> resultsIterator, QName targetQName, 
        QueryModifier queryModifier) {
        this.cqlResultsIterator = resultsIterator;
        this.targetQName = targetQName;
        this.queryModifier = queryModifier;
    }
    
    
    public CQL1ResultsIteratorToCQL2ResultsIterator(Iterator<?> resultsIterator, QName targetQName, 
        QueryModifier queryModifier, InputStream wsddStream) throws IOException {
        this.cqlResultsIterator = resultsIterator;
        this.targetQName = targetQName;
        this.queryModifier = queryModifier;
        this.wsddStream = inputStreamToByteArrayStream(wsddStream);
    }
    
    
    private ByteArrayInputStream inputStreamToByteArrayStream(InputStream stream) throws IOException {
        byte[] bytes = Utils.inputStreamToStringBuffer(stream).toString().getBytes();
        return new ByteArrayInputStream(bytes);
    }


    public boolean hasNext() {
        return cqlResultsIterator.hasNext();
    }


    public CQLResult next() {
        CQLResult result = null;
        if (queryModifier == null) {
            // object result
            Object instance = cqlResultsIterator.next();
            try {
                // serialize the object so it can go into an AnyNode
                StringWriter xmlWriter = new StringWriter();
                if (wsddStream == null) {
                    Utils.serializeObject(instance, targetQName, xmlWriter);
                } else {
                    wsddStream.reset();
                    Utils.serializeObject(instance, targetQName, xmlWriter, wsddStream);
                }
                AnyNode node = AnyNodeHelper.convertStringToAnyNode(xmlWriter.toString());
                result = new CQLObjectResult(node);
            } catch (Exception ex) {
                NoSuchElementException nse = new NoSuchElementException("Unable to convert object to AnyNode: " + ex.getMessage());
                nse.initCause(ex);
                throw nse;
            }
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
