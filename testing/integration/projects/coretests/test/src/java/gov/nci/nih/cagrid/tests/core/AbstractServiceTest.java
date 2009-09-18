/*
 * Created on Jun 11, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.ServiceCreateStep;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nci.nih.cagrid.tests.core.util.IntroduceServiceInfo;
import gov.nci.nih.cagrid.tests.core.util.ServiceHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;


/**
 * This is a base class to be used for creating and invoking Introduce-built
 * services.
 * 
 * @see <a
 *      href="http://gforge.nci.nih.gov/plugins/scmcvs/cvsweb.php/cagrid-1-0/Documentation/docs/tests/cagrid-1-0-testing.doc?cvsroot=cagrid-1-0">cagrid-1-0-testing.doc</a>
 * @author Patrick McConnell
 */
public abstract class AbstractServiceTest extends Story {
    protected ServiceHelper helper;


    @Override
    public String getName() {
        String name = "";
        if (this.helper != null) {
            name = this.helper.getServiceName() + " ";
        }
        return name += "Introduce Story";
    }


    protected void init(String serviceName) {
        // service name
        this.helper = new ServiceHelper(serviceName);
    }


    @SuppressWarnings("unchecked")
    public void addInvokeSteps(Vector steps) throws Exception {
        steps.addAll(this.helper.getInvokeSteps());
    }


    @SuppressWarnings("unchecked")
    public static void addInvokeSteps(Vector steps, File serviceDir, File testDir, File methodsDir,
        EndpointReferenceType endpoint) throws Exception {
        steps.addAll(ServiceHelper.getInvokeSteps(serviceDir, testDir, methodsDir, endpoint));
    }


    @Override
    protected boolean storySetUp() throws Throwable {
        return true;
    }


    @Override
    protected void storyTearDown() throws Throwable {
        if (getGlobus() != null) {
            getGlobus().stopGlobus();
            getGlobus().cleanupTempGlobus();
        }
        Utils.deleteDir(helper.getTempDir());
    }


    /**
     * used to make sure that if we are going to use a junit testsuite to test
     * this that the test suite will not error out looking for a single
     * test......
     */
    public void testDummy() throws Throwable {
    }


    public String getServiceName() {
        return this.helper.getServiceName();
    }


    public ServiceCreateStep getCreateServiceStep() {
        return this.helper.getCreateServiceStep();
    }


    public EndpointReferenceType getEndpoint() {
        return this.helper.getEndpoint();
    }


    public GlobusHelper getGlobus() {
        if (this.helper != null) {
            return this.helper.getGlobus();
        }
        return null;
    }


    public File getIntroduceDir() {
        return this.helper.getIntroduceDir();
    }


    public File getMetadataFile() {
        return this.helper.getMetadataFile();
    }


    public File getServiceDir() {
        return this.helper.getServiceDir();
    }


    public IntroduceServiceInfo getServiceInfo() {
        return this.helper.getServiceInfo();
    }


    public File getTempDir() {
        return this.helper.getTempDir();
    }


    public File getTestDir() {
        return this.helper.getTestDir();
    }
}
