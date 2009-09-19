package org.cagrid.data.test.system.bdt;

import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.SetIndexRegistrationStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;
import org.cagrid.data.test.creation.bdt.BDTDataServiceCreationTests;
import org.cagrid.data.test.system.AddBookstoreStep;
import org.cagrid.data.test.system.AddTestingJarToServiceStep;
import org.cagrid.data.test.system.BaseSystemTest;
import org.cagrid.data.test.system.RebuildServiceStep;
import org.cagrid.data.test.system.SetCqlValidationStep;
import org.cagrid.data.test.system.SetQueryProcessorStep;

/** 
 *  BDTDataServiceSystemTests
 *  System tests for BDT Data Service
 * 
 * @author David Ervin
 * 
 * @created Mar 14, 2007 2:19:42 PM
 * @version $Id: BDTDataServiceSystemTests.java,v 1.6 2009-04-15 15:20:53 dervin Exp $ 
 */
public class BDTDataServiceSystemTests extends BaseSystemTest {
    
    private ServiceContainer container;
    private DataTestCaseInfo testServiceInfo;
    
    public BDTDataServiceSystemTests() {
        super();
    }
    
	
	public String getDescription() {
		return "System tests for BDT Data Service";
	}
    
    
    public String getName() {
        return "BDT Data Service System Tests";
    }
	
	
	protected boolean storySetUp() {
        // get the testing service info
	    this.testServiceInfo = new BDTDataServiceCreationTests.TestBDTDataServiceInfo();
        
        // obtain a new container instance
        try {
            container = ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
        
		// verify the BDT service has been built
		File serviceDir = new File(testServiceInfo.getDir());
		assertTrue("BDT Data Service directory NOT FOUND", serviceDir.exists());
		File serviceModel = new File(serviceDir.getAbsolutePath() 
			+ File.separator + IntroduceConstants.INTRODUCE_XML_FILE);
		assertTrue("BDT Data Service directory does not appear to be an Introduce service", 
			serviceModel.exists());
        
        // unpack the container
        Step unpack = new UnpackContainerStep(container);
        try {
            unpack.runStep();
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
		return true;
	}


	protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
		// assumes the BDT service has been created already
        // Add the data tests jar to the service lib
        steps.add(new AddTestingJarToServiceStep(testServiceInfo));
		// Add the bookstore schema to the data service
		steps.add(new AddBookstoreStep(testServiceInfo));
		// change out query processor
		steps.add(new SetQueryProcessorStep(testServiceInfo.getDir()));
		// Rebuild the service to pick up the bookstore beans
		steps.add(new RebuildServiceStep(testServiceInfo, getIntroduceBaseDir()));
        // Turn on query validation, turn off model validation
        steps.add(new SetCqlValidationStep(testServiceInfo, true, false));
        // disable index service registration
        steps.add(new SetIndexRegistrationStep(testServiceInfo.getDir(), false));
		// deploy data service
		steps.add(new DeployServiceStep(container, testServiceInfo.getDir()));
		// start the container
		steps.add(new StartContainerStep(container));
		// test bdt data service
		steps.add(new InvokeBDTDataServiceStep(container, testServiceInfo.getName()));
		return steps;
	}
	
	
	protected void storyTearDown() throws Throwable {
		super.storyTearDown();
		// stop the container
		Step stopStep = new StopContainerStep(container);
		try {
			stopStep.runStep();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		// throw away the container
		Step destroyStep = new DestroyContainerStep(container);
		try {
			destroyStep.runStep();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
        // throw away service
        Step deleteServiceStep = new DeleteOldServiceStep(testServiceInfo);
        try {
            deleteServiceStep.runStep();
        } catch (Throwable th) {
            th.printStackTrace();
        }
	}
	

	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(
				BDTDataServiceSystemTests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
