package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ChangeFqpServiceProperties
 * Changes the various values of the FQP service's
 * deployment properties
 * 
 * @author David
 *
 */
public class ChangeFqpServiceProperties extends Step {
    
    public static final String LEASE_TIME = "initialResultLeaseInMinutes";
    public static final String MAX_RETRY_TIMEOUT = "maxRetryTimeout";
    public static final String MAX_TARGET_SERVICES = "maxTargetServicesPerQuery";
    public static final String MAX_RETRIES = "maxRetries";

    private File serviceDir = null;
    private int leaseMinutes;
    private int retryTimeout;
    private int maxTargetServices;
    private int maxRetries;
    
    public ChangeFqpServiceProperties(File serviceDir, 
        int leaseMinutes, int retryTimeout, 
        int maxTargetServices, int maxRetries) {
        this.serviceDir = serviceDir;
        this.leaseMinutes = leaseMinutes;
        this.retryTimeout = retryTimeout;
        this.maxTargetServices = maxTargetServices;
        this.maxRetries = maxRetries;
    }


    public void runStep() throws Throwable {
        // get the service properties file
        File servicePropsFile = new File(serviceDir, "service.properties");
        assertTrue("Service properties file (" 
            + serviceDir.getAbsolutePath() + ") not found!", 
            servicePropsFile.exists());
        // read it
        Properties props = new Properties();
        try {
            FileInputStream input = new FileInputStream(servicePropsFile);
            props.load(input);
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error reading service properties: " + ex.getMessage());
        }
        // set the properties
        props.setProperty(LEASE_TIME, String.valueOf(leaseMinutes));
        props.setProperty(MAX_RETRY_TIMEOUT, String.valueOf(retryTimeout));
        props.setProperty(MAX_TARGET_SERVICES, String.valueOf(maxTargetServices));
        props.setProperty(MAX_RETRIES, String.valueOf(maxRetries));
        // write the properties file
        try {
            FileOutputStream output = new FileOutputStream(servicePropsFile);
            props.store(output, "Edited by " + ChangeFqpServiceProperties.class.getName());
            output.flush();
            output.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error storing edited service properties: " + ex.getMessage());
        }
    }
}
