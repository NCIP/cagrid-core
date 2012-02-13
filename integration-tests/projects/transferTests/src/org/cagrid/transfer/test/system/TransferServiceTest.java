package org.cagrid.transfer.test.system;

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
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.PropertyConfigurator;
import org.cagrid.transfer.test.system.steps.AddCreateStreamingTransferMethodImplStep;
import org.cagrid.transfer.test.system.steps.AddCreateStreamingTransferMethodStep;
import org.cagrid.transfer.test.system.steps.AddCreateTransferMethodImplStep;
import org.cagrid.transfer.test.system.steps.AddCreateTransferMethodStep;
import org.cagrid.transfer.test.system.steps.CopyCAStep;
import org.cagrid.transfer.test.system.steps.CopyProxyStep;
import org.cagrid.transfer.test.system.steps.InvokeClientStep;


public class TransferServiceTest extends ServiceStoryBase {

    private TestCaseInfo tci = new TransferTestCaseInfo();


    public TransferServiceTest(ServiceContainer container) {
       super(container);
       PropertyConfigurator.configure("." + File.separator + "conf" + File.separator + "log4j.properties");
    }
    
    public TransferServiceTest() {
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator + "introduce" + File.separator
            + "log4j.properties");
        // init the container
        try {
            this.setContainer(ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
    }


    public String getName() {
        return getDescription();
    }

    public String getDescription() {
        if(getContainer().getProperties().isSecure()){
            return "Secure Transfer Service Test";
        }
        return "Transfer Service Test";
    }


    protected Vector steps() {
        Vector steps = new Vector();
        try {
            steps.add(new UnpackContainerStep(getContainer()));
            List<String> deploymentArgs = 
                Arrays.asList(new String[] {"-Dno.deployment.validation=true"});
            steps.add(new DeployServiceStep(getContainer(), "../transfer", deploymentArgs));

            steps.add(new CreateSkeletonStep(tci, false));
            steps.add(new AddCreateTransferMethodStep(tci,getContainer(), false));
            steps.add(new AddCreateTransferMethodImplStep(tci, false));
            if (getContainer() instanceof SecureContainer) {
                steps.add(new CopyProxyStep((SecureContainer) getContainer(), tci));
                steps.add(new CopyCAStep((SecureContainer)getContainer(), tci));
            }
            steps.add(new DeployServiceStep(getContainer(), tci.getDir()));
            steps.add(new StartContainerStep(getContainer()));

            steps.add(new InvokeClientStep(getContainer(), tci));
            
            steps.add(new StopContainerStep(getContainer()));
            
            steps.add(new AddCreateStreamingTransferMethodStep(tci,getContainer(), false));
            steps.add(new AddCreateStreamingTransferMethodImplStep(tci, false));
            steps.add(new DeployServiceStep(getContainer(), tci.getDir()));
            steps.add(new StartContainerStep(getContainer()));

            steps.add(new InvokeClientStep(getContainer(), tci));
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        

        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci);
        try {
            step1.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }


    protected void storyTearDown() throws Throwable {
        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci);
        try {
            step1.runStep();
        } catch (Throwable e) {
            e.printStackTrace();
        }
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
