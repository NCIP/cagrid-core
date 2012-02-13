package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.Utils;

import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;


public class CertificateAuthorityCreationPolicy {
    private String subject;
    private int keySize;
    private Lifetime lifetime;


    public CertificateAuthorityCreationPolicy(String subject, int keySize, Lifetime lifetime)
        throws DorianInternalFault {

        if (Utils.clean(subject) == null) {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("Could not initialize CA, invalid subject specified.");
            throw f;
        }

        this.subject = subject;

        if (KeySizeValidator.isKeySizeValid(keySize)) {
            this.keySize = keySize;
        } else {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("Could not initialize CA, invalid key size specified.");
            throw f;
        }

        if (lifetime == null) {
            DorianInternalFault f = new DorianInternalFault();
            f.setFaultString("Could not initialize CA, invalid lifetime specified.");
            throw f;
        }

        this.lifetime = lifetime;

    }


    public String getSubject() {
        return subject;
    }


    public int getKeySize() {
        return keySize;
    }


    public Lifetime getLifetime() {
        return lifetime;
    }
}
