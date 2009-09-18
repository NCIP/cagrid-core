package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.client.LocalUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.idp.Application;
import org.globus.gsi.GlobusCredential;

public class ChangeLocalUserPasswordStep extends Step {

	private Application user;
	private String serviceURL;
	private String newPassword;

	public ChangeLocalUserPasswordStep(String serviceURL, Application app,
			String newPassword) {
		this.serviceURL = serviceURL;
		this.user = app;
		this.newPassword = newPassword;
	}

	public void runStep() throws Throwable {
		BasicAuthentication ba = new BasicAuthentication();
		ba.setUserId(user.getUserId());
		ba.setPassword(user.getPassword());
		LocalUserClient client = new LocalUserClient(serviceURL);
		client.changePassword(ba, newPassword);
		ba.setPassword(newPassword);
		SAMLAssertion saml = client.authenticate(ba);
		CertificateLifetime lifetime = new CertificateLifetime();
		lifetime.setHours(12);
		GridUserClient client2 = new GridUserClient(this.serviceURL);
		GlobusCredential gridCredential = client2
				.requestUserCertificate(saml, lifetime);
		assertNotNull(gridCredential);
		gridCredential.verify();
	}

}
