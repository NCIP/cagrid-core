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
package gov.nih.nci.cagrid.metadata.exceptions;

/**
 * @author oster
 * 
 */
public class QueryInvalidException extends ResourcePropertyRetrievalException {

	/**
	 * 
	 */
	public QueryInvalidException() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param message
	 * @param cause
	 */
	public QueryInvalidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param message
	 */
	public QueryInvalidException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param cause
	 */
	public QueryInvalidException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
