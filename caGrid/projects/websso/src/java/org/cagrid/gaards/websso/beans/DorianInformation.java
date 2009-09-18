package org.cagrid.gaards.websso.beans;

import java.io.Serializable;

import org.cagrid.gaards.dorian.federation.CertificateLifetime;

public class DorianInformation implements Serializable {
	private static final long serialVersionUID = 1L;
	private int proxyLifetimeHours = 12;
	private int proxyLifetimeMinutes = 0;
	private int proxyLifetimeSeconds = 0;
	private String displayName;
	private String dorianServiceURL;
	private java.lang.String serviceIdentity;


	public int getProxyLifetimeHours() {
		return proxyLifetimeHours;
	}

	public void setProxyLifetimeHours(int proxyLifetimeHours) {
		this.proxyLifetimeHours = proxyLifetimeHours;
	}

	public int getProxyLifetimeMinutes() {
		return proxyLifetimeMinutes;
	}

	public void setProxyLifetimeMinutes(int proxyLifetimeMinutes) {
		this.proxyLifetimeMinutes = proxyLifetimeMinutes;
	}

	public int getProxyLifetimeSeconds() {
		return proxyLifetimeSeconds;
	}

	public void setProxyLifetimeSeconds(int proxyLifetimeSeconds) {
		this.proxyLifetimeSeconds = proxyLifetimeSeconds;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setServiceIdentity(java.lang.String serviceIdentity) {
		this.serviceIdentity = serviceIdentity;
	}

	public java.lang.String getServiceIdentity() {
		return serviceIdentity;
	}

	public void setDorianServiceURL(String dorianServiceURL) {
		this.dorianServiceURL = dorianServiceURL;
	}
	
	public String getDorianServiceURL() {
		return dorianServiceURL;
	}

	public CertificateLifetime getProxyLifeTime() {
		CertificateLifetime proxyLifetime = new CertificateLifetime();
		proxyLifetime.setHours(this.getProxyLifetimeHours());
		proxyLifetime.setMinutes(this.getProxyLifetimeMinutes());
		proxyLifetime.setSeconds(this.getProxyLifetimeSeconds());
		return proxyLifetime;
	}
}
