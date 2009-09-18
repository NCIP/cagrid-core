package gov.nih.nci.cagrid.introduce.test.unit;

import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoLifetimeResource;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoMain;
import gov.nih.nci.cagrid.introduce.test.steps.AddMetadataStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddServiceContextStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddServicePropertiesStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddSimpleMethodImplStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddSimpleMethodStep;
import gov.nih.nci.cagrid.introduce.test.steps.CreateSkeletonStep;
import gov.nih.nci.cagrid.introduce.test.steps.ModifySimpleMethodStep;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSimpleMethodImplStep;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSkeletonStep;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.log4j.PropertyConfigurator;


public class SyncToolsSmokeTest extends Story {

    private TestCaseInfo tci1;

    private TestCaseInfo tci2;


    public SyncToolsSmokeTest() {
        this.setName("Introduce Codegen System Test");
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator + "log4j.properties");
    }


    public String getName() {
        return "Introduce Codegen System Test";
    }


    public String getDescription() {
        return "Testing the Introduce code generation tools";
    }


    protected Vector steps() {
        Vector steps = new Vector();

        try {
            steps.add(new CreateSkeletonStep(tci1, true));
            steps.add(new AddServiceContextStep(tci2, true));
            steps.add(new AddMetadataStep(tci1, true));
            steps.add(new AddServicePropertiesStep(tci1, true));
            steps.add(new AddSimpleMethodStep(tci1, "newMethod", false));
            steps.add(new AddSimpleMethodImplStep(tci1, "newMethod", true));
            steps.add(new AddSimpleMethodStep(tci2, "newMethod2", true));
            steps.add(new RemoveSimpleMethodImplStep(tci1, "newMethod", true));
            steps.add(new ModifySimpleMethodStep(tci1, "newMethod", false));
            steps.add(new ModifySimpleMethodStep(tci2, "newMethod2", true));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        tci1 = new TestCaseInfoMain();
        tci2 = new TestCaseInfoLifetimeResource();

        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci1);
        try {
            step1.runStep();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }


    protected void storyTearDown() throws Throwable {
        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci1);
        try {
            step1.runStep();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Convenience method for running all the Steps in this Story.
     */
    public static void main(String args[]) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(SyncToolsSmokeTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
