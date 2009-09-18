package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;

import gov.nih.nci.cagrid.testing.system.haste.Step;

public class CleanupGlobusCertificatesStep extends Step {

	public CleanupGlobusCertificatesStep() {
	}

	public void runStep() throws Throwable {
		String userHome = System.getProperty("user.home");
		String globusdirPath = userHome + File.separator + ".globus"
				+ File.separator + "certificates";
		File globusDir = new File(globusdirPath);
		if (globusDir.isDirectory()) {
			String[] fileNames = globusDir.list();
			for (String fileName : fileNames) {
				File file = new File(globusdirPath + File.separator + fileName);
				file.delete();
			}
		}
	}
	
	public static void main(String[] args) throws Throwable {
		CleanupGlobusCertificatesStep certificates=new CleanupGlobusCertificatesStep();
		certificates.runStep();
	}
}
