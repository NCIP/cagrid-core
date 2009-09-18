package org.cagrid.gaards.websso.exception;

public class GenericException extends Exception {

	private static final long serialVersionUID = 1L;

	public GenericException(String message) {
		super(message);
	}

	public GenericException(String message, Throwable e) {
		super(message, e);
	}
}