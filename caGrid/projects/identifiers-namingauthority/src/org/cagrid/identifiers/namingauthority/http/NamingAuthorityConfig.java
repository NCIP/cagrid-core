package org.cagrid.identifiers.namingauthority.http;

//
// This class holds configuration that can be made
// publicly available via the HTTP port.
//
public class NamingAuthorityConfig implements java.io.Serializable {
	
	private String gridSvcUrl;
	
	public NamingAuthorityConfig() {
		
	}
	
	public NamingAuthorityConfig( org.cagrid.identifiers.namingauthority.NamingAuthorityConfig config) {
		setGridSvcUrl(config.getGridSvcUrl());
	}
	
	public void setGridSvcUrl( String gridSvcUrl ) {
		this.gridSvcUrl = gridSvcUrl;
	}
	
	public String getGridSvcUrl() {
		return this.gridSvcUrl;
	}

}
