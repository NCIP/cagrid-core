package gov.nih.nci.cagrid.sdkquery32.test;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class SDKQuery32CycleTestCase extends CycleTestCase {

    public SDKQuery32CycleTestCase(String name) {
        super(name);
    }


    public static void main(String args[]) {

        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SDKQuery32CycleTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
