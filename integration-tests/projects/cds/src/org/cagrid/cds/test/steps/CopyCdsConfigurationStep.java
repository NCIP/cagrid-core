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
package org.cagrid.cds.test.steps;

import java.io.File;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

public class CopyCdsConfigurationStep extends Step {

	private File serviceDir;
	private File properties;

	public CopyCdsConfigurationStep(File serviceDir, File properties) {
		this.serviceDir = serviceDir;
		this.properties = properties;
	}

	public void runStep() throws Throwable {
		if (this.properties != null) {
			Utils.copyFile(this.properties, new File(this.serviceDir
					.getAbsolutePath()
					+ File.separator
					+ "etc"
					+ File.separator
					+ "cds.properties"));
		}
	}
}
