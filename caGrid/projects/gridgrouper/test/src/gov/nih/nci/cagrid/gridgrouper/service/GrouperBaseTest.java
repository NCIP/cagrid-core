package gov.nih.nci.cagrid.gridgrouper.service;

import edu.internet2.middleware.grouper.RegistryReset;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilege;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberType;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.subject.AnonymousGridUserSubject;
import gov.nih.nci.cagrid.gridgrouper.testutils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import junit.framework.TestCase;


public abstract class GrouperBaseTest extends TestCase {

	public static final String SUPER_USER = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=super admin";

	public GridGrouper grouper = null;
	
	public static final String GROUPER_ALL = "GrouperAll";


	protected void setUp() throws Exception {
		super.setUp();
		RegistryReset.reset();
		this.grouper = new GridGrouper();
	}


	protected void tearDown() throws Exception {
		super.tearDown();
		RegistryReset.reset();
	}


	protected GroupDescriptor createAndCheckGroup(StemDescriptor stem, String extension, String displayExtension,
		int childGroupCount) throws Exception {
		GroupDescriptor grp = grouper.addChildGroup(SUPER_USER, Utils.getStemIdentifier(stem), extension,
			displayExtension);
		assertEquals(extension, grp.getExtension());
		assertEquals(displayExtension, grp.getDisplayExtension());
		assertEquals(childGroupCount, grouper.getChildGroups(SUPER_USER, Utils.getStemIdentifier(stem)).length);
		assertFalse(grp.isHasComposite());
		Map expected = new HashMap();
		expected.clear();
		verifyMembers(grp, MemberFilter.All, expected);
		expected.clear();
		verifyMembers(grp, MemberFilter.EffectiveMembers, expected);
		expected.clear();
		verifyMembers(grp, MemberFilter.ImmediateMembers, expected);
		expected.clear();
		verifyMembers(grp, MemberFilter.CompositeMembers, expected);

		expected.clear();
		verifyMemberships(grp, MemberFilter.All, 0, expected);
		expected.clear();
		verifyMemberships(grp, MemberFilter.EffectiveMembers, 0, expected);
		expected.clear();
		verifyMemberships(grp, MemberFilter.ImmediateMembers, 0, expected);
		expected.clear();
		verifyMemberships(grp, MemberFilter.CompositeMembers, 0, expected);
		return grp;
	}


	protected void verifyMembers(GroupDescriptor grp, MemberFilter filter, Map expected) {
		verifyMembers(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, grp, filter, expected);
	}


	protected void verifyMembers(String caller, GroupDescriptor grp, MemberFilter filter, Map expected) {
		try {
			int expectedCount = expected.size();
			assertEquals(expectedCount, expected.size());
			MemberDescriptor[] members = grouper.getMembers(caller, Utils.getGroupIdentifier(grp), filter);
			assertEquals(expectedCount, members.length);

			for (int i = 0; i < expectedCount; i++) {
				if (expected.containsKey(members[i].getSubjectId())) {
					MemberCaddy caddy = (MemberCaddy) expected.remove(members[i].getSubjectId());
					assertEquals(caddy.getMemberId(), members[i].getSubjectId());
					assertEquals(caddy.getMemberType(), members[i].getMemberType());
					if (!filter.equals(MemberFilter.CompositeMembers)) {
						assertTrue(grouper.isMemberOf(caller, Utils
							.getGroupIdentifier(grp), caddy.getMemberId(), filter));
					}
				} else {
					fail("Member " + members[i].getSubjectId() + " not expected!!!");
				}
			}
			assertEquals(0, expected.size());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail("Error verifying members");
		}

	}
	
	protected void verifyMembersGroups(String caller, String member, MembershipType type, Map expected) {
		try {
			int expectedCount = expected.size();
			assertEquals(expectedCount, expected.size());
			GroupDescriptor[] groups = grouper.getMembersGroups(caller, member, type);
			assertEquals(expectedCount, groups.length);

			for (int i = 0; i < expectedCount; i++) {
				if (expected.containsKey(groups[i].getName())) {
					GroupDescriptor grp = (GroupDescriptor) expected.remove(groups[i].getName());
					assertEquals(grp.getUUID(), groups[i].getUUID());
				} else {
					fail("Group " + groups[i].getName() + " not expected!!!");
				}
			}
			assertEquals(0, expected.size());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail("Error verifying member's groups");
		}

	}


