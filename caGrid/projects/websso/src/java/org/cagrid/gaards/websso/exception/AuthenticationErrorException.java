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
package org.cagrid.gaards.websso.exception;

import org.jasig.cas.authentication.handler.BadCredentialsAuthenticationException;

public class AuthenticationErrorException extends
		BadCredentialsAuthenticationException {

	private static final long serialVersionUID = 1L;

	public AuthenticationErrorException(String message) {
		super(message);
	}
	
	
	public AuthenticationErrorException(String message, Throwable cause) {
	    super(message, cause);
	}
}
