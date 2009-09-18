package org.cagrid.gaards.websso.authentication.helper.impl;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.common.Utils;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
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
		issueCredentialsCDSLifeTime
				.setHours(this.credentialDelegationServiceInformation
						.getDelegationLifetimeHours());
		issueCredentialsCDSLifeTime
				.setMinutes(this.credentialDelegationServiceInformation
						.getDelegationLifetimeMinutes());
		issueCredentialsCDSLifeTime
				.setSeconds(this.credentialDelegationServiceInformation
						.getDelegationLifetimeMinutes());

		DelegationUserClient client = null;
		try {
			client = new DelegationUserClient(
					this.credentialDelegationServiceInformation.getServiceURL(),globusCredential);			

			if (Utils.clean(credentialDelegationServiceInformation.getServiceIdentity()) != null) {
				IdentityAuthorization auth = new IdentityAuthorization(credentialDelegationServiceInformation.getServiceIdentity());
				client.setAuthorization(auth);
			}			
		} catch (Exception e) {
			e.printStackTrace();
			throw new AuthenticationConfigurationException("Error accessing the Delegation Service : "+ e.getMessage(), e);
		}

		DelegatedCredentialReference delegatedCredentialReference = null;
		try {
			int issuedCredentialPathLength = this.credentialDelegationServiceInformation
					.getIssuedCredentialPathLength();
			delegatedCredentialReference = client.delegateCredential(null,
					issuedCredentialPathLength + 1, identityDelegationPolicy,
					issueCredentialsCDSLifeTime, issuedCredentialPathLength,
					ClientConstants.DEFAULT_KEY_SIZE);
		} catch (CDSInternalFault e) {
			throw new AuthenticationConfigurationException(
					"Internal Error in the Delegation Service : "
							+ FaultUtil.printFaultToString(e));
		} catch (DelegationFault e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Delegation Service, Unable to delegate credentials : "
							+ FaultUtil.printFaultToString(e));
		} catch (PermissionDeniedFault e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Delegation Service, Permission Denied : "
							+ FaultUtil.printFaultToString(e));
		} catch (RemoteException e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Delegation Service : "
							+ e.getMessage(), e);
		} catch (MalformedURIException e) {
			throw new AuthenticationConfigurationException(
					"Error accessing the Delegation Service, Please check the URL for Delegation Service : "
							+ e.getMessage());
		}

		String serializedDelegatedCredentialReference = null;
		try {
			StringWriter stringWriter = new StringWriter();
			Utils
					.serializeObject(
							delegatedCredentialReference,
							new QName(
									"http://cds.gaards.cagrid.org/CredentialDelegationService/DelegatedCredential/types",
									"DelegatedCredentialReference"),
							stringWriter, DelegationUserClient.class.getResourceAsStream("client-config.wsdd"));
			serializedDelegatedCredentialReference = stringWriter.toString();
		} catch (Exception e) {
			throw new AuthenticationConfigurationException(
					"Unable to serialize the message Delegated Credentials : "
							+ e.getMessage(), e);
		}
		return serializedDelegatedCredentialReference;
	}
}
