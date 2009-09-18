package org.cagrid.gaards.pki;

import java.math.BigInteger;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CRLEntry {
	private BigInteger certificateSerialNumber;
	private int reason;


	public CRLEntry(BigInteger sn, int reason) {
		this.certificateSerialNumber = sn;
		this.reason = reason;
	}


	public BigInteger getCertificateSerialNumber() {
		return certificateSerialNumber;
	}


	public int getReason() {
		return reason;
	}

}
