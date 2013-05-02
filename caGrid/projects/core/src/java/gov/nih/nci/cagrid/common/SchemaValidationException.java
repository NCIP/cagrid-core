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
package gov.nih.nci.cagrid.common;

/** 
 *  SchemaValidationException
 *  Exception thrown when schema validation goes awry
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created May 25, 2006 
 * @version $Id$ 
 */
public class SchemaValidationException extends Exception {

	public SchemaValidationException(String message) {
		super(message);
	}
	
	
	public SchemaValidationException(Exception ex) {
		super(ex);
	}
	
	
	public SchemaValidationException(String message, Exception ex) {
		super(message, ex);
	}
}
