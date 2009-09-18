package org.cagrid.tools.events;

public interface EventHandler {
	public void handleEvent(Event event) throws EventHandlingException;
	public String getName();
	public void clear() throws EventHandlingException;
}
