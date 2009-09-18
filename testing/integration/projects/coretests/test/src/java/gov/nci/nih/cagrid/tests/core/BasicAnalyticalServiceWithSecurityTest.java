/*
 * Created on Apr 12, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.DorianAddTrustedCAStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianAuthenticateStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianDestroyDefaultProxyStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusDeployServiceStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusInstallSecurityDescriptorStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStopStep;

import java.io.File;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.axis.types.URI.MalformedURIException;


/**
 * This is an integration test that tests the Introduce functionality of
 * creating an analytical service with transport-level security and deploying
 * it. It creates a service from scratch, deploys it, and attempts to invoke a
 * method on it. Dorian is also deployed and used to gain user credentials.
 * 
 * @testType integration
 * @steps ServiceCreateStep,
 * @steps GlobusCreateStep, GTSSyncOnceStep, GlobusDeployServiceStep,
 *        GlobusStartStep
 * @steps DorianAuthenticateStep, DorianAddTrustedCAStep
 * @steps ServiceInvokeStep, ServiceCheckMetadataStep
 * @steps GlobusStopStep, GlobusCleanupStep
 * @steps DorianCleanupStep, DorianDestroyDefaultProxyStep
 * @author Patrick McConnell
 */
public class BasicAnalyticalServiceWithSecurityTest extends AbstractServiceTest {
    private File caFile;


    public BasicAnalyticalServiceWithSecurityTest() {
        super();
    }


    @Override
    protected void storyTearDown() throws Throwable {
        super.storyTearDown();

        if (this.caFile != null) {
            this.caFile.delete();
        }
        // super.storyTearDown();
        new DorianDestroyDefaultProxyStep().runStep();
        new DorianCleanupStep().runStep();
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Vector steps() {
        super.init("BasicAnalyticalServiceWithSecurity");

        File dorianDir = new File(System.getProperty("dorian.dir", ".." + File.separator + ".." + File.separator + ".."
            + File.separator + "caGrid" + File.separator + "projects" + File.separator + "dorian"));
        this.caFile = new File(System.getProperty("user.home"), ".globus" + File.separator + "certificates"
            + File.separator + "BasicAnalyticalServiceWithSecurityTest_ca.1");

        Vector steps = new Vector();
        steps.add(getCreateServiceStep());
        steps.add(new GlobusCreateStep(getGlobus()));
        steps.add(new GlobusInstallSecurityDescriptorStep(getGlobus()));
        steps.add(new GlobusDeployServiceStep(getGlobus(), dorianDir));
        steps.add(new GlobusDeployServiceStep(getGlobus(), getCreateServiceStep().getServiceDir()));
        steps.add(new GlobusStartStep(getGlobus()));

        String dorianURL = null;
        try {
            dorianURL = getGlobus().getServiceEPR("cagrid/Dorian").getAddress().toString();
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

        steps.add(new DorianAuthenticateStep("dorian", Constants.DORIAN_ADMIN_PASSWORD, dorianURL));
        steps.add(new DorianAddTrustedCAStep(this.caFile, dorianURL));
        try {
            addInvokeSteps(steps);
        } catch (Exception e) {
            throw new IllegalArgumentException("could not add invoke steps", e);
        }

        steps.add(new GlobusStopStep(getGlobus()));
        steps.add(new GlobusCleanupStep(getGlobus()));
        return steps;
    }


    @Override
    public String getDescription() {
        return "BasicAnalyticalServiceTest";
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(BasicAnalyticalServiceWithSecurityTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
