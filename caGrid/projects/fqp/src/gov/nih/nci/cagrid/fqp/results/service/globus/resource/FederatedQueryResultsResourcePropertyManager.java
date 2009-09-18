package gov.nih.nci.cagrid.fqp.results.service.globus.resource;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault;
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.fqp.results.metadata.FederatedQueryExecutionStatus;
import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.results.metadata.ResultsRange;
import org.cagrid.fqp.results.metadata.ServiceConnectionStatus;
import org.cagrid.fqp.results.metadata.TargetServiceStatus;
import org.globus.wsrf.ResourceException;

/**
 * Utility for managing the status resource property of the
 * Federated Query Processor Results Resource
 * 
 * TODO: This class needs to call the store() method on the resource to
 * persist the resource property changes
 * 
 * @author ervin
 */
public class FederatedQueryResultsResourcePropertyManager {

    private FederatedQueryResultsResource managedResource = null;
    
    public FederatedQueryResultsResourcePropertyManager(FederatedQueryResultsResource resource) {
        this.managedResource = resource;
    }
    
    
    public synchronized void setExecutionDetailMessage(String message) throws ResourceException {
        FederatedQueryExecutionStatus executionStatus = getExecutionStatus();
        executionStatus.setExecutionDetails(message);
        storeExecutionStatus(executionStatus);
    }
    
    
    public synchronized void setProcessingStatus(ProcessingStatus status) throws ResourceException {
        FederatedQueryExecutionStatus executionStatus = getExecutionStatus();
        executionStatus.setCurrentStatus(status);
        storeExecutionStatus(executionStatus);
    }
    
    
    public synchronized void setTargetServiceConnectionStatusOk(String serviceURL) throws InternalErrorFault, ResourceException {
        TargetServiceStatus status = getTargetServiceStatus(serviceURL);
        status.setConnectionStatus(ServiceConnectionStatus.OK);
        storeTargetServiceStatus(status);
    }
    
    
    public synchronized void setServiceResultsRange(String serviceURL, ResultsRange range) throws InternalErrorFault, ResourceException {
        TargetServiceStatus status = getTargetServiceStatus(serviceURL);
        status.setResultsRange(range);
        storeTargetServiceStatus(status);
    }
    
    
    public synchronized void setTargetServiceConnectionStatusRefused(String serviceURL) throws InternalErrorFault, ResourceException {
        TargetServiceStatus status = getTargetServiceStatus(serviceURL);
        status.setConnectionStatus(ServiceConnectionStatus.Could_Not_Connect);
        storeTargetServiceStatus(status);
    }
    
    
    public synchronized void setTargetServiceConnectionStatusException(String serviceURL, Exception ex) throws InternalErrorFault, ResourceException {
        TargetServiceStatus status = getTargetServiceStatus(serviceURL);
        status.setConnectionStatus(ServiceConnectionStatus.Exception);
        FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
        helper.addDescription("Error parsing data service URL");
        helper.addDescription(ex.getMessage());
        helper.addFaultCause(ex);
        FederatedQueryProcessingFault fqpFault = (FederatedQueryProcessingFault) helper.getFault();
        status.setBaseFault(fqpFault);
        storeTargetServiceStatus(status);
    }
    
    
    public synchronized void setTargetServiceResultsRange(String serviceURL, int lowerIndex, int count) throws InternalErrorFault, ResourceException {
        TargetServiceStatus status = getTargetServiceStatus(serviceURL);
        ResultsRange range = new ResultsRange(lowerIndex, lowerIndex + count);
        status.setResultsRange(range);
        storeTargetServiceStatus(status);
    }
    
    
    private TargetServiceStatus getTargetServiceStatus(String serviceURL) throws InternalErrorFault, ResourceException {
        FederatedQueryExecutionStatus executionStatus = getExecutionStatus();
        TargetServiceStatus status = null;
        if (executionStatus.getTargetServiceStatus() == null) {
            executionStatus.setTargetServiceStatus(new TargetServiceStatus[0]);
        }
        for (TargetServiceStatus storedStatus : executionStatus.getTargetServiceStatus()) {
            if (storedStatus.getServiceURL().toString().equals(serviceURL)) {
                status = storedStatus;
                break;
            }
        }
        if (status == null) {
            status = new TargetServiceStatus();
            try {
                status.setServiceURL(new URI(serviceURL));
            } catch (MalformedURIException ex) {
                FaultHelper helper = new FaultHelper(new InternalErrorFault());
                helper.addDescription("Error parsing data service URL");
                helper.addDescription(ex.getMessage());
                helper.addFaultCause(ex);
                throw (InternalErrorFault) helper.getFault();
            }
            TargetServiceStatus[] statusArray = 
                (TargetServiceStatus[]) Utils.appendToArray(executionStatus.getTargetServiceStatus(), status);
            executionStatus.setTargetServiceStatus(statusArray);
        }
        return status;
    }
    
    
    private void storeTargetServiceStatus(TargetServiceStatus targetStatus) throws ResourceException {
        FederatedQueryExecutionStatus executionStatus = getExecutionStatus();
        if (executionStatus.getTargetServiceStatus() == null || executionStatus.getTargetServiceStatus().length == 0) {
            executionStatus.setTargetServiceStatus(new TargetServiceStatus[] {targetStatus});
        } else {
            for (int i = 0; i < executionStatus.getTargetServiceStatus().length; i++) {
                if (executionStatus.getTargetServiceStatus()[i].getServiceURL().equals(targetStatus.getServiceURL())) {
                    executionStatus.setTargetServiceStatus(i, targetStatus);
                }
            }
        }
        storeExecutionStatus(executionStatus);
    }
    
    
    private FederatedQueryExecutionStatus getExecutionStatus() throws ResourceException {
        FederatedQueryExecutionStatus status = managedResource.getFederatedQueryExecutionStatus();
        if (status == null) {
            status = new FederatedQueryExecutionStatus();
            storeExecutionStatus(status);
        }
        return managedResource.getFederatedQueryExecutionStatus();
    }
    
    
    /**
     * Every time this is called, a notification event will go out to all subscribers
     * to the FederatedQueryExecutionStatus type.
     * 
     * @param executionStatus
     * @throws ResourceException
     */
    private void storeExecutionStatus(FederatedQueryExecutionStatus executionStatus) throws ResourceException {
        managedResource.setFederatedQueryExecutionStatus(executionStatus);
    }
}
