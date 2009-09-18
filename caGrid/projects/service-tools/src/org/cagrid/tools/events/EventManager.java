package org.cagrid.tools.events;

import gov.nih.nci.cagrid.common.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class EventManager {

    private static String UNKNOWN_PARTY = "UNKNOWN";
    private static String NO_MESSAGE = "";

    private Map<String, EventHandler> handlers;
    private Map<String, Set<String>> events;
    private Log log;


    public EventManager() throws InvalidHandlerException {
        this(null, null);
    }


    public EventManager(List<EventHandler> handlerList, List<EventToHandlerMapping> mappings)
        throws InvalidHandlerException {
        this.log = LogFactory.getLog(this.getClass().getName());
        handlers = new HashMap<String, EventHandler>();
        events = new HashMap<String, Set<String>>();

        if (handlerList != null) {
            for (int i = 0; i < handlerList.size(); i++) {
                this.registerHandler(handlerList.get(i));
            }
        }
        if (mappings != null) {
            for (int i = 0; i < mappings.size(); i++) {
                this.registerEventWithHandler(mappings.get(i));
            }
        }
    }


    public EventHandler getEventHandler(String name) throws InvalidHandlerException {
        if (handlers.containsKey(name)) {
            return handlers.get(name);
        } else {
            throw new InvalidHandlerException("Could not find the event handler " + name + ".");
        }
    }


    public void logEvent(String targetId, String reportingPartyId, String eventType, String message) {
        try {
            // TODO: Thread this out.
            Event e = new Event();
            if (targetId != null) {
                e.setTargetId(targetId);
            } else {
                e.setTargetId(UNKNOWN_PARTY);
            }

            if (reportingPartyId != null) {
                e.setReportingPartyId(reportingPartyId);
            } else {
                e.setReportingPartyId(UNKNOWN_PARTY);
            }
            e.setEventType(eventType);
            if (message != null) {
                e.setMessage(message);
            } else {
                e.setMessage(NO_MESSAGE);
            }
            e.setOccurredAt(new Date().getTime());
            Set<EventHandler> s = getHandlers(eventType);
            Iterator<EventHandler> itr = s.iterator();
            while (itr.hasNext()) {
                itr.next().handleEvent(e);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public void registerEventWithHandler(EventToHandlerMapping mapping) throws InvalidHandlerException {
        if (!handlers.containsKey(mapping.getHandlerName())) {
            throw new InvalidHandlerException("Cannot register the event " + mapping.getEventName()
                + " with the handler " + mapping.getHandlerName() + ", no such handler is registered.");
        }
        Set<String> set = events.get(mapping.getEventName());
        if (set == null) {
            set = new HashSet<String>();
            events.put(mapping.getEventName(), set);
        }
        if (!set.contains(mapping.getHandlerName())) {
            set.add(mapping.getHandlerName());
        }
    }


    protected EventHandler getHandler(String name) throws InvalidHandlerException {

        if (!handlers.containsKey(name)) {
            throw new InvalidHandlerException("Cannot get the handler " + name + ", no such handler is registered.");
        } else {
            return this.handlers.get(name);
        }
    }


    protected Set<EventHandler> getHandlers(String event) {
        Set<EventHandler> set = new HashSet<EventHandler>();
        Set<String> s = events.get(event);
        if (s != null) {
            Iterator<String> itr = s.iterator();
            while (itr.hasNext()) {
                set.add(handlers.get(itr.next()));
            }

        }
        return set;
    }


    protected Set<EventHandler> getHandlers() {
        Set<EventHandler> set = new HashSet<EventHandler>();
        Iterator<EventHandler> itr = handlers.values().iterator();
        while (itr.hasNext()) {
            set.add(handlers.get(itr.next()));
        }

        return set;
    }


    public boolean isHandlerRegistered(String name) {
        if (handlers.containsKey(name)) {
            return true;
        } else {
            return false;
        }
    }


    public void registerHandler(EventHandler handler) throws InvalidHandlerException {
        if (Utils.clean(handler.getName()) == null) {
            throw new InvalidHandlerException("Invalid name provided for handler.");
        }

        if (!handlers.containsKey(handler.getName())) {
            handlers.put(handler.getName(), handler);
        } else {
            throw new InvalidHandlerException("The handler " + handler.getName() + " is already registered.");
        }
    }


    public void unregisterHandler(String name) {
        handlers.remove(name);
        Iterator<Set<String>> itr = events.values().iterator();
        while (itr.hasNext()) {
            itr.next().remove(name);
        }
    }


    public void unregisterEventWithHandler(String eventName, String handlerName) {
        Set set = events.get(eventName);
        if (set != null) {
            set = new HashSet<String>();
            set.remove(handlerName);
        }
    }


    public void clearHandlers() {
        Iterator<EventHandler> itr = handlers.values().iterator();
        while (itr.hasNext()) {
            try {
                itr.next().clear();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }


    public List<Event> findEvents(String targetId, String reportingPartyId, String eventType, Date start, Date end,
        String message) throws EventAuditingException {
        Set<EventHandler> temp = null;
        if (eventType != null) {
            temp = getHandlers(eventType);
        } else {
            temp = getHandlers();
        }
        List<Event> list = new ArrayList<Event>();
        Iterator<EventHandler> itr = temp.iterator();
        while (itr.hasNext()) {
            EventHandler eh = itr.next();
            if (eh instanceof Auditor) {
                Auditor a = (Auditor) eh;
                list.addAll(a.findEvents(targetId, reportingPartyId, eventType, start, end, message));
            }
        }
        return list;
    }
}
