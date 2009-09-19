package org.cagrid.fqp.test.common.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  DeleteDirectoryStep
 *  Deletes a directory
 * 
 * @author David Ervin
 * 
 * @created Jul 9, 2008 3:55:19 PM
 * @version $Id: DeleteDirectoryStep.java,v 1.1 2008-07-09 21:04:08 dervin Exp $ 
 */
public class DeleteDirectoryStep extends Step {
    
    public File deleteMe;
    
    public DeleteDirectoryStep(File dir) {
        this.deleteMe = dir;
    }
    

    public void runStep() throws Throwable {
        assertTrue("Supplied file was not a directory (" + deleteMe.getAbsolutePath() + ")", deleteMe.isDirectory());
        Utils.deleteDir(deleteMe);
    }
}
