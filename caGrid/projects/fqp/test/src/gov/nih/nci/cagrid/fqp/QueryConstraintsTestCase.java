package gov.nih.nci.cagrid.fqp;

import java.rmi.RemoteException;

import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.fqp.execution.TargetDataServiceQueryBehavior;

import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.common.FQPConstants;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.service.FederatedQueryProcessorConfiguration;
import gov.nih.nci.cagrid.fqp.service.QueryConstraintsValidator;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * QueryConstraintsTestCase
 * Tests the query constraints validator utility
 * 
 * @author David
 */
public class QueryConstraintsTestCase extends TestCase {
    
    public static final int MAX_TARGET_SERVICES = 12;
    public static final int MAX_RETRY_TIMEOUT = 30;
    public static final int MAX_RETRIES = 10;


    public QueryConstraintsTestCase(String name) {
        super(name);
    }
    
    
    public void testTooManyTargetServices() {
        DCQLQuery query = new DCQLQuery();
        String[] targetServices = new String[MAX_TARGET_SERVICES + 1];
        for (int i = 0; i < targetServices.length; i++) {
            String targetServiceUrl = "http://some.service/" + i;
            targetServices[i] = targetServiceUrl;
        }
        query.setTargetServiceURL(targetServices);
        try {
            getValidator().validateAgainstConstraints(query, FQPConstants.DEFAULT_QUERY_EXECUTION_PARAMETERS);
            fail("Validator should have failed too many target services, but passed!");
        } catch (FederatedQueryProcessingException ex) {
            // expected
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown during validation");
        }
    }
    
    
    public void testOkTargetServices() {
        DCQLQuery query = new DCQLQuery();
        String[] targetServices = new String[MAX_TARGET_SERVICES - 1];
        for (int i = 0; i < targetServices.length; i++) {
            String targetServiceUrl = "http://some.service/" + i;
            targetServices[i] = targetServiceUrl;
        }
        query.setTargetServiceURL(targetServices);
        try {
            getValidator().validateAgainstConstraints(query, FQPConstants.DEFAULT_QUERY_EXECUTION_PARAMETERS);
        } catch (FederatedQueryProcessingException ex) {
            ex.printStackTrace();
            fail("Validator should have allowed the number of target services, but failed!");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown during validation");
        }
    }
    
    
    public void testTooManyRetries() {
        DCQLQuery query = new DCQLQuery();
        String[] targetServiceURLs = new String[] {"http://non.existant.service/service"};
        query.setTargetServiceURL(targetServiceURLs);
        QueryExecutionParameters parameters = new QueryExecutionParameters();
        TargetDataServiceQueryBehavior behavior = new TargetDataServiceQueryBehavior();
        behavior.setFailOnFirstError(Boolean.FALSE);
        behavior.setRetries(Integer.valueOf(MAX_RETRIES + 1));
        parameters.setTargetDataServiceQueryBehavior(behavior);
        try {
            getValidator().validateAgainstConstraints(query, parameters);
            fail("Validator should have failed too many retries, but passed!");
        } catch (FederatedQueryProcessingException ex) {
            // expected
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown during validation!");
        }
    }
    
    
    public void testOkRetries() {
        DCQLQuery query = new DCQLQuery();
        String[] targetServiceURLs = new String[] {"http://non.existant.service/service"};
        query.setTargetServiceURL(targetServiceURLs);
        QueryExecutionParameters parameters = new QueryExecutionParameters();
        TargetDataServiceQueryBehavior behavior = new TargetDataServiceQueryBehavior();
        behavior.setFailOnFirstError(Boolean.FALSE);
        behavior.setRetries(Integer.valueOf(MAX_RETRIES - 1));
        parameters.setTargetDataServiceQueryBehavior(behavior);
        try {
            getValidator().validateAgainstConstraints(query, parameters);
        } catch (FederatedQueryProcessingException ex) {
            ex.printStackTrace();
            fail("Validator should have allowed the number of retries, but failed!");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown during validation!");
        }
    }
    
    
    public void testTooLongRetryTimeout() {
        DCQLQuery query = new DCQLQuery();
        String[] targetServiceURLs = new String[] {"http://non.existant.service/service"};
        query.setTargetServiceURL(targetServiceURLs);
        QueryExecutionParameters parameters = new QueryExecutionParameters();
        TargetDataServiceQueryBehavior behavior = new TargetDataServiceQueryBehavior();
        behavior.setFailOnFirstError(Boolean.FALSE);
        behavior.setRetries(Integer.valueOf(MAX_RETRIES - 1));
        behavior.setTimeoutPerRetry(Integer.valueOf(MAX_RETRY_TIMEOUT + 1));
        parameters.setTargetDataServiceQueryBehavior(behavior);
        try {
            getValidator().validateAgainstConstraints(query, parameters);
            fail("Validator should have failed too long retry timeout, but passed!");
        } catch (FederatedQueryProcessingException ex) {
            // expected
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown during validation!");
        }
    }
    
    
    public void testOkRetryTimeout() {
        DCQLQuery query = new DCQLQuery();
        String[] targetServiceURLs = new String[] {"http://non.existant.service/service"};
        query.setTargetServiceURL(targetServiceURLs);
        QueryExecutionParameters parameters = new QueryExecutionParameters();
        TargetDataServiceQueryBehavior behavior = new TargetDataServiceQueryBehavior();
        behavior.setFailOnFirstError(Boolean.FALSE);
        behavior.setRetries(Integer.valueOf(MAX_RETRIES - 1));
        behavior.setTimeoutPerRetry(Integer.valueOf(MAX_RETRY_TIMEOUT - 1));
        parameters.setTargetDataServiceQueryBehavior(behavior);
        try {
            getValidator().validateAgainstConstraints(query, parameters);
        } catch (FederatedQueryProcessingException ex) {
            ex.printStackTrace();
            fail("Validator should have allowed the retry timeout, but failed!");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception thrown during validation!");
        }
    }
    
    
    private QueryConstraintsValidator getValidator() {
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
    

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TestResult result = runner.doRun(new TestSuite(QueryConstraintsTestCase.class));
        System.exit(result.errorCount() + result.failureCount());
    }
}
