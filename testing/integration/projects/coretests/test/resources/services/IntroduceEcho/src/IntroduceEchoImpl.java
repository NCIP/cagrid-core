package gov.nih.nci.cagrid.tests.service;

import java.rmi.RemoteException;

/** 
 *  TODO:DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.0
 * 
 */
public class IntroduceEchoImpl extends IntroduceEchoImplBase {

	
	public IntroduceEchoImpl() throws RemoteException {
		super();
	}
	
	public java.lang.String echo(java.lang.String value) throws RemoteException {
		return value;
	}

}

