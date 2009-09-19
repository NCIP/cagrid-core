package org.cagrid.data.style.test.cacore32;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.IOException;

import org.cagrid.data.test.creation.DataTestCaseInfo;

public class CopyEtcFilesStep extends Step {
    
    private DataTestCaseInfo testInfo = null;
    
    public CopyEtcFilesStep(DataTestCaseInfo testInfo) {
        this.testInfo = testInfo;
    }
    

    public void runStep() throws Throwable {
        File classToQnameFile = new File(Sdk32TestConstants.CLASS_TO_QNAME_FILENAME);
        copyFileToServiceEtc(classToQnameFile);
        File domainModelFile = new File(Sdk32TestConstants.DOMAIN_MODEL_FILENAME);
        copyFileToServiceEtc(domainModelFile);
    }
    
    
    private void copyFileToServiceEtc(File input) {
        File etcDir = new File(testInfo.getDir(), "etc");
        File output = new File(etcDir, input.getName());
        try {
            Utils.copyFile(input, output);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error copying " + input.getAbsolutePath() + " to service's etc directory: " + ex.getMessage());
        }
    }
}
