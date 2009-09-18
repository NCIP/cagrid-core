package org.cagrid.gme;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class GlobalModelExchangeCycleTestCase extends CycleTestCase {
	public GlobalModelExchangeCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(GlobalModelExchangeCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
