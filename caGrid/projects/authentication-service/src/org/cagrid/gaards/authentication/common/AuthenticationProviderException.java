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
/**
 * $Id: AuthenticationProviderException.java,v 1.1 2008-05-10 01:47:36 langella Exp $
 *
 */
package org.cagrid.gaards.authentication.common;

/**
 *
 * @version $Revision: 1.1 $
 * @author Joshua Phillips
 *
 */
public class AuthenticationProviderException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -161046673913742365L;

    /**
     * 
     */
    public AuthenticationProviderException() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public AuthenticationProviderException(String message) {
	super(message);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public AuthenticationProviderException(Throwable cause) {
	super(cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public AuthenticationProviderException(String message, Throwable cause) {
	super(message, cause);
	// TODO Auto-generated constructor stub
    }

}
