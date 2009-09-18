package org.cagrid.introduce.test.system;

import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoForImportService;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfoMain;
import gov.nih.nci.cagrid.introduce.test.steps.AddImportedMethodStep;
import gov.nih.nci.cagrid.introduce.test.steps.AddSimpleMethodStep;
import gov.nih.nci.cagrid.introduce.test.steps.CreateSkeletonStep;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSkeletonStep;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.log4j.PropertyConfigurator;


public class SyncToolsSimpleImportTest extends Story {
    private TestCaseInfo tci1;

    private TestCaseInfo tci3;


    public SyncToolsSimpleImportTest() {
        this.setName("Introduce Codegen Simple Import System Test");
        PropertyConfigurator.configure("." + File.separator + "conf" + File.separator
            + "log4j.properties");
    }


    public String getName() {
        return "Introduce Codegen Simple Import System Test";
    }


    public String getDescription() {
        return "Testing the Introduce code generation tools";
    }


    protected Vector steps() {
        Vector steps = new Vector();

        try {
            steps.add(new CreateSkeletonStep(tci1, true));
            steps.add(new CreateSkeletonStep(tci3, true));

            steps.add(new AddSimpleMethodStep(tci3, "newMethod", true));
            steps.add(new AddImportedMethodStep(tci1, tci3, "newMethod", true, true));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        return steps;
    }


    protected boolean storySetUp() throws Throwable {
        this.tci1 = new TestCaseInfoMain();
        this.tci3 = new TestCaseInfoForImportService();

        RemoveSkeletonStep step1 = new RemoveSkeletonStep(tci1);
        try {
            step1.runStep();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RemoveSkeletonStep step2 = new RemoveSkeletonStep(tci3);
        try {
            step2.runStep();
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
        RemoveSkeletonStep step2 = new RemoveSkeletonStep(tci3);
        try {
            step2.runStep();
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
        TestResult result = runner.doRun(new TestSuite(SyncToolsSimpleImportTest.class));
        System.exit(result.errorCount() + result.failureCount());
    }

}
