package org.cagrid.gaards.pki;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
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
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInputStream;
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
import org.bouncycastle.jce.X509V2CRLGenerator;
import org.bouncycastle.jce.X509V3CertificateGenerator;
import org.bouncycastle.openssl.PEMReader;
import org.globus.util.Base64;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CertUtil {

    public static final String SIGNATURE_ALGORITHM = "SHA1WithRSAEncryption";


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
        return generateCertficateRequest("BC", subject, pair, SIGNATURE_ALGORITHM);

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
        return signCertificateRequest("BC", request, start, expired, cacert, signerKey, SIGNATURE_ALGORITHM, policyId);
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
        SecurityUtil.init();
        return generateCACertificate("BC", subject, start, expired, pair, 1, SIGNATURE_ALGORITHM);
    }


    public static X509Certificate generateCACertificate(String provider, X509Name subject, Date start, Date expired,
        KeyPair pair, String signatureAlgorithm) throws InvalidKeyException, NoSuchProviderException,
        SignatureException, IOException {
        return generateCACertificate(provider, subject, start, expired, pair, 1, signatureAlgorithm);
    }


    public static X509Certificate generateIntermediateCACertificate(X509Certificate cacert, PrivateKey signerKey,
        X509Name subject, Date start, Date expired, PublicKey publicKey) throws InvalidKeyException,
        NoSuchProviderException, SignatureException, IOException {
        SecurityUtil.init();
        return generateIntermediateCACertificate("BC", cacert, signerKey, subject, start, expired, publicKey,
            SIGNATURE_ALGORITHM);
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

        SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(
            new ByteArrayInputStream(publicKey.getEncoded())).readObject());
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifier(spki));

        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(
            new ByteArrayInputStream(cacert.getPublicKey().getEncoded())).readObject());
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifier(apki));
        return certGen.generateX509Certificate(signerKey, provider);
    }


    public static X509Certificate generateCACertificate(X509Name subject, Date start, Date expired, KeyPair pair,
        int numberOfCAs) throws InvalidKeyException, NoSuchProviderException, SignatureException, IOException {
        SecurityUtil.init();
        return generateCACertificate("BC", subject, start, expired, pair, numberOfCAs, SIGNATURE_ALGORITHM);
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

        SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(
            new ByteArrayInputStream(pair.getPublic().getEncoded())).readObject());
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifier(spki));

        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(
            new ByteArrayInputStream(pair.getPublic().getEncoded())).readObject());
        certGen.addExtension(X509Extensions.AuthorityKeyIdentifier, false, new AuthorityKeyIdentifier(apki));
        return certGen.generateX509Certificate(pair.getPrivate(), provider);
    }


    public static X509Certificate generateCertificate(X509Name subject, Date start, Date expired, PublicKey publicKey,
        X509Certificate cacert, PrivateKey signerKey, String policyId) throws InvalidKeyException,
        NoSuchProviderException, SignatureException, IOException {
        SecurityUtil.init();
        return generateCertificate("BC", subject, start, expired, publicKey, cacert, signerKey, SIGNATURE_ALGORITHM,
            policyId);
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

        SubjectPublicKeyInfo spki = new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(
            new ByteArrayInputStream(publicKey.getEncoded())).readObject());
        certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false, new SubjectKeyIdentifier(spki));

        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(
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


    public static X509Certificate loadCertificate(File certLocation) throws IOException, GeneralSecurityException {
        SecurityUtil.init();
        return loadCertificate("BC", new FileReader(certLocation));
    }


    public static X509Certificate loadCertificate(InputStream certLocation) throws IOException,
        GeneralSecurityException {
        SecurityUtil.init();
        return loadCertificate("BC", certLocation);
    }


    public static X509Certificate loadCertificate(String str) throws IOException, GeneralSecurityException {
        SecurityUtil.init();
        return CertUtil.loadCertificate("BC", str);

    }


    public static X509Certificate loadCertificate(String provider, File certLocation) throws IOException,
        GeneralSecurityException {
        return loadCertificate(provider, new FileReader(certLocation));
    }


    public static X509Certificate loadCertificate(String provider, InputStream certLocation) throws IOException,
        GeneralSecurityException {
        return loadCertificate(provider, new InputStreamReader(certLocation));
    }


    public static X509Certificate loadCertificate(String provider, String str) throws IOException,
        GeneralSecurityException {
        StringReader reader = new StringReader(str);
        return CertUtil.loadCertificate(provider, reader);

    }


    public static X509Certificate loadCertificate(Reader in) throws IOException, GeneralSecurityException {
        SecurityUtil.init();
        return CertUtil.loadCertificate("BC", in);

    }


    public static X509Certificate loadCertificate(String provider, Reader in) throws IOException,
        GeneralSecurityException {
        PEMReader reader = new PEMReader(in, null, provider);
        X509Certificate cert = (X509Certificate) reader.readObject();
        reader.close();
        return cert;
    }


    public static PKCS10CertificationRequest loadCertificateRequest(File certLocation) throws IOException,
        GeneralSecurityException {
        SecurityUtil.init();
        return loadCertificateRequest("BC", new FileReader(certLocation));
    }


    public static PKCS10CertificationRequest loadCertificateRequest(InputStream certLocation) throws IOException,
        GeneralSecurityException {
        SecurityUtil.init();
        return loadCertificateRequest("BC", certLocation);
    }


    public static PKCS10CertificationRequest loadCertificateRequest(String str) throws IOException,
        GeneralSecurityException {
        SecurityUtil.init();
        return CertUtil.loadCertificateRequest("BC", str);

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
        SecurityUtil.init();
        return CertUtil.loadCertificateRequest("BC", in);

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
        SecurityUtil.init();
        return createCRL("BC", caCert, caKey, entries, expires, SIGNATURE_ALGORITHM);
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
        SubjectPublicKeyInfo apki = new SubjectPublicKeyInfo((ASN1Sequence) new DERInputStream(
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


    public static X509CRL loadCRL(File crlLocation) throws IOException, GeneralSecurityException {
        SecurityUtil.init();
        return loadCRL("BC", new FileReader(crlLocation));
    }


    public static X509CRL loadCRL(InputStream crlLocation) throws IOException, GeneralSecurityException {
        SecurityUtil.init();
        return loadCRL("BC", new InputStreamReader(crlLocation));
    }


    public static X509CRL loadCRL(String str) throws IOException, GeneralSecurityException {
        SecurityUtil.init();
        StringReader reader = new StringReader(str);
        return CertUtil.loadCRL("BC", reader);

    }


    public static X509CRL loadCRL(String provider, File crlLocation) throws IOException, GeneralSecurityException {
        return loadCRL(provider, new FileReader(crlLocation));
    }


    public static X509CRL loadCRL(String provider, InputStream crlLocation) throws IOException,
        GeneralSecurityException {
        return loadCRL(provider, new InputStreamReader(crlLocation));
    }


    public static X509CRL loadCRL(String provider, String str) throws IOException, GeneralSecurityException {
        StringReader reader = new StringReader(str);
        return CertUtil.loadCRL(provider, reader);

    }


    public static X509CRL loadCRL(String provider, Reader in) throws IOException, GeneralSecurityException {
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

}
