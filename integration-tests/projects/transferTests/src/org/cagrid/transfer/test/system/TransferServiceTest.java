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
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.io.IOException;
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


public abstract class TransferServiceTest extends Story {

    private TestCaseInfo tci = new TransferTestCaseInfo();
    private ServiceContainer container = null;
    

    public TransferServiceTest() {
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator + "log4j.properties");
    }


    public String getName() {
        return getDescription();
    }


    public String getDescription() {
        String description = "Transfer Service Test";
        if (streamingTest()) {
            description = "Streaming " + description;
        }
        if (getContainer().getProperties().isSecure()) {
            description = "Secure " + description; 
        }
        return description;
    }
    
    
    private synchronized ServiceContainer getContainer() {
        if (container == null) {
            try {
                container = ServiceContainerFactory.createContainer(secureTest() ? 
                    ServiceContainerType.SECURE_TOMCAT_6_CONTAINER : ServiceContainerType.TOMCAT_6_CONTAINER);
            } catch (IOException ex) {
                ex.printStackTrace();
                fail("Error creating service container: " + ex.getMessage());
            }
        }
        return container;
    }
    
    
    protected abstract boolean streamingTest();
    
    protected abstract boolean secureTest();


    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        try {
            steps.add(new UnpackContainerStep(getContainer()));
            List<String> deploymentArgs = Arrays.asList(new String[]{"-Dno.deployment.validation=true"});
            steps.add(new DeployServiceStep(getContainer(), "../transfer", deploymentArgs));

            steps.add(new CreateSkeletonStep(tci, false));
            
            steps.add(new AddCreateTransferMethodStep(tci, getContainer(), false));
            steps.add(new AddCreateTransferMethodImplStep(tci, false));
            
            if (streamingTest()) {
                steps.add(new AddCreateStreamingTransferMethodStep(tci, getContainer(), false));
                steps.add(new AddCreateStreamingTransferMethodImplStep(tci, false));
            }
            
            if (getContainer().getProperties().isSecure()) {
                steps.add(new CopyProxyStep((SecureContainer) getContainer(), tci));
                steps.add(new CopyCAStep((SecureContainer) getContainer(), tci));
            }
            
            steps.add(new DeployServiceStep(getContainer(), tci.getDir()));
            steps.add(new StartContainerStep(getContainer()));
            
            steps.add(new InvokeClientStep(getContainer(), tci));

            steps.add(new StopContainerStep(getContainer()));
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
