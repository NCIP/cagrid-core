package org.cagrid.gaards.credentials;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.cagrid.gaards.pki.CA;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.Credential;
import org.cagrid.gaards.pki.KeyUtil;
import org.globus.gsi.GlobusCredential;


public class TestCredentialEncoding extends TestCase {
    public void testX509Credential() {
        try {
            CA ca = new CA();
            Credential c = ca.createIdentityCertificate("Test");
            GlobusCredential cred = getGlobusCredential(c);
            X509CredentialDescriptor des = new X509CredentialDescriptor();
            des.setIdentity(cred.getIdentity());
            des.setEncodedCertificates(getCertificates(c));
            des.setEncodedKey(KeyUtil.writePrivateKey(c.getPrivateKey(), (String) null));
            String xml = EncodingUtil.serialize(des);
            X509CredentialDescriptor des2 = EncodingUtil.deserialize(xml);
            assertEquals(des, des2);
            GlobusCredential cred2 = getGlobusCredential(des2);
            assertEquals(cred.getIdentity(), cred2.getIdentity());
            assertEquals(cred.getPrivateKey(), cred2.getPrivateKey());
            assertEquals(cred.getCertificateChain().length, cred2.getCertificateChain().length);
            assertEquals(cred.getCertificateChain()[0], cred2.getCertificateChain()[0]);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testDorianUserCredential() {
        File f = new File("test.credential");
        try {
            CA ca = new CA();
            Credential c = ca.createIdentityCertificate("Test");
            GlobusCredential cred = getGlobusCredential(c);
            DorianUserCredentialDescriptor des = new DorianUserCredentialDescriptor();
            des.setAuthenticationServiceURL("https://training.cagrid.org:8443/wsrf/services/cagrid/Dorian");
            des.setDorianURL("https://training.cagrid.org:8443/wsrf/services/cagrid/Dorian");
            des.setEmail("user@cagrid.org");
            des.setFirstName("Grid");
            des.setLastName("User");
            des.setOrganization("caGrid University");
            des.setIdentity(cred.getIdentity());
            des.setEncodedCertificates(getCertificates(c));
            des.setEncodedKey(KeyUtil.writePrivateKey(c.getPrivateKey(), (String) null));
            String xml = EncodingUtil.serialize(des);
            DorianUserCredentialDescriptor des2 = (DorianUserCredentialDescriptor) EncodingUtil.deserialize(xml);
            assertEquals(des, des2);
            GlobusCredential cred2 = getGlobusCredential(des2);
            assertEquals(cred.getIdentity(), cred2.getIdentity());
            assertEquals(cred.getPrivateKey(), cred2.getPrivateKey());
            assertEquals(cred.getCertificateChain().length, cred2.getCertificateChain().length);
            assertEquals(cred.getCertificateChain()[0], cred2.getCertificateChain()[0]);
            EncodingUtil.serialize(f, des);
            DorianUserCredentialDescriptor des3 = (DorianUserCredentialDescriptor) EncodingUtil.deserialize(f);
            assertEquals(des, des3);
            GlobusCredential cred3 = getGlobusCredential(des2);
            assertEquals(cred.getIdentity(), cred3.getIdentity());
            assertEquals(cred.getPrivateKey(), cred3.getPrivateKey());
            assertEquals(cred.getCertificateChain().length, cred3.getCertificateChain().length);
            assertEquals(cred.getCertificateChain()[0], cred3.getCertificateChain()[0]);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        } finally {
            f.delete();
        }
    }


    private GlobusCredential getGlobusCredential(X509CredentialDescriptor des) throws Exception {
        PrivateKey key = KeyUtil.loadPrivateKey(new ByteArrayInputStream(des.getEncodedKey().getBytes()), null);
        GlobusCredential cred = new GlobusCredential(key, getCertificates(des));
        return cred;
    }


    private GlobusCredential getGlobusCredential(Credential c) throws Exception {
        X509Certificate[] certs = new X509Certificate[1];
        certs[0] = c.getCertificate();
        GlobusCredential cred = new GlobusCredential(c.getPrivateKey(), certs);
        return cred;
    }


    private X509Certificate[] getCertificates(X509CredentialDescriptor des) throws Exception {
        EncodedCertificates ec = des.getEncodedCertificates();
        String[] list = ec.getEncodedCertificate();
        X509Certificate[] certs = new X509Certificate[list.length];
        for (int i = 0; i < list.length; i++) {
            certs[i] = CertUtil.loadCertificate(list[i]);
        }
        return certs;
    }


    private EncodedCertificates getCertificates(Credential c) throws Exception {
        EncodedCertificates list = new EncodedCertificates();
        String[] certs = new String[1];
        certs[0] = CertUtil.writeCertificate(c.getCertificate());
        list.setEncodedCertificate(certs);
        return list;
    }

}
