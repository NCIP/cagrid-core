package org.cagrid.data.test.system.transfer;

import gov.nih.nci.cagrid.common.Utils;
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
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.data.test.creation.DeleteOldServiceStep;
import org.cagrid.data.test.creation.transfer.CreateTransferTests;
import org.cagrid.data.test.system.AddBookstoreStep;
import org.cagrid.data.test.system.AddTestingJarToServiceStep;
import org.cagrid.data.test.system.BaseSystemTest;
import org.cagrid.data.test.system.ResyncAndBuildStep;
import org.cagrid.data.test.system.SetCqlValidationStep;
import org.cagrid.data.test.system.SetQueryProcessorStep;
import org.cagrid.data.test.system.VerifyOperationsStep;
import org.junit.Assert;


/**
 * TransferSystemTests 
 * Story for caGrid Transfer data service system tests
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Nov 7, 2006
 * @version $Id: TransferSystemTests.java,v 1.1 2006/12/18 14:48:47 dervin
 *          Exp $
 */
public class TransferSystemTests extends BaseSystemTest {
    
    public static final String TRANSFER_SERVICE_DIR_PROPERTY = "transfer.service.dir";
    
    private DataTestCaseInfo info;
    private File tempTransferDir;
    private ServiceContainer container;

	public TransferSystemTests() {
        super();
		this.setName("Transfer Data Service System Tests");
	}
    
    
    public String getName() {
        return "Transfer Data Service System Tests";
    }


	public String getDescription() {
		return "Testing the data service infrastructure with transfer";
	}


	protected boolean storySetUp() {
        info = new CreateTransferTests.TestTransferDataServiceInfo();
        // obtain a new container instance
        try {
            // must be tomcat for transfer to work right
            container = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
        
		// unpack the service container
        Step unpack = new UnpackContainerStep(container);
        try {
            unpack.runStep();
        } catch (Throwable th) {
            th.printStackTrace();
            return false;
        }
        
        // copy transfer to a temp dir
        tempTransferDir = new File("tmp/TempTransferServiceDir");
        File originalTransferDir = getTransferDir();
        try {
            new CopyServiceStep(originalTransferDir, tempTransferDir).runStep();
        } catch (Throwable ex) {
            ex.printStackTrace();
            fail("Failed to copy transfer service: " + ex.getMessage());
        }
		return true;
	}


	protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
		// a transfer supporting data service is presumed to have been
		// created by a previous testing process
        // Add the data tests jar to the service lib
        steps.add(new AddTestingJarToServiceStep(info));
		// Add the bookstore schema to the data service
		steps.add(new AddBookstoreStep(info));
		// change out query processor
		steps.add(new SetQueryProcessorStep(info.getDir()));
		// Rebuild the service to pick up the bookstore beans
		steps.add(new ResyncAndBuildStep(info, getIntroduceBaseDir()));
        // Turn on query validation, turn off model validation
        steps.add(new SetCqlValidationStep(info, true, false));
        // disable index service registration
        steps.add(new SetIndexRegistrationStep(info.getDir(), false));
        // deploy the data service
        List<String> args = Arrays.asList(new String[] {
            "-Dno.deployment.validation=true", "-Dperform.index.service.registration=false"});
		steps.add(new DeployServiceStep(container, info.getDir(), args));
        // disable index service registration for the transfer service
        steps.add(new SetIndexRegistrationStep(tempTransferDir.getAbsolutePath(), false));
        // deploy the transfer service
        steps.add(new DeployServiceStep(container, tempTransferDir.getAbsolutePath(), args));
		// start container
		steps.add(new StartContainerStep(container));
		// verify the operations we expect
        steps.add(new VerifyOperationsStep(container, info.getName(),
            false, false, true));
		// test data service
		steps.add(new InvokeTransferDataServiceStep(container, info.getName()));
		return steps;
	}


	protected void storyTearDown() throws Throwable {
		super.storyTearDown();
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
        // throw away the data service
        Step deleteServiceStep = new DeleteOldServiceStep(info);
        try {
            deleteServiceStep.runStep();
        } catch (Throwable th) {
            th.printStackTrace();
        }
        // throw away the temp transfer dir
        try {
            Utils.deleteDir(tempTransferDir);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
    
    
    private File getTransferDir() {
        String value = System.getProperty(TRANSFER_SERVICE_DIR_PROPERTY);
        Assert.assertNotNull("System property " + TRANSFER_SERVICE_DIR_PROPERTY + " was not set!", value);
        File dir = new File(value);
        return dir;
    }


	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(TransferSystemTests.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
