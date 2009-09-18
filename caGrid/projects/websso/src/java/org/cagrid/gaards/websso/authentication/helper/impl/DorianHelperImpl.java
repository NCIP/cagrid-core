package org.cagrid.gaards.websso.authentication.helper.impl;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
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
			ifsUserClient = new GridUserClient(dorianInformation
					.getDorianServiceURL());
		} catch (MalformedURIException e) {
			throw new AuthenticationConfigurationException(
					"Invalid Dorian Service URL : " + FaultUtil.printFaultToString(e));
		} catch (RemoteException e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Dorian Service : "+ FaultUtil.printFaultToString(e));
		}
		try {
			globusCredential = ifsUserClient.requestUserCertificate(
					samlAssertion, dorianInformation.getProxyLifeTime());
		} catch (DorianFault e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Dorian Service : "
							+ FaultUtil.printFaultToString(e));
		} catch (DorianInternalFault e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Dorian Service : "
							+ FaultUtil.printFaultToString(e));
		} catch (InvalidAssertionFault e) {
			throw new AuthenticationConfigurationException(
					"Invalid SAML Assertion obtained from Authentication Service : "
							+ FaultUtil.printFaultToString(e));
		} catch (UserPolicyFault e) {
			throw new AuthenticationConfigurationException(
					"Policy Error occured obtaining Proxy from Dorian : "
							+ FaultUtil.printFaultToString(e));
		} catch (PermissionDeniedFault e) {
			throw new AuthenticationErrorException(
					"Permission denied while obtaining Proxy from Dorian : "
							+ FaultUtil.printFaultToString(e));
		}

		return globusCredential;
	}
}
