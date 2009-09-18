package gov.nih.nci.cagrid.workflow.service;

import gov.nih.nci.cagrid.workflow.common.SampleService1I;
import gov.nih.nci.cagrid.workflow.service.globus.ServiceConfiguration;

import java.rmi.RemoteException;

import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;

/**
 * gov.nih.nci.cagrid.workflowI TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class SampleService1Impl implements SampleService1I {
	private ServiceConfiguration configuration;

	public SampleService1Impl() throws RemoteException {

	}

	public ServiceConfiguration getConfiguration() throws Exception {
		if (this.configuration != null) {
			return this.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath
				+ "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			this.configuration = (ServiceConfiguration) initialContext
					.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.",
					e);
		}

		return this.configuration;
	}

	public java.lang.String invoke(java.lang.String param)
			throws RemoteException {
		String returnString = null;
		if (param != null) {
			returnString = "Service 1 got : " + param;
		}
		return returnString;
	}

}
