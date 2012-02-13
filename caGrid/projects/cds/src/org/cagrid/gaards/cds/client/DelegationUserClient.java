package org.cagrid.gaards.cds.client;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.security.ProxyUtil;

import java.rmi.RemoteException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.axis.types.URI;
import org.cagrid.gaards.cds.common.CertificateChain;
import org.cagrid.gaards.cds.common.ClientDelegationFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditFilter;
import org.cagrid.gaards.cds.common.DelegatedCredentialAuditRecord;
import org.cagrid.gaards.cds.common.DelegationDescriptor;
import org.cagrid.gaards.cds.common.DelegationIdentifier;
import org.cagrid.gaards.cds.common.DelegationPolicy;
import org.cagrid.gaards.cds.common.DelegationRecord;
import org.cagrid.gaards.cds.common.DelegationRecordFilter;
import org.cagrid.gaards.cds.common.DelegationRequest;
import org.cagrid.gaards.cds.common.DelegationSigningRequest;
import org.cagrid.gaards.cds.common.DelegationSigningResponse;
import org.cagrid.gaards.cds.common.DelegationStatus;
import org.cagrid.gaards.cds.common.Errors;
import org.cagrid.gaards.cds.common.ProxyLifetime;
import org.cagrid.gaards.cds.common.Utils;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.pki.ProxyCreator;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.impl.security.authorization.Authorization;

public class DelegationUserClient {

	private GlobusCredential cred;
	private CredentialDelegationServiceClient client;

	public DelegationUserClient(String url) throws Exception {
		this(url, ProxyUtil.getDefaultProxy());
	}

	public DelegationUserClient(String url, GlobusCredential cred)
			throws Exception {
		this.cred = cred;
		this.client = new CredentialDelegationServiceClient(url, cred);
	}
	
	
	/**
     * This method specifies an authorization policy that the client should use
     * for authorizing the server that it connects to.
     * 
     * @param authorization
     *            The authorization policy to enforce
     */

    public void setAuthorization(Authorization authorization) {
        client.setAuthorization(authorization);
    }

	/**
	 * This method allows a user to delegated their credential to the Credential
	 * Delegation Service
	 * 
	 * @param policy
	 *            The policy specifying who may request this delegated
	 *            credential from the Credential Delegation Service
	 * @param issuedCredentialLifetime
	 *            The life time of the credentials delegated to entities by the
	 *            Credential Delegation Service on you behalf.
	 * @return A reference to the delegated credential, this reference may be
	 *         used by entites to request a credential.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationFault
	 * @throws PermissionDeniedFault
	 * @throws URI.MalformedURIException
	 */
	public DelegatedCredentialReference delegateCredential(
			DelegationPolicy policy, ProxyLifetime issuedCredentialLifetime)
			throws RemoteException, CDSInternalFault, DelegationFault,
			PermissionDeniedFault, URI.MalformedURIException {
		return this.delegateCredential(null, policy, issuedCredentialLifetime);
	}

	/**
	 * This method allows a user to delegated their credential to the Credential
	 * Delegation Service
	 * 
	 * @param delegationLifetime
	 *            The life time of the credential being delegated to the
	 *            Credential Delegation Service. This lifetime specifies how
	 *            long the Credential Delegation Service may delegate this
	 *            credential.
	 * @param policy
	 *            The policy specifying who may request this delegated
	 *            credential from the Credential Delegation Service
	 * @param issuedCredentialLifetime
	 *            The life time of the credentials delegated to entities by the
	 *            Credential Delegation Service on you behalf.
	 * @return A reference to the delegated credential, this reference may be
	 *         used by entites to request a credential.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationFault
	 * @throws PermissionDeniedFault
	 * @throws URI.MalformedURIException
	 */
	public DelegatedCredentialReference delegateCredential(
			ProxyLifetime delegationLifetime, DelegationPolicy policy,
			ProxyLifetime issuedCredentialLifetime) throws RemoteException,
			CDSInternalFault, DelegationFault, PermissionDeniedFault,
			URI.MalformedURIException {
		return this.delegateCredential(delegationLifetime, 1, policy,
				issuedCredentialLifetime, 0, ClientConstants.DEFAULT_KEY_SIZE);
	}

