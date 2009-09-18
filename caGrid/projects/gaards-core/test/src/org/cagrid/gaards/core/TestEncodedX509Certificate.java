package org.cagrid.gaards.core;

import org.cagrid.gaards.pki.CA;

import gov.nih.nci.cagrid.common.FaultUtil;
import junit.framework.TestCase;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class TestEncodedX509Certificate extends TestCase {

	public void testEncoding() {
		try {
			CA ca = new CA();
			EncodedX509Certificate en1 = new EncodedX509Certificate();
			en1.setCertificate(ca.getCertificate());
			String str = org.cagrid.gaards.core.Utils.serialize(en1);
			System.out.println(str);
			EncodedX509Certificate en2 = (EncodedX509Certificate)org.cagrid.gaards.core.Utils.deserialize(str, EncodedX509Certificate.class);
			assertTrue(en1.equals(en2));
			assertTrue(ca.getCertificate().equals(en2.getCertificate()));
		} catch (Exception e) {
			FaultUtil.printFault(e);
			fail(e.getMessage());
		}
	}
}
