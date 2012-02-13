package org.cagrid.gaards.pki.tools;


import java.io.File;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.bouncycastle.asn1.x509.X509Name;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class AntGenerateEracomCA {

	public static void main(String[] args) {
		try {
			String alias = args[0];
			String dn = args[1];
			String daysValid = args[2];
			int slot = Integer.valueOf(args[3]).intValue();
			String password = args[4];
			String dir = args[5];
			Provider provider = (Provider) Class.forName(
				"au.com.eracom.crypto.provider.slot" + slot + ".ERACOMProvider").newInstance();
			Security.addProvider(provider);
			KeyStore keyStore = KeyStore.getInstance("CRYPTOKI", provider.getName());
			keyStore.load(null, password.toCharArray());
			KeyPair root = KeyUtil.generateRSAKeyPair2048(provider.getName());
			int days = Integer.valueOf(daysValid).intValue();
			while (days <= 0) {
				System.err.println("Days Valid must be >0");
				System.exit(1);
			}
			GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

			date.add(Calendar.MINUTE, -5);

			Date start = new Date(date.getTimeInMillis());
			date.add(Calendar.MINUTE, 5);
			date.add(Calendar.DAY_OF_MONTH, days);
			Date end = new Date(date.getTimeInMillis());
			X509Certificate cert = CertUtil.generateCACertificate(provider.getName(), new X509Name(dn), start, end,
				root, "SHA1WithRSA");

			keyStore.setKeyEntry(alias, root.getPrivate(), null, new X509Certificate[]{cert});
			String hash = CertUtil.getHashCode(cert);
			File certFile = new File(dir + File.separator + hash + ".0");
			File policyFile = new File(dir + File.separator + hash + ".signing_policy");
			CertUtil.writeCertificate(cert, certFile);
			CertUtil.writeSigningPolicy(cert, policyFile);
			System.out.println("Successfully created the CA certificate:");
			System.out.println(dn);
			System.out.println("CA certificate valid till:");
			System.out.println(cert.getNotAfter());
			System.out.println("The CA certificate and private key were written to slot " + slot + " on the HSM.");
			System.out.println("The CA certificate was written to the file: " + certFile.getAbsolutePath());
			System.out.println("The CA signing policy was written to the file: " + policyFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}