	/**
	 * This method allows a user to delegated their credential to the Credential
	 * Delegation Service
	 * 
	 * @param delegationLifetime
	 *            The life time of the credential being delegated to the
	 *            Credential Delegation Service. This lifetime specifies how
	 *            long the Credential Delegation Service may delegate this
	 *            credential.
	 * @param delegationPathLength
	 *            The delegation path length of the credential being delegated
	 *            to the Credential Delegation Service.
	 * @param policy
	 *            The policy specifying who may request this delegated
	 *            credential from the Credential Delegation Service
	 * @param issuedCredentialLifetime
	 *            The life time of the credentials delegated to entities by the
	 *            Credential Delegation Service on you behalf.
	 * @param issuedCredentialPathLength
	 *            The path length of the credentials delegated to entities by
	 *            the Credential Delegation Service on you behalf. A path length
	 *            of 0 means that entities that can you obtain a delegated
	 *            credential cannot further delegate it.
	 * @param keyLength
	 *            The key length of the signing credential.
	 * @return A reference to the delegated credential, this reference may be
	 *         used by entites to request a credential.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationFault
	 * @throws PermissionDeniedFault
	 * @throws URI.MalformedURIException
	 */
	public DelegatedCredentialReference delegateCredential(
			ProxyLifetime delegationLifetime, int delegationPathLength,
			DelegationPolicy policy, ProxyLifetime issuedCredentialLifetime,
			int issuedCredentialPathLength, int keyLength)
			throws RemoteException, CDSInternalFault, DelegationFault,
			PermissionDeniedFault, URI.MalformedURIException {

		DelegationRequest req = new DelegationRequest();
		req.setDelegationPolicy(policy);
		req.setIssuedCredentialLifetime(issuedCredentialLifetime);
		req.setIssuedCredentialPathLength(issuedCredentialPathLength);
		req.setKeyLength(keyLength);
		DelegationSigningRequest dsr = client.initiateDelegation(req);

		int hours = 0;
		int minutes = 0;
		int seconds = 0;

		if (delegationLifetime != null) {
			hours = delegationLifetime.getHours();
			minutes = delegationLifetime.getMinutes();
			seconds = delegationLifetime.getSeconds();
		}
		CertificateChain chain = null;
		try {
			PublicKey publicKey = KeyUtil.loadPublicKey(dsr.getPublicKey()
					.getKeyAsString());

			X509Certificate[] certs = ProxyCreator
					.createImpersonationProxyCertificate(cred
							.getCertificateChain(), cred.getPrivateKey(),
							publicKey, hours, minutes, seconds,
							delegationPathLength);
			chain = Utils.toCertificateChain(certs);

		} catch (Exception e) {
			DelegationFault f = new DelegationFault();
			f.setFaultString("Unexpected error creating proxy: "
					+ e.getMessage() + ".");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (DelegationFault) helper.getFault();
			throw f;
		}
		DelegationSigningResponse res = new DelegationSigningResponse();
		res.setDelegationIdentifier(dsr.getDelegationIdentifier());
		res.setCertificateChain(chain);
		return client.approveDelegation(res);
	}

	/**
	 * This method allows a user to find credentials that they delegated.
	 * 
	 * @return A list of records each representing a credential delegated by the
	 *         user
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationInternalFault
	 * @throws PermissionDeniedFault
	 */

	public List<DelegationRecord> findMyDelegatedCredentials()
			throws RemoteException, CDSInternalFault, DelegationFault,
			PermissionDeniedFault {
		return findMyDelegatedCredentials(new DelegationRecordFilter());
	}

	/**
	 * This method allows a user to find credentials that they delegated.
	 * 
	 * @param filter
	 *            Search criteria to use in finding delegated credentials
	 * @return A list of records each representing a credential delegated by the
	 *         user
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationInternalFault
	 * @throws PermissionDeniedFault
	 */

