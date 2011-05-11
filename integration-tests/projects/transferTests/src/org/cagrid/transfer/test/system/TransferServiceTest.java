package org.cagrid.transfer.test.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.CreateSkeletonStep;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSkeletonStep;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.SetIndexRegistrationStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

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
    private File transferServiceTemp = null;


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


    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        try {
            steps.add(new UnpackContainerStep(getContainer()));

            // deploy the transfer service to the container
            List<String> deploymentArgs = 
                Arrays.asList(new String[] {"-Dno.deployment.validation=true"});
            // turn off index service registration here
            steps.add(new CopyServiceStep(new File("../transfer"), transferServiceTemp));
            steps.add(new SetIndexRegistrationStep(transferServiceTemp.getAbsolutePath(), false));
            steps.add(new DeployServiceStep(getContainer(), transferServiceTemp.getAbsolutePath(), deploymentArgs));

            // create a service
            steps.add(new CreateSkeletonStep(tci, false));
            // add the transfer method
            steps.add(new AddCreateTransferMethodStep(tci,getContainer(), false));
            // add the implementation for the transfer method
            steps.add(new AddCreateTransferMethodImplStep(tci, false));
            if (getContainer() instanceof SecureContainer) {
                // if it's secure, copy in the proxy and CA
                steps.add(new CopyProxyStep((SecureContainer) getContainer(), tci));
                steps.add(new CopyCAStep((SecureContainer)getContainer(), tci));
            }
            steps.add(new SetIndexRegistrationStep(tci.getDir(), false));
            // deploy the service we just created into the container
            steps.add(new DeployServiceStep(getContainer(), tci.getDir()));
            // start up the container
            steps.add(new StartContainerStep(getContainer()));

            // invoke the service we created
            steps.add(new InvokeClientStep(getContainer(), tci));

            // shut down the container
            steps.add(new StopContainerStep(getContainer()));

            // add method and impl to the service for streaming transfer
            steps.add(new AddCreateStreamingTransferMethodStep(tci,getContainer(), false));
            steps.add(new AddCreateStreamingTransferMethodImplStep(tci, false));
            // deploy, start
            steps.add(new DeployServiceStep(getContainer(), tci.getDir()));
            steps.add(new StartContainerStep(getContainer()));

            // invoke
            steps.add(new InvokeClientStep(getContainer(), tci));
        } catch (Exception ex) {
            
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
        transferServiceTemp = File.createTempFile("TransferService", "temp");
        transferServiceTemp.delete();
        boolean created = transferServiceTemp.mkdirs();
        assertTrue("Did not create temp transfer service dir", created);
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
        try {
            Utils.deleteDir(transferServiceTemp);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Did not completely delete temp dir: " + transferServiceTemp + " (" + ex.getMessage() + ")");
        }
    }
}
