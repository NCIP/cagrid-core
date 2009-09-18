package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.List;

import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.client.AuthenticationClient;
import org.cagrid.gaards.dorian.client.GridAdministrationClient;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.federation.HostCertificateFilter;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.globus.gsi.GlobusCredential;

public class HostCertificatesStep extends Step {

	private File tomcatCertsDir;
	
	private String dorianServiceURL;

	private Credential userCredential;

	private Credential adminCredential;

	private String hostname;

	public HostCertificatesStep(File tomcatCertsDir,String dorianServiceURL,
			Credential userCredential, Credential adminCredential,String hostName) {
		this.tomcatCertsDir=tomcatCertsDir;
		this.dorianServiceURL = dorianServiceURL;
		this.userCredential = userCredential;
		this.adminCredential = adminCredential;
		this.hostname=hostName;
	}

	public void runStep() throws Throwable {

		AuthenticationClient client = new AuthenticationClient(this.dorianServiceURL);
		GridUserClient gridUserClient = new GridUserClient(dorianServiceURL);
		int stren = 1024;
		KeyPair pair = KeyUtil.generateRSAKeyPair(stren);
		
		//Get saml assertion ,globus cre
		SAMLAssertion samlAssertion = client.authenticate(userCredential);
		GlobusCredential globusCredential = gridUserClient
				.requestUserCertificate(samlAssertion, new CertificateLifetime(1, 60, 60));
		gridUserClient = new GridUserClient(dorianServiceURL, globusCredential);
		HostCertificateRecord record = gridUserClient.requestHostCertificate(
				hostname, pair.getPublic());
		
		SAMLAssertion adminSAMLAssertion = client.authenticate(adminCredential);
		GlobusCredential adminGlobusCredential = gridUserClient
				.requestUserCertificate(adminSAMLAssertion,new CertificateLifetime(1, 60, 60));
		GridAdministrationClient gridAdminClient = new GridAdministrationClient(
				dorianServiceURL, adminGlobusCredential);
		HostCertificateFilter filter = new HostCertificateFilter();
		filter.setHost(hostname);

		List<HostCertificateRecord> certs = gridAdminClient
				.findHostCertificates(filter);
		for (HostCertificateRecord hostCertificateRecord : certs) {
			gridAdminClient.approveHostCertificate(hostCertificateRecord.getId());
		}

		// get User Certificates list
		List<HostCertificateRecord> hostCertificateList = gridUserClient.getOwnedHostCertificates();
		for (HostCertificateRecord hostCertificateRecord : hostCertificateList) {
			if (hostCertificateRecord.getHost().equals(hostname)) {
				File keyPath = new File(tomcatCertsDir.getAbsolutePath()
						+ File.separator + record.getHost() + "-key.pem");
				File certPath = new File(tomcatCertsDir + File.separator
						+ record.getHost() + "-cert.pem");

				KeyUtil.writePrivateKey(pair.getPrivate(), keyPath);
				X509Certificate cert = CertUtil
						.loadCertificate(hostCertificateRecord.getCertificate()
								.getCertificateAsString());
				CertUtil.writeCertificate(cert, certPath);
			}
		}
	}
}
