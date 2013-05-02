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
package gov.nih.nci.cagrid.advertisement.exceptions;

/**
 * UnregistrationException
 * 
 * @author oster
 * @created Apr 7, 2007 12:54:51 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class UnregistrationException extends Exception {

    public UnregistrationException() {
        super();
    }


    /**
     * @param message
     */
    public UnregistrationException(String message) {
        super(message);
    }


    /**
     * @param cause
     */
    public UnregistrationException(Throwable cause) {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public UnregistrationException(String message, Throwable cause) {
        super(message, cause);
    }

}
