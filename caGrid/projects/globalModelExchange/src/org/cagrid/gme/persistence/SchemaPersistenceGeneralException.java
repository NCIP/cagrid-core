/**
 * 
 */
package org.cagrid.gme.persistence;

/**
 * Indicates a general problem communicating with the persistence backend.
 * 
 * @author oster
 */
public class SchemaPersistenceGeneralException extends Exception {

	public SchemaPersistenceGeneralException() {
	}


	/**
	 * @param message
	 */
	public SchemaPersistenceGeneralException(String message) {
		super(message);
	}


	/**
	 * @param cause
	 */
	public SchemaPersistenceGeneralException(Throwable cause) {
		super(cause);
	}


	/**
	 * @param message
	 * @param cause
	 */
	public SchemaPersistenceGeneralException(String message, Throwable cause) {
		super(message, cause);
	}

}
