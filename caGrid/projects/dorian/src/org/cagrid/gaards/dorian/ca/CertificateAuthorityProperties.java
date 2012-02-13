package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.Utils;

import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;


public class CertificateAuthorityProperties {
    private String policyOID;
    private int issuedCertificateKeySize;
    private Lifetime renewalLifetime;
    private boolean autoRenewCA;
    private boolean autoCreateCA;
    private String certificateAuthorityPassword;
    private CertificateAuthorityCreationPolicy creationPolicy;


    public CertificateAuthorityProperties(String certificateAuthorityPassword, int issuedCertificateKeySize)
        throws DorianInternalFault {
        this(certificateAuthorityPassword, null, issuedCertificateKeySize, false, null, false, null);
    }


    public CertificateAuthorityProperties(String certificateAuthorityPassword, String policyOID,
        int issuedCertificateKeySize) throws DorianInternalFault {
        this(certificateAuthorityPassword, policyOID, issuedCertificateKeySize, false, null, false, null);
    }


    public CertificateAuthorityProperties(String certificateAuthorityPassword, String policyOID,
        int issuedCertificateKeySize, boolean autoCreate, CertificateAuthorityCreationPolicy creationPolicy,
        boolean autoRenew, Lifetime renewalLifetime) throws DorianInternalFault {
        this.certificateAuthorityPassword = certificateAuthorityPassword;
        this.policyOID = Utils.clean(policyOID);

        if (KeySizeValidator.isKeySizeValid(issuedCertificateKeySize)) {
            this.issuedCertificateKeySize = issuedCertificateKeySize;
        } else {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("Could not initialize CA, invalid key size specified.");
            throw f;
        }

        this.autoCreateCA = autoCreate;

        if ((this.autoCreateCA) && (creationPolicy == null)) {
            DorianInternalFault f = new DorianInternalFault();
            f
                .setFaultString("Could not initialize CA, auto creation is enabled however no creation policy was specified.");
            throw f;
        } else {
            this.creationPolicy = creationPolicy;
        }

        this.autoRenewCA = autoRenew;

        if ((this.autoRenewCA) && (renewalLifetime == null)) {
            DorianInternalFault f = new DorianInternalFault();
            f
                .setFaultString("Could not initialize CA, auto renewal is enabled however no renewal lifetime was specified.");
            throw f;
        } else {
            this.renewalLifetime = renewalLifetime;
        }
    }


    public String getPolicyOID() {
        return policyOID;
    }


    public int getIssuedCertificateKeySize() {
        return issuedCertificateKeySize;
    }


    public Lifetime getRenewalLifetime() {
        return renewalLifetime;
    }


    public boolean isAutoRenewCAEnabled() {
        return autoRenewCA;
    }


    public boolean isAutoCreateCAEnabled() {
        return autoCreateCA;
    }


    public CertificateAuthorityCreationPolicy getCreationPolicy() {
        return creationPolicy;
    }


    public String getCertificateAuthorityPassword() {
        return certificateAuthorityPassword;
    }


    public void setPolicyOID(String policyOID) {
        this.policyOID = policyOID;
    }


    public void setIssuedCertificateKeySize(int issuedCertificateKeySize) {
        this.issuedCertificateKeySize = issuedCertificateKeySize;
    }


    public void setRenewalLifetime(Lifetime renewalLifetime) {
        this.renewalLifetime = renewalLifetime;
    }


    public void setAutoRenewCA(boolean autoRenewCA) {
        this.autoRenewCA = autoRenewCA;
    }


    public void setAutoCreateCA(boolean autoCreateCA) {
        this.autoCreateCA = autoCreateCA;
    }


    public void setCertificateAuthorityPassword(String certificateAuthorityPassword) {
        this.certificateAuthorityPassword = certificateAuthorityPassword;
    }


    public void setCreationPolicy(CertificateAuthorityCreationPolicy creationPolicy) {
        this.creationPolicy = creationPolicy;
    }

}
