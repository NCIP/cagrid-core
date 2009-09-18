package org.cagrid.websso.client.acegi.logout;

public interface ExpiredTicketCache {
	boolean isTicketExpired(String serviceTicket);

	void putTicketInCache(String serviceTicket);

	public void removeTicketFromCache(String serviceTicket);
}