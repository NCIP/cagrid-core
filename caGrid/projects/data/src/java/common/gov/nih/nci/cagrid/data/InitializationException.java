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
package gov.nih.nci.cagrid.data;

/** 
 *  InvalidInitializationException
 *  Thrown when the CQLQueryProcessor cannot process it's initialization string
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 31, 2006 
 * @version $Id$ 
 */
public class InitializationException extends Exception {

	public InitializationException(String message) {
		super(message);
	}
	
	
	public InitializationException(Exception ex) {
		super(ex);
	}
	
	
	public InitializationException(String message, Exception ex) {
		super(message, ex);
	}
}
