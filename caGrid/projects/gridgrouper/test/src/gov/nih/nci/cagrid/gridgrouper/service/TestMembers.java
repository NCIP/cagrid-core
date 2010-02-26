package gov.nih.nci.cagrid.gridgrouper.service;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupCompositeType;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.service.tools.GridGrouperBootstrapper;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.testutils.Utils;

import java.util.HashMap;
import java.util.Map;

import org.oasis.wsrf.faults.BaseFaultType;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestMembers extends GrouperBaseTest {

	private String USER_A = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user a";

	private String USER_B = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user b";

	private String USER_C = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user c";

	private String USER_D = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user d";


	public void testGetMember() {
		try {

			try {
				grouper.getMember(SUPER_USER, USER_A);
				fail("Should not be able to get member!!!");
			} catch (InsufficientPrivilegeFault f) {

			}

			GridGrouperBootstrapper.addAdminMember(SUPER_USER);
			MemberDescriptor des = grouper.getMember(SUPER_USER, USER_A);
			assertNotNull(des);
			assertEquals(USER_A, des.getSubjectId());
			assertEquals(USER_A, des.getSubjectName());

			try {
				grouper.getMember(USER_A, USER_B);
				fail("Should not be able to get member!!!");
			} catch (InsufficientPrivilegeFault f) {

			}

			des = null;
			des = grouper.getMember(USER_B, USER_B);
			assertNotNull(des);
			assertEquals(USER_B, des.getSubjectId());
			assertEquals(USER_B, des.getSubjectName());

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}

	}


	public void testGetMemberGroups() {
		try {
			GroupDescriptor[] groups = grouper.getMembersGroups(SUPER_USER, USER_A, null);
			assertEquals("The super user should not see any of the member's groups yet", 0, groups.length);

			GridGrouperBootstrapper.addAdminMember(SUPER_USER);

			Map expected = new HashMap();
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_A, null, expected);

			StemDescriptor root = grouper.getStem(SUPER_USER, Utils.getRootStemIdentifier());
			assertNotNull(root);
			assertEquals(root.getName(), Utils.getRootStemIdentifier().getStemName());

			String testStem = "TestStem";
			StemDescriptor test = grouper.addChildStem(SUPER_USER, Utils.getRootStemIdentifier(), testStem, testStem);

			final String groupExtensionA = "mygroupa";
			final String groupDisplayExtensionA = "My Group A";

			GroupDescriptor grpa = createAndCheckGroup(test, groupExtensionA, groupDisplayExtensionA, 1);

			final String groupExtensionB = "mygroupb";
			final String groupDisplayExtensionB = "My Group B";

			GroupDescriptor grpb = createAndCheckGroup(test, groupExtensionB, groupDisplayExtensionB, 2);
			grouper.revokeGroupPrivilege(SUPER_USER, Utils.getGroupIdentifier(grpb), GROUPER_ALL,
				GroupPrivilegeType.read);
			grouper.revokeGroupPrivilege(SUPER_USER, Utils.getGroupIdentifier(grpb), GROUPER_ALL,
				GroupPrivilegeType.view);

			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpa), USER_A);
			expected.clear();
			expected.put(USER_A, getGridMember(USER_A));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpa), USER_C);
			expected.put(USER_C, getGridMember(USER_C));
			verifyMembers(grpa, MemberFilter.All, expected);

			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), USER_B);

			expected.clear();
			expected.put(USER_B, getGridMember(USER_B));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), USER_C);
			expected.put(USER_C, getGridMember(USER_C));
			verifyMembers(SUPER_USER, grpb, MemberFilter.All, expected);

			groups = grouper.getMembersGroups(USER_D, USER_B, null);
			assertEquals("USER_D should not see USER_B's group.  GrouperAll does not have READ or VIEW for GroupB", 0, groups.length);
				
			expected.clear();
			expected.put(grpa.getName(), grpa);
			verifyMembersGroups(USER_A, USER_A, null, expected);

			expected.clear();
			expected.put(grpa.getName(), grpa);
			verifyMembersGroups(SUPER_USER, USER_A, null, expected);

			groups = grouper.getMembersGroups(USER_D, USER_B, null);
			assertEquals("UUSER_D should not see USER_B's group.  GrouperAll does not have READ or VIEW for GroupB", 0, groups.length);

			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(USER_B, USER_B, null, expected);

			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_B, null, expected);

			groups = grouper.getMembersGroups(USER_D, USER_C, null);
			assertEquals("USER_D should only see 1 of USER_C's groups", 1, groups.length);
			assertEquals(grpa.getName(), groups[0].getName());
				
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(USER_C, USER_C, null, expected);

			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_C, null, expected);

			groups = grouper.getMembersGroups(USER_C, USER_D, null);
			assertEquals("USER_C should not see any groups for USER_D. USER_D has not been added to any groups.", 0, groups.length);
				
			expected.clear();
			verifyMembersGroups(USER_D, USER_D, null, expected);

			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_D, null, expected);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}
	}


	public void testGetMembersGroupsByAssociation() {
		try {
			GridGrouperBootstrapper.addAdminMember(SUPER_USER);

			Map expected = new HashMap();
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_A, null, expected);

			StemDescriptor root = grouper.getStem(SUPER_USER, Utils.getRootStemIdentifier());
			assertNotNull(root);
			assertEquals(root.getName(), Utils.getRootStemIdentifier().getStemName());

			String testStem = "TestStem";
			StemDescriptor test = grouper.addChildStem(SUPER_USER, Utils.getRootStemIdentifier(), testStem, testStem);

			final String groupExtensionA = "mygroupa";
			final String groupDisplayExtensionA = "My Group A";

			GroupDescriptor grpa = createAndCheckGroup(test, groupExtensionA, groupDisplayExtensionA, 1);

			final String groupExtensionB = "mygroupb";
			final String groupDisplayExtensionB = "My Group B";

			GroupDescriptor grpb = createAndCheckGroup(test, groupExtensionB, groupDisplayExtensionB, 2);
			grouper.revokeGroupPrivilege(SUPER_USER, Utils.getGroupIdentifier(grpb), GROUPER_ALL,
				GroupPrivilegeType.read);
			grouper.revokeGroupPrivilege(SUPER_USER, Utils.getGroupIdentifier(grpb), GROUPER_ALL,
				GroupPrivilegeType.view);

			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpa), USER_A);
			expected.clear();
			expected.put(USER_A, getGridMember(USER_A));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpa), USER_C);
			expected.put(USER_C, getGridMember(USER_C));
			verifyMembers(grpa, MemberFilter.All, expected);

			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), USER_B);

			expected.clear();
			expected.put(USER_B, getGridMember(USER_B));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), USER_C);
			expected.put(USER_C, getGridMember(USER_C));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), grpa.getUUID());
			expected.put(grpa.getUUID(), getGroupMember(grpa.getUUID()));
			expected.put(USER_A, getGridMember(USER_A));
			verifyMembers(SUPER_USER, grpb, MemberFilter.All, expected);

			// Test Groups for USER_A

			// All groups that USER_A is a member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_A, MembershipType.Any, expected);

			// Groups that USER_A is an immediate member of
			expected.clear();
			expected.put(grpa.getName(), grpa);;
			verifyMembersGroups(SUPER_USER, USER_A, MembershipType.ImmediateMembers, expected);

			// Groups that USER_A is an effective member of
			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_A, MembershipType.EffectiveMembers, expected);

			// Test Groups for USER_B

			// All groups that USER_B is a member of
			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_B, MembershipType.Any, expected);

			// Groups that USER_B is an immediate member of
			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_B, MembershipType.ImmediateMembers, expected);

			// Groups that USER_B is an effective member of
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_B, MembershipType.EffectiveMembers, expected);

			// Test Groups for USER_C

			// All groups that USER_C is a member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_C, MembershipType.Any, expected);

			// Groups that USER_C is an immediate member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_C, MembershipType.ImmediateMembers, expected);

			// Groups that USER_C is an effective member of
			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_C, MembershipType.EffectiveMembers, expected);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}
	}


	public void testGetMembersGroupsByComposite() {
		try {
			GridGrouperBootstrapper.addAdminMember(SUPER_USER);

			Map expected = new HashMap();
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_A, null, expected);

			StemDescriptor root = grouper.getStem(SUPER_USER, Utils.getRootStemIdentifier());
			assertNotNull(root);
			assertEquals(root.getName(), Utils.getRootStemIdentifier().getStemName());

			String testStem = "TestStem";
			StemDescriptor test = grouper.addChildStem(SUPER_USER, Utils.getRootStemIdentifier(), testStem, testStem);

			final String groupExtensionA = "mygroupa";
			final String groupDisplayExtensionA = "My Group A";

			GroupDescriptor grpa = createAndCheckGroup(test, groupExtensionA, groupDisplayExtensionA, 1);

			final String groupExtensionB = "mygroupb";
			final String groupDisplayExtensionB = "My Group B";

			GroupDescriptor grpb = createAndCheckGroup(test, groupExtensionB, groupDisplayExtensionB, 2);

			final String compositeExtension = "compositegroup";
			final String compositeDisplayExtension = "Composite Group";

			GroupDescriptor compgrp = createAndCheckGroup(test, compositeExtension, compositeDisplayExtension, 3);

			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpa), USER_A);
			expected.clear();
			expected.put(USER_A, getGridMember(USER_A));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpa), USER_C);
			expected.put(USER_C, getGridMember(USER_C));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpa), USER_D);
			expected.put(USER_D, getGridMember(USER_D));
			verifyMembers(grpa, MemberFilter.All, expected);

			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), USER_B);
			expected.clear();
			expected.put(USER_B, getGridMember(USER_B));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), USER_C);
			expected.put(USER_C, getGridMember(USER_C));
			grouper.addMember(SUPER_USER, Utils.getGroupIdentifier(grpb), USER_D);
			expected.put(USER_D, getGridMember(USER_D));
			verifyMembers(SUPER_USER, grpb, MemberFilter.All, expected);

			grouper.addCompositeMember(SUPER_USER, GroupCompositeType.Intersection, Utils.getGroupIdentifier(compgrp), Utils
				.getGroupIdentifier(grpa), Utils.getGroupIdentifier(grpb));

			expected.clear();
			expected.put(USER_C, getGridMember(USER_C));
			expected.put(USER_D, getGridMember(USER_D));
			verifyMembers(SUPER_USER, compgrp, MemberFilter.All, expected);

			// Test Groups for USER_A

			// All groups that USER_A is a member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			verifyMembersGroups(SUPER_USER, USER_A, MembershipType.Any, expected);

			// Groups that USER_A is an immediate member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			verifyMembersGroups(SUPER_USER, USER_A, MembershipType.ImmediateMembers, expected);

			// Groups that USER_A is an effective member of
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_A, MembershipType.EffectiveMembers, expected);

			// Test Groups for USER_B

			// All groups that USER_B is a member of
			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_B, MembershipType.Any, expected);

			// Groups that USER_B is an immediate member of
			expected.clear();
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_B, MembershipType.ImmediateMembers, expected);

			// Groups that USER_B is an effective member of
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_B, MembershipType.EffectiveMembers, expected);

			// Test Groups for USER_C

			// All groups that USER_C is a member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			expected.put(compgrp.getName(), compgrp);
			verifyMembersGroups(SUPER_USER, USER_C, MembershipType.Any, expected);

			// Groups that USER_C is an immediate member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_C, MembershipType.ImmediateMembers, expected);

			// Groups that USER_C is an effective member of
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_C, MembershipType.EffectiveMembers, expected);
			
			
			// Test Groups for USER_D

			// All groups that USER_D is a member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			expected.put(compgrp.getName(), compgrp);
			verifyMembersGroups(SUPER_USER, USER_D, MembershipType.Any, expected);

			// Groups that USER_C is an immediate member of
			expected.clear();
			expected.put(grpa.getName(), grpa);
			expected.put(grpb.getName(), grpb);
			verifyMembersGroups(SUPER_USER, USER_D, MembershipType.ImmediateMembers, expected);

			// Groups that USER_C is an effective member of
			expected.clear();
			verifyMembersGroups(SUPER_USER, USER_D, MembershipType.EffectiveMembers, expected);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}
	}
	
	public void testGetMembers() {
		try {
			GridGrouperBootstrapper.addAdminMember(SUPER_USER);
			grouper.getStem(SUPER_USER, Utils.getRootStemIdentifier());

			String testStem = "TestStem";
			StemDescriptor test = grouper.addChildStem(SUPER_USER, Utils.getRootStemIdentifier(), testStem, testStem);
			final String groupExtension = "mygroup";
			final String groupDisplayExtension = "My Group";

			GroupDescriptor grp = createAndCheckGroup(test, groupExtension, groupDisplayExtension, 1);

			try {
				grouper.getMembers(SUPER_USER, Utils.getGroupIdentifier(grp), null);
				fail("Should not be able to get member!!!");
			} catch (BaseFaultType f) {
				// Expected Fault
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}

	}

}
