package org.cagrid.gaards.dorian.tools;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.cagrid.gaards.dorian.client.GridUserClient;
import org.cagrid.gaards.dorian.federation.CertificateLifetime;
import org.globus.gsi.GlobusCredential;
import org.globus.util.ConfigUtil;


public class GridProxyInit {

    public static final int DEFAULT_LIFETIME_HOURS = 12;

    public static final int DEFAULT_LIFETIME_MINUTES = 0;

    public static final int DEFAULT_LIFETIME_SECONDS = 0;

    public static final String AUTHENTICATION_SERVICE_URL = "a";

    public static final String AUTHENTICATION_SERVICE_URL_VERBOSE = "authenticationURL";

    public static final String AUTHENTICATION_SERVICE_URL_DESCRIPTION = "Specifies the URL of the Authentication Service.";

    public static final String DORIAN_URL = "d";

    public static final String DORIAN_URL_VERBOSE = "dorianURL";

    public static final String DORIAN_URL_DESCRIPTION = "URL of the Dorian Service.";

    public static final String USER_ID = "u";

    public static final String USER_ID_VERBOSE = "user";

    public static final String USER_ID_DESCRIPTION = "The user's user id.";

    public static final String PASSWORD = "p";

    public static final String PASSWORD_VERBOSE = "password";

    public static final String PASSWORD_DESCRIPTION = "The user's password.";

    public static final String LIFETIME_HOURS = "h";

    public static final String LIFETIME_HOURS_VERBOSE = "hours";

    public static final String LIFETIME_HOURS_DESCRIPTION = "The number of hours the proxy will be valid for. ";

    public static final String LIFETIME_MINUTES = "m";

    public static final String LIFETIME_MINUTES_VERBOSE = "minutes";

    public static final String LIFETIME_MINUTES_DESCRIPTION = "The number of minutes the proxy will be valid for. ";

    public static final String LIFETIME_SECONDS = "s";

    public static final String LIFETIME_SECONDS_VERBOSE = "seconds";

    public static final String LIFETIME_SECONDS_DESCRIPTION = "The number of seconds the proxy will be valid for. ";

    public static final String OUT = "o";

    public static final String OUT_VERBOSE = "out";

    public static final String OUT_DESCRIPTION = "Location of the file to write the proxy to, if this option is not specified the Globus default location will be used.";

    public static final String HELP_OPT = "help";

    public static final String HELP_OPT_FULL = "help";


    public static GlobusCredential requestUserCertificate(String authenticationServiceURL, String dorianURL,
        Credential cred, CertificateLifetime lifetime, int delegationPathLength) throws InvalidCredentialFault,
        InsufficientAttributeFault, AuthenticationProviderFault, RemoteException, MalformedURIException {
        AuthenticationClient client = new AuthenticationClient(authenticationServiceURL, cred);
        SAMLAssertion saml = client.authenticate();
        GridUserClient dorian = new GridUserClient(dorianURL);
        GlobusCredential proxy = dorian.requestUserCertificate(saml, lifetime);
        return proxy;
    }


