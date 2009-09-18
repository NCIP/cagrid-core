package org.cagrid.gaards.pki;

import java.security.Security;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class SecurityUtil {
	private static boolean isInit = false;


	public static void init() {
		if (!isInit) {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			isInit = true;
		}
	}
}
