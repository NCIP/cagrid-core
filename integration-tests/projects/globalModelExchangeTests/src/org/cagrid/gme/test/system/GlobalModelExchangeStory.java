package org.cagrid.gme.test.system;

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
import org.cagrid.gme.domain.XMLSchema;
import org.cagrid.gme.domain.XMLSchemaImportInformation;
import org.cagrid.gme.domain.XMLSchemaNamespace;
import org.cagrid.gme.test.system.steps.CheckCacheSchemasStep;
import org.cagrid.gme.test.system.steps.CheckGetSchemaBundlesStep;
import org.cagrid.gme.test.system.steps.CheckGetSchemasStep;
import org.cagrid.gme.test.system.steps.CheckPublishedNamespacesStep;
import org.cagrid.gme.test.system.steps.CreateDatabaseStep;
import org.cagrid.gme.test.system.steps.DeleteSchemasStep;
import org.cagrid.gme.test.system.steps.PublishSchemasStep;
import org.cagrid.gme.test.system.steps.SetDatabasePropertiesStep;


public class GlobalModelExchangeStory extends ServiceStoryBase {

    private static final String SERVICE_TEMP_PATH = "tmp/TempGME";
    private static final String RESULTS_TEMP_PATH = "tmp/results";
    private static final String GME_URL_PATH = "cagrid/GlobalModelExchange";
    private static final String PATH_TO_GME_PROJECT = "../../../caGrid/projects/globalModelExchange";
    public static final String GME_DIR_PROPERTY = "gme.service.dir";
    private static final File CAARRAY_TEST_CASE_DIR = new File("resources/schemas/caarray");


    public GlobalModelExchangeStory(ServiceContainer container) {
        super(container);
    }


    public GlobalModelExchangeStory() {

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
        return "Global Model Exchange Service Test";
    }


    protected File getGMEDir() {
        String value = System.getProperty(GME_DIR_PROPERTY, PATH_TO_GME_PROJECT);
        assertNotNull("System property " + GME_DIR_PROPERTY + " was not set!", value);
        File dir = new File(value);
        return dir;
    }


    @Override
    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();
        File tempGMEServiceDir = new File(SERVICE_TEMP_PATH);

        // SETUP
        steps.add(new UnpackContainerStep(getContainer()));
        steps.add(new CopyServiceStep(getGMEDir(), tempGMEServiceDir));

        // CONFIGURE
        steps.add(new SetDatabasePropertiesStep(tempGMEServiceDir));
        steps.add(new CreateDatabaseStep(tempGMEServiceDir));

        DeployServiceStep deployStep = new DeployServiceStep(getContainer(), tempGMEServiceDir.getAbsolutePath(),
            Arrays.asList(new String[]{"-Dno.deployment.validation=true"}));
        steps.add(deployStep);
        steps.add(new StartContainerStep(getContainer()));

        EndpointReferenceType epr = null;
        try {
            epr = getContainer().getServiceEPR(GME_URL_PATH);
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("Error constructing client:" + e.getMessage());
        }

        // TEST

        // get namespaces
        steps.add(new CheckPublishedNamespacesStep(epr, new ArrayList<XMLSchemaNamespace>()));

        TestCaseInfo caArrayTestCaseInfo = null;
        Collection<XMLSchema> caArraySchemas = null;
        Collection<XMLSchemaNamespace> caArrayNamespaces = null;
        Collection<XMLSchemaImportInformation> caArrayIIs = null;

        // upload some schemas
        try {
            caArrayTestCaseInfo = new TestCaseInfo(CAARRAY_TEST_CASE_DIR);
            caArraySchemas = caArrayTestCaseInfo.getSchemas();
            caArrayNamespaces = caArrayTestCaseInfo.getNamespaces();
            caArrayIIs = caArrayTestCaseInfo.getImportInformation();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to construct test case information");
        }
        steps.add(new PublishSchemasStep(epr, caArraySchemas));

        // check the namespaces
        steps.add(new CheckPublishedNamespacesStep(epr, caArrayNamespaces));

        // retrieve them
        steps.add(new CheckGetSchemasStep(epr, caArraySchemas));

        // check the imports
        steps.add(new CheckGetSchemaBundlesStep(epr, caArrayIIs));

        for (XMLSchemaImportInformation ii : caArrayIIs) {
            steps.add(new CheckCacheSchemasStep(epr, ii.getTargetNamespace(), new File(RESULTS_TEMP_PATH)));
        }

        // delete
        steps.add(new DeleteSchemasStep(epr, caArrayNamespaces));
        steps.add(new CheckPublishedNamespacesStep(epr, new ArrayList<XMLSchemaNamespace>()));

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
