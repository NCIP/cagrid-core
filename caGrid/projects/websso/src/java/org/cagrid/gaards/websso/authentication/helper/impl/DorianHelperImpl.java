package org.cagrid.gaards.websso.authentication.helper.impl;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.common.DorianFault;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.stubs.types.UserPolicyFault;
import org.cagrid.gaards.websso.authentication.helper.DorianHelper;
import org.cagrid.gaards.websso.beans.DorianInformation;
import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;
import org.cagrid.gaards.websso.exception.AuthenticationErrorException;
import org.globus.gsi.GlobusCredential;

public class DorianHelperImpl implements DorianHelper {

	private final Log log = LogFactory.getLog(getClass());
	
	public DorianHelperImpl() {
		super();
	}

	public GlobusCredential obtainProxy(SAMLAssertion samlAssertion,
			DorianInformation dorianInformation)
			throws AuthenticationConfigurationException,
			AuthenticationErrorException {
		GlobusCredential globusCredential = null;

		GridUserClient ifsUserClient = null;
		try {
			ifsUserClient = new GridUserClient(
					dorianInformation.getDorianServiceURL());
			globusCredential = ifsUserClient.requestUserCertificate(
					samlAssertion, dorianInformation.getProxyLifeTime());
		} catch (Exception e) {
			handleException(e);
		}
		return globusCredential;
	}

	private void handleException(Exception e)
			throws AuthenticationErrorException,
			AuthenticationConfigurationException {
		if (e instanceof MalformedURIException) {
			log.error(e);
			throw new AuthenticationConfigurationException(
					"Invalid Dorian Service URL (" + e.getMessage() + ")", e);
		} else if (e instanceof DorianFault) {
			log.error(FaultUtil.printFaultToString(e));
			String faultString = ((DorianFault) e).getFaultString();
			throw new AuthenticationConfigurationException(faultString, e);
		} else if (e instanceof DorianInternalFault) {
			log.error(FaultUtil.printFaultToString(e));
			String faultString = ((DorianInternalFault) e).getFaultString();
			throw new AuthenticationConfigurationException(faultString, e);
		} else if (e instanceof InvalidAssertionFault) {
			log.error(FaultUtil.printFaultToString(e));
			String faultString = ((InvalidAssertionFault) e).getFaultString();
			throw new AuthenticationConfigurationException(faultString, e);
		} else if (e instanceof UserPolicyFault) {
			log.error(FaultUtil.printFaultToString(e));
			String faultString = ((UserPolicyFault) e).getFaultString();
			throw new AuthenticationConfigurationException(faultString, e);
		} else if (e instanceof PermissionDeniedFault) {
			log.error(FaultUtil.printFaultToString(e));
			String faultString = ((PermissionDeniedFault) e).getFaultString();
			throw new AuthenticationErrorException(faultString, e);
		} else if (e instanceof RemoteException) {
			log.error(e);
			throw new AuthenticationConfigurationException(
					"Error accessing the Dorian Service :  " + e.getMessage(), e);
		}
	}
}
