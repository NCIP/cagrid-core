package gov.nih.nci.cagrid.tools.cycle;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class ToolsCycleTestCase extends CycleTestCase {

	public ToolsCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {

		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(ToolsCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}