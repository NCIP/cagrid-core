package gov.nih.nci.cagrid.gts.service;

import gov.nih.nci.cagrid.gts.test.CA;
import gov.nih.nci.cagrid.gts.test.Credential;

import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Enumeration;

import junit.framework.TestCase;

import org.bouncycastle.asn1.x509.CRLReason;
import org.cagrid.gaards.pki.CRLEntry;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.pki.ProxyCreator;
import org.globus.gsi.proxy.ProxyPathValidatorException;

import cryptix.provider.Cryptix;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A href="mailto:David.Ervin@osumc.edu">David W. Ervin</A>
 */

public class TestBetterProxyPathValidator extends TestCase {
    
    public static final String CRL_ERROR = "is on a CRL";
    public static final String UNKNOWN_CA = "Unknown CA";

    private CertificateFactory factory = null;
    private CA caX = null;
    private CA caY = null;
    private String userA = null;
    private String userB = null;
    private Credential credX1 = null;
    private Credential credX2 = null;
    private Credential credY1 = null;
    private BetterProxyPathValidator validator = null;


    public TestBetterProxyPathValidator(String name) {
        super(name);
    }


    private CertPath getCertPath(X509Certificate[] certs) {
        CertPath path = null;
        try {
            path = factory.generateCertPath(Arrays.asList(certs));
        } catch (CertificateException ex) {
            ex.printStackTrace();
            fail("Unable to build cert path! " + ex.getMessage());
        }
        return path;
    }


    public void setUp() {
        userA = "User A";
        userB = "User B";
        try {
            // cert factory
            factory = CertificateFactory.getInstance("X.509");
            // create two CAs, X and Y
            caX = new CA();
            caY = new CA();
            // create user identity certs for user A and B from CA X
            credX1 = caX.createIdentityCertificate(userA);
            credX2 = caX.createIdentityCertificate(userB);
            // create user identity cert for user A from CA Y
            credY1 = caY.createIdentityCertificate(userA);
            // revoke userB's cert on CA X
            CRLEntry credX2CRL = new CRLEntry(credX2.getCertificate().getSerialNumber(), 
                CRLReason.PRIVILEGE_WITHDRAWN);
            caX.updateCRL(credX2CRL);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error setting up certificates for testing: " + ex.getMessage());
        }

        validator = new BetterProxyPathValidator();
    }


