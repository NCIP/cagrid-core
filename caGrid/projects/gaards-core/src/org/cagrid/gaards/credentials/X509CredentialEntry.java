package org.cagrid.gaards.credentials;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.globus.gsi.GlobusCredential;


public class X509CredentialEntry {

    private X509CredentialDescriptor descriptor;
    private GlobusCredential credential;
    private boolean isDefault;


    public X509CredentialEntry(X509CredentialDescriptor des) throws Exception {
        try {
            this.descriptor = des;
            this.isDefault = false;
            EncodedCertificates ec = des.getEncodedCertificates();
            if (ec == null) {
                throw new Exception("No certificates provided.");
            }
            String[] list = ec.getEncodedCertificate();
            if (list == null || list.length <= 0) {
                throw new Exception("No certificates provided.");
            }
            X509Certificate[] certs = new X509Certificate[list.length];
            for (int i = 0; i < list.length; i++) {
                certs[i] = CertUtil.loadCertificate(list[i]);
            }
            if (des.getEncodedKey() == null) {
                throw new Exception("No key provided.");
            }
            PrivateKey key = KeyUtil.loadPrivateKey(new ByteArrayInputStream(des.getEncodedKey().getBytes()), null);
            this.credential = new GlobusCredential(key, certs);
        } catch (Exception e) {
            throw new Exception("An error occured creating the credential entry: " + e.getMessage() + ".", e);
        }
    }


    public String getIdentity() {
        return this.getCredential().getIdentity();
    }


    public GlobusCredential getCredential() {
        return credential;
    }


    public String getDisplayName() {
        return this.getIdentity();
    }


    public String getDescription() {
        return this.getIdentity();
    }


    public String toString() {
        return getDisplayName();
    }


    public boolean equals(Object obj) {
        if ((obj == null) || (!(obj instanceof X509CredentialEntry))) {
            return false;
        }
        X509CredentialEntry d = (X509CredentialEntry) obj;
        if (this.getIdentity().equals(d.getIdentity())) {
            return true;
        } else {
            return false;
        }
    }


    public X509CredentialDescriptor getDescriptor() {
        return descriptor;
    }


    public boolean isDefault() {
        return isDefault;
    }


    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

}
