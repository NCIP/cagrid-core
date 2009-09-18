package org.cagrid.gaards.dorian.common;

import gov.nih.nci.cagrid.common.FaultUtil;
import junit.framework.TestCase;

import org.cagrid.gaards.dorian.Metadata;
import org.cagrid.gaards.dorian.service.MetadataManager;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestMetadataManager extends TestCase {

    private static final String TABLE = "test_metadata";

    private Database db;


    public void testDelete() {

        MetadataManager mm = new MetadataManager(db, TABLE);
        try {
            mm.clearDatabase();
            // mm.destroy();
            int count = 20;

            // Test Insert;
            for (int i = 0; i < count; i++) {
                Metadata data = new Metadata();
                data.setName("name" + i);
                data.setValue("value" + i);
                data.setDescription("description" + i);
                mm.insert(data);
                Metadata out = mm.get(data.getName());
                assertNotNull(out);
                assertEquals(data, out);
            }

            // Test delete
            for (int i = 0; i < count; i++) {
                String n = "name" + count;
                assertNull(mm.get(n));

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                mm.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testUpdate() {
        MetadataManager mm = new MetadataManager(db, TABLE);
        try {
            mm.clearDatabase();
            int count = 20;

            // Test Insert;
            for (int i = 0; i < count; i++) {
                Metadata data = new Metadata();
                data.setName("name" + i);
                data.setValue("value" + i);
                data.setDescription("description" + i);
                mm.insert(data);
                Metadata out = mm.get(data.getName());
                assertNotNull(out);
                assertEquals(data, out);
            }
            // Test update
            for (int i = 0; i < count; i++) {
                Metadata data = new Metadata();
                data.setName("name" + i);
                data.setValue("changedvalue" + i);
                data.setDescription("description" + i);
                mm.update(data);
                Metadata out = mm.get(data.getName());
                assertNotNull(out);
                assertEquals(data, out);
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                mm.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testInsert() {
        MetadataManager mm = new MetadataManager(db, TABLE);
        try {
            mm.clearDatabase();
            int count = 20;

            // Test Insert;
            for (int i = 0; i < count; i++) {
                Metadata data = new Metadata();
                data.setName("name" + i);
                data.setValue("value" + i);
                data.setDescription("description" + i);
                mm.insert(data);
                Metadata out = mm.get(data.getName());
                assertNotNull(out);
                assertEquals(data, out);
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                mm.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    protected void tearDown() throws Exception {
        super.setUp();
        try {
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }

}
