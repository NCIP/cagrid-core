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
