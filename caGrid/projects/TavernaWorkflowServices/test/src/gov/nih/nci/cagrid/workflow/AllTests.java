package gov.nih.nci.cagrid.workflow;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for TavernaWorkflowService");
		//$JUnit-BEGIN$
		suite.addTestSuite(BasicTests.class);
		//$JUnit-END$
		return suite;
	}

}