	protected void verifyMemberships(GroupDescriptor grp, MemberFilter filter, int expectedCount, Map expected) {
		try {
			assertEquals(expectedCount, expected.size());
			MembershipDescriptor[] members = grouper.getMemberships(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID,
				Utils.getGroupIdentifier(grp), filter);
			assertEquals(expectedCount, members.length);

			for (int i = 0; i < expectedCount; i++) {
				if (expected.containsKey(members[i].getMember().getSubjectId())) {
					MembershipCaddy caddy = (MembershipCaddy) expected.remove(members[i].getMember().getSubjectId());
					assertEquals(caddy.getMemberId(), members[i].getMember().getSubjectId());
					assertEquals(caddy.getMemberType(), members[i].getMember().getMemberType());
					assertEquals(caddy.getDepth(), members[i].getDepth());

					assertEquals(caddy.getGroupName(), members[i].getGroup().getName());
					String viaGN = null;
					if (members[i].getViaGroup() != null) {
						viaGN = members[i].getViaGroup().getName();
					}
					assertEquals(caddy.getViaGroupName(), viaGN);
					if (!filter.equals(MemberFilter.CompositeMembers)) {
						assertTrue(grouper.isMemberOf(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
							.getGroupIdentifier(grp), caddy.getMemberId(), filter));
					}
				} else {
					fail("Membership " + members[i].getMember().getSubjectId() + " not expected!!!");
				}
			}
			assertEquals(0, expected.size());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail("Error verifying members");
		}

	}


	protected void verifyUserPrivileges(GroupDescriptor grp, String user, HashSet expected) {
		verifyUserPrivileges(SUPER_USER, grp, user, expected);
	}


	protected void verifyUserPrivileges(String caller, GroupDescriptor grp, String user, HashSet expected) {
		try {
			GroupPrivilege[] privs = grouper.getGroupPrivileges(caller, Utils.getGroupIdentifier(grp), user);
			assertEquals(expected.size(), privs.length);
			for (int i = 0; i < privs.length; i++) {
				if (expected.contains(privs[i].getPrivilegeType())) {
					assertEquals(user, privs[i].getSubject());
					expected.remove(privs[i].getPrivilegeType());
				} else {
					fail("The privilege " + privs[i].getPrivilegeType().getValue() + " was not expected!!!");
				}
			}
			assertEquals(0, expected.size());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail("Error verifying members");
		}

	}


	protected void verifyPrivileges(GroupDescriptor grp, GroupPrivilegeType priv, HashSet expected) {
		verifyPrivileges(SUPER_USER, grp, priv, expected);
	}


	protected void verifyPrivileges(String caller, GroupDescriptor grp, GroupPrivilegeType priv, HashSet expected) {
		try {
			String[] users = grouper.getSubjectsWithGroupPrivilege(caller, Utils.getGroupIdentifier(grp), priv);
			assertEquals(expected.size(), users.length);
			for (int i = 0; i < users.length; i++) {
				if (expected.contains(users[i])) {
					expected.remove(users[i]);
				} else {
					fail("The privilege " + priv.getValue() + " was not expected for the user " + users[i] + "!!!");
				}
			}
			assertEquals(0, expected.size());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail("Error verifying members");
		}

	}


	protected MemberCaddy getGridMember(String name) {
		return new MemberCaddy(name, MemberType.Grid);
	}


	protected class MemberCaddy {
		private String memberId;

		private MemberType memberType;


		public MemberCaddy(String id, MemberType type) {
			this.memberId = id;
			this.memberType = type;
		}


		public String getMemberId() {
			return memberId;
		}


		public MemberType getMemberType() {
			return memberType;
		}

	}


	protected class MembershipCaddy {
		private String memberId;

		private String groupName;

		private String viaGroupName;

		private int depth;

		private MemberType memberType;


		public MembershipCaddy(String id, String groupName, String viaGroupName, int depth, MemberType type) {
			this.memberId = id;
			this.memberType = type;
			this.groupName = groupName;
			this.viaGroupName = viaGroupName;
			this.depth = depth;
		}


		public String getMemberId() {
			return memberId;
		}


		public MemberType getMemberType() {
			return memberType;
		}


		public int getDepth() {
			return depth;
		}


		public String getGroupName() {
			return groupName;
		}


		public String getViaGroupName() {
			return viaGroupName;
		}

	}
	
	protected MemberCaddy getGroupMember(String name) {
		return new MemberCaddy(name, MemberType.GrouperGroup);
	}

}
