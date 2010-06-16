package org.cagrid.gaards.cds.service;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class CDSCycleTestCase extends CycleTestCase {

	public CDSCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {

		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(CDSCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}