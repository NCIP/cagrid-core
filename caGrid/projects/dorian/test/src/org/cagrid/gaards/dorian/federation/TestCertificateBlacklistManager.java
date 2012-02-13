package org.cagrid.gaards.dorian.federation;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.math.BigInteger;
import java.util.List;

import junit.framework.TestCase;

import org.cagrid.gaards.dorian.test.CA;
import org.cagrid.gaards.dorian.test.Utils;
import org.cagrid.gaards.pki.Credential;
import org.cagrid.tools.database.Database;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestCertificateBlacklistManager extends TestCase {
    private Database db;
    private CA ca;
    private CertificateBlacklistManager blackList;


    public void testCertificateBlacklist() {
        try {
            int size = 3;
            Credential creds[] = new Credential[size];
            for (int i = 0; i < size; i++) {
                creds[i] = ca.createIdentityCertificate("user " + i);
            }
            assertEquals(0, blackList.getBlackList().size());
            for (int i = 0; i < size; i++) {
                assertEquals(i, blackList.getBlackList().size());
                blackList.addCertificateToBlackList(creds[i].getCertificate(), "Testing");
                List<Long> list = blackList.getBlackList();
                assertEquals((i + 1), list.size());
                for (int j = 0; j <= i; j++) {
                    boolean found = false;
                    for (int x = 0; x < list.size(); x++) {
                        if (creds[j].getCertificate().getSerialNumber().equals(
                            BigInteger.valueOf(list.get(x).longValue()))) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        fail("Certificate " + creds[j].getCertificate().getSubjectDN().getName()
                            + " is not in the black list but should be.");
                    }
                }
            }

            for (int i = 0; i < size; i++) {
                assertEquals((size - i), blackList.getBlackList().size());
                blackList.removeCertificateFromBlackList(creds[i].getCertificate().getSerialNumber().longValue());
                List<Long> list = blackList.getBlackList();
                assertEquals(size - (i + 1), list.size());
                for (int j = 0; j <= i; j++) {
                    boolean found = false;
                    for (int x = 0; x < list.size(); x++) {
                        if (creds[j].getCertificate().getSerialNumber().equals(
                            BigInteger.valueOf(list.get(x).longValue()))) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        fail("Certificate " + creds[j].getCertificate().getSubjectDN().getName()
                            + " is in the black list and should not be.");
                    }
                }
            }

        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            db = Utils.getDB();
            assertEquals(0, db.getUsedConnectionCount());
            ca = new CA();
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
            blackList.clearDatabase();
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }

        try {
            assertEquals(0, db.getUsedConnectionCount());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        }
    }

}
