package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.gts.bean.TrustLevel;
import gov.nih.nci.cagrid.gts.service.db.DBManager;
import gov.nih.nci.cagrid.gts.service.db.TrustLevelTable;
import gov.nih.nci.cagrid.gts.stubs.types.GTSInternalFault;
import gov.nih.nci.cagrid.gts.stubs.types.IllegalTrustLevelFault;
import gov.nih.nci.cagrid.gts.test.Utils;
import junit.framework.TestCase;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestTrustLevelManager extends TestCase implements TrustedAuthorityLevelRemover {

	private DBManager db;

	private TrustLevel ref;

	private final static String GTS_URI = "localhost";


	public TestTrustLevelManager() {
		ref = new TrustLevel();
		ref.setName("REF");
	}


	public void testCreateAndDestroy() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			trust.clearDatabase();
			trust.buildDatabase();
			assertTrue(db.getDatabase().tableExists(TrustLevelTable.TABLE_NAME));
			trust.clearDatabase();
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddRemoveTrustLevel() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			TrustLevel level = new TrustLevel();
			level.setName("One");
			level.setDescription("Trust Level One");
			trust.addTrustLevel(level);
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));
			trust.removeTrustLevel(level.getName());
			assertEquals(0, trust.getTrustLevels().length);
			assertEquals(false, trust.doesTrustLevelExist(level.getName()));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddUpdateRemoveExternalTrustLevel() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			TrustLevel level = new TrustLevel();
			level.setName("One");
			level.setDescription("Trust Level One");
			level.setIsAuthority(Boolean.FALSE);
			level.setAuthorityGTS("somehost");
			level.setSourceGTS("somehost");
			trust.addTrustLevel(level, false);
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));
			level.setAuthorityGTS("someotherhost");
			level.setSourceGTS("someotherhost");
			trust.updateTrustLevel(level, false);
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));

			trust.removeTrustLevel(level.getName());
			assertEquals(0, trust.getTrustLevels().length);
			assertEquals(false, trust.doesTrustLevelExist(level.getName()));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddIllegalTrustLevel() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			TrustLevel level = new TrustLevel();
			level.setName("One");
			level.setDescription("Trust Level One");
			trust.addTrustLevel(level);

			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));

			try {
				trust.addTrustLevel(level);
				fail("Trust Level should not be able to be added when it already exists!!!");
			} catch (IllegalTrustLevelFault f) {

			}
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));

			try {
				trust.addTrustLevel(new TrustLevel());
				fail("Trust Level should not be able to be added without an name!!!");
			} catch (IllegalTrustLevelFault f) {

			}
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));

			trust.removeTrustLevel(level.getName());
			assertEquals(0, trust.getTrustLevels().length);
			assertEquals(false, trust.doesTrustLevelExist(level.getName()));

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddIllegalExternalTrustLevel() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			TrustLevel level = new TrustLevel();
			level.setName("One");
			level.setDescription("Trust Level One");
			level.setIsAuthority(Boolean.FALSE);
			level.setAuthorityGTS("somehost");
			level.setSourceGTS("somehost");
			trust.addTrustLevel(level, false);
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));
			try {
				trust.addTrustLevel(level, false);
				fail("Trust Level should not be able to be added when it already exists!!!");
			} catch (IllegalTrustLevelFault f) {

			}
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));

			trust.removeTrustLevel(level.getName());
			assertEquals(0, trust.getTrustLevels().length);
			assertEquals(false, trust.doesTrustLevelExist(level.getName()));

			try {
				trust.addTrustLevel(new TrustLevel(), false);
				fail("Trust Level should not be able to be added without an name!!!");
			} catch (IllegalTrustLevelFault f) {

			}
			assertEquals(0, trust.getTrustLevels().length);

			// Test Adding without authority

			try {
				TrustLevel tl = new TrustLevel();
				tl.setName("One");
				tl.setDescription("Trust Level One");
				tl.setAuthorityGTS("somehost");
				tl.setSourceGTS("somehost");
				trust.addTrustLevel(tl, false);
				fail("Trust Level should not be able to be added!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(0, trust.getTrustLevels().length);

			// Test Adding without Authority Trust Service

			try {
				TrustLevel tl = new TrustLevel();
				tl.setName("One");
				tl.setDescription("Trust Level One");
				tl.setIsAuthority(Boolean.FALSE);
				tl.setSourceGTS("somehost");
				trust.addTrustLevel(tl, false);
				fail("Trust Level should not be able to be added!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(0, trust.getTrustLevels().length);

			// Test Adding without source trust service

			try {
				TrustLevel tl = new TrustLevel();
				tl.setName("One");
				tl.setDescription("Trust Level One");
				tl.setIsAuthority(Boolean.FALSE);
				tl.setAuthorityGTS("somehost");
				trust.addTrustLevel(tl, false);
				fail("Trust Level should not be able to be added!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(0, trust.getTrustLevels().length);

			TrustLevel level2 = new TrustLevel();
			level2.setName("One");
			level2.setDescription("Trust Level One");
			level2.setIsAuthority(Boolean.TRUE);
			level2.setAuthorityGTS("someotherhost");
			level2.setSourceGTS("someotherhost");
			try {
				trust.addTrustLevel(level2, false);
				fail("Trust Level should not be able to be added without an name!!!");
			} catch (IllegalTrustLevelFault f) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testUpdateIllegalTrustLevel() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			TrustLevel level = new TrustLevel();
			level.setName("One");
			level.setDescription("Trust Level One");
			level.setIsAuthority(Boolean.TRUE);
			level.setAuthorityGTS(GTS_URI);
			level.setSourceGTS(GTS_URI);
			trust.addTrustLevel(level, false);
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));

			// Test changing the authority

			TrustLevel tl = trust.getTrustLevel(level.getName());
			try {
				tl.setIsAuthority(Boolean.FALSE);
				trust.updateTrustLevel(tl);
				fail("Trust Level should not be able to be updated!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(1, trust.getTrustLevels().length);

			tl = trust.getTrustLevel(level.getName());
			try {
				tl.setAuthorityGTS("someotherhost");
				trust.updateTrustLevel(tl);
				fail("Trust Level should not be able to be updated!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(1, trust.getTrustLevels().length);

			tl = trust.getTrustLevel(level.getName());
			try {
				tl.setSourceGTS("someotherhost");
				trust.updateTrustLevel(tl);
				fail("Trust Level should not be able to be updated!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(1, trust.getTrustLevels().length);

			TrustLevel level2 = new TrustLevel();
			level2.setName("Two");
			level2.setDescription("Trust Level Two");
			level2.setIsAuthority(Boolean.FALSE);
			level2.setAuthorityGTS("some other host");
			level2.setSourceGTS("some other host");
			trust.addTrustLevel(level2, false);

			assertEquals(2, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level2.getName()));
			assertEquals(level2, trust.getTrustLevel(level2.getName()));

			tl = trust.getTrustLevel(level2.getName());
			try {
				tl.setDescription("new description");
				trust.updateTrustLevel(tl);
				fail("Trust Level should not be able to be updated!!!");
			} catch (IllegalTrustLevelFault f) {

			}

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testUpdateIllegalExternalTrustLevel() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			TrustLevel level = new TrustLevel();
			level.setName("One");
			level.setDescription("Trust Level One");
			trust.addTrustLevel(level);
			assertEquals(1, trust.getTrustLevels().length);
			assertEquals(true, trust.doesTrustLevelExist(level.getName()));
			assertEquals(level, trust.getTrustLevel(level.getName()));

			// Test changing the authority

			TrustLevel tl = trust.getTrustLevel(level.getName());
			try {
				tl.setIsAuthority(Boolean.FALSE);
				trust.updateTrustLevel(tl, false);
				fail("Trust Level should not be able to be updated!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(1, trust.getTrustLevels().length);

			tl = trust.getTrustLevel(level.getName());
			try {
				tl.setAuthorityGTS("someotherhost");
				trust.updateTrustLevel(tl, false);
				fail("Trust Level should not be able to be updated!!!");
			} catch (IllegalTrustLevelFault f) {

			}

			assertEquals(1, trust.getTrustLevels().length);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void testAddGetUpdateRemoveTrustLevels() {
		TrustLevelManager trust = new TrustLevelManager(GTS_URI, this, db);
		try {
			int size = 5;
			TrustLevel[] level = new TrustLevel[size];
			for (int i = 0; i < size; i++) {
				level[i] = new TrustLevel();
				level[i].setName("Level " + i);
				level[i].setDescription("Trust Level " + i);
				trust.addTrustLevel(level[i]);
				assertEquals((i + 1), trust.getTrustLevels().length);
				assertEquals(true, trust.doesTrustLevelExist(level[i].getName()));
				assertEquals(level[i], trust.getTrustLevel(level[i].getName()));
				level[i].setDescription("Updated Trust Level " + i);
				trust.updateTrustLevel(level[i]);
				assertEquals((i + 1), trust.getTrustLevels().length);
				assertEquals(true, trust.doesTrustLevelExist(level[i].getName()));
				assertEquals(level[i], trust.getTrustLevel(level[i].getName()));
			}
			int count = size;
			for (int i = 0; i < size; i++) {
				trust.removeTrustLevel(level[i].getName());
				count = count - 1;
				assertEquals(count, trust.getTrustLevels().length);
				assertEquals(false, trust.doesTrustLevelExist(level[i].getName()));
			}
			assertEquals(0, trust.getTrustLevels().length);

		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		} finally {
			try {
				trust.clearDatabase();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void removeAssociatedTrustedAuthorities(String trustLevel) throws GTSInternalFault {
		// TODO Auto-generated method stub

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
		} catch (Exception e) {
			FaultUtil.printFault(e);
			assertTrue(false);
		}
	}

}
