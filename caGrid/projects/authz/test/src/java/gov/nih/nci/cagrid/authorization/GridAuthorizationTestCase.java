package gov.nih.nci.cagrid.authorization;

import gov.nih.nci.cagrid.authorization.impl.CSMGridAuthorizationManager;
import gov.nih.nci.cagrid.gridgrouper.grouper.GrouperI;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.authentication.principal.LoginIdPrincipal;
import gov.nih.nci.security.authorization.domainobjects.Group;
import gov.nih.nci.security.exceptions.CSException;
import gov.nih.nci.security.authorization.jaas.AccessPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GridAuthorizationTestCase extends TestCase {

	// private String localUsr = "reader1";
	//
	// private String localPwd = "reader1";
	//
	// private String gridIdentity =
	// "/CN=test2/ST=MD/C=US/E=me@somewhere.com/O=semanticbits/OU=dev";
	//
	// private String serviceUrl = "http://someservice/url";
	//
	// private String app = "SDK";
	//
	// private String objectId = "gov.nih.nci.cabio.domain.Gene";
	//
	// private String privilege = "READ";

	// private CSMGridAuthorizationManager mgr;
	//
	// private Map accessibleGroups = new HashMap();
	//
	private Map userGroups = new HashMap();

	private String localUserId;

	private String objectId;

	private String attributeId;

	private String privilege;

	private MockAuthorizationManager csmMgr;

	private CSMGridAuthorizationManager gridMgr;

	private String gridGrouperGroup;

	private String gridGrouperUrl;

	private String gridUserId;

	private MockGridGrouperClient gridGrouperClient;

	private MockGridGrouperClientFactory fact;

	private AccessPermission accessPermission;

	private Subject localSubject;

	private Subject gridSubject;

	public GridAuthorizationTestCase() {
		init();
	}

	public GridAuthorizationTestCase(String name) {
		super(name);
		init();
	}

	private void init() {
		// this.mgr = new CSMGridAuthorizationManager();
		//		
		// //Set up some CSM groups
		// String[] groupNames = new String[] { "localGroup",
		// "http://some.host:1234/some/GridGrouperService",
		// "http://some.otherhost:5678/someother/GridGrouperService"};
		// List csmGroups = new ArrayList();
		// for(String groupName : groupNames){
		// Group group = new Group();
		// group.setGroupName(groupName);
		// }

	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();

		suite.addTest(new GridAuthorizationTestCase("testIsAuthorized1"));
		suite.addTest(new GridAuthorizationTestCase("testIsAuthorized2"));
		suite.addTest(new GridAuthorizationTestCase("testCheckPermission1"));
		suite.addTest(new GridAuthorizationTestCase("testCheckPermission2"));
		suite.addTest(new GridAuthorizationTestCase("testCheckPermission3"));
		suite.addTest(new GridAuthorizationTestCase("testCheckPermission4"));

		return suite;
	}

	public void setUp() {
		// Create local policy

		this.localUserId = "someLocalUser";
		this.objectId = "some.object.ID";
		this.attributeId = "someAtt";
		this.privilege = "somePrivilege";
		this.accessPermission = new MockAccessPermission(this.objectId,
				this.privilege);
		this.localSubject = new Subject();
		this.localSubject.getPrincipals().add(
				new LoginIdPrincipal(this.localUserId));
		this.csmMgr = new MockAuthorizationManager();
		this.csmMgr.grantPrivilegeToUser(this.localUserId, this.objectId,
				this.privilege);
		this.csmMgr.grantPrivilegeToUser(this.localUserId, this.objectId,
				this.attributeId, this.privilege);
		this.gridMgr = new CSMGridAuthorizationManager();
		this.gridMgr.setAuthorizationManager(this.csmMgr);

		// Create grid policy
		this.gridGrouperGroup = "some:grid:group";
		this.gridGrouperUrl = "http://some.host/SomeService";
		this.gridUserId = "O=someorg/CN=someuser";
		this.gridSubject = new Subject();
		this.gridSubject.getPrincipals().add(
				new LoginIdPrincipal(this.gridUserId));
		this.gridGrouperClient = new MockGridGrouperClient();
		this.gridGrouperClient.addUserToGroup(this.gridUserId,
				this.gridGrouperGroup);
		this.csmMgr.addAccessibleGroup("{" + this.gridGrouperUrl + "}"
				+ this.gridGrouperGroup, this.objectId, this.privilege);
		this.csmMgr.addAccessibleGroup("{" + this.gridGrouperUrl + "}"
				+ this.gridGrouperGroup, this.objectId, this.attributeId,
				this.privilege);
		this.fact = new MockGridGrouperClientFactory(this.gridGrouperClient);
		this.gridMgr.setGridGrouperClientFactory(this.fact);
	}

	public void tearDown() {

	}

	public void testTimeout() {
		long stallTime = 5000;
		this.gridGrouperClient.setStallTime(stallTime);
		this.gridMgr.setGridGrouperThreadTimeout(stallTime - 1000);
		try {
			this.gridMgr.isAuthorized(this.gridUserId, this.objectId,
					this.privilege);
			fail("Should have thrown exception because of timeout");
		} catch (Exception ex) {
			assertEquals(
					"Error checking authorization: GridGrouper search did not finish",
					ex.getMessage());
		}
		this.gridMgr.setGridGrouperThreadTimeout(stallTime + 1000);
		boolean isAuthorized = false;
		try {
			isAuthorized = this.gridMgr.isAuthorized(this.gridUserId,
					this.objectId, this.privilege);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Should not have thrown exception");
		}
		assertTrue(this.gridUserId + " should have " + this.privilege + " on "
				+ this.objectId, isAuthorized);
	}

	public void testIsAuthorized1() {
		assertTrue(this.localUserId + " should have " + this.privilege + " on "
				+ this.objectId, this.gridMgr.isAuthorized(this.localUserId,
				this.objectId, this.privilege));
		assertTrue(this.localUserId + " should NOT have " + this.privilege
				+ " on yadda", !this.gridMgr.isAuthorized(this.localUserId,
				"yadda", this.privilege));
		assertTrue(this.gridUserId + " should have " + this.privilege + " on "
				+ this.objectId, this.gridMgr.isAuthorized(this.gridUserId,
				this.objectId, this.privilege));
		assertTrue(this.gridUserId + " should have " + this.privilege
				+ " on yadda", !this.gridMgr.isAuthorized(this.gridUserId,
				"yadda", this.privilege));
	}

	public void testIsAuthorized2() {
		assertTrue(this.localUserId + " should have " + this.privilege + " on "
				+ makeKey(this.objectId, this.attributeId), this.gridMgr
				.isAuthorized(this.localUserId, this.objectId,
						this.attributeId, this.privilege));
		assertTrue(this.localUserId + " should NOT have " + this.privilege
				+ " on " + makeKey("yadda", this.attributeId), !this.gridMgr
				.isAuthorized(this.localUserId, "yadda", this.attributeId,
						this.privilege));
		assertTrue(this.gridUserId + " should have " + this.privilege + " on "
				+ makeKey(this.objectId, this.attributeId), this.gridMgr
				.isAuthorized(this.gridUserId, this.objectId, this.attributeId,
						this.privilege));
		assertTrue(this.gridUserId + " should have " + this.privilege + " on "
				+ makeKey("yadda", this.attributeId), !this.gridMgr
				.isAuthorized(this.gridUserId, "yadda", this.attributeId,
						this.privilege));
	}

	public void testCheckPermission1() {

		try {
			assertTrue(this.localUserId + " should have " + this.privilege
					+ " on " + this.objectId, this.gridMgr.checkPermission(
					this.localUserId, this.objectId, this.privilege));
			assertTrue(this.localUserId + " should NOT have " + this.privilege
					+ " on yadda", !this.gridMgr.checkPermission(
					this.localUserId, "yadda", this.privilege));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on " + this.objectId, this.gridMgr.checkPermission(
					this.gridUserId, this.objectId, this.privilege));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on yadda", !this.gridMgr.checkPermission(
					this.gridUserId, "yadda", this.privilege));
		} catch (CSException ex) {
			ex.printStackTrace();
			fail("Should not have gotten CSException");
		}
	}

	public void testCheckPermission2() {

		try {
			assertTrue(this.localUserId + " should have " + this.privilege
					+ " on " + makeKey(this.objectId, this.attributeId),
					this.gridMgr.checkPermission(this.localUserId,
							this.objectId, this.attributeId, this.privilege));
			assertTrue(this.localUserId + " should NOT have " + this.privilege
					+ " on " + makeKey("yadda", this.attributeId),
					!this.gridMgr.checkPermission(this.localUserId, "yadda",
							this.attributeId, this.privilege));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on " + makeKey(this.objectId, this.attributeId),
					this.gridMgr.checkPermission(this.gridUserId,
							this.objectId, this.attributeId, this.privilege));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on " + makeKey("yadda", this.attributeId),
					!this.gridMgr.checkPermission(this.gridUserId, "yadda",
							this.attributeId, this.privilege));
		} catch (CSException ex) {
			ex.printStackTrace();
			fail("Should not have gotten CSException");
		}
	}

	public void testCheckPermission3() {

		try {
			AccessPermission yadda = new MockAccessPermission("yadda",
					this.privilege);
			assertTrue(this.localUserId + " should have " + this.privilege
					+ " on " + this.objectId, this.gridMgr.checkPermission(
					this.accessPermission, this.localUserId));
			assertTrue(this.localUserId + " should NOT have " + this.privilege
					+ " on yadda", !this.gridMgr.checkPermission(yadda,
					this.localUserId));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on " + this.objectId, this.gridMgr.checkPermission(
					this.accessPermission, this.gridUserId));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on yadda", !this.gridMgr.checkPermission(yadda,
					this.gridUserId));
		} catch (CSException ex) {
			ex.printStackTrace();
			fail("Should not have gotten CSException");
		}
	}

	public void testCheckPermission4() {

		try {

			AccessPermission yadda = new MockAccessPermission("yadda",
					this.privilege);
			assertTrue(this.localUserId + " should have " + this.privilege
					+ " on " + this.objectId, this.gridMgr.checkPermission(
					this.accessPermission, this.localSubject));
			assertTrue(this.localUserId + " should NOT have " + this.privilege
					+ " on yadda", !this.gridMgr.checkPermission(yadda,
					this.localUserId));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on " + this.objectId, this.gridMgr.checkPermission(
					this.accessPermission, this.gridSubject));
			assertTrue(this.gridUserId + " should have " + this.privilege
					+ " on yadda", !this.gridMgr.checkPermission(yadda,
					this.gridUserId));
		} catch (CSException ex) {
			ex.printStackTrace();
			fail("Should not have gotten CSException");
		}
	}

	private String makeKey(String... elements) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] != null) {
				sb.append(elements[i]);
				if (i + 1 < elements.length) {
					sb.append(":");
				}
			}
		}
		return sb.toString();
	}

	private class MockAuthorizationManager extends
			MockAuthorizationManagerAdapter {

		private Map<String, Set<String>> userPrivilegeMap = new HashMap<String, Set<String>>();

		private Map<String, List<Group>> accessibleGroups = new HashMap<String, List<Group>>();

		public void grantPrivilegeToUser(String userId, String objectId,
				String privilege) {
			grantPrivilegeToUser(userId, objectId, null, privilege);
		}

		public void grantPrivilegeToUser(String userId, String objectId,
				String attributeId, String privilege) {
			Set<String> objectPrivileges = this.userPrivilegeMap.get(userId);
			if (objectPrivileges == null) {
				objectPrivileges = new HashSet<String>();
				this.userPrivilegeMap.put(userId, objectPrivileges);
			}
			objectPrivileges.add(makeKey(objectId, attributeId, privilege));
		}

		public void addAccessibleGroup(String groupName, String objectId,
				String privilege) {
			addAccessibleGroup(groupName, objectId, null, privilege);
		}

		public void addAccessibleGroup(String groupName, String objectId,
				String attributeId, String privilege) {
			String key = makeKey(objectId, attributeId, privilege);
			List<Group> groups = this.accessibleGroups.get(key);
			if (groups == null) {
				groups = new ArrayList<Group>();
				this.accessibleGroups.put(key, groups);
			}
			Group group = new Group();
			group.setGroupName(groupName);
			groups.add(group);

		}

		public List getAccessibleGroups(String objectId, String privilege) {
			return getAccessibleGroups(objectId, null, privilege);
		}

		public List getAccessibleGroups(String objectId, String attributeId,
				String privilege) {
			String key = makeKey(objectId, attributeId, privilege);
			List<Group> groups = this.accessibleGroups.get(key);
			if (groups == null) {
				groups = new ArrayList<Group>();
			}
			return groups;
		}

		public boolean checkPermission(String userId, String objectId,
				String privilege) {
			return checkPermission(userId, objectId, null, privilege);
		}

		public boolean checkPermission(String userId, String objectId,
				String attributeId, String privilege) {
			String key = makeKey(objectId, attributeId, privilege);
			boolean isAuthorized = false;
			Set<String> objectPrivileges = this.userPrivilegeMap.get(userId);
			if (objectPrivileges != null && objectPrivileges.contains(key)) {
				isAuthorized = true;
			}
			return isAuthorized;
		}

	}

	private class MockGridGrouperClient extends MockGrouperIAdapter {

		private long stallTime = 0;

		private Set<String> groupMappings = new HashSet<String>();

		public void setStallTime(long stallTime) {
			this.stallTime = stallTime;
		}

		public long getStallTime() {
			return this.stallTime;
		}

		public void addUserToGroup(String gridUserId, String gridGrouperGroup) {
			this.groupMappings.add(gridUserId + ":" + gridGrouperGroup);
		}

		public boolean isMemberOf(String identity, String groupName) {
			try {
				Thread.sleep(this.stallTime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return this.groupMappings.contains(identity + ":" + groupName);
		}

	}

	private class MockGridGrouperClientFactory implements
			GridGrouperClientFactory {

		private GrouperI client;

		MockGridGrouperClientFactory(GrouperI client) {
			this.client = client;
		}

		public GrouperI getGridGrouperClient(String url) {
			return this.client;
		}

	}

	private class MockAccessPermission extends AccessPermission {
		private String actions;

		MockAccessPermission(String objectId, String privilege) {
			super(objectId);
			this.actions = privilege;
		}

		public String getActions() {
			return this.actions;
		}
	}

}
