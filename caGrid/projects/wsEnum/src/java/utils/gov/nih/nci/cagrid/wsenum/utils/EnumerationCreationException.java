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
package gov.nih.nci.cagrid.wsenum.utils;

/** 
 *  EnumerationCreationException
 *  Generic exception for enumerate response factory to throw
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Aug 17, 2006 
 * @version $Id: EnumerationCreationException.java,v 1.1 2007-05-16 15:00:57 dervin Exp $ 
 */
public class EnumerationCreationException extends Exception {

	public EnumerationCreationException(String message) {
		super(message);
	}
	
	
	public EnumerationCreationException(Throwable th) {
		super(th);
	}
	
	
	public EnumerationCreationException(String message, Throwable th) {
		super(message, th);
	}
}
