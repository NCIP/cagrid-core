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
package org.cagrid.tools.events;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:ervin@bmi.osu.edu">David Ervin</A>
 */
public class InvalidHandlerException extends Exception {

	public InvalidHandlerException() {
		// TODO Auto-generated constructor stub
	}


	public InvalidHandlerException(String message) {
		super(message);
	}


	public InvalidHandlerException(Throwable cause) {
		super(cause);
	}


	public InvalidHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

}
