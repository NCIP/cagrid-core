package org.cagrid.fqp.test.remote.secure;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.SetIndexRegistrationStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.cagrid.fqp.test.common.ServiceContainerSource;

/**
 * CDSDeploymentStory
 * Deploys the CDS service to a secure tomcat container
 * 
 * @author David
 */
public class CDSDeploymentStory extends Story implements ServiceContainerSource {

    private File cdsDir = null;
    private boolean complete = false;
    private ServiceContainer serviceContainer = null;
    
    public CDSDeploymentStory(File cdsDir) {
        this.cdsDir = cdsDir;
        this.complete = false;
    }
    
    
    public String getName() {
        return "CDS Deployment Story";
    }


    public String getDescription() {
        return "Deploys a CDS to a secure container";
    }
    
    
    /**
     * Overridden to run tests, and on successful completion set 
     * the 'complete' flag to true so the service container can be returned
     */
    protected void runTest() throws Throwable {
        super.runTest();
        complete = true;
    }
    
    
    public boolean storySetUp() {
        try {
            // must be tomcat container for transfer to work
            ServiceContainerType containerType = ServiceContainerType.SECURE_TOMCAT_CONTAINER;
            serviceContainer = ServiceContainerFactory.createContainer(containerType);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        File tempCdsDir = new File("tmp/TempCDS");
        steps.add(new UnpackContainerStep(serviceContainer));
        steps.add(new CopyServiceStep(cdsDir, tempCdsDir));
        List<String> args = Arrays.asList(new String[] {
            "-Dno.deployment.validation=true", "-Dperform.index.service.registration=false"});
        steps.add(new SetIndexRegistrationStep(tempCdsDir.getAbsolutePath(), false));
        steps.add(new DeployServiceStep(serviceContainer, tempCdsDir.getAbsolutePath(), args));
        steps.add(new StartContainerStep(serviceContainer));
        return steps;
    }


    public ServiceContainer getServiceContainer() {
        if (serviceContainer == null || !complete) {
            throw new IllegalStateException(
                "Deployment Story has not completed to create a working service container!");
        }
        return serviceContainer;
    }
}
