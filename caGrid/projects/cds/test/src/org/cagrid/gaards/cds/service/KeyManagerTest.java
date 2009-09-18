package org.cagrid.gaards.cds.service;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.security.KeyPair;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.testutils.CA;
import org.cagrid.gaards.cds.testutils.Utils;

public class KeyManagerTest extends TestCase {
	private CA ca;

	public void testKeyManagerCreateDestroy() {
		try {
			KeyManager km = getKeyManager();
			km.deleteAll();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void testKeyManager() {
		KeyManager km = null;
		try {
			km = getKeyManager();
			int size = 3;
			for (int i = 0; i < size; i++) {
				String alias = String.valueOf(i);
				assertFalse(km.exists(alias));
				KeyPair pair = km.createAndStoreKeyPair(alias, 1024);
				assertTrue(km.exists(alias));
				assertEquals(pair.getPublic(), km.getPublicKey(alias));
				assertEquals(pair.getPrivate(), km.getPrivateKey(alias));
				assertNull(km.getCertificates(alias));
				X509Certificate cert = ca.createCredential(alias,km
						.getPublicKey(alias),km.getPrivateKey(alias)).getIdentityCertificate();
				X509Certificate[] certs = new X509Certificate[] { cert,
						ca.getCertificate() };
				km.storeCertificates(alias, certs);
				X509Certificate[] certs2 = km.getCertificates(alias);
				validateCertificateChain(certs, certs2);
			}

			for (int i = 0; i < size; i++) {
				String alias = String.valueOf(i);
				assertTrue(km.exists(alias));
				km.delete(alias);
				assertFalse(km.exists(alias));
				assertNull(km.getPublicKey(alias));
				assertNull(km.getPrivateKey(alias));
				assertNull(km.getCertificates(alias));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				km.deleteAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testKeyManagerBadCertificate() {
		KeyManager km = null;
		try {
			km = getKeyManager();

			String alias = "1";
			assertFalse(km.exists(alias));
			KeyPair pair = km.createAndStoreKeyPair(alias, 1024);
			assertTrue(km.exists(alias));
			assertEquals(pair.getPublic(), km.getPublicKey(alias));
			assertEquals(pair.getPrivate(), km.getPrivateKey(alias));
			assertNull(km.getCertificates(alias));

			try {
				X509Certificate cert = ca.createCredential(alias).getIdentityCertificate();
				X509Certificate[] certs = new X509Certificate[] { cert,
						ca.getCertificate() };
				km.storeCertificates(alias, certs);
				fail("Should not be able to store an invalid certificate!!!");
			} catch (DelegationFault f) {

			}
			assertNull(km.getCertificates(alias));
			X509Certificate cert =ca.createCredential(alias,km
					.getPublicKey(alias),km.getPrivateKey(alias)).getIdentityCertificate();
			X509Certificate[] certs = new X509Certificate[] { cert,
					ca.getCertificate() };
			km.storeCertificates(alias, certs);
			validateCertificateChain(certs, km.getCertificates(alias));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				km.deleteAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testKeyManagerDuplicatedAlias() {
		KeyManager km = null;
		try {
			km = getKeyManager();

			String alias = "1";
			assertFalse(km.exists(alias));
			KeyPair pair = km.createAndStoreKeyPair(alias, 1024);
			assertTrue(km.exists(alias));
			assertEquals(pair.getPublic(), km.getPublicKey(alias));
			assertEquals(pair.getPrivate(), km.getPrivateKey(alias));
			assertNull(km.getCertificates(alias));

			try {
				km.createAndStoreKeyPair(alias, 1024);
				fail("Should not be able to create key with duplicate alias!!!");
			} catch (CDSInternalFault f) {

			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				km.deleteAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testKeyManagerInvalidAlias() {
		KeyManager km = null;
		try {
			km = getKeyManager();

			String alias = "1";
			assertFalse(km.exists(alias));
			KeyPair pair = km.createAndStoreKeyPair(alias, 1024);
			assertTrue(km.exists(alias));
			assertEquals(pair.getPublic(), km.getPublicKey(alias));
			assertEquals(pair.getPrivate(), km.getPrivateKey(alias));

			String invalidAlias = "2";
			assertFalse(km.exists(invalidAlias));
			assertNull(km.getPublicKey(invalidAlias));
			assertNull(km.getPrivateKey(invalidAlias));
			assertNull(km.getCertificates(invalidAlias));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			try {
				km.deleteAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private KeyManager getKeyManager() throws Exception {
		return Utils.getKeyManager();
	}

	protected void setUp() throws Exception {
		super.setUp();
		try {
			ca = new CA();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
		Utils.getDatabase().createDatabaseIfNeeded();
	}

	protected void tearDown() throws Exception {
		super.setUp();
	}

	private void validateCertificateChain(X509Certificate[] chain1,
			X509Certificate[] chain2) {
		assertNotNull(chain1);
		assertNotNull(chain2);
		assertEquals(chain1.length, chain2.length);
		for (int i = 0; i < chain1.length; i++) {
			assertEquals(chain1[i], chain2[i]);
		}
	}
}
