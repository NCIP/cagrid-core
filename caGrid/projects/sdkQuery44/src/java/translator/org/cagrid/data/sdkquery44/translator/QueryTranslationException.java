package org.cagrid.data.sdkquery44.translator;

/**
 * QueryTranslationException
 * Thrown when an error occurs during query translation
 * 
 * @author David
 */
public class QueryTranslationException extends Exception {

    public QueryTranslationException(String message) {
        super(message);
    }
    
    
    public QueryTranslationException(Exception cause) {
        super(cause);
    }
    
    
    public QueryTranslationException(String message, Exception cause) {
        super(message, cause);
    }
}
