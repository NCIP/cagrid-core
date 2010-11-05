package gov.nih.nci.cagrid.gts.service;

import java.security.cert.CertificateException;

public class CertificateVerifyException extends CertificateException {

    public CertificateVerifyException() {
        super();
    }


    public CertificateVerifyException(String msg) {
        super(msg);
    }


    public CertificateVerifyException(Throwable cause) {
        super(cause);
    }


    public CertificateVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

}
