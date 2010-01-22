package org.cagrid.data.test.system;

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

import java.util.Collections;
import java.util.Vector;

import org.cagrid.data.test.creation.CreationTests;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  PlainDataServiceSystemTests
 *  System test to just create a data service, deploy it, 
 *  and make sure it can start up.
 * 
 * @author David Ervin
 * 
 * @created Sep 28, 2007 12:22:29 PM
 * @version $Id: PlainDataServiceSystemTests.java,v 1.2 2009-04-15 15:20:53 dervin Exp $ 
 */
public class PlainDataServiceSystemTests extends BaseSystemTest {
    
    // because of a Haste weirdness, can't have = null on the end here
    private ServiceContainer container;
    
    public PlainDataServiceSystemTests() {
        super();
        setName("Plain Data Service System Test");
    }
    
    
    public String getName() {
        return "Plain Data Service System Test";
    }


    public String getDescription() {
        return "System test to just create a data service, deploy it, " +
                "and make sure it can start up.";
    }
    
    
    protected boolean storySetUp() {
        // instantiate a new container instance
        try {
            container = ServiceContainerFactory.createContainer(ServiceContainerType.GLOBUS_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
        
        // set up a clean, temporary service container
        Step step = new UnpackContainerStep(container);
        try {
            step.runStep();
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
        return true;
    }


    protected Vector<?> steps() {
        DataTestCaseInfo info = new CreationTests.PlainDataServiceInfo();
        Vector<Step> steps = new Vector<Step>();
        // data service presumed to have been created
        // by the data service creation tests
        // Rebuild the service
        steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // turn off index registration
        steps.add(new SetIndexRegistrationStep(info.getDir(), false));
        // deploy data service
        steps.add(new DeployServiceStep(container, info.getDir(), Collections.singletonList("-Dno.deployment.validation=true")));
        //  start the container
        steps.add(new StartContainerStep(container));
        // check the CQL 2 support metadata
        steps.add(new CheckCql2QueryLanguageSupportResourcePropertyStep(container, info));
        // stop globus so the service can be redeployed
        steps.add(new StopContainerStep(container));
        // add the Testing CQL 2 query processor to the service
        steps.add(new AddTestingJarToServiceStep(info));
        steps.add(new SetCql2QueryProcessorStep(info.getDir()));
        steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // deploy the service again
        steps.add(new DeployServiceStep(container, info.getDir(), Collections.singletonList("-Dno.deployment.validation=true")));
        // start the container
        steps.add(new StartContainerStep(container));
        // check the CQL 2 support metadata again
        steps.add(new CheckCql2QueryLanguageSupportResourcePropertyStep(container, info, 
            true, TestingCQL2QueryProcessor.getTestingSupportedExtensionsBean()));
        return steps;
    }
    
    
    protected void storyTearDown() throws Throwable {
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
    }
}
