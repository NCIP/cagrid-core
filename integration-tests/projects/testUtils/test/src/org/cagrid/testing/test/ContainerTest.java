package org.cagrid.testing.test;


import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.util.Vector;



public class ContainerTest extends ServiceStoryBase {


    public ContainerTest(ServiceContainer container) {
       super(container);
    }
    
    
    public String getName() {
        return getDescription();
    }


    public String getDescription() {
        String desc = "Service Container Test";
        if (getContainer().getProperties().isSecure()){
            desc = "Secure " + desc;
        }
        return getContainer().getClass().getSimpleName() + " " + desc;
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new UnpackContainerStep(getContainer()));
        steps.add(new StartContainerStep(getContainer()));
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        return true;
    }


    protected void storyTearDown() throws Throwable {
     
        StopContainerStep step2 = new StopContainerStep(getContainer());
        try {
            step2.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        DestroyContainerStep step3 = new DestroyContainerStep(getContainer());
        try {
            step3.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
