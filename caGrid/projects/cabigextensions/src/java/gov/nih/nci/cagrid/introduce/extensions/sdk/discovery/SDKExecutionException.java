/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
