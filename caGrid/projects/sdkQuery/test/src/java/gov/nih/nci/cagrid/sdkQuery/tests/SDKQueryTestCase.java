package gov.nih.nci.cagrid.sdkQuery.tests;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class SDKQueryTestCase extends CycleTestCase {

	public SDKQueryTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {

		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(CycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}