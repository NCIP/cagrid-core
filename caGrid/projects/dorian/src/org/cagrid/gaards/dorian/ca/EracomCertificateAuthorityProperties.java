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

import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;


public class EracomCertificateAuthorityProperties extends CertificateAuthorityProperties {

    private int slot;


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, int issuedCertificateKeySize,
        int slot) throws DorianInternalFault {
        this(certificateAuthorityPassword, null, issuedCertificateKeySize, false, null, false, null, slot);
    }


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, String policyOID,
        int issuedCertificateKeySize, int slot) throws DorianInternalFault {
        this(certificateAuthorityPassword, policyOID, issuedCertificateKeySize, false, null, false, null, slot);
    }


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, String policyOID,
        int issuedCertificateKeySize, boolean autoCreate, CertificateAuthorityCreationPolicy creationPolicy,
        boolean autoRenew, Lifetime renewalLifetime, int slot) throws DorianInternalFault {
        super(certificateAuthorityPassword, policyOID, issuedCertificateKeySize, autoCreate, creationPolicy, autoRenew,
            renewalLifetime);
        this.slot = slot;
    }


    public int getSlot() {
        return slot;
    }

}
