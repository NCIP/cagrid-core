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
