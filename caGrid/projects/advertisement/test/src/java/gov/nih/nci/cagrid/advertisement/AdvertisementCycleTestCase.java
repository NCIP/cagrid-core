package gov.nih.nci.cagrid.advertisement;


import gov.nih.nci.cagrid.core.CycleTestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class AdvertisementCycleTestCase extends CycleTestCase {

	public AdvertisementCycleTestCase(String name) {
		super(name);
	}


	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(AdvertisementCycleTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}