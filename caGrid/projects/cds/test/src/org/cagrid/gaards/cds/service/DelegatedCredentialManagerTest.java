package org.cagrid.gaards.cds.service;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.io.File;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.CertificateChain;
import org.cagrid.gaards.cds.common.ClientDelegationFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditRecord;
import org.cagrid.gaards.cds.common.DelegatedCredentialEvent;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.common.DelegationRequest;
import org.cagrid.gaards.cds.common.DelegationSigningRequest;
import org.cagrid.gaards.cds.common.DelegationSigningResponse;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.common.Errors;
import org.cagrid.gaards.cds.common.ExpirationStatus;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.service.policy.PolicyHandler;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.InvalidPolicyFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.cds.testutils.CA;
import org.cagrid.gaards.cds.testutils.Constants;
import org.cagrid.gaards.cds.testutils.Utils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.globus.gsi.CertificateRevocationLists;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.proxy.ProxyPathValidator;

public class DelegatedCredentialManagerTest extends TestCase {

	private static String GRID_IDENTITY = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=user";

	private CA ca;

	private File caCert;
	
	private String caDN;

	private int DEFAULT_PROXY_LIFETIME_SECONDS = 300;

	private int PROXY_BUFFER_MAX_COUNT = 8;

	private int PROXY_BUFFER_TIME_MULTIPLIER = 2;
	
	

	public void testDelegatedCredentialCreateDestroy() {
		try {
			DelegatedCredentialManager dcm = Utils
					.getDelegatedCredentialManager();
			dcm.clearDatabase();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}
	}

