package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.FaultHelper;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import javax.naming.ldap.LdapName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.X509Name;
import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.service.util.Utils;
import org.cagrid.gaards.pki.CRLEntry;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;


/**
 * CertificateAuthority
 * The certificate authority base class.  Creates and signs
 * certificates and CRLs
 * 
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:David.Ervin@osumc.edu">David Ervin</A>
 */
public abstract class CertificateAuthority {
    
    private static Log LOG = LogFactory.getLog(CertificateAuthority.class);

    private boolean initialized = false;
    private CertificateAuthorityProperties properties = null;


    public CertificateAuthority(CertificateAuthorityProperties properties) {
        this.properties = properties;
    }
    
    
    private synchronized void init() throws CertificateAuthorityFault {
        try {
            if (!initialized) {
                // if we don't already have CA credentials and the config says we should 
                // auto-create them, do it now
                if (!hasCACredentials() && properties.isAutoCreateCAEnabled()) {
                    Lifetime lifetime = properties.getCreationPolicy().getLifetime();
                    this.createCertifcateAuthorityCredentials(properties.getCreationPolicy().getSubject(), Utils
                        .getExpiredDate(lifetime), properties.getCreationPolicy().getKeySize());
                }
                initialized = true;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not initialize the Dorian Certificate Authority.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }
    

    public abstract String getUserCredentialsProvider();


    public abstract String getCACredentialsProvider();


    public abstract boolean hasCACredentials() throws CertificateAuthorityFault;
    
    
    public abstract void deleteCACredentials() throws CertificateAuthorityFault;


    public abstract void setCACredentials(X509Certificate cert, PrivateKey key, String password)
        throws CertificateAuthorityFault;
    
    
    public synchronized PrivateKey getPrivateKey(String password) throws CertificateAuthorityFault, NoCACredentialsFault {
        init();
        return internalGetPrivateKey(password);
    }
    
    
    protected abstract PrivateKey internalGetPrivateKey(String password) throws CertificateAuthorityFault, NoCACredentialsFault;


    protected X509Certificate getCertificate() throws CertificateAuthorityFault {
        init();
        return internalGetCertificate();
    }
    
    
    protected abstract X509Certificate internalGetCertificate() throws CertificateAuthorityFault; 
    
    
    
    public String getSignatureAlgorithm() {
        return properties.getSignatureAlgorithm();
    }


    public void clearCertificateAuthority() throws CertificateAuthorityFault {
        deleteCACredentials();
        this.initialized = false;
    }


    private void createCertifcateAuthorityCredentials(String dn, Date expirationDate, int keySize)
        throws CertificateAuthorityFault {
        try {
            KeyPair pair = KeyUtil.generateRSAKeyPair(getCACredentialsProvider(), keySize);
            X509Certificate cacert = CertUtil.generateCACertificate(getCACredentialsProvider(), new X509Name(dn),
                new Date(), expirationDate, pair, getSignatureAlgorithm());
            deleteCACredentials();
            this.setCACredentials(cacert, pair.getPrivate(), properties.getCertificateAuthorityPassword());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
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
        init();
        X509Certificate cert = null;
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
            LOG.error(e.getMessage(), e);
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
                LOG.debug("The CA credentials were not yet valid or expired");
                // our current cert is either expired, or not yet valid
                // are we supposed to auto-renew it?
                if (properties.isAutoRenewCAEnabled()) {
                    LOG.debug("Automatically renewing the CA cert");
                    Lifetime lifetime = properties.getRenewalLifetime();
                    cert = renewCertifcateAuthorityCredentials(Utils.getExpiredDate(lifetime));
                } else {
                    // no auto-renewal, throw an exception
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
            LdapName caSubject = new LdapName(cacert.getSubjectX500Principal().getName());
            caSubject.remove(caSubject.size() - 1);
            LdapName ldapSubject = new LdapName(subject);

            if (!ldapSubject.startsWith(caSubject)) {
                CertificateAuthorityFault fault = new CertificateAuthorityFault();
                fault.setFaultString("Invalid certificate subject: " + ldapSubject.toString() + ", the subject must start with, " + caSubject.toString());
                throw fault;
            }
            X509Certificate cert = CertUtil.generateCertificate(getCACredentialsProvider(), new X509Name(subject),
                start, expiration, publicKey, cacert, getPrivateKey(), getSignatureAlgorithm(), properties
                    .getPolicyOID());
            return cert;
        } catch (CertificateAuthorityFault e) {
            throw e;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
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
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could not sign host certificate.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public synchronized X509Certificate renewCertifcateAuthorityCredentials(Date expirationDate)
        throws CertificateAuthorityFault {
        init();
        try {
            X509Certificate oldcert = getCACertificate(false);
            int size = ((RSAPublicKey) oldcert.getPublicKey()).getModulus().bitLength();
            KeyPair pair = KeyUtil.generateRSAKeyPair(getCACredentialsProvider(), size);
            X509Certificate cacert = CertUtil.generateCACertificate(getCACredentialsProvider(), 
                new X509Name(oldcert.getSubjectX500Principal().getName()), new Date(), expirationDate, pair, getSignatureAlgorithm());
            deleteCACredentials();
            this.setCACredentials(cacert, pair.getPrivate(), properties.getCertificateAuthorityPassword());
            return cacert;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            CertificateAuthorityFault fault = new CertificateAuthorityFault();
            fault.setFaultString("Unexpected Error, could renew the CA credentials.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (CertificateAuthorityFault) helper.getFault();
            throw fault;
        }
    }


    public X509CRL getCRL(CRLEntry[] entries) throws CertificateAuthorityFault {
        init();
        try {
            return CertUtil.createCRL(getCACredentialsProvider(), getCACertificate(), getPrivateKey(), entries,
                getCACertificate().getNotAfter(), getSignatureAlgorithm());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
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
            LOG.error(e.getMessage(), e);
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
