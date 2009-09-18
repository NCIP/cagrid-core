package org.cagrid.gaards.authentication.service;

import org.cagrid.gaards.authentication.common.AuthenticationProfile;

public abstract class BasicAuthenticationSubjectProvider extends
		BaseSubjectProvider {
	public BasicAuthenticationSubjectProvider() {
		super();
		addSupportedProfile(AuthenticationProfile.BASIC_AUTHENTICATION);
	}

}
