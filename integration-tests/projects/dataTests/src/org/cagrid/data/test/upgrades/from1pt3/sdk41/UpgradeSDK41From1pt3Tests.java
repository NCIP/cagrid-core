package org.cagrid.data.test.upgrades.from1pt3.sdk41;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;
import org.cagrid.data.test.system.BaseSystemTest;
import org.cagrid.data.test.system.ResyncAndBuildStep;
import org.cagrid.data.test.system.VerifyOperationsStep;
import org.cagrid.data.test.upgrades.UnpackOldServiceStep;
import org.cagrid.data.test.upgrades.UpgradeIntroduceServiceStep;
import org.cagrid.data.test.upgrades.UpgradeTestConstants;

/** 
 *  UpgradeSDK41From1pt3Tests
 *  Tests to upgrade a data service backed by caCORE SDK 4.1 from 1.3 to current
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Feb 20, 2007 
 * @version $Id: UpgradeSDKFrom1pt2Tests.java,v 1.2 2008-11-26 15:48:07 dervin Exp $ 
 */
public class UpgradeSDK41From1pt3Tests extends BaseSystemTest {
	public static final String SERVICE_ZIP_NAME = "DataServiceWithSdk41_1-3.zip";
    public static final String SERVICE_DIR_NAME = "DataServiceWithSdk41_1-3";
    public static final String SERVICE_NAME = "DataServceWithSdk41";
    public static final String SERVICE_PACKAGE = "org.cagrid.data.with.sdk41";
    public static final String SERVICE_NAMESPACE = "http://sdk41.with.data.cagrid.org/DataServceWithSdk41";
    
	private DataTestCaseInfo testServiceInfo = null;
	private ServiceContainer container = null;
    
	public String getDescription() {
		return "Tests upgrade of a data service backed by the SDK 4.1 from version 1.3 to " 
		    + UpgradeTestConstants.getCurrentDataVersion();
	}
    
    
    public String getName() {
        return "Data Service backed by the SDK 4_0 from 1_3 to " 
            + UpgradeTestConstants.getCurrentDataVersion().replace(".", "_") 
            + " Upgrade Tests";
    }
    
    
    public boolean storySetUp() {
        this.testServiceInfo =  new DataTestCaseInfo() {
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
        try {
            this.container = ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error setting up service container: " + ex.getMessage());
        }
        return true;
    }
	

	protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
		// steps to unpack and upgrade the old service
		steps.add(new DeleteOldServiceStep(testServiceInfo));
		steps.add(new UnpackOldServiceStep(SERVICE_ZIP_NAME));
		steps.add(new UpgradeIntroduceServiceStep(testServiceInfo.getDir()));
		steps.add(new ResyncAndBuildStep(testServiceInfo, getIntroduceBaseDir()));
	    // deploy the service, check out the CQL 2 related operations and metadata
        steps.add(new UnpackContainerStep(container));
        steps.add(new DeployServiceStep(container, testServiceInfo.getDir(), 
            Collections.singletonList("-Dno.deployment.validation=true")));
        steps.add(new StartContainerStep(container));
        // caCORE SDK 4.1 style upgrader should add CQL 2 support
        steps.add(new VerifyOperationsStep(container, testServiceInfo.getName(),
            true, false, false));
		return steps;
	}
    
    
    protected void storyTearDown() throws Throwable {
        if (container.isStarted()) {
            Step stopStep = new StopContainerStep(container);
            stopStep.runStep();
        }
        if (container.isUnpacked()) {
            Step deleteStep = new DestroyContainerStep(container);
            deleteStep.runStep();
        }
        Step deleteServiceStep = new DeleteOldServiceStep(testServiceInfo);
        deleteServiceStep.runStep();
    }
    

	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(UpgradeSDK41From1pt3Tests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
