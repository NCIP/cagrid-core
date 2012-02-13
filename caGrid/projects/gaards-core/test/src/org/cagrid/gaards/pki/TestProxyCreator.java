package org.cagrid.gaards.pki;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cagrid.gaards.pki.CertificateExtensionsUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.pki.ProxyCreator;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestProxyCreator extends TestCase {

	private String identityToSubject(String identity) {
		String s = identity.substring(1);
		return s.replace('/', ',');
	}


	public void testCreateProxy() {
		checkCreateProxy(Integer.MAX_VALUE);
	}


	public void testDelegationProxyLength0() {
		checkCreateProxy(0);
	}


	public void testDelegationProxyLength5() {
		checkCreateProxy(5);
	}


	public void checkCreateProxy(int length) {
		try {
			CA ca = new CA();
			Credential gridCred = ca.createIdentityCertificate("User X");
			int hours = 2;
			int minutes = 0;
			int seconds = 0;
			PrivateKey key = gridCred.getPrivateKey();
			assertNotNull(key);
			KeyPair pair = KeyUtil.generateRSAKeyPair512("BC");
			assertNotNull(pair);
			PublicKey proxyPublicKey = pair.getPublic();
			assertNotNull(proxyPublicKey);
			X509Certificate cert = gridCred.getCertificate();
			assertNotNull(cert);
			X509Certificate[] certs = ProxyCreator.createImpersonationProxyCertificate(CA.PROVIDER.getName(), cert,
				key, proxyPublicKey, hours, minutes, seconds, length, CA.SIGNATURE_ALGORITHM);
			assertNotNull(certs);
			assertEquals(2, certs.length);
			GlobusCredential cred = new GlobusCredential(pair.getPrivate(), certs);
			assertNotNull(cred);
			long timeLeft = cred.getTimeLeft();
			assertEquals(cert.getSubjectDN().toString(), identityToSubject(cred.getIdentity()));
			assertEquals(cred.getIssuer(), identityToSubject(cred.getIdentity()));
			assertEquals(length, CertificateExtensionsUtil.getDelegationPathLength(certs[0]));

			long okMax = hours * 60 * 60;
			// Allow some Buffer
			long okMin = okMax - 3;

			if ((okMin > timeLeft) || (timeLeft > okMax)) {
				assertTrue(false);
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}


	public void testInvalidProxyTimeToGreat() {
		try {
			CA ca = new CA();
			Credential gridCred = ca.createIdentityCertificate("User X");
			int hours = 50000;
			int minutes = 0;
			int seconds = 0;
			PrivateKey key = gridCred.getPrivateKey();
			assertNotNull(key);
			KeyPair pair = KeyUtil.generateRSAKeyPair512("BC");
			assertNotNull(pair);
			PublicKey proxyPublicKey = pair.getPublic();
			assertNotNull(proxyPublicKey);
			X509Certificate cert = gridCred.getCertificate();
			assertNotNull(cert);
			ProxyCreator.createImpersonationProxyCertificate(CA.PROVIDER.getName(), cert, key, proxyPublicKey, hours,
				minutes, seconds, CA.SIGNATURE_ALGORITHM);
			assertTrue(false);
		} catch (Exception e) {
			assertEquals("Cannot create a proxy that expires after issuing certificate.", e.getMessage());
		}
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
