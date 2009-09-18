package org.cagrid.transfer.service;

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
public class TransferServiceConfiguration implements ServiceConfiguration {

	public static TransferServiceConfiguration  configuration = null;
    public String etcDirectoryPath;
    	
	public static TransferServiceConfiguration getConfiguration() throws Exception {
		if (TransferServiceConfiguration.configuration != null) {
			return TransferServiceConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			TransferServiceConfiguration.configuration = (TransferServiceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return TransferServiceConfiguration.configuration;
	}
	

	
	private String transferServletPathName;
	
	private String storageDirectory;
	
	private String defaultTransferServiceContextTerminationTimeInMinutes;
	
	
    public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}


	
	public String getTransferServletPathName() {
		return transferServletPathName;
	}
	
	
	public void setTransferServletPathName(String transferServletPathName) {
		this.transferServletPathName = transferServletPathName;
	}

	
	public String getStorageDirectory() {
		return ContainerConfig.getBaseDirectory() + File.separator + storageDirectory;
	}
	
	
	public void setStorageDirectory(String storageDirectory) {
		this.storageDirectory = storageDirectory;
	}

	
	public String getDefaultTransferServiceContextTerminationTimeInMinutes() {
		return defaultTransferServiceContextTerminationTimeInMinutes;
	}
	
	
	public void setDefaultTransferServiceContextTerminationTimeInMinutes(String defaultTransferServiceContextTerminationTimeInMinutes) {
		this.defaultTransferServiceContextTerminationTimeInMinutes = defaultTransferServiceContextTerminationTimeInMinutes;
	}

	
}
