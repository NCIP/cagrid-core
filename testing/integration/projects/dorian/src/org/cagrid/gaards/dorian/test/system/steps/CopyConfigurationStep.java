package org.cagrid.gaards.dorian.test.system.steps;

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
		if (this.configuration != null) {
			Utils.copyFile(this.configuration, new File(this.serviceDir
					.getAbsolutePath()
					+ File.separator
					+ "etc"
					+ File.separator
					+ "dorian-configuration.xml"));
		}
		if (this.properties != null) {
			Utils.copyFile(this.properties, new File(this.serviceDir
					.getAbsolutePath()
					+ File.separator
					+ "etc"
					+ File.separator
					+ "dorian.properties"));
		}
	}

}
