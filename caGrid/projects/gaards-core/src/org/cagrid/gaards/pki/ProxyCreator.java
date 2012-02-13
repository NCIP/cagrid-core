package org.cagrid.gaards.pki;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.globus.gsi.GSIConstants;
import org.globus.gsi.X509ExtensionSet;
import org.globus.gsi.proxy.ext.ProxyCertInfo;
import org.globus.gsi.proxy.ext.ProxyCertInfoExtension;
import org.globus.gsi.proxy.ext.ProxyPolicy;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class ProxyCreator {

	public static final String SIGNATURE_ALGORITHM = "MD5WithRSAEncryption";


	public static X509Certificate[] createImpersonationProxyCertificate(X509Certificate cert, PrivateKey privateKey,
		PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds)
		throws GeneralSecurityException {
		SecurityUtil.init();
		return createImpersonationProxyCertificate("BC", new X509Certificate[]{cert}, privateKey, proxyPublicKey,
			lifetimeHours, lifetimeMinutes, lifetimeSeconds, SIGNATURE_ALGORITHM);
	}


	public static X509Certificate[] createImpersonationProxyCertificate(String provider, X509Certificate cert,
		PrivateKey privateKey, PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds,
		String signatureAlgorithm) throws GeneralSecurityException {
		return createImpersonationProxyCertificate(provider, new X509Certificate[]{cert}, privateKey, proxyPublicKey,
			lifetimeHours, lifetimeMinutes, lifetimeSeconds, signatureAlgorithm);
	}


	public static X509Certificate[] createImpersonationProxyCertificate(X509Certificate cert, PrivateKey privateKey,
		PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds, int delegationPathLength)
		throws GeneralSecurityException {
		SecurityUtil.init();
		return createImpersonationProxyCertificate("BC", new X509Certificate[]{cert}, privateKey, proxyPublicKey,
			lifetimeHours, lifetimeMinutes, lifetimeSeconds, delegationPathLength, SIGNATURE_ALGORITHM);
	}


	public static X509Certificate[] createImpersonationProxyCertificate(String provider, X509Certificate cert,
		PrivateKey privateKey, PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds,
		int delegationPathLength, String signatureAlgorithm) throws GeneralSecurityException {
		return createImpersonationProxyCertificate(provider, new X509Certificate[]{cert}, privateKey, proxyPublicKey,
			lifetimeHours, lifetimeMinutes, lifetimeSeconds, delegationPathLength, signatureAlgorithm);
	}


	public static X509Certificate[] createImpersonationProxyCertificate(X509Certificate[] certs, PrivateKey privateKey,
		PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds)
		throws GeneralSecurityException {
		SecurityUtil.init();
		return createImpersonationProxyCertificate("BC", certs, privateKey, proxyPublicKey, lifetimeHours,
			lifetimeMinutes, lifetimeSeconds, Integer.MAX_VALUE, SIGNATURE_ALGORITHM);
	}


	public static X509Certificate[] createImpersonationProxyCertificate(String provider, X509Certificate[] certs,
		PrivateKey privateKey, PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds,
		String signatureAlgorithm) throws GeneralSecurityException {
		return createImpersonationProxyCertificate(provider, certs, privateKey, proxyPublicKey, lifetimeHours,
			lifetimeMinutes, lifetimeSeconds, Integer.MAX_VALUE, signatureAlgorithm);
	}


	public static X509Certificate[] createImpersonationProxyCertificate(X509Certificate[] certs, PrivateKey privateKey,
		PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds, int delegationPathLength)
		throws GeneralSecurityException {
		SecurityUtil.init();
		return createImpersonationProxyCertificate("BC", certs, privateKey, proxyPublicKey, lifetimeHours,
			lifetimeMinutes, lifetimeSeconds, delegationPathLength, SIGNATURE_ALGORITHM);
	}


	public static X509Certificate[] createImpersonationProxyCertificate(String provider, X509Certificate[] certs,
		PrivateKey privateKey, PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds,
		int delegationPathLength, String signatureAlgorithm) throws GeneralSecurityException {
		ProxyPolicy policy = new ProxyPolicy(ProxyPolicy.IMPERSONATION);
		ProxyCertInfo proxyCertInfo = new ProxyCertInfo(delegationPathLength, policy);
		org.globus.gsi.X509Extension x509Ext = new ProxyCertInfoExtension(proxyCertInfo);
		X509ExtensionSet extSet = new X509ExtensionSet();
		extSet.add(x509Ext);
		return createProxyCertificate(provider, certs, privateKey, proxyPublicKey, lifetimeHours, lifetimeMinutes,
			lifetimeSeconds, GSIConstants.GSI_4_IMPERSONATION_PROXY, extSet, signatureAlgorithm);
	}


	public static X509Certificate[] createProxyCertificate(X509Certificate[] certs, PrivateKey privateKey,
		PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds, int proxyType,
		X509ExtensionSet extSet) throws GeneralSecurityException {
		SecurityUtil.init();
		return createProxyCertificate("BC", certs, privateKey, proxyPublicKey, lifetimeHours, lifetimeMinutes,
			lifetimeSeconds, proxyType, extSet, SIGNATURE_ALGORITHM);
	}


	public static X509Certificate[] createProxyCertificate(String provider, X509Certificate[] certs,
		PrivateKey privateKey, PublicKey proxyPublicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds,
		int proxyType, X509ExtensionSet extSet, String signatureAlgorithm) throws GeneralSecurityException {
		X509Certificate newCert = createProxyCertificate(provider, certs[0], privateKey, proxyPublicKey, lifetimeHours,
			lifetimeMinutes, lifetimeSeconds, proxyType, extSet, signatureAlgorithm);

		X509Certificate[] newCerts = new X509Certificate[certs.length + 1];
		newCerts[0] = newCert;
		System.arraycopy(certs, 0, newCerts, 1, certs.length);
		return newCerts;
	}


	protected static X509Certificate createProxyCertificate(String provider, X509Certificate issuerCert,
		PrivateKey issuerKey, PublicKey publicKey, int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds,
		int proxyType, X509ExtensionSet extSet, String signatureAlgorithm) throws GeneralSecurityException {
		Date d = getProxyValid(lifetimeHours, lifetimeMinutes, lifetimeSeconds);
		if (issuerCert.getNotAfter().before(d)) {
			throw new GeneralSecurityException("Cannot create a proxy that expires after issuing certificate.");
		}
		int hoursToSeconds = lifetimeHours * 60 * 60;
		int minutesToSeconds = lifetimeMinutes * 60;
		int seconds = hoursToSeconds + minutesToSeconds + lifetimeSeconds;
		BouncyCastleCertProcessingFactory bc = BouncyCastleCertProcessingFactory.getDefault();
		return bc.createProxyCertificate(provider, issuerCert, issuerKey, publicKey, seconds, proxyType, extSet, null,
			signatureAlgorithm);
	}


	public static Date getProxyValid(int lifetimeHours, int lifetimeMinutes, int lifetimeSeconds) {
		Calendar c = new GregorianCalendar();
		c.add(Calendar.HOUR_OF_DAY, lifetimeHours);
		c.add(Calendar.MINUTE, lifetimeMinutes);
		c.add(Calendar.SECOND, lifetimeSeconds);
		return c.getTime();
	}

}
