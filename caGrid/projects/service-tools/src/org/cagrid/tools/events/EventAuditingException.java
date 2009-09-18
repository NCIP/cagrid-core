package org.cagrid.tools.events;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class EventAuditingException extends Exception {

	public EventAuditingException() {
		super();
	}


	public EventAuditingException(String message) {
		super(message);
	}


	public EventAuditingException(Throwable cause) {
		super(cause);
	}


	public EventAuditingException(String message, Throwable cause) {
		super(message, cause);
	}

}
