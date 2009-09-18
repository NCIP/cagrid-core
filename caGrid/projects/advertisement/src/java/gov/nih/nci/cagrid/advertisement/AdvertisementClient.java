package gov.nih.nci.cagrid.advertisement;

import gov.nih.nci.cagrid.advertisement.exceptions.InvalidConfigurationException;
import gov.nih.nci.cagrid.advertisement.exceptions.UnregistrationException;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.mds.servicegroup.client.ServiceGroupRegistrationParameters;
import org.globus.wsrf.impl.servicegroup.client.ServiceGroupRegistrationClient;
import org.globus.wsrf.impl.servicegroup.client.ServiceGroupRegistrationClientCallback;


/**
 * AdvertisementClient Currently just a wrapper for
 * ServiceGroupRegistrationClient, because its expected expected future releases
 * of ServiceGroupRegistrationClient will have some needed features (such as
 * unregistration).
 * 
 * @author oster
 * @created Apr 2, 2007 2:55:33 PM
 * @version $Id: multiscaleEclipseCodeTemplates.xml,v 1.1 2007/03/02 14:35:01
 *          dervin Exp $
 */
public class AdvertisementClient {
    protected static Log LOG = LogFactory.getLog(AdvertisementClient.class.getName());
    protected ServiceGroupRegistrationParameters sgParams;
    protected ServiceGroupRegistrationClient client;


    protected AdvertisementClient() {
        this.client = new ServiceGroupRegistrationClient();
        this.client.setClientCallback(new Callback());
        LOG.debug("Installing JVM shutdown hook to unregister.");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOG.info("Running shutdown hook.");
                try {
                    unregister();
                } catch (UnregistrationException e) {
                    LOG.error("Problem unreqistering", e);
                }
            }
        });
    }


    /**
     * Parses the file representing ServiceGroupRegistrationParameters
     * 
     * @param registrationConfigFile
     * @throws InvalidConfigurationException
     */

    public AdvertisementClient(File registrationConfigFile) throws InvalidConfigurationException {
        this();
        if (registrationConfigFile == null) {
            throw new InvalidConfigurationException("Configuration file cannot be null.");
        }
        if (!registrationConfigFile.isFile() || !registrationConfigFile.canRead()) {
            throw new InvalidConfigurationException("Configuration file must be a readable file.");
        }
        ServiceGroupRegistrationParameters readParams = null;
        try {
            readParams = ServiceGroupRegistrationClient.readParams(registrationConfigFile.getAbsolutePath());
        } catch (Exception e) {
            LOG.error("Problem parsing file:" + registrationConfigFile, e);
            throw new InvalidConfigurationException("Problem parsing configuration file:" + registrationConfigFile, e);
        }
        reinitialize(readParams);
    }


    /**
     * Initializes the client with the service group parameters
     * 
     * @param registrationConfig
     * @throws InvalidConfigurationException
     *             if the specific configuration is not valid
     * @see #reinitialize(ServiceGroupRegistrationParameters)
     */
    public AdvertisementClient(ServiceGroupRegistrationParameters registrationConfig)
        throws InvalidConfigurationException {
        this();
        reinitialize(registrationConfig);
    }


    /**
     * Can be called to cancel any existing registration, and repurpose the
     * client for new registrations.
     * 
     * @param registrationConfig
     * @throws InvalidConfigurationException
     *             if the specified config is invalid (in this case the client
     *             remains in the old state). An invalid config is one which is
     *             null, or has a null servicegroup or registrant epr
     */
    public void reinitialize(ServiceGroupRegistrationParameters registrationConfig)
        throws InvalidConfigurationException {
        if (registrationConfig == null) {
            throw new InvalidConfigurationException("Cannot use null parameters.");
        }
        if (registrationConfig.getRegistrantEPR() == null) {
            throw new InvalidConfigurationException("Must specify a non-null registrant.");
        }
        if (registrationConfig.getServiceGroupEPR() == null) {
            throw new InvalidConfigurationException("Must specify a non-null service group.");
        }

        try {
            unregister();
        } catch (UnregistrationException e) {
            // ignore this, as client is requesting a new registration, and
            // nothing they can do to correct problems with the existing one
            LOG.error("Problem unregistering existing entry", e);
        }
        this.sgParams = registrationConfig;
    }


    /**
     * Registers with the service group
     */
    public void register() {
        LOG.info("Registering  " + printServiceGroupRegistrationParameters(this.sgParams));
        this.client.register(this.sgParams);
    }


    /**
     * Unregisters from the service group
     */
    public void unregister() throws UnregistrationException {
        if (this.sgParams == null) {
            return;
        }
        LOG.info("Unregistering " + printServiceGroupRegistrationParameters(this.sgParams));
        this.client.terminate();
        TerminationClient termClient = new TerminationClient(this.sgParams.getServiceGroupEPR());
        if (this.sgParams.getSecurityDescriptorFile() != null) {
            termClient.setSecurityDescriptorFile(this.sgParams.getSecurityDescriptorFile());
        }
        int unregisterCount = termClient.unregister(this.sgParams.getRegistrantEPR());
        LOG.info("Unregistered " + unregisterCount + " service entries.");
    }


    /**
     * Returns a prinatable string representing the registration information
     * 
     * @param params
     * @return The registration parameters
     */
    public static String printServiceGroupRegistrationParameters(ServiceGroupRegistrationParameters params) {
        if (params == null) {
            return "null";
        }
        return "resource (" + params.getRegistrantEPR() + ") registering every (" + params.getRefreshIntervalSecs()
            + ") seconds to Service Group (" + params.getServiceGroupEPR() + ")";

    }


    /**
     * Logs status information.
     * 
     * @created Apr 5, 2007 11:25:48 PM
     * @version $Id: AdvertisementClient.java,v 1.1 2007/04/07 21:04:15 oster
     *          Exp $
     */
    protected class Callback implements ServiceGroupRegistrationClientCallback {
        public boolean setRegistrationStatus(ServiceGroupRegistrationParameters regParams, boolean wasSuccessful,
            boolean wasRenewalAttempt, Exception exception) {
            LOG.debug("Status Callback: " + AdvertisementClient.printServiceGroupRegistrationParameters(regParams)
                + " was " + (wasSuccessful ? "" : "not") + " successful.");
            if (exception != null) {
                LOG.error("Got exception from status callback:" + exception.getMessage(), exception);
            }
            return true;
        }
    }


    public static void main(String[] args) {
        AdvertisementClient client = null;
        try {
            client = new AdvertisementClient(new File("test/resources/registration.xml"));
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        client.register();
        // client.unregister();
    }
}
