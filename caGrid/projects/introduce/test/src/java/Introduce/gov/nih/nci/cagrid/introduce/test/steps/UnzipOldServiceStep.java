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
