package gov.nih.nci.cagrid.gts.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.bc.BouncyCastleUtil;
import org.globus.gsi.proxy.ProxyPathValidatorException;
import org.globus.gsi.proxy.ProxyPolicyHandler;
import org.globus.gsi.proxy.ext.ProxyCertInfo;
import org.globus.gsi.proxy.ext.ProxyPolicy;
import org.globus.gsi.ptls.PureTLSUtil;

import COM.claymoresystems.ptls.SSLDebug;
import COM.claymoresystems.sslg.CertVerifyPolicyInt;

public class BetterProxyPathValidator {

    private static final int MAX_PATH_LENGTH = 255;

    // public static final String CRYPTO_PROVIDER = "SunRsaSign";
    // use the BouncyCastle crypto provider
    public static final String CRYPTO_PROVIDER = "BC";

    private static Log LOG = LogFactory.getLog(BetterProxyPathValidator.class);

    private static Map<String, String> OID_TO_NAME;

    static {
        OID_TO_NAME = new HashMap<String, String>();

        OID_TO_NAME.put("1.2.840.10040.4.3", "DSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.2", "MD2/RSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.3", "MD4/RSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.4", "MD5/RSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.5", "SHA-1/RSA/PKCS#1");
        // added OIDs for a variety of algorithms
        // OIDs from http://www.oid-info.com/index.htm
        OID_TO_NAME.put("1.2.840.113549.1.1.11", "SHA256withRSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.12", "SHA384withRSA");
        OID_TO_NAME.put("1.2.840.113549.1.1.13", "SHA512withRSA");
    }

    private static Set<String> RSA_ALGORITHMS;
    static {
        RSA_ALGORITHMS = new HashSet<String>();
        RSA_ALGORITHMS.add("MD2/RSA");
        RSA_ALGORITHMS.add("MD2/RSA");
        RSA_ALGORITHMS.add("MD5/RSA");
        RSA_ALGORITHMS.add("SHA-1/RSA/PKCS#1");
        RSA_ALGORITHMS.add("SHA256withRSA");
        RSA_ALGORITHMS.add("SHA384withRSA");
        RSA_ALGORITHMS.add("SHA512withRSA");
    }
    
    private Map<String, ProxyPolicyHandler> proxyPolicyHandlers = null;
    private CertificateFactory certFactory = null;
    private X509Certificate identityCert = null;
    private boolean limited = false;
    private boolean rejectLimitedProxyCheck = false;
    
    public BetterProxyPathValidator() throws CertificateException {
        this.proxyPolicyHandlers = new HashMap<String, ProxyPolicyHandler>();
        certFactory = CertificateFactory.getInstance("X.509");
    }
    
    
    /**
     * Returns if the validated proxy path is limited. A proxy path is limited
     * when a limited proxy is present anywhere after the first
     * non-impersonation proxy certificate.
     * 
     * @return true if the validated path is limited
     */
    public boolean isLimited() {
        return this.limited;
    }
    
    
    /**
     * Resets the internal state. Useful for reusing the same instance for
     * validating multiple certificate paths.
     */
    public void reset() {
        this.rejectLimitedProxyCheck = false;
        this.limited = false;
        this.identityCert = null;
    }


    /**
     * If set, the validate rejects certificate chain if limited proxy if found
     */
    public void setRejectLimitedProxyCheck(boolean rejectLimProxy) {
        this.rejectLimitedProxyCheck = rejectLimProxy;
    }
    
    
    /**
     * Retrieves a restricted proxy policy handler for a given policy id.
     * 
     * @param id
     *            the Oid of the proxy policy to get the handler for.
     * @return <code>ProxyPolicyHandler</code> the policy handler registered
     *         for the given id or null if none is registered.
     */
    public ProxyPolicyHandler getProxyPolicyHandler(String id) {
        return (id != null && this.proxyPolicyHandlers != null)
            ? (ProxyPolicyHandler) this.proxyPolicyHandlers.get(id)
            : null;
    }
    
    
    /**
     * Removes a restricted proxy policy handler.
     * 
     * @param id
     *            the Oid of the policy handler to remove.
     * @return <code>ProxyPolicyHandler</code> the removed handler, or null if
     *         there is no handler registered under that id.
     */
    public ProxyPolicyHandler removeProxyPolicyHandler(String id) {
        return (id != null && this.proxyPolicyHandlers != null) ? (ProxyPolicyHandler) this.proxyPolicyHandlers
            .remove(id) : null;
    }


