package gov.nih.nci.cagrid.metadata;

import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class MetadataUtilsCycleTestCase extends CycleTestCase {

	public MetadataUtilsCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(MetadataUtilsCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}