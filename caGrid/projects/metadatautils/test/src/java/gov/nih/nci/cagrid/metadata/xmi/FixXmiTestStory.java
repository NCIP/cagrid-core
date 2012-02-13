package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * FixXmiTestStory Haste system-testing story to exercise fix-xmi functionality
 * 
 * @author David Ervin
 * @created Nov 8, 2007 10:50:25 AM
 * @version $Id: FixXmiTestStory.java,v 1.3 2008-02-17 03:17:41 oster Exp $
 */
public class FixXmiTestStory extends Story {
    public static final String SDK_ZIP_LOCATION = "ext/dependencies/test/zips/caCORE_SDK_321.zip";
    public static final String FIX_MODELS_ZIP_LOCATION = "test/resources/fixModels.zip";

    private File sdkZipFile;
    private File sdkTempDir;

    private File modelsZipFile;
    private File modelsTempDir;


    public FixXmiTestStory() {
        super();
        setName("Fix Xmi Test Story");
    }


    public String getName() {
        return "Fix Xmi Test Story";
    }


    public String getDescription() {
        return "Tests the fix-xmi functionality";
    }


    protected Vector steps() {
        // set up file locations
        File temp = new File("tmp");
        temp.mkdirs();
        sdkZipFile = new File(SDK_ZIP_LOCATION);
        try {
            sdkTempDir = File.createTempFile("SDK321", "Temp", temp);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error creating SDK temp dir: " + ex.getMessage());
        }
        modelsZipFile = new File(FIX_MODELS_ZIP_LOCATION);
        try {
            modelsTempDir = File.createTempFile("XMIModels", "Temp", temp);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error creating XMI Models temp dir: " + ex.getMessage());
        }

        // sequence steps
        Vector<Step> steps = new Vector<Step>();
        // unzip the SDK
        steps.add(new UnzipStep(sdkZipFile.getAbsolutePath(), sdkTempDir.getAbsolutePath()));
        // unzip the models
        steps.add(new UnzipStep(modelsZipFile.getAbsolutePath(), modelsTempDir.getAbsolutePath()));
        // fix the models
        steps.add(new FixXmiStep(sdkTempDir.getAbsolutePath() + File.separator + "cacoresdk", modelsTempDir
            .getAbsolutePath()));
        return steps;
    }


    protected void storyTearDown() {
        if (sdkTempDir.exists()) {
            Utils.deleteDir(sdkTempDir);
        }
        if (modelsTempDir.exists()) {
            Utils.deleteDir(modelsTempDir);
        }
    }


    // used to make sure that if we are going to use a junit testsuite to
    // test this that the test suite will not error out
    // looking for a single test......
    public void testDummy() throws Throwable {
    }


    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(FixXmiTestStory.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
