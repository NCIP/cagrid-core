package org.cagrid.gaards.dorian.service.tools;

import gov.nih.nci.cagrid.common.IOUtils;
import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cagrid.gaards.dorian.ca.CertificateAuthority;
import org.cagrid.gaards.dorian.service.BeanUtils;
import org.cagrid.gaards.pki.CertUtil;
import org.cagrid.gaards.pki.KeyUtil;
import org.cagrid.tools.database.Database;
import org.springframework.core.io.FileSystemResource;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class ImportCA {

    public static final String CA_FILE_OPT = "c";

    public static final String CA_FILE_FULL = "cert";

    public static final String PKEY_FILE_OPT = "k";

    public static final String PKEY_FILE_FULL = "key";

    public static final String PKEY_PASSWORD_OPT = "p";

    public static final String PKEY_PASSWORD_FULL = "password";

    public static final String INTERACTIVE_MODE_OPT = "i";

    public static final String INTERACTIVE_MODE_FULL = "interactive";

    public static final String HELP_OPT = "h";

    public static final String HELP_OPT_FULL = "help";

    public static final String CONFIG_FILE_OPT = "f";

    public static final String CONFIG_FILE_FULL = "conf";

    public static final String PROPERTIES_FILE_OPT = "a";

    public static final String PROPERTIES_FILE_FULL = "properties";


    public static void main(String[] args) {

        Options options = new Options();
        Option service = new Option(CONFIG_FILE_OPT, CONFIG_FILE_FULL, true, "The config file for the Dorian CA.");
        service.setRequired(true);
        Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
        Option cacert = new Option(CA_FILE_OPT, CA_FILE_FULL, true,
            "The file containing the CA's certificate in PEM format.");
        Option key = new Option(PKEY_FILE_OPT, PKEY_FILE_FULL, true,
            "The file containing the CA's private key in PEM format.");
        Option password = new Option(PKEY_PASSWORD_OPT, PKEY_PASSWORD_FULL, true,
            "The file containing the CA's private key in PEM format.");
        Option im = new Option(INTERACTIVE_MODE_OPT, INTERACTIVE_MODE_FULL, false,
            "Specifies the use of interactive mode.");
        Option props = new Option(PROPERTIES_FILE_OPT, PROPERTIES_FILE_FULL, true,
            "The properties file for the Dorian CA.");
        props.setRequired(true);
        options.addOption(props);
        options.addOption(help);
        options.addOption(service);
        options.addOption(cacert);
        options.addOption(im);
        options.addOption(key);
        options.addOption(password);

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);

            if (line.getOptionValue(HELP_OPT) != null) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(ImportCA.class.getName(), options);
                System.exit(0);
            } else {
                String configFile = line.getOptionValue(CONFIG_FILE_OPT);
                String propertiesFile = line.getOptionValue(PROPERTIES_FILE_OPT);
                BeanUtils utils = new BeanUtils(new FileSystemResource(configFile), new FileSystemResource(
                    propertiesFile));
                Database db = utils.getDatabase();
                db.destroyDatabase();
                db.createDatabaseIfNeeded();
                CertificateAuthority ca = utils.getCertificateAuthority();
                boolean interactive = false;
                if (line.hasOption(INTERACTIVE_MODE_OPT)) {
                    interactive = true;
                }

                String certStr = line.getOptionValue(CA_FILE_OPT);
                File certFile = null;
                if (certStr != null) {
                    certFile = new File(certStr);
                    if (!certFile.exists()) {
                        System.err.println("The ca certificate specified does not exist in the location specified!!!");
                        certFile = null;
                    }
                }

                if (interactive) {
                    while (certFile == null) {
                        certStr = IOUtils.readLine("Enter CA Certificate (PEM format)", true);
                        certFile = new File(certStr);
                        if (!certFile.exists()) {
                            System.err
                                .println("The ca certificate specified does not exist in the location specified!!!");
                            certFile = null;
                        }
                    }
                }

                if (certFile == null) {
                    System.err.println("No CA certificate could be found...exiting!!!");
                    System.exit(0);
                }

                String keyStr = line.getOptionValue(PKEY_FILE_OPT);
                File keyFile = null;
                if (keyStr != null) {
                    keyFile = new File(keyStr);
                    if (!keyFile.exists()) {
                        System.err.println("The ca private key specified does not exist in the location specified!!!");
                        keyFile = null;
                    }
                }

                if (interactive) {
                    while (keyFile == null) {
                        keyStr = IOUtils.readLine("Enter CA Private Key (PEM format)", true);
                        keyFile = new File(keyStr);
                        if (!keyFile.exists()) {
                            System.err
                                .println("The ca private key specified does not exist in the location specified!!!");
                            keyFile = null;
                        }
                    }
                }

                if (keyFile == null) {
                    System.err.println("No CA private key could be found......exiting!!!");
                    System.exit(0);
                }
                String pass = line.getOptionValue(PKEY_PASSWORD_OPT);
                if (pass == null && interactive) {
                    pass = IOUtils.readLine("Private Key Password", false);
                }

                X509Certificate cert = CertUtil.loadCertificate(certFile);
                PrivateKey pkey = KeyUtil.loadPrivateKey(keyFile, Utils.clean(pass));
                ca.setCACredentials(cert, pkey, Utils.clean(pass));

            }
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(ImportCA.class.getName(), options, false);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}