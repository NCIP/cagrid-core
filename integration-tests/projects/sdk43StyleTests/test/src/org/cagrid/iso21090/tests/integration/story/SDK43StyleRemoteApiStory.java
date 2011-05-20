package org.cagrid.iso21090.tests.integration.story;

import gov.nih.nci.cagrid.common.Utils;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.iso21090.tests.integration.SDK43ServiceStyleSystemTestConstants;
import org.cagrid.iso21090.tests.integration.steps.CreateDataServiceStep;
import org.cagrid.iso21090.tests.integration.steps.DeployExampleProjectStep;
import org.cagrid.iso21090.tests.integration.steps.FixSdkWarStep;
import org.cagrid.iso21090.tests.integration.steps.InvokeCql2DataServiceStep;
import org.cagrid.iso21090.tests.integration.steps.InvokeDataServiceStep;

public class SDK43StyleRemoteApiStory extends Story {
    
    private DataTestCaseInfo testInfo = null;
    private ServiceContainer dataServiceContainer = null;
    private ServiceContainer sdkApplicationServiceContainer = null;

    public SDK43StyleRemoteApiStory() {
        super();
    }


    public String getDescription() {
        return "Creates an SDK 4.3 with ISO types style data service, configures it to use the remote API, deploys and invokes it.";
    }
    
    
    public String getName() {
        return "SDK 4_3 with ISO types Style data service using remote API creation and invocation test";
    }
    
    
    public boolean storySetUp() throws Throwable {
        ServiceContainerFactory.setMaxContainerHeapSizeMB(256);
        testInfo = SDK43ServiceStyleSystemTestConstants.getTestServiceInfo();
        dataServiceContainer = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_6_CONTAINER);
        sdkApplicationServiceContainer = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_6_CONTAINER);
        File serviceDir = new File(testInfo.getDir());
        serviceDir.mkdirs();
        
        return dataServiceContainer != null && serviceDir.exists() && serviceDir.isDirectory();
    }


    protected Vector<?> steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new UnpackContainerStep(sdkApplicationServiceContainer));
        steps.add(new FixSdkWarStep());
        steps.add(new DeployExampleProjectStep(sdkApplicationServiceContainer));
        steps.add(new CreateDataServiceStep(testInfo, getIntroduceBaseDir(), sdkApplicationServiceContainer));
        steps.add(new UnpackContainerStep(dataServiceContainer));
        List<String> deploymentArgs = 
            Arrays.asList(new String[] {"-Dno.deployment.validation=true", "-Dperform.index.service.registration=false"});
        steps.add(new DeployServiceStep(dataServiceContainer, testInfo.getDir(), deploymentArgs));
        steps.add(new StartContainerStep(sdkApplicationServiceContainer));
        steps.add(new StartContainerStep(dataServiceContainer));
        steps.add(new InvokeDataServiceStep(testInfo, dataServiceContainer));
        steps.add(new InvokeCql2DataServiceStep(testInfo, dataServiceContainer));
        return steps;
    }
    
    
    public void storyTearDown() throws Throwable {
        List<Throwable> errors = new LinkedList<Throwable>();
        try {
            new StopContainerStep(dataServiceContainer).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
            new DestroyContainerStep(dataServiceContainer).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
            new StopContainerStep(sdkApplicationServiceContainer).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
            new DestroyContainerStep(sdkApplicationServiceContainer).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
            // System.out.println("Data Service was in " + testInfo.getDir() + ", deleting...");
            Utils.deleteDir(new File(testInfo.getDir()));
        } catch (Throwable th) {
            errors.add(th);
        }
        
        if (errors.size() != 0) {
            System.err.println("EXCEPTION(S) OCCURED DURING TEAR DOWN:");
            for (Throwable t : errors) {
                System.err.println("----------------");
                t.printStackTrace();
            }
            throw new Exception("EXCEPTION(S) OCCURED DURING TEAR DOWN.  SEE LOGS");
        }
    }
    
    
    public String getIntroduceBaseDir() {
        String dir = System.getProperty(SDK43ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY);
        assertNotNull("Introduce base dir system property " + 
            SDK43ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY + " is required", dir);
        return dir;
    }
}
