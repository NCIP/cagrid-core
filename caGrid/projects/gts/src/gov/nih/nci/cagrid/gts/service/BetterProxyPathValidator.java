package gov.nih.nci.cagrid.gts.service;

import java.io.ByteArrayInputStream;
import java.security.cert.CRL;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.gaards.pki.CertUtil;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.proxy.ProxyPathValidatorException;
import org.globus.gsi.ptls.PureTLSUtil;

import COM.claymoresystems.ptls.SSLDebug;
import COM.claymoresystems.sslg.CertVerifyPolicyInt;
import cryptix.util.core.ArrayUtil;

public class BetterProxyPathValidator {

    private static Log LOG = LogFactory.getLog(BetterProxyPathValidator.class);
    
    public void validate(CertPath proxyCertPath, X509Certificate[] trustedCerts, CRL revocationList) 
        throws ProxyPathValidatorException, CertificateEncodingException {
        // do some basic sanity checks
        if (proxyCertPath == null) {
            throw new IllegalArgumentException("Proxy Certificate Path was null");
        }
        
        // TODO: make sure this plays nice with SHA256 certs!
        TrustedCertificates trustedCertificates = null;
        if (trustedCerts != null) {
            trustedCertificates = new TrustedCertificates(trustedCerts);
        }
        
        // can use the verification policy from PureTLS so we don't need new config magic
        CertVerifyPolicyInt policy = PureTLSUtil.getDefaultCertVerifyPolicy();
        
        List<X509Certificate> validatedChain = validatePath(proxyCertPath, trustedCertificates, revocationList, policy);
        
        if (validatedChain == null || validatedChain.size() < proxyCertPath.getCertificates().size()) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.UNKNOWN_CA, null, "Unknown CA");
        }
        
        /**
         * The chain returned by PureTSL code contains the CA certificates we
         * need to insert those certificates into the new certPath if the sizes
         * are different
         */
        // if the size of the validation chain != size of the cert path, we 
        // need to insert certificates.  We'll do this by copying the
        // validation chain's certs over and over again until we reach
        // the required size
        if (proxyCertPath.getCertificates().size() != validatedChain.size()) {
            int initialValidatedSize = validatedChain.size();
            int copyFromLocation = 0;
            while (validatedChain.size() < proxyCertPath.getCertificates().size()) {
                X509Certificate copyMe = validatedChain.get(copyFromLocation);
                byte[] encodedCert = copyMe.getEncoded();
                X509Certificate copy = null;
                try {
                    copy = CertUtil.loadCertificate(new ByteArrayInputStream(encodedCert));
                } catch (Exception ex) {
                    throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, ex);
                }
                validatedChain.add(copy);
                copyFromLocation++;
                copyFromLocation %= initialValidatedSize;
            }
        }
       
        // validate(certPath, trustedCertificates, crls);
    }
    
    
    private List<X509Certificate> validatePath(CertPath certPath, TrustedCertificates trustedCerts, CRL revocationList, CertVerifyPolicyInt policy) 
        throws ProxyPathValidatorException, CertificateEncodingException {
        List<X509Certificate> chain = new ArrayList<X509Certificate>();
        boolean foundRoot = false;
        X509Certificate last = null;
        int pathLength = 0;
        
        // iterate the proxy cert path
        for (Certificate c : certPath.getCertificates()) {
            if (!(c instanceof X509Certificate)) {
                throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, "Certificate on path was not an X509Certificate!", null);
            }
            X509Certificate cert = (X509Certificate) c;
            
            // some basic debugging
            SSLDebug.debug(SSLDebug.DEBUG_CERT, "Trying to validate", cert.getEncoded());
            
            if (!foundRoot) {
                // no root found yet, let's see if this cert is a root
                if (isRoot(cert, trustedCerts)) {
                    LOG.debug("Cert is root");
                    last = cert;
                    foundRoot = true;
                    // TODO: I hate continues, how can I clean this up?
                    continue;
                } else {
                    // not a root, but maybe it's signed by one?
                    LOG.debug("Trying to find the trusted root of this cert");
                    last = getSigningRoot(cert, trustedCerts);
                    if (last == null) {
                        LOG.debug("Did not find a trusted root for this cert");
                        continue;
                    }
                    LOG.debug("The trusted root was found");
                }
                // to get here, we must have found a trusted root cert ('last') that
                // signed our user cert.  Add it to the chain, mark found_root == true
                chain.add(last);
                foundRoot = true;
            }
            
            // to get here, we must have found a root in the chain, or the user
            // cert was signed by a trusted root and we're using that. 
            // Either way, that cert is called 'last' and has been added to the chain

            // Now check that the signer's subjectName is the same
            // as the issuerName
            if (!subjectMatchesIssuer(cert, last)) {
                String certSubject = cert.getSubjectX500Principal().toString();
                throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE,
                    "Invalid certificate chain at '" + certSubject 
                    + "' certificate. Subject and issuer names do not match", null);
            }
            
            // Ok, now we have to verify this certificate
            // TODO: implement me
            
            // if we're checking dates, do it now
            if (policy.checkDatesP()) {
                try {
                    // checking vs now
                    checkValidDates(cert, new Date());
                } catch (CertificateNotYetValidException ex) {
                    throw new ProxyPathValidatorException(
                        ProxyPathValidatorException.FAILURE, "The certificate is not yet valid", ex);
                } catch (CertificateExpiredException ex) {
                    throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE,
                        "The certificate is expired", ex);
                }
            }
            
            pathLength++;
            if (pathLength > 255) {
                throw new ProxyPathValidatorException(
                    ProxyPathValidatorException.PATH_LENGTH_EXCEEDED, "Path length greater than 255", null);
            }
            
            last = cert;
        }
        
        if (last != null) {
            return chain;
        }
        
        return null;
    }
    
    
    private boolean isRoot(X509Certificate cert, TrustedCertificates trustedRoots) throws CertificateEncodingException {
        byte[] certEnc = cert.getEncoded();
        for (X509Certificate root : trustedRoots.getCertificates()) {
            byte[] rootEnc = root.getEncoded();
            boolean equal = ArrayUtil.areEqual(certEnc, rootEnc);
            if (equal) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Returns the Trusted Signing Root for the certificate if the
     * certificate's issuer == a trusted root's subject, otherwise
     * returns null.
     *  
     * @param cert
     * @param trustedRoots
     * @return
     */
    private X509Certificate getSigningRoot(X509Certificate cert, TrustedCertificates trustedRoots) {
        X509Certificate signedByRoot = null;
        // TODO: verify this is what I really want
        byte[] certIssuer = cert.getIssuerX500Principal().getEncoded();
        for (X509Certificate root : trustedRoots.getCertificates()) {
            // TODO: also that this is what I really want
            byte[] rootSig = root.getSubjectX500Principal().getEncoded();
            if (ArrayUtil.areEqual(certIssuer, rootSig)) {
                signedByRoot = root;
                break;
            }
        }
        return signedByRoot;
    }
    
    
    private boolean subjectMatchesIssuer(X509Certificate cert, X509Certificate trusted) {
        byte[] trustedSubject = trusted.getSubjectX500Principal().getEncoded();
        byte[] testIssuer = cert.getIssuerX500Principal().getEncoded();
        return ArrayUtil.areEqual(trustedSubject, testIssuer);
    }
    
    
    private void checkValidDates(X509Certificate cert, Date date) 
        throws CertificateNotYetValidException, CertificateExpiredException {
        cert.checkValidity(date);
    }
}
