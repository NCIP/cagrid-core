package gov.nih.nci.cagrid.workflow.service.impl.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public interface TavernaWorkflowServiceImplI {

  public org.oasis.wsrf.lifetime.DestroyResponse destroy(org.oasis.wsrf.lifetime.Destroy params) throws RemoteException ;

  public org.oasis.wsrf.lifetime.SetTerminationTimeResponse setTerminationTime(org.oasis.wsrf.lifetime.SetTerminationTime params) throws RemoteException ;

  /**
   * Cancels the execution of a workflow
   *
   * @throws CannotCancelWorkflowFault
   *	
   */
  public void cancel() throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.CannotCancelWorkflowFault ;

  /**
   * Gives a detailed status of the workflow
   *
   * @throws WorkflowException
   *	
   */
  public workflowmanagementfactoryservice.WorkflowStatusEventType[] getDetailedStatus() throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.WorkflowException ;

  /**
   * Returns status of the workflow
   *
   * @throws WorkflowException
   *	
   */
  public workflowmanagementfactoryservice.WorkflowStatusType getStatus() throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.WorkflowException ;

  /**
   * Returns the output of the workflow
   *
   * @throws WorkflowException
   *	
   */
  public workflowmanagementfactoryservice.WorkflowOutputType getWorkflowOutput() throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.WorkflowException ;

  /**
   * Pause the workflow execution
   *
   * @throws WorkflowException
   *	
   * @throws CannotPauseWorkflowFault
   *	
   */
  public workflowmanagementfactoryservice.WorkflowStatusType pause() throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.WorkflowException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.CannotPauseWorkflowFault ;

  /**
   * Resumes a paused workflow
   *
   * @throws WorkflowException
   *	
   * @throws CannotResumeWorkflowFault
   *	
   */
  public workflowmanagementfactoryservice.WorkflowStatusType resume() throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.WorkflowException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.CannotResumeWorkflowFault ;

  /**
   * starts the workflow
   *
   * @param startInputElement
   * @throws CannotStartWorkflowFault
   *	
   */
  public workflowmanagementfactoryservice.WorkflowStatusType start(workflowmanagementfactoryservice.StartInputType startInputElement) throws RemoteException, gov.nih.nci.cagrid.workflow.service.impl.stubs.types.CannotStartWorkflowFault ;

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException ;

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException ;

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException ;

  public org.oasis.wsn.SubscribeResponse subscribe(org.oasis.wsn.Subscribe params) throws RemoteException ;

}

