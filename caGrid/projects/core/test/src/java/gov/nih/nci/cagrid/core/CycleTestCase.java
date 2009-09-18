package gov.nih.nci.cagrid.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class CycleTestCase extends TestCase {

    private JDepend jdepend;


    public CycleTestCase(String name) {
        super(name);
    }


    protected void setUp() {
        jdepend = new JDepend();
        try {
            String dir = System.getProperty("build.dir", ".");
            jdepend.addDirectory(dir);
            jdepend.analyzeTestClasses(false);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail(ioe.getMessage());
        }
    }


    protected void tearDown() {
        jdepend = null;
    }


    /**
     * Tests that a package dependency cycle does not exist for any of the
     * analyzed packages.
     */
    public void testAllPackagesCycle() {
        int numCycles = 0;
        Collection packages = jdepend.analyze();
        if (jdepend.containsCycles()) {
            Iterator iter = packages.iterator();
            while (iter.hasNext()) {
                JavaPackage p = (JavaPackage) iter.next();
                if (p.containsCycle()) {
                    System.out.println("\nPackage: " + p.getName() + " contains a cycle with:");
                    numCycles++;
                    List list = new ArrayList();
                    p.collectAllCycles(list);
                    for (Iterator iterator = list.iterator(); iterator.hasNext();) {
                        JavaPackage dependsP = (JavaPackage) iterator.next();
                        System.out.println("->" + dependsP.getName());

                    }
                }
            }
        }
        System.out.println("\n===== Found " + numCycles + " cyclic packages. =====\n\n");

        assertEquals("Cycles exist", false, jdepend.containsCycles());
    }


    public static void main(String args[]) {

        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(CycleTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}