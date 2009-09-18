package org.cagrid.tools.groups;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class GroupException extends Exception {

	public GroupException() {
		
	}


	public GroupException(String message) {
		super(message);
	}


	public GroupException(Throwable cause) {
		super(cause);
	}


	public GroupException(String message, Throwable cause) {
		super(message, cause);
	}

}
