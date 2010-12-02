package org.cagrid.gaards.pki;

import gov.nih.nci.cagrid.common.security.SecurityConstants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Stack;
import java.util.StringTokenizer;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.bc.BouncyCastleUtil;
import org.globus.util.Base64;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CertUtil {

    public static String getHashCode(X509Certificate cert) throws Exception {
        X509Principal x509 = (X509Principal) cert.getSubjectDN();
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] bytes = digest.digest(x509.getEncoded());
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < 4; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                hexString.insert(0, "0" + hex);
            } else {
                hexString.insert(0, hex);
            }
        }
        return hexString.toString();
    }


    public static void writeSigningPolicy(X509Certificate cert, File f) throws Exception {
        PrintWriter out = new PrintWriter(f);
        out.println("access_id_CA X509 '" + subjectToIdentity(cert.getSubjectDN().getName()) + "'");
        out.println("pos_rights globus CA:sign");
        out.println("cond_subjects globus '\"*\"'");
        out.close();
    }


    public static String subjectToIdentity(String subject) {
        return "/" + subject.replace(',', '/');
    }


    public static PKCS10CertificationRequest generateCertficateRequest(String subject, KeyPair pair) throws Exception {
        SecurityUtil.init();
        return generateCertficateRequest(
            SecurityConstants.CRYPTO_PROVIDER, subject, pair, SecurityConstants.DEFAULT_SIGNING_ALGORITHM);

    }


    public static PKCS10CertificationRequest generateCertficateRequest(String provider, String subject, KeyPair pair,
        String algorithm) throws Exception {
        return new PKCS10CertificationRequest(algorithm, new X509Principal(subject), pair.getPublic(), null, pair
            .getPrivate(), provider);
    }


    public static X509Certificate signCertificateRequest(PKCS10CertificationRequest request, Date start, Date expired,
        X509Certificate cacert, PrivateKey signerKey, String policyId) throws InvalidKeyException,
        NoSuchProviderException, SignatureException, NoSuchAlgorithmException, IOException {
        SecurityUtil.init();
        return signCertificateRequest(
            SecurityConstants.CRYPTO_PROVIDER, request, start, expired, cacert, signerKey, SecurityConstants.DEFAULT_SIGNING_ALGORITHM, policyId);
    }


    public static X509Certificate signCertificateRequest(String provider, PKCS10CertificationRequest request,
        Date start, Date expired, X509Certificate cacert, PrivateKey signerKey, String signatureAlgorithm,
        String policyId) throws InvalidKeyException, NoSuchProviderException, SignatureException,
        NoSuchAlgorithmException, IOException {
        return generateCertificate(provider, request.getCertificationRequestInfo().getSubject(), start, expired,
            request.getPublicKey(provider), cacert, signerKey, signatureAlgorithm, policyId);
    }


    public static X509Certificate generateCACertificate(X509Name subject, Date start, Date expired, KeyPair pair)
        throws InvalidKeyException, NoSuchProviderException, SignatureException, IOException {
        return generateCACertificate(
            SecurityConstants.CRYPTO_PROVIDER, subject, start, expired, pair, 1, SecurityConstants.DEFAULT_SIGNING_ALGORITHM);
    }


    public static X509Certificate generateCACertificate(String provider, X509Name subject, Date start, Date expired,
        KeyPair pair, String signatureAlgorithm) throws InvalidKeyException, NoSuchProviderException,
        SignatureException, IOException {
        return generateCACertificate(provider, subject, start, expired, pair, 1, signatureAlgorithm);
    }


    public static X509Certificate generateIntermediateCACertificate(X509Certificate cacert, PrivateKey signerKey,
        X509Name subject, Date start, Date expired, PublicKey publicKey) throws InvalidKeyException,
        NoSuchProviderException, SignatureException, IOException {
        return generateIntermediateCACertificate(
            SecurityConstants.CRYPTO_PROVIDER, cacert, signerKey, subject, start, expired, publicKey,
            SecurityConstants.DEFAULT_SIGNING_ALGORITHM);
    }


    public static X509Certificate generateIntermediateCACertificate(String provider, X509Certificate cacert,
        PrivateKey signerKey, X509Name subject, Date start, Date expired, PublicKey publicKey, String signatureAlgorithm)
        throws InvalidKeyException, NoSuchProviderException, SignatureException, IOException {
        int constraints = cacert.getBasicConstraints();
        if (constraints <= 1) {
            throw new SignatureException(
                "The CA Certificate specified cannot generate an intermediate CA certificate (Basic Constraints :"
                    + constraints + ")");
        }
        constraints = constraints - 1;

        // generate the certificate
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X509Name(cacert.getSubjectDN().toString()));
        certGen.setNotBefore(start);
        certGen.setNotAfter(expired);
        certGen.setSubjectDN(subject);
        certGen.setPublicKey(publicKey);
        certGen.setSignatureAlgorithm(signatureAlgorithm);
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(constraints));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature
            | KeyUsage.keyEncipherment | KeyUsage.keyCertSign));

        SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
            new ByteArrayInputStream(publicKey.getEncoded())).readObject());
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifier(spki));

        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
            new ByteArrayInputStream(cacert.getPublicKey().getEncoded())).readObject());
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifier(apki));
        return certGen.generateX509Certificate(signerKey, provider);
    }


    public static X509Certificate generateCACertificate(X509Name subject, Date start, Date expired, KeyPair pair,
        int numberOfCAs) throws InvalidKeyException, NoSuchProviderException, SignatureException, IOException {
        return generateCACertificate(
            SecurityConstants.CRYPTO_PROVIDER, subject, start, expired, 
            pair, numberOfCAs, SecurityConstants.DEFAULT_SIGNING_ALGORITHM);
    }


    public static X509Certificate generateCACertificate(String provider, X509Name subject, Date start, Date expired,
        KeyPair pair, int numberOfCAs, String signartureAlgorthm) throws InvalidKeyException, NoSuchProviderException,
        SignatureException, IOException {
        // generate the certificate
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(subject);
        certGen.setNotBefore(start);
        certGen.setNotAfter(expired);
        certGen.setSubjectDN(subject);
        certGen.setPublicKey(pair.getPublic());
        certGen.setSignatureAlgorithm(signartureAlgorthm);
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(numberOfCAs));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature
            | KeyUsage.keyCertSign | KeyUsage.cRLSign));

        SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
            new ByteArrayInputStream(pair.getPublic().getEncoded())).readObject());
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifier(spki));

        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
            new ByteArrayInputStream(pair.getPublic().getEncoded())).readObject());
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifier(apki));
        return certGen.generateX509Certificate(pair.getPrivate(), provider);
    }


    public static X509Certificate generateCertificate(X509Name subject, Date start, Date expired, PublicKey publicKey,
        X509Certificate cacert, PrivateKey signerKey, String policyId) throws InvalidKeyException,
        NoSuchProviderException, SignatureException, IOException {
        return generateCertificate(
            SecurityConstants.CRYPTO_PROVIDER, subject, start, expired, publicKey, cacert, signerKey, 
            SecurityConstants.DEFAULT_SIGNING_ALGORITHM, policyId);
    }


    public static X509Certificate generateCertificate(String provider, X509Name subject, Date start, Date expired,
        PublicKey publicKey, X509Certificate cacert, PrivateKey signerKey, String signatureAlgorithm, String policyId)
        throws InvalidKeyException, NoSuchProviderException, SignatureException, IOException {
        // create the certificate using the information in the request
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setIssuerDN(new X509Name(cacert.getSubjectDN().getName()));
        certGen.setNotBefore(start);
        certGen.setNotAfter(expired);
        certGen.setSubjectDN(subject);
        certGen.setPublicKey(publicKey);
        certGen.setSignatureAlgorithm(signatureAlgorithm);
        certGen.addExtension(X509Extensions.BasicConstraints, true, new BasicConstraints(false));
        certGen.addExtension(X509Extensions.KeyUsage, true, new KeyUsage(KeyUsage.digitalSignature
            | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment | KeyUsage.nonRepudiation));

        SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
            new ByteArrayInputStream(publicKey.getEncoded())).readObject());
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifier(spki));

        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
            new ByteArrayInputStream(cacert.getPublicKey().getEncoded())).readObject());
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifier(apki));
        if (policyId != null) {
            PolicyInformation pi = new PolicyInformation(new DERObjectIdentifier(policyId));
            DERSequence seq = new DERSequence(pi);
            certGen.addExtension(X509Extensions.CertificatePolicies.getId(), false, seq);
        }

        X509Certificate issuedCert = certGen.generateX509Certificate(signerKey, provider);
        return issuedCert;
    }


    public static void writeCertificate(X509Certificate cert, File path) throws IOException {
        PEMWriter pem = new PEMWriter(new FileWriter(path));
        pem.writeObject(cert);
        pem.close();
    }


    public static String writeCertificate(X509Certificate cert) throws IOException {
        StringWriter sw = new StringWriter();
        PEMWriter pem = new PEMWriter(sw);
        pem.writeObject(cert);
        pem.close();
        return sw.toString();
    }


    public static void writeCertificateRequest(PKCS10CertificationRequest cert, String path) throws IOException {
        PEMWriter pem = new PEMWriter(new FileWriter(new File(path)));
        pem.writeObject(cert);
        pem.close();
    }


    /**
     * Just a facade to loadCertificate(InputStream)
     * 
     * @param certLocation
     * @return
     * @throws IOException
     * @throws NoSuchProviderException 
     * @throws CertificateException 
     */
    public static X509Certificate loadCertificate(File certLocation) throws IOException, CertificateException, NoSuchProviderException {
        return loadCertificate(null, certLocation);
    }


    /**
     * Just a facade to loadCertificate(InputStream)
     * 
     * @param certLocation
     * @return
     * @throws IOException
     * @throws NoSuchProviderException 
     * @throws CertificateException 
     */
    public static X509Certificate loadCertificate(String str) throws IOException, CertificateException, NoSuchProviderException {
        return CertUtil.loadCertificate(new ByteArrayInputStream(str.getBytes()));
    }
    
    
    /**
     * Just a facade to loadCertificate(String, InputStream)
     * 
     * @param certLocation
     * @return
     * @throws IOException
     * @throws NoSuchProviderException 
     * @throws CertificateException 
     */
    public static X509Certificate loadCertificate(InputStream certLocation) throws IOException, CertificateException, NoSuchProviderException {
        return loadCertificate(null, certLocation);
    }


    /**
     * Just a facade to loadCertificate(String, InputStream)
     * 
     * @param provider
     * @param certLocation
     * @return
     * @throws IOException
     * @throws NoSuchProviderException 
     * @throws CertificateException 
     */
    public static X509Certificate loadCertificate(String provider, File certLocation) throws IOException, CertificateException, NoSuchProviderException {
        FileInputStream fis = new FileInputStream(certLocation);
        X509Certificate cert = loadCertificate(provider, fis);
        fis.close();
        return cert;
    }


    /**
     * Just a facade to loadCertificate(String, InputStream)
     * 
     * @param provider
     * @param certLocation
     * @return
     * @throws IOException
     * @throws NoSuchProviderException 
     * @throws CertificateException 
     */
    public static X509Certificate loadCertificate(String provider, String str) throws IOException, CertificateException, NoSuchProviderException {
        return CertUtil.loadCertificate(provider, new ByteArrayInputStream(str.getBytes()));
    }


    public static X509Certificate loadCertificate(String provider, InputStream in) throws CertificateException, NoSuchProviderException {
        if (provider == null) {
            // Default the provider to the CERT_PROVIDER
            provider = SecurityConstants.CERT_PROVIDER;
        }
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509", provider);
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
        return cert;
    }


    public static PKCS10CertificationRequest loadCertificateRequest(File certLocation) throws IOException,
        GeneralSecurityException {
        return loadCertificateRequest(SecurityConstants.CRYPTO_PROVIDER, new FileReader(certLocation));
    }


    public static PKCS10CertificationRequest loadCertificateRequest(InputStream certLocation) throws IOException,
        GeneralSecurityException {
        return loadCertificateRequest(SecurityConstants.CRYPTO_PROVIDER, certLocation);
    }


    public static PKCS10CertificationRequest loadCertificateRequest(String str) throws IOException,
        GeneralSecurityException {
        return CertUtil.loadCertificateRequest(SecurityConstants.CRYPTO_PROVIDER, str);
    }


    public static PKCS10CertificationRequest loadCertificateRequest(String provider, File certLocation)
        throws IOException, GeneralSecurityException {
        return loadCertificateRequest(provider, new FileReader(certLocation));
    }


    public static PKCS10CertificationRequest loadCertificateRequest(String provider, InputStream certLocation)
        throws IOException, GeneralSecurityException {
        return loadCertificateRequest(provider, new InputStreamReader(certLocation));
    }


    public static PKCS10CertificationRequest loadCertificateRequest(String provider, String str) throws IOException,
        GeneralSecurityException {
        StringReader reader = new StringReader(str);
        return CertUtil.loadCertificateRequest(provider, reader);
    }


    public static PKCS10CertificationRequest loadCertificateRequest(Reader in) throws IOException,
        GeneralSecurityException {
        return CertUtil.loadCertificateRequest(SecurityConstants.CRYPTO_PROVIDER, in);
    }


    public static PKCS10CertificationRequest loadCertificateRequest(String provider, Reader in) throws IOException,
        GeneralSecurityException {
        String line = null;
        StringBuffer stringBuffer = new StringBuffer();
        final BufferedReader bufferReader = new BufferedReader(in);

        // Dont store the first and last line of the request.
        String firstLine = bufferReader.readLine();
        if (firstLine == null || !firstLine.equals("-----BEGIN CERTIFICATE REQUEST-----")) {
            throw new GeneralSecurityException("Malformed certificate request.");
        }
        String lastLine = null;
        while ((line = bufferReader.readLine()) != null) {
            lastLine = line;
            stringBuffer.append(line + "\n");
        }
        if (lastLine == null || !lastLine.equals("-----END CERTIFICATE REQUEST-----")) {
            throw new GeneralSecurityException("Malformed certificate request.");
        }

        stringBuffer.delete(stringBuffer.length() - lastLine.length() - 1, stringBuffer.length() - 1);

        byte[] base64Encoded = stringBuffer.toString().getBytes();
        byte[] data = Base64.decode(base64Encoded);
        PKCS10CertificationRequest request = new PKCS10CertificationRequest(data);
        return request;
    }


    public static X509CRL createCRL(X509Certificate caCert, PrivateKey caKey, CRLEntry[] entries, Date expires)
        throws Exception {
        return createCRL(
            SecurityConstants.CRYPTO_PROVIDER, caCert, caKey, 
            entries, expires, SecurityConstants.DEFAULT_SIGNING_ALGORITHM);
    }


    public static X509CRL createCRL(String provider, X509Certificate caCert, PrivateKey caKey, CRLEntry[] entries,
        Date expires, String signatureAlgorithm) throws Exception {
        X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
        Date now = new Date();
        crlGen.setIssuerDN(new X509Name(caCert.getSubjectDN().getName()));
        crlGen.setThisUpdate(now);
        crlGen.setNextUpdate(expires);
        crlGen.setSignatureAlgorithm(signatureAlgorithm);
        for (int i = 0; i < entries.length; i++) {
            crlGen.addCRLEntry(entries[i].getCertificateSerialNumber(), now, entries[i].getReason());
        }
        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new ASN1InputStream(
            new ByteArrayInputStream(caCert.getPublicKey().getEncoded())).readObject());
        crlGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifier(apki));
        crlGen.addExtension(X509Extensions.CRLNumber, false, new CRLNumber(BigInteger.valueOf(System
            .currentTimeMillis())));
        return crlGen.generateX509CRL(caKey, provider);
    }


    public static void writeCRL(X509CRL crl, File path) throws IOException {
        PEMWriter pem = new PEMWriter(new FileWriter(path));
        pem.writeObject(crl);
        pem.close();
    }


    public static String writeCRL(X509CRL crl) throws IOException {
        StringWriter sw = new StringWriter();
        PEMWriter pem = new PEMWriter(sw);
        pem.writeObject(crl);
        pem.close();
        return sw.toString();
    }


    /**
     * Just a facade to loadCRL(Reader)
     * 
     * @param crlLocation
     * @return
     * @throws IOException
     */
    public static X509CRL loadCRL(File crlLocation) throws IOException {
        return loadCRL(new FileReader(crlLocation));
    }


    /**
     * Just a facade to loadCRL(Reader)
     * 
     * @param crlLocation
     * @return
     * @throws IOException
     */
    public static X509CRL loadCRL(InputStream crlLocation) throws IOException {
        return loadCRL(new InputStreamReader(crlLocation));
    }

    
    /**
     * Just a facade to loadCRL(Reader)
     * 
     * @param crlLocation
     * @return
     * @throws IOException
     */
    public static X509CRL loadCRL(String str) throws IOException {
        return CertUtil.loadCRL(new StringReader(str));
    }
        
    
    /**
     * Just a facade to loadCRL(String, Reader)
     * 
     * @param crlLocation
     * @return
     * @throws IOException
     */
    public static X509CRL loadCRL(Reader reader) throws IOException {
        return loadCRL(null, reader);
    }


    /**
     * Just a facade to loadCRL(String, Reader)
     * 
     * @param crlLocation
     * @return
     * @throws IOException
     */
    public static X509CRL loadCRL(String provider, File crlLocation) throws IOException {
        return loadCRL(provider, new FileReader(crlLocation));
    }

    
    /**
     * Just a facade to loadCRL(String, Reader)
     * 
     * @param crlLocation
     * @return
     * @throws IOException
     */
    public static X509CRL loadCRL(String provider, InputStream crlLocation) throws IOException {
        return loadCRL(provider, new InputStreamReader(crlLocation));
    }


    /**
     * Just a facade to loadCRL(String, Reader)
     * 
     * @param crlLocation
     * @return
     * @throws IOException
     */
    public static X509CRL loadCRL(String provider, String str) throws IOException {
        return CertUtil.loadCRL(provider, new StringReader(str));
    }


    public static X509CRL loadCRL(String provider, Reader in) throws IOException {
        if (provider == null) {
            provider = SecurityConstants.CERT_PROVIDER;
        }
        CRLReader reader = new CRLReader(in, provider);
        X509CRL crl = reader.readCRL();
        reader.close();
        return crl;
    }


    public static boolean isExpired(X509Certificate cert) {
        Date now = new Date();
        if (now.before(cert.getNotBefore()) || (now.after(cert.getNotAfter()))) {
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * Gets the certificate's subject DN in "reverse" format -- most significant
     * part first.  This is exactly the opposite of what RFC 2253 says to do,
     * but apparently it's the format which BouncyCastle and the rest of the
     * older Globus code expect and work with.
     * 
     * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>
     * 
     * @param cert
     * @return
     */
    public static String getSubjectDN(X509Certificate cert) {
        String dn = cert.getSubjectDN().toString();
        return globusFormatDN(dn);
    }
    
    
    /**
     * Gets the certificate's issuer DN in "reverse" format -- most significant
     * part first.  This is exactly the opposite of what RFC 2253 says to do,
     * but apparently it's the format which BouncyCastle and the rest of the
     * older Globus code expect and work with.
     * 
     * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>
     * 
     * @param cert
     * @return
     */
    public static String getIssuerDN(X509Certificate cert) {
        String dn = cert.getIssuerDN().toString();
        return globusFormatDN(dn);
    }
    
    
    public static String globusFormatDN(String dn) {
        if (dn != null && dn.startsWith("CN=")) {
            dn = reverseDN(dn);
        }
        return dn;
    }
    
    
    public static String getIdentity(GlobusCredential cred) throws CertificateException {
        X509Certificate[] chain = cred.getCertificateChain();
        X509Certificate idCert = BouncyCastleUtil.getIdentityCertificate(chain);
        String identity = null;
        if (idCert != null) {
            identity = getSubjectDN(idCert);
        }
        return identity;
    }
    
    
    private static String reverseDN(String dn) {
        StringBuffer reverse = new StringBuffer();
        StringTokenizer tok = new StringTokenizer(dn, ",");
        Stack<String> stack = new Stack<String>();
        while (tok.hasMoreTokens()) {
            stack.push(tok.nextToken());
        }
        while (!stack.isEmpty()) {
            reverse.append(stack.pop().trim());
            if (!stack.isEmpty()) {
                reverse.append(",");
            }
        }
        return reverse.toString();
    }
}
