/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;

import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.results.CQLQueryResults;

public class Cql2DataServiceImpl extends BaseDataServiceImpl {

    public Cql2DataServiceImpl() throws DataServiceInitializationException {
        super();
    }
    
    
    public CQLQueryResults executeQuery(CQLQuery query) throws QueryProcessingExceptionType, MalformedQueryExceptionType {
        CQLQueryResults results = null;
        try {
            results = processCql2Query(query);
        } catch (QueryProcessingException ex) {
            throw getTypedException(ex, new QueryProcessingExceptionType());
        } catch (MalformedQueryException ex) {
            throw getTypedException(ex, new MalformedQueryExceptionType());
        }
        return results;
    }
}
