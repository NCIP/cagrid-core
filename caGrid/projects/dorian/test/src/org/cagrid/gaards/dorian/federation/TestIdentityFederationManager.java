package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.cagrid.opensaml.SAMLAttribute;
import gov.nih.nci.cagrid.opensaml.SAMLAttributeStatement;
import gov.nih.nci.cagrid.opensaml.SAMLAuthenticationStatement;
import gov.nih.nci.cagrid.opensaml.SAMLNameIdentifier;
import gov.nih.nci.cagrid.opensaml.SAMLSubject;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.xml.security.signature.XMLSignature;
import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.common.AuditConstants;
import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.service.PropertyManager;
import org.cagrid.gaards.dorian.stubs.types.DorianInternalFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidAssertionFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.PermissionDeniedFault;
import org.cagrid.gaards.dorian.stubs.types.UserPolicyFault;
import org.cagrid.gaards.dorian.test.CA;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.Credential;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.gaards.saml.encoding.SAMLConstants;
import org.cagrid.tools.database.Database;
import org.cagrid.tools.events.EventManager;
import org.globus.gsi.GlobusCredential;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestIdentityFederationManager extends TestCase {

    private static final int MIN_NAME_LENGTH = 4;

    private static final int MAX_NAME_LENGTH = 50;

    private static final int SHORT_PROXY_VALID = 10;

    private static final int SHORT_CREDENTIALS_VALID = 35;

    public final static String INITIAL_ADMIN = "admin";

    private Database db;

    private CertificateAuthority ca;

    private CA memoryCA;

    private PropertyManager props;

    private EventManager eventManager;


    public void testRequestHostCertificateManualApproval() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String host = "localhost";
            HostCertificateRequest req = getHostCertificateRequest(host);
            HostCertificateRecord record = ifs.requestHostCertificate(usr.getGridId(), req);
            assertEquals(HostCertificateStatus.Pending, record.getStatus());
            assertEquals(null, record.getCertificate());
            String hostId = String.valueOf(record.getId());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                FederationAudit.HostCertificateRequested);

            String subject = org.cagrid.gaards.dorian.service.util.Utils.getHostCertificateSubject(ca
                .getCACertificate(), host);
            record = ifs.approveHostCertificate(adminGridId, record.getId());
            assertEquals(HostCertificateStatus.Active, record.getStatus());;
            assertEquals(subject, record.getSubject());
            assertEquals(subject, CertUtil.loadCertificate(record.getCertificate().getCertificateAsString())
                .getSubjectDN().getName());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, adminGridId,
                FederationAudit.HostCertificateApproved);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestHostCertificateAutoApproval() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(true);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String host = "localhost";
            HostCertificateRequest req = getHostCertificateRequest(host);
            HostCertificateRecord record = ifs.requestHostCertificate(usr.getGridId(), req);
            String subject = org.cagrid.gaards.dorian.service.util.Utils.getHostCertificateSubject(ca
                .getCACertificate(), host);
            assertEquals(HostCertificateStatus.Active, record.getStatus());;
            assertEquals(subject, record.getSubject());
            assertEquals(subject, CertUtil.loadCertificate(record.getCertificate().getCertificateAsString())
                .getSubjectDN().getName());
            String hostId = String.valueOf(record.getId());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                FederationAudit.HostCertificateRequested);
            performAndValidateSingleAudit(ifs, adminGridId, hostId, AuditConstants.SYSTEM_ID,
                FederationAudit.HostCertificateApproved);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestHostCertificateInvalidUser() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            createUser(ifs, adminGridId, idp, "user");
            String host = "localhost";
            HostCertificateRequest req = getHostCertificateRequest(host);
            try {
                ifs.requestHostCertificate("bad user", req);
                fail("Should have failed.");
            } catch (PermissionDeniedFault f) {

            }
            performAndValidateSingleAudit(ifs, adminGridId, "bad user", AuditConstants.SYSTEM_ID,
                FederationAudit.AccessDenied);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void testApproveHostCertificateInvalidUser() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String host = "localhost";
            HostCertificateRequest req = getHostCertificateRequest(host);
            HostCertificateRecord record = ifs.requestHostCertificate(usr.getGridId(), req);
            assertEquals(HostCertificateStatus.Pending, record.getStatus());
            assertEquals(null, record.getCertificate());

            String hostId = String.valueOf(record.getId());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                FederationAudit.HostCertificateRequested);

            try {
                ifs.approveHostCertificate("bad subject", record.getId());
            } catch (PermissionDeniedFault f) {

            }

            performAndValidateSingleAudit(ifs, adminGridId, "bad subject", AuditConstants.SYSTEM_ID,
                FederationAudit.AccessDenied);
            assertEquals(0, ifs.performAudit(adminGridId,
                getHostCertificatedApprovedAuditingFilter(hostId, usr.getGridId())).size());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testFindHostCertificates() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(true);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String subjectPrefix = org.cagrid.gaards.dorian.service.util.Utils.getHostCertificateSubjectPrefix(ca
                .getCACertificate());
            String hostPrefix = "localhost";
            int total = 3;

            for (int i = 0; i < total; i++) {
                HostCertificateRequest req = getHostCertificateRequest(hostPrefix + i);
                String hostId = String.valueOf(ifs.requestHostCertificate(usr.getGridId(), req).getId());
                performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                    FederationAudit.HostCertificateRequested);
                performAndValidateSingleAudit(ifs, adminGridId, hostId, AuditConstants.SYSTEM_ID,
                    FederationAudit.HostCertificateApproved);
            }

            // Find by Subject;
            HostCertificateFilter f1 = new HostCertificateFilter();
            f1.setSubject(subjectPrefix);
            assertEquals(total, ifs.findHostCertificates(adminGridId, f1).length);
            for (int i = 0; i < total; i++) {
                String subject = org.cagrid.gaards.dorian.service.util.Utils.getHostCertificateSubject(ca
                    .getCACertificate(), hostPrefix + i);
                f1.setSubject(subject);
                HostCertificateRecord[] r = ifs.findHostCertificates(adminGridId, f1);
                assertEquals(1, r.length);
                assertEquals(subject, r[0].getSubject());
            }

            // Find by host;
            HostCertificateFilter f2 = new HostCertificateFilter();
            f2.setHost(hostPrefix);
            assertEquals(total, ifs.findHostCertificates(adminGridId, f2).length);
            for (int i = 0; i < total; i++) {
                String host = hostPrefix + i;
                f2.setHost(host);
                HostCertificateRecord[] r = ifs.findHostCertificates(adminGridId, f2);
                assertEquals(1, r.length);
                assertEquals(host, r[0].getHost());
            }

            // Find by Owner;
            HostCertificateFilter f3 = new HostCertificateFilter();
            f3.setOwner(usr.getGridId());
            assertEquals(total, ifs.findHostCertificates(adminGridId, f3).length);

            // Find by host;
            HostCertificateFilter f4 = new HostCertificateFilter();
            f4.setStatus(HostCertificateStatus.Active);
            assertEquals(total, ifs.findHostCertificates(adminGridId, f4).length);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testFindHostCertificatesInvalidUser() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            createUser(ifs, adminGridId, idp, "user");
            try {
                ifs.findHostCertificates("bad user", new HostCertificateFilter());
                fail("Should have failed.");
            } catch (PermissionDeniedFault f) {

            }

            performAndValidateSingleAudit(ifs, adminGridId, "bad user", AuditConstants.SYSTEM_ID,
                FederationAudit.AccessDenied);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testUpdateHostCertificate() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(true);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String host = "localhost";
            HostCertificateRequest req = getHostCertificateRequest(host);
            HostCertificateRecord record = ifs.requestHostCertificate(usr.getGridId(), req);
            String subject = org.cagrid.gaards.dorian.service.util.Utils.getHostCertificateSubject(ca
                .getCACertificate(), host);
            assertEquals(HostCertificateStatus.Active, record.getStatus());;
            assertEquals(subject, record.getSubject());
            assertEquals(subject, CertUtil.loadCertificate(record.getCertificate().getCertificateAsString())
                .getSubjectDN().getName());
            String hostId = String.valueOf(record.getId());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                FederationAudit.HostCertificateRequested);
            performAndValidateSingleAudit(ifs, adminGridId, hostId, AuditConstants.SYSTEM_ID,
                FederationAudit.HostCertificateApproved);

            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(record.getId());
            update.setStatus(HostCertificateStatus.Suspended);
            ifs.updateHostCertificateRecord(adminGridId, update);
            HostCertificateFilter f = new HostCertificateFilter();
            f.setId(BigInteger.valueOf(record.getId()));
            HostCertificateRecord[] r = ifs.findHostCertificates(adminGridId, f);
            assertEquals(1, r.length);
            assertEquals(HostCertificateStatus.Suspended, r[0].getStatus());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, adminGridId, FederationAudit.HostCertificateUpdated);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testUpdateHostCertificatesInvalidUser() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            createUser(ifs, adminGridId, idp, "user");
            try {
                ifs.updateHostCertificateRecord("bad user", new HostCertificateUpdate());
                fail("Should have failed.");
            } catch (PermissionDeniedFault f) {

            }

            performAndValidateSingleAudit(ifs, adminGridId, "bad user", AuditConstants.SYSTEM_ID,
                FederationAudit.AccessDenied);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testUpdateInvalidOwner() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String host = "localhost";
            HostCertificateRequest req = getHostCertificateRequest(host);
            HostCertificateRecord record = ifs.requestHostCertificate(usr.getGridId(), req);
            String hostId = String.valueOf(record.getId());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                FederationAudit.HostCertificateRequested);
            try {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(record.getId());
                update.setOwner("invalid user");
                ifs.updateHostCertificateRecord(adminGridId, update);
                fail("Should have failed.");
            } catch (InvalidHostCertificateFault f) {

            }

            try {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(record.getId());
                update.setOwner("");
                ifs.updateHostCertificateRecord(adminGridId, update);
                fail("Should have failed.");
            } catch (InvalidHostCertificateFault f) {

            }

            GridUser usr2 = createUser(ifs, adminGridId, idp, "user2");
            usr2.setUserStatus(GridUserStatus.Suspended);
            ifs.updateUser(adminGridId, usr2);

            try {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(record.getId());
                update.setOwner(usr2.getGridId());
                ifs.updateHostCertificateRecord(adminGridId, update);
                fail("Should have failed.");
            } catch (InvalidHostCertificateFault f) {

            }

            usr2.setUserStatus(GridUserStatus.Active);
            ifs.updateUser(adminGridId, usr2);

            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(record.getId());
            update.setOwner(usr2.getGridId());
            ifs.updateHostCertificateRecord(adminGridId, update);
            HostCertificateFilter f = new HostCertificateFilter();
            f.setId(BigInteger.valueOf(record.getId()));
            assertEquals(usr2.getGridId(), ifs.findHostCertificates(adminGridId, f)[0].getOwner());
            performAndValidateSingleAudit(ifs, adminGridId, hostId, adminGridId, FederationAudit.HostCertificateUpdated);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testGetHostCertificatesForCaller() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(true);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String hostPrefix = "localhost";
            int total = 3;

            for (int i = 0; i < total; i++) {
                HostCertificateRequest req = getHostCertificateRequest(hostPrefix + i);
                String hostId = String.valueOf(ifs.requestHostCertificate(usr.getGridId(), req).getId());
                performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                    FederationAudit.HostCertificateRequested);
                performAndValidateSingleAudit(ifs, adminGridId, hostId, AuditConstants.SYSTEM_ID,
                    FederationAudit.HostCertificateApproved);
            }

            HostCertificateRecord[] r = ifs.getHostCertificatesForCaller(usr.getGridId());
            assertEquals(total, r.length);
            for (int i = 0; i < total; i++) {
                String host = hostPrefix + i;
                boolean found = false;
                for (int j = 0; j < r.length; j++) {
                    if (host.equals(r[j].getHost())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    fail("A host certificate that was expected was not found.");
                }
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void testGetHostCertificatesForCallerInvalidUser() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            createUser(ifs, adminGridId, idp, "user");
            try {
                ifs.getHostCertificatesForCaller("bad user");
                fail("Should have failed.");
            } catch (PermissionDeniedFault f) {

            }
            performAndValidateSingleAudit(ifs, adminGridId, "bad user", AuditConstants.SYSTEM_ID,
                FederationAudit.AccessDenied);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testHostCeritifcateStatusAfterUserRemoval() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");
            String host = "localhost1";
            HostCertificateRequest req = getHostCertificateRequest(host);
            HostCertificateRecord record = ifs.requestHostCertificate(usr.getGridId(), req);
            String hostId = String.valueOf(record.getId());
            assertEquals(HostCertificateStatus.Pending, record.getStatus());
            assertEquals(null, record.getCertificate());
            String subject = org.cagrid.gaards.dorian.service.util.Utils.getHostCertificateSubject(ca
                .getCACertificate(), host);

            performAndValidateSingleAudit(ifs, adminGridId, hostId, usr.getGridId(),
                FederationAudit.HostCertificateRequested);

            record = ifs.approveHostCertificate(adminGridId, record.getId());

            assertEquals(HostCertificateStatus.Active, record.getStatus());
            assertEquals(subject, record.getSubject());
            assertEquals(subject, CertUtil.loadCertificate(record.getCertificate().getCertificateAsString())
                .getSubjectDN().getName());

            performAndValidateSingleAudit(ifs, adminGridId, hostId, adminGridId,
                FederationAudit.HostCertificateApproved);

            String host2 = "localhost2";
            HostCertificateRequest req2 = getHostCertificateRequest(host2);
            HostCertificateRecord record2 = ifs.requestHostCertificate(usr.getGridId(), req2);
            assertEquals(HostCertificateStatus.Pending, record2.getStatus());
            assertEquals(null, record2.getCertificate());
            String hostId2 = String.valueOf(record.getId());
            performAndValidateSingleAudit(ifs, adminGridId, hostId2, usr.getGridId(),
                FederationAudit.HostCertificateRequested);

            ifs.removeUser(adminGridId, usr);

            performAndValidateSingleAudit(ifs, adminGridId, usr.getGridId(), adminGridId,
                FederationAudit.AccountRemoved);

            HostCertificateFilter f = new HostCertificateFilter();
            f.setId(BigInteger.valueOf(record.getId()));

            HostCertificateRecord[] r = ifs.findHostCertificates(adminGridId, f);
            assertEquals(1, r.length);
            assertEquals(HostCertificateStatus.Compromised, r[0].getStatus());

            performAndValidateSingleAudit(ifs, adminGridId, hostId, AuditConstants.SYSTEM_ID,
                FederationAudit.HostCertificateUpdated);

            f.setId(BigInteger.valueOf(record2.getId()));
            HostCertificateRecord[] r2 = ifs.findHostCertificates(adminGridId, f);
            assertEquals(1, r2.length);
            assertEquals(HostCertificateStatus.Rejected, r2[0].getStatus());

            performAndValidateSingleAudit(ifs, adminGridId, hostId2, AuditConstants.SYSTEM_ID,
                FederationAudit.HostCertificateUpdated);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void testAdminAcessAfterUserRemoval() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf(false);
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            GridUser usr = createUser(ifs, adminGridId, idp, "user");

            ifs.addAdmin(adminGridId, usr.getGridId());

            performAndValidateSingleAudit(ifs, adminGridId, usr.getGridId(), adminGridId, FederationAudit.AdminAdded);

            ifs.removeUser(adminGridId, usr);

            performAndValidateSingleAudit(ifs, adminGridId, usr.getGridId(), adminGridId,
                FederationAudit.AccountRemoved);

            performAndValidateSingleAudit(ifs, adminGridId, usr.getGridId(), AuditConstants.SYSTEM_ID,
                FederationAudit.AdminRemoved);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void testGetCRL() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);

            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            IdPContainer idp2 = this.getTrustedIdpAutoApprove("My IdP2");
            ifs.addTrustedIdP(adminGridId, idp2.getIdp());
            String hostPrefix = "myhost";
            int hostCount = 1;
            int totalUsers = 3;
            int userHostCerts = 2;
            int total = (totalUsers) + (totalUsers * userHostCerts);

            // Create users and host certificates
            List<UserContainer> list = new ArrayList<UserContainer>();
            for (int i = 0; i < totalUsers; i++) {
                String uid = "user" + i;
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                PublicKey publicKey = pair.getPublic();
                CertificateLifetime lifetime = getLifetime();
                X509Certificate cert = ifs.requestUserCertificate(getSAMLAssertion(uid, idp2), publicKey, lifetime);
                String expectedIdentity = UserManager
                    .subjectToIdentity(UserManager.getUserSubject(ifs.getIdentityAssignmentPolicy(), ca
                        .getCACertificate().getSubjectDN().getName(), idp2.getIdp(), uid));

                checkCertificate(expectedIdentity, lifetime, pair.getPrivate(), cert);
                GridUserFilter f1 = new GridUserFilter();
                f1.setIdPId(idp2.getIdp().getId());
                f1.setUID(uid);
                GridUser[] users = ifs.findUsers(adminGridId, f1);
                assertEquals(1, users.length);
                UserContainer usr = new UserContainer(users[0]);
                list.add(usr);
                for (int j = 0; j < userHostCerts; j++) {
                    usr.addHostCertificate(createAndSubmitHostCert(ifs, conf, adminGridId, usr.getUser().getGridId(),
                        hostPrefix + hostCount));
                    hostCount++;
                }
            }

            X509CRL crl = ifs.getCRL();
            assertEquals(null, crl.getRevokedCertificates());

            // Suspend IDP
            idp2.getIdp().setStatus(TrustedIdPStatus.Suspended);
            ifs.updateTrustedIdP(adminGridId, idp2.getIdp());
            crl = ifs.getCRL();
            assertEquals(total, crl.getRevokedCertificates().size());
            for (int i = 0; i < list.size(); i++) {
                UserCertificateFilter f = new UserCertificateFilter();
                f.setGridIdentity(list.get(i).getUser().getGridId());
                DateRange now = new DateRange();
                now.setStartDate(new GregorianCalendar());
                now.setEndDate(new GregorianCalendar());
                f.setDateRange(now);
                List<UserCertificateRecord> records = ifs.findUserCertificateRecords(adminGridId, f);
                for (int j = 0; j < records.size(); j++) {
                    X509Certificate cert = CertUtil.loadCertificate(records.get(j).getCertificate()
                        .getCertificateAsString());
                    assertNotNull(crl.getRevokedCertificate(cert));
                }
                List<HostCertificateRecord> hosts = list.get(i).getHostCertificates();
                for (int j = 0; j < hosts.size(); j++) {
                    assertNotNull(crl.getRevokedCertificate(CertUtil.loadCertificate(hosts.get(j).getCertificate()
                        .getCertificateAsString())));
                }
            }

            idp2.getIdp().setStatus(TrustedIdPStatus.Active);
            ifs.updateTrustedIdP(adminGridId, idp2.getIdp());

            crl = ifs.getCRL();
            assertEquals(null, crl.getRevokedCertificates());
            for (int i = 0; i < list.size(); i++) {
                UserCertificateFilter f = new UserCertificateFilter();
                f.setGridIdentity(list.get(i).getUser().getGridId());
                DateRange now = new DateRange();
                now.setStartDate(new GregorianCalendar());
                now.setEndDate(new GregorianCalendar());
                f.setDateRange(now);
                List<UserCertificateRecord> records = ifs.findUserCertificateRecords(adminGridId, f);
                for (int j = 0; j < records.size(); j++) {
                    X509Certificate cert = CertUtil.loadCertificate(records.get(j).getCertificate()
                        .getCertificateAsString());
                    assertNull(crl.getRevokedCertificate(cert));
                }
                List<HostCertificateRecord> hosts = list.get(i).getHostCertificates();
                for (int j = 0; j < hosts.size(); j++) {
                    assertNull(crl.getRevokedCertificate(CertUtil.loadCertificate(hosts.get(j).getCertificate()
                        .getCertificateAsString())));
                }
            }

            this.validateCRLOnDisabledUserStatus(ifs, list, GridUserStatus.Suspended, adminGridId, userHostCerts);

            assertTrue(userHostCerts >= 1);

            X509Certificate oldHostCert = CertUtil.loadCertificate(list.get(0).getHostCertificates().get(0)
                .getCertificate().getCertificateAsString());
            HostCertificateRecord hcr = ifs.renewHostCertificate(adminGridId, list.get(0).getHostCertificates().get(0)
                .getId());
            assertEquals(list.get(0).getHostCertificates().get(0).getId(), hcr.getId());
            X509Certificate newHostCert = CertUtil.loadCertificate(hcr.getCertificate().getCertificateAsString());
            crl = ifs.getCRL();
            int alreadyRevokedCertificates = 1;
            assertNotNull(crl.getRevokedCertificate(oldHostCert));
            assertNull(crl.getRevokedCertificate(newHostCert));

            alreadyRevokedCertificates = this.validateCRLOnDisabledHostStatus(ifs, list,
                HostCertificateStatus.Suspended, adminGridId, userHostCerts, alreadyRevokedCertificates);
            alreadyRevokedCertificates = this.validateCRLOnDisabledHostStatus(ifs, list,
                HostCertificateStatus.Compromised, adminGridId, userHostCerts, alreadyRevokedCertificates);

            // Test compromising user certificates
            for (int i = 0; i < list.size(); i++) {
                UserContainer usr = list.get(i);
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                PublicKey publicKey = pair.getPublic();
                CertificateLifetime lifetime = getLifetime();
                X509Certificate cert = ifs.requestUserCertificate(getSAMLAssertion(usr.getUser().getUID(), idp2),
                    publicKey, lifetime);
                String expectedIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(ifs
                    .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp2.getIdp(), usr
                    .getUser().getUID()));

                checkCertificate(expectedIdentity, lifetime, pair.getPrivate(), cert);

                crl = ifs.getCRL();
                assertEquals(alreadyRevokedCertificates, crl.getRevokedCertificates().size());
                assertNull(crl.getRevokedCertificate(cert));
                UserCertificateUpdate u = new UserCertificateUpdate();
                u.setSerialNumber(cert.getSerialNumber().longValue());
                u.setStatus(UserCertificateStatus.Compromised);
                ifs.updateUserCertificateRecord(adminGridId, u);
                alreadyRevokedCertificates = alreadyRevokedCertificates + 1;
                crl = ifs.getCRL();
                assertEquals(alreadyRevokedCertificates, crl.getRevokedCertificates().size());
                assertNotNull(crl.getRevokedCertificate(cert));
                UserCertificateFilter f = new UserCertificateFilter();
                f.setGridIdentity(usr.getUser().getGridId());
                List<UserCertificateRecord> records = ifs.findUserCertificateRecords(adminGridId, f);
                assertEquals(2, records.size());
                for (int j = 0; j < records.size(); j++) {
                    X509Certificate cert2 = CertUtil.loadCertificate(records.get(j).getCertificate()
                        .getCertificateAsString());
                    if (records.get(j).getSerialNumber() == cert.getSerialNumber().longValue()) {
                        assertNotNull(crl.getRevokedCertificate(cert2));
                        usr.addCompromisedUserCertificate(cert2);
                    } else {
                        assertNull(crl.getRevokedCertificate(cert2));
                        usr.addUserCertificate(cert2);
                    }
                }
            }

            // Test deleting users

            for (int i = 0; i < list.size(); i++) {
                UserContainer usr = list.get(i);
                ifs.removeUser(adminGridId, usr.getUser());
                crl = ifs.getCRL();
                List<X509Certificate> compromisedUserCerts = usr.getCompromisedUserCerts();
                for (int j = 0; j < compromisedUserCerts.size(); j++) {
                    assertNotNull(crl.getRevokedCertificate(compromisedUserCerts.get(j)));
                }

                List<X509Certificate> userCerts = usr.getUserCerts();
                for (int j = 0; j < userCerts.size(); j++) {
                    assertNull(crl.getRevokedCertificate(userCerts.get(j)));
                }

                List<HostCertificateRecord> hostCerts = usr.getHostCertificates();
                for (int j = 0; j < compromisedUserCerts.size(); j++) {
                    X509Certificate hostCert = CertUtil.loadCertificate(hostCerts.get(j).getCertificate()
                        .getCertificateAsString());
                    assertNotNull(crl.getRevokedCertificate(hostCert));
                }
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testFindRemoveUpdateUsers() {
        IdentityFederationManager ifs = null;
        try {
            int times = 3;
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String uidPrefix = "user";
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            int ucount = 1;
            for (int i = 0; i < times; i++) {
                String uid = uidPrefix + i;
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                PublicKey publicKey = pair.getPublic();
                CertificateLifetime lifetime = getLifetime();
                X509Certificate cert = ifs.requestUserCertificate(getSAMLAssertion(uid, idp), publicKey, lifetime);
                String expectedIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(ifs
                    .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(), uid));

                checkCertificate(expectedIdentity, lifetime, pair.getPrivate(), cert);
                performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                    FederationAudit.AccountCreated);
                performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                    FederationAudit.SuccessfulUserCertificateRequest);
                ucount = ucount + 1;
                assertEquals(ucount, ifs.findUsers(adminGridId, new GridUserFilter()).length);
                GridUserFilter f1 = new GridUserFilter();
                f1.setIdPId(idp.getIdp().getId());
                f1.setUID(uid);
                GridUser[] usr = ifs.findUsers(adminGridId, f1);
                assertEquals(1, usr.length);

                try {
                    ifs.findUsers(usr[0].getGridId(), new GridUserFilter());
                    fail("Should have thrown exception attempting to find users.");
                } catch (PermissionDeniedFault f) {

                }

                performAndValidateSingleAudit(ifs, adminGridId, usr[0].getGridId(), AuditConstants.SYSTEM_ID,
                    FederationAudit.AccessDenied);

                ifs.addAdmin(adminGridId, usr[0].getGridId());
                performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, adminGridId,
                    FederationAudit.AdminAdded);
                assertEquals(ucount, ifs.findUsers(usr[0].getGridId(), new GridUserFilter()).length);
            }

            for (int i = 0; i < times; i++) {
                String uid = uidPrefix + i;
                GridUserFilter f1 = new GridUserFilter();
                f1.setIdPId(idp.getIdp().getId());
                f1.setUID(uid);
                GridUser[] usr = ifs.findUsers(adminGridId, f1);
                assertEquals(1, usr.length);
                usr[0].setUserStatus(GridUserStatus.Suspended);
                ifs.updateUser(adminGridId, usr[0]);
                performAndValidateSingleAudit(ifs, adminGridId, usr[0].getGridId(), adminGridId,
                    FederationAudit.AccountUpdated);
            }

            int rcount = ucount;

            for (int i = 0; i < times; i++) {
                String uid = uidPrefix + i;
                GridUserFilter f1 = new GridUserFilter();
                f1.setIdPId(idp.getIdp().getId());
                f1.setUID(uid);
                GridUser[] usr = ifs.findUsers(adminGridId, f1);
                assertEquals(1, usr.length);
                ifs.removeUser(adminGridId, usr[0]);
                performAndValidateSingleAudit(ifs, adminGridId, usr[0].getGridId(), adminGridId,
                    FederationAudit.AccountRemoved);
                performAndValidateSingleAudit(ifs, adminGridId, usr[0].getGridId(), AuditConstants.SYSTEM_ID,
                    FederationAudit.AdminRemoved);
                rcount = rcount - 1;
                assertEquals(rcount, ifs.findUsers(adminGridId, new GridUserFilter()).length);
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void testRequestCertificate() {
        IdentityFederationManager ifs = null;
        try {

            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String adminSubject = UserManager.getUserSubject(conf.getIdentityAssignmentPolicy(), ca.getCACertificate()
                .getSubjectDN().getName(), idp.getIdp(), INITIAL_ADMIN);
            String adminGridId = UserManager.subjectToIdentity(adminSubject);
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            PublicKey publicKey = pair.getPublic();
            CertificateLifetime lifetime = getLifetime();
            String uid = "user";
            X509Certificate cert = ifs.requestUserCertificate(getSAMLAssertion(uid, idp), publicKey, lifetime);
            String expectedIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(ifs
                .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(), uid));

            checkCertificate(expectedIdentity, lifetime, pair.getPrivate(), cert);
            performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.AccountCreated);
            performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.SuccessfulUserCertificateRequest);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void testRequestCertificateSuspendedIdP() {
        IdentityFederationManager ifs = null;
        try {

            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            idp.getIdp().setStatus(TrustedIdPStatus.Suspended);
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            PublicKey publicKey = pair.getPublic();
            CertificateLifetime lifetime = getLifetime();
            try {
                ifs.requestUserCertificate(getSAMLAssertion("user", idp), publicKey, lifetime);
                fail("Should not be able to request a certificate if the IdP is suspended.");
            } catch (PermissionDeniedFault f) {
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void testRequestCertificateAutoApproval() {
        IdentityFederationManager ifs = null;
        try {
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            CertificateLifetime lifetime = getLifetimeShort();
            String username = "user";
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getExpiringCredentialsConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String gridId = UserManager.subjectToIdentity(UserManager.getUserSubject(
                conf.getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(),
                INITIAL_ADMIN));
            // give a chance for others to run right before we enter timing
            // sensitive code
            X509Certificate cert = ifs.requestUserCertificate(getSAMLAssertion(username, idp), pair.getPublic(),
                lifetime);
            String expectedIdentity = UserManager
                .subjectToIdentity(UserManager.getUserSubject(ifs.getIdentityAssignmentPolicy(), ca.getCACertificate()
                    .getSubjectDN().getName(), idp.getIdp(), username));

            checkCertificate(expectedIdentity, lifetime, pair.getPrivate(), cert);
            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.AccountCreated);
            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.SuccessfulUserCertificateRequest);
            assertEquals(ifs.getUser(gridId, idp.getIdp().getId(), username).getUserStatus(), GridUserStatus.Active);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestCertificateManualApproval() {
        IdentityFederationManager ifs = null;
        try {
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            CertificateLifetime lifetime = getLifetimeShort();
            String username = "user";
            IdPContainer idp = this.getTrustedIdpManualApprove("My IdP");
            IdentityFederationProperties conf = getExpiringCredentialsConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String gridId = UserManager.subjectToIdentity(UserManager.getUserSubject(
                conf.getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(),
                defaults.getDefaultUser().getUID()));

            try {
                ifs.requestUserCertificate(getSAMLAssertion(username, idp), pair.getPublic(), lifetime);
                fail("Should have thrown exception attempting to create proxy.");
            } catch (PermissionDeniedFault fault) {

            }

            String expectedIdentity = UserManager
                .subjectToIdentity(UserManager.getUserSubject(ifs.getIdentityAssignmentPolicy(), ca.getCACertificate()
                    .getSubjectDN().getName(), idp.getIdp(), username));

            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.AccountCreated);
            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest);
            assertEquals(ifs.getUser(gridId, idp.getIdp().getId(), username).getUserStatus(), GridUserStatus.Pending);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestCertificateInvalidLifetime() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            String username = "user";
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            Thread.sleep(500);
            try {
                CertificateLifetime valid = new CertificateLifetime();
                valid.setHours(12);
                valid.setMinutes(0);
                valid.setSeconds(1);
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                PublicKey publicKey = pair.getPublic();
                ifs.requestUserCertificate(getSAMLAssertion(username, idp), publicKey, valid);
                fail("Should not be able to request a certificate with an invalid lifetime.");
            } catch (UserPolicyFault f) {

            }

            String gridId = UserManager.subjectToIdentity(UserManager.getUserSubject(
                conf.getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(),
                defaults.getDefaultUser().getUID()));

            String expectedIdentity = UserManager
                .subjectToIdentity(UserManager.getUserSubject(ifs.getIdentityAssignmentPolicy(), ca.getCACertificate()
                    .getSubjectDN().getName(), idp.getIdp(), username));

            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.AccountCreated);
            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest);
            assertEquals(ifs.getUser(gridId, idp.getIdp().getId(), username).getUserStatus(), GridUserStatus.Pending);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestCertificateInvalidAuthenticationMethod() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            String username = "user";
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            try {
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                PublicKey publicKey = pair.getPublic();
                ifs.requestUserCertificate(getSAMLAssertionUnspecifiedMethod(username, idp), publicKey, getLifetime());
                fail("Should not be able to request a certificate with an Invalid Authentication Method.");
            } catch (InvalidAssertionFault f) {

            }
            String gridId = UserManager.subjectToIdentity(UserManager.getUserSubject(
                conf.getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(),
                defaults.getDefaultUser().getUID()));

            String expectedIdentity = UserManager
                .subjectToIdentity(UserManager.getUserSubject(ifs.getIdentityAssignmentPolicy(), ca.getCACertificate()
                    .getSubjectDN().getName(), idp.getIdp(), username));

            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestCertificateUntrustedIdP() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdPContainer idp2 = this.getTrustedIdpAutoApprove("My IdP 2");

            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            String username = "user";
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);

            try {
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                PublicKey publicKey = pair.getPublic();
                ifs.requestUserCertificate(getSAMLAssertion(username, idp2), publicKey, getLifetime());
                fail("Should not be able to request a certificate when the IdP is not trusted.");
            } catch (InvalidAssertionFault f) {

            }
            String gridId = UserManager.subjectToIdentity(UserManager.getUserSubject(
                conf.getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(),
                defaults.getDefaultUser().getUID()));

            performAndValidateSingleAudit(ifs, gridId, AuditConstants.SYSTEM_ID, AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testRequestCertificateExpiredAssertion() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            String username = "user";
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            try {
                KeyPair pair = KeyUtil.generateRSAKeyPair1024();
                PublicKey publicKey = pair.getPublic();
                ifs.requestUserCertificate(getExpiredSAMLAssertion(username, idp), publicKey, getLifetime());
                fail("Should not be able to request a certificate if the SAML Asserion is expired");
            } catch (InvalidAssertionFault f) {

            }
            String gridId = UserManager.subjectToIdentity(UserManager.getUserSubject(
                conf.getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(),
                defaults.getDefaultUser().getUID()));

            String expectedIdentity = UserManager
                .subjectToIdentity(UserManager.getUserSubject(ifs.getIdentityAssignmentPolicy(), ca.getCACertificate()
                    .getSubjectDN().getName(), idp.getIdp(), username));

            performAndValidateSingleAudit(ifs, gridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.InvalidUserCertificateRequest);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testGetTrustedIdPs() {
        IdentityFederationManager ifs = null;
        try {
            IdPContainer idp0 = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getExpiringCredentialsConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp0.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            String gridId = UserManager.subjectToIdentity(UserManager.getUserSubject(
                conf.getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp0.getIdp(),
                defaults.getDefaultUser().getUID()));

            int times = 3;
            String baseName = "Test IdP";
            int count = 1;
            for (int i = 0; i < times; i++) {
                assertEquals(count, ifs.getTrustedIdPs(gridId).length);
                count = count + 1;
                String name = baseName + " " + count;
                IdPContainer cont = getTrustedIdpAutoApprove(name);
                TrustedIdP idp = cont.getIdp();
                idp = ifs.addTrustedIdP(gridId, idp);
                assertEquals(count, ifs.getTrustedIdPs(gridId).length);
                performAndValidateSingleAudit(ifs, gridId, idp.getName(), gridId, FederationAudit.IdPAdded);

                // Test Updates
                IdPContainer updateCont = getTrustedIdpManualApprove(name);
                TrustedIdP updateIdp = updateCont.getIdp();
                updateIdp.setId(idp.getId());
                ifs.updateTrustedIdP(gridId, updateIdp);
                assertEquals(count, ifs.getTrustedIdPs(gridId).length);
                performAndValidateSingleAudit(ifs, gridId, idp.getName(), gridId, FederationAudit.IdPUpdated);
            }

            TrustedIdP[] idps = ifs.getTrustedIdPs(gridId);
            assertEquals(times + 1, idps.length);
            count = times + 1;
            for (int i = 0; i < idps.length; i++) {
                if (idps[i].getId() != idp0.getIdp().getId()) {
                    count = count - 1;
                    ifs.removeTrustedIdP(gridId, idps[i].getId());
                    assertEquals(count, ifs.getTrustedIdPs(gridId).length);
                    performAndValidateSingleAudit(ifs, gridId, idps[i].getName(), gridId, FederationAudit.IdPRemoved);
                }
            }

            assertEquals(count, ifs.getTrustedIdPs(gridId).length);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void testAdministrators() {
        IdentityFederationManager ifs = null;
        try {

            IdPContainer idp = this.getTrustedIdpAutoApprove("My IdP");
            IdentityFederationProperties conf = getConf();
            FederationDefaults defaults = getDefaults();
            defaults.setDefaultIdP(idp.getIdp());
            ifs = new IdentityFederationManager(conf, db, props, ca, eventManager, defaults);
            KeyPair pair = KeyUtil.generateRSAKeyPair1024();
            PublicKey publicKey = pair.getPublic();
            CertificateLifetime lifetime = getLifetime();
            String uid = "user";
            X509Certificate cert = ifs.requestUserCertificate(getSAMLAssertion(uid, idp), publicKey, lifetime);
            String expectedIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(ifs
                .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(), uid));

            checkCertificate(expectedIdentity, lifetime, pair.getPrivate(), cert);

            String adminGridId = defaults.getDefaultUser().getGridId();

            performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.AccountCreated);
            performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
                FederationAudit.SuccessfulUserCertificateRequest);

            String userId = UserManager.subjectToIdentity(cert.getSubjectDN().toString());
            // Check that the user cannot call any admin methods
            int count = validateAccessControl(ifs, adminGridId, userId, 0);
            ifs.addAdmin(defaults.getDefaultUser().getGridId(), userId);
            assertEquals(2, ifs.findUsers(userId, null).length);

            performAndValidateSingleAudit(ifs, adminGridId, userId, adminGridId, FederationAudit.AdminAdded);
            ifs.removeAdmin(userId, userId);
            validateAccessControl(ifs, adminGridId, userId, count);
            performAndValidateSingleAudit(ifs, adminGridId, userId, userId, FederationAudit.AdminRemoved);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        } finally {
            try {
                ifs.clearDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    private int validateAccessControl(IdentityFederationManager ifs, String adminId, String userId, int startCount)
        throws Exception {
        int count = startCount;
        try {
            ifs.addAdmin(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }
        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.addTrustedIdP(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.findUsers(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.getAdmins(userId);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.getTrustedIdPs(userId);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.getUser(userId, 0, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.getUserPolicies(userId);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.removeAdmin(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.removeTrustedIdP(userId, 0);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.removeUser(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.updateTrustedIdP(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.updateUser(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.findUserCertificateRecords(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.updateUserCertificateRecord(userId, null);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.removeUserCertificate(userId, 0);
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        try {
            ifs.performAudit(userId, new FederationAuditFilter());
            fail("Should not have permission to execute the operation.");
        } catch (PermissionDeniedFault f) {

        }

        count = count + 1;
        performAndValidateMultipleAudit(ifs, adminId, userId, AuditConstants.SYSTEM_ID, FederationAudit.AccessDenied,
            count);

        return count;
    }


    private IdentityFederationProperties getConf() throws Exception {
        return getConf(true);
    }


    private IdentityFederationProperties getConf(boolean autoHostCertificateApproval) throws Exception {
        IdentityFederationProperties conf = new IdentityFederationProperties();
        conf.setIdentityAssignmentPolicy(org.cagrid.gaards.dorian.federation.IdentityAssignmentPolicy.NAME);
        Lifetime l = new Lifetime();
        l.setYears(1);
        l.setMonths(0);
        l.setDays(0);
        l.setHours(0);
        l.setMinutes(0);
        l.setSeconds(0);
        conf.setIssuedCertificateLifetime(l);
        conf.setAutoHostCertificateApproval(autoHostCertificateApproval);

        conf.setMinIdPNameLength(MIN_NAME_LENGTH);
        conf.setMaxIdPNameLength(MAX_NAME_LENGTH);

        Lifetime pl = new Lifetime();
        pl.setHours(12);
        pl.setMinutes(0);
        pl.setSeconds(0);
        conf.setUserCertificateLifetime(pl);
        List<AccountPolicy> policies = new ArrayList<AccountPolicy>();
        policies.add(new ManualApprovalPolicy());
        policies.add(new AutoApprovalPolicy());
        conf.setAccountPolicies(policies);
        return conf;
    }


    private IdentityFederationProperties getExpiringCredentialsConf() throws Exception {

        IdentityFederationProperties conf = new IdentityFederationProperties();
        conf.setIdentityAssignmentPolicy(org.cagrid.gaards.dorian.federation.IdentityAssignmentPolicy.NAME);
        Lifetime l = new Lifetime();
        l.setYears(0);
        l.setMonths(0);
        l.setDays(0);
        l.setHours(0);
        l.setMinutes(0);
        l.setSeconds(SHORT_CREDENTIALS_VALID);
        conf.setIssuedCertificateLifetime(l);
        conf.setAutoHostCertificateApproval(false);

        conf.setMinIdPNameLength(MIN_NAME_LENGTH);
        conf.setMaxIdPNameLength(MAX_NAME_LENGTH);

        Lifetime pl = new Lifetime();
        pl.setHours(12);
        pl.setMinutes(0);
        pl.setSeconds(0);
        conf.setUserCertificateLifetime(pl);
        List<AccountPolicy> policies = new ArrayList<AccountPolicy>();
        policies.add(new ManualApprovalPolicy());
        policies.add(new AutoApprovalPolicy());
        conf.setAccountPolicies(policies);
        return conf;
    }


    private FederationDefaults getDefaults() throws Exception {
        TrustedIdP idp = this.getTrustedIdpAutoApprove("Initial IdP").getIdp();
        GridUser usr = new GridUser();
        usr.setUID(INITIAL_ADMIN);
        usr.setFirstName("Mr");
        usr.setLastName("Admin");
        usr.setEmail(INITIAL_ADMIN + "@test.com");
        usr.setUserStatus(GridUserStatus.Active);
        return new FederationDefaults(idp, usr);
    }


    private SAMLAssertion getSAMLAssertion(String id, IdPContainer idp) throws Exception {
        GregorianCalendar cal = new GregorianCalendar();
        Date start = cal.getTime();
        cal.add(Calendar.MINUTE, 2);
        Date end = cal.getTime();
        return this.getSAMLAssertion(id, idp, start, end, "urn:oasis:names:tc:SAML:1.0:am:password");
    }


    private SAMLAssertion getSAMLAssertionUnspecifiedMethod(String id, IdPContainer idp) throws Exception {
        GregorianCalendar cal = new GregorianCalendar();
        Date start = cal.getTime();
        cal.add(Calendar.MINUTE, 2);
        Date end = cal.getTime();
        return this.getSAMLAssertion(id, idp, start, end, "urn:oasis:names:tc:SAML:1.0:am:unspecified");
    }


    private SAMLAssertion getExpiredSAMLAssertion(String id, IdPContainer idp) throws Exception {
        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.MINUTE, -1);
        Date start = cal.getTime();
        Date end = cal.getTime();
        return this.getSAMLAssertion(id, idp, start, end, "urn:oasis:names:tc:SAML:1.0:am:password");
    }


    private SAMLAssertion getSAMLAssertion(String id, IdPContainer idp, Date start, Date end, String method)
        throws Exception {
        try {
            org.apache.xml.security.Init.init();
            X509Certificate cert = idp.getCert();
            PrivateKey key = idp.getKey();
            String firstName = "first" + id;
            String lastName = "first" + id;
            String email = id + "@test.com";

            String issuer = cert.getSubjectDN().toString();
            String federation = cert.getSubjectDN().toString();
            String ipAddress = null;
            String subjectDNS = null;
            SAMLNameIdentifier ni = new SAMLNameIdentifier(id, federation,
                "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
            SAMLNameIdentifier ni2 = new SAMLNameIdentifier(id, federation,
                "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified");
            SAMLSubject sub = new SAMLSubject(ni, null, null, null);
            SAMLSubject sub2 = new SAMLSubject(ni2, null, null, null);
            SAMLAuthenticationStatement auth = new SAMLAuthenticationStatement(sub, method, new Date(), ipAddress,
                subjectDNS, null);

            QName quid = new QName(SAMLConstants.UID_ATTRIBUTE_NAMESPACE, SAMLConstants.UID_ATTRIBUTE);
            List vals1 = new ArrayList();
            vals1.add(id);
            SAMLAttribute uidAtt = new SAMLAttribute(quid.getLocalPart(), quid.getNamespaceURI(), quid, 0, vals1);

            QName qfirst = new QName(SAMLConstants.FIRST_NAME_ATTRIBUTE_NAMESPACE, SAMLConstants.FIRST_NAME_ATTRIBUTE);
            List vals2 = new ArrayList();
            vals2.add(firstName);
            SAMLAttribute firstNameAtt = new SAMLAttribute(qfirst.getLocalPart(), qfirst.getNamespaceURI(), qfirst, 0,
                vals2);

            QName qLast = new QName(SAMLConstants.LAST_NAME_ATTRIBUTE_NAMESPACE, SAMLConstants.LAST_NAME_ATTRIBUTE);
            List vals3 = new ArrayList();
            vals3.add(lastName);
            SAMLAttribute lastNameAtt = new SAMLAttribute(qLast.getLocalPart(), qLast.getNamespaceURI(), qLast, 0,
                vals3);

            QName qemail = new QName(SAMLConstants.EMAIL_ATTRIBUTE_NAMESPACE, SAMLConstants.EMAIL_ATTRIBUTE);
            List vals4 = new ArrayList();
            vals4.add(email);
            SAMLAttribute emailAtt = new SAMLAttribute(qemail.getLocalPart(), qemail.getNamespaceURI(), qemail, 0,
                vals4);

            List atts = new ArrayList();
            atts.add(uidAtt);
            atts.add(firstNameAtt);
            atts.add(lastNameAtt);
            atts.add(emailAtt);
            SAMLAttributeStatement attState = new SAMLAttributeStatement(sub2, atts);

            List l = new ArrayList();
            l.add(auth);
            l.add(attState);

            SAMLAssertion saml = new SAMLAssertion(issuer, start, end, null, null, l);
            List a = new ArrayList();
            a.add(cert);
            saml.sign(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA1, key, a);

            return saml;
        } catch (Exception e) {
            DorianInternalFault fault = new DorianInternalFault();
            fault.setFaultString("Error creating SAML Assertion.");
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            fault = (DorianInternalFault) helper.getFault();
            throw fault;

        }
    }


    private String identityToSubject(String identity) {
        String s = identity.substring(1);
        return s.replace('/', ',');
    }


    private IdPContainer getTrustedIdpAutoApprove(String name) throws Exception {
        return this.getTrustedIdp(name, AutoApprovalPolicy.class.getName());
    }


    private IdPContainer getTrustedIdpManualApprove(String name) throws Exception {
        return this.getTrustedIdp(name, ManualApprovalPolicy.class.getName());
    }


    private void validateCRLOnDisabledUserStatus(IdentityFederationManager ifs, List<UserContainer> list,
        GridUserStatus status, String adminGridId, int userHostCerts) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            GridUser usr = list.get(i).getUser();
            usr.setUserStatus(status);
            ifs.updateUser(adminGridId, usr);
            X509CRL crl = ifs.getCRL();
            int sum = (i + 1) + ((i + 1) * userHostCerts);
            assertEquals(sum, crl.getRevokedCertificates().size());
            for (int j = 0; j < list.size(); j++) {
                UserContainer curr = list.get(j);
                if (j <= i) {
                    UserCertificateFilter f = new UserCertificateFilter();
                    f.setGridIdentity(curr.getUser().getGridId());
                    DateRange now = new DateRange();
                    now.setStartDate(new GregorianCalendar());
                    now.setEndDate(new GregorianCalendar());
                    f.setDateRange(now);
                    List<UserCertificateRecord> records = ifs.findUserCertificateRecords(adminGridId, f);
                    for (int x = 0; x < records.size(); x++) {
                        X509Certificate cert = CertUtil.loadCertificate(records.get(x).getCertificate()
                            .getCertificateAsString());
                        assertNotNull(crl.getRevokedCertificate(cert));
                    }

                    for (int x = 0; x < curr.getHostCertificates().size(); x++) {
                        assertNotNull(crl.getRevokedCertificate(CertUtil.loadCertificate(curr.getHostCertificates()
                            .get(x).getCertificate().getCertificateAsString())));
                    }
                } else {
                    UserCertificateFilter f = new UserCertificateFilter();
                    f.setGridIdentity(curr.getUser().getGridId());
                    DateRange now = new DateRange();
                    now.setStartDate(new GregorianCalendar());
                    now.setEndDate(new GregorianCalendar());
                    f.setDateRange(now);
                    List<UserCertificateRecord> records = ifs.findUserCertificateRecords(adminGridId, f);
                    for (int x = 0; x < records.size(); x++) {
                        X509Certificate cert = CertUtil.loadCertificate(records.get(x).getCertificate()
                            .getCertificateAsString());
                        assertNull(crl.getRevokedCertificate(cert));
                    }

                    for (int x = 0; x < curr.getHostCertificates().size(); x++) {
                        assertNull(crl.getRevokedCertificate(CertUtil.loadCertificate(curr.getHostCertificates().get(x)
                            .getCertificate().getCertificateAsString())));
                    }
                }

            }
        }

        for (int i = 0; i < list.size(); i++) {
            GridUser usr = list.get(i).getUser();
            usr.setUserStatus(GridUserStatus.Active);
            ifs.updateUser(adminGridId, usr);
        }
        assertEquals(null, ifs.getCRL().getRevokedCertificates());
    }


    private int validateCRLOnDisabledHostStatus(IdentityFederationManager ifs, List<UserContainer> list,
        HostCertificateStatus status, String adminGridId, int userHostCerts, int alreadyRevokedCerts) throws Exception {
        int sum = alreadyRevokedCerts;
        for (int i = 0; i < list.size(); i++) {
            UserContainer usr = list.get(i);
            for (int j = 0; j < usr.getHostCertificates().size(); j++) {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(usr.getHostCertificates().get(j).getId());
                update.setStatus(status);
                ifs.updateHostCertificateRecord(adminGridId, update);
            }
            X509CRL crl = ifs.getCRL();
            sum = ((i + 1) * userHostCerts) + alreadyRevokedCerts;
            assertEquals(sum, crl.getRevokedCertificates().size());
            for (int j = 0; j < list.size(); j++) {
                UserContainer curr = list.get(j);
                UserCertificateFilter f = new UserCertificateFilter();
                f.setGridIdentity(curr.getUser().getGridId());
                DateRange now = new DateRange();
                now.setStartDate(new GregorianCalendar());
                now.setEndDate(new GregorianCalendar());
                f.setDateRange(now);
                List<UserCertificateRecord> records = ifs.findUserCertificateRecords(adminGridId, f);
                for (int x = 0; x < records.size(); x++) {
                    X509Certificate cert = CertUtil.loadCertificate(records.get(x).getCertificate()
                        .getCertificateAsString());
                    assertNull(crl.getRevokedCertificate(cert));
                }

                for (int x = 0; x < curr.getHostCertificates().size(); x++) {
                    if (j <= i) {
                        assertNotNull(crl.getRevokedCertificate(CertUtil.loadCertificate(curr.getHostCertificates()
                            .get(x).getCertificate().getCertificateAsString())));
                    } else {
                        assertNull(crl.getRevokedCertificate(CertUtil.loadCertificate(curr.getHostCertificates().get(x)
                            .getCertificate().getCertificateAsString())));
                    }
                }
            }
        }
        if (!status.equals(HostCertificateStatus.Compromised)) {
            for (int i = 0; i < list.size(); i++) {
                UserContainer usr = list.get(i);
                for (int j = 0; j < usr.getHostCertificates().size(); j++) {
                    HostCertificateUpdate update = new HostCertificateUpdate();
                    update.setId(usr.getHostCertificates().get(j).getId());
                    update.setStatus(HostCertificateStatus.Active);
                    ifs.updateHostCertificateRecord(adminGridId, update);
                }
            }
            if (alreadyRevokedCerts > 0) {
                assertEquals(alreadyRevokedCerts, ifs.getCRL().getRevokedCertificates().size());
            } else {
                assertEquals(null, ifs.getCRL().getRevokedCertificates());
            }
            return alreadyRevokedCerts;
        } else {
            return sum;
        }
    }


    private IdPContainer getTrustedIdp(String name, String policyClass) throws Exception {
        TrustedIdP idp = new TrustedIdP();
        idp.setName(name);
        idp.setDisplayName(name);
        idp.setUserPolicyClass(policyClass);
        idp.setStatus(TrustedIdPStatus.Active);
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

        SAMLAuthenticationMethod[] methods = new SAMLAuthenticationMethod[1];
        methods[0] = SAMLAuthenticationMethod.fromString("urn:oasis:names:tc:SAML:1.0:am:password");
        idp.setAuthenticationMethod(methods);

        String subject = Utils.CA_SUBJECT_PREFIX + ",CN=" + name;
        Credential cred = memoryCA.createIdentityCertificate(name);
        X509Certificate cert = cred.getCertificate();
        assertNotNull(cert);
        assertEquals(cert.getSubjectDN().getName(), subject);
        idp.setIdPCertificate(CertUtil.writeCertificate(cert));
        return new IdPContainer(idp, cert, cred.getPrivateKey());
    }


    private GridUser createUser(IdentityFederationManager ifs, String adminGridId, IdPContainer idp, String uid)
        throws Exception {
        KeyPair pair = KeyUtil.generateRSAKeyPair1024();
        PublicKey publicKey = pair.getPublic();
        CertificateLifetime lifetime = getLifetime();
        X509Certificate cert = ifs.requestUserCertificate(getSAMLAssertion(uid, idp), publicKey, lifetime);
        String expectedIdentity = UserManager.subjectToIdentity(UserManager.getUserSubject(ifs
            .getIdentityAssignmentPolicy(), ca.getCACertificate().getSubjectDN().getName(), idp.getIdp(), uid));
        checkCertificate(expectedIdentity, lifetime, pair.getPrivate(), cert);
        performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
            FederationAudit.AccountCreated);
        performAndValidateSingleAudit(ifs, adminGridId, expectedIdentity, AuditConstants.SYSTEM_ID,
            FederationAudit.SuccessfulUserCertificateRequest);
        GridUserFilter f1 = new GridUserFilter();
        f1.setIdPId(idp.getIdp().getId());
        f1.setUID(uid);
        GridUser[] users = ifs.findUsers(adminGridId, f1);
        assertEquals(1, users.length);
        return users[0];
    }


    private FederationAuditFilter getHostCertificatedApprovedAuditingFilter(String target, String reporter) {
        FederationAuditFilter f = new FederationAuditFilter();
        f.setTargetId(target);
        f.setReportingPartyId(reporter);
        f.setAuditType(FederationAudit.HostCertificateApproved);
        return f;
    }


    private void performAndValidateMultipleAudit(IdentityFederationManager ifm, String adminId, String target,
        String reportingParty, FederationAudit type, int count) throws Exception {
        FederationAuditFilter f = new FederationAuditFilter();
        f.setTargetId(target);
        f.setReportingPartyId(reportingParty);
        f.setAuditType(type);
        List<FederationAuditRecord> results = ifm.performAudit(adminId, f);
        assertEquals(count, results.size());
    }


    private void performAndValidateSingleAudit(IdentityFederationManager ifm, String adminId, String target,
        String reportingParty, FederationAudit type) throws Exception {
        FederationAuditFilter f = new FederationAuditFilter();
        f.setTargetId(target);
        f.setReportingPartyId(reportingParty);
        f.setAuditType(type);
        List<FederationAuditRecord> results = ifm.performAudit(adminId, f);
        assertEquals(1, results.size());
        // printAuditingResults(results);
        validateAuditingResult(results.get(0), target, reportingParty, type);
    }


    private void validateAuditingResult(FederationAuditRecord a, String target, String reportingParty,
        FederationAudit type) {
        assertEquals(target, a.getTargetId());
        assertEquals(reportingParty, a.getReportingPartyId());
        assertEquals(type, a.getAuditType());
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            ca = Utils.getCA();
            memoryCA = new CA(Utils.getCASubject());
            props = new PropertyManager(db);
            eventManager = Utils.getEventManager();
            eventManager.clearHandlers();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        }
    }


    protected void tearDown() throws Exception {
        super.setUp();
        try {
            eventManager.clearHandlers();
            assertEquals(0, db.getUsedConnectionCount());
            assertEquals(0, db.getRootUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail("Exception occured:" + e.getMessage());
        }
        // return the thread to normal priority for those tests which raise the
        // thread priority
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
    }


    private CertificateLifetime getLifetimeShort() {
        CertificateLifetime valid = new CertificateLifetime();
        valid.setHours(0);
        valid.setMinutes(0);
        valid.setSeconds(SHORT_PROXY_VALID);
        return valid;
    }


    private CertificateLifetime getLifetime() {
        CertificateLifetime valid = new CertificateLifetime();
        valid.setHours(12);
        valid.setMinutes(0);
        valid.setSeconds(0);
        return valid;
    }


    private HostCertificateRecord createAndSubmitHostCert(IdentityFederationManager ifs,
        IdentityFederationProperties conf, String admin, String owner, String host) throws Exception {
        HostCertificateRequest req = getHostCertificateRequest(host);
        HostCertificateRecord record = ifs.requestHostCertificate(owner, req);
        if (!conf.autoHostCertificateApproval()) {
            assertEquals(HostCertificateStatus.Pending, record.getStatus());
            record = ifs.approveHostCertificate(admin, record.getId());
        }
        assertEquals(HostCertificateStatus.Active, record.getStatus());
        assertEquals(host, record.getHost());
        return record;
    }


    private HostCertificateRequest getHostCertificateRequest(String host) throws Exception {
        KeyPair pair = KeyUtil.generateRSAKeyPair(ca.getProperties().getIssuedCertificateKeySize());
        HostCertificateRequest req = new HostCertificateRequest();
        req.setHostname(host);
        org.cagrid.gaards.dorian.federation.PublicKey key = new org.cagrid.gaards.dorian.federation.PublicKey();
        key.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
        req.setPublicKey(key);
        return req;
    }


    private void checkCertificate(String expectedIdentity, CertificateLifetime lifetime, PrivateKey key,
        X509Certificate cert) throws Exception {
        assertNotNull(cert);
        GlobusCredential cred = new GlobusCredential(key, new X509Certificate[]{cert});
        assertNotNull(cred);
        long max = FederationUtils.getTimeInSeconds(lifetime);
        // what is this 3 for?
        long min = max - 3;
        long timeLeft = cred.getTimeLeft();
        assertTrue(min <= timeLeft);
        assertTrue(timeLeft <= max);
        assertEquals(expectedIdentity, cred.getIdentity());
        assertEquals(cert.getSubjectDN().toString(), identityToSubject(cred.getIdentity()));
        assertEquals(cred.getIssuer(), ca.getCACertificate().getSubjectDN().getName());
        cred.verify();
    }


    public class IdPContainer {

        TrustedIdP idp;

        X509Certificate cert;

        PrivateKey key;


        public IdPContainer(TrustedIdP idp, X509Certificate cert, PrivateKey key) {
            this.idp = idp;
            this.cert = cert;
            this.key = key;
        }


        public X509Certificate getCert() {
            return cert;
        }


        public TrustedIdP getIdp() {
            return idp;
        }


        public PrivateKey getKey() {
            return key;
        }
    }


    public class UserContainer {
        private GridUser usr;
        private List<HostCertificateRecord> hostCertificates;
        private List<X509Certificate> compromisedUserCerts;
        private List<X509Certificate> userCerts;


        public UserContainer(GridUser usr) {
            this.usr = usr;
            this.hostCertificates = new ArrayList<HostCertificateRecord>();
            this.compromisedUserCerts = new ArrayList<X509Certificate>();
            this.userCerts = new ArrayList<X509Certificate>();
        }


        public GridUser getUser() {
            return usr;
        }


        public List<HostCertificateRecord> getHostCertificates() {
            return hostCertificates;
        }


        public void addHostCertificate(HostCertificateRecord record) {
            hostCertificates.add(record);
        }


        public void addCompromisedUserCertificate(X509Certificate cert) {
            compromisedUserCerts.add(cert);
        }


        public void addUserCertificate(X509Certificate cert) {
            userCerts.add(cert);
        }


        public List<X509Certificate> getCompromisedUserCerts() {
            return compromisedUserCerts;
        }


        public List<X509Certificate> getUserCerts() {
            return userCerts;
        }

    }

}
