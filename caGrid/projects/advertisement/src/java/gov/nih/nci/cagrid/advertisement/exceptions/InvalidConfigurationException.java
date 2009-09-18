package gov.nih.nci.cagrid.advertisement.exceptions;

public class InvalidConfigurationException extends Exception {

    public InvalidConfigurationException() {
        super();
    }


    public InvalidConfigurationException(String message) {
        super(message);
    }


    public InvalidConfigurationException(Throwable cause) {
        super(cause);
    }


    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

}
