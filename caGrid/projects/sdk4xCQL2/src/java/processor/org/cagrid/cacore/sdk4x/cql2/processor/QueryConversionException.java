package org.cagrid.cacore.sdk4x.cql2.processor;

public class QueryConversionException extends Exception {

    public QueryConversionException(String message) {
        super(message);
    }
    
    
    public QueryConversionException(Throwable cause) {
        super(cause);
    }
    
    
    public QueryConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
