package gov.nih.nci.cagrid.testing.system.deployment;


/** 
 *  ContainerException
 *  Exception to be thrown when operations on a web service container fail
 * 
 * @author David Ervin
 * 
 * @created Oct 12, 2007 10:06:26 AM
 * @version $Id: ContainerException.java,v 1.1 2008-05-14 17:17:42 hastings Exp $ 
 */
public class ContainerException extends Exception {

    public ContainerException(Exception cause) {
        super(cause);
    }
    
    
    public ContainerException(String message) {
        super(message);
    }
    
    
    public ContainerException(String message, Exception cause) {
        super(message, cause);
    }
}
