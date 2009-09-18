package gov.nih.nci.cagrid.data.extensions.test;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class DataExtensionsCycleTestCase extends CycleTestCase {

	public DataExtensionsCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {

		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(DataExtensionsCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}