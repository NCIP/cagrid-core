package org.cagrid.gaards.dorian.service.tools;

import gov.nih.nci.cagrid.common.Utils;

import java.io.File;
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
import org.springframework.core.io.FileSystemResource;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class ConfigureGlobusToTrustDorian {

    public static final String PROPERTIES_FILE_OPT = "p";

    public static final String PROPERTIES_FILE_FULL = "properties";

    public static final String CONFIG_FILE_OPT = "c";

    public static final String CONFIG_FILE_FULL = "conf";

    public static final String HELP_OPT = "u";

    public static final String HELP_OPT_FULL = "help";


    public static void main(String[] args) {

        Options options = new Options();
        Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
        Option conf = new Option(CONFIG_FILE_OPT, CONFIG_FILE_FULL, true, "The config file for the Dorian CA.");
        conf.setRequired(true);
        Option props = new Option(PROPERTIES_FILE_OPT, PROPERTIES_FILE_FULL, true,
            "The properties file for the Dorian CA.");
        props.setRequired(true);
        options.addOption(props);
        options.addOption(help);
        options.addOption(conf);

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);

            if (line.getOptionValue(HELP_OPT) != null) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(ConfigureGlobusToTrustDorian.class.getName(), options);
                System.exit(0);
            } else {
                String configFile = line.getOptionValue(CONFIG_FILE_OPT);
                String propertiesFile = line.getOptionValue(PROPERTIES_FILE_OPT);
                BeanUtils utils = new BeanUtils(new FileSystemResource(configFile), new FileSystemResource(
                    propertiesFile));
                utils.getDatabase().createDatabaseIfNeeded();
                CertificateAuthority ca = utils.getCertificateAuthority();
                X509Certificate cacert = ca.getCACertificate();

                File dir = Utils.getTrustedCerificatesDirectory();
                File caFile = new File(dir.getAbsolutePath() + File.separator + CertUtil.getHashCode(cacert) + ".0");
                File policyFile = new File(dir.getAbsolutePath() + File.separator + CertUtil.getHashCode(cacert)
                    + ".signing_policy");
                CertUtil.writeCertificate(cacert, caFile);
                CertUtil.writeSigningPolicy(cacert, policyFile);
                System.out.println("Succesfully configured Globus to trust the Dorian CA: "
                    + cacert.getSubjectDN().getName());
                System.out.println("Succesfully wrote CA certificate to " + caFile.getAbsolutePath());
                System.out.println("Succesfully wrote CA signing policy to " + policyFile.getAbsolutePath());
            }
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(ConfigureGlobusToTrustDorian.class.getName(), options, false);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}