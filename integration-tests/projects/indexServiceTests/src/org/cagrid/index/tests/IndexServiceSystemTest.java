package org.cagrid.index.tests;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.CreateSkeletonStep;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSkeletonStep;
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.index.tests.steps.ChangeIndexSweeperDelayStep;
import org.cagrid.index.tests.steps.DeployIndexServiceStep;
import org.cagrid.index.tests.steps.FillInMetadataStep;
import org.cagrid.index.tests.steps.ServiceDiscoveryStep;
import org.cagrid.index.tests.steps.SetAdvertisementUrlStep;
import org.cagrid.index.tests.steps.SetMetadataHostingResearchCenterStep;
import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;


public class IndexServiceSystemTest extends Story {

    private static Log log = LogFactory.getLog(IndexServiceSystemTest.class);

    public static final String INDEX_SERVICE_DIR_PROPERTY = "index.dir";
    public static final String INTRODUCE_DIR_PROPERTY = "introduce.base.dir";
    public static final String DEFAULT_INDEX_SERVICE_DIR = ".." + File.separator + ".." + File.separator + ".."
        + File.separator + "caGrid" + File.separator + "projects" + File.separator + "index";
    public static final String SERVICE_NAME = "TestService";
    public static final String PACKAGE_NAME = "gov.nih.nci.cagrid.testing.service";
    public static final String SERVICE_NAMESPACE = "http://" + PACKAGE_NAME + "/" + SERVICE_NAME;

    private ServiceContainer indexServiceContainer = null;
    private ServiceContainer testServiceContainer = null;
    private TestCaseInfo testServiceInfo = null;


    public String getName() {
        return "Index Service System Test";
    }


    public String getDescription() {
        return "Tests the Index Service";
    }


    @Override
    public void runBare() throws Throwable {
        // TODO Auto-generated method stub
        super.runBare();
    }


    public boolean storySetUp() {
        testServiceInfo = new TestHelloWorldServiceInfo();

        // set up the index service container
        try {
            log.debug("Creating container for index service");
            indexServiceContainer = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
            new UnpackContainerStep(indexServiceContainer).runStep();
        } catch (Throwable ex) {
            String message = "Error creating container for index service: " + ex.getMessage();
            log.error(message, ex);
            fail(message);
        }

        // set up a testing service container
        try {
            log.debug("Creating container for testing service");
            testServiceContainer = ServiceContainerFactory.createContainer(ServiceContainerType.TOMCAT_CONTAINER);
            new UnpackContainerStep(testServiceContainer).runStep();
        } catch (Throwable ex) {
            String message = "Error creating container for testing service: " + ex.getMessage();
            log.error(message, ex);
            fail(message);
        }
        return true;
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        // the directory of the testing data service
        File testServiceDir = new File(testServiceInfo.getDir());
        if (testServiceDir.exists()) {
            try {
                log.info("Deleting old test service directory: " + testServiceDir.getAbsolutePath());
                Utils.deleteDir(testServiceDir);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error deleting old test service directory: " + ex.getMessage());
            }
        }

        // get the EPR of the Index service
        EndpointReferenceType indexEPR = null;
        try {
            indexEPR = indexServiceContainer.getServiceEPR("DefaultIndexService");
        } catch (MalformedURIException ex) {
            String message = "Error obtaining EPR of the Index Service: " + ex.getMessage();
            log.error(message, ex);
            fail(message);
        }

        // create a test grid service
        try {
            steps.add(new CreateSkeletonStep(testServiceInfo, true));
        } catch (Exception ex) {
            String message = "Error setting up service creation step: " + ex.getMessage();
            log.error(message, ex);
            fail(message);
        }
        // set the service's expected metadata
        StringBuffer testMetadata = getTestingResearchCenterInfo();
        steps.add(new SetMetadataHostingResearchCenterStep(testServiceDir, testMetadata));
        steps.add(new FillInMetadataStep(testServiceDir));
        // make that service register to our testing index service
        steps.add(new SetAdvertisementUrlStep(testServiceDir, indexEPR));

        // deploy the index service
        String indexServiceLocation = System.getProperty(INDEX_SERVICE_DIR_PROPERTY, DEFAULT_INDEX_SERVICE_DIR);
        log.info("Index service dir: " + indexServiceLocation);
        steps.add(new DeployIndexServiceStep(indexServiceContainer, new File(indexServiceLocation)));
        // change the sweeper delay of the index service
        steps.add(new ChangeIndexSweeperDelayStep(indexServiceContainer));
        // start the index service
        steps.add(new StartContainerStep(indexServiceContainer));

        // deploy the test service
        // FIXME: this SHOULD work without doing this, but Tomcat apparently
        // kills the advertisement client's JVM shutdown hook before it gets
        // a chance to complete, which would unregister the test service.
        // The option passed to this deploy step overrides the interval
        // in the test service which tells the index service how often to
        // refresh its registration
        long refreshInterval = ChangeIndexSweeperDelayStep.DEFAULT_SWEEPER_DELAY; // milliseconds
        List<String> args = Arrays.asList(new String[]{"-Dindex.service.index.refresh_milliseconds=" + refreshInterval,
                "-Dindex.service.registration.refresh_seconds=" + refreshInterval / 1000});
        steps.add(new DeployServiceStep(testServiceContainer, testServiceDir.getAbsolutePath(), args));
        // start the test service
        steps.add(new StartContainerStep(testServiceContainer));

        // sleep long enough to allow the test service to register itself
        steps.add(new SleepStep(ChangeIndexSweeperDelayStep.DEFAULT_SWEEPER_DELAY * 3));

        // get the EPR of the test service
        EndpointReferenceType testEPR = null;
        try {
            testEPR = testServiceContainer.getServiceEPR("cagrid/" + testServiceInfo.getName());
        } catch (MalformedURIException ex) {
            String message = "Error obtaining EPR of test service: " + ex.getMessage();
            log.error(message, ex);
            fail(message);
        }

        // find the test service in the index service
        steps.add(new ServiceDiscoveryStep(indexEPR, testEPR, testServiceDir, true));

        // make sure the sweeper has run and the test service is still there
        steps.add(new SleepStep(ChangeIndexSweeperDelayStep.DEFAULT_SWEEPER_DELAY * 3));
        steps.add(new ServiceDiscoveryStep(indexEPR, testEPR, testServiceDir, true));

        // shut down the test service... see note above!
        steps.add(new StopContainerStep(testServiceContainer));

        // make sure the sweeper has run and the service is gone
        steps.add(new SleepStep(ChangeIndexSweeperDelayStep.DEFAULT_SWEEPER_DELAY * 3));
        steps.add(new ServiceDiscoveryStep(indexEPR, testEPR, testServiceDir, false));

        return steps;
    }


