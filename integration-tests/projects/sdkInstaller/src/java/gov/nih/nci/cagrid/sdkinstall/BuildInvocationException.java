package gov.nih.nci.cagrid.sdkinstall;

/** 
 *  BuildInvocationException
 *  Exception thrown when the build process for the SDK fails
 * 
 * @author David Ervin
 * 
 * @created Jun 18, 2007 10:30:38 AM
 * @version $Id: BuildInvocationException.java,v 1.1 2007-06-18 15:29:39 dervin Exp $ 
 */
public class BuildInvocationException extends Exception {

    public BuildInvocationException(String message) {
        super(message);
    }
    
    
    public BuildInvocationException(Throwable cause) {
        super(cause);
    }
    
    
    public BuildInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
