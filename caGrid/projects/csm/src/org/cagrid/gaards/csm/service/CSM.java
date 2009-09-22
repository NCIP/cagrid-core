package org.cagrid.gaards.csm.service;

import gov.nih.nci.security.AuthorizationManager;

import java.rmi.RemoteException;
import java.util.List;

import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;

public class CSM {

	private CSMProperties conf;
	private AuthorizationManager auth;

	public CSM(CSMProperties conf) throws Exception {
		this.conf = conf;
		this.auth = CSMInitializer.getAuthorizationManager(this.conf
				.getDatabaseProperties());
	}

	public List<Application> getApplications(
			ApplicationSearchCriteria applicationSearchCriteria)
			throws RemoteException,
			org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
		List<gov.nih.nci.security.authorization.domainobjects.Application> apps = this.auth
				.getObjects(CSMUtils.convert(applicationSearchCriteria));
		return CSMUtils.convert(apps);
	}

}
