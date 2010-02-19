package edu.internet2.middleware.grouper;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.MemberFilter;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestStatus;
import gov.nih.nci.cagrid.gridgrouper.bean.MembershipRequestUpdate;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.service.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.service.tools.GridGrouperBootstrapper;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemAddFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault;
import gov.nih.nci.cagrid.gridgrouper.testutils.Utils;
import junit.framework.TestCase;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;

public class TestMembershipRequests extends TestCase {

	public static final String SUPER_USER = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=super admin";

	public GridGrouper grouper = null;

	public static final String GROUPER_ALL = "GrouperAll";

	private String USER_A = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user a";

	private String USER_B = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user b";

	private String USER_C = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user c";

	private String USER_D = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user d";

	protected void setUp() throws Exception {
		super.setUp();
		// Need to clear the MembershipRequests table prior to calling
		// RegistryReset.reset as RegistryReset.reset is not aware of the MembershipRequests
		clearMembershipRequestsTable();
		RegistryReset.reset();
		this.grouper = new GridGrouper();
	}

	private void clearMembershipRequestsTable() throws HibernateException {
		Session hs = GridGrouperHibernateHelper.getSession();
		Transaction tx = hs.beginTransaction();

		hs.delete("from MembershipRequests");

		tx.commit();
		hs.close();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		clearMembershipRequestsTable();
		RegistryReset.reset();
	}

	public void testAddMembershipRequests() {
		try {
			GroupDescriptor grp = initialGroupAndRequestSetup();
			grouper.grantGroupPrivilege(SUPER_USER, Utils.getGroupIdentifier(grp), SUPER_USER, GroupPrivilegeType.membershiprequest);

			grouper.addMembershipRequest(USER_A, Utils.getGroupIdentifier(grp), USER_A);
			grouper.addMembershipRequest(USER_B, Utils.getGroupIdentifier(grp), USER_B);
			grouper.addMembershipRequest(USER_C, Utils.getGroupIdentifier(grp), USER_C);
			grouper.addMembershipRequest(USER_D, Utils.getGroupIdentifier(grp), USER_D);

			MembershipRequestDescriptor[] members = grouper.getMembershipRequests(SUPER_USER, Utils.getGroupIdentifier(grp),
					MembershipRequestStatus.Pending);

			assertEquals("Do not retrieve the expected pending membership requests", 4, members.length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}

	}

	private GroupDescriptor initialGroupAndRequestSetup() throws GridGrouperRuntimeFault, StemNotFoundFault,
			InsufficientPrivilegeFault, StemAddFault, Exception {
		GridGrouperBootstrapper.addAdminMember(SUPER_USER);
		grouper.getStem(SUPER_USER, Utils.getRootStemIdentifier());

		String testStem = "TestStem";
		StemDescriptor test = grouper.addChildStem(SUPER_USER, Utils.getRootStemIdentifier(), testStem, testStem);
		final String groupExtension = "mygroup";
		final String groupDisplayExtension = "My Group";

		GroupDescriptor grp = createAndCheckGroup(test, groupExtension, groupDisplayExtension, 1);
		
		final String subGroupExtension = "mysubgroup";
		final String subGroupDisplayExtension = "My Sub Group";

		createAndCheckGroup(test, subGroupExtension, subGroupDisplayExtension, 2);
		return grp;
	}

	public void testApproveMembershipRequest() {
		try {
			GroupDescriptor grp = initialGroupAndRequestSetup();
			grouper.grantGroupPrivilege(SUPER_USER, Utils.getGroupIdentifier(grp), SUPER_USER, GroupPrivilegeType.membershiprequest);

			grouper.addMembershipRequest(USER_A, Utils.getGroupIdentifier(grp), USER_A);
			grouper.addMembershipRequest(USER_B, Utils.getGroupIdentifier(grp), USER_B);
			grouper.addMembershipRequest(USER_C, Utils.getGroupIdentifier(grp), USER_C);
			grouper.addMembershipRequest(USER_D, Utils.getGroupIdentifier(grp), USER_D);

			MembershipRequestUpdate update = new MembershipRequestUpdate();
			update.setStatus(MembershipRequestStatus.Approved);
			update.setNote("I approve of this approval.");

			grouper.updateMembershipRequest(SUPER_USER, Utils.getGroupIdentifier(grp), USER_A, update);

			GrouperSession session = GrouperSession.start(SubjectFinder.findById(SUPER_USER));

			assertTrue(grouper.isMemberOf(session, Utils.getGroupIdentifier(grp), USER_A, MemberFilter.All));
			assertFalse(grouper.isMemberOf(session, Utils.getGroupIdentifier(grp), USER_B, MemberFilter.All));

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}

	}

	public void testRejectMembershipRequest() {
		try {
			GroupDescriptor grp = initialGroupAndRequestSetup();
			grouper.grantGroupPrivilege(SUPER_USER, Utils.getGroupIdentifier(grp), SUPER_USER, GroupPrivilegeType.membershiprequest);

			grouper.addMembershipRequest(USER_A, Utils.getGroupIdentifier(grp), USER_A);
			grouper.addMembershipRequest(USER_B, Utils.getGroupIdentifier(grp), USER_B);
			grouper.addMembershipRequest(USER_C, Utils.getGroupIdentifier(grp), USER_C);
			grouper.addMembershipRequest(USER_D, Utils.getGroupIdentifier(grp), USER_D);

			MembershipRequestUpdate update = new MembershipRequestUpdate();
			update.setStatus(MembershipRequestStatus.Rejected);
			update.setNote("I reject this rejection.");

			grouper.updateMembershipRequest(SUPER_USER, Utils.getGroupIdentifier(grp), USER_A, update);

			GrouperSession session = GrouperSession.start(SubjectFinder.findById(SUPER_USER));

			assertFalse(grouper.isMemberOf(session, Utils.getGroupIdentifier(grp), USER_A, MemberFilter.All));

			MembershipRequestDescriptor[] members = grouper.getMembershipRequests(SUPER_USER, Utils.getGroupIdentifier(grp),
					MembershipRequestStatus.Rejected);
			assertEquals("Did not find the rejected request", 1, members.length);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}

	}

	protected GroupDescriptor createAndCheckGroup(StemDescriptor stem, String extension, String displayExtension,
			int childGroupCount) throws Exception {
		GroupDescriptor grp = grouper.addChildGroup(SUPER_USER, Utils.getStemIdentifier(stem), extension, displayExtension);
		return grp;
	}

}
