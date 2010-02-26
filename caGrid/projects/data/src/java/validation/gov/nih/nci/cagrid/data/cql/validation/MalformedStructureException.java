package gov.nih.nci.cagrid.data.cql.validation;

import gov.nih.nci.cagrid.data.MalformedQueryException;

public class MalformedStructureException extends MalformedQueryException implements StructureValidationError {

    public MalformedStructureException(String message) {
        super(message);
    }
    
    
    public MalformedStructureException(Exception ex) {
        super(ex);
    }
    
    
    public MalformedStructureException(String message, Exception ex) {
        super(message, ex);
    }
}
