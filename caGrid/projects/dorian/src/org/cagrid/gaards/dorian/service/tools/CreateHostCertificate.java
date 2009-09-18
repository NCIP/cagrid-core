package org.cagrid.gaards.dorian.service.tools;

import gov.nih.nci.cagrid.common.IOUtils;

import java.io.File;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cagrid.gaards.dorian.ca.CertificateAuthorityProperties;
import org.cagrid.gaards.dorian.federation.HostCertificateRecord;
import org.cagrid.gaards.dorian.federation.HostCertificateRequest;
import org.cagrid.gaards.dorian.federation.PublicKey;
import org.cagrid.gaards.dorian.service.BeanUtils;
import org.cagrid.gaards.dorian.service.Dorian;
import org.cagrid.gaards.dorian.service.DorianProperties;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.springframework.core.io.FileSystemResource;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class CreateHostCertificate {

    public static final String CONFIG_FILE_OPT = "c";

    public static final String CONFIG_FILE_FULL = "conf";

    public static final String PROPERTIES_FILE_OPT = "p";

    public static final String PROPERTIES_FILE_FULL = "properties";

    public static final String INTERACTIVE_MODE_OPT = "i";

    public static final String INTERACTIVE_MODE_FULL = "interactive";

    public static final String HOST_OPT = "h";

    public static final String HOST_FULL = "host";

    public static final String DIRECTORY_OPT = "d";

    public static final String DIRECTORY_FULL = "directory";

    public static final String HELP_OPT = "u";

    public static final String HELP_OPT_FULL = "help";


    public static void main(String[] args) {

        Options options = new Options();
        Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
        Option conf = new Option(CONFIG_FILE_OPT, CONFIG_FILE_FULL, true, "The config file for the Dorian CA.");
        conf.setRequired(true);
        Option im = new Option(INTERACTIVE_MODE_OPT, INTERACTIVE_MODE_FULL, false,
            "Specifies the use of interactive mode.");
        Option host = new Option(HOST_OPT, HOST_FULL, true,
            "Specifies the host that the host certificate is being created for.");

        Option directory = new Option(DIRECTORY_OPT, DIRECTORY_FULL, true,
            "Specifies the directory to write the host certificate and private key out to.");
        Option props = new Option(PROPERTIES_FILE_OPT, PROPERTIES_FILE_FULL, true,
            "The properties file for the Dorian CA.");
        props.setRequired(true);
        options.addOption(props);
        options.addOption(help);
        options.addOption(conf);
        options.addOption(im);
        options.addOption(host);
        options.addOption(directory);

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);

            if (line.getOptionValue(HELP_OPT) != null) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(CreateHostCertificate.class.getName(), options);
                System.exit(0);
            } else {
                String configFile = line.getOptionValue(CONFIG_FILE_OPT);
                String propertiesFile = line.getOptionValue(PROPERTIES_FILE_OPT);
                BeanUtils utils = new BeanUtils(new FileSystemResource(configFile), new FileSystemResource(
                    propertiesFile));
                DorianProperties c = utils.getDorianProperties();
                c.getIdentityFederationProperties().setAutoHostCertificateApproval(true);
                Dorian dorian = new Dorian(c, "https://localhost", true);
                boolean interactive = false;
                if (line.hasOption(INTERACTIVE_MODE_OPT)) {
                    interactive = true;
                }
                String hostname = null;
                String dir = null;
                if (interactive) {
                    hostname = IOUtils.readLine("Enter the hostname", true);
                    dir = IOUtils
                        .readLine("Enter the directory to write the host certificate and private key to", true);
                } else {
                    hostname = line.getOptionValue(HOST_OPT);
                    if (hostname == null) {
                        throw new Exception("No host specified, please specify a host or use interactive mode.");
                    }

                    dir = line.getOptionValue(DIRECTORY_OPT);
                    if (dir == null) {
                        throw new Exception(
                            "No directory specified, please specify a directory or use interactive mode.");
                    }

                }

                c.getIdentityFederationProperties().setAutoHostCertificateApproval(true);
                CertificateAuthorityProperties caProperties = utils.getCertificateAuthorityProperties();

                KeyPair pair = KeyUtil.generateRSAKeyPair(caProperties.getIssuedCertificateKeySize());
                X509Certificate cacert = dorian.getCACertificate();
                String caSubject = cacert.getSubjectDN().getName();
                int index = caSubject.lastIndexOf(",");
                String subjectPrefix = caSubject.substring(0, index);
                String gridId = null;
                if (c.getIdentityFederationProperties().getIdentityAssignmentPolicy().equals(
                    org.cagrid.gaards.dorian.federation.IdentityAssignmentPolicy.NAME)) {
                    gridId = CertUtil.subjectToIdentity(subjectPrefix + ",OU="
                        + c.getIdentityProviderProperties().getName() + "/CN=dorian");
                } else {
                    gridId = CertUtil.subjectToIdentity(subjectPrefix + ",OU=IdP [1]/CN=dorian");
                }
                System.out.println(gridId);
                HostCertificateRequest req = new HostCertificateRequest();
                req.setHostname(hostname);
                PublicKey publicKey = new PublicKey();
                publicKey.setKeyAsString(KeyUtil.writePublicKey(pair.getPublic()));
                req.setPublicKey(publicKey);
                HostCertificateRecord record = dorian.requestHostCertificate(gridId, req);
                X509Certificate cert = CertUtil.loadCertificate(record.getCertificate().getCertificateAsString());
                System.out.println("Successfully created the host certificate:");
                System.out.println("Subject: " + cert.getSubjectDN());
                System.out.println("Created: " + cert.getNotBefore());
                System.out.println("Expires: " + cert.getNotAfter());
                File f = new File(dir);
                f.mkdirs();
                File keyFile = new File(f.getAbsolutePath() + File.separator + hostname + "-key.pem");

                KeyUtil.writePrivateKey(pair.getPrivate(), keyFile);
                System.out.println("Succesfully wrote private key to " + keyFile.getAbsolutePath());
                File certFile = new File(f.getAbsolutePath() + File.separator + hostname + "-cert.pem");
                CertUtil.writeCertificate(cert, certFile);
                System.out.println("Succesfully wrote certificate to " + certFile.getAbsolutePath());

            }
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(CreateHostCertificate.class.getName(), options, false);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}