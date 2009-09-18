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
