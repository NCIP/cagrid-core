package gov.nih.nci.cagrid.workflow.tests.sample1.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public interface WorkflowTestService1I {

    public gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata getServiceSecurityMetadata() throws RemoteException ;

  public java.lang.String invoke(java.lang.String invokeInput) throws RemoteException ;

}

