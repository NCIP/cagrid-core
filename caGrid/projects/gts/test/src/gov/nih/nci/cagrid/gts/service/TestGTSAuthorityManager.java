package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gts.bean.AuthorityGTS;
import gov.nih.nci.cagrid.gts.bean.AuthorityPrioritySpecification;
import gov.nih.nci.cagrid.gts.bean.AuthorityPriorityUpdate;
import gov.nih.nci.cagrid.gts.bean.TimeToLive;
import gov.nih.nci.cagrid.gts.service.db.AuthorityTable;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalAuthorityFault;
import gov.nih.nci.cagrid.gts.test.Utils;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.TestCase;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestGTSAuthorityManager extends TestCase {

	private DBManager db;

	private final String GTS_URI = "localhost";


	public TestGTSAuthorityManager() {

	}


	public void testCreateAndDestroy() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		try {
			am.clearDatabase();
			assertTrue(db.getDatabase().tableExists(AuthorityTable.TABLE_NAME));
			am.clearDatabase();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddInvalidAuthority() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		try {
			am.clearDatabase();
			TimeToLive ttl = new TimeToLive();
			ttl.setHours(1);
			ttl.setMinutes(1);
			ttl.setSeconds(1);

			// Add Authority no serviceURI
			AuthorityGTS a1 = new AuthorityGTS();
			a1.setPriority(1);
			a1.setPerformAuthorization(true);
			a1.setServiceIdentity("Service");
			a1.setSyncTrustLevels(true);
			a1.setTimeToLive(ttl);
			try {
				am.addAuthority(a1);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Add Authority no ttl
			AuthorityGTS a2 = new AuthorityGTS();
			a2.setServiceURI("Service");
			a2.setPriority(1);
			a2.setPerformAuthorization(true);
			a2.setServiceIdentity("Service");
			a2.setSyncTrustLevels(true);
			try {
				am.addAuthority(a2);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Add Authority no service identity
			AuthorityGTS a3 = new AuthorityGTS();
			a3.setServiceURI("Service");
			a3.setPriority(1);
			a3.setPerformAuthorization(true);
			a3.setSyncTrustLevels(true);
			a3.setTimeToLive(ttl);
			try {
				am.addAuthority(a3);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}
			a3.setServiceIdentity("");
			try {
				am.addAuthority(a3);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			a3.setServiceIdentity("    ");
			try {
				am.addAuthority(a3);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Invalid Priority
			AuthorityGTS a4 = new AuthorityGTS();
			a4.setServiceURI("Service");
			a4.setPriority(0);
			a4.setPerformAuthorization(true);
			a4.setSyncTrustLevels(true);
			a4.setServiceIdentity("Service");
			a4.setTimeToLive(ttl);
			try {
				am.addAuthority(a4);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Invalid Time To Sync
			AuthorityGTS a5 = new AuthorityGTS();
			a5.setServiceURI("Service");
			a5.setPriority(1);
			a5.setPerformAuthorization(true);
			a5.setSyncTrustLevels(true);
			a5.setServiceIdentity("Service");
			TimeToLive ttl2 = new TimeToLive();
			ttl2.setHours(0);
			ttl2.setMinutes(0);
			ttl2.setSeconds(1);
			a5.setTimeToLive(ttl2);
			try {
				am.addAuthority(a5);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			AuthorityGTS a6 = getAuthority("GTS 6", 1);
			am.addAuthority(a6);
			assertTrue(am.doesAuthorityExist(a6.getServiceURI()));
			assertEquals(1, am.getAuthorityCount());
			assertEquals(a6, am.getAuthority(a6.getServiceURI()));

			try {
				am.addAuthority(a6);
				fail("Should not be able to add authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testUpdateInvalidAuthority() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		try {
			am.clearDatabase();
			TimeToLive ttl = new TimeToLive();
			ttl.setHours(10);
			ttl.setMinutes(10);
			ttl.setSeconds(10);

			AuthorityGTS a = getAuthority("GTS", 1);
			am.addAuthority(a);
			assertTrue(am.doesAuthorityExist(a.getServiceURI()));
			assertEquals(1, am.getAuthorityCount());
			assertEquals(a, am.getAuthority(a.getServiceURI()));

			// First make sure update works

			a.setTimeToLive(ttl);
			am.updateAuthority(a);

			assertTrue(am.doesAuthorityExist(a.getServiceURI()));
			assertEquals(1, am.getAuthorityCount());
			assertEquals(a, am.getAuthority(a.getServiceURI()));

			// Add Authority no serviceURI
			AuthorityGTS a1 = getAuthority("GTS", 1);
			a1.setServiceURI(null);
			try {
				am.updateAuthority(a1);
				fail("Should not be able to update authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Add Authority no ttl
			AuthorityGTS a2 = getAuthority("GTS", 1);
			a2.setTimeToLive(null);
			try {
				am.addAuthority(a2);
				fail("Should not be able to update authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Add Authority no service identity
			AuthorityGTS a3 = getAuthority("GTS", 1);
			a3.setServiceIdentity(null);
			try {
				am.addAuthority(a3);
				fail("Should not be able to update authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			a3.setServiceIdentity("");
			try {
				am.addAuthority(a3);
				fail("Should not be able to update authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			a3.setServiceIdentity("   ");
			try {
				am.addAuthority(a3);
				fail("Should not be able to update authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Invalid Priority
			AuthorityGTS a4 = getAuthority("GTS", 1);
			a4.setPriority(2);
			try {
				am.addAuthority(a4);
				fail("Should not be able to update authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

			// Adding Self
			AuthorityGTS a5 = getAuthority(GTS_URI, 1);
			a5.setPriority(2);
			try {
				am.addAuthority(a5);
				fail("Should not be able to update authority!!!");
			} catch (IllegalAuthorityFault f) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddAuthority() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		try {
			am.clearDatabase();
			AuthorityGTS a1 = getAuthority("GTS 1", 1);
			assertFalse(am.doesAuthorityExist(a1.getServiceURI()));
			assertEquals(0, am.getAuthorityCount());
			am.addAuthority(a1);
			assertTrue(am.doesAuthorityExist(a1.getServiceURI()));
			assertEquals(1, am.getAuthorityCount());
			assertEquals(a1, am.getAuthority(a1.getServiceURI()));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddUpdateRemoveAuthorities() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		int count = 5;
		AuthorityGTS[] a = new AuthorityGTS[count];

		try {
			am.clearDatabase();
			for (int i = 0; i < count; i++) {
				a[i] = getAuthority("GTS " + i, 1);
				assertFalse(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals(i, am.getAuthorityCount());
				am.addAuthority(a[i]);
				assertTrue(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals((i + 1), am.getAuthorityCount());
				assertEquals(a[i], am.getAuthority(a[i].getServiceURI()));
				for (int j = 0; j < i; j++) {
					a[j].setPriority(a[j].getPriority() + 1);
					assertEquals(a[j], am.getAuthority(a[j].getServiceURI()));
				}
			}

			for (int i = 0; i < count; i++) {
				updateAuthority(a[i]);
				am.updateAuthority(a[i]);
				assertTrue(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals(count, am.getAuthorityCount());
				assertEquals(a[i], am.getAuthority(a[i].getServiceURI()));
			}
			int priority = 1;
			AuthorityPrioritySpecification[] specs = new AuthorityPrioritySpecification[count];
			for (int i = 0; i < count; i++) {
				a[i].setPriority(priority);
				specs[i] = new AuthorityPrioritySpecification();
				specs[i].setServiceURI(a[i].getServiceURI());
				specs[i].setPriority(a[i].getPriority());
				priority = priority + 1;
			}
			AuthorityPriorityUpdate update = new AuthorityPriorityUpdate();
			update.setAuthorityPrioritySpecification(specs);
			am.updateAuthorityPriorities(update);

			for (int i = 0; i < count; i++) {
				assertTrue(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals(count, am.getAuthorityCount());
				assertEquals(a[i], am.getAuthority(a[i].getServiceURI()));
			}
			AuthorityGTS[] auths = am.getAuthorities();
			for (int i = 0; i < count; i++) {
				assertEquals(a[i], auths[i]);
			}
			int num = count;
			for (int i = 0; i < count; i++) {
				am.removeAuthority(a[i].getServiceURI());
				num = num - 1;
				assertFalse(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals(num, am.getAuthorityCount());
				for (int j = (i + 1); j <= i; j++) {
					a[j].setPriority(a[j].getPriority() - 1);
					assertEquals(a[j], am.getAuthority(a[j].getServiceURI()));
				}
			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testInvalidUpdatePrioritiesAuthorities() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		int count = 5;
		AuthorityGTS[] a = new AuthorityGTS[count];

		try {
			am.clearDatabase();
			for (int i = 0; i < count; i++) {
				a[i] = getAuthority("GTS " + i, 1);
				assertFalse(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals(i, am.getAuthorityCount());
				am.addAuthority(a[i]);
				assertTrue(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals((i + 1), am.getAuthorityCount());
				assertEquals(a[i], am.getAuthority(a[i].getServiceURI()));
				for (int j = 0; j < i; j++) {
					a[j].setPriority(a[j].getPriority() + 1);
					assertEquals(a[j], am.getAuthority(a[j].getServiceURI()));
				}
			}

			// First insert invalid amount
			int priority = 1;
			AuthorityPrioritySpecification[] specs = new AuthorityPrioritySpecification[(count - 1)];
			for (int i = 0; i < (count - 1); i++) {
				a[i].setPriority(priority);
				specs[i] = new AuthorityPrioritySpecification();
				specs[i].setServiceURI(a[i].getServiceURI());
				specs[i].setPriority(a[i].getPriority());
				priority = priority + 1;
			}
			AuthorityPriorityUpdate update = new AuthorityPriorityUpdate();
			update.setAuthorityPrioritySpecification(specs);
			try {
				am.updateAuthorityPriorities(update);
				fail("Should not be able to update priorities");
			} catch (IllegalAuthorityFault g) {

			}

			// Invalid priority
			int priority2 = 2;
			AuthorityPrioritySpecification[] specs2 = new AuthorityPrioritySpecification[count];
			for (int i = 0; i < count; i++) {
				a[i].setPriority(priority2);
				specs2[i] = new AuthorityPrioritySpecification();
				specs2[i].setServiceURI(a[i].getServiceURI());
				specs2[i].setPriority(a[i].getPriority());
				priority2 = priority2 + 1;
			}
			AuthorityPriorityUpdate update2 = new AuthorityPriorityUpdate();
			update2.setAuthorityPrioritySpecification(specs2);
			try {
				am.updateAuthorityPriorities(update2);
				fail("Should not be able to update priorities");
			} catch (IllegalAuthorityFault g) {

			}

			// Invalid priority
			AuthorityPrioritySpecification[] specs3 = new AuthorityPrioritySpecification[count];
			for (int i = 0; i < count; i++) {
				a[i].setPriority(1);
				specs3[i] = new AuthorityPrioritySpecification();
				specs3[i].setServiceURI(a[i].getServiceURI());
				specs3[i].setPriority(a[i].getPriority());
			}
			AuthorityPriorityUpdate update3 = new AuthorityPriorityUpdate();
			update3.setAuthorityPrioritySpecification(specs3);
			try {
				am.updateAuthorityPriorities(update3);
				fail("Should not be able to update priorities");
			} catch (IllegalAuthorityFault g) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testUpdateRollback() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		int count = 5;
		AuthorityGTS[] a = new AuthorityGTS[count];
		Connection c = null;
		try {
			am.clearDatabase();
			for (int i = 0; i < count; i++) {
				a[i] = getAuthority("GTS " + i, 1);
				assertFalse(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals(i, am.getAuthorityCount());
				am.addAuthority(a[i]);
				assertTrue(am.doesAuthorityExist(a[i].getServiceURI()));
				assertEquals((i + 1), am.getAuthorityCount());
				assertEquals(a[i], am.getAuthority(a[i].getServiceURI()));

				for (int j = 0; j < i; j++) {
					a[j].setPriority(a[j].getPriority() + 1);
					assertEquals(a[j], am.getAuthority(a[j].getServiceURI()));
				}
			}
			c = db.getDatabase().getConnection();
			c.setAutoCommit(false);
			for (int i = 0; i < count; i++) {
				a[i].setPriority(a[i].getPriority() + 1);
				am.updateAuthorityPriority(c, a[i].getServiceURI(), a[i].getPriority());
			}

			for (int i = 0; i < count; i++) {
				assertEquals((a[i].getPriority() - 1), am.getAuthority(a[i].getServiceURI()).getPriority());
			}

			c.commit();

			for (int i = 0; i < count; i++) {
				assertEquals(a[i], am.getAuthority(a[i].getServiceURI()));
			}

			for (int i = 0; i < count; i++) {
				a[i].setPriority(a[i].getPriority() + 1);
				am.updateAuthorityPriority(c, a[i].getServiceURI(), a[i].getPriority());
			}

			try {
				PreparedStatement bad = c.prepareStatement("INSERT INTO NOTHING SET VALUES(1,2)");
				bad.executeUpdate();
				try {
					c.commit();
				} catch (Exception com) {
					c.rollback();
				}
			} catch (Exception ex) {
				c.rollback();
			}
			for (int i = 0; i < count; i++) {
				a[i].setPriority(a[i].getPriority() - 1);
				assertEquals(a[i], am.getAuthority(a[i].getServiceURI()));

			}
			c.setAutoCommit(true);
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			if (c != null) {
				try {
					c.setAutoCommit(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
				db.getDatabase().releaseConnection(c);
			}
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddAuthorityOverwritePriority() {
		GTSAuthorityManager am = new GTSAuthorityManager(GTS_URI, getAuthoritySyncTime(), db);
		try {
			am.clearDatabase();
			AuthorityGTS a1 = getAuthority("GTS 1", 1);
			assertFalse(am.doesAuthorityExist(a1.getServiceURI()));
			assertEquals(0, am.getAuthorityCount());
			am.addAuthority(a1);
			assertTrue(am.doesAuthorityExist(a1.getServiceURI()));
			assertEquals(1, am.getAuthorityCount());
			assertEquals(a1, am.getAuthority(a1.getServiceURI()));

			AuthorityGTS a2 = getAuthority("GTS 2", 1);
			am.addAuthority(a2);
			assertTrue(am.doesAuthorityExist(a2.getServiceURI()));
			assertEquals(2, am.getAuthorityCount());
			assertEquals(a2, am.getAuthority(a2.getServiceURI()));

			a1.setPriority(2);
			assertEquals(a1, am.getAuthority(a1.getServiceURI()));

		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				am.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	private AuthorityGTS getAuthority(String uri, int priority) {
		TimeToLive ttl = new TimeToLive();
		ttl.setHours(1);
		ttl.setMinutes(1);
		ttl.setSeconds(1);
		AuthorityGTS a1 = new AuthorityGTS();
		a1.setServiceURI(uri);
		a1.setPriority(1);
		a1.setPerformAuthorization(true);
		a1.setServiceIdentity(uri);
		a1.setSyncTrustLevels(true);
		a1.setTimeToLive(ttl);
		return a1;
	}


	private AuthoritySyncTime getAuthoritySyncTime() {
		AuthoritySyncTime time = new AuthoritySyncTime(0, 0, 2);
		return time;
	}


	private void updateAuthority(AuthorityGTS gts) {
		TimeToLive ttl = new TimeToLive();
		ttl.setHours(10);
		ttl.setMinutes(10);
		ttl.setSeconds(10);
		gts.setPerformAuthorization(false);
		gts.setServiceIdentity(null);
		gts.setSyncTrustLevels(false);
		gts.setTimeToLive(ttl);
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
