/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
