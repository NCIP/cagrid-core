package gov.nih.nci.cagrid.data.cql2.validation.walker;

public class DomainValidationException extends Cql2WalkerException {

    public DomainValidationException(String message) {
        super(message);
    }


    public DomainValidationException(Throwable cause) {
        super(cause);
    }


    public DomainValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
