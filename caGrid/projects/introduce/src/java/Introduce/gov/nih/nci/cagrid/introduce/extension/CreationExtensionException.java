package gov.nih.nci.cagrid.introduce.extension;

/**
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created 
 */
public class CreationExtensionException extends Exception {

	public CreationExtensionException(String message) {
		super(message);
	}
	
	
	public CreationExtensionException(Throwable th) {
		super(th);
	}
	
	
	public CreationExtensionException(String message, Throwable th) {
		super(message, th);
	}
}
