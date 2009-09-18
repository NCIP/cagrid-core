package org.cagrid.websso.client.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.cagrid.websso.common.WebSSOConstants;
import org.cagrid.websso.common.WebSSOClientHelper;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;

public class CaGridWebSSOAttributeLoaderFilter implements Filter {

	public void destroy() {
		// do nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response,FilterChain filterChain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		Boolean isSessionLoaded = (Boolean) session.getAttribute(WebSSOConstants.IS_SESSION_ATTRIBUTES_LOADED);
		if (null == isSessionLoaded || isSessionLoaded == Boolean.FALSE) {
			Assertion assertion = (Assertion) session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
			AttributePrincipal attributePrincipal = assertion.getPrincipal();
			String attributesString = attributePrincipal.getName();
			loadSessionAttributes(attributesString,session);
			session.setAttribute(WebSSOConstants.IS_SESSION_ATTRIBUTES_LOADED, Boolean.TRUE);			
		}
		filterChain.doFilter(request, response);
	}

	private static void loadSessionAttributes(String attributesString,HttpSession session){
		Map<String, String> userAttributesMap = WebSSOClientHelper.getUserAttributes(attributesString);
		Iterator<String> iterator = userAttributesMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = userAttributesMap.get(key);
			session.setAttribute(key, value);
		}
	}
	
	public void init(FilterConfig arg0) throws ServletException {
		// do nothing
	}
}