package org.cagrid.websso.client.acegi.logout;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SingleSignoutCallbackFilter implements Filter, InitializingBean {
	private String filterProcessesUrl;
	private ExpiredTicketCache expiredTicketCache;

	public void setFilterProcessesUrl(String s) {
		this.filterProcessesUrl = s;
	}

	public void setExpiredTicketCache(ExpiredTicketCache cache) {
		this.expiredTicketCache = cache;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.hasLength(this.filterProcessesUrl,
				"filterProcessesUrl must be specified");
		Assert.notNull(this.expiredTicketCache, "cache mandatory");
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("Can only process HttpServletRequest");
		}
		if (!(response instanceof HttpServletResponse)) {
			throw new ServletException("Can only process HttpServletResponse");
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		if (processLogout(httpRequest)) {
			return;
		}
		chain.doFilter(request, response);
	}

	protected boolean processLogout(HttpServletRequest request)
			throws IOException {
		if (!request.getMethod().equalsIgnoreCase("POST")) {
			return false;
		}
		String uri = request.getRequestURI();

		// strip everything after the first semi-colon
		int pathParamIndex = uri.indexOf(';');
		if (pathParamIndex > 0) {
			uri = uri.substring(0, pathParamIndex);
		}
		if (!uri.endsWith(request.getContextPath() + this.filterProcessesUrl)) {
			return false;
		}
		String sTicket = null;
		BufferedReader reader = request.getReader();
		String line = null;
		while ((line = reader.readLine()) != null) {
			line = URLDecoder.decode(line, "UTF-8");
			if (line.startsWith("logoutRequest=")) {
				int start = line.indexOf("<samlp:SessionIndex>");
				int end = line.indexOf("</samlp:SessionIndex>");

				if (start > -1 && start < end) {
					sTicket = line.substring(start
							+ "<samlp:SessionIndex>".length(), end);
				}
			}
		}
		reader.close();
		if (sTicket != null) {
			this.expiredTicketCache.putTicketInCache(sTicket);
		}
		return true;
	}
}