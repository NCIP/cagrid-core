package org.cagrid.gaards.cds.client;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.rmi.RemoteException;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.cagrid.gaards.cds.common.CertificateChain;
import org.cagrid.gaards.cds.common.PublicKey;
import org.cagrid.gaards.cds.common.Utils;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;
import org.cagrid.gaards.cds.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.pki.KeyUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.ResourceException;
import org.oasis.wsrf.lifetime.Destroy;

public class DelegatedCredentialUserClient {

	private org.cagrid.gaards.cds.delegated.client.DelegatedCredentialClient client;

	public DelegatedCredentialUserClient(EndpointReferenceType ref)
			throws Exception {
		this(ref, null);
	}

	public DelegatedCredentialUserClient(DelegatedCredentialReference ref)
			throws Exception {
		this(ref.getEndpointReference(), null);
	}

	public DelegatedCredentialUserClient(DelegatedCredentialReference ref,
			GlobusCredential cred) throws Exception {
		this(ref.getEndpointReference(), cred);
	}

	public DelegatedCredentialUserClient(EndpointReferenceType ref,
			GlobusCredential cred) throws Exception {
		client = new org.cagrid.gaards.cds.delegated.client.DelegatedCredentialClient(
				ref, cred);
	}

	/**
	 * This method suspends this credential, no further credentials will be
	 * issued or delegated.
	 * 
	 * @throws RemoteException
	 * @throws ResourceException
	 */
	public void suspend() throws RemoteException, ResourceException {
		client.destroy(new Destroy());
	}

	/**
	 * This method allow an authorized user to obtain a delegated credential
	 * 
	 * @return The delegated credential.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationFault
	 * @throws PermissionDeniedFault
	 */

	public GlobusCredential getDelegatedCredential() throws RemoteException,
			CDSInternalFault, DelegationFault, PermissionDeniedFault {
		return getDelegatedCredential(ClientConstants.DEFAULT_KEY_SIZE);
	}

	/**
	 * This method allow an authorized user to obtain a delegated credential
	 * 
	 * @param keySize
	 *            The size (bits) of the delegated credential's private key.
	 * @return The delegated credential.
	 * @throws RemoteException
	 * @throws CDSInternalFault
	 * @throws DelegationFault
	 * @throws PermissionDeniedFault
	 */

	public GlobusCredential getDelegatedCredential(int keySize)
			throws RemoteException, CDSInternalFault, DelegationFault,
			PermissionDeniedFault {
		KeyPair pair = null;
		PublicKey publicKey = new PublicKey();
		try {
			pair = KeyUtil.generateRSAKeyPair(keySize);
			publicKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
		} catch (Exception e) {
			DelegationFault f = new DelegationFault();
			f
					.setFaultString("An unexpected error occurred in generating the local key pair: "
							+ e.getMessage() + ".");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (DelegationFault) helper.getFault();
			throw f;
		}
		CertificateChain chain = client.getDelegatedCredential(publicKey);
		X509Certificate[] certs = null;

		try {
			certs = Utils.toCertificateArray(chain);
		} catch (Exception e) {
			DelegationFault f = new DelegationFault();
			f
					.setFaultString("An unexpected error occurred in unmarshalling the signed certificate chain: "
							+ e.getMessage() + ".");
			FaultHelper helper = new FaultHelper(f);
			helper.addFaultCause(e);
			f = (DelegationFault) helper.getFault();
			throw f;

		}
		GlobusCredential proxy = new GlobusCredential(pair.getPrivate(), certs);
		return proxy;
	}
}
