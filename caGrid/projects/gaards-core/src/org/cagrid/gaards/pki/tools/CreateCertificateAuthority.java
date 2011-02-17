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
 * This class is used as a command-line program to create files for a certificate authority.
 * It creates a key, certificate and signing policy file.
 *
 * Given a path foo/bar, it will create foo/bar-cert.pem, foo/bar-key.pem and
 * foo/bar.signing_policy.
 *
 * @author <A href="mailto:mgrand@emory.edu">Mark Grand </A>
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

	public static final String PASSWORD_DESCRIPTION = "The password to be used to encrypt the CA's private key. This must be no longer than 13 characters";

	public static final String BASE_PATH = "basepath";

	public static final String BASE_PATH_DESCRIPTION
		= "The directory path and base name for the files to be written. If this is /foo/bar then"
		+ " the names of the files that will be written are foo/bar-cert.pem, foo/bar-key.pem and"
		+ " foo/bar.signing_policy";

	public static final String HELP_OPT = "h";

	public static final String HELP_OPT_FULL = "help";

	public static void main(String[] args) {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			Options options = new Options();
			Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
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

			Option basePathOpt = new Option(BASE_PATH, true, BASE_PATH_DESCRIPTION);
			basePathOpt.setRequired(true);
			options.addOption(basePathOpt);

			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELP_OPT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(CreateCertificateAuthority.class.getName(),
						options);
				System.exit(0);
			} else {
				String dn = dnOpt.getValue();
				String daysValid = lifetimeOpt.getValue();
				String password = Utils.clean(passwordOpt.getValue());
				String basePathOut = Utils.clean(basePathOpt.getValue());
				(new File(basePathOut)).getAbsoluteFile().getParentFile().mkdirs();
				String keyOut = basePathOut + "-key.pem";
				String certOut =  basePathOut + "-cert.pem";
				String signingOut = basePathOut + ".signing_policy";

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
				if (password.length() < 1 || password.length() > 13) {
					System.err.println("Password length must be at least one and no greater than 13.");
					System.exit(1);
				}
				KeyUtil.writePrivateKey(root.getPrivate(), new File(keyOut), password);
				CertUtil.writeCertificate(cert, new File(certOut));
				CertUtil.writeSigningPolicy(cert, new File(signingOut));
				System.out.println("Successfully created the CA certificate:");
				System.out.println(dn);
				System.out.print("CA certificate valid till: ");
				System.out.println(cert.getNotAfter());
				System.out.print("CA private key written to: ");
				System.out.println(keyOut);
				System.out.print("CA certificate written to: ");
				System.out.println(certOut);
				System.out.print("CA signing policy file written to: ");
				System.out.println(signingOut);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

}