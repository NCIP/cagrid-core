package gov.nih.nci.cagrid.introduce.test.unit;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class IntroduceCycleTestCase extends CycleTestCase {

	public IntroduceCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(IntroduceCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}