package org.cagrid.gaards.dorian.test.system.steps;

import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.security.cert.X509Certificate;

import org.cagrid.gaards.authentication.test.system.steps.SigningCertificateProxy;
import org.cagrid.gaards.dorian.idp.AssertionCredentialsManager;
import org.cagrid.gaards.dorian.service.BeanUtils;
import org.springframework.core.io.FileSystemResource;

public class GetAsserionSigningCertificateStep extends Step implements SigningCertificateProxy{

	private ServiceContainer container;
	private X509Certificate signingCertificate;

	public GetAsserionSigningCertificateStep(ServiceContainer container) {
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
		BeanUtils utils = new BeanUtils(new FileSystemResource(conf), new FileSystemResource(props));
		AssertionCredentialsManager acm = utils.getAssertionCredentialsManager();
		this.signingCertificate = acm.getIdPCertificate();
	}

	public X509Certificate getSigningCertificate() {
		return signingCertificate;
	}
	
	

}
