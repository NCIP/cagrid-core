package org.cagrid.tools.events;

import java.util.Date;
import java.util.List;


public interface Auditor extends EventHandler {
    public List<Event> findEvents(String targetId, String reportingPartyId, String eventType, Date start, Date end, String message)
        throws EventAuditingException;
}
