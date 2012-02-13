package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement;
import gov.nih.nci.cagrid.opensaml.SAMLNameIdentifier;
import gov.nih.nci.cagrid.opensaml.SAMLSubject;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import org.apache.xml.security.signature.XMLSignature;
import org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidTrustedIdPFault;
import org.cagrid.gaards.dorian.test.CA;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.Credential;
import org.cagrid.gaards.saml.encoding.SAMLConstants;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestTrustedIdPManager extends TestCase {

    private static final int MIN_NAME_LENGTH = 4;

    private static final int MAX_NAME_LENGTH = 50;

    private Database db;

    private TrustedIdPManager tm;

    private SAMLAuthenticationMethod[] methods;

    private CA ca;


    public void testUniqueCertificates() {
        try {
            TrustedIdP idp1 = getTrustedIdp("IdP 1").getIdp();
            idp1 = tm.addTrustedIdP(idp1);
            assertEquals(1, tm.getTrustedIdPs().length);
            TrustedIdP idp2 = getTrustedIdp("IdP 2").getIdp();
            idp2.setIdPCertificate(idp1.getIdPCertificate());
            try {
                idp2 = tm.addTrustedIdP(idp2);
                assertTrue(false);
            } catch (InvalidTrustedIdPFault f) {

            }
            assertEquals(1, tm.getTrustedIdPs().length);
            TrustedIdP idp3 = getTrustedIdp("IdP 3").getIdp();
            idp3 = tm.addTrustedIdP(idp3);
            assertEquals(2, tm.getTrustedIdPs().length);
            idp3.setIdPCertificate(idp1.getIdPCertificate());
            try {
                tm.updateIdP(idp3);
                assertTrue(false);
            } catch (InvalidTrustedIdPFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testUniqueNames() {
        try {
            String name = "Test IdP";
            TrustedIdP idp1 = getTrustedIdp(name).getIdp();
            idp1 = tm.addTrustedIdP(idp1);
            assertEquals(1, tm.getTrustedIdPs().length);
            TrustedIdP idp2 = getTrustedIdp(name, true).getIdp();
            try {
                idp2 = tm.addTrustedIdP(idp2);
                assertTrue(false);
            } catch (InvalidTrustedIdPFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testInvalidDisplayName() {
        try {
            String name = "Test IdP";
            TrustedIdP idp1 = getTrustedIdp(name).getIdp();
            idp1.setDisplayName(null);
            try {
                idp1 = tm.addTrustedIdP(idp1);
                fail("Should not be able to add a Trusted IdP without a display name.");
            } catch (InvalidTrustedIdPFault f) {
                String str = gov.nih.nci.cagrid.common.Utils.getExceptionMessage(f);
                if (str.indexOf("Invalid IdP display name") == -1) {
                    fail("Unexpected error message received when trying to validate that a trusted idp could not be added with an invalid display name.");
                }
            }
            idp1.setDisplayName("t");
            try {
                idp1 = tm.addTrustedIdP(idp1);
                fail("Should not be able to add a Trusted IdP without a display name.");
            } catch (InvalidTrustedIdPFault f) {
                String str = gov.nih.nci.cagrid.common.Utils.getExceptionMessage(f);
                if (str.indexOf("Invalid IdP display name") == -1) {
                    fail("Unexpected error message received when trying to validate that a trusted idp could not be added with an invalid display name.");
                }
            }

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < 100; i++) {
                sb.append("t");
            }
            idp1.setDisplayName(sb.toString());
            try {
                idp1 = tm.addTrustedIdP(idp1);
                fail("Should not be able to add a Trusted IdP without a display name.");
            } catch (InvalidTrustedIdPFault f) {
                String str = gov.nih.nci.cagrid.common.Utils.getExceptionMessage(f);
                if (str.indexOf("Invalid IdP display name") == -1) {
                    fail("Unexpected error message received when trying to validate that a trusted idp could not be added with an invalid display name.");
                }
            }
            idp1.setDisplayName(idp1.getName());
            idp1 = tm.addTrustedIdP(idp1);
            assertEquals(1, tm.getTrustedIdPs().length);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testInvalidAuthenticationServiceURL() {
        try {
            String name = "Test IdP";
            TrustedIdP idp1 = getTrustedIdp(name).getIdp();
            idp1.setAuthenticationServiceURL("localhost");
            try {
                idp1 = tm.addTrustedIdP(idp1);
                fail("Should not be able to add a Trusted IdP without a display name.");
            } catch (InvalidTrustedIdPFault f) {
                String str = gov.nih.nci.cagrid.common.Utils.getExceptionMessage(f);
                if (str.indexOf("Invalid Authentication Service URL specified!!!") == -1) {
                    fail("Unexpected error message received when trying to validate that a trusted idp could not be added with an invalid authentication service URL.");
                }
            }

            idp1.setAuthenticationServiceURL("https://localhost");
            idp1 = tm.addTrustedIdP(idp1);
            assertEquals(1, tm.getTrustedIdPs().length);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testNameUpdate() {
        try {
            String name = "Test IdP";
            TrustedIdP idp = getTrustedIdp(name).getIdp();
            idp = tm.addTrustedIdP(idp);
            idp.setName("Updated " + name);
            try {
                tm.updateIdP(idp);
                assertTrue(false);
            } catch (InvalidTrustedIdPFault f) {
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    public void testSingleIdPNullAuthenticationMethods() {
        try {
            // We want to run this multiple times
            assertNotNull(tm);
            assertEquals(0, tm.getTrustedIdPs().length);
            String name = "Test IdP";
            IdPContainer cont = getTrustedIdp(name);
            cont.getIdp().setAuthenticationMethod(null);
            TrustedIdP idp = cont.getIdp();
            idp = tm.addTrustedIdP(idp);
            assertEquals(1, tm.getTrustedIdPs().length);
            assertEquals(null, tm.getAuthenticationMethods(idp.getId()));
            TrustedIdP[] idps = tm.getTrustedIdPs();
            assertEquals(idp, idps[0]);
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            TrustedIdP temp = tm.getTrustedIdPByName(idp.getName());
            assertEquals(idp, temp);
            TrustedIdP temp2 = tm.getTrustedIdPById(idp.getId());
            assertEquals(idp, temp2);
            TrustedIdP temp3 = tm.getTrustedIdP(cont.getSAMLAssertion());
            assertEquals(idp, temp3);
            StringReader reader = new StringReader(idp.getIdPCertificate());
            X509Certificate cert = CertUtil.loadCertificate(reader);
            assertTrue(tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertEquals(idp, tm.getTrustedIdPByDN(cert.getSubjectDN().toString()));
            tm.removeTrustedIdP(idp.getId());
            assertEquals(0, tm.getTrustedIdPs().length);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public void testSingleIdPNoAuthenticationMethods() {
        try {
            // We want to run this multiple times
            assertNotNull(tm);
            assertEquals(0, tm.getTrustedIdPs().length);
            String name = "Test IdP";
            IdPContainer cont = getTrustedIdp(name);
            SAMLAuthenticationMethod[] m = new SAMLAuthenticationMethod[0];
            cont.getIdp().setAuthenticationMethod(m);
            TrustedIdP idp = cont.getIdp();
            idp = tm.addTrustedIdP(idp);
            idp.setAuthenticationMethod(null);
            assertEquals(1, tm.getTrustedIdPs().length);
            assertEquals(null, tm.getAuthenticationMethods(idp.getId()));
            TrustedIdP[] idps = tm.getTrustedIdPs();
            assertEquals(idp, idps[0]);
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            TrustedIdP temp = tm.getTrustedIdPByName(idp.getName());
            assertEquals(idp, temp);
            TrustedIdP temp2 = tm.getTrustedIdPById(idp.getId());
            assertEquals(idp, temp2);
            TrustedIdP temp3 = tm.getTrustedIdP(cont.getSAMLAssertion());
            assertEquals(idp, temp3);
            StringReader reader = new StringReader(idp.getIdPCertificate());
            X509Certificate cert = CertUtil.loadCertificate(reader);
            assertTrue(tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertEquals(idp, tm.getTrustedIdPByDN(cert.getSubjectDN().toString()));
            tm.removeTrustedIdP(idp.getId());
            assertEquals(0, tm.getTrustedIdPs().length);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public void testSingleIdPUpdateNullAuthenticationMethods() {
        try {
            // We want to run this multiple times
            assertNotNull(tm);
            assertEquals(0, tm.getTrustedIdPs().length);
            String name = "Test IdP";
            IdPContainer cont = getTrustedIdp(name);
            TrustedIdP idp = cont.getIdp();
            SAMLAuthenticationMethod[] auth = new SAMLAuthenticationMethod[1];
            auth[0] = SAMLAuthenticationMethod.value1;
            cont.getIdp().setAuthenticationMethod(auth);
            idp = tm.addTrustedIdP(idp);
            assertEquals(1, tm.getTrustedIdPs().length);
            assertEquals(idp.getAuthenticationMethod().length, tm.getAuthenticationMethods(idp.getId()).length);
            TrustedIdP[] idps = tm.getTrustedIdPs();
            assertEquals(idp, idps[0]);
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            TrustedIdP temp = tm.getTrustedIdPByName(idp.getName());
            assertEquals(idp, temp);
            TrustedIdP temp2 = tm.getTrustedIdPById(idp.getId());
            assertEquals(idp, temp2);
            TrustedIdP temp3 = tm.getTrustedIdP(cont.getSAMLAssertion());
            assertEquals(idp, temp3);
            StringReader reader = new StringReader(idp.getIdPCertificate());
            X509Certificate cert = CertUtil.loadCertificate(reader);
            assertTrue(tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertEquals(idp, tm.getTrustedIdPByDN(cert.getSubjectDN().toString()));

            // Update, removing all authentication methods
            idp.setAuthenticationMethod(null);
            tm.updateIdP(idp);
            assertEquals(1, tm.getTrustedIdPs().length);
            assertEquals(null, tm.getAuthenticationMethods(idp.getId()));
            idps = null;
            idps = tm.getTrustedIdPs();
            assertEquals(idp, idps[0]);
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            temp = null;
            temp = tm.getTrustedIdPByName(idp.getName());
            assertEquals(idp, temp);
            temp2 = null;
            temp2 = tm.getTrustedIdPById(idp.getId());
            assertEquals(idp, temp2);
            temp3 = null;
            temp3 = tm.getTrustedIdP(cont.getSAMLAssertion());
            assertEquals(idp, temp3);
            assertTrue(tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertEquals(idp, tm.getTrustedIdPByDN(cert.getSubjectDN().toString()));
            tm.removeTrustedIdP(idp.getId());
            assertEquals(0, tm.getTrustedIdPs().length);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    private void checkAuthenticationMethodLength(TrustedIdP idp) throws Exception {
        if ((idp.getAuthenticationMethod() == null) || (idp.getAuthenticationMethod().length == 0)) {
            assertEquals(null, tm.getAuthenticationMethods(idp.getId()));
        } else {
            assertEquals(idp.getAuthenticationMethod().length, tm.getAuthenticationMethods(idp.getId()).length);
        }
    }


    public void testSingleIdPAllAuthenticationMethods() {
        try {
            assertNotNull(tm);
            assertEquals(0, tm.getTrustedIdPs().length);
            String name = "Test IdP";
            IdPContainer cont = getTrustedIdp(name);
            TrustedIdP idp = cont.getIdp();
            idp = tm.addTrustedIdP(idp);
            assertEquals(1, tm.getTrustedIdPs().length);
            checkAuthenticationMethodLength(idp);
            TrustedIdP[] idps = tm.getTrustedIdPs();
            assertEquals(idp, idps[0]);
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            TrustedIdP temp = tm.getTrustedIdPByName(idp.getName());
            assertEquals(idp, temp);
            TrustedIdP temp2 = tm.getTrustedIdPById(idp.getId());
            assertEquals(idp, temp2);
            TrustedIdP temp3 = tm.getTrustedIdP(cont.getSAMLAssertion());
            assertEquals(idp, temp3);

            // Test for bad assertion
            IdPContainer bad = getTrustedIdp("BAD ASSERTION");
            try {
                tm.getTrustedIdP(bad.getSAMLAssertion());
                assertTrue(false);
            } catch (InvalidAssertionFault f) {

            }

            StringReader reader = new StringReader(idp.getIdPCertificate());
            X509Certificate cert = CertUtil.loadCertificate(reader);
            assertTrue(tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertEquals(idp, tm.getTrustedIdPByDN(cert.getSubjectDN().toString()));

            // Test Updates

            IdPContainer updatedCont = getTrustedIdp(name, true);
            TrustedIdP updateIdp = updatedCont.getIdp();
            updateIdp.setId(idp.getId());
            updateIdp.setStatus(TrustedIdPStatus.Suspended);
            tm.updateIdP(updateIdp);

            TrustedIdP[] ulist = tm.getTrustedIdPs();
            assertEquals(1, ulist.length);
            assertEquals(updateIdp, ulist[0]);
            assertEquals(TrustedIdPStatus.Suspended, ulist[0].getStatus());
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            TrustedIdP utemp = tm.getTrustedIdPByName(updateIdp.getName());
            assertEquals(updateIdp, utemp);
            TrustedIdP utemp2 = tm.getTrustedIdPById(updateIdp.getId());
            assertEquals(updateIdp, utemp2);
            TrustedIdP utemp3 = tm.getTrustedIdP(updatedCont.getSAMLAssertion());
            assertEquals(updateIdp, utemp3);

            StringReader ureader = new StringReader(updateIdp.getIdPCertificate());
            X509Certificate ucert = CertUtil.loadCertificate(ureader);
            assertTrue(!tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertTrue(tm.determineTrustedIdPExistsByDN(ucert.getSubjectDN().toString()));
            assertEquals(updateIdp, tm.getTrustedIdPByDN(ucert.getSubjectDN().toString()));

            tm.removeTrustedIdP(idp.getId());
            assertEquals(0, tm.getTrustedIdPs().length);
            assertEquals(null, tm.getAuthenticationMethods(idp.getId()));

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public void testSingleIdPOneAuthenticationMethods() {
        try {
            assertNotNull(tm);
            assertEquals(0, tm.getTrustedIdPs().length);
            String name = "Test IdP";
            IdPContainer cont = getTrustedIdp(name);
            TrustedIdP idp = cont.getIdp();
            SAMLAuthenticationMethod[] m = new SAMLAuthenticationMethod[1];
            m[0] = getAuthenticationMethods()[0];
            idp.setAuthenticationMethod(m);
            idp = tm.addTrustedIdP(idp);
            assertEquals(1, tm.getTrustedIdPs().length);
            checkAuthenticationMethodLength(idp);
            TrustedIdP[] idps = tm.getTrustedIdPs();
            assertEquals(idp, idps[0]);
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            TrustedIdP temp = tm.getTrustedIdPByName(idp.getName());
            assertEquals(idp, temp);
            TrustedIdP temp2 = tm.getTrustedIdPById(idp.getId());
            assertEquals(idp, temp2);
            TrustedIdP temp3 = tm.getTrustedIdP(cont.getSAMLAssertion());
            assertEquals(idp, temp3);

            // Test for bad assertion
            IdPContainer bad = getTrustedIdp("BAD ASSERTION");
            try {
                tm.getTrustedIdP(bad.getSAMLAssertion());
                assertTrue(false);
            } catch (InvalidAssertionFault f) {

            }

            StringReader reader = new StringReader(idp.getIdPCertificate());
            X509Certificate cert = CertUtil.loadCertificate(reader);
            assertTrue(tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertEquals(idp, tm.getTrustedIdPByDN(cert.getSubjectDN().toString()));

            // Test Updates
            String updateDisplayName = "Updated Display Name";
            String updateServiceURL = "https://localhost.update";
            String updatedServiceIdentity = "Updated Identity";
            IdPContainer updatedCont = getTrustedIdp(name, true);
            TrustedIdP updateIdp = updatedCont.getIdp();
            updateIdp.setId(idp.getId());
            updateIdp.setDisplayName(updateDisplayName);
            updateIdp.setAuthenticationServiceURL(updateServiceURL);
            updateIdp.setAuthenticationServiceIdentity(updatedServiceIdentity);
            updateIdp.setStatus(TrustedIdPStatus.Suspended);
            tm.updateIdP(updateIdp);

            TrustedIdP[] ulist = tm.getTrustedIdPs();
            assertEquals(1, ulist.length);
            assertEquals(updateIdp, ulist[0]);
            assertEquals(TrustedIdPStatus.Suspended, ulist[0].getStatus());
            assertTrue(tm.determineTrustedIdPExistsByName(name));
            TrustedIdP utemp = tm.getTrustedIdPByName(updateIdp.getName());
            assertEquals(updateIdp, utemp);
            TrustedIdP utemp2 = tm.getTrustedIdPById(updateIdp.getId());
            assertEquals(updateIdp, utemp2);
            TrustedIdP utemp3 = tm.getTrustedIdP(updatedCont.getSAMLAssertion());
            assertEquals(updateIdp, utemp3);

            StringReader ureader = new StringReader(updateIdp.getIdPCertificate());
            X509Certificate ucert = CertUtil.loadCertificate(ureader);
            assertTrue(!tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
            assertTrue(tm.determineTrustedIdPExistsByDN(ucert.getSubjectDN().toString()));
            assertEquals(updateIdp, tm.getTrustedIdPByDN(ucert.getSubjectDN().toString()));

            tm.removeTrustedIdP(idp.getId());
            assertEquals(0, tm.getTrustedIdPs().length);
            assertEquals(null, tm.getAuthenticationMethods(idp.getId()));

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public void testMultipleIdPs() {
        try {
            // We want to run this multiple times
            int times = 3;
            String baseName = "Test IdP";
            for (int i = 0; i < times; i++) {
                assertNotNull(tm);
                assertEquals(i, tm.getTrustedIdPs().length);
                String name = baseName + " " + i;
                IdPContainer cont = getTrustedIdp(name);
                TrustedIdP idp = cont.getIdp();
                idp = tm.addTrustedIdP(idp);
                assertEquals((i + 1), tm.getTrustedIdPs().length);
                checkAuthenticationMethodLength(idp);
                assertTrue(tm.determineTrustedIdPExistsByName(name));
                TrustedIdP temp = tm.getTrustedIdPByName(idp.getName());
                assertEquals(idp, temp);
                TrustedIdP temp2 = tm.getTrustedIdPById(idp.getId());
                assertEquals(idp, temp2);
                TrustedIdP temp3 = tm.getTrustedIdP(cont.getSAMLAssertion());
                assertEquals(idp, temp3);

                // Test for bad assertion
                IdPContainer bad = getTrustedIdp("BAD ASSERTION");
                try {
                    tm.getTrustedIdP(bad.getSAMLAssertion());
                    assertTrue(false);
                } catch (InvalidAssertionFault f) {

                }

                StringReader reader = new StringReader(idp.getIdPCertificate());
                X509Certificate cert = CertUtil.loadCertificate(reader);
                assertTrue(tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
                assertEquals(idp, tm.getTrustedIdPByDN(cert.getSubjectDN().toString()));

                // Test Updates
                IdPContainer updateCont = getTrustedIdp(name, true);
                TrustedIdP updateIdp = updateCont.getIdp();
                updateIdp.setId(idp.getId());
                updateIdp.setStatus(TrustedIdPStatus.Suspended);
                tm.updateIdP(updateIdp);

                assertEquals((i + 1), tm.getTrustedIdPs().length);
                assertTrue(tm.determineTrustedIdPExistsByName(name));
                TrustedIdP utemp = tm.getTrustedIdPByName(updateIdp.getName());
                assertEquals(updateIdp, utemp);
                assertEquals(TrustedIdPStatus.Suspended, utemp.getStatus());
                TrustedIdP utemp2 = tm.getTrustedIdPById(updateIdp.getId());
                assertEquals(updateIdp, utemp2);
                TrustedIdP utemp3 = tm.getTrustedIdP(updateCont.getSAMLAssertion());
                assertEquals(updateIdp, utemp3);

                StringReader ureader = new StringReader(updateIdp.getIdPCertificate());
                X509Certificate ucert = CertUtil.loadCertificate(ureader);
                assertTrue(!tm.determineTrustedIdPExistsByDN(cert.getSubjectDN().toString()));
                assertTrue(tm.determineTrustedIdPExistsByDN(ucert.getSubjectDN().toString()));
                assertEquals(updateIdp, tm.getTrustedIdPByDN(ucert.getSubjectDN().toString()));

            }

            TrustedIdP[] idps = tm.getTrustedIdPs();
            assertEquals(times, idps.length);
            int count = times;
            for (int i = 0; i < idps.length; i++) {
                count = count - 1;
                tm.removeTrustedIdP(idps[i].getId());
                assertEquals(count, tm.getTrustedIdPs().length);
            }

            assertEquals(0, tm.getTrustedIdPs().length);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public void testUpdateAuthMethodsOnly() {
        try {
            TrustedIdP idp = getTrustedIdp("Test IdP").getIdp();
            int count = getAuthenticationMethods().length / 2;
            SAMLAuthenticationMethod[] authMethods = new SAMLAuthenticationMethod[count];
            for (int i = 0; i < count; i++) {
                authMethods[i] = getAuthenticationMethods()[i];
            }
            idp.setAuthenticationMethod(authMethods);
            idp = tm.addTrustedIdP(idp);
            assertEquals(1, tm.getTrustedIdPs().length);

            authMethods = new SAMLAuthenticationMethod[count - 1];
            for (int i = 0; i < (count - 1); i++) {
                authMethods[i] = getAuthenticationMethods()[i];
            }
            idp.setAuthenticationMethod(authMethods);
            tm.updateIdP(idp);
            assertEquals(idp, tm.getTrustedIdPById(idp.getId()));

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

    }


    private IdPContainer getTrustedIdp(String name) throws Exception {
        return getTrustedIdp(name, false);
    }


    private IdPContainer getTrustedIdp(String name, boolean nonStandartCert) throws Exception {
        TrustedIdP idp = new TrustedIdP();
        idp.setName(name);
        idp.setDisplayName(name);
        idp.setStatus(TrustedIdPStatus.Active);
        idp.setUserPolicyClass(AutoApprovalPolicy.class.getName());
        idp.setAuthenticationMethod(getAuthenticationMethods());
        idp.setAuthenticationServiceURL("https://localhost");
        idp.setAuthenticationServiceIdentity("/O=caGrid/OU=Testing/CN=Admin");
        SAMLAttributeDescriptor uid = new SAMLAttributeDescriptor();
        uid.setNamespaceURI(SAMLConstants.UID_ATTRIBUTE_NAMESPACE);
        uid.setName(SAMLConstants.UID_ATTRIBUTE);
        idp.setUserIdAttributeDescriptor(uid);

        SAMLAttributeDescriptor firstName = new SAMLAttributeDescriptor();
        firstName.setNamespaceURI(SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE);
        firstName.setName(SAMLConstants.FIRST_NAME_ATTRIBUTE);
        idp.setFirstNameAttributeDescriptor(firstName);

        SAMLAttributeDescriptor lastName = new SAMLAttributeDescriptor();
        lastName.setNamespaceURI(SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE);
        lastName.setName(SAMLConstants.LAST_NAME_ATTRIBUTE);
        idp.setLastNameAttributeDescriptor(lastName);

        SAMLAttributeDescriptor email = new SAMLAttributeDescriptor();
        email.setNamespaceURI(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE);
        email.setName(SAMLConstants.EMAIL_ATTRIBUTE);
        idp.setEmailAttributeDescriptor(email);
        String id = null;
        String subject = null;
        if (nonStandartCert) {
            id = "Non Standard" + name;
        } else {
            id = name;
        }

        subject = Utils.CA_SUBJECT_PREFIX + ",CN=" + id;
        Credential cred = ca.createIdentityCertificate(id);
        X509Certificate cert = cred.getCertificate();
        assertNotNull(cert);
        assertEquals(cert.getSubjectDN().getName(), subject);
        idp.setIdPCertificate(CertUtil.writeCertificate(cert));

        GregorianCalendar cal2 = new GregorianCalendar();
        Date start2 = cal2.getTime();
        cal2.add(Calendar.MINUTE, 2);
        Date end2 = cal2.getTime();
        String issuer = cert.getSubjectDN().toString();
        String federation = cert.getSubjectDN().toString();
        String ipAddress = null;
        String subjectDNS = null;
        SAMLNameIdentifier ni = new SAMLNameIdentifier(name, federation,
            "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
        SAMLSubject sub = new SAMLSubject(ni, null, null, null);
        SAMLAuthenticationStatement auth = new SAMLAuthenticationStatement(sub,
            "urn:oasis:names:tc:SAML:1.0:am:password", new Date(), ipAddress, subjectDNS, null);

        List l = new ArrayList();
        l.add(auth);
        SAMLAssertion saml = new SAMLAssertion(issuer, start2, end2, null, null, l);
        List a = new ArrayList();
        a.add(cert);
        saml.sign(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, cred.getPrivateKey(), a);
        return new IdPContainer(idp, cert, saml);
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            org.apache.xml.security.Init.init();
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            IdentityFederationProperties conf = Utils.getIdentityFederationProperties();
            conf.setMinIdPNameLength(MIN_NAME_LENGTH);
            conf.setMaxIdPNameLength(MAX_NAME_LENGTH);
            ca = new CA(Utils.getCASubject());
            tm = new TrustedIdPManager(conf, db);
            tm.clearDatabase();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    protected void tearDown() throws Exception {
        super.setUp();
        try {
            assertEquals(0, db.getUsedConnectionCount());
            tm.clearDatabase();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public SAMLAuthenticationMethod[] getAuthenticationMethods() {
        if (methods == null) {
            List list = new ArrayList();

            Field[] fields = SAMLAuthenticationMethod.class.getFields();

            for (int i = 0; i < fields.length; i++) {
                if (SAMLAuthenticationMethod.class.isAssignableFrom(fields[i].getType())) {
                    try {
                        Object o = fields[i].get(null);
                        list.add(o);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            methods = new SAMLAuthenticationMethod[list.size()];
            for (int i = 0; i < list.size(); i++) {
                methods[i] = (SAMLAuthenticationMethod) list.get(i);
            }
        }

        return methods;
    }


    public class IdPContainer {

        TrustedIdP idp;

        X509Certificate cert;

        SAMLAssertion saml;


        public IdPContainer(TrustedIdP idp, X509Certificate cert, SAMLAssertion saml) {
            this.idp = idp;
            this.cert = cert;
            this.saml = saml;
        }


        public X509Certificate getCert() {
            return cert;
        }


        public TrustedIdP getIdp() {
            return idp;
        }


        public SAMLAssertion getSAMLAssertion() {
            return saml;
        }

    }

}
