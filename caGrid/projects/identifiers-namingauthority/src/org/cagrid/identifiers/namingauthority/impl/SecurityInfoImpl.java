package org.cagrid.identifiers.namingauthority.impl;

import org.cagrid.identifiers.namingauthority.SecurityInfo;

public class SecurityInfoImpl implements SecurityInfo {

	private String user;
	
	public SecurityInfoImpl( String user ) {
		this.user = user;
	}
	
	public String getUser() {
		return this.user;
	}
}
