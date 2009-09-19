package org.cagrid.introduce.test.system;

import gov.nih.nci.cagrid.introduce.test.NotificationTestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.CreateSkeletonStep;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSkeletonStep;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
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
import org.cagrid.introduce.test.system.steps.AddSecurityMethodsImplStep;
import org.cagrid.introduce.test.system.steps.AddSecurityMethodsStep;
import org.cagrid.introduce.test.system.steps.CopyCAStep;
import org.cagrid.introduce.test.system.steps.CopyProxyStep;
import org.cagrid.introduce.test.system.steps.InvokeClientStep;
import org.cagrid.introduce.test.system.tci.SecurityTestCaseInfo;


public class SecurityConfigurationTest extends Story {
    
    private ServiceContainer container;
    private TestCaseInfo tci;

    public SecurityConfigurationTest() {
        this.setName("Introduce Security System Test");
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator
            + "log4j.properties");
    }


    public String getName() {
        return "Introduce Security System Test";
    }


    public String getDescription() {
        return "Testing the Introduce Security support";
    }


    @Override
    protected Vector steps() {

        Vector steps = new Vector();
        try {
            steps.add(new UnpackContainerStep(container));
            steps.add(new CreateSkeletonStep(new SecurityTestCaseInfo(),false));
            steps.add(new CopyProxyStep((SecureContainer) container, tci));
            steps.add(new CopyCAStep((SecureContainer) container, tci));
            steps.add(new AddSecurityMethodsStep(tci,true));
            steps.add(new AddSecurityMethodsImplStep(tci,true));
            
            steps.add(new DeployServiceStep(container,tci.getDir()));
            
            steps.add(new StartContainerStep(container));
            
            steps.add(new InvokeClientStep(container,tci));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return steps;
    }
    

    protected boolean storySetUp() throws Throwable {
        // init the container
        try {
            tci = new SecurityTestCaseInfo();
            container = ServiceContainerFactory.createContainer(
                ServiceContainerType.SECURE_TOMCAT_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
        
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
