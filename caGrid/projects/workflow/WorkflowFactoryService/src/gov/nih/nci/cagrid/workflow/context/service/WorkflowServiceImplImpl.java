package gov.nih.nci.cagrid.workflow.context.service;

import gov.nih.nci.cagrid.workflow.context.service.globus.resource.WorkflowServiceImplResource;
import gov.nih.nci.cagrid.workflow.context.stubs.types.CannotCancelWorkflowFault;
import gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException;

import java.rmi.RemoteException;

import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceKey;

/**
 * 
 */
public class WorkflowServiceImplImpl extends WorkflowServiceImplImplBase {

	public WorkflowServiceImplImpl() throws RemoteException {
		super();
	}

  public gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType start(gov.nih.nci.cagrid.workflow.stubs.types.StartInputType startInputElement) throws RemoteException, gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException, gov.nih.nci.cagrid.workflow.context.stubs.types.StartCalledOnStartedWorkflow {
		gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType status = null;
		WorkflowServiceImplResource resource = null;
		try {
			resource = getWorkflowResource();
			status = resource.start(startInputElement);
		} catch (ResourceException nsre) {
			throw new RemoteException("Exception starting workflow", nsre);
		}
		return status;
	}

	private WorkflowServiceImplResource getWorkflowResource() throws ResourceException, ResourceContextException {
		ResourceContext resourceContext = ResourceContext.getResourceContext();
		ResourceHome resourceHome = resourceContext.getResourceHome();
		ResourceKey resourceKey = resourceContext.getResourceKey();
		WorkflowServiceImplResource resource = (WorkflowServiceImplResource) resourceHome.find(resourceKey);
		return resource;
	}

  public gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType getStatus() throws RemoteException, gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException {
		return getWorkflowResource().getStatus();
	}

  public gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType pause() throws RemoteException, gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException, gov.nih.nci.cagrid.workflow.context.stubs.types.CannotPauseWorkflowFault {
		return getWorkflowResource().pause();
	}

  public gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusType resume() throws RemoteException, gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException, gov.nih.nci.cagrid.workflow.context.stubs.types.CannotResumeWorkflowFault {
		return getWorkflowResource().resume();
	}

  public void cancel() throws RemoteException, gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException, gov.nih.nci.cagrid.workflow.context.stubs.types.CannotCancelWorkflowFault {
		getWorkflowResource().cancel();
	}

  public gov.nih.nci.cagrid.workflow.stubs.types.WorkflowOutputType getWorkflowOutput() throws RemoteException, gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException {
		return getWorkflowResource().getWorkflowOutput();
	}
  public gov.nih.nci.cagrid.workflow.stubs.types.WorkflowStatusEventType[] getDetailedStatus() throws RemoteException, gov.nih.nci.cagrid.workflow.stubs.types.WorkflowException {
	  return getWorkflowResource().getDetailedStatus();
  }

}
