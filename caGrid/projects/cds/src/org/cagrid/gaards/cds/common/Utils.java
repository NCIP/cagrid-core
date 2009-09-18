package org.cagrid.gaards.cds.common;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.pki.CertUtil;

public class Utils {

	public static Date getEarliestExpiration(X509Certificate[] certs) {
		Date earliestTime = null;
		for (int i = 0; i < certs.length; i++) {
			Date time = certs[i].getNotAfter();
			if (earliestTime == null || time.before(earliestTime)) {
				earliestTime = time;
			}
		}
		return earliestTime;
	}

	public static DelegationIdentifier getDelegationIdentifier(
			DelegatedCredentialReference ref) {
		MessageElement e = (MessageElement) ref.getEndpointReference()
				.getProperties().get(0);
		MessageElement c = (MessageElement) e.getChildElements().next();
		String s = c.getValue();
		DelegationIdentifier id = new DelegationIdentifier();
		id.setDelegationId(Long.valueOf(s).longValue());
		return id;
	}

	public static org.cagrid.gaards.cds.common.X509Certificate convertCertificate(
			X509Certificate cert) throws Exception {
		String str = CertUtil.writeCertificate(cert);
		org.cagrid.gaards.cds.common.X509Certificate x509 = new org.cagrid.gaards.cds.common.X509Certificate();
		x509.setCertificateAsString(str);
		return x509;
	}

	public static X509Certificate convertCertificate(
			org.cagrid.gaards.cds.common.X509Certificate cert) throws Exception {
		return CertUtil.loadCertificate(cert.getCertificateAsString());
	}

	public static CertificateChain toCertificateChain(X509Certificate[] certs)
			throws Exception {
		CertificateChain chain = new CertificateChain();
		if (certs != null) {
			org.cagrid.gaards.cds.common.X509Certificate[] x509 = new org.cagrid.gaards.cds.common.X509Certificate[certs.length];
			for (int i = 0; i < certs.length; i++) {
				x509[i] = convertCertificate(certs[i]);
			}
			chain.setX509Certificate(x509);
		}
		return chain;
	}

	public static X509Certificate[] toCertificateArray(CertificateChain chain)
			throws Exception {
		if (chain != null) {
			org.cagrid.gaards.cds.common.X509Certificate[] certs = chain
					.getX509Certificate();
			if (certs != null) {
				X509Certificate[] x509 = new X509Certificate[certs.length];
				for (int i = 0; i < certs.length; i++) {
					x509[i] = convertCertificate(certs[i]);
				}
				return x509;
			} else {
				return new X509Certificate[0];
			}

		} else {
			return new X509Certificate[0];
		}
	}

	public static IdentityDelegationPolicy createIdentityDelegationPolicy(
			List<String> parties) {
		IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
		AllowedParties ap = new AllowedParties();
		if (parties != null) {
			String[] ids = new String[parties.size()];
			for (int i = 0; i < parties.size(); i++) {
				ids[i] = parties.get(i);
			}
			ap.setGridIdentity(ids);
		} else {
			ap.setGridIdentity(new String[0]);
		}
		policy.setAllowedParties(ap);
		return policy;
	}
}
