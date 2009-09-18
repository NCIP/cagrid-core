package org.cagrid.gaards.pki.tools;


import java.io.File;
import java.security.cert.X509Certificate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.SecurityUtil;

public class ConvertCAFileNameToHash {

	public static final String CERTIFICATE_IN = "c";

	public static final String CERTIFICATE_IN_FULL = "cert";

	public static final String HELP_OPT = "h";

	public static final String HELP_OPT_FULL = "help";

	public static void main(String[] args) {
		SecurityUtil.init();
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP_OPT_FULL, false,
				"Prints this message.");
		Option in = new Option(CERTIFICATE_IN, CERTIFICATE_IN_FULL, true,
				"Path to the certificate you wish to rename to use a hash filename.");
		in.setRequired(true);
		options.addOption(help);
		options.addOption(in);

		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELP_OPT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(ConvertCAFileNameToHash.class.getName(),
						options);
				System.exit(0);
			} else {
				try {
					File f = new File(line.getOptionValue(CERTIFICATE_IN));
					X509Certificate cert = CertUtil.loadCertificate("BC", f);
					String hash = CertUtil.getHashCode(cert);
					File dir = f.getParentFile();
					if(dir==null){
						dir = new File(".");
					}
					File hashFile = new File(dir.getAbsolutePath()
							+ File.separator + hash + ".0");
					CertUtil.writeCertificate(cert, hashFile);
					System.out.println("Wrote hash certificate to: "
							+ hashFile.getAbsolutePath());
					File signingPolicy = new File(dir.getAbsolutePath()
							+ File.separator + hash + ".signing_policy");
					CertUtil.writeSigningPolicy(cert, signingPolicy);
					System.out.println("Wrote hash signing policy to: "
							+ signingPolicy.getAbsolutePath());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} catch (ParseException exp) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(ConvertCAFileNameToHash.class.getName(),
					options);
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}
