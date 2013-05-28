/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.data.test.upgrades.from1pt4;

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
import java.util.Arrays;
import java.util.List;
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
 *  UpgradeFrom1pt4Tests
 *  Tests to upgrade a data service from 1.4 to current
 */
public class UpgradeFrom1pt4Tests extends BaseSystemTest {
    public static final String SERVICE_ZIP_NAME = "BasicDataService_1-4.zip";
    public static final String SERVICE_DIR_NAME = "BasicDataService_1-4";
    public static final String SERVICE_NAME = "BasicDataService";
    public static final String SERVICE_PACKAGE = "org.cagrid.test.data.basic14";
    public static final String SERVICE_NAMESPACE = "http://basic14.data.test.cagrid.org/BasicDataService";
    
	private DataTestCaseInfo testServiceInfo = null;
	private ServiceContainer container = null;
    
	public String getDescription() {
		return "Tests upgrade of a data service from version 1.4 to " + UpgradeTestConstants.getCurrentDataVersion();
	}
    
    
    public String getName() {
        return "Data Service 1_4 to " 
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
        try {
            this.container = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error creating service container: " + ex.getMessage());
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
		List<String> args = Arrays.asList(new String[] {
	            "-Dno.deployment.validation=true", "-Dperform.index.service.registration=false"});
        steps.add(new DeployServiceStep(container, testServiceInfo.getDir(), args));
        steps.add(new StartContainerStep(container));
        steps.add(new VerifyOperationsStep(container, testServiceInfo.getName(),
            false, false, false));
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
        Step destroyStep = new DeleteOldServiceStep(testServiceInfo);
        destroyStep.runStep();
    }


	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(UpgradeFrom1pt4Tests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
