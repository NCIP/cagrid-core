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

  public org.cagrid.gaards.csm.bean.Application[] getApplications(org.cagrid.gaards.csm.bean.ApplicationSearchCriteria applicationSearchCriteria) throws RemoteException, org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
		return null;
	}

}
