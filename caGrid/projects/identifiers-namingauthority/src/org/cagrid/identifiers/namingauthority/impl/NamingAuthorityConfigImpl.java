package org.cagrid.identifiers.namingauthority.impl;

import java.net.URI;

import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;

public class NamingAuthorityConfigImpl implements NamingAuthorityConfig {

	private URI prefix = null;
	private Integer httpServerPort = null;
	private URI gridSvcUrl = null;
	
	public void setPrefix( URI prefix ) {
		this.prefix = prefix;
	}
	
	public URI getPrefix() {
		return this.prefix;
	}
	
	public void setHttpServerPort( Integer port ) {
		this.httpServerPort = port;
	}

	public Integer getHttpServerPort() {
		return this.httpServerPort;
	}

	public void setGridSvcUrl(URI gridSvcUrl) {
		this.gridSvcUrl = gridSvcUrl;
	}
	
	public URI getGridSvcUrl() {
		return this.gridSvcUrl;
	}
}
