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
package org.cagrid.websso.client.acegi.logout;

public interface ExpiredTicketCache {
	boolean isTicketExpired(String serviceTicket);

	void putTicketInCache(String serviceTicket);

	public void removeTicketFromCache(String serviceTicket);
}
