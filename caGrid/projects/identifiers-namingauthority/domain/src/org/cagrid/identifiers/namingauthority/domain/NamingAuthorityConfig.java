package org.cagrid.identifiers.namingauthority.domain;

//
// This class holds configuration that can be made
// publicly available via the HTTP port.
//
public class NamingAuthorityConfig implements java.io.Serializable {
	
	private String gridSvcUrl;
	
	public void setGridSvcUrl( String gridSvcUrl ) {
		this.gridSvcUrl = gridSvcUrl;
	}
	
	public String getGridSvcUrl() {
		return this.gridSvcUrl;
	}

}
