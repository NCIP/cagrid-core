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
