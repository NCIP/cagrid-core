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