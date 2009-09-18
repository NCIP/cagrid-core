package org.cagrid.websso.client.acegi.logout;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.cas.CasAuthenticationToken;
import org.acegisecurity.ui.logout.LogoutHandler;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class SingleSignoutFilter implements Filter, InitializingBean {
	private String filterProcessesUrl;
	private LogoutHandler[] logoutHandlers;
	private ExpiredTicketCache expiredTicketCache;
	private SingleSignoutHelper webssoLogoutHelper;

	/**
	 * The "magic" URL that triggers a CAS logout
	 */
	public void setFilterProcessesUrl(String s) {
		this.filterProcessesUrl = s;
	}

	/**
	 * Logout handlers that clean up after logout
	 */
	public void setLogoutHandlers(LogoutHandler[] handlers) {
		this.logoutHandlers = handlers;
	}

	/**
	 * The store of expired tickets received from CAS
	 */
	public void setExpiredTicketCache(ExpiredTicketCache cache) {
		this.expiredTicketCache = cache;
	}
	
	public void setWebssoLogoutHelper(SingleSignoutHelper webssoLogoutHelper) {
		this.webssoLogoutHelper = webssoLogoutHelper;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.hasText(this.filterProcessesUrl, "filterProcessesUrl required");
		Assert.notEmpty(this.logoutHandlers, "logoutHandlers are required");
		Assert.notNull(this.webssoLogoutHelper, "webssoLogoutHelper required");
	}

	public void init(FilterConfig config) throws ServletException {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("Can only process HttpServletRequest");
		}

		if (!(response instanceof HttpServletResponse)) {
			throw new ServletException("Can only process HttpServletResponse");
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		boolean loggedOut = false;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// has the authentication's ticket expired because of a CAS logout
		// initiated from another webapp?
		if (auth instanceof CasAuthenticationToken && this.expiredTicketCache != null) {
			String serviceTicket = auth.getCredentials().toString();

			if (this.expiredTicketCache.isTicketExpired(serviceTicket)) {
				for (int i = 0; i < this.logoutHandlers.length; i++) {
					this.logoutHandlers[i].logout(httpRequest, httpResponse,auth);
				}

				this.expiredTicketCache.removeTicketFromCache(serviceTicket);

				loggedOut = true;
			}
		}

		// is the user explicitly requesting logout?
		if (requiresLogout(httpRequest)) {
			String logoutURL=webssoLogoutHelper.getLogoutURL();
			if (loggedOut == false) {
				// we haven't called the logout handlers above, so do so now
				for (int i = 0; i < this.logoutHandlers.length; i++) {
					this.logoutHandlers[i].logout(httpRequest, httpResponse,
							auth);
				}
			}
			// have browser tell CAS to log out
			httpResponse.sendRedirect(logoutURL);
			return;
		}
		chain.doFilter(request, response);
	}

	protected boolean requiresLogout(HttpServletRequest request) {
		String uri = request.getRequestURI();
		// strip everything after the first semi-colon
		int pathParamIndex = uri.indexOf(';');
		if (pathParamIndex > 0) {
			uri = uri.substring(0, pathParamIndex);
		}
		return uri.endsWith(request.getContextPath() + this.filterProcessesUrl);
	}
}