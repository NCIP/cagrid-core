package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  DeleteModelTempStep
 *  Deletes the temporary model directory
 * 
 * @author David Ervin
 * 
 * @created Oct 24, 2007 12:03:38 PM
 * @version $Id: DeleteModelTempStep.java,v 1.2 2007-12-03 16:27:18 hastings Exp $ 
 */
public class DeleteModelTempStep extends Step {
    
    private String tempDir;
    
    public DeleteModelTempStep(String tempDir) {
        this.tempDir = tempDir;
    }
    

    public void runStep() throws Throwable {
        File dir = new File(tempDir);
        Utils.deleteDir(dir);
    }
}
