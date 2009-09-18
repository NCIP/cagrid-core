package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusDeployServiceStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusInstallSecurityDescriptorStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStopStep;
import gov.nci.nih.cagrid.tests.core.steps.IndexServiceConfigStep;
import gov.nci.nih.cagrid.tests.core.steps.ServiceAdvertiseConfigStep;
import gov.nci.nih.cagrid.tests.core.steps.ServiceDiscoveryStep;
import gov.nci.nih.cagrid.tests.core.steps.SleepStep;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nci.nih.cagrid.tests.core.util.PortPreference;

import java.io.File;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;


/**
 * This is an integration test that tests the deployment of the index service
 * and the discovery of services registering to it and unregistering from it.
 * 
 * @testType integration
 * @steps ServiceCreateStep, ServiceAdvertiseConfigStep
 * @steps GlobusCreateStep, GlobusDeployServiceStep, GlobusStartStep
 * @steps SleepStep, ServiceDiscoveryStep
 * @steps GlobusStopStep, GlobusCleanupStep
 * @author Patrick McConnell
 */
public class IndexServiceTest extends AbstractServiceTest {
    GlobusHelper indexGlobus = null;


    public IndexServiceTest() {
        super();
    }


    @Override
    protected void storyTearDown() throws Throwable {
        if (this.indexGlobus != null) {
          this.indexGlobus.stopGlobus();
          this.indexGlobus.cleanupTempGlobus();
        }
        super.storyTearDown();
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Vector steps() {
        super.init("BasicDataService");

        File indexServiceDir = new File(System.getProperty("index.dir", ".." + File.separator + ".." + File.separator
            + ".." + File.separator + "caGrid" + File.separator + "projects" + File.separator + "index"));

        PortPreference indexPortPref = null;
        try {
            indexPortPref = new PortPreference(GlobusHelper.getDefaultPortRangeMinimum(), GlobusHelper
                .getDefaultPortRangeMaximum(), new Integer[]{getGlobus().getPort()});
        } catch (Exception e1) {
            e1.printStackTrace();
            fail("Problem getting index port:" + e1.getMessage());
        }
        this.indexGlobus = new GlobusHelper(true, indexPortPref);

        EndpointReferenceType indexEPR = null;
        try {
            indexEPR = this.indexGlobus.getServiceEPR("DefaultIndexService");
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("could not add advertise steps:" + e.getMessage());
        }

        // 5 seconds
        long duration = 5000;

        Vector steps = new Vector();

        // stand up Index Service
        steps.add(new GlobusCreateStep(this.indexGlobus));
        if (this.indexGlobus.isSecure()) {
            steps.add(new GlobusInstallSecurityDescriptorStep(this.indexGlobus));
        }
        steps.add(new GlobusDeployServiceStep(this.indexGlobus, indexServiceDir, "deployIndexGlobus"));
        steps.add(new IndexServiceConfigStep(this.indexGlobus, duration));
        steps.add(new GlobusStartStep(this.indexGlobus));

        // stand up the service
        steps.add(getCreateServiceStep());
        steps.add(new ServiceAdvertiseConfigStep(indexEPR, getServiceDir(), getServiceName()));
        steps.add(new GlobusCreateStep(getGlobus()));
        steps.add(new GlobusDeployServiceStep(getGlobus(), getCreateServiceStep().getServiceDir()));
        steps.add(new GlobusStartStep(getGlobus()));

        // give it time to start up
        steps.add(new SleepStep(duration));
        try {
            steps.add(new ServiceDiscoveryStep(indexEPR, getEndpoint(), getMetadataFile(), true));
        } catch (Exception e) {
            fail("could not add discovery step");
        }

        // make sure the sweeper has run and its still there
        steps.add(new SleepStep(duration * 2));
        try {
            steps.add(new ServiceDiscoveryStep(indexEPR, getEndpoint(), getMetadataFile(), true));
        } catch (Exception e) {
            fail("could not add discovery step");
        }

        // shutdown the service
        steps.add(new GlobusStopStep(getGlobus()));

        // make sure the sweeper has run and the service is gone
        steps.add(new SleepStep(duration * 3));
        try {
            steps.add(new ServiceDiscoveryStep(indexEPR, getEndpoint(), getMetadataFile(), false));
        } catch (Exception e) {
            fail("could not add discovery step");
        }

        // TODO: this should be able to be done from storyTearDown, but is not
        // working
        steps.add(new GlobusStopStep(this.indexGlobus));

        return steps;
    }


    @Override
    public String getDescription() {
        return "IndexServiceTest";
    }


    @Override
    public String getName() {
        return "Index Service Story";
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(IndexServiceTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
