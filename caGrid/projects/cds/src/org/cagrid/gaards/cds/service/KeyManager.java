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
package org.cagrid.gaards.cds.service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import org.cagrid.gaards.cds.stubs.types.CDSInternalFault;
import org.cagrid.gaards.cds.stubs.types.DelegationFault;

public interface KeyManager {

	public abstract KeyPair createAndStoreKeyPair(String alias, int keyLength)
			throws CDSInternalFault;

	public abstract PublicKey getPublicKey(String alias)
			throws CDSInternalFault;

	public abstract PrivateKey getPrivateKey(String alias)
			throws CDSInternalFault;

	public abstract X509Certificate[] getCertificates(String alias)
			throws CDSInternalFault;

	public abstract boolean exists(String alias) throws CDSInternalFault;

	public abstract void storeCertificates(String alias, X509Certificate[] cert)
			throws CDSInternalFault, DelegationFault;

	public abstract void delete(String alias) throws CDSInternalFault;

	public abstract void deleteAll() throws CDSInternalFault;
}
