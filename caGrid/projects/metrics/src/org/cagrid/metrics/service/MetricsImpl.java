package org.cagrid.metrics.service;

import java.rmi.RemoteException;

import org.apache.axis.MessageContext;
import org.cagrid.metrics.common.Event;
import org.globus.wsrf.security.SecurityManager;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.1
 * 
 */
public class MetricsImpl extends MetricsImplBase {

	public MetricsImpl() throws RemoteException {
		super();
	}

	private String getCallerIdentity() {
		return SecurityManager.getManager().getCaller();
	}

  public void report(org.cagrid.metrics.common.EventSubmission submission) throws RemoteException {
		String callerIP = (String) MessageContext.getCurrentContext()
				.getProperty("remoteaddr");
		int count = 0;
		if (submission != null) {
			Event[] event = submission.getEvent();
			if (event != null) {
				count = event.length;
			}
		}
		System.out.println("An event submission containing " + count
				+ " event(s) was received from " + callerIP + ".");
	}

}
