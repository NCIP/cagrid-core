package gov.nih.nci.cagrid.workflow.tests.sample2.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class WorkflowTestService2Impl extends WorkflowTestService2ImplBase {

	
	public WorkflowTestService2Impl() throws RemoteException {
		super();
	}
	
  public java.lang.String invoke(java.lang.String invokeInput) throws RemoteException {
        String returnString = "Test Service2 Got: " ;
        if (invokeInput != null) {
            returnString = returnString + invokeInput;
        }
        return returnString;
	}

}

