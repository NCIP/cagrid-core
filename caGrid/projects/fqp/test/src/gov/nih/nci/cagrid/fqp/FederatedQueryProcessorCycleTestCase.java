package gov.nih.nci.cagrid.fqp;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class FederatedQueryProcessorCycleTestCase extends CycleTestCase {
	public FederatedQueryProcessorCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(FederatedQueryProcessorCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
