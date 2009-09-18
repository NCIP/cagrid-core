package org.cagrid.tools.events;

public class EventToHandlerMapping {
	
	private String handlerName;
	private String eventName;
	public EventToHandlerMapping(String eventName, String handlerName){
		this.handlerName = handlerName;
		this.eventName = eventName;
	}
	public String getHandlerName() {
		return handlerName;
	}
	public String getEventName() {
		return eventName;
	}
}
