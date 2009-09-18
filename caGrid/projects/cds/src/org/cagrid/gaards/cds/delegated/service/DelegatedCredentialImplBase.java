package org.cagrid.gaards.cds.delegated.service;

import java.rmi.RemoteException;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.cagrid.gaards.cds.service.CredentialDelegationServiceConfiguration;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceHome;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * Provides some simple accessors for the Impl.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public abstract class DelegatedCredentialImplBase {
	
	public DelegatedCredentialImplBase() throws RemoteException {
	
	}
	
	public CredentialDelegationServiceConfiguration getConfiguration() throws Exception {
		return CredentialDelegationServiceConfiguration.getConfiguration();
	}
	
	
	
	
	public org.cagrid.gaards.cds.service.globus.resource.CredentialDelegationServiceResourceHome getCredentialDelegationServiceResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("credentialDelegationServiceHome");
		return (org.cagrid.gaards.cds.service.globus.resource.CredentialDelegationServiceResourceHome)resource;
	}
	
	
	protected ResourceHome getResourceHome(String resourceKey) throws Exception {
		MessageContext ctx = MessageContext.getCurrentContext();

		ResourceHome resourceHome = null;
		
		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + resourceKey;
		try {
			javax.naming.Context initialContext = new InitialContext();
			resourceHome = (ResourceHome) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate resource home. : " + resourceKey, e);
		}

		return resourceHome;
	}


}

