package org.cagrid.gaards.dorian.service.tools;

import gov.nih.nci.cagrid.common.IOUtils;

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
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.bouncycastle.asn1.x509.X509Name;
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
public class CreateCACertificate {

    public static final String CONFIG_FILE_OPT = "c";

    public static final String CONFIG_FILE_FULL = "conf";

    public static final String PROPERTIES_FILE_OPT = "p";

    public static final String PROPERTIES_FILE_FULL = "properties";

    public static final String INTERACTIVE_MODE_OPT = "i";

    public static final String INTERACTIVE_MODE_FULL = "interactive";

    public static final String CA_SUBJECT_OPT = "s";

    public static final String CA_SUBJECT_FULL = "subject";

    public static final String DAYS_VALID_OPT = "d";

    public static final String DAYS_VALID_FULL = "days";

    public static final String HELP_OPT = "h";

    public static final String HELP_OPT_FULL = "help";


    public static void main(String[] args) {

        Options options = new Options();
        Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
        Option service = new Option(CONFIG_FILE_OPT, CONFIG_FILE_FULL, true, "The config file for the Dorian CA.");
        service.setRequired(true);
        Option im = new Option(INTERACTIVE_MODE_OPT, INTERACTIVE_MODE_FULL, false,
            "Specifies the use of interactive mode.");
        Option subject = new Option(CA_SUBJECT_OPT, CA_SUBJECT_FULL, true,
            "Specifies the subject of the new CA certificate, this option is required in non interactive mode.");

        Option d = new Option(DAYS_VALID_OPT, DAYS_VALID_FULL, true,
            "Specifies the number of days the ca certificate should be valid for, this option is required in non interactive mode.");

        Option props = new Option(PROPERTIES_FILE_OPT, PROPERTIES_FILE_FULL, true,
            "The properties file for the Dorian CA.");
        props.setRequired(true);
        options.addOption(props);
        options.addOption(help);
        options.addOption(service);
        options.addOption(im);
        options.addOption(subject);
        options.addOption(d);

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);

            if (line.getOptionValue(HELP_OPT) != null) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(CreateCACertificate.class.getName(), options);
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
                String sub = null;
                int days = 0;
                if (interactive) {
                    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
                    StringBuffer sb = new StringBuffer();
                    String o = IOUtils.readLine("Enter Organization (O)", true);
                    sb.append("O=").append(o);
                    String ou = null;
                    int count = 1;
                    while (true) {
                        ou = IOUtils.readLine("Enter Organizational Unit (OU) " + count);
                        count++;
                        if ((ou == null) || (ou.trim().length() == 0)) {
                            break;
                        } else {
                            sb.append(",OU=" + ou);
                        }
                    }

                    String cn = IOUtils.readLine("Enter Common Name (CN)", true);
                    sb.append(",CN=" + cn);
                    sub = sb.toString();
                    days = IOUtils.readInteger("Enter number of days valid", true);
                } else {
                    sub = line.getOptionValue(CA_SUBJECT_OPT);
                    if (sub == null) {
                        throw new Exception(
                            "No CA Subject specified, please specify a subject or use interactive mode.");
                    }

                    String str = line.getOptionValue(DAYS_VALID_OPT);
                    if (str == null) {
                        throw new Exception(
                            "No number of days valid specified, please specify a subject or use interactive mode.");
                    } else {
                        try {
                            days = Integer.valueOf(str).intValue();
                        } catch (NumberFormatException e) {
                            throw new Exception("The number of days specified was not a valid integer.");
                        }
                    }

                }

                GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

                Date start = new Date(date.getTimeInMillis());
                date.add(Calendar.DAY_OF_MONTH, days);
                Date end = new Date(date.getTimeInMillis());
                KeyPair root = KeyUtil.generateRSAKeyPair1024();
                X509Certificate cert = CertUtil.generateCACertificate(new X509Name(sub), start, end, root);
                ca.setCACredentials(cert, root.getPrivate(), null);
                System.out.println("Successfully created the CA cerrtificate:");
                System.out.println("Subject: " + cert.getSubjectDN());
                System.out.println("Created: " + cert.getNotBefore());
                System.out.println("Expires: " + cert.getNotAfter());
            }
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(CreateCACertificate.class.getName(), options, false);
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}