    /**
     * Sets a restricted proxy policy handler.
     * 
     * @param id
     *            the Oid of the proxy policy to install the handler for.
     * @param handler
     *            the proxy policy handler.
     * @return <code>ProxyPolicyHandler</code> the previous handler installed
     *         under the specified id. Usually, will be null.
     */
    public ProxyPolicyHandler setProxyPolicyHandler(String id, ProxyPolicyHandler handler) {
        if (id == null) {
            throw new IllegalArgumentException("id == null");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        return this.proxyPolicyHandlers.put(id, handler);
    }


    public void validate(CertPath certificatePath, X509Certificate[] trustedCerts, X509CRL revocationList)
        throws ProxyPathValidatorException, CertificateEncodingException {
        CertificateRevocationLists crls = CertificateRevocationLists
            .getCertificateRevocationLists(new X509CRL[]{revocationList});
        validate(certificatePath, trustedCerts, crls);
    }
    
    
    public void validate(X509Certificate[] certPath, X509Certificate[] trustedCerts, CertificateRevocationLists revocationLists) 
        throws ProxyPathValidatorException, CertificateEncodingException {
        CertPath path = null;
        try {
            path = certFactory.generateCertPath(Arrays.asList(certPath));
        } catch (CertificateException e) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, 
                "Error creating CertPath instance: " + e.getMessage(), e);
        }
        validate(path, trustedCerts, revocationLists);
    }


    public void validate(CertPath proxyCertPath, X509Certificate[] trustedCerts,
        CertificateRevocationLists revocationLists) throws ProxyPathValidatorException, CertificateEncodingException {
        // do some basic sanity checks
        if (proxyCertPath == null) {
            throw new IllegalArgumentException("Proxy Certificate Path was null");
        }

        // this seems to play well with certs regardless of algorithm
        TrustedCertificates trustedCertificates = null;
        if (trustedCerts != null) {
            trustedCertificates = new TrustedCertificates(trustedCerts);
        }

        // can use the verification policy from PureTLS so we don't need new config magic
        CertVerifyPolicyInt policy = PureTLSUtil.getDefaultCertVerifyPolicy();

        // create the validated certificate chain
        List<X509Certificate> validatedChain = createValidatedChain(proxyCertPath, trustedCertificates, policy);

        // no validated chain, or it's not of the right length
        if (validatedChain == null || validatedChain.size() < proxyCertPath.getCertificates().size()) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.UNKNOWN_CA, null, "Unknown CA");
        }

        // if the size of the validation chain != size of the cert path, we
        // need to insert certificates into the cert path for more validation.
        if (proxyCertPath.getCertificates().size() != validatedChain.size()) {
            // since the CertPath certificates list is immutable, copy it
            List<Certificate> newCertPath = new ArrayList<Certificate>();
            newCertPath.addAll(proxyCertPath.getCertificates());
            for (int i = 0; i < validatedChain.size() - proxyCertPath.getCertificates().size(); i++) {
                // copy from the beginning of the validated chain to the new path
                newCertPath.add(validatedChain.get(i));
            }
            // make the new cert path
            try {
                proxyCertPath = certFactory.generateCertPath(newCertPath);
            } catch (CertificateException ex) {
                throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, ex.getMessage(), ex);
            }
        }

        deepValidate(proxyCertPath, trustedCertificates, revocationLists);
    }


    private List<X509Certificate> createValidatedChain(CertPath certPath, TrustedCertificates trustedCerts,
        CertVerifyPolicyInt policy) throws ProxyPathValidatorException, CertificateEncodingException {
        List<X509Certificate> chain = new ArrayList<X509Certificate>();
        boolean foundRoot = false;
        X509Certificate last = null;
        int pathLength = 0;

        // iterate the proxy cert path in reverse, since CertPath puts the most
        // specific cert (i.e. the proxy) up front, and this algorithm
        // wants the most significant (i.e. the CA cert) up front
        List<Certificate> certs = new ArrayList<Certificate>();
        certs.addAll(certPath.getCertificates());
        Collections.reverse(certs);
        for (Certificate c : certs) {
            if (!(c instanceof X509Certificate)) {
                throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE,
                    "Certificate on path was not an X509Certificate!", null);
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
                    chain.add(last);
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
                // to get here, we must have found a trusted root cert ('last') 
                // that signed our user cert. Add it to the chain, mark 
                // found_root == true
                chain.add(last);
                foundRoot = true;
            }

            // to get here, we must have found a root in the chain, or the user
            // cert was signed by a trusted root and we're using that.
            // Either way, that cert is called 'last' and has been added to the
            // chain

            // Now check that the signer's subjectName is the same
            // as the issuerName
            if (!subjectMatchesIssuer(cert, last)) {
                String certSubject = cert.getSubjectX500Principal().toString();
                throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE,
                    "Invalid certificate chain at '" + certSubject
                        + "' certificate. Subject and issuer names do not match", null);
            }

            // now we have to verify this certificate was signed
            // by the issuer we think it was signed by
            verifyCert(cert, last.getPublicKey());

            // if we're checking dates, do it now
            if (policy.checkDatesP()) {
                try {
                    // checking vs now
                    checkValidDates(cert, new Date());
                } catch (CertificateNotYetValidException ex) {
                    throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE,
                        "The certificate is not yet valid", ex);
                } catch (CertificateExpiredException ex) {
                    throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE,
                        "The certificate is expired", ex);
                }
            }

            // increment the path length
            pathLength++;
            // path length capped at 255
            if (pathLength > MAX_PATH_LENGTH) {
                throw new ProxyPathValidatorException(ProxyPathValidatorException.PATH_LENGTH_EXCEEDED,
                    "Path length greater than " + MAX_PATH_LENGTH, null);
            }

            last = cert;
            
            chain.add(cert);
        }

        if (last != null) {
            return chain;
        }

        return null;
    }


    private void deepValidate(CertPath certPath, TrustedCertificates trustedCerts, CertificateRevocationLists crlLists)
        throws ProxyPathValidatorException {
        if (certPath == null) {
            throw new IllegalArgumentException("certs == null");
        }

        if (trustedCerts == null) {
            LOG.debug("No trusted certs specified, using defaults");
            trustedCerts = TrustedCertificates.getDefaultTrustedCertificates();
        }

        if (crlLists == null) {
            LOG.debug("No crl lists specified, using defaults");
            crlLists = CertificateRevocationLists.getDefaultCertificateRevocationLists();
        }

        X509Certificate cert;
        TBSCertificateStructure tbsCert;
        int certType;

        int proxyDepth = 0;

        try {
            cert = (X509Certificate) certPath.getCertificates().get(0);
            tbsCert = BouncyCastleUtil.getTBSCertificateStructure(cert);
            certType = BouncyCastleUtil.getCertificateType(tbsCert, trustedCerts);

            LOG.debug("Found cert of type " + certType);
            if (LOG.isTraceEnabled()) {
                LOG.trace(cert);
            }

            // make sure the cert is within the valid time
            checkValidDates(cert, new Date());

            // check for unsupported critical extensions
            checkUnsupportedCriticalExtensions(tbsCert, certType, cert);
            checkIdentity(cert, certType);
            checkCRL(cert, crlLists, trustedCerts);
            if (CertUtil.isProxy(certType)) {
                proxyDepth++;
            }

            for (int i = 1; i < certPath.getCertificates().size(); i++) {
                X509Certificate issuerCert = (X509Certificate) certPath.getCertificates().get(i);
                TBSCertificateStructure issuerTbsCert = BouncyCastleUtil.getTBSCertificateStructure(issuerCert);
                int issuerCertType = BouncyCastleUtil.getCertificateType(issuerTbsCert, trustedCerts);

                LOG.debug("Found issuer cert of type " + issuerCertType);
                if (LOG.isTraceEnabled()) {
                    LOG.trace(issuerCert);
                }

                if (issuerCertType == GSIConstants.CA) {
                    // PC can only be signed by EEC or PC
                    if (CertUtil.isProxy(certType)) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, issuerCert,
                            "CA certificate cannot sign Proxy Certificate");
                    }
                    int pathLen = getCAPathConstraint(issuerTbsCert);
                    if (pathLen >= 0 && pathLen < Integer.MAX_VALUE && (i - proxyDepth - 1) > pathLen) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.PATH_LENGTH_EXCEEDED,
                            issuerCert, "CA Certificate does not allow path length > " + pathLen
                                + " and path length is " + (i - proxyDepth - 1));
                    }
                } else if (CertUtil.isGsi3Proxy(issuerCertType)
                    || CertUtil.isGsi4Proxy(issuerCertType)) {
                    // PC can sign EEC or another PC only.
                    String errMsg = "Proxy Certificate can only sign another proxy of the same type";
                    if (CertUtil.isGsi3Proxy(issuerCertType)) {
                        if (!CertUtil.isGsi3Proxy(certType)) {
                            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, issuerCert,
                                errMsg);
                        }
                    } else if (CertUtil.isGsi4Proxy(issuerCertType)) {
                        if (!CertUtil.isGsi4Proxy(certType)) {
                            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, issuerCert,
                                errMsg);
                        }
                    }
                    int pathLen = getProxyPathConstraint(issuerTbsCert);
                    if (pathLen == 0) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, issuerCert,
                            "Proxy Certificate cannot be used to sign another Proxy Certificate.");
                    }
                    if (pathLen < Integer.MAX_VALUE && proxyDepth > pathLen) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.PATH_LENGTH_EXCEEDED,
                            issuerCert, "Proxy Certificate does not allow path length > " + pathLen
                                + " and path length is " + proxyDepth);
                    }
                    proxyDepth++;
                } else if (CertUtil.isGsi2Proxy(issuerCertType)) {
                    // PC can sign EEC or another PC only
                    if (!CertUtil.isGsi2Proxy(certType)) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, issuerCert,
                            "Proxy Certificate can only sign another proxy of the same type");
                    }
                    proxyDepth++;
                } else if (issuerCertType == GSIConstants.EEC) {
                    if (!CertUtil.isProxy(certType)) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, issuerCert,
                            "End Entity Certificate can only sign Proxy Certificates");
                    }
                } else {
                    // that should never happen
                    throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, issuerCert,
                        "Unknown cert type: " + issuerCertType);
                }

                if (CertUtil.isProxy(certType)) {
                    // check all the proxy & issuer constraints
                    if (CertUtil.isGsi3Proxy(certType) || CertUtil.isGsi4Proxy(certType)) {
                        checkProxyConstraints(tbsCert, issuerTbsCert, cert);
                        if ((certType == GSIConstants.GSI_3_RESTRICTED_PROXY)
                            || (certType == GSIConstants.GSI_4_RESTRICTED_PROXY)) {
                            checkRestrictedProxy(tbsCert, certPath, i - 1);
                        }
                    }
                } else {
                    checkKeyUsage(issuerTbsCert, certPath, i);
                }

                checkValidDates(issuerCert, new Date());
                checkUnsupportedCriticalExtensions(issuerTbsCert, issuerCertType, issuerCert);
                checkIdentity(issuerCert, issuerCertType);
                checkCRL(cert, crlLists, trustedCerts);
                cert = issuerCert;
                certType = issuerCertType;
                tbsCert = issuerTbsCert;
            }
        } catch (IOException e) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, e);
        } catch (CertificateEncodingException e) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, e);
        } catch (ProxyPathValidatorException e) {
            // prevents wrapping and re-throwing one of these
            throw e;
        } catch (Exception e) {
            // whatever happened, it was bad and fails the validation
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, e);
        }
    }


    /**
     * Walks through the trusted roots looking for one which
     * matches the cert
     * 
     * @param cert
     *      The cert to test
     * @param trustedRoots
     *      The trusted root certs
     * @return
     *      True if the cert is a trusted root cert
     * @throws CertificateEncodingException
     */
    private boolean isRoot(X509Certificate cert, TrustedCertificates trustedRoots) 
        throws CertificateEncodingException {
        byte[] certEnc = cert.getEncoded();
        for (X509Certificate root : trustedRoots.getCertificates()) {
            byte[] rootEnc = root.getEncoded();
            boolean equal = Arrays.equals(certEnc, rootEnc);
            if (equal) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Gets the signing root cert of the tested cert
     * 
     * @param cert
     *      The cert to test and find a signing root for
     * @param trustedRoots
     *      The trusted root certs
     * @return
     *      The trusted root cert which signed the cert in question, or null if none is found
     * @throws CertificateEncodingException
     * @throws ProxyPathValidatorException
     */
    private X509Certificate getSigningRoot(X509Certificate cert, TrustedCertificates trustedRoots) 
        throws CertificateEncodingException, ProxyPathValidatorException {
        X509Certificate signedBy = null;
        for (X509Certificate root : trustedRoots.getCertificates()) {
            // see if the cert was signed by this root
            if (verifyCert(cert, root.getPublicKey())) {
                signedBy = root;
                break;
            }
        }
        return signedBy;
    }


    private boolean subjectMatchesIssuer(X509Certificate cert, X509Certificate trusted) {
        byte[] trustedSubject = trusted.getSubjectX500Principal().getEncoded();
        byte[] testIssuer = cert.getIssuerX500Principal().getEncoded();
        return Arrays.equals(trustedSubject, testIssuer);
    }


    private void checkValidDates(X509Certificate cert, Date date) throws CertificateNotYetValidException,
        CertificateExpiredException {
        cert.checkValidity(date);
    }


    /**
     * Ensures the certificate is signed by the issuer
     * 
     * @param cert
     * @param signerKey
     */
    private boolean verifyCert(X509Certificate cert, PublicKey signerKey) throws ProxyPathValidatorException {
        // Lookup the algorithm
        String certAlgorithmOid = cert.getSigAlgOID();
        String algorithmName = OID_TO_NAME.get(certAlgorithmOid);
        if (algorithmName == null) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE,
                "Unknown certificate signature algorithm OID: " + certAlgorithmOid, null);
        }
        LOG.debug("Certificate has signing algorithm OID " + certAlgorithmOid + ", which maps to " + algorithmName);
        try {
            checkSignatureKey(signerKey, algorithmName);
        } catch (CertificateVerifyException ex) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, ex.getMessage(), ex);
        }

        // Security.addProvider(new Cryptix());
        Signature sig;
        try {
            sig = Signature.getInstance(algorithmName, CRYPTO_PROVIDER);
        } catch (NoSuchAlgorithmException e) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, e.getMessage(), e);
        }

        boolean verified = false;
        try {
            sig.initVerify(signerKey);
            byte[] tbsCertDER = cert.getTBSCertificate();
            sig.update(tbsCertDER);
            verified = sig.verify(cert.getSignature());
        } catch (Exception ex) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, "Unable to verify signature: "
                + ex.getMessage(), ex);
        }
        return verified;
    }


    /**
     * Verifies the public key and associated algorithm are something we
     * can reasonably expect to handle, specifically any of the RSA key
     * algorithms, or DSA
     * 
     * @param key
     * @param alg
     * @throws CertificateVerifyException
     */
    private void checkSignatureKey(PublicKey key, String alg) throws CertificateVerifyException {
        if (RSA_ALGORITHMS.contains(alg)) {
            if (!(key instanceof RSAPublicKey)) {
                throw new CertificateVerifyException("Public key doesn't match algorithm " + alg);
            }
        } else if (alg.equals("DSA")) {
            if (!(key instanceof java.security.interfaces.DSAPublicKey)) {
                throw new CertificateVerifyException("Public key doesn't match algorithm " + alg);
            }
        } else {
            throw new CertificateVerifyException("Unknown algorithm " + alg);
        }
    }


    protected void checkUnsupportedCriticalExtensions(TBSCertificateStructure crt, int certType,
        X509Certificate checkedProxy) throws ProxyPathValidatorException {
        X509Extensions extensions = crt.getExtensions();
        if (extensions != null) {
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                DERObjectIdentifier oid = (DERObjectIdentifier) e.nextElement();
                X509Extension ext = extensions.getExtension(oid);
                if (ext.isCritical()) {
                    if (oid.equals(X509Extensions.BasicConstraints) || oid.equals(X509Extensions.KeyUsage)
                        || (oid.equals(ProxyCertInfo.OID) && CertUtil.isGsi4Proxy(certType))
                        || (oid.equals(ProxyCertInfo.OLD_OID) && CertUtil.isGsi3Proxy(certType))) {
                    } else {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.UNSUPPORTED_EXTENSION,
                            checkedProxy, "Unsuppored critical exception : " + oid.getId());
                    }
                }
            }
        }
    }


    protected void checkIdentity(X509Certificate cert, int certType) throws ProxyPathValidatorException {
        if (this.identityCert == null) {
            // check if limited
            if (CertUtil.isLimitedProxy(certType)) {
                this.limited = true;

                if (this.rejectLimitedProxyCheck) {
                    throw new ProxyPathValidatorException(ProxyPathValidatorException.LIMITED_PROXY_ERROR, cert,
                        "Limited Proxies not accepted");
                }
            }

            // set the identity cert
            if (!CertUtil.isImpersonationProxy(certType)) {
                this.identityCert = cert;
            }
        }
    }


    protected void checkCRL(X509Certificate cert, CertificateRevocationLists crlsList, TrustedCertificates trustedCerts)
        throws ProxyPathValidatorException {
        if (crlsList == null) {
            return;
        }

        // does the issuer identity publish a CRL?
        String issuerName = cert.getIssuerDN().getName();
        X509CRL crl = crlsList.getCrl(issuerName);
        if (crl == null) {
            LOG.debug("No CRL for certificate");
            return;
        }

        // get CA cert for the CRL
        X509Certificate x509Cert = trustedCerts.getCertificate(issuerName);
        if (x509Cert == null) {
            // if there is no trusted certs from that CA, then
            // the chain cannot contain a cert from that CA,
            // which implies not checking this CRL should be fine.
            LOG.debug("No trusted cert with this CA signature");
            return;
        }

        // validate CRL
        try {
            crl.verify(x509Cert.getPublicKey());
        } catch (Exception exp) {
            LOG.debug("CRL verification failed: " + exp.getMessage());
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, exp);
        }

        Date now = new Date();
        // check date validity of CRL
        if ((crl.getThisUpdate().before(now)) || ((crl.getNextUpdate() != null) && (crl.getNextUpdate().after(now)))) {
            if (crl.isRevoked(cert)) {
                throw new ProxyPathValidatorException(ProxyPathValidatorException.REVOKED, cert, "This cert "
                    + cert.getSubjectDN().getName() + " is on a CRL");
            }
        }
    }


    protected int getCAPathConstraint(TBSCertificateStructure crt) throws IOException {
        X509Extensions extensions = crt.getExtensions();
        if (extensions == null) {
            return -1;
        }
        X509Extension ext = extensions.getExtension(X509Extensions.BasicConstraints);
        if (ext != null) {
            BasicConstraints basicExt = BouncyCastleUtil.getBasicConstraints(ext);
            if (basicExt.isCA()) {
                BigInteger pathLen = basicExt.getPathLenConstraint();
                return (pathLen == null) ? Integer.MAX_VALUE : pathLen.intValue();
            } else {
                return -1;
            }
        }
        return -1;
    }


    protected int getProxyPathConstraint(TBSCertificateStructure crt) throws IOException {
        ProxyCertInfo proxyCertExt = getProxyCertInfo(crt);
        return (proxyCertExt != null) ? proxyCertExt.getPathLenConstraint() : -1;
    }


    protected ProxyCertInfo getProxyCertInfo(TBSCertificateStructure crt) throws IOException {
        X509Extensions extensions = crt.getExtensions();
        if (extensions == null) {
            return null;
        }
        X509Extension ext = extensions.getExtension(ProxyCertInfo.OID);
        if (ext == null) {
            ext = extensions.getExtension(ProxyCertInfo.OLD_OID);
        }
        return (ext != null) ? BouncyCastleUtil.getProxyCertInfo(ext) : null;
    }


    protected void checkProxyConstraints(TBSCertificateStructure proxy, TBSCertificateStructure issuer,
        X509Certificate checkedProxy) throws ProxyPathValidatorException, IOException {
        X509Extensions extensions;
        DERObjectIdentifier oid;
        X509Extension ext;

        X509Extension proxyKeyUsage = null;

        extensions = proxy.getExtensions();
        if (extensions != null) {
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                oid = (DERObjectIdentifier) e.nextElement();
                ext = extensions.getExtension(oid);
                if (oid.equals(X509Extensions.SubjectAlternativeName)
                    || oid.equals(X509Extensions.IssuerAlternativeName)) {
                    // No Alt name extensions - 3.2 & 3.5
                    throw new ProxyPathValidatorException(ProxyPathValidatorException.PROXY_VIOLATION, checkedProxy,
                        "Proxy certificate cannot contain subject or issuer alternative name extension");
                } else if (oid.equals(X509Extensions.BasicConstraints)) {
                    // Basic Constraint must not be true - 3.8
                    BasicConstraints basicExt = BouncyCastleUtil.getBasicConstraints(ext);
                    if (basicExt.isCA()) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.PROXY_VIOLATION,
                            checkedProxy, "Proxy certificate cannot have BasicConstraint CA=true");
                    }
                } else if (oid.equals(X509Extensions.KeyUsage)) {
                    proxyKeyUsage = ext;

                    boolean[] keyUsage = BouncyCastleUtil.getKeyUsage(ext);
                    // these must not be asserted
                    if (keyUsage[1] || keyUsage[5]) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.PROXY_VIOLATION,
                            checkedProxy,
                            "The keyCertSign and nonRepudiation bits must not be asserted in Proxy Certificate");
                    }
                    boolean[] issuerKeyUsage = getKeyUsage(issuer);
                    if (issuerKeyUsage != null) {
                        for (int i = 0; i < 9; i++) {
                            if (i == 1 || i == 5) {
                                continue;
                            }
                            if (!issuerKeyUsage[i] && keyUsage[i]) {
                                throw new ProxyPathValidatorException(ProxyPathValidatorException.PROXY_VIOLATION,
                                    checkedProxy, "Bad KeyUsage in Proxy Certificate");
                            }
                        }
                    }
                }
            }
        }

        extensions = issuer.getExtensions();

        if (extensions != null) {
            Enumeration e = extensions.oids();
            while (e.hasMoreElements()) {
                oid = (DERObjectIdentifier) e.nextElement();
                ext = extensions.getExtension(oid);
                if (oid.equals(X509Extensions.KeyUsage)) {
                    // If issuer has it then proxy must have it also
                    if (proxyKeyUsage == null) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.PROXY_VIOLATION,
                            checkedProxy, "KeyUsage extension missing in Proxy Certificate");
                    }
                    // If issuer has it as critical so does the proxy
                    if (ext.isCritical() && !proxyKeyUsage.isCritical()) {
                        throw new ProxyPathValidatorException(ProxyPathValidatorException.PROXY_VIOLATION,
                            checkedProxy, "KeyUsage extension in Proxy Certificate is not critical");
                    }
                }
            }
        }
    }


    protected boolean[] getKeyUsage(TBSCertificateStructure crt) throws IOException {
        X509Extensions extensions = crt.getExtensions();
        if (extensions == null) {
            return null;
        }
        X509Extension ext = extensions.getExtension(X509Extensions.KeyUsage);
        return (ext != null) ? BouncyCastleUtil.getKeyUsage(ext) : null;
    }


    protected void checkRestrictedProxy(TBSCertificateStructure proxy, CertPath certPath, int index)
        throws ProxyPathValidatorException, IOException {
        ProxyCertInfo info = getProxyCertInfo(proxy);

        // just a sanity check
        if (info == null) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, 
                (X509Certificate) certPath.getCertificates().get(index),
                "Could not retreive ProxyCertInfo extension");
        }

        ProxyPolicy policy = info.getProxyPolicy();

        // another sanity check
        if (policy == null) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, 
                (X509Certificate) certPath.getCertificates().get(index),
                "Could not retreive ProxyPolicy from ProxyCertInfo extension");
        }

        String pl = policy.getPolicyLanguage().getId();

        ProxyPolicyHandler handler = getProxyPolicyHandler(pl);

        if (handler == null) {
            throw new ProxyPathValidatorException(ProxyPathValidatorException.UNKNOWN_POLICY,
                (X509Certificate) certPath.getCertificates().get(index),
                "Unknown policy: " + pl);
        }
        
        X509Certificate[] certPathArray = new X509Certificate[certPath.getCertificates().size()];
        for (int i = 0; i < certPath.getCertificates().size(); i++) {
            certPathArray[i] = (X509Certificate) certPath.getCertificates().get(i);
        }

        handler.validate(info, certPathArray, index);
    }
    
    
    protected void checkKeyUsage(TBSCertificateStructure issuer, CertPath certPath, int index)
        throws ProxyPathValidatorException, IOException {
        boolean[] issuerKeyUsage = getKeyUsage(issuer);
        if (issuerKeyUsage != null) {
            if (!issuerKeyUsage[5]) {
                throw new ProxyPathValidatorException(ProxyPathValidatorException.FAILURE, 
                    (X509Certificate) certPath.getCertificates().get(index),
                    "KeyUsage extension present but keyCertSign bit not asserted");
            }
        }
    }
}
