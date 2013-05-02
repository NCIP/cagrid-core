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
