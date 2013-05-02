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
