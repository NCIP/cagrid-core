package gov.nih.nci.cagrid.data.cql2.validation;

import gov.nih.nci.cagrid.data.MalformedQueryException;

public class DomainValidationException extends MalformedQueryException {

    public DomainValidationException(String message) {
        super(message);
    }


    public DomainValidationException(Exception ex) {
        super(ex);
    }


    public DomainValidationException(String message, Exception ex) {
        super(message, ex);
    }
}
