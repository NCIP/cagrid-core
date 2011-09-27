package gov.nih.nci.cagrid.introduce.extensions.metadata.upgrade.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.extensions.metadata.upgrade.system.steps.CompareServiceToServiceMetadataStep;
import gov.nih.nci.cagrid.introduce.test.IntroduceTestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.introduce.test.steps.RemoveSkeletonStep;
import gov.nih.nci.cagrid.introduce.test.steps.UnzipOldServiceStep;
import gov.nih.nci.cagrid.introduce.test.steps.UpgradesStep;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;


/**
 * UpgradeMetadataFrom1pt5Story
 * Tests upgrading a service with metadat from caGrid 1.5 to 1.6
 * 
 * NOTE: Needs to be run from Introduce directory
 */
public class UpgradeMetadataFrom1pt5Story extends Story {

    protected static final File PROJECT_DIR = new File(".." + File.separator + ".." + File.separator + ".."
        + File.separator + "integration-tests" + File.separator + "projects" + File.separator + "cabigextensionsTests");
    protected static final File TEST_DIR = new File(PROJECT_DIR, "test" + File.separator + "MetadataUpgradeService15");
    protected static final File TEST_SERVICE_ZIP = new File(PROJECT_DIR, "test" + File.separator + "resources"
        + File.separator + "Introduce_1.5_ServiceWithMetadata.zip");


    @Override
    public void testDummy() {
    }


    @Override
    public String getDescription() {
        return "Tests the metadata upgrade extensions.";
    }


    @Override
    public String getName() {
        return "Metadata Upgrade Story (1-5 to 1-6)";
    }


    @Override
    protected boolean storySetUp() throws Throwable {
        if (TEST_DIR.exists()) {
            TEST_DIR.delete();
        }
        assertTrue("Old service zip (" + TEST_SERVICE_ZIP + ") does not exist.", TEST_SERVICE_ZIP.exists());
        assertTrue("Old service zip (" + TEST_SERVICE_ZIP + ") is not readable.", TEST_SERVICE_ZIP.canRead());
        return true;
    }


    @Override
    protected void storyTearDown() throws Throwable {
        super.storyTearDown();
        if (TEST_DIR.exists()) {
            Utils.deleteDir(TEST_DIR);
        }
    }


    @Override
    protected Vector<Step> steps() {
        Vector<Step> steps = new Vector<Step>();

        TestCaseInfo tci = new IntroduceTestCaseInfo("EVSGridService", TEST_DIR.getPath(),
            "gov.nih.nci.cagrid.evsgridservice", "http://cagrid.nci.nih.gov/EVSGridService");

        File newMDFile = new File(TEST_DIR, "etc/serviceMetadata.xml");

        try {
            // remove old skeleton if exists
            steps.add(new RemoveSkeletonStep(tci));
            // load existing service
            steps.add(new UnzipOldServiceStep(TEST_SERVICE_ZIP.getAbsolutePath(), tci));
            // upgrade service ( upgrades, syncs, builds)
            steps.add(new UpgradesStep(tci, true));
            // compare updated service to whats expected
            steps.add(new CompareServiceToServiceMetadataStep(TEST_DIR, newMDFile));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return steps;
    }
}
