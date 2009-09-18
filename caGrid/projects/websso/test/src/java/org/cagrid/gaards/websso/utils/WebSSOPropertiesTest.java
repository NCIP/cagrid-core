package org.cagrid.gaards.websso.utils;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.cagrid.gaards.websso.beans.CredentialDelegationServiceInformation;
import org.cagrid.gaards.websso.beans.DorianInformation;
import org.cagrid.gaards.websso.utils.WebSSOProperties;
import org.globus.gsi.GlobusCredential;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class WebSSOPropertiesTest extends
		AbstractDependencyInjectionSpringContextTests {

	protected String[] getConfigLocations() {
		return new String[] { "classpath:websso-beans.xml" };
	}

	private WebSSOProperties webSSOProperties;

	public void setWebSSOProperties(WebSSOProperties webSSOProperties) {
		this.webSSOProperties = webSSOProperties;
	}

	public void testValidateIssuedCredentialPathLength() {
		assertEquals(0, webSSOProperties.getCredentialDelegationServiceInformation().getIssuedCredentialPathLength());
	}
	
	public void testValidateDorianServiceIdentity() {
		DorianInformation dorianInformation = webSSOProperties
				.getDoriansInformation().get(0);
		assertNotNull(dorianInformation.getServiceIdentity());
	}

	public void testValidateCredentialServiceIdentity() {
		CredentialDelegationServiceInformation cdsInformation = webSSOProperties
				.getCredentialDelegationServiceInformation();
		assertNotNull(cdsInformation.getServiceIdentity());
	}

	private GlobusCredential getGlobusGridCredential() {
		try {
			Credential cred = new Credential();
			BasicAuthenticationCredential bac = new BasicAuthenticationCredential();
			bac.setUserId("dorian");
			bac.setPassword("DorianAdmin$1");
			cred.setBasicAuthenticationCredential(bac);

			AuthenticationClient authClient = new AuthenticationClient(
					"https://localhost:8443/wsrf/services/cagrid/Dorian", cred);
			SAMLAssertion saml = authClient.authenticate();
			ProxyLifetime lifetime = new ProxyLifetime();
			lifetime.setHours(12);
			int delegationLifetime = 0;

			IFSUserClient dorian = new IFSUserClient("https://localhost:8443/wsrf/services/cagrid/Dorian");
			GlobusCredential proxy = dorian.createProxy(saml, lifetime,delegationLifetime);
			return proxy;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}