package org.cagrid.gaards.credentials;

import org.globus.gsi.GlobusCredential;


public class CredentialEntryFactory {
    public static X509CredentialEntry getEntry(X509CredentialDescriptor des) throws Exception {
        if (des instanceof DorianUserCredentialDescriptor) {
            return new DorianUserCredentialEntry((DorianUserCredentialDescriptor) des);
        } else {
            return new X509CredentialEntry(des);
        }
    }


    public static X509CredentialEntry getEntry(GlobusCredential cred) throws Exception {
        X509CredentialDescriptor des = EncodingUtil.encode(cred);
        return new X509CredentialEntry(des);
    }
}
