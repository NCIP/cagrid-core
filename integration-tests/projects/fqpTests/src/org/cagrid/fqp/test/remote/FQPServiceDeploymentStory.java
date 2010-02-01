package org.cagrid.fqp.test.remote;

import gov.nih.nci.cagrid.fqp.common.FQPConstants;
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

import org.cagrid.fqp.execution.TargetDataServiceQueryBehavior;
import org.cagrid.fqp.test.common.ServiceContainerSource;
import org.cagrid.fqp.test.remote.steps.ChangeFqpServiceProperties;
import org.cagrid.fqp.test.remote.steps.ChangeJndiSweeperDelayStep;

/** 
 *  FQPServiceDeploymentStory
 *  Deploys the federated query service to a local service 
 *  container and gets it running
 * 
 * @author David Ervin
 * 
 * @created Jul 15, 2008 12:46:02 PM
 * @version $Id: FQPServiceDeploymentStory.java,v 1.11 2009-04-16 14:42:04 dervin Exp $ 
 */
public class FQPServiceDeploymentStory extends Story implements ServiceContainerSource {
    
    private File fqpServiceDirectory;
    private File transferServiceDirectory;
    
    private ServiceContainer fqpServiceContainer;
    private boolean secureDeployment;
    private boolean complete;
    private File tempFqpServiceDir;    
    
    public FQPServiceDeploymentStory(File fqpDir, File transferDir, boolean secureDeployment) {
        this.fqpServiceDirectory = fqpDir;
        this.transferServiceDirectory = transferDir;
        this.secureDeployment = secureDeployment;
        this.complete = false;
    }
    
    
    public String getName() {
        return (secureDeployment ? "Secure " : "") + "FQP Service Deployment" 
            + (transferServiceDirectory != null ? " With Transfer" : "");
    }
    

    public String getDescription() {
        return "Deploys the FQP service " + (transferServiceDirectory != null ? "with transfer" : "") 
            + " to a " + (secureDeployment ? "secure " : "") + "local service container and starts it";
    }
    
    
    public boolean storySetUp() {
        try {
            // must be tomcat container for transfer to work
            ServiceContainerType containerType = 
                secureDeployment ? ServiceContainerType.SECURE_TOMCAT_CONTAINER : ServiceContainerType.TOMCAT_CONTAINER;
            fqpServiceContainer = ServiceContainerFactory.createContainer(containerType);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    
        
    /**
     * Overridden to run tests, and on successful completion set 
     * the 'complete' flag to true so the service container can be returned
     */
    protected void runTest() throws Throwable {
        super.runTest();
        complete = true;
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        // unpack the container
        steps.add(new UnpackContainerStep(fqpServiceContainer));
        
        List<String> args = Arrays.asList(new String[] {
            "-Dno.deployment.validation=true", "-Dperform.index.service.registration=false"});
        
        // deploy transfer
        // copy Transfer to a temp dir
        File tempTransferServiceDir = new File("tmp/TempTransferService");
        System.out.println("Copying Transfer for pre-deployment to " + tempTransferServiceDir.getAbsolutePath());
        steps.add(new CopyServiceStep(transferServiceDirectory, tempTransferServiceDir));
        steps.add(new SetIndexRegistrationStep(tempTransferServiceDir.getAbsolutePath(), false));
        // deploy transfer
        steps.add(new DeployServiceStep(fqpServiceContainer, tempTransferServiceDir.getAbsolutePath(), args));
       
        // copy FQP to a temp dir
        this.tempFqpServiceDir = new File("tmp/Temp" + (secureDeployment ? "Secure" : "") + "FQP");
        System.out.println("Copying FQP for pre-deployment to " + tempFqpServiceDir.getAbsolutePath());
        steps.add(new CopyServiceStep(fqpServiceDirectory, tempFqpServiceDir));
        // configure the resource sweeper delay
        steps.add(new ChangeJndiSweeperDelayStep(tempFqpServiceDir, 2000));
        // change the deployment properties
        TargetDataServiceQueryBehavior defaultBehavior = FQPConstants.DEFAULT_TARGET_QUERY_BEHAVIOR;
        steps.add(new ChangeFqpServiceProperties(tempFqpServiceDir, 
            1, defaultBehavior.getTimeoutPerRetry().intValue(), 12, 5));
        steps.add(new SetIndexRegistrationStep(tempFqpServiceDir.getAbsolutePath(), false));
        // deploy FQP
        steps.add(new DeployServiceStep(fqpServiceContainer, tempFqpServiceDir.getAbsolutePath(), args));
        
        // start the container
        steps.add(new StartContainerStep(fqpServiceContainer));
        
        // url information step
        steps.add(new Step() {
            public void runStep() throws Throwable {
                System.out.println("FQP SERVICE URL SHOULD BE " 
                    + fqpServiceContainer.getServiceEPR("cagrid/FederatedQueryProcessor").getAddress().toString());
            }
        });
        return steps;
    }
    
    
    public ServiceContainer getServiceContainer() {
        if (fqpServiceContainer == null || !complete) {
            throw new IllegalStateException(
                "Deployment Story has not completed to create a working service container!");
        }
        return fqpServiceContainer;
    }
    
    
    public File getTempFqpServiceDir() {
        return tempFqpServiceDir;
    }
}
