package org.cagrid.gaards.cds.service.policy;

import gov.nih.nci.cagrid.common.FaultUtil;
import junit.framework.TestCase;

import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.service.InvalidDelegationPolicy;
import org.cagrid.gaards.cds.stubs.types.InvalidPolicyFault;
import org.cagrid.gaards.cds.testutils.Utils;

public class IdentityPolicyHandlerTest extends TestCase {

	private static String GRID_IDENTITY = "/C=US/O=abc/OU=xyz/OU=caGrid/CN=jdoe";

	public void testCreateDestroy() {
		IdentityPolicyHandler handler = null;
		try {
			handler = Utils.getIdentityPolicyHandler();
			handler.removeAllStoredPolicies();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				handler.removeAllStoredPolicies();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testStoreInvalidPolicy() {
		IdentityPolicyHandler handler = null;
		try {
			DelegationIdentifier id = new DelegationIdentifier();
			id.setDelegationId(1);
			handler = Utils.getIdentityPolicyHandler();
			try {
				handler.storePolicy(id, new InvalidDelegationPolicy());
				fail("Should not be able to store invalid policy.");
			} catch (InvalidPolicyFault e) {
				String s = e.getFaultString();
				String expected = "The policy handler "
						+ IdentityPolicyHandler.class.getName()
						+ " does not support the policy";
				if (s.indexOf(expected) == -1) {
					fail("Should not be able to store invalid policy.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				handler.removeAllStoredPolicies();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testStoreEmptyPolicy() {
		IdentityPolicyHandler handler = null;
		try {
			DelegationIdentifier id = new DelegationIdentifier();
			id.setDelegationId(1);
			handler = Utils.getIdentityPolicyHandler();
			IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
			try {
				handler.storePolicy(id, policy);
				fail("Should not be able to store empty policy.");
			} catch (InvalidPolicyFault e) {
				String s = e.getFaultString();
				String expected = "No allowed parties provided.";
				if (s.indexOf(expected) == -1) {
					fail("Should not be able to store empty policy.");
				}
			}
			AllowedParties ap = new AllowedParties();
			policy.setAllowedParties(ap);
			try {
				handler.storePolicy(id, policy);
				fail("Should not be able to store empty policy.");
			} catch (InvalidPolicyFault e) {
				String s = e.getFaultString();
				String expected = "No allowed parties provided.";
				if (s.indexOf(expected) == -1) {
					fail("Should not be able to store empty policy.");
				}
			}

			ap.setGridIdentity(new String[0]);
			try {
				handler.storePolicy(id, policy);
				fail("Should not be able to store empty policy.");
			} catch (InvalidPolicyFault e) {
				String s = e.getFaultString();
				String expected = "No allowed parties provided.";
				if (s.indexOf(expected) == -1) {
					fail("Should not be able to store empty policy.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				handler.removeAllStoredPolicies();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testStoreExistingPolicy() {
		IdentityPolicyHandler handler = null;
		try {
			DelegationIdentifier id = new DelegationIdentifier();
			id.setDelegationId(1);
			handler = Utils.getIdentityPolicyHandler();
			IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
			AllowedParties ap = new AllowedParties();
			ap
					.setGridIdentity(new String[] { GRID_IDENTITY });
			policy.setAllowedParties(ap);
			handler.storePolicy(id, policy);
			assertTrue(handler.policyExists(id));
			try {
				handler.storePolicy(id, policy);
				fail("Should not be able to store a policy that already exists.");
			} catch (InvalidPolicyFault e) {
				String s = e.getFaultString();
				String expected = "A policy already exists for the delegation";
				if (s.indexOf(expected) == -1) {
					fail("Should not be able to store a policy that already exists.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				handler.removeAllStoredPolicies();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void testGetInvalidPolicy() {
		IdentityPolicyHandler handler = null;
		try {
			handler = Utils.getIdentityPolicyHandler();
			DelegationIdentifier id = new DelegationIdentifier();
			id.setDelegationId(1);
			try {
				handler.getPolicy(id);
				fail("Should not be able to get a policy that does not exists.");
			} catch (InvalidPolicyFault e) {
				String s = e.getFaultString();
				String expected = "The requested policy does not exist.";
				if (s.indexOf(expected) == -1) {
					fail("Should not be able to get a policy that does not exists.");
				}
			}
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				handler.removeAllStoredPolicies();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void testStoreGetAndRemovePolicy() {
		IdentityPolicyHandler handler = null;
		try {
			int count = 3;
			handler = Utils.getIdentityPolicyHandler();
			for (int i = 1; i <= count; i++) {
				DelegationIdentifier id = new DelegationIdentifier();
				id.setDelegationId(i);
				assertFalse(handler.policyExists(id));
				IdentityDelegationPolicy policy = new IdentityDelegationPolicy();
				AllowedParties ap = new AllowedParties();
				String[] parties = new String[i];
				for (int j = 0; j < i; j++) {
					parties[j] = GRID_IDENTITY + j;
				}
				ap.setGridIdentity(parties);
				policy.setAllowedParties(ap);
				handler.storePolicy(id, policy);
				assertTrue(handler.policyExists(id));
				assertEquals(policy, handler.getPolicy(id));
			}

			// Check Authorization
			for (int i = 1; i <= count; i++) {
				DelegationIdentifier id = new DelegationIdentifier();
				id.setDelegationId(i);
				for (int j = 0; j < count; j++) {
					String gridIdentity = GRID_IDENTITY + j;
					if (j < i) {
						assertTrue(handler.isAuthorized(id, gridIdentity));
					} else {
						assertFalse(handler.isAuthorized(id, gridIdentity));
					}
				}
			}

			// Check Delete

			for (int i = 1; i <= count; i++) {
				DelegationIdentifier id = new DelegationIdentifier();
				id.setDelegationId(i);
				assertTrue(handler.policyExists(id));
				handler.removePolicy(id);
				assertFalse(handler.policyExists(id));
				for (int j = 0; j < count; j++) {
					String gridIdentity = GRID_IDENTITY + j;
					assertFalse(handler.isAuthorized(id, gridIdentity));
				}

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				handler.removeAllStoredPolicies();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		Utils.getDatabase().createDatabaseIfNeeded();
	}
}
