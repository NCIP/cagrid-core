/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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

