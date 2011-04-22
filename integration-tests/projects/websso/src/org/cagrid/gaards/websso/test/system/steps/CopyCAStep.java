package org.cagrid.gaards.websso.test.system.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

public class CopyCAStep extends Step {
	private File outPutDir;
	private ServiceContainer certsContainer;

	public CopyCAStep(ServiceContainer certsContainer, File outPutDir) {
		this.certsContainer = certsContainer;
		this.outPutDir = outPutDir;
	}

	public void runStep() throws Throwable {
		System.out.println("Copying CA certificates to services dir");

		File inPutDir = new File(certsContainer.getProperties().getContainerDirectory(), "certificates" + File.separator + "ca");
		Utils.copyDirectory(inPutDir, outPutDir);
	}

}
