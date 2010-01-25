package org.cagrid.data.test.creation;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.system.BaseSystemTest;

/** 
 *  CreationTests
 *  Tests for creation of a data service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 22, 2006 
 * @version $Id: CreationTests.java,v 1.4 2008-10-28 22:50:57 dervin Exp $ 
 */
public class CreationTests extends BaseSystemTest {	
    public static final String PLAIN_SERVICE_NAME = "PlainTestDataService";
    public static final String PLAIN_PACKAGE_NAME = "gov.nih.nci.cagrid.plainds";
    public static final String PLAIN_SERVICE_NAMESPACE = "http://" + PLAIN_PACKAGE_NAME + "/" + PLAIN_SERVICE_NAME;
    
    public String getName() {
        return "Data Service Creation Tests";
    }
    
	
	public String getDescription() {
		return "Testing the data service creation extension for Introduce"; 
	}
	

	protected Vector<?> steps() {
        DataTestCaseInfo plainInfo = new PlainDataServiceInfo();
        
		Vector<Step> steps = new Vector<Step>();
		// delete any existing service dir
        steps.add(new DeleteOldServiceStep(plainInfo));
		// create new data service
        steps.add(new CreationStep(plainInfo, getIntroduceBaseDir()));
		return steps;
	}


	/**
	 * Convenience method for running all the Steps in this Story.
	 */
	public static void main(String args[]) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(CreationTests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
    
    
    public static class PlainDataServiceInfo extends DataTestCaseInfo {
        public String getName() {
            return PLAIN_SERVICE_NAME;
        }


        public String getServiceDirName() {
            return PLAIN_SERVICE_NAME;
        }


        public String getNamespace() {
            return PLAIN_SERVICE_NAMESPACE;
        }


        public String getPackageName() {
            return PLAIN_PACKAGE_NAME;
        }   
    }
}
