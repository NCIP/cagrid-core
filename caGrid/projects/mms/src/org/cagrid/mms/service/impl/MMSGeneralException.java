package org.cagrid.mms.service.impl;

@SuppressWarnings("serial")
public class MMSGeneralException extends Exception {

    public MMSGeneralException() {
    }


    public MMSGeneralException(String message) {
        super(message);
    }


    public MMSGeneralException(Throwable cause) {
        super(cause);
    }


    public MMSGeneralException(String message, Throwable cause) {
        super(message, cause);
    }

}
