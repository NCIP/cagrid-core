package gov.nih.nci.cagrid.sdkinstall;

/** 
 *  DeploymentConfigurationException
 *  Exception which may be thrown when configuration of the SDK's
 *  deployment fails
 * 
 * @author David Ervin
 * 
 * @created Jun 15, 2007 11:22:28 AM
 * @version $Id: DeploymentConfigurationException.java,v 1.1 2007-06-15 16:57:33 dervin Exp $ 
 */
public class DeploymentConfigurationException extends Exception {

    public DeploymentConfigurationException(String message) {
        super(message);
    }
    
    
    public DeploymentConfigurationException(Throwable cause) {
        super(cause);
    }
    
    
    public DeploymentConfigurationException(String messageg, Throwable cause) {
        super(messageg, cause);
    }
}
