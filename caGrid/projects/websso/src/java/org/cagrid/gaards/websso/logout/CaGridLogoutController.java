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
package org.cagrid.gaards.websso.logout;

import gov.nih.nci.cagrid.common.Utils;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.encoding.DeserializationException;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.jasig.cas.ticket.registry.TicketRegistry;
import org.jasig.cas.web.LogoutController;
import org.jasig.cas.web.support.CookieRetrievingCookieGenerator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.xml.sax.SAXException;

public class CaGridLogoutController extends AbstractController {
	
	private final Log log = LogFactory.getLog(getClass());
	private LogoutController logoutController = new LogoutController();
	
	private CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator;
	
    private TicketRegistry ticketRegistry;
	
	protected ModelAndView handleRequestInternal(
			final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {

		final String ticketGrantingTicketId = this.ticketGrantingTicketCookieGenerator
				.retrieveCookieValue(request);
		Ticket ticket = this.ticketRegistry.getTicket(ticketGrantingTicketId);
		final TicketGrantingTicket tgt = (TicketGrantingTicket) ticket;

		if (tgt != null) {
			org.jasig.cas.authentication.Authentication authentication = tgt
					.getAuthentication();
			Map<String, Object> userInfo = authentication.getPrincipal()
					.getAttributes();
			String delegationEPR = (String) userInfo
					.get(WebSSOConstants.CAGRID_SSO_DELEGATION_SERVICE_EPR);
			if (delegationEPR != null && delegationEPR.trim().length() != 0) {
				GlobusCredential webSSOServerHostCredential = extractGlobusCredential();
				DelegatedCredentialReference delegatedCredentialReference = extractDelegationCredentialReference(delegationEPR);
				DelegatedCredentialUserClient dcuc = getDelegatedUserCredentialUserClient(
						webSSOServerHostCredential,
						delegatedCredentialReference);
				suspendUserCredentials(delegatedCredentialReference, dcuc);
			}
		}
		return logoutController.handleRequest(request, response);
	}

	private DelegatedCredentialUserClient getDelegatedUserCredentialUserClient(
			GlobusCredential webSSOServerHostCredential,
			DelegatedCredentialReference delegatedCredentialReference)
			throws ServletException {
		DelegatedCredentialUserClient delegatedCredentialUserClient = null;
		try {
			delegatedCredentialUserClient = new DelegatedCredentialUserClient(
					delegatedCredentialReference, webSSOServerHostCredential);
		} catch (Exception e) {
			throw new ServletException(
					"Unable to Initialize the Delegation Lookup Client: " + e.getMessage(), e);
		}
		return delegatedCredentialUserClient;
	}
	
	private void suspendUserCredentials(
			DelegatedCredentialReference delegatedCredentialReference,
			DelegatedCredentialUserClient delegatedCredentialUserClient)
			throws RemoteException, CDSInternalFault, DelegationFault,
			PermissionDeniedFault, Exception, ResourceException {

		GlobusCredential userCredential = delegatedCredentialUserClient
				.getDelegatedCredential();
		DelegatedCredentialUserClient delegatedCredentialUserClientForUser = new DelegatedCredentialUserClient(
				delegatedCredentialReference, userCredential);
		try {
			delegatedCredentialUserClientForUser.suspend();
		} catch (CDSInternalFault e) {
			log.error(e);
			String faultString = e.getFaultString();
			throw new Exception(faultString, e);
		} catch (DelegationFault e) {
			log.error(e);
			String faultString = e.getFaultString();
			throw new Exception(faultString, e);
		} catch (PermissionDeniedFault e) {
			log.error(e);
			String faultString = e.getFaultString();
			throw new Exception(faultString, e);
		}
	}

	private DelegatedCredentialReference extractDelegationCredentialReference(
			String delegationEPR) throws SAXException, ServletException {
		DelegatedCredentialReference delegatedCredentialReference = null;
		try {
			delegatedCredentialReference = Utils
					.deserializeObject(new StringReader(delegationEPR),
							DelegatedCredentialReference.class,
							DelegationUserClient.class
									.getResourceAsStream("client-config.wsdd"));
		} catch (DeserializationException e) {
			log.error(e);
			throw new ServletException(
					"Unable to deserialize the Delegation Reference: "
							+ e.getMessage(), e);
		}
		return delegatedCredentialReference;
	}

	private GlobusCredential extractGlobusCredential() throws Exception {
		WebApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(this.getServletContext());
		WebSSOProperties webSSOProperties = (WebSSOProperties) ctx
				.getBean(WebSSOConstants.WEBSSO_PROPERTIES);
		WebSSOServerInformation webSSOServerInformation = webSSOProperties
				.getWebSSOServerInformation();
		GlobusCredential webSSOServerHostCredential;
		try {
			webSSOServerHostCredential = new GlobusCredential(
					webSSOServerInformation
							.getHostCredentialCertificateFilePath(),
					webSSOServerInformation.getHostCredentialKeyFilePath());
		} catch (GlobusCredentialException e) {
			log.error(e);
			throw new Exception(
					"Invalid Certificate and Key File in web-properties.xml. Error creating WebSSOServerHostCredential: "
							+ e.getMessage(), e);
		}
		return webSSOServerHostCredential;
	}

	public void setTicketGrantingTicketCookieGenerator(
			final CookieRetrievingCookieGenerator ticketGrantingTicketCookieGenerator) {

		this.ticketGrantingTicketCookieGenerator = ticketGrantingTicketCookieGenerator;
		logoutController
				.setTicketGrantingTicketCookieGenerator(ticketGrantingTicketCookieGenerator);
	}

	public void setWarnCookieGenerator(
			final CookieRetrievingCookieGenerator warnCookieGenerator) {
		logoutController.setWarnCookieGenerator(warnCookieGenerator);
	}

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

	public void setTicketRegistry(final TicketRegistry ticketRegistry) {
		this.ticketRegistry = ticketRegistry;
	}
}
