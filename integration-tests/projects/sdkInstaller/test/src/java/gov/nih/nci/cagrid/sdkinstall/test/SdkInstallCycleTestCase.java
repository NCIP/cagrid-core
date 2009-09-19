package gov.nih.nci.cagrid.sdkinstall.test;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class SdkInstallCycleTestCase extends CycleTestCase {

	public SdkInstallCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {

		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(SdkInstallCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}