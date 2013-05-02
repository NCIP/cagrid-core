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
package gov.nih.nci.cagrid.data.service.auditing;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;

/** 
 *  QueryResultsAuditingEvent
 *  Auditing event fired when query results are produced
 * 
 * @author David Ervin
 * 
 * @created May 17, 2007 3:41:22 PM
 * @version $Id: QueryResultsAuditingEvent.java,v 1.2 2007-05-18 16:04:01 dervin Exp $ 
 */
public class QueryResultsAuditingEvent extends BaseAuditingEvent {

    private CQLQueryResults results;
    
    public QueryResultsAuditingEvent(CQLQuery query, String callerId, CQLQueryResults results) {
        super(query, callerId);
        this.results = results;
    }

    
    public CQLQueryResults getResults() {
        return results;
    }
}
