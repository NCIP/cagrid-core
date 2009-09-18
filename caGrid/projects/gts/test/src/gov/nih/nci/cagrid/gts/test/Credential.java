package gov.nih.nci.cagrid.gts.test;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: Credential.java,v 1.1 2006-05-17 03:06:35 langella Exp $
 */

public class Credential {

	X509Certificate certificate;
	PrivateKey privateKey;


	public Credential(X509Certificate cert, PrivateKey key) {
		this.certificate = cert;
		this.privateKey = key;
	}


	public X509Certificate getCertificate() {
		return certificate;
	}


	public PrivateKey getPrivateKey() {
		return privateKey;
	}

}