    public void storyTearDown() throws Throwable {

        if (testServiceContainer != null) {
            if (testServiceContainer.isStarted()) {
                new StopContainerStep(testServiceContainer).runStep();
            }
            new DestroyContainerStep(testServiceContainer).runStep();
        }

        if (indexServiceContainer != null) {
            if (indexServiceContainer.isStarted()) {
                new StopContainerStep(indexServiceContainer).runStep();
            }
            new DestroyContainerStep(indexServiceContainer).runStep();
        }

        new RemoveSkeletonStep(testServiceInfo);
    }


    private StringBuffer getTestingResearchCenterInfo() {
        InputStream metadataInput = getClass().getResourceAsStream("/researchCenter.xml");
        StringBuffer metadata = null;
        try {
            metadata = Utils.inputStreamToStringBuffer(metadataInput);
            metadataInput.close();
        } catch (IOException ex) {
            String message = "Error reading default research center metadata document: " + ex.getMessage();
            log.error(message, ex);
            fail(message);
        }
        return metadata;
    }


    private static class TestHelloWorldServiceInfo extends TestCaseInfo {
        public String getName() {
            return SERVICE_NAME;
        }


        public String getDir() {
            return getName();
        }


        public String getPackageDir() {
            return getPackageName().replace('.', File.separatorChar);
        }


        public String getResourceFrameworkType() {
            return IntroduceConstants.INTRODUCE_MAIN_RESOURCE + "," + IntroduceConstants.INTRODUCE_SINGLETON_RESOURCE
                + "," + IntroduceConstants.INTRODUCE_IDENTIFIABLE_RESOURCE + ","
                + IntroduceConstants.INTRODUCE_RESOURCEPROPETIES_RESOURCE;
        }


        public String getNamespace() {
            return SERVICE_NAMESPACE;
        }


        public String getPackageName() {
            return PACKAGE_NAME;
        }


        /**
         * Overridden to only use the cagrid metadata extension and not create a
         * data service
         */
        public String getExtensions() {
            return "cagrid_metadata";
        }
    }


    private static class SleepStep extends Step {

        private long sleep;


        public SleepStep(long sleep) {
            this.sleep = sleep;
        }


        public void runStep() throws Throwable {
            Thread.sleep(sleep);
        }
    }
}
