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

public class WrappedKey {

	private byte[] wrappedKeyData;
	private byte[] iv;


	public WrappedKey(byte[] wrappedKeyData, byte[] iv) {
		this.wrappedKeyData = wrappedKeyData;
		this.iv = iv;
	}


	public byte[] getWrappedKeyData() {
		return wrappedKeyData;
	}


	public byte[] getIV() {
		return iv;
	}
}
