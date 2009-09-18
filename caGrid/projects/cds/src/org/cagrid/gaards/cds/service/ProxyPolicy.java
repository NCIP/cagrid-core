package org.cagrid.gaards.cds.service;

import java.util.Set;

public class ProxyPolicy {

	private Set<String> supportedKeySizes;
	private int maxDelegationPathLength;

	public ProxyPolicy() {

	}

	public Set<String> getSupportedKeySizes() {
		return supportedKeySizes;
	}

	public void setSupportedKeySizes(Set<String> keySizes) {
		this.supportedKeySizes = keySizes;
	}

	public boolean isKeySizeSupported(int keySize) {
		if (this.supportedKeySizes.contains(String.valueOf(keySize))) {
			return true;
		} else {
			return false;
		}
	}

	public int getMaxDelegationPathLength() {
		return maxDelegationPathLength;
	}

	public void setMaxDelegationPathLength(int maxDelegationPathLength) {
		this.maxDelegationPathLength = maxDelegationPathLength;
	}
}
