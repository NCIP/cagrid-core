package gov.nih.nci.cagrid.workflow.tests.sample1.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class WorkflowTestService1Impl extends WorkflowTestService1ImplBase {

	
	public WorkflowTestService1Impl() throws RemoteException {
		super();
	}
	
  public java.lang.String invoke(java.lang.String invokeInput) throws RemoteException {
        String returnString = "TestService1 got: ";
        if (invokeInput != null) {
            returnString = returnString + invokeInput;
        }
        return returnString;
	}

}

