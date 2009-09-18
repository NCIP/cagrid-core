package org.cagrid.mms.service.impl.cadsr;

/**
 * @author oster
 * 
 */
public class CaDSRGeneralException extends Exception {

	/**
	 * 
	 */
	public CaDSRGeneralException() {
		super();
	}


	/**
	 * @param message
	 * @param cause
	 */
	public CaDSRGeneralException(String message, Throwable cause) {
		super(message, cause);
	}


	/**
	 * @param message
	 */
	public CaDSRGeneralException(String message) {
		super(message);
	}


	/**
	 * @param cause
	 */
	public CaDSRGeneralException(Throwable cause) {
		super(cause);
	}

}
