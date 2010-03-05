package gov.nih.nci.cagrid.data.enumeration.service;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.cagrid.cql.utilities.CQLConstants;
import org.cagrid.cql2.results.CQLObjectResult;
import org.cagrid.cql2.results.CQLResult;

/**
 * CQL2ResultsTypeDeterminingIterator
 * Iterates CQL 2 Result instances and determines the QName
 * of the result type (i.e. object, attribute, etc).
 * 
 * This is generally for use with enumeration where the result type cannot be known;
 * since CQL extensions may cause any result type to be returned, the query modifier
 * is not a reliable source of information to determine the result type
 * 
 * @author David
 */
public class CQL2ResultsTypeDeterminingIterator implements Iterator<CQLResult> {
    
    private Iterator<CQLResult> realIterator = null;
    private CQLResult firstResult = null;
    boolean triedFirstResult = false;
    boolean returnedFirstResult = false;
    
    public CQL2ResultsTypeDeterminingIterator(Iterator<CQLResult> realIterator) {
        this.realIterator = realIterator;
    }
    

    public boolean hasNext() {
        return realIterator.hasNext();
    }

    
    public CQLResult next() {
        CQLResult item = null;
        if (returnedFirstResult) {
            item = realIterator.next();
        } else {
            item = getFirstResult();
            returnedFirstResult = true;
        }
        return item;
    }

    
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
    
    
    public QName getResultQName() {
        QName name = null;
        CQLResult first = getFirstResult();
        if (first == null) {
            // default to object results
            name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(CQLObjectResult.class);
        } else {
            name = CQLConstants.CQL_RESULT_ELEMENT_QNAMES.get(first.getClass());
        }
        return name;
    }
    
    
    private CQLResult getFirstResult() {
        if (!triedFirstResult) {
            if (realIterator.hasNext()) {
                firstResult = realIterator.next();
            }
            triedFirstResult = true;
        }
        return firstResult;
    }
}