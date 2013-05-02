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


import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;

/**
 * Listens for changes in a resource property to which a subscription has been made
 * 
 * @author David
 */
public interface SubscriptionListener {

    public void subscriptionValueChanged(ResourcePropertyValueChangeNotificationType notification);
}
