package gov.nih.nci.cagrid.cadsr;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class CaDSRServiceCycleTestCase extends CycleTestCase {
	public CaDSRServiceCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(CaDSRServiceCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
