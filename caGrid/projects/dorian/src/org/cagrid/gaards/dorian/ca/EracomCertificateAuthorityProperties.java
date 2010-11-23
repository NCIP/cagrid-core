package org.cagrid.gaards.dorian.ca;

import org.cagrid.gaards.core.EracomUtils;
import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;


public class EracomCertificateAuthorityProperties extends CertificateAuthorityProperties {
    private int slot;


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, int issuedCertificateKeySize,
        int slot) throws DorianInternalFault {
        this(certificateAuthorityPassword, null, EracomUtils.getEracomCryptoAlgorithm(), issuedCertificateKeySize, false, null, false, null, slot);
    }


    public EracomCertificateAuthorityProperties(String certificateAuthorityPassword, String policyOID,
        int issuedCertificateKeySize, int slot) throws DorianInternalFault {
        this(certificateAuthorityPassword, policyOID, EracomUtils.getEracomCryptoAlgorithm(), issuedCertificateKeySize, false, null, false, null, slot);
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
