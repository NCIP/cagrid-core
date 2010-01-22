package org.cagrid.data.test.creation.transfer;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;

/** 
 *  CreateTransferTests
 *  Tests creation of a transfer supporting caGrid Data Service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 30, 2006 
 * @version $Id: CreateTransferTests.java,v 1.1 2009-04-10 17:30:10 dervin Exp $ 
 */
public class CreateTransferTests extends Story {
	public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";
	public static final String SERVICE_NAME = "TestTransferDataService";
	public static final String PACKAGE_NAME = "gov.nih.nci.cagrid.test.xferds";
	public static final String SERVICE_NAMESPACE = "http://" + PACKAGE_NAME + "/" + SERVICE_NAME;
    
    public String getName() {
        return "Transfer Data Service Creation Tests";
    }
    
	
	public String getDescription() {
		return "Tests creation of a transfer supporting caGrid Data Service";
	}


	protected Vector<?> steps() {
        DataTestCaseInfo info = new TestTransferDataServiceInfo();
		Vector<Step> steps = new Vector<Step>();
		// delete any existing transfer data service directory
		steps.add(new DeleteOldServiceStep(info));
		// create a new transfer data service
		steps.add(new CreateTransferDataServiceStep(info, getIntroduceBaseDir()));
		return steps;
	}
	
	
	private String getIntroduceBaseDir() {
		String dir = System.getProperty(INTRODUCE_DIR_PROPERTY);
		if (dir == null) {
			fail("Introduce base dir environment variable " + INTRODUCE_DIR_PROPERTY + " is required");
		}
		return dir;
	}


	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(CreateTransferTests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
    
    
    public static class TestTransferDataServiceInfo extends DataTestCaseInfo {
        public String getName() {
            return SERVICE_NAME;
        }


        public String getServiceDirName() {
            return SERVICE_NAME;
        }


        public String getNamespace() {
            return SERVICE_NAMESPACE;
        }


        public String getPackageName() {
            return PACKAGE_NAME;
        }
    }
}
