package gov.nih.nci.cagrid.sdkinstall;

/** 
 *  DeployInvocationException
 *  Exception thrown when invocation of the SDK's deployment process fails
 * 
 * @author David Ervin
 * 
 * @created Jun 18, 2007 12:07:19 PM
 * @version $Id: DeployInvocationException.java,v 1.1 2007-06-18 17:23:23 dervin Exp $ 
 */
public class DeployInvocationException extends Exception {

    public DeployInvocationException(String message) {
        super(message);
    }
    
    
    public DeployInvocationException(Throwable cause) {
        super(cause);
    }
    
    
    public DeployInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
