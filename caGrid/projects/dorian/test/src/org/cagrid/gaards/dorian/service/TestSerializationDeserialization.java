package org.cagrid.gaards.dorian.service;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.InvalidCryptoException;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLAttribute;
import gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement;
import gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement;
import gov.nih.nci.cagrid.opensaml.SAMLNameIdentifier;
import gov.nih.nci.cagrid.opensaml.SAMLSubject;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.xml.security.signature.XMLSignature;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cagrid.gaards.core.Utils;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.cagrid.gaards.dorian.federation.PublicKey;
import org.cagrid.gaards.dorian.stubs.RequestUserCertificateRequest;
import org.cagrid.gaards.dorian.stubs.RequestUserCertificateRequestKey;
import org.cagrid.gaards.dorian.stubs.RequestUserCertificateRequestLifetime;
import org.cagrid.gaards.dorian.stubs.RequestUserCertificateRequestSaml;
import org.cagrid.gaards.pki.CA;
import org.cagrid.gaards.pki.Credential;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.saml.encoding.SAMLConstants;


public class TestSerializationDeserialization extends TestCase {

    public void testRequestUserCertificateRequest() {
        try {
            CA ca = new CA();
            Credential c = ca.createIdentityCertificate("some signer");
            Credential c2 = ca.createIdentityCertificate("some other signer");

            X509Certificate cert = c.getCertificate();
            PrivateKey key = c.getPrivateKey();

            String uid = "potter";
            String firstName = "Harry";
            String lastName = "Potter";
            String email = "harry@harrypotter.com";
            SAMLAssertion saml = null;

            org.apache.xml.security.Init.init();
            GregorianCalendar cal = new GregorianCalendar();
            Date start = cal.getTime();
            cal.add(Calendar.MINUTE, 2);
            Date end = cal.getTime();
            String issuer = cert.getSubjectDN().toString();
            String federation = cert.getSubjectDN().toString();
            String ipAddress = null;
            String subjectDNS = null;

            SAMLNameIdentifier ni1 = new SAMLNameIdentifier(uid, federation,
                "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
            SAMLSubject sub = new SAMLSubject(ni1, null, null, null);
            sub.addConfirmationMethod(SAMLSubject.CONF_BEARER);
            SAMLNameIdentifier ni2 = new SAMLNameIdentifier(uid, federation,
                "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
            SAMLSubject sub2 = new SAMLSubject(ni2, null, null, null);
            sub2.addConfirmationMethod(SAMLSubject.CONF_BEARER);
            SAMLAuthenticationStatement auth = new SAMLAuthenticationStatement(sub,
                "urn:oasis:names:tc:SAML:1.0:am:password", new Date(), ipAddress, subjectDNS, null);

            QName quid = new QName(SAMLConstants.UID_ATTRIBUTE_NAMESPACE, SAMLConstants.UID_ATTRIBUTE);
            List<String> vals1 = new ArrayList<String>();
            vals1.add(uid);
            SAMLAttribute uidAtt = new SAMLAttribute(quid.getLocalPart(), quid.getNamespaceURI(), null, 0, vals1);

            QName qfirst = new QName(SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE, SAMLConstants.FIRST_NAME_ATTRIBUTE);
            List<String> vals2 = new ArrayList<String>();
            vals2.add(firstName);
            SAMLAttribute firstNameAtt = new SAMLAttribute(qfirst.getLocalPart(), qfirst.getNamespaceURI(), null, 0,
                vals2);

            QName qLast = new QName(SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE, SAMLConstants.LAST_NAME_ATTRIBUTE);
            List<String> vals3 = new ArrayList<String>();
            vals3.add(lastName);
            SAMLAttribute lastNameAtt = new SAMLAttribute(qLast.getLocalPart(), qLast.getNamespaceURI(), null, 0, vals3);

            QName qemail = new QName(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE, SAMLConstants.EMAIL_ATTRIBUTE);
            List<String> vals4 = new ArrayList<String>();
            vals4.add(email);
            SAMLAttribute emailAtt = new SAMLAttribute(qemail.getLocalPart(), qemail.getNamespaceURI(), null, 0, vals4);

            List<SAMLAttribute> atts = new ArrayList<SAMLAttribute>();
            atts.add(uidAtt);
            atts.add(firstNameAtt);
            atts.add(lastNameAtt);
            atts.add(emailAtt);

            SAMLAttributeStatement attState = new SAMLAttributeStatement(sub2, atts);

            List l = new ArrayList();
            l.add(auth);
            l.add(attState);

            saml = new SAMLAssertion(issuer, start, end, null, null, l);
            List<X509Certificate> a = new ArrayList<X509Certificate>();
            a.add(cert);
            saml.sign(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, key, a);

            saml.verify();
            saml.verify(cert);
            try {
                saml.verify(c2.getCertificate());
                fail("Assertion should not be verified.");
            } catch (InvalidCryptoException e) {

            }
            RequestUserCertificateRequest req = new RequestUserCertificateRequest();
            RequestUserCertificateRequestSaml reqSAML = new RequestUserCertificateRequestSaml();
            reqSAML.setAssertion(saml);
            req.setSaml(reqSAML);
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            PublicKey pkey = new PublicKey();
            pkey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
            RequestUserCertificateRequestKey keyReq = new RequestUserCertificateRequestKey();
            keyReq.setPublicKey(pkey);
            req.setKey(keyReq);
            CertificateLifetime cl = new CertificateLifetime();
            cl.setHours(12);
            cl.setMinutes(0);
            cl.setSeconds(0);
            RequestUserCertificateRequestLifetime lReq = new RequestUserCertificateRequestLifetime();
            lReq.setCertificateLifetime(cl);
            req.setLifetime(lReq);
            String str = Utils.serialize(req);
            RequestUserCertificateRequest req2 = (RequestUserCertificateRequest) Utils.deserialize(str,
                RequestUserCertificateRequest.class);
            SAMLAssertion saml2 = req2.getSaml().getAssertion();
            saml2.verify(cert);
            try {
                saml2.verify(c2.getCertificate());
                fail("Assertion should not be verified.");
            } catch (InvalidCryptoException e) {

            }
            assertEquals(req2.getLifetime().getCertificateLifetime(), cl);
            assertEquals(req2.getKey().getPublicKey(), pkey);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }
}
