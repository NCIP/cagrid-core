package gov.nih.nci.cagrid.testing.system.deployment.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

/**
 * CopyCAStep Copies the CA cert to the service
 * 
 * @author Hastings
 * @author David
 */
public class CopyCAStep extends Step {
	private File serviceDir;
	private SecureContainer container;

	public CopyCAStep(SecureContainer container, File serviceDir) {
		this.container = container;
		this.serviceDir = serviceDir;
	}

	public void runStep() throws Throwable {
		System.out.println("Copying CA certificates to services dir");

		File inFileClient = new File(container.getCertificatesDirectory(), "ca" + File.separator
				+ "testing_ca_cert.0");
		File outFileClient = new File(serviceDir, "testing_ca_cert.0");
		Utils.copyFile(inFileClient, outFileClient);
		
		inFileClient = new File(container.getCertificatesDirectory(), "ca" + File.separator
				+ "testing_ca_cert.signing_policy");
		outFileClient = new File(serviceDir, "testing_ca_cert.signing_policy");
		Utils.copyFile(inFileClient, outFileClient);
	}
}
