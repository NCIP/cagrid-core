package org.cagrid.gaards.authentication.service;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

import org.globus.wsrf.config.ContainerConfig;
import java.io.File;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 * 
 * This class holds all service properties which were defined for the service to have
 * access to.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class AuthenticationServiceConfiguration implements ServiceConfiguration {

	public static AuthenticationServiceConfiguration  configuration = null;
    public String etcDirectoryPath;
    	
	public static AuthenticationServiceConfiguration getConfiguration() throws Exception {
		if (AuthenticationServiceConfiguration.configuration != null) {
			return AuthenticationServiceConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			AuthenticationServiceConfiguration.configuration = (AuthenticationServiceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return AuthenticationServiceConfiguration.configuration;
	}
	

	
	private String authenticationConfiguration;
	
	private String authenticationProperties;
	
	
    public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}


	
	public String getAuthenticationConfiguration() {
		return ContainerConfig.getBaseDirectory() + File.separator + authenticationConfiguration;
	}
	
	
	public void setAuthenticationConfiguration(String authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
	}

	
	public String getAuthenticationProperties() {
		return ContainerConfig.getBaseDirectory() + File.separator + authenticationProperties;
	}
	
	
	public void setAuthenticationProperties(String authenticationProperties) {
		this.authenticationProperties = authenticationProperties;
	}

	
}
