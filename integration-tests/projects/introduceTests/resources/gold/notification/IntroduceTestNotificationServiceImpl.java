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
package org.test.notification.service;

import java.rmi.RemoteException;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class IntroduceTestNotificationServiceImpl extends IntroduceTestNotificationServiceImplBase {

	
	public IntroduceTestNotificationServiceImpl() throws RemoteException {
		super();
	}
	
  public void setBook(projectmobius.org.BookType book) throws RemoteException {
    try {
        getResourceHome().getAddressedResource().setBook(book);
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
  }

}

