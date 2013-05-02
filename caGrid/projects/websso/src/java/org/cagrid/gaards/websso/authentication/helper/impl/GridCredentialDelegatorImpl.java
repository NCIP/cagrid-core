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
package org.cagrid.gaards.websso.authentication.helper.impl;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.cds.client.ClientConstants;
import org.cagrid.gaards.cds.client.DelegationUserClient;
import org.cagrid.gaards.cds.common.AllowedParties;
import org.cagrid.gaards.cds.common.IdentityDelegationPolicy;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.websso.authentication.helper.GridCredentialDelegator;
import org.cagrid.gaards.websso.beans.CredentialDelegationServiceInformation;
import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.IdentityAuthorization;

public class GridCredentialDelegatorImpl implements GridCredentialDelegator {
	private final Log log = LogFactory.getLog(getClass());
	private CredentialDelegationServiceInformation credentialDelegationServiceInformation = null;

	public GridCredentialDelegatorImpl(
			CredentialDelegationServiceInformation credentialDelegationServiceInformation) {
		super();
		this.credentialDelegationServiceInformation = credentialDelegationServiceInformation;
	}

	public String delegateGridCredential(GlobusCredential globusCredential,
			List<String> hostIdentityList)
			throws AuthenticationConfigurationException {

		String[] hostIdentities = new String[hostIdentityList.size()];
		hostIdentityList.toArray(hostIdentities);
		AllowedParties allowedParties = new AllowedParties();
		allowedParties.setGridIdentity(hostIdentities);
		IdentityDelegationPolicy identityDelegationPolicy = new IdentityDelegationPolicy();
		identityDelegationPolicy.setAllowedParties(allowedParties);
		ProxyLifetime issueCredentialsCDSLifeTime = new ProxyLifetime();
		int delegationLifetimeHours = this.credentialDelegationServiceInformation
				.getDelegationLifetimeHours();
		issueCredentialsCDSLifeTime.setHours(delegationLifetimeHours);
		int delegationLifetimeMinutes = this.credentialDelegationServiceInformation
				.getDelegationLifetimeMinutes();
		issueCredentialsCDSLifeTime.setMinutes(delegationLifetimeMinutes);
		issueCredentialsCDSLifeTime.setSeconds(delegationLifetimeMinutes);

		DelegationUserClient client = getDelegationUserClient(globusCredential);
		DelegatedCredentialReference delegatedCredentialReference = delegationCredentialReference(
				identityDelegationPolicy, issueCredentialsCDSLifeTime, client);
		String serializedDelegatedCredentialReference = getSerializedCredentialRef(delegatedCredentialReference);
		return serializedDelegatedCredentialReference;
	}

	private String getSerializedCredentialRef(
			DelegatedCredentialReference delegatedCredentialReference)
			throws AuthenticationConfigurationException {
		String serializedDelegatedCredentialReference = null;
		try {
			StringWriter stringWriter = new StringWriter();
			Utils.serializeObject(
					delegatedCredentialReference,
					new QName(
							"http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential/types",
							"DelegatedCredentialReference"), stringWriter,
					DelegationUserClient.class.getResourceAsStream("client-config.wsdd"));
			serializedDelegatedCredentialReference = stringWriter.toString();
		} catch (Exception e) {
			log.error(FaultUtil.printFaultToString(e));
			throw new AuthenticationConfigurationException(
					"Unable to serialize the message Delegated Credentials: "
							+ FaultUtil.printFaultToString(e), e);
		}
		return serializedDelegatedCredentialReference;
	}

	private DelegatedCredentialReference delegationCredentialReference(
			IdentityDelegationPolicy identityDelegationPolicy,
			ProxyLifetime issueCredentialsCDSLifeTime,
			DelegationUserClient client)
			throws AuthenticationConfigurationException {
		DelegatedCredentialReference delegatedCredentialReference = null;
		try {
			int issuedCredentialPathLength = this.credentialDelegationServiceInformation
					.getIssuedCredentialPathLength();
			delegatedCredentialReference = client.delegateCredential(null,
					issuedCredentialPathLength + 1, identityDelegationPolicy,
					issueCredentialsCDSLifeTime, issuedCredentialPathLength,
					ClientConstants.DEFAULT_KEY_SIZE);
		} catch (CDSInternalFault e) {
			String faultString = e.getFaultString();
			log.error(FaultUtil.printFaultToString(e));
			throw new AuthenticationConfigurationException(faultString, e);
		} catch (DelegationFault e) {
			String faultString = e.getFaultString();
			log.error(FaultUtil.printFaultToString(e));
			throw new AuthenticationConfigurationException(faultString, e);
		} catch (PermissionDeniedFault e) {
			String faultString = e.getFaultString();
			throw new AuthenticationConfigurationException(faultString, e);
		} catch (RemoteException e) {
			log.error(e);
			throw new AuthenticationConfigurationException(
					"Error accessing the Delegation Service: " + e.getMessage(), e);
		} catch (MalformedURIException e) {
			log.error(e);
			throw new AuthenticationConfigurationException(
					"Error accessing the Delegation Service, Please check the URL for Delegation Service: "
							+ e.getMessage(), e);
		}
		return delegatedCredentialReference;
	}

	private DelegationUserClient getDelegationUserClient(
			GlobusCredential globusCredential)
			throws AuthenticationConfigurationException {
		DelegationUserClient client = null;
		try {
			client = new DelegationUserClient(
					this.credentialDelegationServiceInformation.getServiceURL(),
					globusCredential);

			if (Utils.clean(credentialDelegationServiceInformation
					.getServiceIdentity()) != null) {
				IdentityAuthorization auth = new IdentityAuthorization(
						credentialDelegationServiceInformation
								.getServiceIdentity());
				client.setAuthorization(auth);
			}
		} catch (Exception e) {
			log.error(FaultUtil.printFaultToString(e));
			throw new AuthenticationConfigurationException(
					"Error accessing the Delegation Service : "
							+ e.getMessage(), e);
		}
		return client;
	}
}
