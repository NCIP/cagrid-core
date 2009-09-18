/*
 * Created on Jul 24, 2006
 */
package gov.nci.nih.cagrid.tests.core;

import gov.nci.nih.cagrid.tests.core.steps.DorianAddTrustedCAStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianApproveRegistrationStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianAuthenticateStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianConfigureStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianDestroyDefaultProxyStep;
import gov.nci.nih.cagrid.tests.core.steps.DorianSubmitRegistrationStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusCreateStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusDeployServiceStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusInstallSecurityDescriptorStep;
import gov.nci.nih.cagrid.tests.core.steps.GlobusStartStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperAddAdminStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperAddMemberStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCheckGroupsStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCheckMembersStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCheckPrivilegesStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCheckStemsStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCleanupStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCreateDbStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCreateGroupStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperCreateStemStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperGrantPrivilegeStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperInitStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperRemoveMemberStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperRemoveStemStep;
import gov.nci.nih.cagrid.tests.core.steps.GrouperRevokePrivilegeStep;
import gov.nci.nih.cagrid.tests.core.util.GlobusHelper;
import gov.nci.nih.cagrid.tests.core.util.NoAvailablePortException;
import gov.nci.nih.cagrid.tests.core.util.PortPreference;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.gaards.dorian.idp.Application;


/**
 * This is an integration test that tests some of the major functionality of
 * Dorian. It syncs GTS and deploys the dorian service and an echo service to
 * globus. It authenticates the dorian user and then insures some authentication
 * failures. It then adds the dorian CA to the globus trusted CAs. An
 * application for a user account is submitted, approved, and the new user is
 * authenticated.
 * 
 * @testType integration
 * @steps GlobusCreateStep, GTSSyncOnceStep, GlobusDeployServiceStep,
 *        DorianConfigureStep, GlobusStartStep
 * @steps DorianAuthenticateStep, DorianDestroyDefaultProxyStep,
 *        DorianAuthenticateFailStep
 * @steps DorianAddTrustedCAStep, DorianSubmitRegistrationStep,
 *        DorianApproveRegistrationStep
 * @steps GlobusStopStep, GlobusCleanupStep
 * @steps DorianCleanupStep
 * @author Patrick McConnell
 */
public class GridGrouperTest extends Story {
    private GlobusHelper grouperGlobus;
    private GlobusHelper dorianGlobus;

    private File grouperDir;
    private File dorianDir;
    private File caFile;
    private String grouperAdminName;
    private String grouperAdmin;


    public GridGrouperTest() {
        super();
    }


    @Override
    public String getName() {
        return "GridGrouper Story";
    }


    @Override
    protected boolean storySetUp() throws Throwable {
        return true;
    }


    @Override
    protected void storyTearDown() throws Throwable {
        this.caFile.delete();

        if (this.dorianGlobus != null) {
            this.dorianGlobus.stopGlobus();
            this.dorianGlobus.cleanupTempGlobus();
        }
        if (this.grouperGlobus != null) {
            this.grouperGlobus.stopGlobus();
            this.grouperGlobus.cleanupTempGlobus();
        }

        new DorianDestroyDefaultProxyStep().runStep();
        new DorianCleanupStep().runStep();
        new GrouperCleanupStep(this.grouperDir).runStep();
    }


