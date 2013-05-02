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
package org.cagrid.transfer.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class TransferServiceImpl extends TransferServiceImplBase {

	
	public TransferServiceImpl() throws RemoteException {
		super();
		try {
		    //TODO: calling at startup to ensure service configuration is loaded prior
		    //to use by any service from another context
            TransferServiceConfiguration.getConfiguration();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	

}

