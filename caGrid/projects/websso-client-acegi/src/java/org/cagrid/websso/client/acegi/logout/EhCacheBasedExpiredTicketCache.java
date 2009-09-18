package org.cagrid.websso.client.acegi.logout;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.util.Assert;

public class EhCacheBasedExpiredTicketCache implements ExpiredTicketCache,
		InitializingBean {
	private Cache cache;

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.cache, "cache mandatory");
	}

	public boolean isTicketExpired(String serviceTicket) {
		Element element = null;

		try {
			element = this.cache.get(serviceTicket);
		} catch (CacheException cacheException) {
			throw new DataRetrievalFailureException("Cache failure: "+ cacheException.getMessage());
		}

		return (element != null);
	}

	public void putTicketInCache(String serviceTicket) {
		this.cache.put(new Element(serviceTicket, serviceTicket));
	}

	public void removeTicketFromCache(String serviceTicket) {
		this.cache.remove(serviceTicket);
	}
}