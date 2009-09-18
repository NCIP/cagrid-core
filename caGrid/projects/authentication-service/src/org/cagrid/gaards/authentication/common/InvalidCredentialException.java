/**
 * $Id: InvalidCredentialException.java,v 1.1 2008-05-10 01:47:36 langella Exp $
 *
 */
package org.cagrid.gaards.authentication.common;

/**
 *
 * @version $Revision: 1.1 $
 * @author Joshua Phillips
 *
 */
public class InvalidCredentialException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8400902079644944986L;

    /**
     * 
     */
    public InvalidCredentialException() {
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public InvalidCredentialException(String message) {
	super(message);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param cause
     */
    public InvalidCredentialException(Throwable cause) {
	super(cause);
	// TODO Auto-generated constructor stub
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidCredentialException(String message, Throwable cause) {
	super(message, cause);
	// TODO Auto-generated constructor stub
    }

}
