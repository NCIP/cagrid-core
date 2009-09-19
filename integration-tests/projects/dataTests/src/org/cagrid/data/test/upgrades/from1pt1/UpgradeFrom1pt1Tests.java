package org.cagrid.data.test.upgrades.from1pt1;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;
import org.cagrid.data.test.upgrades.BuildUpgradedServiceStep;
import org.cagrid.data.test.upgrades.UnpackOldServiceStep;
import org.cagrid.data.test.upgrades.UpgradeIntroduceServiceStep;
import org.cagrid.data.test.upgrades.UpgradeTestConstants;

/** 
 *  UpgradeFrom1pt1Tests
 *  Tests to upgrade a data service from 1.1 to current
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Feb 20, 2007 
 * @version $Id: UpgradeFrom1pt1Tests.java,v 1.3 2008-10-28 13:51:36 dervin Exp $ 
 */
public class UpgradeFrom1pt1Tests extends Story {
    public static final String SERVICE_ZIP_NAME = "BasicDataService_1-1.zip";
    public static final String SERVICE_DIR_NAME = "BasicDataService_1-1";
    public static final String SERVICE_NAME = "BasicDataService";
    public static final String SERVICE_PACKAGE = "gov.nih.nci.cagrid.basic.data.service";
    public static final String SERVICE_NAMESPACE = "http://service.data.basic.cagrid.nci.nih.gov/BasicDataService";
    
	private DataTestCaseInfo testServiceInfo = null;
    
	public String getDescription() {
		return "Tests upgrade of a data service from version 1.1 to " + UpgradeTestConstants.DATA_CURRENT_VERSION;
	}
    
    
    public String getName() {
        return "Data Service 1_1 to " 
            + UpgradeTestConstants.DATA_CURRENT_VERSION.replace(".", "_") 
            + " Upgrade Tests";
    }
    
    
    public boolean storySetUp() {
        this.testServiceInfo = new DataTestCaseInfo() {
            public String getServiceDirName() {
                return SERVICE_DIR_NAME;
            }


            public String getName() {
                return SERVICE_NAME;
            }


            public String getNamespace() {
                return SERVICE_NAMESPACE;
            }


            public String getPackageName() {
                return SERVICE_PACKAGE;
            }
        };
        return true;
    }
	

	protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
		// steps to unpack and upgrade the old service
		steps.add(new DeleteOldServiceStep(testServiceInfo));
		steps.add(new UnpackOldServiceStep(SERVICE_ZIP_NAME));
		steps.add(new UpgradeIntroduceServiceStep(testServiceInfo.getDir()));
		steps.add(new BuildUpgradedServiceStep(testServiceInfo.getDir()));
		
		return steps;
	}
    
    
    protected void storyTearDown() throws Throwable {
        Step destroyStep = new DeleteOldServiceStep(testServiceInfo);
        destroyStep.runStep();
    }


	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(UpgradeFrom1pt1Tests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
