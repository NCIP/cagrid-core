package org.cagrid.gaards.csm.service;

import java.rmi.RemoteException;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class CSMImpl extends CSMImplBase {

	public CSMImpl() throws RemoteException {
		super();
	}

	public java.lang.String getApplications() throws RemoteException,
			org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
		return "Hello";
	}

}
