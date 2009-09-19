package org.cagrid.gaards.authentication.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

public class CopyConfigurationStep extends Step {

	private File serviceDir;
	private File configuration;
	private File properties;

	public CopyConfigurationStep(File serviceDir, File configuration,
			File properties) {
		this.serviceDir = serviceDir;
		this.configuration = configuration;
		this.properties = properties;
	}

	public void runStep() throws Throwable {
		Utils.copyFile(this.configuration, new File(this.serviceDir
				.getAbsolutePath()
				+ File.separator
				+ "etc"
				+ File.separator
				+ "authentication-config.xml"));
		Utils.copyFile(this.properties, new File(this.serviceDir
				.getAbsolutePath()
				+ File.separator
				+ "etc"
				+ File.separator
				+ "authentication.properties"));

	}

}