    public void testValidCert() {
        // get the trusted certs from caX
        X509Certificate[] trusted1 = new X509Certificate[1];
        trusted1[0] = caX.getCertificate();

        // get the CRL from caX
        X509CRL crl = caX.getCRL();

        // get the cert path for userA / credential X1
        X509Certificate[] chainX1 = new X509Certificate[1];
        chainX1[0] = credX1.getCertificate();
        CertPath pathX1 = getCertPath(chainX1);

        // validate
        try {
            validator.validate(pathX1, trusted1, crl);
        } catch (ProxyPathValidatorException ex) {
            ex.printStackTrace();
            fail("Should have been valid: " + ex.getMessage());
        } catch (CertificateEncodingException ex) {
            ex.printStackTrace();
            fail("Error encoding credentials: " + ex.getMessage());
        }
    }
    
    
    public void testRevokedCert() {
        X509Certificate[] trusted1 = new X509Certificate[1];
        trusted1[0] = caX.getCertificate();

        X509CRL crl = caX.getCRL();
        try {
            X509Certificate[] chainX2 = new X509Certificate[1];
            chainX2[0] = credX2.getCertificate();
            CertPath pathX2 = getCertPath(chainX2);
            validator.validate(pathX2, trusted1, crl);
            fail("Should not be able to validate certificate on the CRL!!!");
        } catch (ProxyPathValidatorException ex) {
            // expected, but verify the message
            assertTrue("Validation message was not as expected! " +
            	"(Found " + ex.getMessage() + ")", ex.getMessage().endsWith(CRL_ERROR));
        } catch (CertificateEncodingException ex) {
            ex.printStackTrace();
            fail("Error encoding certificates");
        }
    }
    
    
    public void testUnknownCaCert() {
        // trust the cert from CA X
        X509Certificate[] trusted1 = new X509Certificate[1];
        trusted1[0] = caX.getCertificate();

        // get CA X's CRL
        X509CRL crl = caX.getCRL();
        
        try {
            // create the cert chain for user A, signed by CA Y
            X509Certificate[] chainY1 = new X509Certificate[1];
            chainY1[0] = credY1.getCertificate();
            CertPath pathY1 = getCertPath(chainY1);
            // try to validate chain for Y1, but only trust CA X's cert
            validator.validate(pathY1, trusted1, crl);
            fail("Should not be able to validate certificate signed by an untrusted CA!!!");
        } catch (ProxyPathValidatorException ex) {
            // expected, but verify the message
            assertTrue("Unexpected validation error message (Found " + ex.getMessage() + ")", 
                ex.getMessage().contains(UNKNOWN_CA));
        } catch (CertificateEncodingException ex) {
            ex.printStackTrace();
            fail("Error encoding certificates");
        }
    }
    
    
    public void testValidProxy() {
        X509Certificate[] trusted1 = new X509Certificate[1];
        trusted1[0] = caX.getCertificate();

        X509CRL crl = caX.getCRL();

        X509Certificate[] chainX1 = new X509Certificate[1];
        chainX1[0] = credX1.getCertificate();
        
        try {
            X509Certificate[] proxyChainX1 = ProxyCreator.createImpersonationProxyCertificate(credX1.getCertificate(),
                credX1.getPrivateKey(), KeyUtil.generateRSAKeyPair512().getPublic(), 12, 0, 0);
            CertPath proxyPathX1 = getCertPath(proxyChainX1);
            validator.validate(proxyPathX1, trusted1, crl);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Should have been able to validate proxy: " + ex.getMessage());
        }
    }
    
    
    public void testRevokedProxy() {
        X509Certificate[] trusted1 = new X509Certificate[1];
        trusted1[0] = caX.getCertificate();

        X509CRL crl = caX.getCRL();
        try {
            X509Certificate[] proxyChainX2 = ProxyCreator.createImpersonationProxyCertificate(credX2.getCertificate(),
                credX2.getPrivateKey(), KeyUtil.generateRSAKeyPair512().getPublic(), 12, 0, 0);
            CertPath proxyPathX2 = getCertPath(proxyChainX2);
            validator.validate(proxyPathX2, trusted1, crl);
            fail("Should not be able to validate revoked proxy certificate!!!");
        } catch (ProxyPathValidatorException ex) {
            // expected, but verify the message
            assertTrue("Validation message was not as expected! " +
                "(Found " + ex.getMessage() + ")", ex.getMessage().endsWith(CRL_ERROR));
        } catch (CertificateEncodingException ex) {
            ex.printStackTrace();
            fail("Error encoding credentials: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error: " + ex.getMessage());
        }
    }


    public void testUnknownCaProxy() {
        X509Certificate[] trusted1 = new X509Certificate[1];
        trusted1[0] = caX.getCertificate();

        X509CRL crl = caX.getCRL();

        X509Certificate[] chainX1 = new X509Certificate[1];
        chainX1[0] = credX1.getCertificate();
        
        try {
            X509Certificate[] proxyChainY1 = ProxyCreator.createImpersonationProxyCertificate(credY1.getCertificate(),
                credY1.getPrivateKey(), KeyUtil.generateRSAKeyPair512().getPublic(), 12, 0, 0);
            CertPath proxyPathY1 = factory.generateCertPath(Arrays.asList(proxyChainY1));
            validator.validate(proxyPathY1, trusted1, crl);
            fail("Should not be able to validate certificate!!!");
        } catch (ProxyPathValidatorException ex) {
            // expected, but verify the message
            assertTrue("Unexpected validation error message (Found " + ex.getMessage() + ")", 
                ex.getMessage().contains(UNKNOWN_CA));
        } catch (CertificateEncodingException ex) {
            ex.printStackTrace();
            fail("Error encoding credentials: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected error: " + ex.getMessage());
        }
    }


    private static void dumpProviders() {

        Security.addProvider(new Cryptix());
        try {
            Provider p[] = Security.getProviders();
            for (int i = 0; i < p.length; i++) {
                System.out.println(p[i]);
                for (Enumeration e = p[i].keys(); e.hasMoreElements();)
                    System.out.println("\t" + e.nextElement());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
