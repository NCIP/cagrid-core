package org.cagrid.data.test.system.enumeration;

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

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;
import org.cagrid.data.test.creation.enumeration.CreateEnumerationTests;
import org.cagrid.data.test.system.AddBookstoreStep;
import org.cagrid.data.test.system.AddTestingJarToServiceStep;
import org.cagrid.data.test.system.BaseSystemTest;
import org.cagrid.data.test.system.ResyncAndBuildStep;
import org.cagrid.data.test.system.SetCqlValidationStep;
import org.cagrid.data.test.system.SetQueryProcessorStep;


/**
 * EnumerationSystemTests 
 * Story for WS-Enumeration data service system tests
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 7, 2006
 * @version $Id: EnumerationSystemTests.java,v 1.1 2006/12/18 14:48:47 dervin
 *          Exp $
 */
public class EnumerationSystemTests extends BaseSystemTest {
    
    private DataTestCaseInfo info;
    private ServiceContainer container;

	public EnumerationSystemTests() {
        super();
		this.setName("Enumeration Data Service System Tests");
	}
    
    
    public String getName() {
        return "Enumeration Data Service System Tests";
    }


	public String getDescription() {
		return "Testing the data service infrastructure";
	}


	protected boolean storySetUp() {
        info = new CreateEnumerationTests.TestEnumerationDataServiceInfo();
        // obtain a new container instance
        try {
            container = ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
        
		// unpack the service container
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
		// an enumeration supporting data service is presumed to have been
		// created by a previous testing process
        // Add the data tests jar to the service lib
        steps.add(new AddTestingJarToServiceStep(info));
		// Add the bookstore schema to the data service
		steps.add(new AddBookstoreStep(info));
		// change out query processor
		steps.add(new SetQueryProcessorStep(info.getDir()));
		// Rebuild the service to pick up the bookstore beans
		steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // Turn on query validation, turn off model validation
        steps.add(new SetCqlValidationStep(info, true, false));
        // disable index service registration
        steps.add(new SetIndexRegistrationStep(info.getDir(), false));
		// deploy data service
		steps.add(new DeployServiceStep(container, info.getDir()));
		// start container
		steps.add(new StartContainerStep(container));
		// test data service
		steps.add(new InvokeEnumerationDataServiceStep(container, info.getName()));
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
        Step deleteServiceStep = new DeleteOldServiceStep(info);
        try {
            deleteServiceStep.runStep();
        } catch (Throwable th) {
            th.printStackTrace();
        }
	}


	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(EnumerationSystemTests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
