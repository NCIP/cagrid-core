package org.cagrid.websso.client.acegi;

import org.acegisecurity.GrantedAuthority;

public class DefaultGrantedAuthority implements GrantedAuthority {
	private static final long serialVersionUID = 1L;
	private String authority = null;

	public DefaultGrantedAuthority(String authority) {
		this.authority = authority;
	}

	public String getAuthority() {
		return authority;
	}
}