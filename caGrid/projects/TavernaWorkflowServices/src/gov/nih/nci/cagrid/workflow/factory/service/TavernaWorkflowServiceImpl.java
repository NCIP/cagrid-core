package gov.nih.nci.cagrid.workflow.factory.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.workflow.service.impl.service.globus.resource.TavernaWorkflowServiceImplResource;
import gov.nih.nci.cagrid.workflow.service.impl.service.globus.resource.TavernaWorkflowServiceImplResourceHome;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.utils.AddressingUtils;

import workflowmanagementfactoryservice.WMSOutputType;

import java.rmi.RemoteException;
import java.util.Calendar;

import javax.naming.Context;
import javax.naming.InitialContext;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class TavernaWorkflowServiceImpl extends TavernaWorkflowServiceImplBase {

	public TavernaWorkflowServiceImpl() throws RemoteException {
		super();
	}

  public workflowmanagementfactoryservice.WMSOutputType createWorkflow(workflowmanagementfactoryservice.WMSInputType wMSInputElement) throws RemoteException, gov.nih.nci.cagrid.workflow.factory.stubs.types.WorkflowException {

	  TavernaWorkflowServiceImplResourceHome home = null;
		ResourceKey key = null;
		int TERM_TIME = 120;
		try {
			Context ctx = new InitialContext();
			String lookupString = Constants.JNDI_SERVICES_BASE_NAME +
			"cagrid/TavernaWorkflowServiceImpl"+ "/home";
			home = (TavernaWorkflowServiceImplResourceHome) ctx.lookup(lookupString);

			key = home.createResource();

			TavernaWorkflowServiceImplResource workflowResource = home.getResource(key);

			EndpointReferenceType epr = AddressingUtils.createEndpointReference(ServiceHost
					.getBaseURL() + "cagrid/TavernaWorkflowServiceImpl", key);
			//System.out.println("EPR :" + epr.getAddress().toString());
			
			
//			Calendar termTime = Calendar.getInstance();
//	        termTime.add(Calendar.MINUTE, TERM_TIME);
//	        workflowResource.setTerminationTime(termTime);
	        
			workflowResource.createWorkflow(wMSInputElement);
			WMSOutputType wMSOutputElement = new WMSOutputType();
			wMSOutputElement.setWorkflowEPR(epr);
			return wMSOutputElement;
			
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		return null;  }

}

