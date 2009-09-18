package gov.nih.nci.cagrid.introduce.extensions.sdk.discovery;

/**
 * @author oster
 * 
 */
public class SDKExecutionException extends Exception {

	/**
	 * 
	 */
	public SDKExecutionException() {
		super();
	}


	/**
	 * @param message
	 */
	public SDKExecutionException(String message) {
		super(message);
	}


	/**
	 * @param message
	 * @param cause
	 */
	public SDKExecutionException(String message, Throwable cause) {
		super(message, cause);
	}


	/**
	 * @param cause
	 */
	public SDKExecutionException(Throwable cause) {
		super(cause);
	}

}
