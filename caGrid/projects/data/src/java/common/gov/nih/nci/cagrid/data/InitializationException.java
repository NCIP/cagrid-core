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
