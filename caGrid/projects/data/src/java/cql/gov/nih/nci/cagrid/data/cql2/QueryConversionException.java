package gov.nih.nci.cagrid.data.cql2;

import gov.nih.nci.cagrid.data.QueryProcessingException;

public class QueryConversionException extends QueryProcessingException {

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
