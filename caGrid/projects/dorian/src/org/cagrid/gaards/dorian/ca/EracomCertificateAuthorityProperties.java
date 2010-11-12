package org.cagrid.gaards.dorian.ca;

import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;


public class EracomCertificateAuthorityProperties extends CertificateAuthorityProperties {
    public static final String DEFAULT_ERACOM_SIGNATURE_ALGORITHM = "SHA256WithRSA";

    private int slot;


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, int issuedCertificateKeySize,
        int slot) throws DorianInternalFault {
        this(certificateAuthorityPassword, null, DEFAULT_ERACOM_SIGNATURE_ALGORITHM, issuedCertificateKeySize, false, null, false, null, slot);
    }


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, String policyOID,
        int issuedCertificateKeySize, int slot) throws DorianInternalFault {
        this(certificateAuthorityPassword, policyOID, DEFAULT_ERACOM_SIGNATURE_ALGORITHM, issuedCertificateKeySize, false, null, false, null, slot);
    }


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, String policyOID,
        String signatureAlgorithm, int issuedCertificateKeySize, boolean autoCreate, 
        CertificateAuthorityCreationPolicy creationPolicy,
        boolean autoRenew, Lifetime renewalLifetime, int slot) throws DorianInternalFault {
        super(certificateAuthorityPassword, policyOID, signatureAlgorithm, issuedCertificateKeySize, autoCreate,
            creationPolicy, autoRenew, renewalLifetime);
        this.slot = slot;
    }


    public int getSlot() {
        return slot;
    }

}
