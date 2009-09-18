package gov.nih.nci.cagrid.data.utilities;

/** 
 *  ResultsCreationException
 *  Exception thrown when creating CQLQueryResults goes awry
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 16, 2006 
 * @version $Id$ 
 */
public class ResultsCreationException extends Exception {

	public ResultsCreationException(String message) {
		super(message);
	}
	
	
	public ResultsCreationException(Exception ex) {
		super(ex);
	}
	
	
	public ResultsCreationException(String message, Exception ex) {
		super(message, ex);
	}
}
