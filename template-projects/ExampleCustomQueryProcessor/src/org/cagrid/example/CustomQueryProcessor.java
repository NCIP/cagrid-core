/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*    Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.example;

import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;

import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.results.CQLQueryResults;

public class CustomQueryProcessor extends CQL2QueryProcessor {


    @Override
    public CQLQueryResults processQuery(CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    /*
     * You might want to implement this method if you want a memory-efficient implementation that can
     * iterate a stream of results rather than having to create the full result set at once.
     * 
     * This is more important for cases using WS-Enumeration or caGrid Transfer
    public Iterator<CQLResult> processQueryAndIterate(CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        CQLQueryResults results = processQuery(query);
        return new ResultsIterator(results);
    }
     */
}
