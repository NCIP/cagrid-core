/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.AddBasicServiceMetadata;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusDeployServiceStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStopStep;
import gov.nci.nih.cagrid.tests.core.steps.ServiceCheckMetadataStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * This is an integration test that tests the Introduce functionality of
 * creating an analytical service with service-level metadata and deploying it.
 * It creates a service from scratch, deploys it, attempts to invoke a method on
 * it, and then checks that it is exposing the correct metadata.
 * 
 * @testType integration
 * @steps ServiceCreateStep,
 * @steps GlobusCreateStep, GlobusDeployServiceStep, GlobusStartStep
 * @steps ServiceInvokeStep, ServiceCheckMetadataStep
 * @steps GlobusStopStep, GlobusCleanupStep
 * @author Patrick McConnell
 */
public class BasicAnalyticalServiceWithMetadataTest extends AbstractServiceTest {
    public BasicAnalyticalServiceWithMetadataTest() {
        super();
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Vector steps() {
        super.init("BasicAnalyticalServiceWithMetadata");

        Vector steps = new Vector();
        steps.add(getCreateServiceStep());
        steps.add(new GlobusCreateStep(getGlobus()));
        steps.add(new AddBasicServiceMetadata(getCreateServiceStep().getServiceDir(),getMetadataFile()));
        GlobusDeployServiceStep deployStep = new GlobusDeployServiceStep(getGlobus(),getCreateServiceStep().getServiceDir());
        steps.add(deployStep);
        steps.add(new GlobusStartStep(getGlobus()));
        try {
            addInvokeSteps(steps);
        } catch (Exception e) {
            throw new IllegalArgumentException("could not add invoke steps", e);
        }
        steps.add(new ServiceCheckMetadataStep(getEndpoint(), getMetadataFile()));
        steps.add(new GlobusStopStep(getGlobus()));
        steps.add(new GlobusCleanupStep(getGlobus()));
        return steps;
    }


    @Override
    public String getDescription() {
        return "BasicAnalyticalServiceWithMetadataTest";
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(BasicAnalyticalServiceWithMetadataTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
