package org.cagrid.gaards.pki.tools;


import java.io.File;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
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
public class AntCreateHostCertificateWithEracomCA {

	public static void main(String[] args) {
		try {
			Security
					.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			String alias = args[0];
			int slot = Integer.valueOf(args[1]).intValue();
			String password = args[2];
			String host = args[3];
			String daysValid = args[4];
			String keyOut = args[5];
			String certOut = args[6];

			int days = Integer.valueOf(daysValid).intValue();
			while (days <= 0) {
				System.err.println("Days Valid must be >0");
				System.exit(1);
			}
			Provider provider = (Provider) Class.forName(
					"au.com.eracom.crypto.provider.slot" + slot
							+ ".ERACOMProvider").newInstance();
			Security.addProvider(provider);
			KeyStore keyStore = KeyStore.getInstance("CRYPTOKI", provider
					.getName());
			keyStore.load(null, password.toCharArray());
			PrivateKey cakey = (PrivateKey) keyStore.getKey(alias, null);
			X509Certificate cacert = convert((X509Certificate) keyStore
					.getCertificate(alias));
			

			KeyPair pair = KeyUtil.generateRSAKeyPair1024("BC");
			String rootSub = cacert.getSubjectDN().toString();
			int index = rootSub.lastIndexOf(",");
			String subject = rootSub.substring(0, index)
					+ ",CN=host/" + host;

			GregorianCalendar date = new GregorianCalendar(TimeZone
					.getTimeZone("GMT"));
			/* Allow for a five minute clock skew here. */
			date.add(Calendar.MINUTE, -5);
			Date start = new Date(date.getTimeInMillis());
			Date end = null;
			/* If hours = 0, then cert lifetime is set to user cert */
			if (days <= 0) {
				end = cacert.getNotAfter();
			} else {
				date.add(Calendar.MINUTE, 5);
				date.add(Calendar.DAY_OF_MONTH, days);
				Date d = new Date(date.getTimeInMillis());
				if (cacert.getNotAfter().before(d)) {
					throw new GeneralSecurityException(
							"Cannot create a certificate that expires after issuing certificate.");
				}
				end = d;
			}
			X509Certificate userCert = convert(CertUtil.generateCertificate(provider
					.getName(), new X509Name(subject), start, end, pair
					.getPublic(), cacert, cakey, "SHA1WithRSA", null));

			KeyUtil.writePrivateKey(pair.getPrivate(), new File(keyOut));
			CertUtil.writeCertificate(userCert, new File(certOut));
			System.out.println("Successfully created the host certificate:");
			System.out.println(userCert.getSubjectDN().toString());
			System.out.println("Host certificate issued by:");
			System.out.println(cacert.getSubjectDN().toString());
			System.out.println("Host certificate valid till:");
			System.out.println(userCert.getNotAfter());
			System.out.println("Host private key written to:");
			System.out.println(keyOut);
			System.out.println("Host certificate written to:");
			System.out.println(certOut);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
	
	protected static X509Certificate convert(X509Certificate cert) throws Exception {
		String str = CertUtil.writeCertificate(cert);
		return CertUtil.loadCertificate(str);
	}

}