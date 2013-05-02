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
package org.cagrid.tools.events;

import java.util.Date;
import java.util.List;


public interface Auditor extends EventHandler {
    public List<Event> findEvents(String targetId, String reportingPartyId, String eventType, Date start, Date end, String message)
        throws EventAuditingException;
}
