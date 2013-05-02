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
import gov.nih.nci.cagrid.data.QueryProcessingException;

/** 
 *  QueryProcessingFailedAuditingEvent
 *  Auditing event fired when the query processor throws a query
 *  processing exception while handling a CQL Query
 * 
 * @author David Ervin
 * 
 * @created May 17, 2007 1:38:10 PM
 * @version $Id: QueryProcessingFailedAuditingEvent.java,v 1.2 2007-05-18 16:04:01 dervin Exp $ 
 */
public class QueryProcessingFailedAuditingEvent extends BaseAuditingEvent {
    
    private QueryProcessingException qpException;

    /**
     * @param query
     * @param callerId
     * @param qpException
     */
    public QueryProcessingFailedAuditingEvent(CQLQuery query, String callerId, 
        QueryProcessingException qpException) {
        super(query, callerId);
        this.qpException = qpException;
    }
    
    
    public QueryProcessingException getQueryProcessingException() {
        return qpException;
    }
}
