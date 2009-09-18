package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gts.bean.Permission;
import gov.nih.nci.cagrid.gts.bean.PermissionFilter;
import gov.nih.nci.cagrid.gts.bean.Role;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.PermissionsTable;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalPermissionFault;
import gov.nih.nci.cagrid.gts.stubs.types.InvalidPermissionFault;
import gov.nih.nci.cagrid.gts.test.Utils;
import junit.framework.TestCase;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestPermissionManager extends TestCase {

	private DBManager db;


	public void testCreateAndDestroy() {
		PermissionManager pm = new PermissionManager(db);
		try {
			pm.clearDatabase();
			pm.buildDatabase();
			assertTrue(db.getDatabase().tableExists(PermissionsTable.TABLE_NAME));
			pm.clearDatabase();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				pm.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddPermission() {
		PermissionManager pm = new PermissionManager(db);
		try {
			pm.clearDatabase();
			Permission p1 = new Permission();
			p1.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
			p1.setRole(Role.TrustServiceAdmin);
			pm.addPermission(p1);
			assertTrue(pm.doesPermissionExist(p1));

			Permission p2 = new Permission();
			p2.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
			p2.setRole(Role.TrustAuthorityManager);
			p2.setTrustedAuthorityName("O=Test Organization,OU=Test Unit,CN=CA");
			pm.addPermission(p2);
			assertTrue(pm.doesPermissionExist(p2));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				pm.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testRevokePermission() {
		PermissionManager pm = new PermissionManager(db);
		try {
			pm.clearDatabase();
			Permission p1 = new Permission();
			p1.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
			p1.setRole(Role.TrustServiceAdmin);
			pm.addPermission(p1);
			assertTrue(pm.doesPermissionExist(p1));

			Permission p2 = new Permission();
			p2.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
			p2.setRole(Role.TrustAuthorityManager);
			p2.setTrustedAuthorityName("O=Test Organization,OU=Test Unit,CN=CA");
			pm.addPermission(p2);
			assertTrue(pm.doesPermissionExist(p2));
			pm.revokePermission(p1);
			assertFalse(pm.doesPermissionExist(p1));
			assertTrue(pm.doesPermissionExist(p2));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				pm.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testRevokeNonExistingPermission() {
		PermissionManager pm = new PermissionManager(db);
		try {
			pm.clearDatabase();
			Permission p = new Permission();
			p.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
			p.setRole(Role.TrustAuthorityManager);
			p.setTrustedAuthorityName("O=Test Organization,OU=Test Unit,CN=CA");

			try {
				pm.revokePermission(p);
				fail("Should not be able to revoke a permission that does not exist.");
			} catch (InvalidPermissionFault f) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				pm.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddInvalidPermissions() {
		PermissionManager pm = new PermissionManager(db);
		try {
			pm.clearDatabase();
			// Test adding the same permission twice

			Permission p1 = new Permission();
			p1.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
			p1.setRole(Role.TrustServiceAdmin);
			pm.addPermission(p1);
			assertTrue(pm.doesPermissionExist(p1));

			try {
				Permission p2 = new Permission();
				p2.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
				p2.setRole(Role.TrustServiceAdmin);
				pm.addPermission(p2);
				fail("Should not be able to add an existing permission.");
			} catch (IllegalPermissionFault f) {

			}

			try {
				Permission p3 = new Permission();
				p3.setRole(Role.TrustServiceAdmin);
				pm.addPermission(p3);
				fail("Should not be able to add a permission without a grid identity.");
			} catch (IllegalPermissionFault f) {

			}

			try {
				Permission p4 = new Permission();
				p4.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
				pm.addPermission(p4);
				fail("Should not be able to add a permission without a role.");
			} catch (IllegalPermissionFault f) {

			}

			try {
				Permission p5 = new Permission();
				p5.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
				p5.setRole(Role.TrustAuthorityManager);
				pm.addPermission(p5);
				fail("Should not be able to add a permission for a TrustAuthorityManager without specifying a trust authority.");
			} catch (IllegalPermissionFault f) {

			}

			try {
				Permission p6 = new Permission();
				p6.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
				p6.setRole(Role.TrustAuthorityManager);
				p6.setTrustedAuthorityName("*");
				pm.addPermission(p6);
				fail("Should not be able to add a permission for a TrustAuthorityManager without specifying a trust authority.");
			} catch (IllegalPermissionFault f) {

			}

			try {
				Permission p7 = new Permission();
				p7.setGridIdentity("O=Test Organization,OU=Test Unit,CN=User");
				p7.setRole(Role.TrustServiceAdmin);
				p7.setTrustedAuthorityName("O=Test Organization,OU=Test Unit,CN=CA");
				pm.addPermission(p7);
				fail("Should not be able to specify a TrustServiceAdmin permission that applies to one TrustAuthority.");
			} catch (IllegalPermissionFault f) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				pm.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


	public void testFindPermissions() {
		PermissionManager pm = new PermissionManager(db);
		try {
			pm.clearDatabase();
			int count = 5;
			String dnPrefix = "O=Organization ABC,OU=Unit XYZ,CN=User";
			String dnPrefix1 = dnPrefix + " X";
			String dnPrefix2 = dnPrefix + " Y";
			String ta = "O=Organization ABC,OU=Unit XYZ,CN=Certificate Authority";
			Permission[] perms1 = new Permission[count];
			Permission[] perms2 = new Permission[count];
			for (int i = 0; i < count; i++) {
				String dn1 = dnPrefix1 + i;
				String dn2 = dnPrefix2 + i;

				perms1[i] = new Permission();
				perms1[i].setGridIdentity(dn1);
				perms1[i].setRole(Role.TrustServiceAdmin);
				pm.addPermission(perms1[i]);
				assertTrue(pm.doesPermissionExist(perms1[i]));
				PermissionFilter fx = new PermissionFilter();
				fx.setGridIdentity(perms1[i].getGridIdentity());
				fx.setRole(perms1[i].getRole());
				fx.setTrustedAuthorityName(perms1[i].getTrustedAuthorityName());
				Permission[] px = pm.findPermissions(fx);
				assertEquals(1, px.length);
				assertEquals(perms1[i], px[0]);

				perms2[i] = new Permission();
				perms2[i].setGridIdentity(dn2);
				perms2[i].setRole(Role.TrustAuthorityManager);
				perms2[i].setTrustedAuthorityName(ta);
				pm.addPermission(perms2[i]);
				assertTrue(pm.doesPermissionExist(perms2[i]));
				assertTrue(pm.isUserTrustedAuthorityAdmin(ta, dn2));

				PermissionFilter fy = new PermissionFilter();
				fy.setGridIdentity(perms2[i].getGridIdentity());
				fy.setRole(perms2[i].getRole());
				fy.setTrustedAuthorityName(perms2[i].getTrustedAuthorityName());
				Permission[] py = pm.findPermissions(fy);
				assertEquals(1, py.length);
				assertEquals(perms2[i], py[0]);

				// Test Filter by Grid Identity
				PermissionFilter f1 = new PermissionFilter();
				f1.setGridIdentity("yada yada");
				assertEquals(0, pm.findPermissions(f1).length);
				f1.setGridIdentity(dnPrefix);
				assertEquals(((i + 1) * 2), pm.findPermissions(f1).length);
				f1.setGridIdentity(dnPrefix1);
				assertEquals(((i + 1)), pm.findPermissions(f1).length);
				f1.setGridIdentity(dnPrefix2);
				assertEquals(((i + 1)), pm.findPermissions(f1).length);
				f1.setGridIdentity(dn1);
				assertEquals(1, pm.findPermissions(f1).length);
				assertEquals(perms1[i], pm.findPermissions(f1)[0]);
				f1.setGridIdentity(dn2);
				assertEquals(1, pm.findPermissions(f1).length);
				assertEquals(perms2[i], pm.findPermissions(f1)[0]);

				// Test Filter by Role
				PermissionFilter f2 = new PermissionFilter();
				f2.setRole(Role.User);
				assertEquals(0, pm.findPermissions(f2).length);
				f2.setRole(Role.TrustServiceAdmin);
				assertEquals(((i + 1)), pm.findPermissions(f2).length);
				f2.setRole(Role.TrustAuthorityManager);
				assertEquals(((i + 1)), pm.findPermissions(f2).length);

				// Test Filter by Trusted Authority
				PermissionFilter f3 = new PermissionFilter();
				assertEquals(((i + 1) * 2), pm.findPermissions(f3).length);
				f3.setTrustedAuthorityName("yada yada");
				assertEquals(0, pm.findPermissions(f3).length);
				f3.setTrustedAuthorityName(ta);
				assertEquals((i + 1), pm.findPermissions(f3).length);
				f3.setTrustedAuthorityName("*");
				assertEquals((i + 1), pm.findPermissions(f3).length);
			}
			// Test Remove
			for (int i = 0; i < count; i++) {
				pm.revokePermission(perms1[i]);
				assertFalse(pm.doesPermissionExist(perms1[i]));
				pm.revokePermission(perms2[i]);
				assertFalse(pm.doesPermissionExist(perms2[i]));
			}
			assertEquals(0, pm.findPermissions(new PermissionFilter()).length);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				pm.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}


	protected void setUp() throws Exception {
		super.setUp();
		try {
			db = Utils.getDBManager();
			assertEquals(0, db.getDatabase().getUsedConnectionCount());
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}


	protected void tearDown() throws Exception {
		super.tearDown();
		try {
			assertEquals(0, db.getDatabase().getUsedConnectionCount());
			// db.getDatabase().destroyDatabase();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}

}
