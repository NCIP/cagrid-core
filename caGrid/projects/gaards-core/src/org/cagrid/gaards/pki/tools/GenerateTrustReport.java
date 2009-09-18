package org.cagrid.gaards.pki.tools;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.io.PrintWriter;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.TrustUtils;
import org.cagrid.gaards.pki.TrustedCAFileListing;


public class GenerateTrustReport {
	public static final String OUT_OPT = "o";
	public static final String OUT_OPT_FULL = "out";
	public static final String HELP_OPT = "h";
	public static final String HELP_OPT_FULL = "help";

	public static PrintWriter pw;


	public static void main(String[] args) {
		Options options = new Options();
		Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
		Option out = new Option(OUT_OPT, OUT_OPT_FULL, true, "The file to write the report to.");
		out.setRequired(false);
		options.addOption(help);
		options.addOption(out);
		try {
			CommandLineParser parser = new PosixParser();
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(HELP_OPT)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(GenerateTrustReport.class.getName(), options);
				System.exit(0);
			} else {
				String str = out.getValue();
				if (Utils.clean(str) == null) {
					str = "grid-trust-report.txt";
				}
				System.out.println(str);
				pw = new PrintWriter(new File(str));
				File f = Utils.getTrustedCerificatesDirectory();
				List<TrustedCAFileListing> list = TrustUtils.getTrustedCertificates(f);
				report("================================================");
				report("|         *** GRID TRUST REPORT ***            |");
				report("================================================");
				report("");
				report("Certificates Directory: " + f.getAbsolutePath());
				report("Number of Certificates: " + list.size());
				report("Report Date: " + new Date());
				report("");
				report("");

				boolean warningsFound = false;
				report("*** WARNINGS ***");
				report("");
				if (checkDuplicates(list)) {
					warningsFound = true;
				}

				if (checkValidity(list)) {
					warningsFound = true;
				}

				if (!warningsFound) {
					report("  NO WARNINGS DETECTED.");
				}
				report("");
				report("*** CERTIFICATE SUMMARY ***");
				report("");
				for (int i = 0; i < list.size(); i++) {
					TrustedCAFileListing ca = list.get(i);
					try {

						X509Certificate cert = CertUtil.loadCertificate(ca.getCertificate());
						report("Certificate Subject:       " + cert.getSubjectDN());
						report("Serial Number:             " + cert.getSerialNumber());
						report("Valid On:                  " + cert.getNotBefore());
						report("Valid After:               " + cert.getNotAfter());
						report("File Prefix:               " + ca.getName());
						report("Certificate File:          " + ca.getCertificate().getAbsolutePath());
						report("Certificate Last Modified: " + new Date(ca.getCertificate().lastModified()));
						if (ca.getSigningPolicy() == null) {
							report("Signing Policy:            NONE");
						} else {
							report("Signing Policy:            " + ca.getSigningPolicy().getAbsolutePath());
						}
						if (ca.getMetadata() == null) {
							report("SyncGTS Report:            NONE");
						} else {
							report("SyncGTS Report:            " + ca.getMetadata().getAbsolutePath());
						}
						if (ca.getCRL() == null) {
							report("CRL:                       NONE");
						} else {
							report("CRL:                       " + ca.getCRL().getAbsolutePath());
							X509CRL crl = CertUtil.loadCRL(ca.getCRL());
							Set s = crl.getRevokedCertificates();
							if (s == null) {
								report("Certificates Revoked:      0");
							} else {
								report("Certificates Revoked:      " + s.size());
								Iterator curr = s.iterator();
								report("Revoked Ceritficates:");
								int count = 1;
								while (curr.hasNext()) {
									X509CRLEntry entry = (X509CRLEntry) curr.next();
									report("	" + count + ") " + entry.getSerialNumber());
									count++;
								}
							}

						}
						report("");
					} catch (Exception e) {
						report("");
						report("***     ERROR LOADING CA " + ca.getName() + "    ***");
						report("*** REASON: " + e.getMessage() + " ***");
						report("");
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}


	public static boolean checkDuplicates(List<TrustedCAFileListing> list) throws Exception {
		boolean warning = false;
		Set<String> found = new HashSet<String>();
		for (int i = 0; i < list.size(); i++) {
			TrustedCAFileListing ca = list.get(i);
			X509Certificate cert = CertUtil.loadCertificate(ca.getCertificate());
			if (found.contains(cert.getSubjectDN().getName())) {
				report("Duplicate entries found for " + cert.getSubjectDN().getName());
				warning = true;
			} else {
				found.add(cert.getSubjectDN().getName());
			}
		}
		return warning;
	}


	public static boolean checkValidity(List<TrustedCAFileListing> list) throws Exception {
		boolean warning = false;
		for (int i = 0; i < list.size(); i++) {
			TrustedCAFileListing ca = list.get(i);
			X509Certificate cert = CertUtil.loadCertificate(ca.getCertificate());
			try {
				cert.checkValidity();
			} catch (Exception e) {
				report("The certificate " + cert.getSubjectDN().getName() + " is invalid.");
				warning = true;
			}

		}
		return warning;
	}


	public static void report(String message) {
		System.out.println(message);
		pw.println(message);
	}
}
