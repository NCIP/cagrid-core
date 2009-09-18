package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.stubs.types.InvalidUserCertificateFault;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.tools.database.Database;


public class TestUserCertificateManager extends TestCase implements Publisher {

    private static final int DEFAULT_SECONDS = 5000;
    private static final int DEFAULT_SECONDS_OFFSET = 300;
    private Database db;
    private CertificateAuthority ca;
    private String caSubject;
    private int crlPublishCount;
    private List<BigInteger> crl;
    private UserCertificateManager man;
    private CertificateBlacklistManager blackList;


    public void testSingleUserCertificate() {
        try {

            String uid = "jdoe";
            X509Certificate cert = getAndValidateCertificate(uid, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(cert.getSubjectDN().getName()), cert);
            UserCertificateRecord r = getAndValidateCertificateRecord(cert);
            checkCRL(man.getCompromisedCertificates(), 0);
            Map<Long, UserCertificateRecord> expected = new HashMap<Long, UserCertificateRecord>();

            // Test empty filter
            UserCertificateFilter empty = new UserCertificateFilter();
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(empty, expected);

            // Test Serial Number
            UserCertificateFilter sn = new UserCertificateFilter();
            sn.setSerialNumber(new Long(r.getSerialNumber()));
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(sn, expected);
            expected.clear();
            sn.setSerialNumber(new Long(0));
            validateFind(sn, expected);

            // Test Grid Identity
            UserCertificateFilter gid = new UserCertificateFilter();
            gid.setGridIdentity(UserManager.subjectToIdentity(cert.getSubjectDN().getName()));
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(gid, expected);
            expected.clear();
            gid.setGridIdentity(UserManager.subjectToIdentity(cert.getSubjectDN().getName() + "2"));
            validateFind(gid, expected);

            // Test Status
            UserCertificateFilter status = new UserCertificateFilter();
            status.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(status, expected);
            expected.clear();
            status.setStatus(UserCertificateStatus.Compromised);
            validateFind(status, expected);

            // Test end outside date range
            UserCertificateFilter endOutside = new UserCertificateFilter();
            DateRange endOutsideRange = new DateRange();
            Calendar endOutsideStart = new GregorianCalendar();
            endOutsideStart.setTime(cert.getNotBefore());
            endOutsideStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET));
            endOutsideRange.setStartDate(endOutsideStart);
            Calendar endOutsideEnd = new GregorianCalendar();
            endOutsideEnd.setTime(cert.getNotAfter());
            endOutsideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET));
            endOutsideRange.setEndDate(endOutsideEnd);
            endOutside.setDateRange(endOutsideRange);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(endOutside, expected);

            // Test start outside date range
            UserCertificateFilter startOutside = new UserCertificateFilter();
            DateRange startOutsideRange = new DateRange();
            Calendar startOutsideStart = new GregorianCalendar();
            startOutsideStart.setTime(cert.getNotBefore());
            startOutsideStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
            startOutsideRange.setStartDate(startOutsideStart);
            Calendar startOutsideEnd = new GregorianCalendar();
            startOutsideEnd.setTime(cert.getNotAfter());
            startOutsideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
            startOutsideRange.setEndDate(startOutsideEnd);
            startOutside.setDateRange(startOutsideRange);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(startOutside, expected);

            // Test outside date range
            UserCertificateFilter outside = new UserCertificateFilter();
            DateRange outsideRange = new DateRange();
            Calendar outsideStart = new GregorianCalendar();
            outsideStart.setTime(cert.getNotBefore());
            outsideStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
            outsideRange.setStartDate(outsideStart);
            Calendar outsideEnd = new GregorianCalendar();
            outsideEnd.setTime(cert.getNotAfter());
            outsideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET));
            outsideRange.setEndDate(outsideEnd);
            outside.setDateRange(outsideRange);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(outside, expected);

            // Test inside date range
            UserCertificateFilter inside = new UserCertificateFilter();
            DateRange insideRange = new DateRange();
            Calendar insideStart = new GregorianCalendar();
            insideStart.setTime(cert.getNotBefore());
            insideStart.add(Calendar.SECOND, DEFAULT_SECONDS_OFFSET);
            insideRange.setStartDate(insideStart);
            Calendar insideEnd = new GregorianCalendar();
            insideEnd.setTime(cert.getNotAfter());
            insideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
            insideRange.setEndDate(insideEnd);
            inside.setDateRange(insideRange);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(inside, expected);

            // Test exact date range
            UserCertificateFilter exactDates = new UserCertificateFilter();
            DateRange range = new DateRange();
            GregorianCalendar start = new GregorianCalendar();
            start.setTime(cert.getNotBefore());
            range.setStartDate(start);
            GregorianCalendar end = new GregorianCalendar();
            end.setTime(cert.getNotAfter());
            range.setEndDate(end);
            exactDates.setDateRange(range);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(exactDates, expected);

            // Test invalid start

            UserCertificateFilter startInvalid = new UserCertificateFilter();
            DateRange startInvalidRange = new DateRange();
            Calendar startInvalidStart = new GregorianCalendar();
            startInvalidStart.setTime(cert.getNotBefore());
            startInvalidStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -2));
            startInvalidRange.setStartDate(startInvalidStart);
            Calendar startInvalidEnd = new GregorianCalendar();
            startInvalidEnd.setTime(cert.getNotAfter());
            startInvalidEnd.add(Calendar.SECOND, ((DEFAULT_SECONDS + DEFAULT_SECONDS_OFFSET) * -1));
            startInvalidRange.setEndDate(startInvalidEnd);
            startInvalid.setDateRange(startInvalidRange);
            expected.clear();
            validateFind(startInvalid, expected);

            // Test invalid end

            UserCertificateFilter endInvalid = new UserCertificateFilter();
            DateRange endInvalidRange = new DateRange();
            Calendar endInvalidStart = new GregorianCalendar();
            endInvalidStart.setTime(cert.getNotBefore());
            endInvalidStart.add(Calendar.SECOND, ((DEFAULT_SECONDS + DEFAULT_SECONDS_OFFSET) * 1));
            endInvalidRange.setStartDate(endInvalidStart);
            Calendar endInvalidEnd = new GregorianCalendar();
            endInvalidEnd.setTime(cert.getNotAfter());
            endInvalidEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * 2));
            endInvalidRange.setEndDate(endInvalidEnd);
            endInvalid.setDateRange(endInvalidRange);
            expected.clear();
            validateFind(endInvalid, expected);

            // Test update
            String msg = "Hello World";
            UserCertificateUpdate u = new UserCertificateUpdate();
            u.setSerialNumber(cert.getSerialNumber().longValue());
            u.setStatus(UserCertificateStatus.Compromised);
            u.setNotes(msg);
            man.updateUserCertificateRecord(u);

            r.setStatus(UserCertificateStatus.Compromised);
            r.setNotes(msg);

            // Test Update Results
            UserCertificateFilter update = new UserCertificateFilter();
            update.setStatus(UserCertificateStatus.OK);
            update.setNotes(msg);
            expected.clear();
            validateFind(update, expected);
            expected.clear();
            update.setStatus(UserCertificateStatus.Compromised);
            update.setNotes(msg);

            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(update, expected);

            checkCRL(man.getCompromisedCertificates(), 1);

            // Test All
            UserCertificateFilter all = new UserCertificateFilter();
            all.setSerialNumber(new Long(r.getSerialNumber()));
            all.setGridIdentity(UserManager.subjectToIdentity(cert.getSubjectDN().getName()));
            all.setStatus(UserCertificateStatus.Compromised);
            all.setNotes(msg);
            all.setDateRange(insideRange);
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(all, expected);

            man.removeCertificate(r.getSerialNumber());
            assertEquals(false, man.determineIfRecordExistBySerialNumber(r.getSerialNumber()));
            assertTrue(blackList.memberOfBlackList(r.getSerialNumber()));
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testMultipleUserCertificates() {
        try {

            int count = 3;

            List<UserCertificateRecord> records = new ArrayList<UserCertificateRecord>();
            for (int i = 0; i < count; i++) {
                String uid = "jdoe" + i;
                X509Certificate cert = getAndValidateCertificate(uid, DEFAULT_SECONDS);
                man.addUserCertifcate(UserManager.subjectToIdentity(cert.getSubjectDN().getName()), cert);
                UserCertificateRecord r = getAndValidateCertificateRecord(cert);
                records.add(r);

                checkCRL(man.getCompromisedCertificates(), i);

                Map<Long, UserCertificateRecord> expected = new HashMap<Long, UserCertificateRecord>();

                // Test empty filter
                UserCertificateFilter empty = new UserCertificateFilter();
                expected.clear();
                for (int j = 0; j < records.size(); j++) {
                    expected.put(records.get(j).getSerialNumber(), records.get(j));
                }
                validateFind(empty, expected);

                // Test Serial Number
                UserCertificateFilter sn = new UserCertificateFilter();
                sn.setSerialNumber(new Long(r.getSerialNumber()));
                expected.clear();
                expected.put(new Long(r.getSerialNumber()), r);
                validateFind(sn, expected);
                expected.clear();
                sn.setSerialNumber(new Long(0));
                validateFind(sn, expected);

                // Test Grid Identity
                UserCertificateFilter gid = new UserCertificateFilter();
                gid.setGridIdentity(UserManager.subjectToIdentity(cert.getSubjectDN().getName()));
                expected.clear();
                expected.put(new Long(r.getSerialNumber()), r);
                validateFind(gid, expected);
                expected.clear();
                gid.setGridIdentity(UserManager.subjectToIdentity(cert.getSubjectDN().getName() + "2"));
                validateFind(gid, expected);

                // Test Status
                UserCertificateFilter status = new UserCertificateFilter();
                status.setStatus(UserCertificateStatus.OK);
                expected.clear();
                expected.put(new Long(r.getSerialNumber()), r);
                validateFind(status, expected);
                expected.clear();

                for (int j = 0; j < i; j++) {
                    expected.put(records.get(j).getSerialNumber(), records.get(j));
                }

                status.setStatus(UserCertificateStatus.Compromised);
                validateFind(status, expected);

                // Test end outside date range
                UserCertificateFilter endOutside = new UserCertificateFilter();
                DateRange endOutsideRange = new DateRange();
                Calendar endOutsideStart = new GregorianCalendar();
                endOutsideStart.setTime(cert.getNotBefore());
                endOutsideStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET));
                endOutsideRange.setStartDate(endOutsideStart);
                Calendar endOutsideEnd = new GregorianCalendar();
                endOutsideEnd.setTime(cert.getNotAfter());
                endOutsideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET));
                endOutsideRange.setEndDate(endOutsideEnd);
                endOutside.setDateRange(endOutsideRange);
                expected.clear();
                for (int j = 0; j < records.size(); j++) {
                    expected.put(records.get(j).getSerialNumber(), records.get(j));
                }
                validateFind(endOutside, expected);

                // Test start outside date range
                UserCertificateFilter startOutside = new UserCertificateFilter();
                DateRange startOutsideRange = new DateRange();
                Calendar startOutsideStart = new GregorianCalendar();
                startOutsideStart.setTime(cert.getNotBefore());
                startOutsideStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
                startOutsideRange.setStartDate(startOutsideStart);
                Calendar startOutsideEnd = new GregorianCalendar();
                startOutsideEnd.setTime(cert.getNotAfter());
                startOutsideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
                startOutsideRange.setEndDate(startOutsideEnd);
                startOutside.setDateRange(startOutsideRange);
                expected.clear();
                for (int j = 0; j < records.size(); j++) {
                    expected.put(records.get(j).getSerialNumber(), records.get(j));
                }
                validateFind(startOutside, expected);

                // Test outside date range
                UserCertificateFilter outside = new UserCertificateFilter();
                DateRange outsideRange = new DateRange();
                Calendar outsideStart = new GregorianCalendar();
                outsideStart.setTime(cert.getNotBefore());
                outsideStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
                outsideRange.setStartDate(outsideStart);
                Calendar outsideEnd = new GregorianCalendar();
                outsideEnd.setTime(cert.getNotAfter());
                outsideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET));
                outsideRange.setEndDate(outsideEnd);
                outside.setDateRange(outsideRange);
                expected.clear();
                for (int j = 0; j < records.size(); j++) {
                    expected.put(records.get(j).getSerialNumber(), records.get(j));
                }
                validateFind(outside, expected);

                // Test inside date range
                UserCertificateFilter inside = new UserCertificateFilter();
                DateRange insideRange = new DateRange();
                Calendar insideStart = new GregorianCalendar();
                insideStart.setTime(cert.getNotBefore());
                insideStart.add(Calendar.SECOND, DEFAULT_SECONDS_OFFSET);
                insideRange.setStartDate(insideStart);
                Calendar insideEnd = new GregorianCalendar();
                insideEnd.setTime(cert.getNotAfter());
                insideEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -1));
                insideRange.setEndDate(insideEnd);
                inside.setDateRange(insideRange);
                expected.clear();
                for (int j = 0; j < records.size(); j++) {
                    expected.put(records.get(j).getSerialNumber(), records.get(j));
                }
                validateFind(inside, expected);

                // Test exact date range
                UserCertificateFilter exactDates = new UserCertificateFilter();
                DateRange range = new DateRange();
                GregorianCalendar start = new GregorianCalendar();
                start.setTime(cert.getNotBefore());
                range.setStartDate(start);
                GregorianCalendar end = new GregorianCalendar();
                end.setTime(cert.getNotAfter());
                range.setEndDate(end);
                exactDates.setDateRange(range);
                expected.clear();
                for (int j = 0; j < records.size(); j++) {
                    expected.put(records.get(j).getSerialNumber(), records.get(j));
                }
                validateFind(exactDates, expected);

                // Test invalid start

                UserCertificateFilter startInvalid = new UserCertificateFilter();
                DateRange startInvalidRange = new DateRange();
                Calendar startInvalidStart = new GregorianCalendar();
                startInvalidStart.setTime(cert.getNotBefore());
                startInvalidStart.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * -2));
                startInvalidRange.setStartDate(startInvalidStart);
                Calendar startInvalidEnd = new GregorianCalendar();
                startInvalidEnd.setTime(cert.getNotAfter());
                startInvalidEnd.add(Calendar.SECOND, ((DEFAULT_SECONDS + DEFAULT_SECONDS_OFFSET) * -1));
                startInvalidRange.setEndDate(startInvalidEnd);
                startInvalid.setDateRange(startInvalidRange);
                expected.clear();
                validateFind(startInvalid, expected);

                // Test invalid end

                UserCertificateFilter endInvalid = new UserCertificateFilter();
                DateRange endInvalidRange = new DateRange();
                Calendar endInvalidStart = new GregorianCalendar();
                endInvalidStart.setTime(cert.getNotBefore());
                endInvalidStart.add(Calendar.SECOND, ((DEFAULT_SECONDS + DEFAULT_SECONDS_OFFSET) * 1));
                endInvalidRange.setStartDate(endInvalidStart);
                Calendar endInvalidEnd = new GregorianCalendar();
                endInvalidEnd.setTime(cert.getNotAfter());
                endInvalidEnd.add(Calendar.SECOND, (DEFAULT_SECONDS_OFFSET * 2));
                endInvalidRange.setEndDate(endInvalidEnd);
                endInvalid.setDateRange(endInvalidRange);
                expected.clear();
                validateFind(endInvalid, expected);

                // Test update
                String msg = "Hello " + i;
                UserCertificateUpdate u = new UserCertificateUpdate();
                u.setSerialNumber(cert.getSerialNumber().longValue());
                u.setStatus(UserCertificateStatus.Compromised);
                u.setNotes(msg);
                man.updateUserCertificateRecord(u);

                r.setStatus(UserCertificateStatus.Compromised);
                r.setNotes(msg);

                // Test Update Results
                UserCertificateFilter update = new UserCertificateFilter();
                update.setStatus(UserCertificateStatus.OK);
                update.setNotes(msg);
                expected.clear();
                validateFind(update, expected);
                expected.clear();
                update.setStatus(UserCertificateStatus.Compromised);
                update.setNotes(msg);

                expected.put(new Long(r.getSerialNumber()), r);
                validateFind(update, expected);

                checkCRL(man.getCompromisedCertificates(), (i + 1));

                // Test All
                UserCertificateFilter all = new UserCertificateFilter();
                all.setSerialNumber(new Long(r.getSerialNumber()));
                all.setGridIdentity(UserManager.subjectToIdentity(cert.getSubjectDN().getName()));
                all.setStatus(UserCertificateStatus.Compromised);
                all.setNotes(msg);
                all.setDateRange(insideRange);
                expected.put(new Long(r.getSerialNumber()), r);
                validateFind(all, expected);

            }
            for (int i = 0; i < count; i++) {
                UserCertificateRecord r = records.get(i);
                man.removeCertificates(r.getGridIdentity());
                assertEquals(false, man.determineIfRecordExistBySerialNumber(r.getSerialNumber()));
                assertTrue(blackList.memberOfBlackList(r.getSerialNumber()));
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testUpdateNonExistingCertificate() {
        try {
            checkCRL(man.getCompromisedCertificates(), 0);
            UserCertificateUpdate update = new UserCertificateUpdate();
            update.setSerialNumber(1);
            update.setStatus(UserCertificateStatus.Compromised);
            try {
                man.updateUserCertificateRecord(update);
                fail("Should not be able to update a certificate that does not exist.");
            } catch (InvalidUserCertificateFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserCertificateManager.CANNOT_UPDATE_CERT_DOES_NOT_EXIST_ERROR)) {
                    fail("Should not be able to update a certificate that does not exist.");
                }
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }

    }


    public void testUpdateCompromisedCertificate() {
        try {
            String uid = "jdoe";
            X509Certificate cert = getAndValidateCertificate(uid, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(cert.getSubjectDN().getName()), cert);
            UserCertificateRecord r = getAndValidateCertificateRecord(cert);
            checkCRL(man.getCompromisedCertificates(), 0);
            Map<Long, UserCertificateRecord> expected = new HashMap<Long, UserCertificateRecord>();

            UserCertificateFilter sn = new UserCertificateFilter();
            sn.setSerialNumber(new Long(r.getSerialNumber()));
            sn.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(sn, expected);
            expected.clear();
            sn.setSerialNumber(new Long(0));
            validateFind(sn, expected);

            // Test update
            UserCertificateUpdate u = new UserCertificateUpdate();
            u.setSerialNumber(cert.getSerialNumber().longValue());
            u.setStatus(UserCertificateStatus.Compromised);
            man.updateUserCertificateRecord(u);

            r.setStatus(UserCertificateStatus.Compromised);

            // Test Update Results
            UserCertificateFilter update = new UserCertificateFilter();
            update.setStatus(UserCertificateStatus.OK);
            expected.clear();
            validateFind(update, expected);
            expected.clear();
            update.setStatus(UserCertificateStatus.Compromised);
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(update, expected);

            checkCRL(man.getCompromisedCertificates(), 1);

            UserCertificateUpdate u2 = new UserCertificateUpdate();
            u2.setSerialNumber(cert.getSerialNumber().longValue());
            u2.setStatus(UserCertificateStatus.OK);
            try {
                man.updateUserCertificateRecord(u2);
                fail("Should not be able to change the status of a compromised certificate.");
            } catch (InvalidUserCertificateFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserCertificateManager.CANNOT_UPDATE_STATUS_IF_COMPROMISED_ERROR)) {
                    fail("Should not be able to change the status of a compromised certificate.");
                }
            }

            checkCRL(man.getCompromisedCertificates(), 1);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    public void testActiveCertificates() {

        try {
            String uid = "jdoe";

            Map<Long, UserCertificateRecord> expected = new HashMap<Long, UserCertificateRecord>();

            GregorianCalendar now = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            end.add(Calendar.YEAR, 5);
            DateRange range = new DateRange();
            range.setStartDate(now);
            range.setEndDate(end);

            // Create expired certificate

            X509Certificate expiredCert = getAndValidateCertificate(uid, DEFAULT_SECONDS, (DEFAULT_SECONDS * -2));
            man.addUserCertifcate(UserManager.subjectToIdentity(expiredCert.getSubjectDN().getName()), expiredCert);
            UserCertificateRecord expired = getAndValidateCertificateRecord(expiredCert);
            UserCertificateFilter expiredFilter = new UserCertificateFilter();
            expiredFilter.setSerialNumber(expired.getSerialNumber());
            expiredFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(expired.getSerialNumber()), expired);
            validateFind(expiredFilter, expected);
            expiredFilter.setDateRange(range);
            expected.clear();
            validateFind(expiredFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 0);

            // Create expired compromised certificate

            X509Certificate expiredCompromisedCert = getAndValidateCertificate(uid, DEFAULT_SECONDS,
                (DEFAULT_SECONDS * -2));
            man.addUserCertifcate(UserManager.subjectToIdentity(expiredCompromisedCert.getSubjectDN().getName()),
                expiredCompromisedCert);
            UserCertificateRecord expiredCompromised = getAndValidateCertificateRecord(expiredCompromisedCert);
            UserCertificateFilter expiredCompromisedFilter = new UserCertificateFilter();
            expiredCompromisedFilter.setSerialNumber(expiredCompromised.getSerialNumber());
            expiredCompromisedFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(expiredCompromised.getSerialNumber()), expiredCompromised);
            validateFind(expiredCompromisedFilter, expected);
            expiredCompromisedFilter.setDateRange(range);
            expected.clear();
            validateFind(expiredCompromisedFilter, expected);
            UserCertificateUpdate expiredCompromisedUpdate = new UserCertificateUpdate();
            expiredCompromisedUpdate.setSerialNumber(expiredCompromised.getSerialNumber());
            expiredCompromisedUpdate.setStatus(UserCertificateStatus.Compromised);
            man.updateUserCertificateRecord(expiredCompromisedUpdate);
            expiredCompromised.setStatus(UserCertificateStatus.Compromised);
            expiredCompromisedFilter.setStatus(UserCertificateStatus.Compromised);
            expiredCompromisedFilter.setDateRange(null);
            expected.clear();
            expected.put(new Long(expiredCompromised.getSerialNumber()), expiredCompromised);
            validateFind(expiredCompromisedFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 1);

            // Create Active certificate

            X509Certificate activeCert = getAndValidateCertificate(uid, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(activeCert.getSubjectDN().getName()), activeCert);
            UserCertificateRecord active = getAndValidateCertificateRecord(activeCert);
            UserCertificateFilter activeFilter = new UserCertificateFilter();
            activeFilter.setSerialNumber(active.getSerialNumber());
            activeFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(active.getSerialNumber()), active);
            validateFind(activeFilter, expected);
            activeFilter.setDateRange(range);
            expected.clear();
            expected.put(new Long(active.getSerialNumber()), active);
            validateFind(activeFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 1);

            // Create Active certificate of a different user
            String uid2 = "jane";
            X509Certificate activeCert2 = getAndValidateCertificate(uid2, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(activeCert2.getSubjectDN().getName()), activeCert2);
            UserCertificateRecord active2 = getAndValidateCertificateRecord(activeCert2);
            UserCertificateFilter activeFilter2 = new UserCertificateFilter();
            activeFilter2.setSerialNumber(active2.getSerialNumber());
            activeFilter2.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(active2.getSerialNumber()), active2);
            validateFind(activeFilter2, expected);
            activeFilter2.setDateRange(range);
            expected.clear();
            expected.put(new Long(active2.getSerialNumber()), active2);
            validateFind(activeFilter2, expected);

            checkCRL(man.getCompromisedCertificates(), 1);

            // Create Active Compromised Certificate

            X509Certificate activeCompromisedCert = getAndValidateCertificate(uid, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(activeCompromisedCert.getSubjectDN().getName()),
                activeCompromisedCert);
            UserCertificateRecord activeCompromised = getAndValidateCertificateRecord(activeCompromisedCert);
            UserCertificateFilter activeCompromisedFilter = new UserCertificateFilter();
            activeCompromisedFilter.setSerialNumber(activeCompromised.getSerialNumber());
            activeCompromisedFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(activeCompromised.getSerialNumber()), activeCompromised);
            validateFind(activeCompromisedFilter, expected);
            activeCompromisedFilter.setDateRange(range);
            expected.clear();
            expected.put(new Long(activeCompromised.getSerialNumber()), activeCompromised);
            validateFind(activeCompromisedFilter, expected);
            UserCertificateUpdate update = new UserCertificateUpdate();
            update.setSerialNumber(activeCompromised.getSerialNumber());
            update.setStatus(UserCertificateStatus.Compromised);
            man.updateUserCertificateRecord(update);
            activeCompromised.setStatus(UserCertificateStatus.Compromised);
            activeCompromisedFilter.setStatus(UserCertificateStatus.Compromised);
            expected.clear();
            expected.put(new Long(activeCompromised.getSerialNumber()), activeCompromised);
            validateFind(activeCompromisedFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 2);

            List<BigInteger> activeResults = man.getActiveCertificates(UserManager
                .subjectToIdentity(expiredCompromisedCert.getSubjectDN().getName()));
            List<BigInteger> expectedResults = new ArrayList<BigInteger>();
            expectedResults.add(activeCert.getSerialNumber());
            validateExpectedSerialNumbers(expectedResults, activeResults);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }

    }


    public void testCompromisedCertificates() {
        try {

            String uid = "john";

            Map<Long, UserCertificateRecord> expected = new HashMap<Long, UserCertificateRecord>();

            GregorianCalendar now = new GregorianCalendar();
            Calendar end = new GregorianCalendar();
            end.add(Calendar.YEAR, 5);
            DateRange range = new DateRange();
            range.setStartDate(now);
            range.setEndDate(end);

            // Create expired certificate

            X509Certificate expiredCert = getAndValidateCertificate(uid, DEFAULT_SECONDS, (DEFAULT_SECONDS * -2));
            man.addUserCertifcate(UserManager.subjectToIdentity(expiredCert.getSubjectDN().getName()), expiredCert);
            UserCertificateRecord expired = getAndValidateCertificateRecord(expiredCert);
            UserCertificateFilter expiredFilter = new UserCertificateFilter();
            expiredFilter.setSerialNumber(expired.getSerialNumber());
            expiredFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(expired.getSerialNumber()), expired);
            validateFind(expiredFilter, expected);
            expiredFilter.setDateRange(range);
            expected.clear();
            validateFind(expiredFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 0);

            // Create expired compromised certificate

            X509Certificate expiredCompromisedCert = getAndValidateCertificate(uid, DEFAULT_SECONDS,
                (DEFAULT_SECONDS * -2));
            man.addUserCertifcate(UserManager.subjectToIdentity(expiredCompromisedCert.getSubjectDN().getName()),
                expiredCompromisedCert);
            UserCertificateRecord expiredCompromised = getAndValidateCertificateRecord(expiredCompromisedCert);
            UserCertificateFilter expiredCompromisedFilter = new UserCertificateFilter();
            expiredCompromisedFilter.setSerialNumber(expiredCompromised.getSerialNumber());
            expiredCompromisedFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(expiredCompromised.getSerialNumber()), expiredCompromised);
            validateFind(expiredCompromisedFilter, expected);
            expiredCompromisedFilter.setDateRange(range);
            expected.clear();
            validateFind(expiredCompromisedFilter, expected);
            UserCertificateUpdate expiredCompromisedUpdate = new UserCertificateUpdate();
            expiredCompromisedUpdate.setSerialNumber(expiredCompromised.getSerialNumber());
            expiredCompromisedUpdate.setStatus(UserCertificateStatus.Compromised);
            man.updateUserCertificateRecord(expiredCompromisedUpdate);
            expiredCompromised.setStatus(UserCertificateStatus.Compromised);
            expiredCompromisedFilter.setStatus(UserCertificateStatus.Compromised);
            expiredCompromisedFilter.setDateRange(null);
            expected.clear();
            expected.put(new Long(expiredCompromised.getSerialNumber()), expiredCompromised);
            validateFind(expiredCompromisedFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 1);

            String uid2 = "jane";

            // Create Active certificate

            X509Certificate activeCert = getAndValidateCertificate(uid2, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(activeCert.getSubjectDN().getName()), activeCert);
            UserCertificateRecord active = getAndValidateCertificateRecord(activeCert);
            UserCertificateFilter activeFilter = new UserCertificateFilter();
            activeFilter.setSerialNumber(active.getSerialNumber());
            activeFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(active.getSerialNumber()), active);
            validateFind(activeFilter, expected);
            activeFilter.setDateRange(range);
            expected.clear();
            expected.put(new Long(active.getSerialNumber()), active);
            validateFind(activeFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 1);

            // Create Active Compromised Certificate

            X509Certificate activeCompromisedCert = getAndValidateCertificate(uid2, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(activeCompromisedCert.getSubjectDN().getName()),
                activeCompromisedCert);
            UserCertificateRecord activeCompromised = getAndValidateCertificateRecord(activeCompromisedCert);
            UserCertificateFilter activeCompromisedFilter = new UserCertificateFilter();
            activeCompromisedFilter.setSerialNumber(activeCompromised.getSerialNumber());
            activeCompromisedFilter.setStatus(UserCertificateStatus.OK);
            expected.clear();
            expected.put(new Long(activeCompromised.getSerialNumber()), activeCompromised);
            validateFind(activeCompromisedFilter, expected);
            activeCompromisedFilter.setDateRange(range);
            expected.clear();
            expected.put(new Long(activeCompromised.getSerialNumber()), activeCompromised);
            validateFind(activeCompromisedFilter, expected);
            UserCertificateUpdate update = new UserCertificateUpdate();
            update.setSerialNumber(activeCompromised.getSerialNumber());
            update.setStatus(UserCertificateStatus.Compromised);
            man.updateUserCertificateRecord(update);
            activeCompromised.setStatus(UserCertificateStatus.Compromised);
            activeCompromisedFilter.setStatus(UserCertificateStatus.Compromised);
            expected.clear();
            expected.put(new Long(activeCompromised.getSerialNumber()), activeCompromised);
            validateFind(activeCompromisedFilter, expected);

            checkCRL(man.getCompromisedCertificates(), 2);

            List<BigInteger> compromisedResults = man.getCompromisedCertificates();
            List<BigInteger> expectedResults = new ArrayList<BigInteger>();
            expectedResults.add(activeCompromisedCert.getSerialNumber());
            expectedResults.add(expiredCompromisedCert.getSerialNumber());
            validateExpectedSerialNumbers(expectedResults, compromisedResults);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }

    }


    public void testFindUserCertificatesInvalidRange() {
        try {
            String uid = "jdoe";
            X509Certificate cert = getAndValidateCertificate(uid, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(cert.getSubjectDN().getName()), cert);
            UserCertificateRecord r = getAndValidateCertificateRecord(cert);
            Map<Long, UserCertificateRecord> expected = new HashMap<Long, UserCertificateRecord>();

            // Test Serial Number
            UserCertificateFilter sn = new UserCertificateFilter();
            sn.setSerialNumber(new Long(r.getSerialNumber()));
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(sn, expected);
            expected.clear();
            sn.setSerialNumber(new Long(0));
            validateFind(sn, expected);

            // Test exact date range
            UserCertificateFilter exactDates = new UserCertificateFilter();
            DateRange range = new DateRange();
            exactDates.setDateRange(range);

            GregorianCalendar start = new GregorianCalendar();
            start.setTime(cert.getNotBefore());
            GregorianCalendar end = new GregorianCalendar();
            end.setTime(cert.getNotAfter());

            try {
                man.findUserCertificateRecords(exactDates);
                fail("Should not be able to find user certificate records with an invalid range.");
            } catch (InvalidUserCertificateFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserCertificateManager.FIND_INVALID_RANGE_NO_START_ERROR)) {
                    fail("Should not be able to find user certificate records with an invalid range.");
                }
            }

            range.setStartDate(start);
            range.setEndDate(null);
            try {
                man.findUserCertificateRecords(exactDates);
                fail("Should not be able to find user certificate records with an invalid range.");
            } catch (InvalidUserCertificateFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserCertificateManager.FIND_INVALID_RANGE_NO_END_ERROR)) {
                    fail("Should not be able to find user certificate records with an invalid range.");
                }
            }

            range.setStartDate(null);
            range.setEndDate(end);
            try {
                man.findUserCertificateRecords(exactDates);
                fail("Should not be able to find user certificate records with an invalid range.");
            } catch (InvalidUserCertificateFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserCertificateManager.FIND_INVALID_RANGE_NO_START_ERROR)) {
                    fail("Should not be able to find user certificate records with an invalid range.");
                }
            }

            range.setStartDate(end);
            range.setEndDate(start);
            try {
                man.findUserCertificateRecords(exactDates);
                fail("Should not be able to find user certificate records with an invalid range.");
            } catch (InvalidUserCertificateFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserCertificateManager.FIND_INVALID_RANGE_ERROR)) {
                    fail("Should not be able to find user certificate records with an invalid range.");
                }
            }

            range.setStartDate(start);
            range.setEndDate(end);
            expected.clear();
            expected.put(new Long(r.getSerialNumber()), r);
            validateFind(exactDates, expected);

            // Test all
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }

    }


    public void testSingleUserCertificateDuplicateEntries() {
        try {
            String uid = "jdoe";
            X509Certificate cert = getAndValidateCertificate(uid, DEFAULT_SECONDS);
            man.addUserCertifcate(UserManager.subjectToIdentity(cert.getSubjectDN().getName()), cert);
            getAndValidateCertificateRecord(cert);

            try {
                man.addUserCertifcate(UserManager.subjectToIdentity(cert.getSubjectDN().getName()), cert);
                fail("Should not be able to add existing user certificate!!!");
            } catch (InvalidUserCertificateFault e) {
                if (!gov.nih.nci.cagrid.common.Utils.getExceptionMessage(e).equals(
                    UserCertificateManager.USER_CERTIFICATE_ALREADY_EXISTS_ERROR)) {
                    fail("Should not be able to add existing user certificate!!!");
                }
            }
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    private void validateExpectedSerialNumbers(List<BigInteger> expected, List<BigInteger> actual) throws Exception {

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            boolean found = false;
            for (int j = 0; j < actual.size(); j++) {
                if (expected.get(i).equals(actual.get(j))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                fail("The user certificate record " + expected.get(i) + " was expected but was not found.");
            }
        }
    }


    private void validateFind(UserCertificateFilter f, Map<Long, UserCertificateRecord> expected) throws Exception {
        List<UserCertificateRecord> results = man.findUserCertificateRecords(f);
        assertEquals(expected.size(), results.size());
        for (int i = 0; i < results.size(); i++) {
            Long sn = new Long(results.get(i).getSerialNumber());
            if (expected.containsKey(sn)) {
                assertEquals(expected.get(sn), results.remove(i));
            } else {
                fail("The user certificate record " + sn + " but was not expected.");
            }
        }
    }


    private UserCertificateRecord getAndValidateCertificateRecord(X509Certificate cert) throws Exception {
        assertTrue(man.determineIfRecordExistBySerialNumber(cert.getSerialNumber().longValue()));
        UserCertificateRecord record = man.getUserCertificateRecord(cert.getSerialNumber().longValue());
        assertNotNull(record);
        assertEquals(UserManager.subjectToIdentity(cert.getSubjectDN().getName()), record.getGridIdentity());
        assertEquals("", record.getNotes());
        assertEquals(cert.getSerialNumber().longValue(), record.getSerialNumber());
        assertEquals(UserCertificateStatus.OK, record.getStatus());
        X509Certificate cert2 = CertUtil.loadCertificate(record.getCertificate().getCertificateAsString());
        assertEquals(cert, cert2);
        return record;
    }


    private X509Certificate getAndValidateCertificate(String uid, int seconds) throws Exception {
        return getAndValidateCertificate(uid, seconds, 0);
    }


    private X509Certificate getAndValidateCertificate(String uid, int seconds, int timeOffset) throws Exception {
        String subject = getUserSubject(uid);
        Calendar c = new GregorianCalendar();
        c.add(Calendar.SECOND, timeOffset);
        Date start = c.getTime();
        c.add(Calendar.SECOND, seconds);
        Date end = c.getTime();
        X509Certificate cert = getCertificate(subject, start, end);
        assertEquals(subject, cert.getSubjectDN().getName());
        return cert;
    }


    private String getUserSubject(String uid) {
        int caindex = caSubject.lastIndexOf(",");
        String caPreSub = caSubject.substring(0, caindex);
        return caPreSub + ",OU=" + 123 + ",CN=" + uid;
    }


    private X509Certificate getCertificate(String subject, Date start, Date end) throws Exception {
        KeyPair pair = KeyUtil.generateRSAKeyPair1024();
        return ca.signCertificate(subject, pair.getPublic(), start, end);
    }


    public void checkCRL(List<BigInteger> expected, int expectedCount) {
        assertEquals(expectedCount, this.crlPublishCount);
        assertEquals(expected, this.crl);
    }


    public void publishCRL() {
        try {
            this.crlPublishCount = this.crlPublishCount + 1;
            this.crl = man.getCompromisedCertificates();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            ca = Utils.getCA();
            caSubject = ca.getCACertificate().getSubjectDN().getName();
            blackList = new CertificateBlacklistManager(db);
            blackList.clearDatabase();
            man = new UserCertificateManager(db, this, blackList);
            man.clearDatabase();
            this.crlPublishCount = 0;
            this.crl = new ArrayList<BigInteger>();
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
            man.clearDatabase();
            this.crlPublishCount = 0;
            this.crl = null;
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }
}
