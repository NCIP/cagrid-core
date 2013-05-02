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
