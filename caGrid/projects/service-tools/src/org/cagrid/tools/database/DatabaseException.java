package org.cagrid.tools.database;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class DatabaseException extends Exception {

	public DatabaseException() {
		super();
	}


	public DatabaseException(String message) {
		super(message);
	}


	public DatabaseException(Throwable cause) {
		super(cause);
	}


	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
