package gov.nih.nci.cagrid.fqp.service;

import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;

import java.rmi.RemoteException;

import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.fqp.execution.TargetDataServiceQueryBehavior;


public class QueryConstraintsValidator {
    
    private int maxTargetServices = 0;
    private int maxTimeout = 0;
    private int maxRetries = 0;
    
    public QueryConstraintsValidator(FederatedQueryProcessorConfiguration configuration) throws RemoteException {
        parseConfiguration(configuration);
    }
    
    
    public void validateAgainstConstraints(DCQLQuery query, QueryExecutionParameters parameters) throws FederatedQueryProcessingException {
        if (!validMaximumTargetServices(query)) {
            throw new FederatedQueryProcessingException(
                "Query specifies more target data services than allowed by this service");
        }
        if (parameters != null) {
            if (!validMaximumTimeout(parameters)) {
                throw new FederatedQueryProcessingException(
                "Query specifies a retry timeout greater than is allowed by this serice");
            }
            if (!validMaximumRetries(parameters)) {
                throw new FederatedQueryProcessingException(
                "Query specifies a greater number of retries than is allowed by this service");
            }
        }
    }
    
    
    private void parseConfiguration(FederatedQueryProcessorConfiguration config) throws RemoteException {
        String maxServicesValue = config.getMaxTargetServicesPerQuery();
        if (maxServicesValue == null || maxServicesValue.length() == 0) {
            throw new RemoteException("Max target services service property not set!");
        }
        try {
            maxTargetServices = Integer.parseInt(maxServicesValue);
        } catch (Exception ex) {
            throw new RemoteException("Error parsing max target services service property: " + ex.getMessage(), ex);
        }
        
        String maxTimeoutValue = config.getMaxRetryTimeout();
        if (maxTimeoutValue == null || maxTimeoutValue.length() == 0) {
            throw new RemoteException("Max retry timeout service property not set!");
        }
        try {
            maxTimeout = Integer.parseInt(maxTimeoutValue);
        } catch (Exception ex) {
            throw new RemoteException("Error parsing max retry timeout service poperty: " + ex.getMessage(), ex);
        }
        
        String maxRetriesValue = config.getMaxRetries();
        if (maxRetriesValue == null || maxRetriesValue.length() == 0) {
            throw new RemoteException("Max retries service property not set!");
        }
        try {
            maxRetries = Integer.parseInt(maxRetriesValue);
        } catch (Exception ex) {
            throw new RemoteException("Error parsing max retries service property: " + ex.getMessage(), ex);
        }
    }
    
    
    private boolean validMaximumTargetServices(DCQLQuery query) {
        return maxTargetServices != 0 && query.getTargetServiceURL().length <= maxTargetServices;
    }
    
    
    private boolean validMaximumTimeout(QueryExecutionParameters parameters) {
        // only need to check this if it will impact the query behavior
        TargetDataServiceQueryBehavior targetBehavior = parameters.getTargetDataServiceQueryBehavior();
        Boolean failFast = targetBehavior.getFailOnFirstError();
        if (failFast != null && !failFast.booleanValue()) {
            if (targetBehavior.getTimeoutPerRetry() != null) {
                return maxTimeout != 0 && targetBehavior.getTimeoutPerRetry().intValue() <= maxTimeout;
            }
        }
        return true;
    }
    
    
    private boolean validMaximumRetries(QueryExecutionParameters parameters) {
        // only need to check this if it will impact the query behavior
        TargetDataServiceQueryBehavior targetBehavior = parameters.getTargetDataServiceQueryBehavior();
        Boolean failFast = targetBehavior.getFailOnFirstError();
        if (failFast != null && !failFast.booleanValue()) {
            if (targetBehavior.getRetries() != null) {
                return maxRetries != 0 && targetBehavior.getRetries().intValue() <= maxRetries;
            }
        }
        return true;
    }
}
