package org.cagrid.gaards.cds.service;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditFilter;
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
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.cds.testutils.CA;
import org.cagrid.gaards.cds.testutils.Constants;
import org.cagrid.gaards.cds.testutils.Utils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.tools.groups.Group;
import org.cagrid.tools.groups.GroupManager;
import org.globus.gsi.GlobusCredential;

public class DelegationManagerTest extends TestCase {

	private int DEFAULT_PROXY_LIFETIME_SECONDS = 300;
	private final static String ADMIN_ALIAS = "admin";

	private CA ca;
	private File caCert;
	private File caCertSigning;
	private String caDN;

	public void testDelegatedCredentialCreateDestroy() {
		try {
			DelegationManager cds = Utils.getCDS();
			cds.clear();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}
	}

	public void testManagesAdmins() {
		DelegationManager cds = null;
		try {
			GlobusCredential admin = addInitialAdmin();
			cds = Utils.getCDS();
			List<String> admins = new ArrayList<String>();
			admins.add(CertUtil.getIdentity(admin));
			checkAdminList(CertUtil.getIdentity(admin), cds, admins);
			String userPrefix = "/O=XYZ/OU=ABC/CN=user";
			int count = 3;
			for (int i = 0; i < count; i++) {
				String user = userPrefix + i;
				try {
					cds.addAdmin(user, user);
					fail("Should not be able to execute admin operation.");
				} catch (PermissionDeniedFault e) {
					if (!e.getFaultString().equals(Errors.ADMIN_REQUIRED)) {
						fail("Should not be able to execute admin operation.");
					}
				}

				try {
					cds.removeAdmin(user, user);
					fail("Should not be able to execute admin operation.");
				} catch (PermissionDeniedFault e) {
					if (!e.getFaultString().equals(Errors.ADMIN_REQUIRED)) {
						fail("Should not be able to execute admin operation.");
					}
				}

				try {
					cds.getAdmins(user);
					fail("Should not be able to execute admin operation.");
				} catch (PermissionDeniedFault e) {
					if (!e.getFaultString().equals(Errors.ADMIN_REQUIRED)) {
						fail("Should not be able to execute admin operation.");
					}
				}
				cds.addAdmin(CertUtil.getIdentity(admin), user);
				admins.add(user);
				this.checkAdminList(user, cds, admins);
			}

			for (int i = 0; i < count; i++) {
				String user = userPrefix + i;
				cds.removeAdmin(CertUtil.getIdentity(admin), user);
				admins.remove(user);
				try {
					cds.getAdmins(user);
					fail("Should not be able to execute admin operation.");
				} catch (PermissionDeniedFault e) {
					if (!e.getFaultString().equals(Errors.ADMIN_REQUIRED)) {
						fail("Should not be able to execute admin operation.");
					}
				}
				this.checkAdminList(CertUtil.getIdentity(admin), cds, admins);
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (cds != null) {
				try {
					cds.clear();
				} catch (Exception e) {
				}
			}
		}
	}

	private void checkAdminList(String admin, DelegationManager cds,
			List<String> expected) throws Exception {
		assertNotNull(expected);
		assertNotNull(admin);
		assertNotNull(cds);
		String[] actual = cds.getAdmins(admin);
		assertNotNull(actual);
		assertEquals(expected.size(), actual.length);
		for (int i = 0; i < expected.size(); i++) {
			boolean found = false;
			for (int j = 0; j < actual.length; j++) {
				if (actual[j].equals(expected.get(i))) {
					found = true;
					break;
				}
			}
			if (!found) {
				fail("Did not find an expected administrator.");
			}
		}
	}

	private GlobusCredential addInitialAdmin() throws Exception {
		GlobusCredential admin = ca.createCredential(ADMIN_ALIAS);
		GroupManager gm = Utils.getGroupManager();
		Group admins = null;
		if (!gm.groupExists(DelegationManager.ADMINISTRATORS)) {
			gm.addGroup(DelegationManager.ADMINISTRATORS);
			admins = gm.getGroup(DelegationManager.ADMINISTRATORS);
		} else {
			admins = gm.getGroup(DelegationManager.ADMINISTRATORS);
		}
		admins.addMember(CertUtil.getIdentity(admin));
		assertTrue(admins.isMember(CertUtil.getIdentity(admin)));
		return admin;
	}

	public void testUpdateDelegationStatusNonAdminUser() {
		DelegationManager cds = null;
		try {
			cds = Utils.getCDS();
			String leonardoAlias = "leonardo";
			String donatelloAlias = "donatello";

			GlobusCredential leonardoCred = ca.createCredential(leonardoAlias);
			GlobusCredential donatelloCred = ca
					.createCredential(donatelloAlias);

			DelegationPolicy policy = getSimplePolicy(
			    CertUtil.getIdentity(donatelloCred));

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					CertUtil.getIdentity(leonardoCred),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(CertUtil.getIdentity(leonardoCred), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();
			try {
				cds.updateDelegatedCredentialStatus(
						CertUtil.getIdentity(donatelloCred), id,
						DelegationStatus.Suspended);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(leonardoCred),
						id, DelegationStatus.Approved);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(leonardoCred),
						id, DelegationStatus.Pending);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(leonardoCred), id,
					DelegationStatus.Suspended);

			DelegationRecordFilter f = new DelegationRecordFilter();
			f.setDelegationIdentifier(id);
			DelegationRecord[] records = cds.findDelegatedCredentials(
					CertUtil.getIdentity(leonardoCred), f);
			assertNotNull(records);
			assertEquals(1, records.length);
			assertEquals(DelegationStatus.Suspended, records[0]
					.getDelegationStatus());

			try {
				cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(leonardoCred),
						id, DelegationStatus.Suspended);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(leonardoCred),
						id, DelegationStatus.Approved);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(leonardoCred),
						id, DelegationStatus.Pending);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (cds != null) {
				try {
					cds.clear();
				} catch (Exception e) {
				}
			}
		}
	}

	public void testUpdateDelegationStatusAdminUser() {
		DelegationManager cds = null;
		try {
			cds = Utils.getCDS();
			GlobusCredential admin = addInitialAdmin();
			String leonardoAlias = "leonardo";
			String donatelloAlias = "donatello";

			GlobusCredential leonardoCred = ca.createCredential(leonardoAlias);
			GlobusCredential donatelloCred = ca
					.createCredential(donatelloAlias);

			DelegationPolicy policy = getSimplePolicy(
			    CertUtil.getIdentity(donatelloCred));

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					CertUtil.getIdentity(leonardoCred),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(CertUtil.getIdentity(leonardoCred), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();

			try {
				cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(admin), id,
						DelegationStatus.Pending);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.CANNOT_CHANGE_STATUS_TO_PENDING)) {
					fail("Should not be able to update the status of the delegated credential.");
				}
			}

			cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(admin), id,
					DelegationStatus.Suspended);

			DelegationRecordFilter f = new DelegationRecordFilter();
			f.setDelegationIdentifier(id);
			DelegationRecord[] records = cds.findDelegatedCredentials(
					CertUtil.getIdentity(leonardoCred), f);
			assertNotNull(records);
			assertEquals(1, records.length);
			assertEquals(DelegationStatus.Suspended, records[0]
					.getDelegationStatus());

			cds.updateDelegatedCredentialStatus(CertUtil.getIdentity(admin), id,
					DelegationStatus.Approved);

			records = cds.findDelegatedCredentials(CertUtil.getIdentity(leonardoCred),
					f);
			assertNotNull(records);
			assertEquals(1, records.length);
			assertEquals(DelegationStatus.Approved, records[0]
					.getDelegationStatus());

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (cds != null) {
				try {
					cds.clear();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public void testDeleteDelegatedCredential() {
		DelegationManager cds = null;
		try {
			cds = Utils.getCDS();
			GlobusCredential admin = addInitialAdmin();
			String leonardoAlias = "leonardo";
			String donatelloAlias = "donatello";

			GlobusCredential leonardoCred = ca.createCredential(leonardoAlias);
			GlobusCredential donatelloCred = ca.createCredential(donatelloAlias);

			DelegationPolicy policy = getSimplePolicy(
			    CertUtil.getIdentity(donatelloCred));

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					CertUtil.getIdentity(leonardoCred),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(
							    leonardoReq.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(CertUtil.getIdentity(leonardoCred), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();
			DelegationRecordFilter f = new DelegationRecordFilter();
			f.setDelegationIdentifier(id);
			assertEquals(1, cds.findDelegatedCredentials(CertUtil.getIdentity(admin), f).length);


			try {
				cds.deleteDelegatedCredential(
				    CertUtil.getIdentity(leonardoCred), id);
				fail("Should not be able to delete the delegate credentail.");
			} catch (PermissionDeniedFault e) {
				if (!e
						.getFaultString()
						.equals(
								Errors.ADMIN_REQUIRED)) {
					fail("Should not be able to delete the delegate credentail.");
				}
			}
			
			try {
				cds.deleteDelegatedCredential(
				    CertUtil.getIdentity(donatelloCred), id);
				fail("Should not be able to delete the delegate credentail.");
			} catch (PermissionDeniedFault e) {
				if (!e
						.getFaultString()
						.equals(
								Errors.ADMIN_REQUIRED)) {
					fail("Should not be able to delete the delegate credentail.");
				}
			}
			cds.deleteDelegatedCredential(CertUtil.getIdentity(admin), id);
			assertEquals(0, cds.findDelegatedCredentials(CertUtil.getIdentity(admin), f).length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (cds != null) {
				try {
					cds.clear();
				} catch (Exception e) {
				}
			}
		}
	}


	public void testAuditAdminUser() {
		DelegationManager cds = null;
		try {
			cds = Utils.getCDS();
			GlobusCredential admin = addInitialAdmin();
			String leonardoAlias = "leonardo";
			String donatelloAlias = "donatello";

			GlobusCredential leonardoCred = ca.createCredential(leonardoAlias);
			GlobusCredential donatelloCred = ca.createCredential(donatelloAlias);

			DelegationPolicy policy = getSimplePolicy(
			    CertUtil.getIdentity(donatelloCred));

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					CertUtil.getIdentity(leonardoCred),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(
			    leonardoReq.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(
							    leonardoReq.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(CertUtil.getIdentity(leonardoCred), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();

			DelegatedCredentialAuditFilter f = null;

			try {
				cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(leonardoCred), f);
				fail("Should not be able to search the audit log.");
			} catch (PermissionDeniedFault e) {
				if (!e
						.getFaultString()
						.equals(
								Errors.PERMISSION_DENIED_NO_DELEGATED_CREDENTIAL_SPECIFIED)) {
					fail("Should not be able to search the audit log.");
				}
			}
			f = new DelegatedCredentialAuditFilter();

			cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(admin), f);
			assertEquals(2, cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(admin), f).length);

			f.setDelegationIdentifier(id);
			assertEquals(2, cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(admin), f).length);

			try {
				cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(donatelloCred), f);
				fail("Should not be able to search the audit log.");
			} catch (PermissionDeniedFault e) {
				if (!e.getFaultString().equals(
						Errors.PERMISSION_DENIED_TO_AUDIT)) {
					fail("Should not be able to search the audit log.");
				}
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (cds != null) {
				try {
					cds.clear();
				} catch (Exception e) {
				}
			}
		}
	}

	public void testAuditNonAdminUser() {
		DelegationManager cds = null;
		try {
			cds = Utils.getCDS();
			String leonardoAlias = "leonardo";
			String donatelloAlias = "donatello";

			GlobusCredential leonardoCred = ca.createCredential(leonardoAlias);
			GlobusCredential donatelloCred = ca
					.createCredential(donatelloAlias);

			DelegationPolicy policy = getSimplePolicy(CertUtil.getIdentity(donatelloCred));

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					CertUtil.getIdentity(leonardoCred),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(CertUtil.getIdentity(leonardoCred), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();

			DelegatedCredentialAuditFilter f = null;

			try {
				cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(leonardoCred), f);
				fail("Should not be able to search the audit log.");
			} catch (PermissionDeniedFault e) {
				if (!e
						.getFaultString()
						.equals(
								Errors.PERMISSION_DENIED_NO_DELEGATED_CREDENTIAL_SPECIFIED)) {
					fail("Should not be able to search the audit log.");
				}
			}
			f = new DelegatedCredentialAuditFilter();
			try {
				cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(leonardoCred), f);
				fail("Should not be able to search the audit log.");
			} catch (PermissionDeniedFault e) {
				if (!e
						.getFaultString()
						.equals(
								Errors.PERMISSION_DENIED_NO_DELEGATED_CREDENTIAL_SPECIFIED)) {
					fail("Should not be able to search the audit log.");
				}
			}

			f.setDelegationIdentifier(id);
			assertEquals(2, cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(leonardoCred), f).length);

			try {
				cds.searchDelegatedCredentialAuditLog(CertUtil.getIdentity(donatelloCred), f);
				fail("Should not be able to search the audit log.");
			} catch (PermissionDeniedFault e) {
				if (!e.getFaultString().equals(
						Errors.PERMISSION_DENIED_TO_AUDIT)) {
					fail("Should not be able to search the audit log.");
				}
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (cds != null) {
				try {
					cds.clear();
				} catch (Exception e) {
				}
			}
		}
	}

	public void testFindDelegatedCredentials() {
		DelegationManager cds = null;
		try {
			cds = Utils.getCDS();
			GlobusCredential admin = addInitialAdmin();
			String leonardoAlias = "leonardo";
			String donatelloAlias = "donatello";
			String michelangeloAlias = "michelangelo";

			GlobusCredential leonardoCred = ca.createCredential(leonardoAlias);
			GlobusCredential donatelloCred = ca
					.createCredential(donatelloAlias);
			GlobusCredential michelangeloCred = ca
					.createCredential(michelangeloAlias);

			DelegationPolicy policy = getSimplePolicy(CertUtil.getIdentity(michelangeloCred));

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					CertUtil.getIdentity(leonardoCred),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(CertUtil.getIdentity(leonardoCred), leonardoRes);

			DelegationSigningRequest donatelloReq = cds.initiateDelegation(
					CertUtil.getIdentity(donatelloCred),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse donatelloRes = new DelegationSigningResponse();
			donatelloRes.setDelegationIdentifier(donatelloReq
					.getDelegationIdentifier());
			donatelloRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							donatelloAlias, KeyUtil.loadPublicKey(donatelloReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(CertUtil.getIdentity(donatelloCred), donatelloRes);

			cds.initiateDelegation(CertUtil.getIdentity(donatelloCred),
					getSimpleDelegationRequest(policy));

			DelegationRecordFilter f = new DelegationRecordFilter();
			validateFind(cds, CertUtil.getIdentity(admin), f, 3,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 2);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setDelegationIdentifier(leonardoReq.getDelegationIdentifier());
			validateFind(cds, CertUtil.getIdentity(admin), f, 1,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 0);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setDelegationIdentifier(donatelloReq.getDelegationIdentifier());
			validateFind(cds, CertUtil.getIdentity(admin), f, 1,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 0);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setGridIdentity(CertUtil.getIdentity(leonardoCred));
			validateFind(cds, CertUtil.getIdentity(admin), f, 1,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 2);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setGridIdentity(CertUtil.getIdentity(donatelloCred));
			validateFind(cds, CertUtil.getIdentity(admin), f, 2,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 2);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setGridIdentity(CertUtil.getIdentity(michelangeloCred));
			validateFind(cds, CertUtil.getIdentity(admin), f, 0,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 2);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setExpirationStatus(ExpirationStatus.Valid);
			validateFind(cds, CertUtil.getIdentity(admin), f, 2,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setExpirationStatus(ExpirationStatus.Expired);
			validateFind(cds, CertUtil.getIdentity(admin), f, 0,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 0);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 0);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setDelegationStatus(DelegationStatus.Approved);
			validateFind(cds, CertUtil.getIdentity(admin), f, 2,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

			resetFilter(f);
			f.setDelegationStatus(DelegationStatus.Pending);
			validateFind(cds, CertUtil.getIdentity(admin), f, 1,false);
			validateFindMy(cds, CertUtil.getIdentity(leonardoCred), f, 0);
			validateFindMy(cds, CertUtil.getIdentity(donatelloCred), f, 1);
			validateFindMy(cds, CertUtil.getIdentity(michelangeloCred), f, 0);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			if (cds != null) {
				try {
					cds.clear();
				} catch (Exception e) {
				}
			}
		}
	}

	protected void resetFilter(DelegationRecordFilter f) throws Exception {
		f.setDelegationIdentifier(null);
		f.setGridIdentity(null);
		f.setDelegationStatus(null);
		f.setExpirationStatus(null);
	}

	protected void validateFindMy(DelegationManager cds, String gridIdentity,
			DelegationRecordFilter f, int expectedCount) throws Exception {
		validateFind(cds, gridIdentity, f, expectedCount, true);
	}

	protected void validateFind(DelegationManager cds, String gridIdentity,
			DelegationRecordFilter f, int expectedCount, boolean matchIds)
			throws Exception {
		DelegationRecord[] records = cds.findDelegatedCredentials(gridIdentity,
				f);
		assertEquals(expectedCount, records.length);
		if (f.getDelegationIdentifier() != null) {
			for (int i = 0; i < records.length; i++) {
				assertEquals(f.getDelegationIdentifier(), records[i]
						.getDelegationIdentifier());
			}
		}
		if (matchIds) {
			for (int i = 0; i < records.length; i++) {
				assertEquals(gridIdentity, records[i].getGridIdentity());
			}
		}

		if (f.getDelegationStatus() != null) {
			for (int i = 0; i < records.length; i++) {
				assertEquals(f.getDelegationStatus(), records[i]
						.getDelegationStatus());
			}
		}
	}

	protected IdentityDelegationPolicy getSimplePolicy(String gridIdentity) {
		IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
		AllowedParties ap = new AllowedParties();
		ap.setGridIdentity(new String[] { gridIdentity });
		policy.setAllowedParties(ap);
		return policy;
	}

	protected DelegationRequest getSimpleDelegationRequest(
			DelegationPolicy policy) {
		DelegationRequest req = new DelegationRequest();
		req.setDelegationPolicy(policy);
		req.setKeyLength(Constants.KEY_LENGTH);
		req.setIssuedCredentialPathLength(Constants.DELEGATION_PATH_LENGTH);
		ProxyLifetime lifetime = new ProxyLifetime();
		lifetime.setHours(0);
		lifetime.setMinutes(0);
		lifetime.setSeconds(DEFAULT_PROXY_LIFETIME_SECONDS);
		req.setIssuedCredentialLifetime(lifetime);
		return req;
	}

	protected void setUp() throws Exception {
		super.setUp();
		Utils.getDatabase().createDatabaseIfNeeded();
		try {
			Date now = new Date();	
			this.caDN = "CN=Certificate Authority,OU=" + now.getTime() + ",O=Delegation Credential Manager";
			this.ca = new CA(this.caDN);
			File f = gov.nih.nci.cagrid.common.Utils
					.getTrustedCerificatesDirectory();
			f.mkdirs();
			caCert = new File(f.getAbsoluteFile() + File.separator
					+ now.getTime()+".0");
			caCertSigning = new File(f.getAbsoluteFile() + File.separator
					+ now.getTime()+".signing_policy");
			CertUtil.writeCertificate(this.ca.getCertificate(), caCert);
			CertUtil.writeSigningPolicy(this.ca.getCertificate(), caCertSigning);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	protected void tearDown() throws Exception {
		super.setUp();
		caCert.delete();
		caCertSigning.delete();
	}

}
