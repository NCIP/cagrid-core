package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.cagrid.gaards.websso.test.system.WebSSOSystemTest;



import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * Modify cas-client properties file in WebSSO-client project and websso-client-acegi project
 * @author garmillas
 *
 */
public class ChangeAcegiCASClientPropertiesStep extends Step {
	
	private File tempWebSSOClientService;
	private String webSSOServerURL;
	private String webSSOClientAcegiURL;
	private String acegiHostCertificate;
	private String acegiHostCertificateKey;
	private int httpPort;
	private int httpsPort;
	

	public ChangeAcegiCASClientPropertiesStep(File tempWebSSOClientService,
			String webSSOServerURL,String webSSOClientAcegiURL,
			String acegiHostCertificate,String acegiHostCertificateKey,int httpport,int httpsport ) {
		this.tempWebSSOClientService = tempWebSSOClientService;
		this.webSSOServerURL = webSSOServerURL;
		this.webSSOClientAcegiURL = webSSOClientAcegiURL;
		this.acegiHostCertificate=acegiHostCertificate;
		this.acegiHostCertificateKey=acegiHostCertificateKey;
		this.httpPort=httpport;
		this.httpsPort=httpsport;		
	}

	@Override
	public void runStep() throws Throwable {
		String casTemplatePropertiesFile = tempWebSSOClientService
				.getCanonicalPath()
				+ File.separator
				+ "ext"
				+ File.separator
				+ "dependencies-properties"
				+ File.separator
				+ "properties"
				+ File.separator + "cas-client-template-acegi-"+WebSSOSystemTest.getProjectVersion()+".properties";

		Properties casClientProperties = new Properties();
		casClientProperties
				.load(new FileInputStream(casTemplatePropertiesFile));

		casClientProperties
				.setProperty("cas.server.url", webSSOServerURL);
		casClientProperties.setProperty("cas.client.service",webSSOClientAcegiURL);
		casClientProperties.setProperty("logout.landing.url", "www.google.com");
		casClientProperties.setProperty("host.credential.certificate", acegiHostCertificate);
		casClientProperties.setProperty("host.credential.key",acegiHostCertificateKey);
		casClientProperties.setProperty("http.port.number", ""+httpPort);
		casClientProperties.setProperty("https.port.number", ""+httpsPort);

		casClientProperties.store(new FileOutputStream(
				casTemplatePropertiesFile), null);
	}

	public static void main(String[] args) throws Throwable {
		File tempWebSSOService = new File("C:/devroot/caGrid/cagrid-1-0/tests/projects/websso/tmp/websso-client-example");
		ChangeAcegiCASClientPropertiesStep step = new ChangeAcegiCASClientPropertiesStep(tempWebSSOService,"https://localhost:8080","test","test","test",8080,8443);
		step.runStep();
	}
}
