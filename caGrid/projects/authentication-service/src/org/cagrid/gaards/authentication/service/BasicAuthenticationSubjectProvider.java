/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.authentication.service;

import org.cagrid.gaards.authentication.common.AuthenticationProfile;

public abstract class BasicAuthenticationSubjectProvider extends
		BaseSubjectProvider {
	public BasicAuthenticationSubjectProvider() {
		super();
		addSupportedProfile(AuthenticationProfile.BASIC_AUTHENTICATION);
	}

}
