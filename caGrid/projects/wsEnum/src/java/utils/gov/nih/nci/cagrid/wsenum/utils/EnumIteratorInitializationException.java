package gov.nih.nci.cagrid.wsenum.utils;

/** 
 *  EnumIteratorInitializationException
 *  Exception thrown when initialization of an enum iterator fails
 * 
 * @author David Ervin
 * 
 * @created Apr 30, 2007 12:31:12 PM
 * @version $Id: EnumIteratorInitializationException.java,v 1.1 2007-05-16 15:00:57 dervin Exp $ 
 */
public class EnumIteratorInitializationException extends Exception {

    public EnumIteratorInitializationException(String message) {
        super(message);
    }
    
    
    public EnumIteratorInitializationException(Throwable th) {
        super(th);
    }
    
    
    public EnumIteratorInitializationException(String message, Throwable th) {
        super(message, th);
    }
}
