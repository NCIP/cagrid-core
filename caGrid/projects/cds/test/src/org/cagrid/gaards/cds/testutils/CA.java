package org.cagrid.gaards.cds.testutils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.ldap.LdapName;

import org.bouncycastle.asn1.x509.X509Name;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.pki.ProxyCreator;
import org.globus.gsi.GlobusCredential;

public class CA {
	private X509Certificate cert;
	private PrivateKey key;
	public static final Provider PROVIDER = new org.bouncycastle.jce.provider.BouncyCastleProvider();
	public static final String SIGNATURE_ALGORITHM = "MD5WithRSAEncryption";
	public static final String PASSWORD = "password";
	public static final String DEFAULT_CA_DN = "CN=Certificate Authority,OU=Unit XYZ,O=Organization ABC";

	private Map<String, GlobusCredential> creds = new HashMap<String, GlobusCredential>();

	public CA() throws Exception {
		this(DEFAULT_CA_DN);
	}

	public CA(String dn) throws Exception {
		Security.addProvider(PROVIDER);
		Calendar c = new GregorianCalendar();
		Date now = c.getTime();
		c.add(Calendar.YEAR, 5);
		Date expires = c.getTime();
		KeyPair pair = KeyUtil.generateRSAKeyPair1024(PROVIDER.getName());
		this.key = pair.getPrivate();
		cert = CertUtil.generateCACertificate(PROVIDER.getName(), new X509Name(
				dn), now, expires, pair, SIGNATURE_ALGORITHM);
		this.creds = new HashMap<String, GlobusCredential>();

	}

	public X509Certificate getCertificate() {
		return cert;
	}

	public GlobusCredential createProxy(String alias, int pathLength)
			throws Exception {
		KeyPair pair = KeyUtil.generateRSAKeyPair(Constants.KEY_LENGTH);
		return createProxy(alias, pair.getPublic(), pair.getPrivate(),
				pathLength);
	}

	public GlobusCredential createProxy(String alias, PublicKey publicKey,
			PrivateKey privateKey, int pathLength) throws Exception {
		GlobusCredential cred = null;
		if (this.creds.containsKey(alias)) {
			cred = this.creds.get(alias);
		} else {
			cred = createCredential(alias);
		}
		X509Certificate[] certs = ProxyCreator
				.createImpersonationProxyCertificate(
						cred.getCertificateChain(), cred.getPrivateKey(),
						publicKey, 12, 0, 0, pathLength);
		return new GlobusCredential(privateKey, certs);
	}

	public X509Certificate[] createProxyCertifcates(String alias,
			PublicKey publicKey, int pathLength) throws Exception {
		return createProxyCertifcates(alias, publicKey, pathLength, 12, 0, 0);
	}

	public X509Certificate[] createProxyCertifcates(String alias,
			PublicKey publicKey, int pathLength, int hours, int minutes,
			int seconds) throws Exception {
		GlobusCredential cred = null;
		if (this.creds.containsKey(alias)) {
			cred = this.creds.get(alias);
		} else {
			cred = createCredential(alias);
		}
		X509Certificate[] certs = ProxyCreator
				.createImpersonationProxyCertificate(
						cred.getCertificateChain(), cred.getPrivateKey(),
						publicKey, hours, minutes, seconds, pathLength);
		return certs;
	}

	public GlobusCredential createCredential(String alias) throws Exception {
		KeyPair pair = KeyUtil.generateRSAKeyPair(Constants.KEY_LENGTH);
		return createCredential(alias, pair.getPublic(), pair.getPrivate());
	}

	public GlobusCredential createCredential(String alias, PublicKey publicKey,
			PrivateKey privateKey) throws Exception {
		LdapName dn = new LdapName(getCertificate().getSubjectX500Principal().getName());
		dn.remove(dn.size() - 1);
		dn.add("CN=" + alias);
		Date now = new Date();
		Date end = getCertificate().getNotAfter();
		X509Certificate cert = CertUtil.generateCertificate(PROVIDER.getName(),
				new X509Name(dn.toString()), now, end, publicKey, getCertificate(),
				getPrivateKey(), SIGNATURE_ALGORITHM, null);
		GlobusCredential cred = new GlobusCredential(privateKey,
				new X509Certificate[] { cert });
		this.creds.put(alias, cred);
		return cred;
	}

	public PrivateKey getPrivateKey() {
		return key;
	}
}
