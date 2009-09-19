/*
 * Created on Jul 14, 2006
 */
package gov.nci.nih.cagrid.tests.core.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;

import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.pki.CertUtil;

/**
 * This step downloads a dorian CA and writes it to a specified location, which
 * is presumably the globus CA directory
 * (user.home/globus/certificates/someFile_ca.#).
 * 
 * @author Patrick McConnell
 */
public class DorianAddTrustedCAStep extends Step {
	private File caFile;
	private String serviceURL;

	public DorianAddTrustedCAStep(File caFile, String serviceURL) {
		super();

		this.caFile = caFile;
		this.serviceURL = serviceURL;
	}

	@Override
	public void runStep() throws Throwable {
		GridUserClient client = new GridUserClient(this.serviceURL);
		CertUtil.writeCertificate(client.getCACertificate(), this.caFile);
	}
}
