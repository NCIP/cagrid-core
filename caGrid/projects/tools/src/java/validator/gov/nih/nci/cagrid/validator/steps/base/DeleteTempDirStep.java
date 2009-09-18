package gov.nih.nci.cagrid.validator.steps.base;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  DeleteTempDirStep
 *  Step deletes a temporary directory
 * 
 * @author David Ervin
 * 
 * @created Aug 28, 2007 11:31:24 AM
 * @version $Id: DeleteTempDirStep.java,v 1.1 2008-03-25 14:20:30 dervin Exp $ 
 */
public class DeleteTempDirStep extends Step {
    
    File tempDir;

    public DeleteTempDirStep(File tempDir) {
        this.tempDir = tempDir;
    }
    
    
    public String getName() {
        return "Delete temp dir " + tempDir.getAbsolutePath();
    }


    public void runStep() throws Throwable {
        if (tempDir.exists()) {
            Utils.deleteDir(tempDir);
        }
    }
}
