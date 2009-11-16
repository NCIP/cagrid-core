package gov.nih.nci.cagrid.data.cql2;

import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;

import java.util.Iterator;

import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.results.CQLResult;

/**
 * CQL 2 Query Processor base class which can optionally return an Iterator over the result set.
 * This iterator may prove beneficial for certain implementations when WS-Enumeration,
 * caGrid Transfer, etc. is in use.
 * 
 * @author David
 */
public abstract class LazyCQL2QueryProcessor extends CQL2QueryProcessor {

    public LazyCQL2QueryProcessor() {
        super();
    }
    
    
    /**
     * Returns an iterator over the CQL results
     * 
     * @param query
     * @return
     * @throws QueryProcessingException
     * @throws MalformedQueryException
     */
    public abstract Iterator<CQLResult> processQueryLazy(CQLQuery query) throws QueryProcessingException, MalformedQueryException;
}
