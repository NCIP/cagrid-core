package gov.nih.nci.cagrid.discovery;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class DiscoveryCycleTestCase extends CycleTestCase {

	public DiscoveryCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(DiscoveryCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}