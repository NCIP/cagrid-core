package gov.nih.nci.cagrid.data.service.auditing;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;


/** 
 *  BaseAuditingEvent
 *  Base class for data service auditing events
 * 
 * @author David Ervin
 * 
 * @created May 17, 2007 11:02:51 AM
 * @version $Id: BaseAuditingEvent.java,v 1.2 2007-05-18 16:04:01 dervin Exp $ 
 */
public abstract class BaseAuditingEvent {

    private CQLQuery query;
    private String callerId;
    
    public BaseAuditingEvent(CQLQuery query, String callerId) {
        this.query = query;
        this.callerId = callerId;
    }
    
    
    public CQLQuery getQuery() {
        return query;
    }
    
    
    public String getCallerId() {
        return callerId;
    }
}
