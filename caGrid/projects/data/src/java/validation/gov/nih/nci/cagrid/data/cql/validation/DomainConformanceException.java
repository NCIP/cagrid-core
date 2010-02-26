package gov.nih.nci.cagrid.data.cql.validation;

import gov.nih.nci.cagrid.data.MalformedQueryException;

public class DomainConformanceException extends MalformedQueryException implements DomainValidationError {

    public DomainConformanceException(String message) {
        super(message);
    }


    public DomainConformanceException(Exception ex) {
        super(ex);
    }


    public DomainConformanceException(String message, Exception ex) {
        super(message, ex);
    }
}
