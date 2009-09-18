/*
 * Created on Jul 14, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nci.nih.cagrid.tests.core.Constants;
import gov.nci.nih.cagrid.tests.core.GridCredential;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.globus.gsi.GlobusCredential;

/**
 * This step authenticates a user with dorian and saves the proxy as the globus
 * default proxy.
 * 
 * @author Patrick McConnell
 */
public class DorianAuthenticateStep extends Step implements GridCredential {
	private String userId;
	private String password;
	private String serviceURL;
	private int hours;
	private SAMLAssertion saml;
	private GlobusCredential credential;

	public DorianAuthenticateStep(String serviceURL) {
		this("dorian", Constants.DORIAN_ADMIN_PASSWORD, serviceURL);
	}

	public DorianAuthenticateStep(String userId, String password,
			String serviceURL) {
		this(userId, password, serviceURL, 12);
	}

	public DorianAuthenticateStep(String userId, String password,
			String serviceURL, int hours) {
		super();
		this.userId = userId;
		this.password = password;
		this.serviceURL = serviceURL;
		this.hours = hours;
	}

	@Override
	public void runStep() throws Throwable {
		BasicAuthentication authCred = new BasicAuthentication();
		authCred.setUserId(this.userId);
		authCred.setPassword(this.password);
		AuthenticationClient client = new AuthenticationClient(this.serviceURL);
		this.saml = client.authenticate(authCred);

		GridUserClient c2 = new GridUserClient(this.serviceURL);
		this.credential = c2.requestUserCertificate(this.saml, new CertificateLifetime(
				this.hours, 0, 0));
		ProxyUtil.saveProxyAsDefault(this.credential);
	}

	public GlobusCredential getCredential() {
		return this.credential;
	}

	public void setCredential(GlobusCredential credential) {
		this.credential = credential;
	}

	public SAMLAssertion getSaml() {
		return this.saml;
	}

	public void setSaml(SAMLAssertion saml) {
		this.saml = saml;
	}
}