    public static void main(String[] args) {

        Options options = new Options();
        Option help = new Option(HELP_OPT, HELP_OPT_FULL, false, "Prints this message.");
        options.addOption(help);

        Option authURL = new Option(AUTHENTICATION_SERVICE_URL, AUTHENTICATION_SERVICE_URL_VERBOSE, true,
            AUTHENTICATION_SERVICE_URL_DESCRIPTION);
        authURL.setRequired(true);
        options.addOption(authURL);

        Option dorianURL = new Option(DORIAN_URL, DORIAN_URL_VERBOSE, true, DORIAN_URL_DESCRIPTION);
        dorianURL.setRequired(true);
        options.addOption(dorianURL);

        Option userId = new Option(USER_ID, USER_ID_VERBOSE, true, USER_ID_DESCRIPTION);
        userId.setRequired(true);
        options.addOption(userId);

        Option password = new Option(PASSWORD, PASSWORD_VERBOSE, true, PASSWORD_DESCRIPTION);
        password.setRequired(true);
        options.addOption(password);

        Option ohours = new Option(LIFETIME_HOURS, LIFETIME_HOURS_VERBOSE, true, LIFETIME_HOURS_DESCRIPTION);
        ohours.setRequired(false);
        options.addOption(ohours);

        Option ominutes = new Option(LIFETIME_MINUTES, LIFETIME_MINUTES_VERBOSE, true, LIFETIME_MINUTES_DESCRIPTION);
        ominutes.setRequired(false);
        options.addOption(ominutes);

        Option oseconds = new Option(LIFETIME_SECONDS, LIFETIME_SECONDS_VERBOSE, true, LIFETIME_SECONDS_DESCRIPTION);
        oseconds.setRequired(false);
        options.addOption(oseconds);

        Option out = new Option(OUT, OUT_VERBOSE, true, OUT_DESCRIPTION);
        out.setRequired(false);
        options.addOption(out);
        try {

            CommandLineParser parser = new PosixParser();
            parser.parse(options, args);

            BasicAuthenticationCredential auth = new BasicAuthenticationCredential();
            auth.setUserId(userId.getValue());
            auth.setPassword(password.getValue());
            Credential cred = new Credential();
            cred.setBasicAuthenticationCredential(auth);

            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            boolean lifetimeSpecified = false;

            if (ohours.getValue() != null) {
                try {
                    hours = Integer.valueOf(ohours.getValue()).intValue();
                    lifetimeSpecified = true;
                } catch (Exception e) {
                    throw new Exception("The number of hours must be specified as an integer!!!");
                }
            }

            if (ominutes.getValue() != null) {
                try {
                    minutes = Integer.valueOf(ominutes.getValue()).intValue();
                    lifetimeSpecified = true;
                } catch (Exception e) {
                    throw new Exception("The number of minutes must be specified as an integer!!!");
                }
            }

            if (oseconds.getValue() != null) {
                try {
                    seconds = Integer.valueOf(oseconds.getValue()).intValue();
                    lifetimeSpecified = true;
                } catch (Exception e) {
                    throw new Exception("The number of seconds must be specified as an integer!!!");
                }
            }

            if (!lifetimeSpecified) {
                hours = DEFAULT_LIFETIME_HOURS;
                minutes = DEFAULT_LIFETIME_MINUTES;
                seconds = DEFAULT_LIFETIME_SECONDS;
            }

            CertificateLifetime lifetime = new CertificateLifetime();
            lifetime.setHours(hours);
            lifetime.setMinutes(minutes);
            lifetime.setSeconds(seconds);

            System.out.print("Authenticating with the service " + authURL.getValue() + ".....");

            AuthenticationClient client = new AuthenticationClient(authURL.getValue(), cred);
            SAMLAssertion saml = client.authenticate();
            System.out.println("SUCCESSFUL");
            System.out.print("Requesting a proxy from the Dorian " + authURL.getValue() + ".....");

            GridUserClient dorian = new GridUserClient(dorianURL.getValue());
            GlobusCredential proxy = dorian.requestUserCertificate(saml, lifetime);
            System.out.println("SUCCESSFUL");
            System.out.println();
            System.out.println("Grid Proxy Certificate Summary");
            System.out.println("==============================");
            System.out.println("Grid Identity: " + proxy.getIdentity());
            System.out.println("Issuer: " + proxy.getIssuer());
            Calendar c = new GregorianCalendar();
            c.add(Calendar.SECOND, (int) proxy.getTimeLeft());
            System.out.println("Expires: " + c.getTime().toString());
            System.out.println("Strength: " + proxy.getStrength() + " bits.");
            System.out.println();
            if (out.getValue() == null) {
                ProxyUtil.saveProxyAsDefault(proxy);
                System.out.println("Proxy written to " + ConfigUtil.discoverProxyLocation());
            } else {
                ProxyUtil.saveProxy(proxy, out.getValue());
                System.out.println("Proxy written to " + out.getValue());
            }
        } catch (ParseException exp) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(GridProxyInit.class.getName(), options, false);
            System.exit(1);
        } catch (Exception e) {
            System.out.println("FAILED");
            System.out.println("The following error occurred: " + Utils.getExceptionMessage(e));
        }
    }
}
