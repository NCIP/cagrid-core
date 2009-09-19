package org.cagrid.introduce.test.system;

import gov.nih.nci.cagrid.introduce.test.TestCaseInfoMain;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoPersistentNotificationResource;
import gov.nih.nci.cagrid.introduce.test.steps.AddBookResourcePropertyStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddBookstoreSchemaStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddFactoryMethodStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddGetBookMethodStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddPersistenceNotificationResourceMethodImplStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddServiceContextStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddSetBookMethodStep;
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
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import org.apache.log4j.PropertyConfigurator;
import org.cagrid.introduce.test.system.steps.InvokeClientStep;


public class PersistenceNotificationResourcesTest extends Story {

    private ServiceContainer container;
    private TestCaseInfoMain tci;
    private TestCaseInfoPersistentNotificationResource pntci;


    public PersistenceNotificationResourcesTest() {
        this.setName("Introduce Persistence Notification System Test");
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator
            + "log4j.properties");
    }


    public String getName() {
        return "Introduce Persistence Notification System Test";
    }


    public String getDescription() {
        return "Testing the Introduce Persistence Notification support";
    }


    @Override
    protected Vector steps() {

        Vector steps = new Vector();
        try {
            steps.add(new UnpackContainerStep(container));
            steps.add(new CreateSkeletonStep(tci, false));
            steps.add(new AddServiceContextStep(pntci, true));
            steps.add(new AddFactoryMethodStep(tci, pntci, true));

            steps.add(new AddBookstoreSchemaStep(tci, false));
            steps.add(new AddBookResourcePropertyStep(pntci, false));
            steps.add(new AddSetBookMethodStep(pntci, false));
            steps.add(new AddGetBookMethodStep(pntci, false));
            steps.add(new AddPersistenceNotificationResourceMethodImplStep(tci, pntci, true));

            steps.add(new DeployServiceStep(container, tci.getDir()));
            steps.add(new StartContainerStep(container));
            steps.add(new InvokeClientStep(container, tci));

            steps.add(new StopContainerStep(container));

//            steps.add(new AddGetPersistenceNotificationResourceMethodImplStep(tci, pntci, true));
//            steps.add(new StartContainerStep(container));
//            steps.add(new InvokeClientStep(container, tci));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        // init the container
        try {
            container = ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }

        tci = new TestCaseInfoMain();
        pntci = new TestCaseInfoPersistentNotificationResource();

        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci);
        try {
            step1.runStep();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    protected void storyTearDown() throws Throwable {
        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci);
        try {
            step1.runStep();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        StopContainerStep step2 = new StopContainerStep(container);
        try {
            step2.runStep();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DestroyContainerStep step3 = new DestroyContainerStep(container);
        try {
            step3.runStep();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
