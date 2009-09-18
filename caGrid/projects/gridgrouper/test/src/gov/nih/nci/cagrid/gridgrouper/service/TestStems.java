package gov.nih.nci.cagrid.gridgrouper.service;

import edu.internet2.middleware.grouper.RegistryReset;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gridgrouper.bean.GroupDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemDescriptor;
import gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilege;
import gov.nih.nci.cagrid.gridgrouper.bean.StemPrivilegeType;
import gov.nih.nci.cagrid.gridgrouper.bean.StemUpdate;
import gov.nih.nci.cagrid.gridgrouper.service.tools.GridGrouperBootstrapper;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.InsufficientPrivilegeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemDeleteFault;
import gov.nih.nci.cagrid.gridgrouper.subject.AnonymousGridUserSubject;
import gov.nih.nci.cagrid.gridgrouper.testutils.Utils;
import junit.framework.TestCase;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestStems extends TestCase {

	private GridGrouper grouper = null;

	private String SUPER_USER = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=super admin";

	private String ADMIN_USER = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=admin";

	private String USER_A = "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid05/OU=IdP [1]/CN=user a";


	public void testRootStem() {
		try {
			StemDescriptor root = grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getRootStemIdentifier());
			assertNotNull(root);
			assertEquals(root.getName(), Utils.getRootStemIdentifier().getStemName());
			String displayExtension = root.getDisplayExtension();
			String description = root.getDescription();
			assertNotNull(displayExtension);
			assertNotNull(description);
			String updatedDisplayExtension = displayExtension + " Update";
			String updatedDescription = displayExtension + " Description Update";

			try {
				StemUpdate update = new StemUpdate();
				update.setDisplayExtension(updatedDisplayExtension);
				grouper.updateStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getStemIdentifier(root),
					update);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault f) {

			}

			try {
				StemUpdate update = new StemUpdate();
				update.setDescription(updatedDescription);
				grouper.updateStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getStemIdentifier(root),
					update);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault f) {

			}

			checkStem(grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getRootStemIdentifier()),
				displayExtension, description);
			// TODO: BUG IN GROUPER CACHING?
			// assertFalse(grouper
			// .hasStemPrivilege(
			// AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID,
			// getStemIdentifier(root), ADMIN_USER,
			// StemPrivilegeType.stem));
			// assertFalse(grouper.hasStemPrivilege(
			// AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID,
			// getStemIdentifier(root), ADMIN_USER,
			// StemPrivilegeType.create));

			// Now create an admin user and do the update
			GridGrouperBootstrapper.addAdminMember(ADMIN_USER);
			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), ADMIN_USER, StemPrivilegeType.stem));
			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), ADMIN_USER, StemPrivilegeType.create));

			StemUpdate update1 = new StemUpdate();
			update1.setDisplayExtension(updatedDisplayExtension);
			update1.setDescription(updatedDescription);
			grouper.updateStem(ADMIN_USER, Utils.getStemIdentifier(root), update1);

			checkStem(grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getRootStemIdentifier()),
				updatedDisplayExtension, updatedDescription);

			StemUpdate update2 = new StemUpdate();
			update2.setDisplayExtension(displayExtension);
			update2.setDescription(description);
			grouper.updateStem(ADMIN_USER, Utils.getStemIdentifier(root), update2);

			checkStem(grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getRootStemIdentifier()),
				displayExtension, description);

			// Now try with another user

			assertFalse(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.stem));
			assertFalse(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.create));

			try {
				grouper.grantStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
					.getStemIdentifier(root), USER_A, StemPrivilegeType.stem);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}

			assertFalse(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.stem));
			assertFalse(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.create));

			grouper.grantStemPrivilege(ADMIN_USER, Utils.getStemIdentifier(root), USER_A, StemPrivilegeType.stem);

			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.stem));
			assertFalse(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.create));

			StemUpdate update3 = new StemUpdate();
			update3.setDisplayExtension(updatedDisplayExtension);
			update3.setDescription(updatedDescription);

			grouper.updateStem(USER_A, Utils.getStemIdentifier(root), update3);

			checkStem(grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getRootStemIdentifier()),
				updatedDisplayExtension, updatedDescription);

			StemUpdate update4 = new StemUpdate();
			update4.setDisplayExtension(displayExtension);
			update4.setDescription(description);

			grouper.updateStem(USER_A, Utils.getStemIdentifier(root), update4);

			checkStem(grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getRootStemIdentifier()),
				displayExtension, description);

			StemPrivilege[] privs = grouper.getStemPrivileges(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A);
			assertNotNull(privs);
			assertEquals(1, privs.length);
			assertEquals(USER_A, privs[0].getSubject());
			assertEquals(StemPrivilegeType.stem, privs[0].getPrivilegeType());
			assertEquals(root.getName(), privs[0].getStemName());

			StemPrivilege[] privs2 = grouper.getStemPrivileges(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), ADMIN_USER);
			assertNotNull(privs2);
			assertEquals(0, privs2.length);

			// TODO: Should I be able to call with anon user?s
			String[] subs1 = grouper.getSubjectsWithStemPrivilege(ADMIN_USER, Utils.getStemIdentifier(root),
				StemPrivilegeType.stem);
			assertNotNull(subs1);
			assertEquals(1, subs1.length);
			assertEquals(USER_A, subs1[0]);

			// TODO: Should I be able to call with anon user?
			String[] subs2 = grouper.getSubjectsWithStemPrivilege(ADMIN_USER, Utils.getStemIdentifier(root),
				StemPrivilegeType.create);
			assertNotNull(subs2);
			assertEquals(0, subs2.length);

			try {

				grouper.revokeStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
					.getStemIdentifier(root), USER_A, StemPrivilegeType.stem);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}

			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.stem));

			grouper.revokeStemPrivilege(USER_A, Utils.getStemIdentifier(root), USER_A, StemPrivilegeType.stem);

			assertFalse(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.stem));

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}

	}


	public void testAddingGroups() {
		try {
			GridGrouperBootstrapper.addAdminMember(SUPER_USER);

			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getRootStemIdentifier(), SUPER_USER, StemPrivilegeType.stem));
			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getRootStemIdentifier(), SUPER_USER, StemPrivilegeType.create));

			StemDescriptor root = grouper.getStem(SUPER_USER, Utils.getRootStemIdentifier());
			assertNotNull(root);
			assertEquals(root.getName(), Utils.getRootStemIdentifier().getStemName());

			String testStem = "TestStem";
			StemDescriptor test = grouper.addChildStem(SUPER_USER, Utils.getRootStemIdentifier(), testStem, testStem);

			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(test), SUPER_USER, StemPrivilegeType.stem));
			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(test), SUPER_USER, StemPrivilegeType.create));

			final String group1Extension = "group1";
			final String group1DisplayExtension = "group 1";
			final String group2Extension = "group2";
			final String group2DisplayExtension = "group 2";

			// TODO: BUG IN GROUPER CACHING?
			// assertFalse(grouper
			// .hasStemPrivilege(
			// SUPER_USER,
			// getStemIdentifier(test), ADMIN_USER,
			// StemPrivilegeType.stem));
			// assertFalse(grouper.hasStemPrivilege(
			// SUPER_USER,
			// getStemIdentifier(test), ADMIN_USER,
			// StemPrivilegeType.create));
			//			
			// try {
			// grouper.addChildGroup(ADMIN_USER, getStemIdentifier(root),
			// group1Extension, group1DisplayExtension);
			// fail("Should have failed, insufficient privilege!!!");
			// } catch (InsufficientPrivilegeFault f) {
			//
			// }

			// Now create an admin user and do the update
			GridGrouperBootstrapper.addAdminMember(ADMIN_USER);

			assertTrue(grouper.hasStemPrivilege(SUPER_USER, Utils.getStemIdentifier(test), ADMIN_USER,
				StemPrivilegeType.stem));
			assertTrue(grouper.hasStemPrivilege(SUPER_USER, Utils.getStemIdentifier(test), ADMIN_USER,
				StemPrivilegeType.create));

			assertEquals(0, grouper.getChildGroups(ADMIN_USER, Utils.getStemIdentifier(test)).length);
			grouper.addChildGroup(ADMIN_USER, Utils.getStemIdentifier(test), group1Extension, group1DisplayExtension);
			GroupDescriptor[] grps = grouper.getChildGroups(ADMIN_USER, Utils.getStemIdentifier(test));
			assertEquals(1, grps.length);
			assertEquals(group1Extension, grps[0].getExtension());
			assertEquals(group1DisplayExtension, grps[0].getDisplayExtension());

			// Now try with another user

			assertFalse(grouper.hasStemPrivilege(SUPER_USER, Utils.getStemIdentifier(test), USER_A,
				StemPrivilegeType.stem));
			assertFalse(grouper.hasStemPrivilege(SUPER_USER, Utils.getStemIdentifier(test), USER_A,
				StemPrivilegeType.create));

			try {
				grouper.addChildGroup(USER_A, Utils.getStemIdentifier(test), group2Extension, group2DisplayExtension);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}

			grouper.grantStemPrivilege(SUPER_USER, Utils.getStemIdentifier(test), USER_A, StemPrivilegeType.create);

			assertFalse(grouper.hasStemPrivilege(SUPER_USER, Utils.getStemIdentifier(test), USER_A,
				StemPrivilegeType.stem));
			assertTrue(grouper.hasStemPrivilege(SUPER_USER, Utils.getStemIdentifier(test), USER_A,
				StemPrivilegeType.create));
			grouper.addChildGroup(USER_A, Utils.getStemIdentifier(test), group2Extension, group2DisplayExtension);
			grps = grouper.getChildGroups(USER_A, Utils.getStemIdentifier(test));
			assertEquals(2, grps.length);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}

	}


	public void testChildStems() {
		try {
			StemDescriptor root = grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getRootStemIdentifier());
			assertNotNull(root);
			GridGrouperBootstrapper.addAdminMember(ADMIN_USER);
			String extensionChildX = "X";
			String extensionChildX1 = "X.1";
			try {
				grouper.addChildStem(USER_A, Utils.getStemIdentifier(root), extensionChildX, extensionChildX);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}

			grouper.grantStemPrivilege(ADMIN_USER, Utils.getStemIdentifier(root), USER_A, StemPrivilegeType.stem);

			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root), USER_A, StemPrivilegeType.stem));

			StemDescriptor childX = grouper.addChildStem(USER_A, Utils.getStemIdentifier(root), extensionChildX,
				extensionChildX);
			assertEquals(extensionChildX, childX.getExtension());
			assertEquals(extensionChildX, childX.getDisplayExtension());

			StemDescriptor[] c1 = grouper.getChildStems(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root));
			assertNotNull(c1);
			assertEquals(2, c1.length);

			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(childX), USER_A, StemPrivilegeType.stem));
			try {
				grouper.grantStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
					.getStemIdentifier(childX), USER_A, StemPrivilegeType.create);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}
			grouper.grantStemPrivilege(USER_A, Utils.getStemIdentifier(childX), USER_A, StemPrivilegeType.create);
			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(childX), USER_A, StemPrivilegeType.create));
			assertEquals(grouper
				.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getRootStemIdentifier()), grouper
				.getParentStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getStemIdentifier(childX)));

			StemDescriptor childX1 = grouper.addChildStem(USER_A, Utils.getStemIdentifier(childX), extensionChildX1,
				extensionChildX1);
			assertEquals(extensionChildX1, childX1.getExtension());
			assertEquals(extensionChildX1, childX1.getDisplayExtension());

			StemDescriptor[] c2 = grouper.getChildStems(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(childX));
			assertNotNull(c2);
			assertEquals(1, c2.length);

			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(childX1), USER_A, StemPrivilegeType.stem));
			try {
				grouper.grantStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
					.getStemIdentifier(childX1), USER_A, StemPrivilegeType.create);
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}
			grouper.grantStemPrivilege(USER_A, Utils.getStemIdentifier(childX1), USER_A, StemPrivilegeType.create);
			assertTrue(grouper.hasStemPrivilege(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(childX1), USER_A, StemPrivilegeType.create));
			assertEquals(grouper.getStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(childX)), grouper.getParentStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID,
				Utils.getStemIdentifier(childX1)));
			try {
				grouper.deleteStem(USER_A, Utils.getStemIdentifier(childX));
				fail("Should not to be able to delete stem, it has child stems!!!");
			} catch (StemDeleteFault e) {

			}

			try {
				grouper.deleteStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getStemIdentifier(childX1));
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}

			grouper.deleteStem(USER_A, Utils.getStemIdentifier(childX1));

			StemDescriptor[] c3 = grouper.getChildStems(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(childX));
			assertNotNull(c3);
			assertEquals(0, c3.length);

			try {
				grouper.deleteStem(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils.getStemIdentifier(childX));
				fail("Should have failed, insufficient privilege!!!");
			} catch (InsufficientPrivilegeFault e) {

			}

			grouper.deleteStem(USER_A, Utils.getStemIdentifier(childX));

			StemDescriptor[] c4 = grouper.getChildStems(AnonymousGridUserSubject.ANONYMOUS_GRID_USER_ID, Utils
				.getStemIdentifier(root));
			assertNotNull(c4);
			assertEquals(1, c4.length);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}

	}


	private void checkStem(StemDescriptor des, String displayExtension, String description) {
		assertEquals(displayExtension, des.getDisplayExtension());
		assertEquals(description, des.getDescription());
	}


	protected void setUp() throws Exception {
		super.setUp();
		RegistryReset.reset();
		this.grouper = new GridGrouper();
	}


	protected void tearDown() throws Exception {
		super.tearDown();
		RegistryReset.reset();
	}

}
