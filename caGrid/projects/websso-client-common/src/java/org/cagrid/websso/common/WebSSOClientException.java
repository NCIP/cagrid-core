package org.cagrid.websso.common;

public class WebSSOClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebSSOClientException(String message) {
		super(message);
	}

	public WebSSOClientException(String message, Throwable e) {
		super(message, e);
	}
}
