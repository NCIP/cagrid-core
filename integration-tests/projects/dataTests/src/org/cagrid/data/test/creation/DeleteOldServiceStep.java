package org.cagrid.data.test.creation;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/** 
 *  DeleteOldServiceStep
 *  This step deletes an old service's directory from the file system
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 22, 2006 
 * @version $Id: DeleteOldServiceStep.java,v 1.1 2008-05-16 19:25:25 dervin Exp $ 
 */
public class DeleteOldServiceStep extends Step {
    private TestCaseInfo serviceInfo;
	
	public DeleteOldServiceStep(TestCaseInfo serviceInfo) {
		super();
        this.serviceInfo = serviceInfo;
	}
	

	public void runStep() throws Throwable {
		File oldServiceDir = new File(serviceInfo.getDir());
		if (oldServiceDir.exists()) {
			boolean deleted = Utils.deleteDir(oldServiceDir);
			assertTrue("Failed to delete directory: " + oldServiceDir.getAbsolutePath(), deleted);
		} else {
			System.out.println("Service dir " + oldServiceDir.getAbsolutePath() + " did not exist...");
		}
	}
}
