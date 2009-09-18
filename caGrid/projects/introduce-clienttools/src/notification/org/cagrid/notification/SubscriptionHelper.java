package org.cagrid.notification;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.globus.wsrf.NotificationConsumerManager;
import org.globus.wsrf.NotifyCallback;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.WSNConstants;
import org.globus.wsrf.container.ContainerException;
import org.globus.wsrf.core.notification.ResourcePropertyValueChangeNotificationElementType;
import org.globus.wsrf.impl.notification.SubscriptionCreationException;
import org.oasis.wsn.Subscribe;
import org.oasis.wsn.SubscribeResponse;
import org.oasis.wsn.TopicExpressionType;
import org.oasis.wsrf.lifetime.Destroy;
import org.oasis.wsrf.lifetime.ImmediateResourceTermination;
import org.oasis.wsrf.lifetime.WSResourceLifetimeServiceAddressingLocator;
import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;


/**
 * Helps clients listen for WS-Notification events
 * 
 * @author ervin
 */
public class SubscriptionHelper {
    
    private Map<EndpointReferenceType, NotificationConsumerManager> notificationConsumers = null;
    
    public SubscriptionHelper() {
        notificationConsumers = new HashMap<EndpointReferenceType, NotificationConsumerManager>();
    }
    
    
    /**
     * Subscribes to notifications from a remote service
     * 
     * @param client
     *      The client through which subscribtion will be made
     * @param resourcePropertyType
     *      The QName of the resource property to subscribe to
     * @param listener
     *      A listener to call when the resource property is updated
     * @return
     *      A Response which can be later used to unsubscribe from notifications
     * @throws SubscriptionCreationException
     */
    public SubscribeResponse subscribe(Object client, QName resourcePropertyType, SubscriptionListener listener) 
        throws SubscriptionCreationException {
        Method subscribeMethod = getSubscribeMethod(client);
        
        // Create client side notification consumer
        NotificationConsumerManager consumer = NotificationConsumerManager.getInstance();
        try {
            consumer.startListening();
        } catch (ContainerException ex) {
            throw new SubscriptionCreationException(
                "Error starting notification consumer listener: " + ex.getMessage(), ex);
        }
        EndpointReferenceType consumerEPR = null;
        try {
            consumerEPR = consumer.createNotificationConsumer(new NotifyCallbackListener(listener));
        } catch (ResourceException ex) {
            throw new SubscriptionCreationException(
                "Error creating notification consumer: " + ex.getMessage(), ex);
        }
        
        // store the notification listener and it's EPR for later
        notificationConsumers.put(consumerEPR, consumer);
        
        // build the subscribe request
        Subscribe subscription = new Subscribe();
        subscription.setUseNotify(Boolean.TRUE);
        subscription.setConsumerReference(consumerEPR);
        TopicExpressionType topicExpression = new TopicExpressionType();
        try {
            topicExpression.setDialect(WSNConstants.SIMPLE_TOPIC_DIALECT);
        } catch (MalformedURIException ex) {
            throw new SubscriptionCreationException(
                "Error setting notification topic dialect: " + ex.getMessage(), ex);
        }
        topicExpression.setValue(resourcePropertyType);
        subscription.setTopicExpression(topicExpression);
        
        // submit the request via the method we discovered
        SubscribeResponse response = invokeSubscribeMethod(subscription, client, subscribeMethod);

        return response;
    }
    
    
    /**
     * Unsubscribes from notifications
     * 
     * @param subscription
     * @throws NotificationSubscriptionException
     */
    public void unSubscribe(SubscribeResponse subscription) throws NotificationSubscriptionException {
        WSResourceLifetimeServiceAddressingLocator locator = new WSResourceLifetimeServiceAddressingLocator();
        try {
            ImmediateResourceTermination port = locator.getImmediateResourceTerminationPort(subscription.getSubscriptionReference());
            port.destroy(new Destroy());
            NotificationConsumerManager consumer = notificationConsumers.get(subscription.getSubscriptionReference());
            if (consumer.isListening()) {
                consumer.stopListening();
            }
        } catch (Exception ex) {
            throw new NotificationSubscriptionException("Error unsubscribing from notification: " + ex.getMessage(), ex);
        }
    }
    
    
    private Method getSubscribeMethod(Object client) throws SubscriptionCreationException {
        Method subscribeMethod = null;
        try {
            subscribeMethod = client.getClass().getMethod("subscribe", new Class[] {org.oasis.wsn.Subscribe.class});
            if (!SubscribeResponse.class.equals(subscribeMethod.getReturnType())) {
                throw new NoSuchMethodException("Subscribe method found, but it returns unexpected type: " + subscribeMethod.getReturnType());
            }
        } catch (NoSuchMethodException ex) {
            throw new SubscriptionCreationException("Client object did not have a valid subscribe method: " + ex.getMessage(), ex);
        }
        return subscribeMethod;
    }
    
    
    private SubscribeResponse invokeSubscribeMethod(Subscribe subscription, Object client, Method method) 
        throws SubscriptionCreationException {
        SubscribeResponse response = null;
        try {
            response = (SubscribeResponse) method.invoke(client, new Object[] {subscription});
        } catch (IllegalAccessException ex) {
            throw new SubscriptionCreationException("Error accessing the discovered subscribe method: " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new SubscriptionCreationException("Error invoking the discovered subscribe method: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new SubscriptionCreationException("Error subscribing to the service: " + ex.getMessage(), ex);
        }
        return response;
    }
    
    
    private static class NotifyCallbackListener implements NotifyCallback {
        private SubscriptionListener listener = null;
        
        public NotifyCallbackListener(SubscriptionListener listener) {
            this.listener = listener;
        }
        
        
        public void deliver(List topicPath, EndpointReferenceType producer, Object message) {
            ResourcePropertyValueChangeNotificationType changeMessage = 
                ((ResourcePropertyValueChangeNotificationElementType) message)
                    .getResourcePropertyValueChangeNotification();

            if (changeMessage != null) {
                listener.subscriptionValueChanged(changeMessage);
            }
        }
    }
}
