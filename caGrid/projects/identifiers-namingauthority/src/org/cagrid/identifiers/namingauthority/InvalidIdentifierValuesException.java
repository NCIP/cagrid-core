package org.cagrid.identifiers.namingauthority;

public class InvalidIdentifierValuesException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public InvalidIdentifierValuesException() {
    }


    public InvalidIdentifierValuesException(String message) {
        super(message);
    }


    public InvalidIdentifierValuesException(Throwable cause) {
        super(cause);
    }


    public InvalidIdentifierValuesException(String message, Throwable cause) {
        super(message, cause);
    }

}
