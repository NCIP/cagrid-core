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
package org.cagrid.notification;

/**
 * Exception thrown when subscribing to notifications fails
 * 
 * @author David
 */
public class NotificationSubscriptionException extends Exception {

    public NotificationSubscriptionException(String message) {
        super(message);
    }
    
    
    public NotificationSubscriptionException(Exception cause) {
        super(cause);
    }
    
    
    public NotificationSubscriptionException(String message, Exception cause) {
        super(message, cause);
    }
}
