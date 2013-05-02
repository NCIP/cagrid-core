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
 *  MalformedQueryException
 *  Exception thrown when a CQL query does not conform to the CQL schema
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 25, 2006 
 * @version $Id$ 
 */
public class MalformedQueryException extends Exception {

	public MalformedQueryException(String message) {
		super(message);
	}
	
	
	public MalformedQueryException(Exception ex) {
		super(ex);
	}
	
	
	public MalformedQueryException(String message, Exception ex) {
		super(message, ex);
	}
}
