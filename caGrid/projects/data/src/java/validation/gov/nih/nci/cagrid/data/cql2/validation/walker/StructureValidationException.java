package gov.nih.nci.cagrid.data.cql2.validation.walker;


public class StructureValidationException extends Cql2WalkerException {

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
