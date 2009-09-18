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
