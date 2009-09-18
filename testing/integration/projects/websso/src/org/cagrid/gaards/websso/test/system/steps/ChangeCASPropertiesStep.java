package org.cagrid.gaards.websso.test.system.steps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import gov.nih.nci.cagrid.testing.system.haste.Step;

/**
 * Change cas.properties in websso project
 * @author garmillas
 *
 */
public class ChangeCASPropertiesStep extends Step {
	
	private File tempWebSSOService;
	private String webSSOURL;

	public ChangeCASPropertiesStep(File tempWebSSOService,String webSSOURL){
		this.tempWebSSOService=tempWebSSOService;
		this.webSSOURL=webSSOURL;
	}

	@Override
	public void runStep() throws Throwable {

		String casTemplatePropertiesFile = tempWebSSOService.getCanonicalPath()
				+ File.separator + "webcontent" + File.separator + "WEB-INF"
				+ File.separator + "cas-template.properties";
		
		String casPropertiesFile = tempWebSSOService.getCanonicalPath()
		+ File.separator + "webcontent" + File.separator + "WEB-INF"
		+ File.separator + "cas.properties";

		Properties casClientProperties = new Properties();
			casClientProperties.load(new FileInputStream(
					casTemplatePropertiesFile));
		
		casClientProperties.setProperty("cas.securityContext.serviceProperties.service", webSSOURL+"/services/j_acegi_cas_security_check");
		casClientProperties.setProperty("cas.securityContext.casProcessingFilterEntryPoint.loginUrl", webSSOURL+"/login");
		casClientProperties.setProperty("cas.securityContext.ticketValidator.casServerUrlPrefix", webSSOURL);
		
		casClientProperties.store(new FileOutputStream(
				casPropertiesFile), null);
	}

	public static void main(String[] args) throws Throwable {

		File tempWebSSOService = new File("../../../caGrid/projects/websso");
		ChangeCASPropertiesStep step = new ChangeCASPropertiesStep(tempWebSSOService,"https://localhost:8080");
		step.runStep();
	}
}
