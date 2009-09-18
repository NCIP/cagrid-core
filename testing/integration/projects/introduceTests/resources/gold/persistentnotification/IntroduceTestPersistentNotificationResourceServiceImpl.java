package org.test.persistentnotification.resource.service;

import java.rmi.RemoteException;

import org.projectmobius.tools.gme.GetAuthority;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class IntroduceTestPersistentNotificationResourceServiceImpl extends IntroduceTestPersistentNotificationResourceServiceImplBase {

	
	public IntroduceTestPersistentNotificationResourceServiceImpl() throws RemoteException {
		super();
	}
	
  public void setBook(projectmobius.org.BookType book) throws RemoteException {
    try {
        getResourceHome().getAddressedResource().setBook(book);
    } catch (Exception e) {
        throw new RemoteException("error",e);
    }
  }

  public projectmobius.org.BookType getBook() throws RemoteException {
    try {
        return getResourceHome().getAddressedResource().getBook();
    } catch (Exception e) {
        throw new RemoteException("error",e);
    }
  }

}

