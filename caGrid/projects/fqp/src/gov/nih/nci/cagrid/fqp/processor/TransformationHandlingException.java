package gov.nih.nci.cagrid.fqp.processor;

public class TransformationHandlingException extends Exception {

    public TransformationHandlingException(String message) {
        super(message);
    }
    
    
    public TransformationHandlingException(Throwable cause) {
        super(cause);
    }
    
    
    public TransformationHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
