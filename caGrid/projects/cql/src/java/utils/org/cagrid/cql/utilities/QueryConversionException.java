package org.cagrid.cql.utilities;


public class QueryConversionException extends Exception {

    public QueryConversionException(String message) {
        super(message);
    }


    public QueryConversionException(Exception ex) {
        super(ex);
    }


    public QueryConversionException(String message, Exception ex) {
        super(message, ex);
    }
}
