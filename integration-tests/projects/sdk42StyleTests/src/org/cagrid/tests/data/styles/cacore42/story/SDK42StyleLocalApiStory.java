package org.cagrid.tests.data.styles.cacore42.story;

import gov.nih.nci.cagrid.common.Utils;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.tests.data.styles.cacore42.SDK42ServiceStyleSystemTestConstants;
import org.cagrid.tests.data.styles.cacore42.steps.ChangeCsmUserInDatabaseStep;
import org.cagrid.tests.data.styles.cacore42.steps.CreateDataServiceStep;
import org.cagrid.tests.data.styles.cacore42.steps.InvokeCsmDataServiceStep;
import org.cagrid.tests.data.styles.cacore42.steps.InvokeDataServiceStep;

public class SDK42StyleLocalApiStory extends Story {
    
    private DataTestCaseInfo testInfo = null;
    private ServiceContainer container = null;
    private boolean useSecureContainer = false;
    private boolean useCsmSecurity = false;

    public SDK42StyleLocalApiStory(boolean useSecureContainer, boolean useCsmSecurity) {
        super();
        this.useSecureContainer = useSecureContainer;
        this.useCsmSecurity = useCsmSecurity;
    }


    public String getDescription() {
        return "Creates an SDK 4.2 style data service, configures it to use the local API" + 
            (useCsmSecurity ? " with CSM security" : "") +
            ", deploys it to " + 
            (useSecureContainer ? "a secure " : "an insecure ") + 
            "container, and invokes it.";
    }
    
    
    public String getName() {
        return "SDK 4_2 Style data service using local API " + 
            (useCsmSecurity ? " with CSM" : "") + 
            "creation and " + (useSecureContainer ? " secure" : "") + " invocation test";
    }
    
    
    public boolean storySetUp() throws Throwable {
        testInfo = SDK42ServiceStyleSystemTestConstants.getTestServiceInfo();
        ServiceContainerType containerType = useSecureContainer ? 
            ServiceContainerType.SECURE_TOMCAT_CONTAINER : ServiceContainerType.TOMCAT_CONTAINER;
        container = ServiceContainerFactory.createContainer(containerType);
        File serviceDir = new File(testInfo.getDir());
        serviceDir.mkdirs();
        
        return container != null && serviceDir.exists() && serviceDir.isDirectory();
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new CreateDataServiceStep(testInfo, getIntroduceBaseDir(), useSecureContainer, useCsmSecurity));
        steps.add(new UnpackContainerStep(container));
        if (useCsmSecurity) {
            steps.add(new ChangeCsmUserInDatabaseStep((SecureContainer) container));
        }
        List<String> deploymentArgs = 
            Arrays.asList(new String[] {"-Dno.deployment.validation=true"});
        steps.add(new DeployServiceStep(container, testInfo.getDir(), deploymentArgs));
        steps.add(new StartContainerStep(container));
        if (useCsmSecurity) {
            steps.add(new InvokeCsmDataServiceStep(testInfo, container));
        } else {
            steps.add(new InvokeDataServiceStep(testInfo, container));
        }
        return steps;
    }
    
    
    public void storyTearDown() throws Throwable {
        List<Throwable> errors = new LinkedList<Throwable>();
        try {
            new StopContainerStep(container).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
            new DestroyContainerStep(container).runStep();
        } catch (Throwable th) {
            errors.add(th);
        }
        try {
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
        String dir = System.getProperty(SDK42ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY);
        assertNotNull("Introduce base dir system property " + 
            SDK42ServiceStyleSystemTestConstants.INTRODUCE_DIR_PROPERTY + " is required", dir);
        return dir;
    }
}
