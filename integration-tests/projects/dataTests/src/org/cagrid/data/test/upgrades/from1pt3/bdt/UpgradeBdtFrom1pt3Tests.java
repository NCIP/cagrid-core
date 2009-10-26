package org.cagrid.data.test.upgrades.from1pt3.bdt;

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
 *  UpgradeBdtFrom1pt3Tests
 *  Tests to upgrade a BDT data service from 1.3 to current
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Feb 20, 2007 
 * @version $Id: UpgradeEnumerationFrom1pt3Tests.java,v 1.1 2008-10-28 15:55:16 dervin Exp $ 
 */
public class UpgradeBdtFrom1pt3Tests extends Story {
    public static final String SERVICE_ZIP_NAME = "DataServiceWithBdt_1-3.zip";
    public static final String SERVICE_DIR_NAME = "DataServiceWithBdt_1-3";
    public static final String SERVICE_NAME = "DataServiceWithBdt";
    public static final String SERVICE_PACKAGE = "org.cagrid.test.data.with.bdt";
    public static final String SERVICE_NAMESPACE = "http://bdt.with.data.test.cagrid.org/DataServiceWithBdt";
    
    private DataTestCaseInfo testServiceInfo = null;
	
	public String getDescription() {
		return "Tests upgrade of a BDT data service from version 1.3 to " + UpgradeTestConstants.getCurrentDataVersion();
	}
    
    
    public String getName() {
        return "Data Service With BDT 1_3 to " 
            + UpgradeTestConstants.getCurrentDataVersion().replace(".", "_") 
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
		steps.add(new VerifyBdtRemovedStep(testServiceInfo));
		return steps;
	}
    
    
    protected void storyTearDown() throws Throwable {
        Step deleteServiceStep = new DeleteOldServiceStep(testServiceInfo);
        deleteServiceStep.runStep();
    }


	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(UpgradeBdtFrom1pt3Tests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
