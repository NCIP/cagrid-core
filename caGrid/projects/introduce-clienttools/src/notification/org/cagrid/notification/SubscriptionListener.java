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
