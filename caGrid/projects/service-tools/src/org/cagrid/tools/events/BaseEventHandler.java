package org.cagrid.tools.events;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public abstract class BaseEventHandler implements EventHandler {
	private String name;

	private Log log;

	public BaseEventHandler(String name) {
		this.name = name;
		this.log = LogFactory.getLog(this.getClass().getName());
	}

	public Log getLog() {
		return log;
	}

	public String getName() {
		return name;
	}

	public abstract void handleEvent(Event event) throws EventHandlingException;

}
