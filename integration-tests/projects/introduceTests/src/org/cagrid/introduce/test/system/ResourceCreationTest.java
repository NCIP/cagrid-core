package org.cagrid.introduce.test.system;

import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoBaseResource;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoLifetimeResource;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoMain;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoNotificationResource;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoPersistentResource;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoSingletonResource;
import gov.nih.nci.cagrid.introduce.test.steps.AddBookResourcePropertyStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddBookstoreSchemaStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddServiceContextStep;
import gov.nih.nci.cagrid.introduce.test.steps.CreateSkeletonStep;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSkeletonStep;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


public class ResourceCreationTest extends Story {
    private TestCaseInfo tci1;
    private TestCaseInfo tci2;
    private TestCaseInfo tci3;
    private TestCaseInfo tci4;
    private TestCaseInfo tci5;
    private TestCaseInfo tci6;

    private ServiceContainer container;
    
    public ResourceCreationTest() {
        this.setName("Introduce Resource Creation System Test");
    }


    public String getName() {
        return "Introduce Resource Creation System Test";
    }


    public String getDescription() {
        return "Testing the Introduce code generation tools";
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();

        try {
            steps.add(new CreateSkeletonStep(tci1, true));
            steps.add(new AddBookstoreSchemaStep(tci1,false));
            steps.add(new AddServiceContextStep(tci2, false));
            steps.add(new AddServiceContextStep(tci3, false));
            steps.add(new AddServiceContextStep(tci4, false));
            steps.add(new AddServiceContextStep(tci5, false));
            steps.add(new AddServiceContextStep(tci6, false));
            steps.add(new AddBookResourcePropertyStep(tci1, false));
            steps.add(new AddBookResourcePropertyStep(tci2, false));
            steps.add(new AddBookResourcePropertyStep(tci3, false));
            steps.add(new AddBookResourcePropertyStep(tci4, false));
            steps.add(new AddBookResourcePropertyStep(tci5, false));
            steps.add(new AddBookResourcePropertyStep(tci6, true));
            steps.add(new DeployServiceStep(container, tci1.getDir()));
            steps.add(new StartContainerStep(container));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        // init the service container
        try {
            container = ServiceContainerFactory.createContainer(
                ServiceContainerType.GLOBUS_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
        tci1 = new TestCaseInfoMain();
        tci2 = new TestCaseInfoLifetimeResource();
        tci3 = new TestCaseInfoBaseResource();
        tci4 = new TestCaseInfoSingletonResource();
        tci5 = new TestCaseInfoNotificationResource();
        tci6 = new TestCaseInfoPersistentResource();
        

        Step step2 = new UnpackContainerStep(container);
        try {
            step2.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci1);
        try {
            step1.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }


    protected void storyTearDown() throws Throwable {
        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci1);
        try {
            step1.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        StopContainerStep step2 = new StopContainerStep(container);
        try {
            step2.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        DestroyContainerStep step3 = new DestroyContainerStep(container);
        try {
            step3.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(ResourceCreationTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
