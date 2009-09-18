package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  XmiToDomainModelTestStory
 *  Tests converting various XMI models to a domain model
 * 
 * @author David Ervin
 * 
 * @created Oct 24, 2007 11:48:02 AM
 * @version $Id: XmiToDomainModelTestStory.java,v 1.3 2007-12-03 16:27:18 hastings Exp $ 
 */
public class XmiToDomainModelTestStory extends Story {
    public static final String MODEL_ZIP_FILE = "test/resources/models.zip";
    public static final String MODEL_UNPACK_DIR = "test/tmp/models";

    public XmiToDomainModelTestStory() {
        setName("XMI to Domain Model Story");
    }


    public String getDescription() {
        return "Tests converting various XMI models to a domain model";
    }
    
    
    public String getName() {
        return "XMI to Domain Model Story";
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        steps.add(new UnzipModelsStep(MODEL_ZIP_FILE, MODEL_UNPACK_DIR));
        steps.add(new ModelConversionStep(MODEL_UNPACK_DIR));
        steps.add(new ModelComparisonStep(MODEL_UNPACK_DIR));
        steps.add(new DeleteModelTempStep(MODEL_UNPACK_DIR));
        return steps;
    }
    
    
    // used to make sure that if we are going to use a junit testsuite to
    // test this that the test suite will not error out
    // looking for a single test......
    public void testDummy() throws Throwable {
    }


    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(XmiToDomainModelTestStory.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
