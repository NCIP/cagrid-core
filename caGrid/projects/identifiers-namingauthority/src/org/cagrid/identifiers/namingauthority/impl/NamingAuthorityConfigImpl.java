package org.cagrid.identifiers.namingauthority.impl;

import org.cagrid.identifiers.namingauthority.NamingAuthorityConfig;

public class NamingAuthorityConfigImpl implements NamingAuthorityConfig {

	private String prefix = null;
	private Integer httpServerPort = null;
	private String gridSvcUrl = null;
	
	public void setPrefix( String prefix ) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return this.prefix;
	}
	
	public void setHttpServerPort( Integer port ) {
		this.httpServerPort = port;
	}

	public Integer getHttpServerPort() {
		return this.httpServerPort;
	}

	public void setGridSvcUrl(String gridSvcUrl) {
		this.gridSvcUrl = gridSvcUrl;
	}
	
	public String getGridSvcUrl() {
		return this.gridSvcUrl;
	}
}
