package org.cagrid.transfer.system.test.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class TransferSystemTestImpl extends TransferSystemTestImplBase {

	
	public TransferSystemTestImpl() throws RemoteException {
		super();
	}
	
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference createTransferMethodStep() throws RemoteException {
    return org.cagrid.transfer.context.service.helper.TransferServiceHelper.createTransferContext(null, (org.cagrid.transfer.context.service.helper.DataStagedCallback)null);
  }

}

