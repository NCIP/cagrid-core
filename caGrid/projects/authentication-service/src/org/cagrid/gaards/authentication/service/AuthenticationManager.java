package org.cagrid.gaards.authentication.service;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.io.File;
import java.rmi.RemoteException;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.Credential;
import org.cagrid.gaards.authentication.common.AuthenticationProfile;
import org.cagrid.gaards.authentication.common.InsufficientAttributeException;
import org.cagrid.gaards.authentication.common.InvalidCredentialException;
import org.cagrid.gaards.authentication.faults.AuthenticationProviderFault;
import org.cagrid.gaards.authentication.faults.CredentialNotSupportedFault;
import org.cagrid.gaards.authentication.faults.InsufficientAttributeFault;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.saml.encoding.SAMLUtils;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;

public class AuthenticationManager {

	private AuthenticationProvider auth;

	public AuthenticationManager(File properties, File configuration)
			throws RemoteException {
		this(new FileSystemResource(
				properties), new FileSystemResource(configuration));
	}

	public AuthenticationManager(AbstractResource properties,
			AbstractResource configuration) throws RemoteException {
		try {
			BeanUtils utils = new BeanUtils(configuration, properties);
			this.auth = utils.getAuthenticationProvider();
			Set<QName> set = this.auth.getSupportedAuthenticationProfiles();
			if ((set == null) || (set.size() < 1)) {
				throw new Exception(
						"The authentication provider must support at least 1 valid authentication profile.");
			} else if (!AuthenticationProfile.isValid(set)) {
				throw new Exception(
						"The authentication provider supports an unknown authentication profile.");
			}
		} catch (Exception ex) {
			throw new RemoteException(
					"Error instantiating AuthenticationProvider: "
							+ ex.getMessage(), ex);
		}
	}

	public SAMLAssertion authenticate(Credential credential)
			throws RemoteException, AuthenticationProviderFault,
			CredentialNotSupportedFault, InsufficientAttributeFault,
			InvalidCredentialFault {
		if (!AuthenticationProfile.isSupported(this.auth
				.getSupportedAuthenticationProfiles(), credential)) {
			CredentialNotSupportedFault fault = new CredentialNotSupportedFault();
			fault
					.setFaultString("The credential provided is not accepted by this service.");
			throw fault;
		}

		try {
			return this.auth.authenticate(credential);
		} catch (InvalidCredentialException ex) {
			InvalidCredentialFault fault = new InvalidCredentialFault();
			fault.setFaultString(ex.getMessage());
			FaultHelper fh = new FaultHelper(fault);
			fh.addFaultCause(ex);
			fault = (InvalidCredentialFault) fh.getFault();
			throw fault;
		} catch (InsufficientAttributeException ex) {
			InsufficientAttributeFault fault = new InsufficientAttributeFault();
			fault.setFaultString(ex.getMessage());
			FaultHelper fh = new FaultHelper(fault);
			fh.addFaultCause(ex);
			fault = (InsufficientAttributeFault) fh.getFault();
			throw fault;
		} catch (Exception ex) {
			AuthenticationProviderFault fault = new AuthenticationProviderFault();
			fault.setFaultString(ex.getMessage());
			FaultHelper fh = new FaultHelper(fault);
			fh.addFaultCause(ex);
			fault = (AuthenticationProviderFault) fh.getFault();
			throw fault;
		}
	}

	public gov.nih.nci.cagrid.authentication.bean.SAMLAssertion authenticate(
			gov.nih.nci.cagrid.authentication.bean.Credential credential)
			throws RemoteException,
			gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault,
			gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault,
			gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault {
		if (credential.getBasicAuthenticationCredential() != null) {
			if (credential.getCredentialExtension() != null) {
				gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault fault = new gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault();
				fault
						.setFaultString("The credential extension cannot be used to authenticate with the deprecated authenticate method, only a basic authentication credential is supported.");
				throw fault;
			} else {
				BasicAuthenticationCredential cred = credential
						.getBasicAuthenticationCredential();
				BasicAuthentication auth = new BasicAuthentication();
				auth.setUserId(cred.getUserId());
				auth.setPassword(cred.getPassword());
				try {
					SAMLAssertion saml = this.authenticate(auth);
					gov.nih.nci.cagrid.authentication.bean.SAMLAssertion assertion = new gov.nih.nci.cagrid.authentication.bean.SAMLAssertion();
					assertion.setXml(SAMLUtils.samlAssertionToString(saml));
					return assertion;
				} catch (org.cagrid.gaards.authentication.faults.InsufficientAttributeFault e) {
					gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault fault = new gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault();
					fault.setFaultString(e.getFaultString());
					FaultHelper fh = new FaultHelper(fault);
					fh.addFaultCause(e);
					fault = (gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault) fh
							.getFault();
					throw fault;
				} catch (org.cagrid.gaards.authentication.faults.InvalidCredentialFault e) {
					gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault fault = new gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault();
					fault.setFaultString(e.getFaultString());
					FaultHelper fh = new FaultHelper(fault);
					fh.addFaultCause(e);
					fault = (gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault) fh
							.getFault();
					throw fault;
				} catch (Exception e) {
					gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault fault = new gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault();
					fault.setFaultString(Utils.getExceptionMessage(e));
					FaultHelper fh = new FaultHelper(fault);
					fh.addFaultCause(e);
					fault = (gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault) fh
							.getFault();
					throw fault;
				}
			}

		} else {
			gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault fault = new gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault();
			fault
					.setFaultString("No basic authentication credential was provided, a basic authentication credential is required to authenticate to this service using the deprecated authenticate method.");
			throw fault;
		}
	}

	public Set<QName> getSupportedAuthenticationProfiles() {
		return this.auth.getSupportedAuthenticationProfiles();
	}

}
