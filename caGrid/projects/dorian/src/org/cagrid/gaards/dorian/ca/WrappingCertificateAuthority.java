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
package org.cagrid.gaards.dorian.ca;

import java.security.PrivateKey;


public interface WrappingCertificateAuthority {
    public WrappedKey wrap(PrivateKey key) throws CertificateAuthorityFault;


    public PrivateKey unwrap(WrappedKey key) throws CertificateAuthorityFault;
}
