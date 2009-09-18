package org.cagrid.gts.test.system;

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
import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gts.test.system.steps.CheckMetadataStep;


public class GridTrustServiceStory extends ServiceStoryBase {

    private static final String SERVICE_TEMP_PATH = "tmp/TempGTS";
    private static final String GTS_URL_PATH = "cagrid/GTS";
    private static final String PATH_TO_GTS_PROJECT = "../../../caGrid/projects/gts";
    public static final String GTS_DIR_PROPERTY = "gts.service.dir";


    public GridTrustServiceStory(ServiceContainer container) {
        super(container);
    }


    public GridTrustServiceStory() {

        // init the container
        try {
            this.setContainer(ServiceContainerFactory.createContainer(ServiceContainerType.SECURE_TOMCAT_CONTAINER));
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Failed to create container: " + ex.getMessage());
        }
    }


    @Override
    public String getName() {
        return getDescription();
    }


    @Override
    public String getDescription() {
        return "Grid Trust Service Test";
    }


    protected File getGTSDir() {
        String value = System.getProperty(GTS_DIR_PROPERTY, PATH_TO_GTS_PROJECT);
        assertNotNull("System property " + GTS_DIR_PROPERTY + " was not set!", value);
        File dir = new File(value);
        return dir;
    }


    @Override
    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        File tempGTSServiceDir = new File(SERVICE_TEMP_PATH);

        // SETUP
        steps.add(new UnpackContainerStep(getContainer()));
        steps.add(new CopyServiceStep(getGTSDir(), tempGTSServiceDir));

        // CONFIGURE
        // steps.add(new CreateDatabaseStep(tempGTSServiceDir));
        //disable registration
        steps.add(new SetIndexRegistrationStep(tempGTSServiceDir.getAbsolutePath(), false));

        //DEPLOY
        DeployServiceStep deployStep = new DeployServiceStep(getContainer(), tempGTSServiceDir.getAbsolutePath(),
            Arrays.asList(new String[]{"-Dno.deployment.validation=true"}));
        steps.add(deployStep);
        steps.add(new StartContainerStep(getContainer()));

        EndpointReferenceType epr = null;
        try {
            epr = getContainer().getServiceEPR(GTS_URL_PATH);
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("Error constructing client:" + e.getMessage());
        }

        // TEST
        steps.add(new CheckMetadataStep(epr));

        return steps;
    }


    @Override
    protected void storyTearDown() throws Throwable {

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
