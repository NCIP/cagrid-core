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

import java.io.File;
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
    private File auditorLogFile;
    
    public PlainDataServiceSystemTests() {
        super();
        setName("Plain Data Service System Test");
    }
    
    
    public String getName() {
        return "Plain Data Service System Test";
    }


    public String getDescription() {
        return "System test to create a data service, add and remove CQL and CQL 2 query processors, " +
                "deploy it, invoke it, and check its metadata.";
    }
    
    
    protected boolean storySetUp() {
        // init the log file
        try {
            auditorLogFile = File.createTempFile("dataServiceAuditing", ".log").getCanonicalFile();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error identifying auditor log file: " + ex.getMessage());
        }
        
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
        
        // Turn on and configure auditing
        steps.add(new AddFileSystemAuditorStep(info.getDir(), auditorLogFile.getAbsolutePath()));
        // Add the bookstore schema to the data service
        steps.add(new AddBookstoreStep(info));
        // copy the testing jars into the service
        steps.add(new AddTestingJarToServiceStep(info));
        // set the CQL 1 query processor
        steps.add(new SetQueryProcessorStep(info.getDir()));
        // re-sync and build the service
        steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // turn off index service registration
        steps.add(new SetIndexRegistrationStep(info.getDir(), false));
        // enable CQL structure validation, disable model validation
        steps.add(new SetCqlValidationStep(info, true, false));
        // deploy the data service
        steps.add(new DeployServiceStep(container, info.getDir(), Collections.singletonList("-Dno.deployment.validation=true")));
        // start the container
        steps.add(new StartContainerStep(container));
        // check the CQL 2 support metadata (should not be supported)
        steps.add(new CheckCql2QueryLanguageSupportResourcePropertyStep(container, info));
        // invoke the data service using CQL 1
        steps.add(new InvokeDataServiceStep(container, info.getName()));
        // verify the audit log
        steps.add(new VerifyAuditLogStep(auditorLogFile.getAbsolutePath()));
        
        // stop the container so the service can be re-deployed later
        steps.add(new StopContainerStep(container));
        
        // add the Testing CQL 2 query processor to the service
        steps.add(new SetCql2QueryProcessorStep(info.getDir()));
        // rebuild again
        steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // enable CQL structure validation, disable model validation
        steps.add(new SetCqlValidationStep(info, true, false));
        // deploy the service again
        steps.add(new DeployServiceStep(container, info.getDir(), Collections.singletonList("-Dno.deployment.validation=true")));
        // start the container
        steps.add(new StartContainerStep(container));
        // check the CQL 2 support metadata again (should be supported now)
        steps.add(new CheckCql2QueryLanguageSupportResourcePropertyStep(container, info, 
            true, TestingCQL2QueryProcessor.getTestingSupportedExtensionsBean()));
        // invoke both CQL and CQL 2 query methods, using the native query processor for each
        steps.add(new InvokeCql2DataServiceStep(container, info.getName()));
        steps.add(new InvokeDataServiceStep(container, info.getName()));
        
        // stop the container
        steps.add(new StopContainerStep(container));
        
        // turn off the CQL 1 query processor
        steps.add(new DisableCql1QueryProcessorStep(info.getDir()));
        // rebuild the service
        steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // enable CQL structure validation, disable model validation
        steps.add(new SetCqlValidationStep(info, true, false));
        // re-deploy the service
        steps.add(new DeployServiceStep(container, info.getDir(), Collections.singletonList("-Dno.deployment.validation=true")));
        // start the container up again
        steps.add(new StartContainerStep(container));
        // check the CQL 2 support metadata (should still be supported)
        steps.add(new CheckCql2QueryLanguageSupportResourcePropertyStep(container, info, 
            true, TestingCQL2QueryProcessor.getTestingSupportedExtensionsBean()));
        // invoke both CQL and CQL 2 query methods, letting the data service translate CQL 1 to 2
        steps.add(new InvokeCql2DataServiceStep(container, info.getName()));
        steps.add(new InvokeDataServiceStep(container, info.getName()));
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
