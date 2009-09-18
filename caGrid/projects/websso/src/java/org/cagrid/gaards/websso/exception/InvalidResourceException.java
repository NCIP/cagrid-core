package org.cagrid.gaards.websso.exception;

public class InvalidResourceException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidResourceException(String message) {
		super(message);
	}

	public InvalidResourceException(String message, Throwable e) {
		super(message, e);
	}
}