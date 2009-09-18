package org.cagrid.gaards.authentication.test.system.steps;

import java.security.cert.X509Certificate;

public class CertificateProxy implements SigningCertificateProxy {
	private X509Certificate signingCertificate;

	public CertificateProxy(X509Certificate cert) {
		this.signingCertificate = cert;
	}

	public X509Certificate getSigningCertificate() {
		return signingCertificate;
	}

}