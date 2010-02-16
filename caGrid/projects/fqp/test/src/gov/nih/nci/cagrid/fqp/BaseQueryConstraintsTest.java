package gov.nih.nci.cagrid.fqp;

import gov.nih.nci.cagrid.fqp.service.FederatedQueryProcessorConfiguration;
import gov.nih.nci.cagrid.fqp.service.QueryConstraintsValidator;

import java.rmi.RemoteException;

import junit.framework.TestCase;

public abstract class BaseQueryConstraintsTest extends TestCase {


    public static final int MAX_TARGET_SERVICES = 12;
    public static final int MAX_RETRY_TIMEOUT = 30;
    public static final int MAX_RETRIES = 10;


    public BaseQueryConstraintsTest(String name) {
        super(name);
    }
    
    
    protected QueryConstraintsValidator getValidator() {
        FederatedQueryProcessorConfiguration config = new FederatedQueryProcessorConfiguration();
        config.setMaxRetries(String.valueOf(MAX_RETRIES));
        config.setMaxRetryTimeout(String.valueOf(MAX_RETRY_TIMEOUT));
        config.setMaxTargetServicesPerQuery(String.valueOf(MAX_TARGET_SERVICES));
        QueryConstraintsValidator validator = null;
        try {
            validator = new QueryConstraintsValidator(config);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            fail("Error setting up query constaints validator: " + ex.getMessage());
        }
        return validator;
    }
}
