package gov.nih.nci.cagrid.fqp.common;

public class DCQLQueryConversionException extends Exception {

    public DCQLQueryConversionException(String message) {
        super(message);
    }
    
    
    public DCQLQueryConversionException(Throwable cause) {
        super(cause);
    }
    
    
    public DCQLQueryConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
