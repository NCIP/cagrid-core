package gov.nih.nci.cagrid.data.service;

/** 
 *  DataServiceInitializationException
 *  Exception thrown when a data service fails to initialize
 * 
 * @author David Ervin
 * 
 * @created Jun 11, 2007 11:47:22 AM
 * @version $Id: DataServiceInitializationException.java,v 1.1 2007-06-11 17:05:34 dervin Exp $ 
 */
public class DataServiceInitializationException extends Exception {

    public DataServiceInitializationException(String message) {
        super(message);
    }
    
    
    public DataServiceInitializationException(Throwable th) {
        super(th);
    }
    
    
    public DataServiceInitializationException(String message, Throwable th) {
        super(message, th);
    }
}
