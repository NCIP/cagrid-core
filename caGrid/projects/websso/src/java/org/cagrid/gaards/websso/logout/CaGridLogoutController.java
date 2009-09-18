package org.cagrid.gaards.websso.logout;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.websso.beans.WebSSOServerInformation;
import org.cagrid.gaards.websso.utils.WebSSOConstants;
import org.cagrid.gaards.websso.utils.WebSSOProperties;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.wsrf.encoding.DeserializationException;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.web.LogoutController;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class CaGridLogoutController extends AbstractController {
	LogoutController logoutController = new LogoutController();

	protected ModelAndView handleRequestInternal(
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		String delegationEPR = request
				.getParameter(WebSSOConstants.CAGRID_SSO_DELEGATION_SERVICE_EPR);
		if (delegationEPR != null && delegationEPR.trim().length() != 0) {
			WebApplicationContext ctx = WebApplicationContextUtils
					.getRequiredWebApplicationContext(this.getServletContext());
			WebSSOProperties webSSOProperties = (WebSSOProperties) ctx
					.getBean(WebSSOConstants.WEBSSO_PROPERTIES);
			WebSSOServerInformation webSSOServerInformation = webSSOProperties
					.getWebSSOServerInformation();
			GlobusCredential webSSOServerHostCredential;
			try {
				webSSOServerHostCredential = new GlobusCredential(
						webSSOServerInformation.getHostCredentialCertificateFilePath(),
						webSSOServerInformation.getHostCredentialKeyFilePath());
			} catch (GlobusCredentialException e) {
				throw new Exception(
						"Invalid Certificate and Key File in web-properties.xml. Error creating WebSSOServerHostCredential",e);
			}
			DelegatedCredentialReference delegatedCredentialReference = null;
			try {
				delegatedCredentialReference = (DelegatedCredentialReference) Utils
						.deserializeObject(
								new StringReader(delegationEPR),
								DelegatedCredentialReference.class,
								DelegationUserClient.class
										.getResourceAsStream("client-config.wsdd"));
			} catch (DeserializationException e) {
				throw new ServletException(
						"Unable to deserialize the Delegation Reference : "
								+ e.getMessage(), e);
			}
			DelegatedCredentialUserClient delegatedCredentialUserClient = null;
			try {
				delegatedCredentialUserClient = new DelegatedCredentialUserClient(
						delegatedCredentialReference,
						webSSOServerHostCredential);
			} catch (Exception e) {
				throw new ServletException(
						"Unable to Initialize the Delegation Lookup Client : "
								+ e.getMessage(), e);
			}
			GlobusCredential userCredential = delegatedCredentialUserClient
					.getDelegatedCredential();

			DelegatedCredentialUserClient delegatedCredentialUserClientForUser = new DelegatedCredentialUserClient(
					delegatedCredentialReference, userCredential);
			try {
				delegatedCredentialUserClientForUser.suspend();
			} catch (CDSInternalFault e) {
				throw new Exception(
						"Error retrieving the Delegated Credentials : "
								+ FaultUtil.printFaultToString(e));
			} catch (DelegationFault e) {
				throw new Exception(
						"Error retrieving the Delegated Credentials : "
								+ FaultUtil.printFaultToString(e));
			} catch (PermissionDeniedFault e) {
				throw new Exception(
						"Permission Denied to retrieve Delegated Credentials : "
								+ FaultUtil.printFaultToString(e));
			}

		}
		return logoutController.handleRequest(request, response);
	}

	public void setTicketGrantingTicketCookieGenerator(
			final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator) {
		logoutController
				.setTicketGrantingTicketCookieGenerator(ticketGrantingTicketCookieGenerator);
	}

	public void setWarnCookieGenerator(
			final CookieRetrievingCookieGenerator warnCookieGenerator) {
		logoutController.setWarnCookieGenerator(warnCookieGenerator);
	}

	/**
	 * @param centralAuthenticationService
	 *            The centralAuthenticationService to set.
	 */
	public void setCentralAuthenticationService(
			final CentralAuthenticationService centralAuthenticationService) {
		logoutController
				.setCentralAuthenticationService(centralAuthenticationService);
	}

	public void setFollowServiceRedirects(final boolean followServiceRedirects) {
		logoutController.setFollowServiceRedirects(followServiceRedirects);
	}

	public void setLogoutView(final String logoutView) {
		logoutController.setLogoutView(logoutView);
	}
}
