package org.cagrid.websso.client.filter;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cagrid.websso.common.WebSSOConstants;
import org.cagrid.websso.common.WebSSOClientHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class CaGridWebSSOLogoutFilter implements Filter {

	private static final String CAS_CLIENT_PROPERTY_FILE = "cas-client-property-file";

	private String casClientPropertyFile;
	
	public void destroy() {
		// do nothing
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) request).getSession();
		Boolean isSessionLoaded = (Boolean) session.getAttribute(WebSSOConstants.IS_SESSION_ATTRIBUTES_LOADED);
		if (null == isSessionLoaded || isSessionLoaded == Boolean.FALSE) {
			throw new ServletException("WebSSO Attributes are not loaded in the Session");
		} else {
			String delegationEPR = (String) session.getAttribute(WebSSOConstants.CAGRID_SSO_DELEGATION_SERVICE_EPR);
			String logoutURL = WebSSOClientHelper.getLogoutURL(getCasClientPropertyResource(),delegationEPR);
			session.invalidate();
			((HttpServletResponse) response).sendRedirect(logoutURL);
		}
	}
	
	private Properties getCasClientPropertyResource(){
		Properties properties = new Properties();
		try {
			Resource classPathResource=new ClassPathResource(casClientPropertyFile);
			properties.load(classPathResource.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(casClientPropertyFile +" not found in WEB-INF/classes folder ", e);
		}
		return properties;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.casClientPropertyFile = filterConfig.getInitParameter(CAS_CLIENT_PROPERTY_FILE);
		Assert.notNull(
						casClientPropertyFile,"init-param value for cas-client-property-file was not specified for caGRID WebSSO Logout Filter in web.xml ");
	}
}