	public void testChangeKeyManager() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			try {
				new DelegatedCredentialManager(Utils.getDatabase(), Utils
						.getPropertyManager(), new InvalidKeyManager(),
						new ArrayList<PolicyHandler>(), new ProxyPolicy(),
						Utils.getEventManager());
				fail("Should not be able to change Key Manager.");
			} catch (CDSInternalFault e) {
				if (!e.getFaultString().equals(Errors.KEY_MANAGER_CHANGED)) {
					fail("Should not be able to change Key Manager.");
				}
			}
			assertEquals(
					0,
					dcm.searchAuditLog(new DelegatedCredentialAuditFilter()).length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInvalidPolicy() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			try {
				DelegationRequest req = new DelegationRequest();
				req.setDelegationPolicy(new InvalidDelegationPolicy());
				req.setKeyLength(Constants.KEY_LENGTH);
				req
						.setIssuedCredentialPathLength(Constants.DELEGATION_PATH_LENGTH);
				dcm.initiateDelegation("some user", req);
				fail("Should not be able to delegate a credential with an invalid delegation policy.");
			} catch (InvalidPolicyFault e) {

			}
			assertEquals(
					0,
					dcm.searchAuditLog(new DelegatedCredentialAuditFilter()).length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInvalidProxyLifetime() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			try {
				DelegationRequest req = getSimpleDelegationRequest();
				req.setIssuedCredentialLifetime(null);
				dcm.initiateDelegation("some user", req);
				fail("Should not be able to delegate a credential without a delegate proxy lifetime specified.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.PROXY_LIFETIME_NOT_SPECIFIED)) {
					fail("Should not be able to delegate a credential without a delegate proxy lifetime specified.");
				}
			}
			assertEquals(
					0,
					dcm.searchAuditLog(new DelegatedCredentialAuditFilter()).length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInvalidKeyLength() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();

			DelegationRequest req = getSimpleDelegationRequest();
			req.setKeyLength(1);

			try {
				dcm.initiateDelegation("some user", req);
				fail("Should not be able to delegate a credential with an invalid Key Length.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.INVALID_KEY_LENGTH_SPECIFIED)) {
					fail("Should not be able to delegate a credential with an invalid Key Length.");
				}
			}
			assertEquals(
					0,
					dcm.searchAuditLog(new DelegatedCredentialAuditFilter()).length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInvalidDelegationPathLength() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();

			DelegationRequest req = getSimpleDelegationRequest();
			req.setIssuedCredentialPathLength(1);

			try {
				dcm.initiateDelegation("some user", req);
				fail("Should not be able to delegate a credential with an invalid delegation path length.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.INVALID_DELEGATION_PATH_LENGTH_SPECIFIED)) {
					fail("Should not be able to delegate a credential with an invalid delegation path length.");
				}
			}
			assertEquals(
					0,
					dcm.searchAuditLog(new DelegatedCredentialAuditFilter()).length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInitiatorDoesNotMatchApprover() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();

			String alias = "some user";
			assertEquals(0,
					dcm.searchAuditLog(Utils.getInitiatedAuditFilter()).length);
			DelegationSigningRequest req = dcm.initiateDelegation(alias,
					getSimpleDelegationRequest());
			assertEquals(1,
					dcm.searchAuditLog(Utils.getInitiatedAuditFilter()).length);
			try {
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(ca.createProxy(alias, 0)
								.getCertificateChain()));
				dcm.approveDelegation("some other user", res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.INITIATOR_DOES_NOT_MATCH_APPROVER)) {
					fail("Should not be able to approve delegation.");
				}
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialApproveWithInvalidStatus() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			DelegatedCredential dc = delegateAndValidate(dcm, alias, cred
					.getIdentity(), null);
			try {
				dcm.approveDelegation(cred.getIdentity(), dc
						.getSigningResponse());
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.CANNOT_APPROVE_INVALID_STATUS)) {
					fail("Should not be able to approve delegation.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialsWithIdentityDelegationPolicy() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			List<DelegationIdentifier> list = new ArrayList<DelegationIdentifier>();
			int size = 3;

			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();
			for (int i = 0; i < size; i++) {
				IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
				String[] users = new String[i + 1];
				for (int j = 0; j <= i; j++) {
					users[j] = GRID_IDENTITY + (j + 1);
				}
				AllowedParties ap = new AllowedParties();
				ap.setGridIdentity(users);
				policy.setAllowedParties(ap);
				list.add(delegateAndValidate(dcm, alias, gridIdentity, policy,
						i).getDelegationIdentifier());
			}
			for (int i = 0; i < list.size(); i++) {
				DelegationIdentifier id = list.get(i);
				assertTrue(dcm.delegationExists(id));
				dcm.delete(id);
				assertFalse(dcm.delegationExists(id));
				DelegatedCredentialAuditFilter f = new DelegatedCredentialAuditFilter();
				f.setDelegationIdentifier(id);
				assertEquals(0, dcm.searchAuditLog(f).length);
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialNoCertificateChain() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "some user";
			GlobusCredential cred = ca.createCredential(alias);
			DelegationSigningRequest req = dcm.initiateDelegation(cred
					.getIdentity(), getSimpleDelegationRequest());
			try {
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(null);
				dcm.approveDelegation(cred.getIdentity(), res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.CERTIFICATE_CHAIN_NOT_SPECIFIED)) {
					fail("Should not be able to approve delegation.");
				}
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInsufficientCertificateChain() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "some user";
			GlobusCredential cred = ca.createCredential(alias);
			DelegationSigningRequest req = dcm.initiateDelegation(cred
					.getIdentity(), getSimpleDelegationRequest());
			try {

				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(cred.getCertificateChain()));
				dcm.approveDelegation(cred.getIdentity(), res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.INSUFFICIENT_CERTIFICATE_CHAIN_SPECIFIED)) {
					fail("Should not be able to approve delegation.");
				}
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialMismatchingPublicKeys() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "some user";
			GlobusCredential cred = ca.createCredential(alias);
			DelegationSigningRequest req = dcm.initiateDelegation(cred
					.getIdentity(), getSimpleDelegationRequest());
			try {
				GlobusCredential proxy = ca.createProxy(alias, 1);
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(proxy.getCertificateChain()));
				dcm.approveDelegation(cred.getIdentity(), res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString()
						.equals(Errors.PUBLIC_KEY_DOES_NOT_MATCH)) {
					fail("Should not be able to approve delegation.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInvalidProxyIdentity() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "some user";
			GlobusCredential cred = ca.createCredential(alias);
			DelegationSigningRequest req = dcm.initiateDelegation(cred
					.getIdentity(), getSimpleDelegationRequest());
			try {
				PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
						.getKeyAsString());
				X509Certificate[] certs = ca.createProxyCertifcates(
						alias + "2", publicKey, 1);
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(certs));
				dcm.approveDelegation(cred.getIdentity(), res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.IDENTITY_DOES_NOT_MATCH_INITIATOR)) {
					fail("Should not be able to approve delegation.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInvalidProxyChain() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "some user";
			GlobusCredential cred = ca.createCredential(alias);
			DelegationSigningRequest req = dcm.initiateDelegation(cred
					.getIdentity(), getSimpleDelegationRequest());
			try {
				PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
						.getKeyAsString());
				X509Certificate[] certs = ca.createProxyCertifcates(alias,
						publicKey, 1);
				X509Certificate[] certs2 = ca.createProxyCertifcates(alias + 2,
						publicKey, 1);
				certs[1] = certs2[1];
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(certs));
				dcm.approveDelegation(cred.getIdentity(), res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString()
						.equals(Errors.INVALID_CERTIFICATE_CHAIN)) {
					fail("Should not be able to approve delegation.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialInsufficientDelegationPathLength() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "some user";
			GlobusCredential cred = ca.createCredential(alias);
			DelegationSigningRequest req = dcm.initiateDelegation(cred
					.getIdentity(), getSimpleDelegationRequest());
			try {
				PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
						.getKeyAsString());
				X509Certificate[] certs = ca.createProxyCertifcates(alias,
						publicKey, 0);
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(certs));
				dcm.approveDelegation(cred.getIdentity(), res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.INSUFFICIENT_DELEGATION_PATH_LENGTH)) {
					fail("Should not be able to approve delegation.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testDelegateCredentialApprovalBufferExpired() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			DelegatedCredentialManager.DELEGATION_BUFFER_SECONDS = 1;
			String alias = "some user";
			GlobusCredential cred = ca.createCredential(alias);
			DelegationSigningRequest req = dcm.initiateDelegation(cred
					.getIdentity(), getSimpleDelegationRequest());
			try {
				PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
						.getKeyAsString());
				X509Certificate[] certs = ca.createProxyCertifcates(alias,
						publicKey, 1);
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(req.getDelegationIdentifier());
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(certs));
				Thread
						.sleep(((DelegatedCredentialManager.DELEGATION_BUFFER_SECONDS * 1000) + 100));
				dcm.approveDelegation(cred.getIdentity(), res);
				fail("Should not be able to approve delegation.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.DELEGATION_APPROVAL_BUFFER_EXPIRED)) {
					fail("Should not be able to approve delegation.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			DelegatedCredentialManager.DELEGATION_BUFFER_SECONDS = 120;
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testGetNonExistingDelegatedCredential() {

		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			KeyPair pair = KeyUtil.generateRSAKeyPair1024();
			org.cagrid.gaards.cds.common.PublicKey publicKey = new org.cagrid.gaards.cds.common.PublicKey();
			publicKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
			DelegationIdentifier id = new DelegationIdentifier();
			id.setDelegationId(2);
			try {
				dcm.getDelegatedCredential(GRID_IDENTITY, id, publicKey);
				fail("Should not be able get delegated credential.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.DELEGATION_RECORD_DOES_NOT_EXIST)) {
					fail("Should not be able get delegated credential.");
				}
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	public void testGetDelegatedCredentialUnAuthorizedUser() {

		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();
			IdentityDelegationPolicy policy = getSimplePolicy();
			DelegationIdentifier id = this.delegateAndValidate(dcm, alias,
					gridIdentity, policy).getDelegationIdentifier();
			KeyPair pair = KeyUtil.generateRSAKeyPair1024();
			org.cagrid.gaards.cds.common.PublicKey publicKey = new org.cagrid.gaards.cds.common.PublicKey();
			publicKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
			try {
				dcm.getDelegatedCredential(GRID_IDENTITY + 2, id, publicKey);
				fail("Should not be able get delegated credential.");
			} catch (PermissionDeniedFault e) {
				if (!e.getFaultString().equals(
						Errors.PERMISSION_DENIED_TO_DELEGATED_CREDENTIAL)) {
					fail("Should not be able get delegated credential.");
				}
			}
			assertEquals(0,
					dcm.searchAuditLog(Utils.getIssuedAuditFilter(id)).length);
			DelegatedCredentialAuditRecord[] ar = dcm.searchAuditLog(Utils
					.getAccessDeniedAuditFilter(id));
			assertEquals(1, ar.length);
			assertEquals(id, ar[0].getDelegationIdentifier());
			assertEquals(GRID_IDENTITY + 2, ar[0].getSourceGridIdentity());
			assertEquals(
					DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
					ar[0].getEvent());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	public void testGetDelegatedCredentialExpiredSigningCredential() {

		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();

			boolean ok = false;
			int count = 1;
			int seconds = 2;
			while (!ok && (count <= PROXY_BUFFER_MAX_COUNT)) {
				DelegationRequest request = getSimpleDelegationRequest();
				DelegationSigningRequest req = dcm.initiateDelegation(
						gridIdentity, request);
				DelegationIdentifier id = req.getDelegationIdentifier();
				PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
						.getKeyAsString());
				
				X509Certificate[] proxy = this.ca.createProxyCertifcates(alias,
						publicKey, 1, 0, 0, seconds);
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(id);
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(proxy));
				dcm.approveDelegation(gridIdentity, res);
				KeyPair pair = KeyUtil.generateRSAKeyPair1024();
				org.cagrid.gaards.cds.common.PublicKey pKey = new org.cagrid.gaards.cds.common.PublicKey();
				pKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
				Thread.sleep(((seconds * 1000) + 100));
				DelegationRecordFilter f = new DelegationRecordFilter();
				f.setDelegationIdentifier(id);
				f.setExpirationStatus(ExpirationStatus.Expired);
				validateFind(dcm, f, 1);
				try {
					dcm.getDelegatedCredential(GRID_IDENTITY, id, pKey);
					fail("Should not be able get delegated credential.");
				} catch (DelegationFault e) {
					if (e.getFaultString().equals(
							Errors.SIGNING_CREDENTIAL_EXPIRED)) {
						ok = true;
					}
				}
				assertEquals(0, dcm.searchAuditLog(Utils
						.getIssuedAuditFilter(id)).length);
				DelegatedCredentialAuditRecord[] ar = dcm.searchAuditLog(Utils
						.getAccessDeniedAuditFilter(id));
				assertEquals(1, ar.length);
				assertEquals(id, ar[0].getDelegationIdentifier());
				assertEquals(GRID_IDENTITY, ar[0].getSourceGridIdentity());
				assertEquals(
						DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
						ar[0].getEvent());
				seconds = seconds * PROXY_BUFFER_TIME_MULTIPLIER;
				count = count+1;
			}

			if (!ok) {
				fail("Unable to validate testGetDelegatedCredentialSignatureCredentialAboutToExpire.");
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	public void testGetDelegatedCredentialInvalidStatus() {

		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();
			DelegationRequest request = getSimpleDelegationRequest();
			DelegationSigningRequest req = dcm.initiateDelegation(gridIdentity,
					request);
			DelegationIdentifier id = req.getDelegationIdentifier();

			KeyPair pair = KeyUtil.generateRSAKeyPair1024();
			org.cagrid.gaards.cds.common.PublicKey pKey = new org.cagrid.gaards.cds.common.PublicKey();
			pKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));

			try {
				dcm.getDelegatedCredential(GRID_IDENTITY, id, pKey);
			} catch (DelegationFault e) {
				if (!e.getFaultString()
						.equals(Errors.CANNOT_GET_INVALID_STATUS)) {
					fail("Should not be able get delegated credential.");
				}
			}

			assertEquals(0,
					dcm.searchAuditLog(Utils.getIssuedAuditFilter(id)).length);
			DelegatedCredentialAuditRecord[] ar = dcm.searchAuditLog(Utils
					.getAccessDeniedAuditFilter(id));
			assertEquals(1, ar.length);
			assertEquals(id, ar[0].getDelegationIdentifier());
			assertEquals(GRID_IDENTITY, ar[0].getSourceGridIdentity());
			assertEquals(
					DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
					ar[0].getEvent());

			PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
					.getKeyAsString());
			X509Certificate[] proxy = this.ca.createProxyCertifcates(alias,
					publicKey, 1);
			DelegationSigningResponse res = new DelegationSigningResponse();
			res.setDelegationIdentifier(id);
			res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(proxy));

			dcm.approveDelegation(gridIdentity, res);

			DelegatedCredentialAuditRecord[] ar2 = dcm.searchAuditLog(Utils
					.getApprovedAuditFilter(id));
			assertEquals(1, ar2.length);
			assertEquals(id, ar2[0].getDelegationIdentifier());
			assertEquals(gridIdentity, ar2[0].getSourceGridIdentity());
			assertEquals(DelegatedCredentialEvent.DelegationApproved, ar2[0]
					.getEvent());

			dcm.getDelegatedCredential(GRID_IDENTITY, id, pKey);

			DelegatedCredentialAuditRecord[] ar3 = dcm.searchAuditLog(Utils
					.getIssuedAuditFilter(id));
			assertEquals(1, ar3.length);
			assertEquals(id, ar3[0].getDelegationIdentifier());
			assertEquals(GRID_IDENTITY, ar3[0].getSourceGridIdentity());
			assertEquals(DelegatedCredentialEvent.DelegatedCredentialIssued,
					ar3[0].getEvent());

			dcm.updateDelegatedCredentialStatus(gridIdentity, id,
					DelegationStatus.Suspended);

			DelegatedCredentialAuditRecord[] ar4 = dcm.searchAuditLog(Utils
					.getUpdateStatusAuditFilter(id));
			assertEquals(1, ar4.length);
			assertEquals(id, ar4[0].getDelegationIdentifier());
			assertEquals(gridIdentity, ar4[0].getSourceGridIdentity());
			assertEquals(DelegatedCredentialEvent.DelegationStatusUpdated,
					ar4[0].getEvent());
			try {
				dcm.getDelegatedCredential(GRID_IDENTITY, id, pKey);
				fail("Should not be able get delegated credential.");
			} catch (DelegationFault e) {
				if (!e.getFaultString()
						.equals(Errors.CANNOT_GET_INVALID_STATUS)) {
					fail("Should not be able get delegated credential.");
				}
			}
			assertEquals(2, dcm.searchAuditLog(Utils
					.getAccessDeniedAuditFilter(id)).length);
			dcm.updateDelegatedCredentialStatus(gridIdentity, id,
					DelegationStatus.Approved);
			assertEquals(2, dcm.searchAuditLog(Utils
					.getUpdateStatusAuditFilter(id)).length);
			dcm.getDelegatedCredential(GRID_IDENTITY, id, pKey);
			assertEquals(2,
					dcm.searchAuditLog(Utils.getIssuedAuditFilter(id)).length);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	public void testGetDelegatedCredentialSignatureCredentialAboutToExpire() {

		DelegatedCredentialManager dcm = null;
		int originalProxyExpiration = DelegatedCredentialManager.PROXY_EXPIRATION_BUFFER_SECONDS;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();

			boolean ok = false;
			int count = 1;

			while (!ok && (count <= PROXY_BUFFER_MAX_COUNT)) {

				DelegationRequest request = getSimpleDelegationRequest();
				DelegationSigningRequest req = dcm.initiateDelegation(
						gridIdentity, request);
				DelegationIdentifier id = req.getDelegationIdentifier();

				KeyPair pair = KeyUtil.generateRSAKeyPair1024();
				org.cagrid.gaards.cds.common.PublicKey pKey = new org.cagrid.gaards.cds.common.PublicKey();
				pKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));

				PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
						.getKeyAsString());

				X509Certificate[] proxy = this.ca
						.createProxyCertifcates(
								alias,
								publicKey,
								1,
								0,
								0,
								DelegatedCredentialManager.PROXY_EXPIRATION_BUFFER_SECONDS);
				DelegationSigningResponse res = new DelegationSigningResponse();
				res.setDelegationIdentifier(id);
				res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
						.toCertificateChain(proxy));
				dcm.approveDelegation(gridIdentity, res);

				DelegatedCredentialAuditRecord[] ar1 = dcm.searchAuditLog(Utils
						.getApprovedAuditFilter(id));
				assertEquals(1, ar1.length);
				assertEquals(id, ar1[0].getDelegationIdentifier());
				assertEquals(gridIdentity, ar1[0].getSourceGridIdentity());
				assertEquals(DelegatedCredentialEvent.DelegationApproved,
						ar1[0].getEvent());

				try {
					dcm.getDelegatedCredential(GRID_IDENTITY, id, pKey);
					fail("Should not be able get delegated credential.");
				} catch (DelegationFault e) {
					if (e.getFaultString().equals(
							Errors.SIGNING_CREDENTIAL_ABOUT_EXPIRE)) {
						ok = true;
					} else if (!e.getFaultString().equals(
							Errors.SIGNING_CREDENTIAL_EXPIRED)) {
						FaultUtil.printFault(e);
						fail("Unexpected error encountered");
					}
				}

				DelegatedCredentialAuditRecord[] ar2 = dcm.searchAuditLog(Utils
						.getAccessDeniedAuditFilter(id));
				assertEquals(1, ar2.length);
				assertEquals(id, ar2[0].getDelegationIdentifier());
				assertEquals(GRID_IDENTITY, ar2[0].getSourceGridIdentity());
				assertEquals(
						DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
						ar2[0].getEvent());
				DelegatedCredentialManager.PROXY_EXPIRATION_BUFFER_SECONDS = DelegatedCredentialManager.PROXY_EXPIRATION_BUFFER_SECONDS
						* PROXY_BUFFER_TIME_MULTIPLIER;
				count = count + 1;
			}

			if (!ok) {
				fail("Unable to validate testGetDelegatedCredentialSignatureCredentialAboutToExpire.");
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			DelegatedCredentialManager.PROXY_EXPIRATION_BUFFER_SECONDS = originalProxyExpiration;
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	// ///////////////////////////////////////////////////////////////////////////////
	/* LEFT OFF HERE */
	// ///////////////////////////////////////////////////////////////////////////////
	public void testGetDelegatedCredentialInvalidKeyLength() {

		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();
			DelegationRequest request = getSimpleDelegationRequest();
			DelegationSigningRequest req = dcm.initiateDelegation(gridIdentity,
					request);
			DelegationIdentifier id = req.getDelegationIdentifier();

			KeyPair pair = KeyUtil.generateRSAKeyPair512();
			org.cagrid.gaards.cds.common.PublicKey pKey = new org.cagrid.gaards.cds.common.PublicKey();
			pKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));

			PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
					.getKeyAsString());
			X509Certificate[] proxy = this.ca.createProxyCertifcates(alias,
					publicKey, 1);
			DelegationSigningResponse res = new DelegationSigningResponse();
			res.setDelegationIdentifier(id);
			res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(proxy));
			dcm.approveDelegation(gridIdentity, res);

			DelegatedCredentialAuditRecord[] ar1 = dcm.searchAuditLog(Utils
					.getApprovedAuditFilter(id));
			assertEquals(1, ar1.length);
			assertEquals(id, ar1[0].getDelegationIdentifier());
			assertEquals(gridIdentity, ar1[0].getSourceGridIdentity());
			assertEquals(DelegatedCredentialEvent.DelegationApproved, ar1[0]
					.getEvent());

			try {
				dcm.getDelegatedCredential(GRID_IDENTITY, id, pKey);
				fail("Should not be able get delegated credential.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.INVALID_KEY_LENGTH_SPECIFIED)) {
					fail("Should not be able get delegated credential.");
				}
			}

			DelegatedCredentialAuditRecord[] ar2 = dcm.searchAuditLog(Utils
					.getAccessDeniedAuditFilter(id));
			assertEquals(1, ar2.length);
			assertEquals(id, ar2[0].getDelegationIdentifier());
			assertEquals(GRID_IDENTITY, ar2[0].getSourceGridIdentity());
			assertEquals(
					DelegatedCredentialEvent.DelegatedCredentialAccessDenied,
					ar2[0].getEvent());

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	public void testGetDelegatedCredentialShorterThanNormalProxy() {

		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();
			int hours = 0;
			int minutes = 0;
			int seconds = 120;
			DelegatedCredential dc = this.delegateAndValidate(dcm, alias,
					gridIdentity, null, hours, minutes, seconds);
			GlobusCredential delegatedProxy = getDelegatedCredentialAndValidate(
					dcm, GRID_IDENTITY, dc.getDelegationIdentifier(), dc
							.getSigningResponse().getCertificateChain());
			assertTrue((delegatedProxy.getTimeLeft() <= seconds));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}
	}

	public void testGetDelegatedCredential() {

		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential cred = ca.createCredential(alias);
			String gridIdentity = cred.getIdentity();
			int hours = 12;
			int minutes = 0;
			int seconds = 0;
			DelegatedCredential dc = this.delegateAndValidate(dcm, alias,
					gridIdentity, null, hours, minutes, seconds);
			GlobusCredential delegatedProxy = getDelegatedCredentialAndValidate(
					dcm, GRID_IDENTITY, dc.getDelegationIdentifier(), dc
							.getSigningResponse().getCertificateChain());
			assertTrue((delegatedProxy.getTimeLeft() <= DEFAULT_PROXY_LIFETIME_SECONDS));
			assertTrue((delegatedProxy.getTimeLeft() > (DEFAULT_PROXY_LIFETIME_SECONDS - 60)));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	public void testFindCredentialsDelegatedToClient() {
		DelegatedCredentialManager dcm = null;
		try {
			dcm = Utils.getDelegatedCredentialManager();
			String alias = "jdoe";
			GlobusCredential jdoe = ca.createCredential(alias);
			GlobusCredential enemy = ca.createCredential("enemy");
			DelegatedCredential dc = this.delegateAndValidate(dcm, alias, jdoe
					.getIdentity(), null, 12, 0, 0);
			assertEquals(0, dcm.findCredentialsDelegatedToClient(enemy
					.getIdentity(), new ClientDelegationFilter()).length);
			DelegationRecord[] records = dcm.findCredentialsDelegatedToClient(
					GRID_IDENTITY, null);
			assertEquals(1, records.length);
			assertEquals(dc.getDelegationIdentifier(), records[0]
					.getDelegationIdentifier());
			assertEquals(jdoe.getIdentity(), records[0].getGridIdentity());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				dcm.clearDatabase();
			} catch (Exception e) {
			}
		}

	}

	private GlobusCredential getDelegatedCredentialAndValidate(
			DelegatedCredentialManager dcm, String gridIdentity,
			DelegationIdentifier id, CertificateChain chain) throws Exception {
		X509Certificate[] signingChain = org.cagrid.gaards.cds.common.Utils
				.toCertificateArray(chain);
		KeyPair pair = KeyUtil.generateRSAKeyPair1024();
		org.cagrid.gaards.cds.common.PublicKey pKey = new org.cagrid.gaards.cds.common.PublicKey();
		pKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
		X509Certificate[] delegatedProxy = org.cagrid.gaards.cds.common.Utils
				.toCertificateArray(dcm.getDelegatedCredential(GRID_IDENTITY,
						id, pKey));
		assertNotNull(delegatedProxy);
		assertEquals(delegatedProxy.length, (signingChain.length + 1));
		for (int i = 0; i < signingChain.length; i++) {
			assertEquals(signingChain[i], delegatedProxy[i + 1]);
		}

		ProxyPathValidator validator = new ProxyPathValidator();
		validator.validate(delegatedProxy, TrustedCertificates
				.getDefaultTrustedCertificates().getCertificates(),
				CertificateRevocationLists
						.getDefaultCertificateRevocationLists());

		DelegatedCredentialAuditRecord[] ar = dcm.searchAuditLog(Utils
				.getIssuedAuditFilter(id));
		assertEquals(1, ar.length);
		assertEquals(id, ar[0].getDelegationIdentifier());
		assertEquals(GRID_IDENTITY, ar[0].getSourceGridIdentity());
		assertEquals(DelegatedCredentialEvent.DelegatedCredentialIssued, ar[0]
				.getEvent());

		return new GlobusCredential(pair.getPrivate(), delegatedProxy);
	}

	protected DelegationRequest getSimpleDelegationRequest() {
		DelegationRequest req = new DelegationRequest();
		req.setDelegationPolicy(getSimplePolicy());
		req.setKeyLength(Constants.KEY_LENGTH);
		req.setIssuedCredentialPathLength(Constants.DELEGATION_PATH_LENGTH);
		ProxyLifetime lifetime = new ProxyLifetime();
		lifetime.setHours(0);
		lifetime.setMinutes(0);
		lifetime.setSeconds(DEFAULT_PROXY_LIFETIME_SECONDS);
		req.setIssuedCredentialLifetime(lifetime);
		return req;
	}

	protected IdentityDelegationPolicy getSimplePolicy() {
		IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
		AllowedParties ap = new AllowedParties();
		ap.setGridIdentity(new String[] { GRID_IDENTITY });
		policy.setAllowedParties(ap);
		return policy;
	}

	protected DelegatedCredential delegateAndValidate(
			DelegatedCredentialManager dcm, String alias, String gridIdentity,
			DelegationPolicy policy) throws Exception {
		return delegateAndValidate(dcm, alias, gridIdentity, policy, 12, 0, 0);
	}

	protected DelegatedCredential delegateAndValidate(
			DelegatedCredentialManager dcm, String alias, String gridIdentity,
			DelegationPolicy policy, int expected) throws Exception {
		return delegateAndValidate(dcm, alias, gridIdentity, policy, 12, 0, 0,
				expected);
	}

	protected DelegatedCredential delegateAndValidate(
			DelegatedCredentialManager dcm, String alias, String gridIdentity,
			DelegationPolicy policy, int hours, int minutes, int seconds)
			throws Exception {
		return delegateAndValidate(dcm, alias, gridIdentity, policy, hours,
				minutes, seconds, 0);

	}

	protected DelegatedCredential delegateAndValidate(
			DelegatedCredentialManager dcm, String alias, String gridIdentity,
			DelegationPolicy policy, int hours, int minutes, int seconds,
			int delegationCount) throws Exception {

		DelegationRecordFilter f = new DelegationRecordFilter();

		validateFind(dcm, f, delegationCount);

		DelegationRequest request = getSimpleDelegationRequest();
		if (policy != null) {
			request.setDelegationPolicy(policy);
		}
		DelegationSigningRequest req = dcm.initiateDelegation(gridIdentity,
				request);

		DelegationIdentifier id = req.getDelegationIdentifier();
		assertNotNull(id);
		assertTrue(dcm.delegationExists(id));
		DelegationRecord r = dcm.getDelegationRecord(id);
		assertEquals(id, r.getDelegationIdentifier());
		assertEquals(gridIdentity, r.getGridIdentity());
		assertEquals(0, r.getDateApproved());
		assertEquals(0, r.getExpiration());
		assertEquals(DelegationStatus.Pending, r.getDelegationStatus());
		assertTrue((0 < r.getDateInitiated()));
		assertEquals(request.getIssuedCredentialPathLength(), r
				.getIssuedCredentialPathLength());
		assertEquals(request.getDelegationPolicy(), r.getDelegationPolicy());
		assertNotNull(r.getCertificateChain());
		assertNull(r.getCertificateChain().getX509Certificate());

		// Validate Auditing
		DelegatedCredentialAuditRecord[] ar1 = dcm.searchAuditLog(Utils
				.getInitiatedAuditFilter(id));
		assertEquals(1, ar1.length);
		assertEquals(id, ar1[0].getDelegationIdentifier());
		assertEquals(DelegatedCredentialEvent.DelegationInitiated, ar1[0]
				.getEvent());
		assertEquals(gridIdentity, ar1[0].getSourceGridIdentity());

		// Validate Find Operation

		validateFind(dcm, f, (delegationCount + 1));
		resetFilter(f);
		f.setDelegationIdentifier(req.getDelegationIdentifier());
		validateFind(dcm, f, 1);
		resetFilter(f);
		f.setGridIdentity(gridIdentity);
		validateFind(dcm, f, (delegationCount + 1));
		resetFilter(f);
		f.setExpirationStatus(ExpirationStatus.Valid);
		validateFind(dcm, f, (delegationCount));
		resetFilter(f);
		f.setExpirationStatus(ExpirationStatus.Expired);
		validateFind(dcm, f, 0);
		resetFilter(f);
		f.setDelegationStatus(DelegationStatus.Suspended);
		validateFind(dcm, f, 0);
		resetFilter(f);
		f.setDelegationStatus(DelegationStatus.Pending);
		validateFind(dcm, f, 1);
		resetFilter(f);
		f.setDelegationStatus(DelegationStatus.Approved);
		validateFind(dcm, f, delegationCount);
		resetFilter(f);
		f.setDelegationIdentifier(req.getDelegationIdentifier());
		f.setGridIdentity(gridIdentity);
		f.setDelegationStatus(DelegationStatus.Pending);
		validateFind(dcm, f, 1);

		PublicKey publicKey = KeyUtil.loadPublicKey(req.getPublicKey()
				.getKeyAsString());
		X509Certificate[] proxy = this.ca.createProxyCertifcates(alias,
				publicKey, 1, hours, minutes, seconds);
		assertNotNull(proxy);
		DelegationSigningResponse res = new DelegationSigningResponse();
		res.setDelegationIdentifier(id);
		res.setCertificateChain(org.cagrid.gaards.cds.common.Utils
				.toCertificateChain(proxy));
		dcm.approveDelegation(gridIdentity, res);

		DelegationRecord r2 = dcm.getDelegationRecord(id);
		assertEquals(gridIdentity, r2.getGridIdentity());
		assertTrue(0 < r2.getDateApproved());
		assertEquals(org.cagrid.gaards.cds.common.Utils.getEarliestExpiration(
				proxy).getTime(), r2.getExpiration());
		assertEquals(DelegationStatus.Approved, r2.getDelegationStatus());
		assertEquals(r.getDateInitiated(), r2.getDateInitiated());
		assertEquals(r.getIssuedCredentialPathLength(), r2
				.getIssuedCredentialPathLength());
		assertEquals(request.getDelegationPolicy(), r2.getDelegationPolicy());
		X509Certificate[] chain = org.cagrid.gaards.cds.common.Utils
				.toCertificateArray(r2.getCertificateChain());
		assertNotNull(chain);
		assertEquals(proxy.length, chain.length);
		for (int i = 0; i < proxy.length; i++) {
			assertEquals(proxy[i], chain[i]);
		}

		// Validate Auditing
		DelegatedCredentialAuditRecord[] ar2 = dcm.searchAuditLog(Utils
				.getApprovedAuditFilter(id));
		assertEquals(1, ar2.length);
		assertEquals(id, ar2[0].getDelegationIdentifier());
		assertEquals(DelegatedCredentialEvent.DelegationApproved, ar2[0]
				.getEvent());
		assertEquals(gridIdentity, ar2[0].getSourceGridIdentity());

		// Validate Find Operation
		resetFilter(f);
		validateFind(dcm, f, (delegationCount + 1));
		resetFilter(f);
		f.setDelegationIdentifier(req.getDelegationIdentifier());
		validateFind(dcm, f, 1);
		resetFilter(f);
		f.setGridIdentity(gridIdentity);
		validateFind(dcm, f, (delegationCount + 1));
		resetFilter(f);
		f.setExpirationStatus(ExpirationStatus.Valid);
		validateFind(dcm, f, (delegationCount + 1));
		resetFilter(f);
		f.setExpirationStatus(ExpirationStatus.Expired);
		validateFind(dcm, f, 0);
		resetFilter(f);
		f.setDelegationStatus(DelegationStatus.Suspended);
		validateFind(dcm, f, 0);
		resetFilter(f);
		f.setDelegationStatus(DelegationStatus.Pending);
		validateFind(dcm, f, 0);
		resetFilter(f);
		f.setDelegationStatus(DelegationStatus.Approved);
		validateFind(dcm, f, delegationCount + 1);
		resetFilter(f);
		f.setDelegationIdentifier(req.getDelegationIdentifier());
		f.setGridIdentity(gridIdentity);
		f.setDelegationStatus(DelegationStatus.Approved);
		validateFind(dcm, f, 1);

		return new DelegatedCredential(req, res);
	}

	protected void resetFilter(DelegationRecordFilter f) throws Exception {
		f.setDelegationIdentifier(null);
		f.setGridIdentity(null);
		f.setDelegationStatus(null);
		f.setExpirationStatus(null);
	}

	protected void validateFind(DelegatedCredentialManager dcm,
			DelegationRecordFilter f, int expectedCount) throws Exception {
		DelegationRecord[] records = dcm.findDelegatedCredentials(f);
		assertEquals(expectedCount, records.length);
		if (f.getDelegationIdentifier() != null) {
			for (int i = 0; i < records.length; i++) {
				assertEquals(f.getDelegationIdentifier(), records[i]
						.getDelegationIdentifier());
			}
		}

		if (f.getGridIdentity() != null) {
			for (int i = 0; i < records.length; i++) {
				assertEquals(f.getGridIdentity(), records[i].getGridIdentity());
			}
		}

		if (f.getDelegationStatus() != null) {
			for (int i = 0; i < records.length; i++) {
				assertEquals(f.getDelegationStatus(), records[i]
						.getDelegationStatus());
			}
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		Utils.getDatabase().createDatabaseIfNeeded();
		try {
			Date now = new Date();	
			this.caDN = "O=Delegation Credential Manager,OU="+now.getTime()+",CN=Certificate Authority";
			this.ca = new CA(this.caDN);
			File f = gov.nih.nci.cagrid.common.Utils
					.getTrustedCerificatesDirectory();
			f.mkdirs();
			caCert = new File(f.getAbsoluteFile() + File.separator
					+ now.getTime()+".0");
			CertUtil.writeCertificate(this.ca.getCertificate(), caCert);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	protected void tearDown() throws Exception {
		super.setUp();
		caCert.delete();
	}

	public class InvalidKeyManager implements KeyManager {

		public KeyPair createAndStoreKeyPair(String alias, int keyLength)
				throws CDSInternalFault {
			return null;
		}

		public void delete(String alias) throws CDSInternalFault {

		}

		public void deleteAll() throws CDSInternalFault {

		}

		public boolean exists(String alias) throws CDSInternalFault {
			return false;
		}

		public X509Certificate[] getCertificates(String alias)
				throws CDSInternalFault {
			return null;
		}

		public PrivateKey getPrivateKey(String alias) throws CDSInternalFault {
			return null;
		}

		public PublicKey getPublicKey(String alias) throws CDSInternalFault {
			return null;
		}

		public void storeCertificates(String alias, X509Certificate[] cert)
				throws CDSInternalFault, DelegationFault {

		}

	}

}
