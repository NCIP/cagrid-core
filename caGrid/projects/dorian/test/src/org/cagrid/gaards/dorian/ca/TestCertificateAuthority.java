package org.cagrid.gaards.dorian.ca;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

import org.bouncycastle.asn1.x509.X509Name;
import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.tools.database.Database;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestCertificateAuthority extends TestCase {
    private static final String TABLE = "test_dorian_ca";

    private static final String SUBJECT_PREFIX = "O=Testing Organization,OU=ABC,OU=XYZ,CN=";

    private Database db;

    private int MAX_COUNT = 8;

    private int TIME_MULTIPLIER = 2;


    public void testWrappingCertificateAuthority() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoCreateAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            if (ca instanceof WrappingCertificateAuthority) {
                ca.clearCertificateAuthority();
                createAndStoreCA(ca);
                WrappingCertificateAuthority wca = (WrappingCertificateAuthority) ca;
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                WrappedKey wk = wca.wrap(pair.getPrivate());
                try {
                    KeyUtil.loadPrivateKey(new ByteArrayInputStream(wk.getWrappedKeyData()), null);
                    fail("Should not be able to unwrap key.");
                } catch (Exception e) {

                }
                PrivateKey key = wca.unwrap(wk);
                assertEquals(pair.getPrivate(), key);
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private CertificateAuthority getCertificateAuthority(CertificateAuthorityProperties conf) throws Exception {
        Class type = Utils.getCA().getClass();
        CertificateAuthorityProperties props = Utils.getCAProperties();
        props.setAutoCreateCA(conf.isAutoCreateCAEnabled());
        props.setAutoRenewCA(conf.isAutoRenewCAEnabled());
        props.setCreationPolicy(conf.getCreationPolicy());
        props.setRenewalLifetime(conf.getRenewalLifetime());

        CertificateAuthority ca = null;
        if (type.equals(DBCertificateAuthority.class)) {
            ca = new DBCertificateAuthority(db, props);
        } else if (type.equals(DBCertificateAuthority.class)) {
            ca = new EracomCertificateAuthority((EracomCertificateAuthorityProperties) props);
        } else {
            throw new Exception("The certificate authority " + type + " is not a valid ca for the test "
                + TestCertificateAuthority.class.getName() + ".");
        }
        ca.clearCertificateAuthority();
        return ca;

    }


    private String identityToSubject(String identity) {
        String s = identity.substring(1);
        return s.replace('/', ',');
    }


    public void testRequestCertificateBadSubject() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoCreateAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            createAndStoreCA(ca);
            GregorianCalendar cal = new GregorianCalendar();
            Date start = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 5);
            Date end = cal.getTime();
            submitCertificateRequest(ca, "O=OSU,OU=BMI,OU=MSCL,CN=foo", start, end);
            assertTrue(false);
        } catch (CertificateAuthorityFault f) {
            if (f.getFaultString().indexOf("Invalid certificate subject") == -1) {
                FaultUtil.printFault(f);
                assertTrue(false);
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testCreateCertificate() {

        CertificateAuthority ca = null;
        try {
            int hours = 12;
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoCreateAutoRenewalLong();
            String user = SUBJECT_PREFIX + "User";
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            Calendar c = new GregorianCalendar();
            Date start = c.getTime();
            c.add(Calendar.HOUR, hours);

            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            assertNotNull(pair);
            PublicKey proxyPublicKey = pair.getPublic();
            assertNotNull(proxyPublicKey);

            X509Certificate cert = ca.signCertificate(user, pair.getPublic(), start, c.getTime());
            assertNotNull(cert);
            GlobusCredential cred = new GlobusCredential(pair.getPrivate(), new X509Certificate[]{cert});
            assertNotNull(cred);
            long timeLeft = cred.getTimeLeft();
            assertEquals(user, cert.getSubjectDN().getName());
            assertEquals(user, identityToSubject(cred.getIdentity()));
            assertEquals(cert.getIssuerDN(), ca.getCACertificate().getSubjectDN());

            long okMax = hours * 60 * 60;
            // Allow some Buffer
            long okMin = okMax - 300;

            // must have less time left than the maximum
            assertTrue(timeLeft <= okMax);
            // must have more time left than the minimum
            assertTrue(timeLeft >= okMin);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    public void testCertificateTimeToGreat() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoCreateAutoRenewalLong();
            String user = SUBJECT_PREFIX + "User";
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            Calendar c = new GregorianCalendar();
            Date start = c.getTime();
            c.add(Calendar.YEAR, 10);
            Date expiration = c.getTime();
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            try {
                ca.signCertificate(user, pair.getPublic(), start, expiration);
                fail("Should not be able to sign a certificate that expires after the CA.");
            } catch (CertificateAuthorityFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testNoCACredentials() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfNoAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            try {
                ca.getCACertificate();
                assertTrue(false);
            } catch (NoCACredentialsFault f) {

            }
            assertEquals(0, db.getUsedConnectionCount());
            try {
                GregorianCalendar cal = new GregorianCalendar();
                Date start = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 5);
                Date end = cal.getTime();
                submitCertificateRequest(ca, SUBJECT_PREFIX, start, end);
                assertEquals(0, db.getUsedConnectionCount());
                assertTrue(false);
            } catch (NoCACredentialsFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testNoCACredentialsWithAutoRenew() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();

            try {
                ca.getCACertificate();
                assertTrue(false);
            } catch (NoCACredentialsFault f) {

            }
            try {
                GregorianCalendar cal = new GregorianCalendar();
                Date start = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 5);
                Date end = cal.getTime();
                submitCertificateRequest(ca, SUBJECT_PREFIX, start, end);
                assertTrue(false);
            } catch (NoCACredentialsFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testSetCACredentials() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            // ca.destroyTable();
            createAndStoreCA(ca);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testAutoCreateCA() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoCreateAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            assertFalse(ca.hasCACredentials());
            X509Certificate cert = ca.getCACertificate();
            assertTrue(ca.hasCACredentials());
            assertEquals(conf.getCreationPolicy().getSubject(), cert.getSubjectDN().getName());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestCertificate() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            createAndStoreCA(ca);
            GregorianCalendar cal = new GregorianCalendar();
            Date start = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 5);
            Date end = cal.getTime();
            submitCertificateRequest(ca, SUBJECT_PREFIX, start, end);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestCertificateBadDate() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            createAndStoreCA(ca);
            GregorianCalendar cal = new GregorianCalendar();
            Date start = cal.getTime();
            cal.add(Calendar.YEAR, 5);
            Date end = cal.getTime();
            submitCertificateRequest(ca, SUBJECT_PREFIX, start, end);
            assertTrue(false);
        } catch (CertificateAuthorityFault f) {
            if (f.getFaultString().indexOf("Certificate expiration date is after the CA certificates expiration date") == -1) {

                FaultUtil.printFault(f);
                fail(f.getMessage());
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testExpiredCACredentialsWithRenewal() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = this.getDorianCAConfAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            // give a chance for others to run right before we enter timing
            // sensitive code
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Thread.currentThread().yield();
            X509Certificate origRoot = null;

            boolean completed = false;
            int count = 0;
            int seconds = 5000;
            while ((!completed) && (count < MAX_COUNT)) {
                try {
                    origRoot = createAndStoreCAShort(ca, seconds);
                    completed = true;
                } catch (Exception f) {
                    count = count + 1;
                    seconds = seconds * TIME_MULTIPLIER;
                }
            }
            Thread.sleep(seconds + 500);

            GregorianCalendar cal = new GregorianCalendar();
            Date start = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 5);
            Date end = cal.getTime();
            assertNotSame(origRoot, ca.getCACertificate());
            submitCertificateRequest(ca, SUBJECT_PREFIX, start, end);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testExpiredCACredentialsNoRenewal() {

        CertificateAuthority ca = null;
        try {
            CertificateAuthorityProperties conf = getDorianCAConfNoAutoRenewalLong();
            ca = getCertificateAuthority(conf);
            ca.clearCertificateAuthority();
            // ca.destroyTable();
            // give a chance for others to run right before we enter timing
            // sensitive code
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Thread.currentThread().yield();
            boolean completed = false;
            int count = 0;
            int seconds = 5000;
            while ((!completed) && (count < MAX_COUNT)) {
                try {
                    createAndStoreCAShort(ca, seconds);
                    completed = true;
                } catch (Exception f) {
                    count = count + 1;
                    seconds = seconds * TIME_MULTIPLIER;
                }
            }
            Thread.sleep(seconds + 500);
            try {
                ca.getCACertificate();
                assertTrue(false);
            } catch (NoCACredentialsFault f) {

            }

            try {
                GregorianCalendar cal = new GregorianCalendar();
                Date start = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 5);
                Date end = cal.getTime();
                submitCertificateRequest(ca, SUBJECT_PREFIX, start, end);
            } catch (NoCACredentialsFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                ca.clearCertificateAuthority();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private CertificateAuthorityProperties getDorianCAConfAutoRenewalLong() throws DorianInternalFault {
        Lifetime lifetime = new Lifetime();
        lifetime.setYears(5);
        lifetime.setMonths(0);
        lifetime.setDays(0);
        CertificateAuthorityCreationPolicy creation = new CertificateAuthorityCreationPolicy(SUBJECT_PREFIX
            + "Temp Certificate Authority", 2048, lifetime);
        CertificateAuthorityProperties conf = new CertificateAuthorityProperties("password", null, 1024, false, null,
            true, lifetime);
        return conf;
    }


    private CertificateAuthorityProperties getDorianCAConfAutoCreateAutoRenewalLong() throws DorianInternalFault {
        Lifetime lifetime = new Lifetime();
        lifetime.setYears(5);
        lifetime.setMonths(0);
        lifetime.setDays(0);
        CertificateAuthorityCreationPolicy creation = new CertificateAuthorityCreationPolicy(SUBJECT_PREFIX
            + "Temp Certificate Authority", 2048, lifetime);
        CertificateAuthorityProperties conf = new CertificateAuthorityProperties("password", null, 1024, true,
            creation, true, lifetime);
        return conf;
    }


    private CertificateAuthorityProperties getDorianCAConfNoAutoRenewalLong() throws DorianInternalFault {
        CertificateAuthorityProperties conf = new CertificateAuthorityProperties("password", 1024);
        return conf;
    }


    private void createAndStoreCA(CertificateAuthority ca) throws Exception {
        KeyPair rootPair = KeyUtil.generateRSAKeyPair1024(ca.getCACredentialsProvider());
        assertNotNull(rootPair);
        String rootSub = SUBJECT_PREFIX + "Temp Certificate Authority";
        X509Name rootSubject = new X509Name(rootSub);
        GregorianCalendar cal = new GregorianCalendar();
        Date start = cal.getTime();
        cal.add(Calendar.YEAR, 1);
        Date end = cal.getTime();
        X509Certificate root = CertUtil.generateCACertificate(ca.getCACredentialsProvider(), rootSubject, start, end,
            rootPair, ca.getSignatureAlgorithm());
        assertNotNull(root);
        ca.setCACredentials(root, rootPair.getPrivate(), null);
        X509Certificate r = ca.getCACertificate();
        assertNotNull(r);
        assertEquals(r, root);
    }


    private X509Certificate createAndStoreCAShort(CertificateAuthority ca, int seconds) throws Exception {
        KeyPair rootPair = KeyUtil.generateRSAKeyPair2048(ca.getCACredentialsProvider());
        assertNotNull(rootPair);
        String rootSub = SUBJECT_PREFIX + "Temp Certificate Authority";
        X509Name rootSubject = new X509Name(rootSub);
        GregorianCalendar cal = new GregorianCalendar();
        Date start = cal.getTime();
        cal.add(Calendar.SECOND, (seconds / 1000));
        Date end = cal.getTime();
        X509Certificate root = CertUtil.generateCACertificate(ca.getCACredentialsProvider(), rootSubject, start, end,
            rootPair, ca.getSignatureAlgorithm());
        assertNotNull(root);
        ca.setCACredentials(root, rootPair.getPrivate(), null);
        X509Certificate r = ca.getCACertificate();
        assertNotNull(r);
        if (!r.equals(root)) {
            throw new Exception("The CA certificate obtained was not expected");
        }
        return r;
    }


    private void submitCertificateRequest(CertificateAuthority ca, String prefix, Date start, Date end)
        throws Exception {
        String cn = "User";
        String subject = prefix + cn;
        KeyPair pair = KeyUtil.generateRSAKeyPair1024();
        X509Certificate cert = ca.signCertificate(subject, pair.getPublic(), start, end);
        assertNotNull(cert);
        assertEquals(cert.getSubjectDN().getName(), subject);
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            CredentialsManager.CREDENTIALS_TABLE = TABLE;
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    protected void tearDown() throws Exception {
        super.setUp();
        try {
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }

}
