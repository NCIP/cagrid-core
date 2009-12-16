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
