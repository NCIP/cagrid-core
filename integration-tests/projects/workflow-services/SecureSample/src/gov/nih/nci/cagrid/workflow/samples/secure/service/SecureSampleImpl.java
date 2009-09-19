package gov.nih.nci.cagrid.workflow.samples.secure.service;

import gov.nih.nci.cagrid.workflow.samples.secure.service.globus.ServiceConfiguration;

import java.rmi.RemoteException;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;

/** 
 *  gov.nih.nci.cagrid.workflow.samples.secureI
 *  TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class SecureSampleImpl {
    private ServiceConfiguration configuration;
	
	public SecureSampleImpl() throws RemoteException {
	
	}
	
	public ServiceConfiguration getConfiguration() throws Exception {
		if (this.configuration != null) {
			return this.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			this.configuration = (ServiceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return this.configuration;
	}


	     public java.lang.String invoke(java.lang.String string) throws RemoteException {
        String retString = "Got this from client: " + string;
        return retString;
	}

}

