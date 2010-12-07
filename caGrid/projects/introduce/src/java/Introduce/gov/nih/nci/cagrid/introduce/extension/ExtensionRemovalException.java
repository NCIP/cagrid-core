package gov.nih.nci.cagrid.introduce.extension;

/**
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created 
 */
public class ExtensionRemovalException extends Exception {

	/**
     * Hash code for serialization
     */
    private static final long serialVersionUID = -3358222009988143217L;


    public ExtensionRemovalException(String message) {
		super(message);
	}
	
	
	public ExtensionRemovalException(Throwable th) {
		super(th);
	}
	
	
	public ExtensionRemovalException(String message, Throwable th) {
		super(message, th);
	}
}
