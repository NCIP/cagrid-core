package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;

import java.io.File;

public class RemoveSkeletonStep extends BaseStep {
	private TestCaseInfo tci;

	public RemoveSkeletonStep(TestCaseInfo tci) throws Exception {
		super(tci.getDir(), false);
		this.tci = tci;
	}
    

	public void runStep() throws Throwable {
		System.out.println("Removing the service skeleton");

		Utils.deleteDir(new File(getBaseDir()
				+ File.separator + tci.getDir()));
		//assertTrue(results);
	}
}
