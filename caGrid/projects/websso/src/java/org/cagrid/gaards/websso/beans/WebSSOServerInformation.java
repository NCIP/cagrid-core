package org.cagrid.gaards.websso.beans;

import java.io.Serializable;

public class WebSSOServerInformation implements Serializable {

	private static final long serialVersionUID = 1L;

	private String startAutoSyncGTS = null;

	private String trustStorePath = null;

	private String hostCredentialCertificateFilePath = null;

	private String hostCredentialKeyFilePath = null;

	public String getStartAutoSyncGTS() {
		return startAutoSyncGTS;
	}

	public void setStartAutoSyncGTS(String startAutoSyncGTS) {
		this.startAutoSyncGTS = startAutoSyncGTS;
	}

	public String getTrustStorePath() {
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

	public String getHostCredentialCertificateFilePath() {
		return hostCredentialCertificateFilePath;
	}

	public void setHostCredentialCertificateFilePath(
			String hostCredentialCertificateFilePath) {
		this.hostCredentialCertificateFilePath = hostCredentialCertificateFilePath;
	}

	public String getHostCredentialKeyFilePath() {
		return hostCredentialKeyFilePath;
	}

	public void setHostCredentialKeyFilePath(String hostCredentialKeyFilePath) {
		this.hostCredentialKeyFilePath = hostCredentialKeyFilePath;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

}
