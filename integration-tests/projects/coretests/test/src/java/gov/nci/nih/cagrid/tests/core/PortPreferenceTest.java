package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.util.NoAvailablePortException;
import gov.nci.nih.cagrid.tests.core.util.PortPreference;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class PortPreferenceTest extends TestCase {
    public static final int TEST_PORT_MIN = Integer.parseInt(System.getProperty(PortPreferenceTest.class.getName()
        + ".test.port.min", "5555"));
    public static final int TEST_PORT_MAX = Integer.parseInt(System.getProperty(PortPreferenceTest.class.getName()
        + ".test.port.max", "5560"));;


    @Override
    public void setUp() {
        assertTrue(TEST_PORT_MIN < TEST_PORT_MAX);
        assertTrue(TEST_PORT_MAX - TEST_PORT_MIN > 1);
    }


    public PortPreferenceTest(String name) {
        super(name);
    }


    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }


    public void testIllegalArguments() {
        try {
            new PortPreference(null);
            fail("Should have been illegal argument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new PortPreference(-1);
            fail("Should have been illegal argument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new PortPreference(null, 1, null);
            fail("Should have been illegal argument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new PortPreference(1, null, null);
            fail("Should have been illegal argument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new PortPreference(-1, 1, null);
            fail("Should have been illegal argument");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            new PortPreference(1, -1, null);
            fail("Should have been illegal argument");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }


    public void testNoAvailablePorts() {
        PortPreference pref = new PortPreference(1, 1, new Integer[]{1});
        try {
            pref.getPort();
            fail("No port should have been available.");
        } catch (NoAvailablePortException e) {
            // expected
        }

        pref = new PortPreference(1, 3, new Integer[]{3, 2, 1});
        try {
            pref.getPort();
            fail("No port should have been available.");
        } catch (NoAvailablePortException e) {
            // expected
        }
    }


    public void testGetPort() {
        Integer testPort = new Integer(1);
        PortPreference pref = new PortPreference(testPort);
        try {
            Integer port = pref.getPort();
            assertEquals(testPort, port);
        } catch (NoAvailablePortException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        // max sure we aren't leaving ports bound
        int testRuns = (TEST_PORT_MAX - TEST_PORT_MIN) * 2;
        for (int times = 0; times < testRuns; times++) {
            for (int i = TEST_PORT_MIN; i <= TEST_PORT_MAX; i++) {
                pref = new PortPreference(TEST_PORT_MIN, TEST_PORT_MAX, null);
                try {
                    Integer port = pref.getPort();
                    assertTrue(port >= TEST_PORT_MIN);
                    assertTrue(port <= TEST_PORT_MAX);
                } catch (NoAvailablePortException e) {
                    fail(e.getMessage());
                }
            }
            for (int i = TEST_PORT_MIN; i <= TEST_PORT_MAX; i++) {
                pref = new PortPreference(TEST_PORT_MIN, TEST_PORT_MAX, new Integer[]{TEST_PORT_MIN, TEST_PORT_MAX});
                try {
                    Integer port = pref.getPort();
                    assertTrue(port > TEST_PORT_MIN);
                    assertTrue(port < TEST_PORT_MAX);
                } catch (NoAvailablePortException e) {
                    fail(e.getMessage());
                }
            }
        }

    }


    public static void main(String[] args) throws Exception {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(PortPreferenceTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
