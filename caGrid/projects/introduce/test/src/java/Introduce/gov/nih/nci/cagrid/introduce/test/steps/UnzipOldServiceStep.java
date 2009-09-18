package gov.nih.nci.cagrid.introduce.test.steps;

import gov.nih.nci.cagrid.common.ZipUtilities;
import gov.nih.nci.cagrid.introduce.test.TestCaseInfo;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

public class UnzipOldServiceStep extends Step {

	private TestCaseInfo tci;
	private String serviceZipFile;

	public UnzipOldServiceStep(String serviceZipFile, TestCaseInfo tci)
			throws Exception {
		super();
		this.tci = tci;
		this.serviceZipFile = serviceZipFile;
	}

	public void runStep() throws Throwable {
		System.out.println("Unzipping old service");
		File zipDir = new File(tci.getDir());
		zipDir.mkdir();
		ZipUtilities.unzip(new File(serviceZipFile), zipDir);
	}

}
