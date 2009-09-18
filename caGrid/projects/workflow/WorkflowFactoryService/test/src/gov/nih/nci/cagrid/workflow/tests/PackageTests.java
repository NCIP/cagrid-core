package gov.nih.nci.cagrid.workflow.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.globus.wsrf.test.GridTestSuite;

public class PackageTests extends GridTestSuite {
	public PackageTests(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public void run(TestResult result){
		super.run(result);
	}
	
	public static Test suite() throws Exception {
		TestSuite suite = new PackageTests("Workflow Tests");
		suite.addTestSuite(BasicTests.class);
	//	suite.addTestSuite(BasicSecurityTests.class);
		return suite;
	}
}
