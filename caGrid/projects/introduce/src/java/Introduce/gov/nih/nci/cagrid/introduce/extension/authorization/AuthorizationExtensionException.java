package gov.nih.nci.cagrid.introduce.extension.authorization;

/**
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created 
 */
public class AuthorizationExtensionException extends Exception {

	/**
     * Hash code for serialization.
     */
    private static final long serialVersionUID = -4343954700815512841L;


    public AuthorizationExtensionException(String message) {
		super(message);
	}
	
	
	public AuthorizationExtensionException(Throwable th) {
		super(th);
	}
	
	
	public AuthorizationExtensionException(String message, Throwable th) {
		super(message, th);
	}
}