    @Override
    @SuppressWarnings("unchecked")
    protected Vector steps() {
        String idp = "/C=US/O=abc/OU=xyz/OU=caGrid/OU=Dorian/CN=";

        this.dorianGlobus = new GlobusHelper(true);
        this.dorianDir = new File(System.getProperty("dorian.dir", ".." + File.separator + ".." + File.separator + ".."
            + File.separator + "caGrid" + File.separator + "projects" + File.separator + "dorian"));
        this.caFile = new File(System.getProperty("user.home"), ".globus" + File.separator + "certificates"
            + File.separator + "DorianTest_ca.1");

        Vector steps = new Vector();

        // initialize dorian
        steps.add(new GlobusCreateStep(this.dorianGlobus));
        steps.add(new GlobusInstallSecurityDescriptorStep(this.dorianGlobus));
        steps.add(new GlobusDeployServiceStep(this.dorianGlobus, this.dorianDir));
        steps.add(new DorianConfigureStep(this.dorianGlobus));
        steps.add(new GlobusStartStep(this.dorianGlobus));

        String dorianURL = null;
        Integer dorianPort = null;
        try {
            dorianURL = this.dorianGlobus.getServiceEPR("cagrid/Dorian").getAddress().toString();
            dorianPort = this.dorianGlobus.getPort();
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("Unable to get dorian URL:" + e.getMessage());
        } catch (NoAvailablePortException e) {
            fail("Unable to find a port for dorian:" + e.getMessage());
        }

        // the above doesn't actually bind to the socket until the
        // startstep runs, so setup the grid grouper globus's port preferences
        // to exclude the port dorian will use
        PortPreference grouperPortPref = new PortPreference(GlobusHelper.getDefaultPortRangeMinimum(), GlobusHelper
            .getDefaultPortRangeMaximum(), new Integer[]{dorianPort});

        this.grouperGlobus = new GlobusHelper(true, grouperPortPref);
        this.grouperDir = new File(System.getProperty("grouper.dir", ".." + File.separator + ".." + File.separator
            + ".." + File.separator + "caGrid" + File.separator + "projects" + File.separator + "gridgrouper"));
        this.grouperAdminName = System.getProperty("grouper.adminId", "grouper");
        this.grouperAdmin = System.getProperty("grouper.admin", idp + this.grouperAdminName);

        // initialize grouper
        steps.add(new GrouperCreateDbStep());
        steps.add(new GrouperInitStep(this.grouperDir));
        steps.add(new GrouperAddAdminStep(this.grouperDir, this.grouperAdmin));
        steps.add(new GlobusCreateStep(this.grouperGlobus));
        steps.add(new GlobusInstallSecurityDescriptorStep(this.grouperGlobus));
        steps.add(new GlobusDeployServiceStep(this.grouperGlobus, this.grouperDir));
        steps.add(new GlobusStartStep(this.grouperGlobus));

        String grouperURL = null;
        try {
            grouperURL = this.grouperGlobus.getServiceEPR("cagrid/GridGrouper").getAddress().toString();
        } catch (MalformedURIException e) {
            e.printStackTrace();
            fail("Unable to get grouper URL:" + e.getMessage());
        }

        // test successful authenticate
        steps.add(new DorianAuthenticateStep("dorian", Constants.DORIAN_ADMIN_PASSWORD, dorianURL));
        steps.add(new DorianDestroyDefaultProxyStep());

        // add trusted ca
        steps.add(new DorianAuthenticateStep("dorian", Constants.DORIAN_ADMIN_PASSWORD, dorianURL));
        steps.add(new DorianAddTrustedCAStep(this.caFile, dorianURL));
        steps.add(new DorianDestroyDefaultProxyStep());

        // register users in dorian
        String[] users = new String[]{this.grouperAdminName, "subject1", "subject2"};
        String password = "$W0rdD0ct0R$";
        try {
            File applicationFile = new File("test", "resources" + File.separator + "userApplications")
                .listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.isFile() && file.getName().endsWith(".xml");
                    }
                })[0];
            
            
            DorianAuthenticateStep auth = new DorianAuthenticateStep("dorian", Constants.DORIAN_ADMIN_PASSWORD, dorianURL);
            steps.add(auth);
            for (String user : users) {
                // create registration
                Application application = (Application) Utils.deserializeDocument(applicationFile.toString(),
                    Application.class);
                application.setUserId(user);
                application.setPassword(password);

                // submit and approve registration
                steps.add(new DorianSubmitRegistrationStep(application, dorianURL));
                steps.add(new DorianApproveRegistrationStep(application, dorianURL, auth));
            }
        } catch (Exception e) {
            throw new RuntimeException("unable add new user steps", e);
        }

        // authenticate grouper
        steps.add(new DorianAuthenticateStep(this.grouperAdminName, password, dorianURL));

        // add stems and groups
        steps.add(new GrouperCreateStemStep("test:stem1", grouperURL));
        steps.add(new GrouperCreateStemStep("test:stem2:stem3", grouperURL));
        steps.add(new GrouperCreateStemStep("test:stem2:stem4", grouperURL));
        steps.add(new GrouperCreateGroupStep("test:stem1:group1", grouperURL));
        steps.add(new GrouperCreateGroupStep("test:stem2:stem3:group2", grouperURL));
        steps.add(new GrouperCreateGroupStep("test:stem2:stem3:group3", grouperURL));

        // add members
        steps.add(new GrouperAddMemberStep("test:stem1:group1", idp + "subject1", grouperURL));
        steps.add(new GrouperAddMemberStep("test:stem1:group1", idp + "subject2", grouperURL));
        steps.add(new GrouperAddMemberStep("test:stem2:stem3:group2", idp + "subject1", grouperURL));
        steps.add(new GrouperAddMemberStep("test:stem2:stem3:group2", idp + "subject2", grouperURL));

        // check stems, groups, and members
        steps.add(new GrouperCheckStemsStep("test", new String[]{"stem1", "stem2"}, grouperURL));
        steps.add(new GrouperCheckStemsStep("test:stem1", new String[]{}, grouperURL));
        steps.add(new GrouperCheckStemsStep("test:stem2", new String[]{"stem3", "stem4"}, grouperURL));
        steps.add(new GrouperCheckGroupsStep("test", new String[]{}, grouperURL));
        steps.add(new GrouperCheckGroupsStep("test:stem1", new String[]{"group1"}, grouperURL));
        steps.add(new GrouperCheckGroupsStep("test:stem2:stem3", new String[]{"group2", "group3"}, grouperURL));
        steps.add(new GrouperCheckMembersStep("test:stem1:group1", "All", new String[]{idp + "subject1",
                idp + "subject2"}, grouperURL));
        steps.add(new GrouperCheckMembersStep("test:stem2:stem3:group2", "All", new String[]{idp + "subject1",
                idp + "subject2"}, grouperURL));
        steps.add(new GrouperCheckMembersStep("test:stem2:stem3:group3", "All", new String[]{}, grouperURL));

        // grant privileges
        steps.add(new GrouperGrantPrivilegeStep("test:stem1:group1", idp + "subject1", "admin", grouperURL));
        steps.add(new GrouperGrantPrivilegeStep("test:stem2:stem3:group2", idp + "subject2", "admin", grouperURL));
        steps.add(new GrouperGrantPrivilegeStep("test:stem2:stem3:group2", idp + "subject1", "optout", grouperURL));
        steps.add(new GrouperGrantPrivilegeStep("test:stem1", idp + "subject1", "stem", grouperURL));

        // check privileges
        steps.add(new GrouperCheckPrivilegesStep("test:stem1:group1", idp + "subject1", new String[]{"admin"},
            grouperURL));
        steps.add(new GrouperCheckPrivilegesStep("test:stem2:stem3:group2", idp + "subject2", new String[]{"admin"},
            grouperURL));
        steps.add(new GrouperCheckPrivilegesStep("test:stem2:stem3:group2", idp + "subject1", new String[]{"optout"},
            grouperURL));
        steps.add(new GrouperCheckPrivilegesStep("test:stem1", idp + "subject1", new String[]{"stem"}, grouperURL));

        // test group admin privileges
        steps.add(new DorianAuthenticateStep("subject1", password, dorianURL));
        steps.add(new GrouperAddMemberStep("test:stem1:group1", idp + "subject3", grouperURL));
        steps.add(new GrouperCheckMembersStep("test:stem1:group1", "All", new String[]{idp + "subject1",
                idp + "subject2", idp + "subject3"}, grouperURL));
        steps.add(new GrouperGrantPrivilegeStep("test:stem1:group1", idp + "subject3", "admin", grouperURL));
        steps.add(new GrouperCheckPrivilegesStep("test:stem1:group1", idp + "subject3", new String[]{"admin"},
            grouperURL));
        steps.add(new GrouperRemoveMemberStep("test:stem1:group1", idp + "subject3", grouperURL));
        steps.add(new GrouperCheckMembersStep("test:stem1:group1", "All", new String[]{idp + "subject1",
                idp + "subject2"}, grouperURL));

        // test group admin privileges fail
        steps.add(new DorianAuthenticateStep("subject1",password, dorianURL));
        steps.add(new GrouperRemoveMemberStep("test:stem2:stem3:group2", idp + "subject2", true, grouperURL));

        // test group optout privileges
        steps.add(new DorianAuthenticateStep("subject1", password, dorianURL));
        steps.add(new GrouperRemoveMemberStep("test:stem2:stem3:group2", idp + "subject1", grouperURL));
        steps.add(new DorianAuthenticateStep("subject2", password, dorianURL));
        steps.add(new GrouperAddMemberStep("test:stem2:stem3:group2", idp + "subject1", grouperURL));
        steps.add(new GrouperRevokePrivilegeStep("test:stem2:stem3:group2", idp + "subject1", "optout", grouperURL));
        steps.add(new DorianAuthenticateStep("subject1", password, dorianURL));
        steps.add(new GrouperRemoveMemberStep("test:stem2:stem3:group2", idp + "subject1", true, grouperURL));
        steps.add(new DorianAuthenticateStep("subject2", password, dorianURL));
        steps.add(new GrouperGrantPrivilegeStep("test:stem2:stem3:group2", idp + "subject1", "optout", grouperURL));

        // test stem privileges
        steps.add(new DorianAuthenticateStep("subject1", password, dorianURL));
        steps.add(new GrouperCreateStemStep("test:stem1:stem5", grouperURL));
        steps.add(new GrouperCheckStemsStep("test:stem1", new String[]{"stem5"}, grouperURL));
        steps.add(new GrouperRemoveStemStep("test:stem1:stem5", grouperURL));
        steps.add(new DorianAuthenticateStep("subject2", password, dorianURL));
        steps.add(new GrouperCreateStemStep("test:stem1:stem5", true, grouperURL));

        return steps;
    }


    @Override
    public String getDescription() {
        return "GridGrouperTest";
    }


    /**
     * used to make sure that if we are going to use a junit testsuite to test
     * this that the test suite will not error out looking for a single
     * test......
     */
    public void testDummy() throws Throwable {
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(GridGrouperTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
