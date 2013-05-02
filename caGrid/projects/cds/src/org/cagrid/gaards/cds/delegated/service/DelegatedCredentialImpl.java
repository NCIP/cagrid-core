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
package org.cagrid.gaards.cds.delegated.service;

import java.rmi.RemoteException;

import org.globus.wsrf.ResourceContext;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class DelegatedCredentialImpl extends DelegatedCredentialImplBase {

	public DelegatedCredentialImpl() throws RemoteException {
		super();
	}

  public org.cagrid.gaards.cds.common.CertificateChain getDelegatedCredential(org.cagrid.gaards.cds.common.PublicKey publicKey) throws RemoteException, org.cagrid.gaards.cds.stubs.types.CDSInternalFault, org.cagrid.gaards.cds.stubs.types.DelegationFault, org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault {
		DelegatedCredentialResource resource = (DelegatedCredentialResource) ResourceContext
				.getResourceContext().getResource();
		return resource.getDelegatedCredential(publicKey);
	}

}
