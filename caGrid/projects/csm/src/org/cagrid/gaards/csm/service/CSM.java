package org.cagrid.gaards.csm.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.security.AuthorizationManager;
import gov.nih.nci.security.exceptions.CSException;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.csm.bean.Application;
import org.cagrid.gaards.csm.bean.ApplicationSearchCriteria;
import org.cagrid.gaards.csm.stubs.types.AccessDeniedFault;
import org.cagrid.gaards.csm.stubs.types.CSMInternalFault;
import org.cagrid.gaards.csm.stubs.types.CSMTransactionFault;

public class CSM {

	private CSMProperties conf;
	private AuthorizationManager auth;
	private Log log;

	public CSM(CSMProperties conf) throws Exception {
		this.conf = conf;
		this.log = LogFactory.getLog(getClass().getName());
		this.auth = CSMInitializer.getAuthorizationManager(this.conf
				.getDatabaseProperties());
	}

	public void addWebServiceAdmin(String gridIdentity) throws CSMInternalFault {
		CSMInitializer.addWebServiceAdmin(auth, gridIdentity);
	}

	public List<Application> getApplications(
			ApplicationSearchCriteria applicationSearchCriteria)
			throws RemoteException,
			org.cagrid.gaards.csm.stubs.types.CSMInternalFault {
		List<gov.nih.nci.security.authorization.domainobjects.Application> apps = this.auth
				.getObjects(CSMUtils.convert(applicationSearchCriteria));
		return CSMUtils.convert(apps);
	}

	public Application createApplication(String callerIdentity, Application app)
			throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		checkWebServiceAdmin(callerIdentity);
		gov.nih.nci.security.authorization.domainobjects.Application a = CSMUtils
				.convert(app);
		try {
			this.auth.createApplication(a);
			gov.nih.nci.security.authorization.domainobjects.Application search = new gov.nih.nci.security.authorization.domainobjects.Application();
			search.setApplicationName(app.getName());
			List<gov.nih.nci.security.authorization.domainobjects.Application> apps = this.auth
					.getObjects(new gov.nih.nci.security.dao.ApplicationSearchCriteria(
							search));
			gov.nih.nci.security.authorization.domainobjects.Application created = apps
					.get(0);
			CSMInitializer
					.initializeApplication(auth, auth
							.getApplication(Constants.CSM_WEB_SERVICE_CONTEXT),
							created);
			return CSMUtils.convert(created);
		} catch (Exception e) {
			String error = "Error creating the application " + app.getName()
					+ ":\n" + e.getMessage();
			log.error(error, e);
			CSMTransactionFault fault = new CSMTransactionFault();
			fault.setFaultString(error);
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CSMTransactionFault) helper.getFault();
			throw fault;
		}
	}

	public void removeApplication(String callerIdentity, Long applicationId)
			throws CSMInternalFault, AccessDeniedFault, CSMTransactionFault {
		checkWebServiceAdmin(callerIdentity);
		try {
			auth.removeApplication(String.valueOf(applicationId));
		} catch (Exception e) {
			String error = "Error removing the application " + applicationId
					+ ":\n" + e.getMessage();
			log.error(error, e);
			CSMTransactionFault fault = new CSMTransactionFault();
			fault.setFaultString(error);
			FaultHelper helper = new FaultHelper(fault);
			helper.addFaultCause(e);
			fault = (CSMTransactionFault) helper.getFault();
			throw fault;
		}
	}

	private void checkWebServiceAdmin(String callerIdentity)
			throws CSMInternalFault, AccessDeniedFault {
		try {
			if (!auth.checkPermission(callerIdentity,
					Constants.CSM_WEB_SERVICE_CONTEXT,
					Constants.ADMIN_PRIVILEGE)) {
				AccessDeniedFault fault = new AccessDeniedFault();
				fault
						.setFaultString("You are not a CSM Web Service administrator!!!");
				throw fault;
			}
		} catch (CSException e) {
			log.error(e.getMessage(), e);
			CSMInternalFault fault = new CSMInternalFault();
			fault
					.setFaultString("An unexpected error occurred determining administrative access to the CSM Web Service.");
			throw fault;
		}

	}

	public AuthorizationManager getAuthorizationManager() {
		return auth;
	}
}
