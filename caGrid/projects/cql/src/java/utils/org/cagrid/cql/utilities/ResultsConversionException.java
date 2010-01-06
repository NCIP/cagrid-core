package org.cagrid.cql.utilities;

public class ResultsConversionException extends Exception {

    public ResultsConversionException(String message) {
        super(message);
    }
    
    
    public ResultsConversionException(Exception cause) {
        super(cause);
    }
    
    
    public ResultsConversionException(String message, Exception cause) {
        super(message, cause);
    }
}
