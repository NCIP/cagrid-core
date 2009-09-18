package gov.nih.nci.cagrid.common.security.commstyle;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CommunicationStyleException extends Exception {
	public CommunicationStyleException() {
		super();
	}


	public CommunicationStyleException(String s) {
		super(s);
	}


	public CommunicationStyleException(String s, Throwable t) {
		super(s, t);
	}

}