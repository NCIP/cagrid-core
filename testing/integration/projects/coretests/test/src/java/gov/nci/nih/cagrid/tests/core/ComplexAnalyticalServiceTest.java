/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.GlobusCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusDeployServiceStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStopStep;

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * This is an integration test that tests the Introduce functionality of
 * creating an analytical service and deploying it. It creates a service from
 * scratch, deploys it, and then attempts to invoke a method on it.
 * 
 * @testType integration
 * @steps ServiceCreateStep,
 * @steps GlobusCreateStep, GlobusDeployServiceStep, GlobusStartStep
 * @steps ServiceInvokeStep
 * @steps GlobusStopStep, GlobusCleanupStep
 * @author Patrick McConnell
 */
public class ComplexAnalyticalServiceTest extends AbstractServiceTest {
    public ComplexAnalyticalServiceTest() {
        super();
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Vector steps() {
        super.init("ComplexAnalyticalService");

        Vector steps = new Vector();
        steps.add(getCreateServiceStep());
        steps.add(new GlobusCreateStep(getGlobus()));
        steps.add(new GlobusDeployServiceStep(getGlobus(), getCreateServiceStep().getServiceDir()));
        steps.add(new GlobusStartStep(getGlobus()));
        try {
            addInvokeSteps(steps);
        } catch (Exception e) {
            throw new IllegalArgumentException("could not add invoke steps", e);
        }
        // steps.add(new CheckServiceMetadataStep(endpoint, metadataFile));
        steps.add(new GlobusStopStep(getGlobus()));
        steps.add(new GlobusCleanupStep(getGlobus()));
        return steps;
    }


    @Override
    public String getDescription() {
        return "ComplexAnalyticalServiceTest";
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(ComplexAnalyticalServiceTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
