package org.cagrid.gaards.websso.utils;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.cagrid.gaards.dorian.client.DorianClient;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.federation.ProxyLifetime;
import org.cagrid.gaards.websso.beans.CredentialDelegationServiceInformation;
import org.cagrid.gaards.websso.beans.DorianInformation;
import org.globus.gsi.GlobusCredential;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class WebSSOPropertiesTest extends
		AbstractDependencyInjectionSpringContextTests {

	protected String[] getConfigLocations() {
		return new String[] { "classpath:/WEB-INF/websso-beans.xml" };
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
			CertificateLifetime lifetime = new CertificateLifetime(12, 0, 0);

			GridUserClient dorianClient = new GridUserClient("https://localhost:8443/wsrf/services/cagrid/Dorian");
			GlobusCredential proxy = dorianClient.requestUserCertificate(saml, lifetime);
			return proxy;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}