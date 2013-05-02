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
package org.cagrid.identifiers.namingauthority;

public class NamingAuthoritySecurityException extends Exception {

    private static final long serialVersionUID = 1L;


    public NamingAuthoritySecurityException() {
    }


    public NamingAuthoritySecurityException(String message) {
        super(message);
    }


    public NamingAuthoritySecurityException(Throwable cause) {
        super(cause);
    }


    public NamingAuthoritySecurityException(String message, Throwable cause) {
        super(message, cause);
    }

}