	public List<DelegationRecord> findMyDelegatedCredentials(
			DelegationRecordFilter filter) throws RemoteException,
			CDSInternalFault, DelegationFault, PermissionDeniedFault {
		if (filter == null) {
			filter = new DelegationRecordFilter();
		}
		if (cred != null) {
			filter.setGridIdentity(cred.getIdentity());
		} else {
			try {
				GlobusCredential c = ProxyUtil.getDefaultProxy();
				if (c != null) {
					filter.setGridIdentity(c.getIdentity());
				}
			} catch (Exception e) {
				DelegationFault f = new DelegationFault();
				f.setFaultString(e.getMessage());
				throw f;
			}
		}

		if (filter.getGridIdentity() == null) {
			throw Errors
					.getPermissionDeniedFault(Errors.AUTHENTICATION_REQUIRED);
		}

		DelegationRecord[] records = client.findDelegatedCredentials(filter);
		if (records == null) {
			return new ArrayList<DelegationRecord>();
		} else {
			List<DelegationRecord> list = Arrays.asList(records);
			return list;
		}
	}

	/**
	 * This method suspends a delegated credential such that no further
	 * credentials will be delegated/issued.
	 * 
	 * @param id
	 *            The delegation identifier of the delegated credentials to
	 *            suspend.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationFault
	 * @throws PermissionDeniedFault
	 */

	public void suspendDelegatedCredential(DelegationIdentifier id)
			throws RemoteException, CDSInternalFault, DelegationFault,
			PermissionDeniedFault {
		client.updateDelegatedCredentialStatus(id, DelegationStatus.Suspended);
	}

	/**
	 * This method obtains a list of credentials that have been delegated to
	 * this client by other clients.
	 * 
	 * @return A list of credentials delegated to this client.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws PermissionDeniedFault
	 */

	public List<DelegationDescriptor> findCredentialsDelegatedToClient()
			throws RemoteException, CDSInternalFault, PermissionDeniedFault {
		return findCredentialsDelegatedToClient(new ClientDelegationFilter());
	}

	/**
	 * This method obtains a list of credentials that have been delegated to
	 * this client by other clients.
	 * 
	 * @param ownerGridIdentity
	 *            The grid identity of the owner to restrict this search to.
	 * @return A list of credentials delegated to this client.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws PermissionDeniedFault
	 */

	public List<DelegationDescriptor> findCredentialsDelegatedToClient(
			String ownerGridIdentity) throws RemoteException, CDSInternalFault,
			PermissionDeniedFault {
		ClientDelegationFilter f = new ClientDelegationFilter();
		f.setGridIdentity(ownerGridIdentity);
		return findCredentialsDelegatedToClient(f);
	}

	/**
	 * This method obtains a list of credentials that have been delegated to
	 * this client by other clients.
	 * 
	 * @param filter
	 *            Search criteria to use in filtering credentials
	 * @return A list of credentials delegated to this client.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws PermissionDeniedFault
	 */

	public List<DelegationDescriptor> findCredentialsDelegatedToClient(
			ClientDelegationFilter filter) throws RemoteException,
			CDSInternalFault, PermissionDeniedFault {
		DelegationDescriptor[] results = client
				.findCredentialsDelegatedToClient(filter);
		List<DelegationDescriptor> list = new ArrayList<DelegationDescriptor>();
		if (results != null) {
			for (int i = 0; i < results.length; i++) {
				list.add(results[i]);
			}
		}
		return list;
	}

	/**
	 * Allows one to search the audit logs from Delegated Credential(s).
	 * 
	 * @param f
	 *            The search criteria
	 * @return Returns a list of audit records meeting the search criteria
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationFault
	 * @throws PermissionDeniedFault
	 */

	public List<DelegatedCredentialAuditRecord> searchDelegatedCredentialAuditLog(
			DelegatedCredentialAuditFilter f) throws RemoteException,
			CDSInternalFault, DelegationFault, PermissionDeniedFault {
		DelegatedCredentialAuditRecord[] r = client
				.searchDelegatedCredentialAuditLog(f);
		List<DelegatedCredentialAuditRecord> list = new ArrayList<DelegatedCredentialAuditRecord>();
		if (r != null) {
			for (int i = 0; i < r.length; i++) {
				list.add(r[i]);
			}
		}
		return list;
	}
}
