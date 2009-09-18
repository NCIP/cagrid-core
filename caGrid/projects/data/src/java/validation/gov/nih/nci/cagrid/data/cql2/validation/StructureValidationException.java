package gov.nih.nci.cagrid.data.cql2.validation;

import gov.nih.nci.cagrid.data.MalformedQueryException;

public class StructureValidationException extends MalformedQueryException {

    public StructureValidationException(String message) {
        super(message);
    }


    public StructureValidationException(Exception ex) {
        super(ex);
    }


    public StructureValidationException(String message, Exception ex) {
        super(message, ex);
    }
}
