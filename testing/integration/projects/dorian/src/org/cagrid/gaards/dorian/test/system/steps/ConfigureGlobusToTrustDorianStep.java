package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.security.cert.X509Certificate;

import org.cagrid.gaards.dorian.service.BeanUtils;
import org.cagrid.gaards.dorian.service.Dorian;
import org.cagrid.gaards.dorian.service.DorianProperties;
import org.cagrid.gaards.pki.CertUtil;
import org.springframework.core.io.FileSystemResource;

public class ConfigureGlobusToTrustDorianStep extends Step {

	private ServiceContainer container;
	private File caFile;
	private File policyFile;

	public ConfigureGlobusToTrustDorianStep(ServiceContainer container) {
		this.container = container;
	}

	public void runStep() throws Throwable {
		File conf = new File(this.container.getProperties()
				.getContainerDirectory().getAbsolutePath()
				+ File.separator
				+ "webapps"
				+ File.separator
				+ "wsrf"
				+ File.separator
				+ "WEB-INF"
				+ File.separator
				+ "etc"
				+ File.separator
				+ "cagrid_Dorian"
				+ File.separator
				+ "dorian-configuration.xml");
		File props = new File(this.container.getProperties()
				.getContainerDirectory().getAbsolutePath()
				+ File.separator
				+ "webapps"
				+ File.separator
				+ "wsrf"
				+ File.separator
				+ "WEB-INF"
				+ File.separator
				+ "etc"
				+ File.separator
				+ "cagrid_Dorian"
				+ File.separator
				+ "dorian.properties");
		BeanUtils utils = new BeanUtils(new FileSystemResource(conf),
				new FileSystemResource(props));
		DorianProperties c = utils.getDorianProperties();
		c.getIdentityFederationProperties()
				.setAutoHostCertificateApproval(true);
		Dorian dorian = new Dorian(c, "https://localhost", true);
		X509Certificate cacert = dorian.getCACertificate();
	
		File dir = new File(this.container.getProperties()
				.getContainerDirectory().getAbsolutePath()
				+ File.separator + "certificates/ca");
		caFile = new File(dir.getAbsolutePath() + File.separator
				+ CertUtil.getHashCode(cacert) + ".0");
		policyFile = new File(dir.getAbsolutePath() + File.separator
				+ CertUtil.getHashCode(cacert) + ".signing_policy");
		CertUtil.writeCertificate(cacert, caFile);
		CertUtil.writeSigningPolicy(cacert, policyFile);
	}

	public void cleanup() {
		if (caFile != null) {
			caFile.delete();
		}
		if (policyFile != null) {
			policyFile.delete();
		}
	}
}
