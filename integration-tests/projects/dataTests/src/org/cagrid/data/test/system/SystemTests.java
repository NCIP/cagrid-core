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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.creation.CreationTests;
import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;


/**
 * SystemTests 
 * Story for data service system tests
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 7, 2006
 * @version $Id: SystemTests.java,v 1.6 2009-04-15 15:20:53 dervin Exp $
 */
public class SystemTests extends BaseSystemTest {
    
    private File auditorLogFile = null;
    
    private DataTestCaseInfo info;
    private ServiceContainer container;
    
    
    public SystemTests() {
        super();
        this.setName("Data Service System Tests");
    }
    
    
    public String getName() {
        return "Data Service System Tests";
    }


    public String getDescription() {
        return "Testing the data service infrastructure";
    }


    protected boolean storySetUp() {
        // init the log file
        try {
            auditorLogFile = new File("./dataServiceAuditing.log").getCanonicalFile();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error identifying auditor log file: " + ex.getMessage());
        }
    
        // init the test info
        info = new CreationTests.TestDataServiceInfo();
        
        // initialize the service container instance
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


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        // data service presumed to have been created
        // by the data service creation tests
        // Add the data tests jar to the service lib
        steps.add(new AddTestingJarToServiceStep(info));
        // Add the bookstore schema to the data service
        steps.add(new AddBookstoreStep(info));
        // change out query processor
        steps.add(new SetQueryProcessorStep(info.getDir()));
        // Turn on and configure auditing
        steps.add(new AddFileSystemAuditorStep(info.getDir(), auditorLogFile.getAbsolutePath()));
        // Rebuild the service to pick up the bookstore beans
        steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // Enable CQL validation, disable model validation
        steps.add(new SetCqlValidationStep(info, true, false));
        // turn off index service registration
        steps.add(new SetIndexRegistrationStep(info.getDir(), false));
        // deploy data service
        steps.add(new DeployServiceStep(container, info.getDir()));
        // start globus
        steps.add(new StartContainerStep(container));
        // test data service
        steps.add(new InvokeDataServiceStep(container, info.getName()));
        // verify the audit log
        steps.add(new VerifyAuditLogStep(auditorLogFile.getAbsolutePath()));
        return steps;
    }


    protected void storyTearDown() throws Throwable {
        super.storyTearDown();
        List<Throwable> exceptions = new ArrayList<Throwable>();
        // stop globus
        Step stopStep = new StopContainerStep(container);
        try {
            stopStep.runStep();
        } catch (Throwable ex) {
            exceptions.add(ex);
        }
        // throw away auditor log
        if (auditorLogFile.exists()) {
            auditorLogFile.deleteOnExit();
        }
        // throw away globus
        Step destroyStep = new DestroyContainerStep(container);
        try {
            destroyStep.runStep();
        } catch (Throwable ex) {
            exceptions.add(ex);
        }
        // Delete the old service
        Step deleteServiceStep = new DeleteOldServiceStep(info);
        try {
            deleteServiceStep.runStep();
        } catch (Throwable th) {
            exceptions.add(th);
        }
        
        // check on exceptions
        if (exceptions.size() != 0) {
            // uh oh
            for (Throwable th : exceptions) {
                System.err.println("EXCEPTION THROWN DURING TEAR DOWN:");
                th.printStackTrace();
            }
            throw new Exception("Error during tear down, see logs");
        }
    }


    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SystemTests.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
