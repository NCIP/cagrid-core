package org.cagrid.identifiers.namingauthority;

public class InvalidIdentifierException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public InvalidIdentifierException() {
        super();
    }


    public InvalidIdentifierException(String message, Throwable cause) {
        super(message, cause);
    }


    public InvalidIdentifierException(String message) {
        super(message);
    }


    public InvalidIdentifierException(Throwable cause) {
        super(cause);
    }

}
