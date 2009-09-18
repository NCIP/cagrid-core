package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.testing.system.haste.Step;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NotificationClientSetupStep extends Step {
    public static final String GLOBUS_LOCATION = "GLOBUS_LOCATION";

    private static Log LOG = LogFactory.getLog(NotificationClientSetupStep.class);
    
    public NotificationClientSetupStep() {
        
    }
    

    public void runStep() throws Throwable {
        if (System.getProperty(GLOBUS_LOCATION, null) == null) {
            String location = System.getenv(GLOBUS_LOCATION);
            LOG.debug("Using globus location environment variable; set to " + location);
            assertNotNull(GLOBUS_LOCATION + " environment variable was null!", location);
            System.setProperty(GLOBUS_LOCATION, location);
        } else {
            LOG.debug(GLOBUS_LOCATION + " system property already set to " 
                + System.getProperty(GLOBUS_LOCATION));
        }
    }
}
