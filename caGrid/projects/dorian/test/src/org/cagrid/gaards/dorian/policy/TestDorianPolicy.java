package org.cagrid.gaards.dorian.policy;

import gov.nih.nci.cagrid.common.FaultUtil;
import gov.nih.nci.cagrid.dorian.common.CommonUtils;
import gov.nih.nci.cagrid.opensaml.InvalidCryptoException;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cagrid.gaards.authentication.BasicAuthentication;
import org.cagrid.gaards.authentication.faults.InvalidCredentialFault;
import org.cagrid.gaards.core.Utils;
import org.cagrid.gaards.dorian.client.DorianBaseClient;
import org.cagrid.gaards.dorian.idp.Application;
import org.cagrid.gaards.dorian.idp.LocalUser;
import org.cagrid.gaards.dorian.idp.LocalUserFilter;
import org.cagrid.gaards.dorian.idp.LocalUserRole;
import org.cagrid.gaards.dorian.idp.LocalUserStatus;
import org.cagrid.gaards.dorian.service.Dorian;
import org.cagrid.gaards.dorian.service.DorianProperties;
import org.cagrid.gaards.pki.CA;
import org.cagrid.gaards.pki.Credential;
import org.cagrid.gaards.saml.encoding.TestSAMLEncoding;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestDorianPolicy extends TestCase {

    public void testPolicyEncoding() {
        Dorian dorian = null;
        try {
            // initialize a Dorian object
            DorianProperties conf = org.cagrid.gaards.dorian.test.Utils.getDorianProperties();
            dorian = new Dorian(conf, "https://localhost");
            assertNotNull(dorian.getConfiguration());
            assertNotNull(dorian.getDatabase());
            DorianPolicy policy = dorian.getDorianPolicy();
            StringWriter writer = new StringWriter();
            gov.nih.nci.cagrid.common.Utils.serializeObject(policy, DorianBaseClient.POLICY, writer,
                TestDorianPolicy.class.getResourceAsStream("/client-config.wsdd"));;
            String str = writer.toString();
            System.out.println(str);
            DorianPolicy policy2 = (DorianPolicy) gov.nih.nci.cagrid.common.Utils.deserializeObject(new StringReader(
                str), DorianPolicy.class, TestDorianPolicy.class.getResourceAsStream("/client-config.wsdd"));
           // assertEquals(policy, policy2);
        } catch (Exception e) {
            FaultUtil.printFault(e);
            assertTrue(false);
        } finally {
            try {
                assertEquals(0, dorian.getDatabase().getUsedConnectionCount());
                dorian.clearDatabase();
                dorian = null;
            } catch (Exception e) {
                FaultUtil.printFault(e);
                assertTrue(false);
            }
        }
    }


    protected void setUp() throws Exception {
        super.setUp();
        try {
            org.apache.xml.security.Init.init();
            Security.addProvider(new BouncyCastleProvider());
        } catch (Exception e) {
            FaultUtil.printFault(e);
            fail(e.getMessage());
        }
    }

}
