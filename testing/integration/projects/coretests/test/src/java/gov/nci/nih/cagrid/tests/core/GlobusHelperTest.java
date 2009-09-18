/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.GlobusCheckRunningStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusInstallSecurityDescriptorStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStopStep;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * This is a unit test that validates the functionality of the GlobusHelper
 * class, which is used to create, start, stop, deploy services, and cleanup
 * instances of Globus.
 * 
 * @testType unit
 * @steps GlobusCreateStep, GlobusStartStep
 * @steps GlobusDeployServiceStep, GlobusCheckRunningStep
 * @steps GlobusStopStep, GlobusCleanupStep
 * @author Patrick McConnell
 */
public class GlobusHelperTest extends Story {
    private GlobusHelper globus;
    private GlobusHelper secureGlobus;


    public GlobusHelperTest() {
        super();
    }


    @Override
    public String getName() {
        return "GlobusHelper Story";
    }


    @Override
    protected boolean storySetUp() throws Throwable {
        return true;
    }


    @Override
    protected void storyTearDown() throws Throwable {
        if (this.globus != null) {
            this.globus.stopGlobus();
            this.globus.cleanupTempGlobus();
        }
        if (this.secureGlobus != null) {
            this.secureGlobus.stopGlobus();
            this.secureGlobus.cleanupTempGlobus();
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Vector steps() {
        this.globus = new GlobusHelper();
        this.secureGlobus = new GlobusHelper(true);

        Vector steps = new Vector();

        steps.add(new GlobusCreateStep(this.globus));
        steps.add(new GlobusStartStep(this.globus));
        steps.add(new GlobusCheckRunningStep(this.globus));

        steps.add(new GlobusCreateStep(this.secureGlobus));
        steps.add(new GlobusInstallSecurityDescriptorStep(this.secureGlobus));
        steps.add(new GlobusStartStep(this.secureGlobus));

        steps.add(new GlobusStopStep(this.globus));
        steps.add(new GlobusCleanupStep(this.globus));

        steps.add(new GlobusStopStep(this.secureGlobus));
        steps.add(new GlobusCleanupStep(this.secureGlobus));

        return steps;
    }


    @Override
    public String getDescription() {
        return "GlobusHelperTest";
    }


    /**
     * used to make sure that if we are going to use a junit testsuite to test
     * this that the test suite will not error out looking for a single
     * test......
     */
    public void testDummy() throws Throwable {
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(GlobusHelperTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
