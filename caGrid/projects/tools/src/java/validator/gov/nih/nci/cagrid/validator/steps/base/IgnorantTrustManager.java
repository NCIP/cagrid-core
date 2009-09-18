package gov.nih.nci.cagrid.validator.steps.base;

/**
  *  IgnorantTrustManager
  *  TODO:DOCUMENT ME
  * 
  * @author David Ervin
  * 
  * @created Mar 28, 2008 3:28:59 PM
  * @version $Id: IgnorantTrustManager.java,v 1.1 2008-03-31 16:02:24 dervin Exp $
 */
public class IgnorantTrustManager implements javax.net.ssl.TrustManager, javax.net.ssl.X509TrustManager {
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }


    public boolean isServerTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }


    public boolean isClientTrusted(java.security.cert.X509Certificate[] certs) {
        return true;
    }


    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
        throws java.security.cert.CertificateException {
        return;
    }


    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
        throws java.security.cert.CertificateException {
        return;
    }
}