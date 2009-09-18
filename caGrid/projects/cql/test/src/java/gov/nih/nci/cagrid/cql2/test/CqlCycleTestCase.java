package gov.nih.nci.cagrid.cql2.test;


import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class CqlCycleTestCase extends CycleTestCase {

	public CqlCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {

		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(CqlCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}