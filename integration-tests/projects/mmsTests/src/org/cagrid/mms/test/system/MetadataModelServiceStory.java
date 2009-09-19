package org.cagrid.mms.test.system;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerFactory;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainerType;
import gov.nih.nci.cagrid.testing.system.deployment.steps.CopyServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DeployServiceStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.DestroyContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StartContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.StopContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.steps.UnpackContainerStep;
import gov.nih.nci.cagrid.testing.system.deployment.story.ServiceStoryBase;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.mms.test.system.steps.CheckMetadataStep;


public class MetadataModelServiceStory extends ServiceStoryBase {

    private static final String SERVICE_TEMP_PATH = "tmp/TempMMS";
    private static final String RESULTS_TEMP_PATH = "tmp/results";
    private static final String MMS_URL_PATH = "cagrid/MetadataModelService";
    private static final String PATH_TO_MMS_PROJECT = "../../../caGrid/projects/mms";
    public static final String MMS_DIR_PROPERTY = "mms.service.dir";


    public MetadataModelServiceStory(ServiceContainer container) {
        super(container);
    }


    public MetadataModelServiceStory() {

        // init the container
        try {
            this.setContainer(ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER));
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
        return "Metadata Model Service Test";
    }


    protected File getMMSDir() {
        String value = System.getProperty(MMS_DIR_PROPERTY, PATH_TO_MMS_PROJECT);
        assertNotNull("System property " + MMS_DIR_PROPERTY + " was not set!", value);
        File dir = new File(value);
        return dir;
    }


    @Override
    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        File tempMMSServiceDir = new File(SERVICE_TEMP_PATH);

        // SETUP
        steps.add(new UnpackContainerStep(getContainer()));
        steps.add(new CopyServiceStep(getMMSDir(), tempMMSServiceDir));

        // CONFIGURE
        // steps.add(new SetDatabasePropertiesStep(tempMMSServiceDir));
        // steps.add(new CreateDatabaseStep(tempMMSServiceDir));

        DeployServiceStep deployStep = new DeployServiceStep(getContainer(), tempMMSServiceDir.getAbsolutePath(),
            Arrays.asList(new String[]{"-Dno.deployment.validation=true"}));
        steps.add(deployStep);
        steps.add(new StartContainerStep(getContainer()));

        EndpointReferenceType epr = null;
        try {
            epr = getContainer().getServiceEPR(MMS_URL_PATH);
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("Error constructing client:" + e.getMessage());
        }

        // TEST
        steps.add(new CheckMetadataStep(epr));

        // retrieve failures

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
