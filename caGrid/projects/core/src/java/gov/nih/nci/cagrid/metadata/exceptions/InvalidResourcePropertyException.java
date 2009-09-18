package gov.nih.nci.cagrid.metadata.exceptions;

/**
 * @author oster
 * 
 */
public class InvalidResourcePropertyException extends RemoteResourcePropertyRetrievalException {

	/**
	 * 
	 */
	public InvalidResourcePropertyException() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param message
	 * @param cause
	 */
	public InvalidResourcePropertyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param message
	 */
	public InvalidResourcePropertyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param cause
	 */
	public InvalidResourcePropertyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
