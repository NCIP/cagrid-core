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
