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
 *  QueryProcessingException
 *  Exception thrown for general errors while processing a CQL Query
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Apr 28, 2006 
 * @version $Id$ 
 */
public class QueryProcessingException extends Exception {

	public QueryProcessingException(String message) {
		super(message);
	}
	
	
	public QueryProcessingException(Exception ex) {
		super(ex);
	}
	
	
	public QueryProcessingException(String message, Exception ex) {
		super(message, ex);
	}
}
