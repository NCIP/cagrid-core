package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.testing.system.haste.Story;

import java.io.File;
import java.util.Vector;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/** 
 *  Sdk4XmiToDomainModelTestStory
 *  Tests converting various SDK 4.0 XMI models to a domain model
 * 
 * @author David Ervin
 * 
 * @created Oct 24, 2007 11:48:02 AM
 * @version $Id: Sdk4XmiToDomainModelStory.java,v 1.3 2009-01-30 21:31:56 dervin Exp $ 
 */
public class Sdk4XmiToDomainModelStory extends Story {
    public static final String XMI_ZIP_FILE = "test/resources/sdk4/sdk4exampleXMI.zip";
    public static final String XMI_UNPACK_DIR = "test/tmp/sdk4/xmi";
    public static final String GOLD_MODELS_ZIP_FILE = "test/resources/sdk4/sdk4goldModels.zip";
    public static final String GOLD_MODELS_UNPACK_DIR = "test/tmp/sdk4/goldmodels";
    
    public static final String PROJECT_SHORT_NAME = "example";
    public static final String PROJECT_VERSION = "4.0";

    public Sdk4XmiToDomainModelStory() {
        setName("SDK 4.0 XMI to Domain Model Story");
    }


    public String getDescription() {
        return "Tests converting SDK 4_0 XMI models to a domain model";
    }
    
    
    public String getName() {
        return "SDK 4_0 XMI to Domain Model Story";
    }


    protected Vector steps() {
        Vector<Step> steps = new Vector<Step>();
        // unpack XMI
        steps.add(new UnzipModelsStep(XMI_ZIP_FILE, XMI_UNPACK_DIR));
        String eaModel = XMI_UNPACK_DIR + File.separator + "EAexample.xmi";
        String argoModel = XMI_UNPACK_DIR + File.separator + "ArgoUMLexample.uml";
        // unpack Gold models
        steps.add(new UnzipModelsStep(GOLD_MODELS_ZIP_FILE, GOLD_MODELS_UNPACK_DIR));
        String goldEaDomain = GOLD_MODELS_UNPACK_DIR + File.separator + "eaDomainModel.xml";
        String goldArgoDomain = GOLD_MODELS_UNPACK_DIR + File.separator + "argoDomainModel.xml";
        String generatedEaDomain = GOLD_MODELS_UNPACK_DIR + File.separator + "generatedEaDomainModel.xml";
        String generatedArgoDomain = GOLD_MODELS_UNPACK_DIR + File.separator + "generatedArgoDomainModel.xml";
        // convert the EA model
        steps.add(new GenericXmiToModelStep(
            eaModel, generatedEaDomain, XmiFileType.SDK_40_EA,
            PROJECT_SHORT_NAME, PROJECT_VERSION));
        // convert the Argo model
        steps.add(new GenericXmiToModelStep(
            argoModel, generatedArgoDomain, XmiFileType.SDK_40_ARGO,
            PROJECT_SHORT_NAME, PROJECT_VERSION));
        // compare the EA model
        steps.add(new GenericDomainModelComparisonStep(goldEaDomain, generatedEaDomain));
        // compare the Argo model
        steps.add(new GenericDomainModelComparisonStep(goldArgoDomain, generatedArgoDomain));
        // clean up dirs
        steps.add(new DeleteModelTempStep(XMI_UNPACK_DIR));
        steps.add(new DeleteModelTempStep(GOLD_MODELS_UNPACK_DIR));
        return steps;
    }
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(Sdk4XmiToDomainModelStory.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
