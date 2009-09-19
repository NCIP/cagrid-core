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
public class ChangeJasigCASClientPropertiesStep extends Step {
	
	private File tempWebSSOClientService;
	private String webSSOServerURL;
	private String webSSOClientJasigURL;

	public ChangeJasigCASClientPropertiesStep(File tempWebSSOClientService,
			String webSSOServerURL, String webSSOClientJasigURL) {
		this.tempWebSSOClientService = tempWebSSOClientService;
		this.webSSOServerURL = webSSOServerURL;
		this.webSSOClientJasigURL = webSSOClientJasigURL;
	}

	@Override
	public void runStep() throws Throwable {
		String casTemplatePropertiesFile = tempWebSSOClientService.getCanonicalPath()
				+ File.separator + "ext" + File.separator + "dependencies-properties"
				+ File.separator + "properties"+File.separator+"cas-client-template-jasig-"+WebSSOSystemTest.getProjectVersion()+".properties";

		Properties casClientProperties = new Properties();
		casClientProperties
				.load(new FileInputStream(casTemplatePropertiesFile));
		
		casClientProperties.setProperty("cas.server.url", webSSOServerURL+"/");
		casClientProperties.setProperty("cas.client.service", webSSOClientJasigURL);
		casClientProperties.setProperty("logout.landing.url", "www.google.com");
		
		casClientProperties.store(new FileOutputStream(casTemplatePropertiesFile), null);
	}

	public static void main(String[] args) throws Throwable {
		File tempWebSSOService = new File("C:/devroot/caGrid/cagrid-1-0/tests/projects/websso/tmp/websso-client-example");
		ChangeJasigCASClientPropertiesStep step = new ChangeJasigCASClientPropertiesStep(
				tempWebSSOService, "https://localhost:8080", "test");
		step.runStep();
	}
}