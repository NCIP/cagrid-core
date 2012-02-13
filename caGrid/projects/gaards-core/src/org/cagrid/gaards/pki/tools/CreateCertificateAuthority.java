package org.cagrid.gaards.pki.tools;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.security.KeyPair;
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
public class CreateCertificateAuthority {


	public static final String DN = "dn";

	public static final String DN_DESCRIPTION = "The Distinguished Name (DN) for the certificate authority.";

	public static final String LIFETIME = "lifetime";

	public static final String LIFETIME_DESCRIPTION = "The number of days the certificate authority should be valid for.";

	public static final String PASSWORD = "password";

	public static final String PASSWORD_DESCRIPTION = "The password to be used to encrypt the CA's private key.";

	public static final String KEY = "key";

	public static final String KEY_DESCRIPTION = "The location to write the CA's private key to.";

	public static final String CERT = "cert";

	public static final String CERT_DESCRIPTION = "The location to write the CA's certificate to.";

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

			Option dnOpt = new Option(DN,  true, DN_DESCRIPTION);
			dnOpt.setRequired(true);
			options.addOption(dnOpt);

			Option lifetimeOpt = new Option(LIFETIME, true,
					LIFETIME_DESCRIPTION);
			lifetimeOpt.setRequired(true);
			options.addOption(lifetimeOpt);

			Option passwordOpt = new Option(PASSWORD, true,
					PASSWORD_DESCRIPTION);
			options.addOption(passwordOpt);

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
				String dn = dnOpt.getValue();
				String daysValid = lifetimeOpt.getValue();
				String password = Utils.clean(passwordOpt.getValue());
				String keyOut = keyOpt.getValue();
				String caOut =  certOpt.getValue();

				KeyPair root = KeyUtil.generateRSAKeyPair2048("BC");
				int days = Integer.valueOf(daysValid).intValue();
				while (days <= 0) {
					System.err.println("Days Valid must be >0");
					System.exit(1);
				}
				GregorianCalendar date = new GregorianCalendar(TimeZone
						.getTimeZone("GMT"));

				date.add(Calendar.MINUTE, -5);

				Date start = new Date(date.getTimeInMillis());
				date.add(Calendar.MINUTE, 5);
				date.add(Calendar.DAY_OF_MONTH, days);
				Date end = new Date(date.getTimeInMillis());
				X509Certificate cert = CertUtil.generateCACertificate(
						new X509Name(dn), start, end, root);

				password = Utils.clean(password);
				KeyUtil.writePrivateKey(root.getPrivate(), new File(keyOut),
						password);
				CertUtil.writeCertificate(cert, new File(caOut));
				System.out.println("Successfully created the CA certificate:");
				System.out.println(dn);
				System.out.println("CA certificate valid till:");
				System.out.println(cert.getNotAfter());
				System.out.println("CA private key written to:");
				System.out.println(keyOut);
				System.out.println("CA certificate written to:");
				System.out.println(caOut);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}