package org.cagrid.identifiers.namingauthority.domain;

import java.net.URI;

//
// This class holds configuration that can be made
// publicly available via the HTTP port.
//
public class NamingAuthorityConfig implements java.io.Serializable {
	
	private URI gridSvcUrl;
	
	public void setGridSvcUrl( URI gridSvcUrl ) {
		this.gridSvcUrl = gridSvcUrl;
	}
	
	public URI getGridSvcUrl() {
		return this.gridSvcUrl;
	}

}
