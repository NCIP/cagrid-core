/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
