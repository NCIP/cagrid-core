package gov.nih.nci.cagrid.data.cql2.validation.walker;

public class Cql2WalkerException extends Exception {

    public Cql2WalkerException(String message) {
        super(message);
    }
    
    
    public Cql2WalkerException(Throwable cause) {
        super(cause);
    }
    
    
    public Cql2WalkerException(String message, Throwable cause) {
        super(message, cause);
    }
}
