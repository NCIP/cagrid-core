package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.math.BigInteger;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.cagrid.gaards.dorian.X509Certificate;
import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.common.Lifetime;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateFault;
import org.cagrid.gaards.dorian.stubs.types.InvalidHostCertificateRequestFault;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestHostCertificateManager extends TestCase implements Publisher {
    public final static String OWNER = "owner";
    private Database db;
    private CertificateAuthority ca;
    private CertificateBlacklistManager blackList;


    public void publishCRL() {
        // TODO Auto-generated method stub

    }


    public void testCreateAndDestroy() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }

    }


    public void testRenewHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            HostCertificateRecord renewed = hcm.renewHostCertificate(id);
            assertEquals(record.getId(), renewed.getId());
            assertEquals(record.getHost(), renewed.getHost());
            assertEquals(record.getOwner(), renewed.getOwner());
            assertEquals(record.getPublicKey(), renewed.getPublicKey());
            assertEquals(record.getSubject(), renewed.getSubject());
            assertEquals(record.getStatus(), renewed.getStatus());

            if (record.getSerialNumber() == renewed.getSerialNumber()) {
                fail("Serial number should not equal.");
            }

            if (record.getCertificate().equals(renewed.getCertificate())) {
                fail("Certificates should not equal.");
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testRenewHostCertificateInvalidStatus() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);

            try {
                hcm.renewHostCertificate(id);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {

            }

            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);

            setHostCertificateStatus(hcm, id, HostCertificateStatus.Suspended);
            try {
                hcm.renewHostCertificate(id);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {
            }

            setHostCertificateStatus(hcm, id, HostCertificateStatus.Compromised);
            try {
                hcm.renewHostCertificate(id);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {
            }

            req = getHostCertificateRequest("localhost");
            id = hcm.requestHostCertifcate(OWNER, req);
            setHostCertificateStatus(hcm, id, HostCertificateStatus.Rejected);
            try {
                hcm.renewHostCertificate(id);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testGetHostCertificateSerialNumbers() {
        try {
            int total = 5;
            String hostPrefix = "localhost";
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            List<Long> ids = new ArrayList<Long>();
            String owner = OWNER;
            for (int i = 0; i < total; i++) {
                String host = hostPrefix + i;
                HostCertificateRequest req = getHostCertificateRequest(host);
                long id = hcm.requestHostCertifcate(owner, req);
                assertEquals(0, hcm.getHostCertificateRecordsSerialNumbers(owner).size());
                ids.add(Long.valueOf(id));
            }

            for (int i = 0; i < total; i++) {
                long id = ids.get(i).longValue();
                hcm.approveHostCertifcate(id);
                List<Long> sn = hcm.getHostCertificateRecordsSerialNumbers(owner);
                assertEquals((i + 1), sn.size());
                for (int j = 0; j < (i + 1); j++) {
                    HostCertificateRecord r = hcm.getHostCertificateRecord(ids.get(j));
                    boolean found = false;
                    for (int x = 0; x < sn.size(); x++) {
                        if (r.getSerialNumber() == sn.get(x).longValue()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        fail("Serial Number not returned.");
                    }
                }
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testGetDisabledHostCertificates() {
        try {
            int total = 5;
            String hostPrefix = "localhost";
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            List<Long> ids = new ArrayList<Long>();
            String owner = OWNER;
            for (int i = 0; i < total; i++) {
                String host = hostPrefix + i;
                HostCertificateRequest req = getHostCertificateRequest(host);
                long id = hcm.requestHostCertifcate(owner, req);
                assertEquals(0, hcm.getDisabledHostCertificatesSerialNumbers().size());
                ids.add(Long.valueOf(id));
            }

            for (int i = 0; i < total; i++) {
                long id = ids.get(i).longValue();
                hcm.approveHostCertifcate(id);
                assertEquals(0, hcm.getDisabledHostCertificatesSerialNumbers().size());
                List<Long> sn = hcm.getHostCertificateRecordsSerialNumbers(owner);
                assertEquals((i + 1), sn.size());
            }

            for (int i = 0; i < 3; i++) {
                long id = ids.get(i).longValue();
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(id);
                update.setStatus(HostCertificateStatus.Suspended);
                hcm.updateHostCertificateRecord(update);
                List<Long> sn = hcm.getDisabledHostCertificatesSerialNumbers();
                assertEquals((i + 1), sn.size());
                for (int j = 0; j < (i + 1); j++) {
                    HostCertificateRecord r = hcm.getHostCertificateRecord(ids.get(j));
                    boolean found = false;
                    for (int x = 0; x < sn.size(); x++) {
                        if (r.getSerialNumber() == sn.get(x).longValue()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        fail("Serial Number not returned.");
                    }
                }
            }

            for (int i = 3; i < 5; i++) {
                long id = ids.get(i).longValue();
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(id);
                update.setStatus(HostCertificateStatus.Compromised);
                hcm.updateHostCertificateRecord(update);
                List<Long> sn = hcm.getDisabledHostCertificatesSerialNumbers();
                assertEquals((i + 1), sn.size());
                for (int j = 0; j < (i + 1); j++) {
                    HostCertificateRecord r = hcm.getHostCertificateRecord(ids.get(j));
                    boolean found = false;
                    for (int x = 0; x < sn.size(); x++) {
                        if (r.getSerialNumber() == sn.get(x).longValue()) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        fail("Serial Number not returned.");
                    }
                }
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testCreateAndApproveManyHostCertificate() {
        try {
            int total = 5;
            String hostPrefix = "localhost";
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            List<HostCertificateRequest> requests = new ArrayList<HostCertificateRequest>();
            List<Long> ids = new ArrayList<Long>();
            for (int i = 0; i < total; i++) {
                String host = hostPrefix + i;
                HostCertificateRequest req = getHostCertificateRequest(host);
                String owner = OWNER + i;
                long id = hcm.requestHostCertifcate(owner, req);
                validateAfterCertificateRequest((i + 1), (i + 1), hcm, owner, req, id);
                requests.add(req);
                ids.add(Long.valueOf(id));

                assertEquals(0, hcm.getHostCertificateRecords(OWNER).size());
                List<HostCertificateRecord> records = hcm.getHostCertificateRecords(owner);
                assertEquals(1, records.size());
                assertEquals(host, records.get(0).getHost());
                assertEquals(owner, records.get(0).getOwner());
            }

            for (int i = 0; i < total; i++) {
                long id = ids.get(i).longValue();
                HostCertificateRequest req = requests.get(i);
                String owner = OWNER + i;
                HostCertificateRecord record = hcm.approveHostCertifcate(id);
                validateAfterCertificateApproval(total, (i + 1), hcm, id, owner, req, record);
                HostCertificateFilter f = new HostCertificateFilter();
                f.setStatus(HostCertificateStatus.Pending);
                assertEquals(total - (i + 1), hcm.findHostCertificates(f).size());

                List<HostCertificateRecord> records = hcm.getHostCertificateRecords(owner);
                assertEquals(1, records.size());
                assertEquals(record, records.get(0));
            }

            // Test find by host
            try {
                HostCertificateFilter f = new HostCertificateFilter();
                f.setHost("foobar");
                assertEquals(0, hcm.findHostCertificates(f).size());
                f.setHost("localhost");
                assertEquals(5, hcm.findHostCertificates(f).size());
            } catch (Exception e) {
                FaultUtil.printFault(e);
                fail(e.getMessage());
            }

            // Test find by owner
            try {
                HostCertificateFilter f = new HostCertificateFilter();
                f.setOwner("foobar");
                assertEquals(0, hcm.findHostCertificates(f).size());
                f.setOwner(OWNER);
                assertEquals(5, hcm.findHostCertificates(f).size());
            } catch (Exception e) {
                FaultUtil.printFault(e);
                fail(e.getMessage());
            }

            // Test find by subject
            try {
                HostCertificateFilter f = new HostCertificateFilter();
                f.setSubject("foobar");
                assertEquals(0, hcm.findHostCertificates(f).size());
                String caSubject = ca.getCACertificate().getSubjectDN().getName();
                int caindex = caSubject.lastIndexOf(",");
                String caPreSub = caSubject.substring(0, caindex);
                f.setSubject(caPreSub);
                assertEquals(5, hcm.findHostCertificates(f).size());
            } catch (Exception e) {
                FaultUtil.printFault(e);
                fail(e.getMessage());
            }

            // Test Find by Multiple
            try {
                HostCertificateFilter f = new HostCertificateFilter();
                String caSubject = ca.getCACertificate().getSubjectDN().getName();
                int caindex = caSubject.lastIndexOf(",");
                String caPreSub = caSubject.substring(0, caindex);
                f.setStatus(HostCertificateStatus.Active);
                f.setHost(hostPrefix);
                f.setOwner(OWNER);
                f.setSubject(caPreSub);
                assertEquals(5, hcm.findHostCertificates(f).size());
            } catch (Exception e) {
                FaultUtil.printFault(e);
                fail(e.getMessage());
            }

            // Update the owner and status

            for (int i = 0; i < total; i++) {
                long id = ids.get(i).longValue();
                String newOwner = "new";
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(id);
                update.setOwner(newOwner);
                update.setStatus(HostCertificateStatus.Suspended);
                hcm.updateHostCertificateRecord(update);
                HostCertificateFilter f = new HostCertificateFilter();
                f.setStatus(HostCertificateStatus.Suspended);
                f.setOwner(newOwner);
                assertEquals((i + 1), hcm.findHostCertificates(f).size());
                List<HostCertificateRecord> records = hcm.getHostCertificateRecords(newOwner);
                assertEquals((i + 1), records.size());
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testFindExpiredHostCertificates() {
        try {
            IdentityFederationProperties conf = getExpiringCredentialsConf();
            HostCertificateManager hcm = new HostCertificateManager(db, conf, ca, this, blackList);
            hcm.clearDatabase();
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Thread.currentThread().yield();
            long id1 = hcm.requestHostCertifcate(OWNER, getHostCertificateRequest("localhost1"));
            hcm.approveHostCertifcate(id1);
            long id2 = hcm.requestHostCertifcate(OWNER, getHostCertificateRequest("localhost2"));
            HostCertificateFilter f1 = new HostCertificateFilter();
            f1.setIsExpired(Boolean.TRUE);
            HostCertificateFilter f2 = new HostCertificateFilter();
            f2.setIsExpired(Boolean.FALSE);
            assertEquals(0, hcm.findHostCertificates(f1).size());
            assertEquals(1, hcm.findHostCertificates(f2).size());
            Thread.sleep((conf.getIssuedCertificateLifetime().getSeconds() * 1000) + 100);
            assertEquals(1, hcm.findHostCertificates(f1).size());
            assertEquals(0, hcm.findHostCertificates(f2).size());
            hcm.approveHostCertifcate(id2);
            assertEquals(1, hcm.findHostCertificates(f1).size());
            assertEquals(1, hcm.findHostCertificates(f2).size());
            Thread.sleep((conf.getIssuedCertificateLifetime().getSeconds() * 1000) + 100);
            assertEquals(2, hcm.findHostCertificates(f1).size());
            assertEquals(0, hcm.findHostCertificates(f2).size());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testCreateAndApproveHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testApproveActiveHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            try {
                hcm.approveHostCertifcate(id);
                fail("Should have failed.");
            } catch (InvalidHostCertificateFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testApproveRejectedHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);

            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setStatus(HostCertificateStatus.Rejected);
            hcm.updateHostCertificateRecord(update);
            try {
                hcm.approveHostCertifcate(id);
                fail("Should have failed.");
            } catch (InvalidHostCertificateFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testApproveSuspendedHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);

            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setStatus(HostCertificateStatus.Suspended);
            hcm.updateHostCertificateRecord(update);
            try {
                hcm.approveHostCertifcate(id);
                fail("Should have failed.");
            } catch (InvalidHostCertificateFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testApproveCompromisedHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);

            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setStatus(HostCertificateStatus.Compromised);
            hcm.updateHostCertificateRecord(update);
            try {
                hcm.approveHostCertifcate(id);
                fail("Should have failed.");
            } catch (InvalidHostCertificateFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testCreateDuplicateHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            try {
                hcm.requestHostCertifcate(OWNER, getHostCertificateRequest("localhost"));
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setStatus(HostCertificateStatus.Compromised);
            hcm.updateHostCertificateRecord(update);
            HostCertificateRequest req2 = getHostCertificateRequest("localhost");
            long id2 = hcm.requestHostCertifcate(OWNER, req2);
            hcm.approveHostCertifcate(id2);

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testCreateHostCertificateWithACompromisedKey() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setStatus(HostCertificateStatus.Compromised);
            hcm.updateHostCertificateRecord(update);
            try {
                req.setHostname("newhost");
                hcm.requestHostCertifcate(OWNER, req);
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

            hcm.requestHostCertifcate(OWNER, getHostCertificateRequest("newhost"));
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testCreateHostCertificateBadHostname() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            try {
                hcm.requestHostCertifcate(OWNER, getHostCertificateRequest(null));
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

            try {
                hcm.requestHostCertifcate(OWNER, getHostCertificateRequest(" "));
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testCreateHostCertificateInvalidPublicKey() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            try {
                HostCertificateRequest req = getHostCertificateRequest("localhost");
                req.setPublicKey(null);
                hcm.requestHostCertifcate(OWNER, req);
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

            try {
                HostCertificateRequest req = getHostCertificateRequest("localhost");
                req.getPublicKey().setKeyAsString(null);
                hcm.requestHostCertifcate(OWNER, req);
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

            try {
                HostCertificateRequest req = getHostCertificateRequest("localhost");
                req.getPublicKey().setKeyAsString(" ");
                hcm.requestHostCertifcate(OWNER, req);
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

            try {
                HostCertificateRequest req = getHostCertificateRequest("localhost");
                req.getPublicKey().setKeyAsString("foobar");
                hcm.requestHostCertifcate(OWNER, req);
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testCreateHostCertificateInvalidPublicKeySize() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            try {
                HostCertificateRequest req = getHostCertificateRequest("localhost", 512);
                hcm.requestHostCertifcate(OWNER, req);
                fail("Should have Failed!!");
            } catch (InvalidHostCertificateRequestFault f) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateHostCertificateStatusBeforeApproval() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);

            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setStatus(HostCertificateStatus.Suspended);

            try {
                hcm.updateHostCertificateRecord(update);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {

            }
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            hcm.updateHostCertificateRecord(update);
            assertEquals(HostCertificateStatus.Suspended, hcm.getHostCertificateRecord(id).getStatus());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateHostCertificateOwnerBeforeApproval() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            String newOwner = "newowner";
            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setOwner(newOwner);
            hcm.updateHostCertificateRecord(update);
            assertEquals(newOwner, hcm.getHostCertificateRecord(id).getOwner());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateHostCertificateOwner() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            String newOwner = "newowner";
            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setOwner(newOwner);
            hcm.updateHostCertificateRecord(update);
            assertEquals(newOwner, hcm.getHostCertificateRecord(id).getOwner());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateAllHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            String newOwner = "newowner";
            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setOwner(newOwner);
            update.setStatus(HostCertificateStatus.Suspended);
            hcm.updateHostCertificateRecord(update);
            HostCertificateRecord r = hcm.getHostCertificateRecord(id);
            assertEquals(newOwner, r.getOwner());
            assertEquals(HostCertificateStatus.Suspended, r.getStatus());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateNonExistingHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);

            try {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(100);
                update.setOwner("newowner");
                hcm.updateHostCertificateRecord(update);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateCompromisedHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            HostCertificateUpdate u = new HostCertificateUpdate();
            u.setId(id);
            u.setStatus(HostCertificateStatus.Compromised);
            hcm.updateHostCertificateRecord(u);
            try {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(id);
                update.setOwner("newowner");
                hcm.updateHostCertificateRecord(update);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateApprovedHostCertificateToPending() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            try {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(id);
                update.setStatus(HostCertificateStatus.Pending);
                hcm.updateHostCertificateRecord(update);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateHostCertificateStatus() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateRecord record = hcm.approveHostCertifcate(id);
            validateAfterCertificateApproval(hcm, id, OWNER, req, record);
            HostCertificateUpdate update = new HostCertificateUpdate();
            update.setId(id);
            update.setStatus(HostCertificateStatus.Suspended);
            hcm.updateHostCertificateRecord(update);
            assertEquals(HostCertificateStatus.Suspended, hcm.getHostCertificateRecord(id).getStatus());

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateRejectedHostCertificate() {
        try {
            HostCertificateManager hcm = new HostCertificateManager(db, getConf(), ca, this, blackList);
            hcm.clearDatabase();
            HostCertificateRequest req = getHostCertificateRequest("localhost");
            long id = hcm.requestHostCertifcate(OWNER, req);
            validateAfterCertificateRequest(hcm, req, id);
            HostCertificateUpdate u = new HostCertificateUpdate();
            u.setId(id);
            u.setStatus(HostCertificateStatus.Rejected);
            hcm.updateHostCertificateRecord(u);
            try {
                HostCertificateUpdate update = new HostCertificateUpdate();
                update.setId(id);
                update.setOwner("newowner");
                hcm.updateHostCertificateRecord(update);
                fail("Should have failed");
            } catch (InvalidHostCertificateFault f) {

            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    private HostCertificateRequest getHostCertificateRequest(String host) throws Exception {
        return getHostCertificateRequest(host, ca.getProperties().getIssuedCertificateKeySize());
    }


    private HostCertificateRequest getHostCertificateRequest(String host, int keySize) throws Exception {
        KeyPair pair = KeyUtil.generateRSAKeyPair(keySize);
        HostCertificateRequest req = new HostCertificateRequest();
        req.setHostname(host);
        String keyStr = KeyUtil.writePublicKey(pair.getPublic());
        PublicKey pk = new PublicKey();
        pk.setKeyAsString(keyStr);
        req.setPublicKey(pk);
        return req;
    }


    private void validateAfterCertificateRequest(HostCertificateManager hcm, HostCertificateRequest req, long id)
        throws Exception {
        validateAfterCertificateRequest(1, 1, hcm, OWNER, req, id);
    }


    private void validateAfterCertificateRequest(int count, int statusCount, HostCertificateManager hcm, String owner,
        HostCertificateRequest req, long id) throws Exception {
        validateFindHostCertificates(count, statusCount, hcm, id, -1, null, req.getHostname(), owner, req
            .getPublicKey(), HostCertificateStatus.Pending, "");
    }


    private void validateAfterCertificateApproval(HostCertificateManager hcm, long id, String owner,
        HostCertificateRequest req, HostCertificateRecord record) throws Exception {
        validateAfterCertificateApproval(1, 1, hcm, id, owner, req, record);
    }


    private void validateAfterCertificateApproval(int count, int statusCount, HostCertificateManager hcm, long id,
        String owner, HostCertificateRequest req, HostCertificateRecord record) throws Exception {
        assertEquals(req.getHostname(), record.getHost());
        assertEquals(req.getPublicKey(), record.getPublicKey());
        assertEquals(owner, record.getOwner());
        assertEquals(HostCertificateStatus.Active, record.getStatus());
        String subject = org.cagrid.gaards.dorian.service.util.Utils.getHostCertificateSubject(ca.getCACertificate(),
            req.getHostname());
        assertEquals(subject, record.getSubject());
        HostCertificateRecord r = hcm.getHostCertificateRecord(id);
        assertEquals(record.getPublicKey(), r.getPublicKey());
        assertEquals(record.getCertificate(), r.getCertificate());
        assertEquals(record.getSerialNumber(), r.getSerialNumber());
        assertEquals(record.getId(), r.getId());
        assertEquals(record.getOwner(), r.getOwner());
        assertEquals(record.getHost(), r.getHost());
        assertEquals(record.getStatus(), r.getStatus());
        assertEquals(record.getSubject(), r.getSubject());
        assertEquals(record, r);
        validateFindHostCertificates(count, statusCount, hcm, id, record.getSerialNumber(), record.getCertificate(),
            req.getHostname(), owner, req.getPublicKey(), HostCertificateStatus.Active, subject);
    }


    private void validateFindHostCertificates(int count, int statusCount, HostCertificateManager hcm, long id, long sn,
        X509Certificate cert, String host, String owner, PublicKey key, HostCertificateStatus status, String subject)
        throws Exception {

        List<HostCertificateRecord> l1 = hcm.findHostCertificates(null);
        assertEquals(count, l1.size());
        if (count == 1) {
            validateHostCertificateRecord(l1.get(count - 1), id, sn, cert, host, owner, key, status, subject);
        }

        if (count == 1) {
            List<HostCertificateRecord> l2 = hcm.findHostCertificates(new HostCertificateFilter());
            assertEquals(count, l2.size());
            validateHostCertificateRecord(l2.get(count - 1), id, sn, cert, host, owner, key, status, subject);
        }
        HostCertificateFilter f3 = new HostCertificateFilter();
        f3.setHost(host);
        List<HostCertificateRecord> l3 = hcm.findHostCertificates(f3);
        assertEquals(1, l3.size());
        validateHostCertificateRecord(l3.get(0), id, sn, cert, host, owner, key, status, subject);

        HostCertificateFilter f4 = new HostCertificateFilter();
        f4.setId(new BigInteger(String.valueOf(id)));
        List<HostCertificateRecord> l4 = hcm.findHostCertificates(f4);
        assertEquals(1, l4.size());
        validateHostCertificateRecord(l4.get(0), id, sn, cert, host, owner, key, status, subject);

        HostCertificateFilter f5 = new HostCertificateFilter();
        f5.setOwner(owner);
        List<HostCertificateRecord> l5 = hcm.findHostCertificates(f5);
        assertEquals(1, l5.size());
        validateHostCertificateRecord(l5.get(0), id, sn, cert, host, owner, key, status, subject);

        if (sn >= 0) {
            HostCertificateFilter f6 = new HostCertificateFilter();
            f6.setSerialNumber(new BigInteger(String.valueOf(sn)));
            List<HostCertificateRecord> l6 = hcm.findHostCertificates(f6);
            assertEquals(1, l6.size());
            validateHostCertificateRecord(l6.get(0), id, sn, cert, host, owner, key, status, subject);
        }

        HostCertificateFilter f7 = new HostCertificateFilter();
        f7.setStatus(status);
        List<HostCertificateRecord> l7 = hcm.findHostCertificates(f7);
        assertEquals(statusCount, l7.size());
        if (statusCount == 1) {
            validateHostCertificateRecord(l7.get(0), id, sn, cert, host, owner, key, status, subject);
        } else if (statusCount > 1) {
            f7.setId(new BigInteger(String.valueOf(id)));
            l7 = hcm.findHostCertificates(f7);
            assertEquals(1, l7.size());
            validateHostCertificateRecord(l7.get(0), id, sn, cert, host, owner, key, status, subject);
        }

        if (gov.nih.nci.cagrid.common.Utils.clean(subject) != null) {
            HostCertificateFilter f8 = new HostCertificateFilter();
            f8.setSubject(subject);
            List<HostCertificateRecord> l8 = hcm.findHostCertificates(f8);
            assertEquals(1, l8.size());
            validateHostCertificateRecord(l8.get(0), id, sn, cert, host, owner, key, status, subject);
        }
    }


    private void validateHostCertificateRecord(HostCertificateRecord record, long id, long sn, X509Certificate cert,
        String host, String owner, PublicKey key, HostCertificateStatus status, String subject) {
        assertEquals(id, record.getId());
        assertEquals(sn, record.getSerialNumber());
        assertEquals(cert, record.getCertificate());
        assertEquals(host, record.getHost());
        assertEquals(owner, record.getOwner());
        assertEquals(key, record.getPublicKey());
        assertEquals(status, record.getStatus());
        assertEquals(subject, record.getSubject());
    }


    public void setHostCertificateStatus(HostCertificateManager hcm, long id, HostCertificateStatus status)
        throws Exception {
        HostCertificateUpdate update = new HostCertificateUpdate();
        update.setId(id);
        update.setStatus(status);
        hcm.updateHostCertificateRecord(update);
    }


    private IdentityFederationProperties getConf() throws Exception {
        IdentityFederationProperties conf = Utils.getIdentityFederationProperties();
        return conf;
    }


    private IdentityFederationProperties getExpiringCredentialsConf() throws Exception {
        IdentityFederationProperties conf = Utils.getIdentityFederationProperties();
        Lifetime l = new Lifetime();
        l.setYears(0);
        l.setMonths(0);
        l.setDays(0);
        l.setHours(0);
        l.setMinutes(0);
        l.setSeconds(35);
        conf.setIssuedCertificateLifetime(l);
        return conf;
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            ca = Utils.getCA();
            blackList = new CertificateBlacklistManager(db);
            blackList.clearDatabase();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }


    protected void tearDown() throws Exception {
        super.setUp();
        try {
            ca.clearCertificateAuthority();
            blackList.clearDatabase();
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }
}
