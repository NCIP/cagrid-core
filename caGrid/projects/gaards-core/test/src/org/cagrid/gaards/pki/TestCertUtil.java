package org.cagrid.gaards.pki;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cagrid.gaards.pki.CRLEntry;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;

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
			String keyPassword = "gomets123";
			X509Certificate[] certs = createCertificateSpecifyRootCA(
					certLocation, keyLocation, keyPassword, "John Doe");
			assertEquals(2, certs.length);
			String rootSub = "O=caBIG,OU=Ohio State University,OU=Department of Biomedical Informatics,CN=caBIG Certificate Authority";
			String issuedSub = "O=caBIG,OU=Ohio State University,OU=Department of Biomedical Informatics,CN=John Doe";
			X509Certificate rootCert = certs[1];
			X509Certificate issuedCert = certs[0];
			checkCert(rootCert, rootSub, rootSub);
			checkCert(issuedCert, rootSub, issuedSub);
			checkWriteReadCertificate(rootCert);
			checkWriteReadCertificate(issuedCert);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
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
			String rootSub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=BMI Certificate Authority";
			String issuedSub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=John Doe";
			X509Certificate rootCert = certs[1];
			X509Certificate issuedCert = certs[0];
			checkCert(rootCert, rootSub, rootSub);
			checkCert(issuedCert, rootSub, issuedSub);
			checkWriteReadCertificate(rootCert);
			checkWriteReadCertificate(issuedCert);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}

	public void testCreateCertificateNewDorianCARootCert() {
		try {
			KeyPair rootPair = KeyUtil.generateRSAKeyPair1024("BC");
			assertNotNull(rootPair);
			String rootSub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=Temp Certificate Authority";
			X509Name rootSubject = new X509Name(rootSub);
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
			String issuedSub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=John Doe";
			X509Certificate rootCert = certs[1];
			X509Certificate issuedCert = certs[0];
			checkCert(rootCert, rootSub, rootSub);
			checkCert(issuedCert, rootSub, issuedSub);
			checkWriteReadCertificate(rootCert);
			checkWriteReadCertificate(issuedCert);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}

	public void testCreateCertificateExpiredRootCert() {
		try {
			KeyPair rootPair = KeyUtil.generateRSAKeyPair1024("BC");
			assertNotNull(rootPair);
			String rootSub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=Temp Certificate Authority";
			X509Name rootSubject = new X509Name(rootSub);
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
				assertTrue(false);
			} catch (Exception e) {
				assertEquals("Root Certificate Expired.", e.getMessage());
			}

			File f1 = new File(certLocation);
			f1.delete();
			File f2 = new File(keyLocation);
			f2.delete();

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}

	public void testCreateCertificateNotYetValidRootCert() {
		try {
			KeyPair rootPair = KeyUtil.generateRSAKeyPair1024("BC");
			assertNotNull(rootPair);
			String rootSub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=Temp Certificate Authority";
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
				assertTrue(false);
			} catch (Exception e) {
				assertEquals("Root Certificate not yet valid.", e.getMessage());
			}

			File f1 = new File(certLocation);
			f1.delete();
			File f2 = new File(keyLocation);
			f2.delete();

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}

	private void checkWriteReadCertificate(X509Certificate cert)
			throws Exception {
		String temp = "temp.pem";
		CertUtil.writeCertificate(cert, new File(temp));
		X509Certificate in = CertUtil.loadCertificate("BC", new File(temp));
		assertEquals(cert, in);
		File f = new File(temp);
		f.delete();
	}

	private void checkCert(X509Certificate cert, String issuer, String subject) {
		assertEquals(subject, cert.getSubjectDN().toString());
		assertEquals(issuer, cert.getIssuerDN().toString());
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
		X509Certificate rootCert = CertUtil.loadCertificate("BC", certLocation);
		assertNotNull(rootCert);
		String rootSub = rootCert.getSubjectDN().toString();

		Date now = new Date(System.currentTimeMillis());

		if (now.after(rootCert.getNotAfter())) {
			throw new Exception("Root Certificate Expired.");
		}

		if (now.before(rootCert.getNotBefore())) {
			throw new Exception("Root Certificate not yet valid.");
		}

		// create the certification request
		KeyPair pair = KeyUtil.generateRSAKeyPair1024("BC");
		assertNotNull(pair);
		int index = rootSub.lastIndexOf(",");
		String sub = rootSub.substring(0, index) + ",CN=" + cn;
		PKCS10CertificationRequest request = CertUtil
				.generateCertficateRequest(sub, pair);

		// validate the certification request
		if (!request.verify("BC")) {
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

			String rootSub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=TestCA";
			String user1Sub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=John Doe";
			String user2Sub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=Jane Doe";
			String user3Sub = "O=Ohio State University,OU=BMI,OU=MSCL,CN=Tom Doe";

			KeyPair rootKeys = KeyUtil.generateRSAKeyPair512("BC");
			assertNotNull(rootKeys);
			Calendar c = new GregorianCalendar();
			Date now = c.getTime();
			c.add(Calendar.YEAR, 1);
			Date end = c.getTime();
			X509Certificate cacert = CertUtil.generateCACertificate(
					new X509Name(rootSub), now, end, rootKeys);
			checkCert(cacert, rootSub, rootSub);
			checkWriteReadCertificate(cacert);

			KeyPair user1Keys = KeyUtil.generateRSAKeyPair512("BC");
			assertNotNull(user1Keys);
			X509Certificate user1 = CertUtil.generateCertificate(new X509Name(
					user1Sub), now, end, user1Keys.getPublic(), cacert,
					rootKeys.getPrivate(),null);
			checkCert(user1, rootSub, user1Sub);
			checkWriteReadCertificate(user1);

			KeyPair user2Keys = KeyUtil.generateRSAKeyPair512("BC");
			assertNotNull(user2Keys);
			X509Certificate user2 = CertUtil.generateCertificate(new X509Name(
					user2Sub), now, end, user2Keys.getPublic(), cacert,
					rootKeys.getPrivate(),null);
			checkCert(user2, rootSub, user2Sub);
			checkWriteReadCertificate(user2);

			KeyPair user3Keys = KeyUtil.generateRSAKeyPair512("BC");
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
			X509CRL crl2 = CertUtil.loadCRL("BC", crlStr);
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
			X509CRL crl3 = CertUtil.loadCRL("BC", f);
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
			assertTrue(false);
		}
	}

	private static FileInputStream getFileInputStream(String file)
			throws Exception {
		return new FileInputStream(new File(file));

	}

	protected void setUp() throws Exception {
		super.setUp();
		try {
			Security.addProvider(new BouncyCastleProvider());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}
}
