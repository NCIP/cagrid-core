package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  UnzipModelsStep
 *  Unpacks the XMIs and Gold Domain Models
 * 
 * @author David Ervin
 * 
 * @created Oct 24, 2007 11:50:38 AM
 * @version $Id: UnzipModelsStep.java,v 1.2 2007-12-03 16:27:18 hastings Exp $ 
 */
public class UnzipModelsStep extends Step {
    
    private String modelsZipFile;
    private String destinationDir;
    
    public UnzipModelsStep(String modelsZipFile, String destinationDir) {
        this.modelsZipFile = modelsZipFile;
        this.destinationDir = destinationDir;
    }
    

    public void runStep() throws Throwable {
        File zipFile = new File(modelsZipFile);
        assertTrue("Model zip file not found", zipFile.exists());
        File destDir = new File(destinationDir);
        destDir.mkdirs();
        ZipUtilities.unzip(zipFile, destDir);
    }
}
