package gov.nih.nci.cagrid.gts.test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.naming.ldap.LdapName;

import org.bouncycastle.asn1.x509.X509Name;
import org.cagrid.gaards.pki.CRLEntry;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;


public class CA {
	private X509Certificate cert;
	private PrivateKey key;
	private X509CRL crl;
	public final static String DEFAULT_CA_DN = "CN=Certificate Authority,OU=Unit XYZ,O=Organization ABC";


	public CA() throws Exception {
		this(DEFAULT_CA_DN);
	}


	public CA(String dn) throws Exception {
		Calendar c = new GregorianCalendar();
		Date now = c.getTime();
		c.add(Calendar.YEAR, 5);
		Date expires = c.getTime();
		KeyPair pair = KeyUtil.generateRSAKeyPair512();
		this.key = pair.getPrivate();
		cert = CertUtil.generateCACertificate(new X509Name(dn), now, expires, pair);

	}


	public CA(String dn, Date start, Date expires) throws Exception {
		KeyPair pair = KeyUtil.generateRSAKeyPair512();
		this.key = pair.getPrivate();
		cert = CertUtil.generateCACertificate(new X509Name(dn), start, expires, pair);
	}


	public CA(X509Certificate cert, PrivateKey key, X509CRL crl) {
		this.cert = cert;
		this.key = key;
		this.crl = crl;
	}


	public X509Certificate getCertificate() {
		return cert;
	}


	public Credential createIdentityCertificate(String id) throws Exception {
		LdapName dn = new LdapName(getCertificate().getSubjectX500Principal().getName());
		dn.remove(dn.size() - 1);
		dn.add("CN="+id);
		KeyPair pair = KeyUtil.generateRSAKeyPair512();
		Date now = new Date();
		Date end = getCertificate().getNotAfter();
		return new Credential(CertUtil.generateCertificate(new X509Name(dn.toString()), now, end, pair.getPublic(),
			getCertificate(), getPrivateKey(),null), pair.getPrivate());

	}


	public X509CRL getCRL() {
		return crl;
	}


	public PrivateKey getPrivateKey() {
		return key;
	}


	public X509CRL updateCRL(CRLEntry entry) throws Exception {
		CRLEntry[] entries = new CRLEntry[1];
		entries[0] = entry;
		crl = CertUtil.createCRL(cert, key, entries, cert.getNotAfter());
		return crl;
	}


	public X509CRL updateCRL(CRLEntry[] entries) throws Exception {
		crl = CertUtil.createCRL(cert, key, entries, cert.getNotAfter());
		return crl;
	}

}
