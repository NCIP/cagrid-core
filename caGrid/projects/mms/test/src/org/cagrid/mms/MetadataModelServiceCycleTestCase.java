package org.cagrid.mms;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class MetadataModelServiceCycleTestCase extends CycleTestCase {
	public MetadataModelServiceCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(MetadataModelServiceCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
