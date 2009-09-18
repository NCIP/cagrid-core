/*
 * Created on Jul 31, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.compare.XmlComparator;
import gov.nci.nih.cagrid.tests.core.util.SimpleXmlReplacer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * This is a unit test that validates the functionality of the SimpleXmlReplacer
 * class, which replaces values in XML elements by looking line-by-line for
 * matching elements.
 * 
 * @testType unit
 * @author Patrick McConnell
 */
public class SimpleXmlReplacerTest extends TestCase {
    private static final boolean DEBUG = false;

    private File testDir;


    public SimpleXmlReplacerTest(String name) {
        super(name);

    }


    /**
     * This tests whether SimpleXmlReplacer can replace a single value in a
     * simple XML document.
     */
    public void testSimple() throws Exception {
        performTest("simple");
    }


    /**
     * This tests whether SimpleXmlReplacer can replace a single value in an
     * element that has no end tag.
     */
    public void testEmpty() throws Exception {
        performTest("empty");
    }


    /**
     * This tests whether SimpleXmlReplacer can replace a single value in an
     * element that has no content but does have an end tag.
     */
    public void testZero() throws Exception {
        performTest("zero");
    }


    /**
     * This tests whether SimpleXmlReplacer can replace a multiple values in a
     * somewhat simple XML document.
     */
    public void testMultiple() throws Exception {
        performTest("multiple");
    }


    /**
     * This tests whether SimpleXmlReplacer can replace a single value in an
     * element that has attributes.
     */
    public void testAttributes() throws Exception {
        performTest("attributes");
    }


    /**
     * This tests whether SimpleXmlReplacer can replace a single value in an
     * element that has a space before the end of the start tag.
     */
    public void testSpace() throws Exception {
        performTest("space");
    }


    /**
     * This tests whether SimpleXmlReplacer properly throws an exception when a
     * replacement cannot be found.
     */
    public void testFail() throws Exception {
        Exception exception = null;
        try {
            performTest("fail");
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof IOException);
        assertFalse(exception.getMessage().contains("found"));
        assertTrue(exception.getMessage().contains("missing"));
    }


    private void performTest(String testName) throws Exception {
        File inFile = new File("test", "resources" + File.separator + "SimpleXmlReplacerTest" + File.separator
            + testName + ".xml");
        File propFile = new File("test", "resources" + File.separator + "SimpleXmlReplacerTest" + File.separator
            + testName + ".properties");
        File compareFile = new File("test", "resources" + File.separator + "SimpleXmlReplacerTest" + File.separator
            + testName + "_replaced.xml");
        File outFile = File.createTempFile("SimpleXmlReplacerTest", ".xml");
        if (!DEBUG) {
            outFile.deleteOnExit();
        }

        try {
            SimpleXmlReplacer replacer = new SimpleXmlReplacer();

            // load properties
            Properties props = new Properties();
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(propFile));
            props.load(is);
            is.close();
            for (Object name : props.keySet()) {
                replacer.addReplacement((String) name, props.getProperty((String) name));
            }

            // perform replacement
            replacer.performReplacement(inFile, outFile);

            // compare
            new XmlComparator().isEqual(new File[]{outFile, compareFile});
        } finally {
            if (DEBUG) {
                System.out.println(outFile);
            } else {
                outFile.delete();
            }
        }
    }


    public static void main(String[] args) throws Exception {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SimpleXmlReplacerTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
