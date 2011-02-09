package org.cagrid.gaards.pki;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.naming.ldap.LdapName;
import javax.security.auth.x500.X500Principal;

import junit.framework.TestCase;

import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestCertUtil extends TestCase {

	public void testCreateCertificateSimpleCARoot() {
		try {
			InputStream certLocation = TestCase.class
					.getResourceAsStream(Constants.SIMPLECA_CACERT);
			InputStream keyLocation = TestCase.class
					.getResourceAsStream(Constants.SIMPLECA_CAKEY);
			String keyPassword = "simpleca";
			X509Certificate[] certs = createCertificateSpecifyRootCA(
					certLocation, keyLocation, keyPassword, "John Doe");
			assertEquals(2, certs.length);
			String rootSub = "CN=caBIG Certificate Authority,OU=Ohio State University,O=caBIG,C=US";
			String issuedSub = "CN=John Doe,OU=Ohio State University,O=caBIG,C=US";
			X509Certificate rootCert = certs[1];
			X509Certificate issuedCert = certs[0];
			checkCert(rootCert, rootSub, rootSub);
			checkCert(issuedCert, rootSub, issuedSub);
			checkWriteReadCertificate(rootCert);
			checkWriteReadCertificate(issuedCert);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail();
		}
	}

	
	public void testCreateCertificateDorianCARoot() {
		try {
			InputStream certLocation = TestCase.class
					.getResourceAsStream(Constants.BMI_CACERT);
			InputStream keyLocation = TestCase.class
					.getResourceAsStream(Constants.BMI_CAKEY);

			String keyPassword = "gomets123";
			X509Certificate[] certs = createCertificateSpecifyRootCA(
					certLocation, keyLocation, keyPassword, "John Doe");
			assertEquals(2, certs.length);
			String rootSub = "CN=BMI Certificate Authority,OU=MSCL,OU=BMI,O=Ohio State University";
			String issuedSub = "CN=John Doe,OU=MSCL,OU=BMI,O=Ohio State University";
			X509Certificate rootCert = certs[1];
			X509Certificate issuedCert = certs[0];
			checkCert(rootCert, rootSub, rootSub);
			checkCert(issuedCert, rootSub, issuedSub);
			checkWriteReadCertificate(rootCert);
			checkWriteReadCertificate(issuedCert);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail();
		}
	}

	
	public void testCreateCertificateNewDorianCARootCert() {
		try {
			KeyPair rootPair = KeyUtil.generateRSAKeyPair1024();
			assertNotNull(rootPair);
			String rootSub = "CN=Temp Certificate Authority,OU=MSCL,OU=BMI,O=Ohio State University";
			X509Name rootSubject = new X509Name(true, rootSub);
			X509Certificate root = CertUtil.generateCACertificate(rootSubject,
					new Date(System.currentTimeMillis()), new Date(System
							.currentTimeMillis() + 500000000), rootPair);
			assertNotNull(root);
			String certLocation = "temp-cacert.pem";
			String keyLocation = "temp-cakey.pem";
			String keyPassword = "gomets123";
			KeyUtil.writePrivateKey(rootPair.getPrivate(),
					new File(keyLocation), keyPassword);
			CertUtil.writeCertificate(root, new File(certLocation));

			X509Certificate[] certs = createCertificateSpecifyRootCA(
					certLocation, keyLocation, keyPassword, "John Doe");
			File f1 = new File(certLocation);
			f1.delete();
			File f2 = new File(keyLocation);
			f2.delete();
			assertEquals(2, certs.length);
			String issuedSub = "CN=John Doe,OU=MSCL,OU=BMI,O=Ohio State University";
			X509Certificate rootCert = certs[1];
			X509Certificate issuedCert = certs[0];
			checkCert(rootCert, rootSub, rootSub);
			checkCert(issuedCert, rootSub, issuedSub);
			checkWriteReadCertificate(rootCert);
			checkWriteReadCertificate(issuedCert);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail();
		}
	}
	

	public void testCreateCertificateExpiredRootCert() {
		try {
			KeyPair rootPair = KeyUtil.generateRSAKeyPair1024();
			assertNotNull(rootPair);
			String rootSub = "CN=Temp Certificate Authority,OU=MSCL,OU=BMI,O=Ohio State University";
			X509Name rootSubject = new X509Name(true, rootSub);
			X509Certificate root = CertUtil.generateCACertificate(rootSubject,
					new Date(System.currentTimeMillis()), new Date(System
							.currentTimeMillis()), rootPair);
			Thread.sleep(10);
			assertNotNull(root);
			String certLocation = "temp-cacert.pem";
			String keyLocation = "temp-cakey.pem";
			String keyPassword = "gomets123";
			KeyUtil.writePrivateKey(rootPair.getPrivate(),
					new File(keyLocation), keyPassword);
			CertUtil.writeCertificate(root, new File(certLocation));
			checkCert(root, rootSub, rootSub);
			checkWriteReadCertificate(root);
			try {
				createCertificateSpecifyRootCA(certLocation, keyLocation,
						keyPassword, "John Doe");
				fail();
			} catch (Exception e) {
				assertEquals("Root Certificate Expired.", e.getMessage());
			}

			File f1 = new File(certLocation);
			f1.delete();
			File f2 = new File(keyLocation);
			f2.delete();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail();
		}
	}

	
	public void testCreateCertificateNotYetValidRootCert() {
		try {
			KeyPair rootPair = KeyUtil.generateRSAKeyPair1024();
			assertNotNull(rootPair);
			String rootSub = "CN=Temp Certificate Authority,OU=MSCL,OU=BMI,O=Ohio State University";
			X509Name rootSubject = new X509Name(rootSub);
			X509Certificate root = CertUtil.generateCACertificate(rootSubject,
					new Date(System.currentTimeMillis() + 50000), new Date(
							System.currentTimeMillis() + 500000), rootPair);
			assertNotNull(root);
			String certLocation = "temp-cacert.pem";
			String keyLocation = "temp-cakey.pem";
			String keyPassword = "gomets123";
			KeyUtil.writePrivateKey(rootPair.getPrivate(),
					new File(keyLocation), keyPassword);
			CertUtil.writeCertificate(root, new File(certLocation));
			checkCert(root, rootSub, rootSub);
			checkWriteReadCertificate(root);
			try {
				createCertificateSpecifyRootCA(certLocation, keyLocation,
						keyPassword, "John Doe");
				fail();
			} catch (Exception e) {
				assertEquals("Root Certificate not yet valid.", e.getMessage());
			}

			File f1 = new File(certLocation);
			f1.delete();
			File f2 = new File(keyLocation);
			f2.delete();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail();
		}
	}
	

	private void checkWriteReadCertificate(X509Certificate cert)
			throws Exception {
		String temp = "temp.pem";
		CertUtil.writeCertificate(cert, new File(temp));
		X509Certificate in = CertUtil.loadCertificate(new File(temp));
		assertEquals(cert, in);
		File f = new File(temp);
		f.delete();
	}
	

	private void checkCert(X509Certificate cert, String issuer, String subject) {
		X500Principal x500s = cert.getSubjectX500Principal();
		String subj = cert.getSubjectX500Principal().getName(X500Principal.RFC2253);
		X500Principal x500 = cert.getIssuerX500Principal();		
		String iss = cert.getIssuerX500Principal().getName(X500Principal.RFC2253);
	    assertEquals(subject, cert.getSubjectX500Principal().getName(X500Principal.RFC2253));
		assertEquals(issuer, cert.getIssuerX500Principal().getName());
	}

	
	public X509Certificate[] createCertificateSpecifyRootCA(
			String certLocation, String keyLocation, String keyPassword,
			String cn) throws Exception {
		return createCertificateSpecifyRootCA(getFileInputStream(certLocation),
				getFileInputStream(keyLocation), keyPassword, cn);
	}
	

	public X509Certificate[] createCertificateSpecifyRootCA(
			InputStream certLocation, InputStream keyLocation,
			String keyPassword, String cn) throws Exception {
		// Load a root certificate
		PrivateKey rootKey = KeyUtil.loadPrivateKey(keyLocation, keyPassword);
		assertNotNull(rootKey);
		X509Certificate rootCert = CertUtil.loadCertificate(certLocation);
		assertNotNull(rootCert);
		LdapName rootSub = new LdapName(rootCert.getSubjectX500Principal().getName());

		Date now = new Date(System.currentTimeMillis());

		if (now.after(rootCert.getNotAfter())) {
			throw new Exception("Root Certificate Expired.");
		}

		if (now.before(rootCert.getNotBefore())) {
			throw new Exception("Root Certificate not yet valid.");
		}

		// create the certification request
		KeyPair pair = KeyUtil.generateRSAKeyPair1024();
		assertNotNull(pair);
		LdapName sub = (LdapName) rootSub.clone();
		sub.remove(sub.size() - 1);
		sub.add("CN=" + cn);
		PKCS10CertificationRequest request = CertUtil
				.generateCertficateRequest(sub.toString(), pair);

		// validate the certification request
		if (!request.verify()) {
			System.out.println("request failed to verify!");
			System.exit(1);
		}

		X509Certificate issuedCert = CertUtil.signCertificateRequest(request,
				new Date(System.currentTimeMillis()), new Date(System
						.currentTimeMillis() + 500000000), rootCert, rootKey,null);
		assertNotNull(issuedCert);

		return new X509Certificate[] { issuedCert, rootCert };
	}

	
	public void testCRL() {
		try {
			String root = "OU=MSCL,OU=BMI,O=Ohio State University";
			String rootSub = "CN=TestCA," + root;
			String user1Sub = "CN=John Doe," + root;
			String user2Sub = "CN=Jane Doe," + root;
			String user3Sub = "CN=Tom Doe," + root;

			KeyPair rootKeys = KeyUtil.generateRSAKeyPair512();
			assertNotNull(rootKeys);
			Calendar c = new GregorianCalendar();
			Date now = c.getTime();
			c.add(Calendar.YEAR, 1);
			Date end = c.getTime();
			X509Certificate cacert = CertUtil.generateCACertificate(
					new X509Name(rootSub), now, end, rootKeys);
			checkCert(cacert, rootSub, rootSub);
			checkWriteReadCertificate(cacert);

			KeyPair user1Keys = KeyUtil.generateRSAKeyPair512();
			assertNotNull(user1Keys);
			X509Certificate user1 = CertUtil.generateCertificate(new X509Name(
					user1Sub), now, end, user1Keys.getPublic(), cacert,
					rootKeys.getPrivate(),null);
			checkCert(user1, rootSub, user1Sub);
			checkWriteReadCertificate(user1);

			KeyPair user2Keys = KeyUtil.generateRSAKeyPair512();
			assertNotNull(user2Keys);
			X509Certificate user2 = CertUtil.generateCertificate(new X509Name(
					user2Sub), now, end, user2Keys.getPublic(), cacert,
					rootKeys.getPrivate(),null);
			checkCert(user2, rootSub, user2Sub);
			checkWriteReadCertificate(user2);

			KeyPair user3Keys = KeyUtil.generateRSAKeyPair512();
			assertNotNull(user3Keys);
			X509Certificate user3 = CertUtil.generateCertificate(new X509Name(
					user3Sub), now, end, user3Keys.getPublic(), cacert,
					rootKeys.getPrivate(),null);
			checkCert(user3, rootSub, user3Sub);
			checkWriteReadCertificate(user3);

			CRLEntry[] crls = new CRLEntry[2];
			crls[0] = new CRLEntry(user1.getSerialNumber(),
					CRLReason.PRIVILEGE_WITHDRAWN);
			crls[1] = new CRLEntry(user3.getSerialNumber(),
					CRLReason.PRIVILEGE_WITHDRAWN);
			X509CRL crl = CertUtil.createCRL(cacert, rootKeys.getPrivate(),
					crls, cacert.getNotAfter());
			assertNotNull(crl);

			// Test validity of CRL
			crl.verify(cacert.getPublicKey());
			try {
				crl.verify(user1.getPublicKey());
				fail("CRL verified against invalid certificate");
			} catch (Exception ex) {
			}
			assertTrue(crl.isRevoked(user1));
			assertTrue(!crl.isRevoked(user2));
			assertTrue(crl.isRevoked(user3));

			// Test validity after reading writing to string
			String crlStr = CertUtil.writeCRL(crl);
			X509CRL crl2 = CertUtil.loadCRL(crlStr);
			assertEquals(crl, crl2);
			crl2.verify(cacert.getPublicKey());
			try {
				crl2.verify(user1.getPublicKey());
				fail("CRL verified against invalid certificate");
			} catch (Exception ex) {
			}
			assertTrue(crl2.isRevoked(user1));
			assertTrue(!crl2.isRevoked(user2));
			assertTrue(crl2.isRevoked(user3));

			// Test validity after reading writing to file
			File f = new File("temp-crl.pem");
			CertUtil.writeCRL(crl, f);
			X509CRL crl3 = CertUtil.loadCRL(f);
			assertEquals(crl, crl3);
			crl3.verify(cacert.getPublicKey());
			try {
				crl3.verify(user1.getPublicKey());
				fail("CRL verified against invalid certificate");
			} catch (Exception ex) {
			}
			assertTrue(crl3.isRevoked(user1));
			assertTrue(!crl3.isRevoked(user2));
			assertTrue(crl3.isRevoked(user3));
			f.delete();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail();
		}
	}

	
	private static FileInputStream getFileInputStream(String file)
			throws Exception {
		return new FileInputStream(new File(file));
	}
}
