package org.cagrid.cql.utilities;

public class ResultsCreationException extends Exception {

    public ResultsCreationException(String message) {
        super(message);
    }
    
    
    public ResultsCreationException(Throwable cause) {
        super(cause);
    }
    
    
    public ResultsCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
