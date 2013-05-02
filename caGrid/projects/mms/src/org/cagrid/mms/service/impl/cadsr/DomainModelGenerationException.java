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
public class DomainModelGenerationException extends Exception {

	/**
	 * 
	 */
	public DomainModelGenerationException() {
		super();
	}


	/**
	 * @param message
	 * @param cause
	 */
	public DomainModelGenerationException(String message, Throwable cause) {
		super(message, cause);
	}


	/**
	 * @param message
	 */
	public DomainModelGenerationException(String message) {
		super(message);
	}


	/**
	 * @param cause
	 */
	public DomainModelGenerationException(Throwable cause) {
		super(cause);
	}

}
