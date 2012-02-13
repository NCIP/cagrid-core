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
			admins.add(admin.getIdentity());
			checkAdminList(admin.getIdentity(), cds, admins);
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
				cds.addAdmin(admin.getIdentity(), user);
				admins.add(user);
				this.checkAdminList(user, cds, admins);
			}

			for (int i = 0; i < count; i++) {
				String user = userPrefix + i;
				cds.removeAdmin(admin.getIdentity(), user);
				admins.remove(user);
				try {
					cds.getAdmins(user);
					fail("Should not be able to execute admin operation.");
				} catch (PermissionDeniedFault e) {
					if (!e.getFaultString().equals(Errors.ADMIN_REQUIRED)) {
						fail("Should not be able to execute admin operation.");
					}
				}
				this.checkAdminList(admin.getIdentity(), cds, admins);
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
		admins.addMember(admin.getIdentity());
		assertTrue(admins.isMember(admin.getIdentity()));
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

			DelegationPolicy policy = getSimplePolicy(donatelloCred
					.getIdentity());

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					leonardoCred.getIdentity(),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(leonardoCred.getIdentity(), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();
			try {
				cds.updateDelegatedCredentialStatus(
						donatelloCred.getIdentity(), id,
						DelegationStatus.Suspended);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(leonardoCred.getIdentity(),
						id, DelegationStatus.Approved);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(leonardoCred.getIdentity(),
						id, DelegationStatus.Pending);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			cds.updateDelegatedCredentialStatus(leonardoCred.getIdentity(), id,
					DelegationStatus.Suspended);

			DelegationRecordFilter f = new DelegationRecordFilter();
			f.setDelegationIdentifier(id);
			DelegationRecord[] records = cds.findDelegatedCredentials(
					leonardoCred.getIdentity(), f);
			assertNotNull(records);
			assertEquals(1, records.length);
			assertEquals(DelegationStatus.Suspended, records[0]
					.getDelegationStatus());

			try {
				cds.updateDelegatedCredentialStatus(leonardoCred.getIdentity(),
						id, DelegationStatus.Suspended);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(leonardoCred.getIdentity(),
						id, DelegationStatus.Approved);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (PermissionDeniedFault e) {

			}

			try {
				cds.updateDelegatedCredentialStatus(leonardoCred.getIdentity(),
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

			DelegationPolicy policy = getSimplePolicy(donatelloCred
					.getIdentity());

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					leonardoCred.getIdentity(),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(leonardoCred.getIdentity(), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();

			try {
				cds.updateDelegatedCredentialStatus(admin.getIdentity(), id,
						DelegationStatus.Pending);
				fail("Should not be able to update the status of the delegated credential.");
			} catch (DelegationFault e) {
				if (!e.getFaultString().equals(
						Errors.CANNOT_CHANGE_STATUS_TO_PENDING)) {
					fail("Should not be able to update the status of the delegated credential.");
				}
			}

			cds.updateDelegatedCredentialStatus(admin.getIdentity(), id,
					DelegationStatus.Suspended);

			DelegationRecordFilter f = new DelegationRecordFilter();
			f.setDelegationIdentifier(id);
			DelegationRecord[] records = cds.findDelegatedCredentials(
					leonardoCred.getIdentity(), f);
			assertNotNull(records);
			assertEquals(1, records.length);
			assertEquals(DelegationStatus.Suspended, records[0]
					.getDelegationStatus());

			cds.updateDelegatedCredentialStatus(admin.getIdentity(), id,
					DelegationStatus.Approved);

			records = cds.findDelegatedCredentials(leonardoCred.getIdentity(),
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
			GlobusCredential donatelloCred = ca
					.createCredential(donatelloAlias);

			DelegationPolicy policy = getSimplePolicy(donatelloCred
					.getIdentity());

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					leonardoCred.getIdentity(),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(leonardoCred.getIdentity(), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();
			DelegationRecordFilter f = new DelegationRecordFilter();
			f.setDelegationIdentifier(id);
			assertEquals(1, cds.findDelegatedCredentials(admin.getIdentity(), f).length);


			try {
				cds.deleteDelegatedCredential(leonardoCred
						.getIdentity(), id);
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
				cds.deleteDelegatedCredential(donatelloCred
						.getIdentity(), id);
				fail("Should not be able to delete the delegate credentail.");
			} catch (PermissionDeniedFault e) {
				if (!e
						.getFaultString()
						.equals(
								Errors.ADMIN_REQUIRED)) {
					fail("Should not be able to delete the delegate credentail.");
				}
			}
			cds.deleteDelegatedCredential(admin.getIdentity(), id);
			assertEquals(0, cds.findDelegatedCredentials(admin.getIdentity(), f).length);
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
			GlobusCredential donatelloCred = ca
					.createCredential(donatelloAlias);

			DelegationPolicy policy = getSimplePolicy(donatelloCred
					.getIdentity());

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					leonardoCred.getIdentity(),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(leonardoCred.getIdentity(), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();

			DelegatedCredentialAuditFilter f = null;

			try {
				cds.searchDelegatedCredentialAuditLog(leonardoCred
						.getIdentity(), f);
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

			cds.searchDelegatedCredentialAuditLog(admin.getIdentity(), f);
			assertEquals(2, cds.searchDelegatedCredentialAuditLog(admin
					.getIdentity(), f).length);

			f.setDelegationIdentifier(id);
			assertEquals(2, cds.searchDelegatedCredentialAuditLog(admin
					.getIdentity(), f).length);

			try {
				cds.searchDelegatedCredentialAuditLog(donatelloCred
						.getIdentity(), f);
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

			DelegationPolicy policy = getSimplePolicy(donatelloCred
					.getIdentity());

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					leonardoCred.getIdentity(),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(leonardoCred.getIdentity(), leonardoRes);
			DelegationIdentifier id = leonardoReq.getDelegationIdentifier();

			DelegatedCredentialAuditFilter f = null;

			try {
				cds.searchDelegatedCredentialAuditLog(leonardoCred
						.getIdentity(), f);
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
				cds.searchDelegatedCredentialAuditLog(leonardoCred
						.getIdentity(), f);
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
			assertEquals(2, cds.searchDelegatedCredentialAuditLog(leonardoCred
					.getIdentity(), f).length);

			try {
				cds.searchDelegatedCredentialAuditLog(donatelloCred
						.getIdentity(), f);
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

			DelegationPolicy policy = getSimplePolicy(michelangeloCred
					.getIdentity());

			DelegationSigningRequest leonardoReq = cds.initiateDelegation(
					leonardoCred.getIdentity(),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse leonardoRes = new DelegationSigningResponse();
			leonardoRes.setDelegationIdentifier(leonardoReq
					.getDelegationIdentifier());
			leonardoRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							leonardoAlias, KeyUtil.loadPublicKey(leonardoReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(leonardoCred.getIdentity(), leonardoRes);

			DelegationSigningRequest donatelloReq = cds.initiateDelegation(
					donatelloCred.getIdentity(),
					getSimpleDelegationRequest(policy));
			DelegationSigningResponse donatelloRes = new DelegationSigningResponse();
			donatelloRes.setDelegationIdentifier(donatelloReq
					.getDelegationIdentifier());
			donatelloRes.setCertificateChain(org.cagrid.gaards.cds.common.Utils
					.toCertificateChain(ca.createProxyCertifcates(
							donatelloAlias, KeyUtil.loadPublicKey(donatelloReq
									.getPublicKey().getKeyAsString()), 2)));
			cds.approveDelegation(donatelloCred.getIdentity(), donatelloRes);

			cds.initiateDelegation(donatelloCred.getIdentity(),
					getSimpleDelegationRequest(policy));

			DelegationRecordFilter f = new DelegationRecordFilter();
			validateFind(cds, admin.getIdentity(), f, 3,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 1);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 2);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setDelegationIdentifier(leonardoReq.getDelegationIdentifier());
			validateFind(cds, admin.getIdentity(), f, 1,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 1);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 0);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setDelegationIdentifier(donatelloReq.getDelegationIdentifier());
			validateFind(cds, admin.getIdentity(), f, 1,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 0);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 1);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setGridIdentity(leonardoCred.getIdentity());
			validateFind(cds, admin.getIdentity(), f, 1,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 1);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 2);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setGridIdentity(donatelloCred.getIdentity());
			validateFind(cds, admin.getIdentity(), f, 2,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 1);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 2);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setGridIdentity(michelangeloCred.getIdentity());
			validateFind(cds, admin.getIdentity(), f, 0,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 1);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 2);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setExpirationStatus(ExpirationStatus.Valid);
			validateFind(cds, admin.getIdentity(), f, 2,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 1);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 1);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setExpirationStatus(ExpirationStatus.Expired);
			validateFind(cds, admin.getIdentity(), f, 0,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 0);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 0);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setDelegationStatus(DelegationStatus.Approved);
			validateFind(cds, admin.getIdentity(), f, 2,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 1);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 1);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

			resetFilter(f);
			f.setDelegationStatus(DelegationStatus.Pending);
			validateFind(cds, admin.getIdentity(), f, 1,false);
			validateFindMy(cds, leonardoCred.getIdentity(), f, 0);
			validateFindMy(cds, donatelloCred.getIdentity(), f, 1);
			validateFindMy(cds, michelangeloCred.getIdentity(), f, 0);

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

}
