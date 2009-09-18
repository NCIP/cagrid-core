package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import org.bouncycastle.asn1.x509.X509Name;
import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.common.LoggingObject;
import org.cagrid.gaards.dorian.service.util.Utils;
import org.cagrid.gaards.pki.CRLEntry;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public abstract class CertificateAuthority extends LoggingObject {

    private boolean initialized = false;

    private CertificateAuthorityProperties properties;


    public CertificateAuthority(CertificateAuthorityProperties properties) {
        this.properties = properties;
    }


    public abstract String getUserCredentialsProvider();


    public abstract String getCACredentialsProvider();


    public abstract String getSignatureAlgorithm();


    public abstract boolean hasCACredentials() throws CertificateAuthorityFault;


    public abstract void setCACredentials(X509Certificate cert, PrivateKey key, String password)
        throws CertificateAuthorityFault;


    public abstract void deleteCACredentials() throws CertificateAuthorityFault;


    public abstract PrivateKey getPrivateKey(String password) throws CertificateAuthorityFault, NoCACredentialsFault;


    protected abstract java.security.cert.X509Certificate getCertificate() throws CertificateAuthorityFault;


    public void clearCertificateAuthority() throws CertificateAuthorityFault {
        deleteCACredentials();
        this.initialized = false;
    }


    private synchronized void init() throws CertificateAuthorityFault {
        try {
            if (!initialized) {
                if (!hasCACredentials()) {
                    if (properties.isAutoCreateCAEnabled()) {
                        Lifetime lifetime = properties.getCreationPolicy().getLifetime();
                        this.createCertifcateAuthorityCredentials(properties.getCreationPolicy().getSubject(), Utils
                            .getExpiredDate(lifetime), properties.getCreationPolicy().getKeySize());
                    }
                }
                initialized = true;
            }
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not initialize the Dorian Certificate Authority.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    private void createCertifcateAuthorityCredentials(String dn, Date expirationDate, int keySize)
        throws CertificateAuthorityFault, NoCACredentialsFault {
        try {
            KeyPair pair = KeyUtil.generateRSAKeyPair(getCACredentialsProvider(), keySize);
            X509Certificate cacert = CertUtil.generateCACertificate(getCACredentialsProvider(), new X509Name(dn),
                new Date(), expirationDate, pair, getSignatureAlgorithm());
            deleteCACredentials();
            this.setCACredentials(cacert, pair.getPrivate(), properties.getCertificateAuthorityPassword());
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not create the CA credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public java.security.cert.X509Certificate getCACertificate() throws CertificateAuthorityFault, NoCACredentialsFault {
        return getCACertificate(true);
    }


    private java.security.cert.X509Certificate getCACertificate(boolean errorOnExpiredCredentials)
        throws CertificateAuthorityFault, NoCACredentialsFault {
        X509Certificate cert = null;
        init();
        try {
            if (!hasCACredentials()) {
                NoCACredentialsFault fault = new NoCACredentialsFault();
                fault.setFaultString("No certificate exists for the CA.");
                throw fault;
            } else {
                cert = getCertificate();
            }
        } catch (NoCACredentialsFault f) {
            throw f;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, Error obtaining the CA private key.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }

        if (errorOnExpiredCredentials) {
            Date now = new Date();
            if (now.before(cert.getNotBefore()) || (now.after(cert.getNotAfter()))) {
                if (properties.isAutoRenewCAEnabled()) {
                    Lifetime lifetime = properties.getRenewalLifetime();
                    return renewCertifcateAuthorityCredentials(Utils.getExpiredDate(lifetime));

                } else {
                    NoCACredentialsFault fault = new NoCACredentialsFault();
                    fault.setFaultString("The CA certificate had expired or is not valid at this time.");
                    throw fault;
                }
            }
        }
        return cert;
    }


    public synchronized X509Certificate signCertificate(String subject, PublicKey publicKey, Date start, Date expiration)
        throws CertificateAuthorityFault, NoCACredentialsFault {
        init();
        X509Certificate cacert = getCACertificate();

        Date caDate = cacert.getNotAfter();
        if (start.after(caDate)) {
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Certificate start date is after the CA certificates expiration date.");
            throw fault;
        }
        if (expiration.after(caDate)) {
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Certificate expiration date is after the CA certificates expiration date.");
            throw fault;
        }

        try {
            // VALIDATE DN
            String caSubject = cacert.getSubjectDN().getName();
            int caindex = caSubject.lastIndexOf(",");
            String caPreSub = caSubject.substring(0, caindex);

            if (!subject.startsWith(caPreSub)) {
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("Invalid certificate subject, the subject must start with, " + caPreSub);
                throw fault;
            }
            X509Certificate cert = CertUtil.generateCertificate(getCACredentialsProvider(), new X509Name(subject),
                start, expiration, publicKey, cacert, getPrivateKey(), getSignatureAlgorithm(), properties
                    .getPolicyOID());
            return cert;
        } catch (CertificateAuthorityFault e) {
            throw e;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not sign certificate.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public synchronized X509Certificate signHostCertificate(String host, PublicKey publicKey, Date start,
        Date expiration) throws CertificateAuthorityFault, NoCACredentialsFault {
        init();
        X509Certificate cacert = getCACertificate();
        try {
            String subject = Utils.getHostCertificateSubject(cacert, host);
            return signCertificate(subject, publicKey, start, expiration);
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not sign host certificate.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public synchronized X509Certificate renewCertifcateAuthorityCredentials(Date expirationDate)
        throws CertificateAuthorityFault, NoCACredentialsFault {
        init();
        try {
            X509Certificate oldcert = getCACertificate(false);
            int size = ((RSAPublicKey) oldcert.getPublicKey()).getModulus().bitLength();
            KeyPair pair = KeyUtil.generateRSAKeyPair(getCACredentialsProvider(), size);
            X509Certificate cacert = CertUtil.generateCACertificate(getCACredentialsProvider(), new X509Name(oldcert
                .getSubjectDN().getName()), new Date(), expirationDate, pair, getSignatureAlgorithm());
            deleteCACredentials();
            this.setCACredentials(cacert, pair.getPrivate(), properties.getCertificateAuthorityPassword());
            return cacert;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could renew the CA credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public X509CRL getCRL(CRLEntry[] entries) throws CertificateAuthorityFault, NoCACredentialsFault {
        try {
            init();
            return CertUtil.createCRL(getCACredentialsProvider(), getCACertificate(), getPrivateKey(), entries,
                getCACertificate().getNotAfter(), getSignatureAlgorithm());
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not create the CRL.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public PrivateKey getPrivateKey() throws CertificateAuthorityFault, NoCACredentialsFault {
        init();
        try {
            if (!hasCACredentials()) {
                NoCACredentialsFault fault = new NoCACredentialsFault();
                fault.setFaultString("No Private Key exists for the CA.");
                throw fault;
            } else {
                return getPrivateKey(properties.getCertificateAuthorityPassword());
            }
        } catch (NoCACredentialsFault f) {
            throw f;
        } catch (Exception e) {
            logError(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, Error obtaining the CA private key.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public CertificateAuthorityProperties getProperties() {
        return properties;
    }
}
