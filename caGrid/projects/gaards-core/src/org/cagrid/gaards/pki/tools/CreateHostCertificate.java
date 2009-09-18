package org.cagrid.gaards.pki.tools;

import java.io.File;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;

/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CreateHostCertificate {

	public static final String CA_KEY = "cakey";

	public static final String CA_KEY_DESCRIPTION = "The location of the CA private key that will be used to sign the host certificate.";

	public static final String CA_CERT = "cacert";

	public static final String CA_CERT_DESCRIPTION = "The location of the CA private key that will be used to sign the host certificate.";

	public static final String HOSTNAME = "host";

	public static final String HOSTNAME_DESCRIPTION = "The host name of the host whom the certificate will be created for.";

	public static final String LIFETIME = "lifetime";

	public static final String LIFETIME_DESCRIPTION = "The number of days the certificate authority should be valid for.";

	public static final String PASSWORD = "password";

	public static final String PASSWORD_DESCRIPTION = "The password for the CA's private key.";

	public static final String KEY = "key";

	public static final String KEY_DESCRIPTION = "The location to write the host private key to.";

	public static final String CERT = "cert";

	public static final String CERT_DESCRIPTION = "The location to write the host certificate to.";

	public static final String HELP_OPT = "h";

	public static final String HELP_OPT_FULL = "help";

	public static void main(String[] args) {
		try {
			Security
					.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			Options options = new Options();
			Option help = new Option(HELP_OPT, HELP_OPT_FULL, false,
					"Prints this message.");
			options.addOption(help);

			Option caKeyOpt = new Option(CA_KEY, true, CA_KEY_DESCRIPTION);
			caKeyOpt.setRequired(true);
			options.addOption(caKeyOpt);

			Option passwordOpt = new Option(PASSWORD, true,
					PASSWORD_DESCRIPTION);
			options.addOption(passwordOpt);

			Option caCertOpt = new Option(CA_CERT, true, CA_CERT_DESCRIPTION);
			caCertOpt.setRequired(true);
			options.addOption(caCertOpt);

			Option hostOpt = new Option(HOSTNAME, true, HOSTNAME_DESCRIPTION);
			hostOpt.setRequired(true);
			options.addOption(hostOpt);

			Option lifetimeOpt = new Option(LIFETIME, true,
					LIFETIME_DESCRIPTION);
			lifetimeOpt.setRequired(true);
			options.addOption(lifetimeOpt);

			Option certOpt = new Option(CERT, true, CERT_DESCRIPTION);
			certOpt.setRequired(true);
			options.addOption(certOpt);

			Option keyOpt = new Option(KEY, true, KEY_DESCRIPTION);
			keyOpt.setRequired(true);
			options.addOption(keyOpt);

			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);
			if (line.hasOption(HELP_OPT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(GenerateTrustReport.class.getName(),
						options);
				System.exit(0);
			} else {
				String key = caKeyOpt.getValue();
				String password = passwordOpt.getValue();
				String cert = caCertOpt.getValue();
				String cn = hostOpt.getValue();
				String daysValid = lifetimeOpt.getValue();
				String keyOut = keyOpt.getValue();
				String certOut = certOpt.getValue();

				int days = Integer.valueOf(daysValid).intValue();
				while (days <= 0) {
					System.err.println("Days Valid must be >0");
					System.exit(1);
				}
				PrivateKey cakey = KeyUtil.loadPrivateKey(new File(key),
						password);

				X509Certificate cacert = CertUtil.loadCertificate("BC",
						new File(cert));

				KeyPair pair = KeyUtil.generateRSAKeyPair1024("BC");
				String rootSub = cacert.getSubjectDN().toString();
				int index = rootSub.lastIndexOf(",");
				String subject = rootSub.substring(0, index) + ",CN=host/" + cn;
				PKCS10CertificationRequest request = CertUtil
						.generateCertficateRequest(subject, pair);

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
				X509Certificate userCert = CertUtil.signCertificateRequest(
						request, start, end, cacert, cakey, null);

				KeyUtil.writePrivateKey(pair.getPrivate(), new File(keyOut));
				CertUtil.writeCertificate(userCert, new File(certOut));
				System.out
						.println("Successfully created the host certificate:");
				System.out.println(userCert.getSubjectDN().toString());
				System.out.println("Host certificate issued by:");
				System.out.println(cacert.getSubjectDN().toString());
				System.out.println("Host certificate valid till:");
				System.out.println(userCert.getNotAfter());
				System.out.println("Host private key written to:");
				System.out.println(keyOut);
				System.out.println("Host certificate written to:");
				System.out.println(certOut);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}