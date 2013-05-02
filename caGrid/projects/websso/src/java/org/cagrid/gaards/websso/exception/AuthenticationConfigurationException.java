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

import org.jasig.cas.authentication.handler.AuthenticationException;

public class AuthenticationConfigurationException extends AuthenticationException
{
	/**
	 * Default serial id
	 */
	private static final long serialVersionUID = 1L;

	public AuthenticationConfigurationException(String message)
	{
		super(message);
	}
	
	public AuthenticationConfigurationException(String message, Exception exception)
	{
		super (message, exception);
	}
}
