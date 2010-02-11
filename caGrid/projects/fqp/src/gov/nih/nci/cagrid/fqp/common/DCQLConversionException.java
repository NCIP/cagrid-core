package gov.nih.nci.cagrid.fqp.common;

public class DCQLConversionException extends Exception {

    public DCQLConversionException(String message) {
        super(message);
    }
    
    
    public DCQLConversionException(Throwable cause) {
        super(cause);
    }
    
    
    public DCQLConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
