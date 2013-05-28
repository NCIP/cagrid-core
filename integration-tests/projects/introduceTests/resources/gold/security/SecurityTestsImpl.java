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
package org.cagrid.introduce.security.tests.service;

import gov.nih.nci.cagrid.common.Utils;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class SecurityTestsImpl extends SecurityTestsImplBase {

	
	public SecurityTestsImpl() throws RemoteException {
		super();
	}
	
  public java.lang.String anonPrefered() throws RemoteException {
    return org.cagrid.introduce.security.tests.service.globus.SecurityTestsAuthorization.getCallerIdentity();
  }

  public java.lang.String anonNotPrefered() throws RemoteException {
      return org.cagrid.introduce.security.tests.service.globus.SecurityTestsAuthorization.getCallerIdentity();
  }

}

