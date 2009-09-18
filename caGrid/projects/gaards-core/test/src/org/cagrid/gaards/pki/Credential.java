package org.cagrid.gaards.pki;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: Credential.java,v 1.1 2008-04-29 19:29:48 langella Exp $
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


	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
	

}
