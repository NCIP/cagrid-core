package org.cagrid.gaards.dorian.policy;

import gov.nih.nci.cagrid.common.FaultUtil;

import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.cagrid.gaards.core.Utils;
import org.cagrid.gaards.pki.CA;
import org.cagrid.gaards.pki.Credential;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestHostAgreementEncoding extends TestCase {

    public void testHostAgreementEncoding() {
        try {
            CA ca = new CA();
            Credential c = ca.createIdentityCertificate("some signer");
            Credential c2 = ca.createIdentityCertificate("some other signer");

            X509Certificate cert = c.getCertificate();
            PrivateKey key = c.getPrivateKey();
            org.apache.xml.security.Init.init();

            HostAgreement ha = new HostAgreement();
            ha.setName("Stephen Langella");
            List<X509Certificate> certs = new ArrayList<X509Certificate>();
            certs.add(cert);
            ha.sign(certs, key);
            ha.verify(cert);
            ha.verify();
            String xml = PolicyUtils.hostAgreementToString(ha);
            HostAgreement haFromString = PolicyUtils.stringToHostAgreement(xml);
            haFromString.verify(cert);
            haFromString.verify();

            String str = Utils.serialize(ha);
            HostAgreement ha2 = (HostAgreement) Utils.deserialize(str, HostAgreement.class);
            ha2.verify(cert);
            ha2.verify();
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
