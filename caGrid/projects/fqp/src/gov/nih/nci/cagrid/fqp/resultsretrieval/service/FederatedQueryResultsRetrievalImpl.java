package gov.nih.nci.cagrid.fqp.resultsretrieval.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.fqp.processor.DCQL2Aggregator;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.results.stubs.types.ProcessingNotCompleteFault;
import gov.nih.nci.cagrid.fqp.resultsretrieval.service.globus.resource.FederatedQueryResultsRetrievalResource;
import gov.nih.nci.cagrid.fqp.resultsretrieval.utils.FQPResultsEnumerationUtil;
import gov.nih.nci.cagrid.fqp.resultsretrieval.utils.FQPResultsTransferUtil;
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import java.rmi.RemoteException;

import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.4
 * 
 */
public class FederatedQueryResultsRetrievalImpl extends FederatedQueryResultsRetrievalImplBase {

    public FederatedQueryResultsRetrievalImpl() throws RemoteException {
        super();
    }

  public boolean isProcessingComplete() throws RemoteException {
        FederatedQueryResultsRetrievalResource resource = getResource();
        return resource.isComplete();
    }

  public org.cagrid.fqp.results.metadata.FederatedQueryExecutionStatus getExecutionStatus() throws RemoteException {
        return getResource().getFederatedQueryExecutionStatus();
    }

  public org.cagrid.cql2.results.CQLQueryResults getAggregateResults() throws RemoteException, gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault, gov.nih.nci.cagrid.fqp.results.stubs.types.ProcessingNotCompleteFault, gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault {
        FederatedQueryResultsRetrievalResource resource = getResource();
        testForErrors(resource);
        DCQLQueryResultsCollection dcqlResults = resource.getResults();
        CQLQueryResults cqlResults = null;
        try {
            cqlResults = DCQL2Aggregator.aggregateDCQLResults(dcqlResults, 
                resource.getQuery().getTargetObject().getName(), resource.getQuery().getQueryModifier());
        } catch (FederatedQueryProcessingException e) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.setDescription(e.getMessage());
            helper.addFaultCause(e);
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
        return cqlResults;
    }

  public org.cagrid.data.dcql.results.DCQLQueryResultsCollection getResults() throws RemoteException, gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault, gov.nih.nci.cagrid.fqp.results.stubs.types.ProcessingNotCompleteFault, gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault {
        FederatedQueryResultsRetrievalResource resource = getResource();
        testForErrors(resource);
        return resource.getResults();
    }

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer enumerate() throws RemoteException, gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault, gov.nih.nci.cagrid.fqp.results.stubs.types.ProcessingNotCompleteFault, gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault {
        CQLQueryResults aggregateResults = getAggregateResults();
        EnumerationResponseContainer response = null;
        try {
            response = FQPResultsEnumerationUtil.setUpEnumeration(aggregateResults);
        } catch (FederatedQueryProcessingException ex) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.addDescription("Error setting up WS-Enumeration of results");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
        
        return response;
    }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference transfer() throws RemoteException, gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault, gov.nih.nci.cagrid.fqp.results.stubs.types.ProcessingNotCompleteFault, gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault {
        FederatedQueryResultsRetrievalResource resource = getResource();
        testForErrors(resource);
        DCQLQueryResultsCollection results = resource.getResults();
        TransferServiceContextReference transferRef = null;
        try {
            transferRef = FQPResultsTransferUtil.setUpTransfer(results);
        } catch (FederatedQueryProcessingException ex) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.addDescription("Error setting up caGrid Transfer of results");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
        return transferRef;
    }

    private void testForErrors(FederatedQueryResultsRetrievalResource resource) 
        throws ProcessingNotCompleteFault, FederatedQueryProcessingFault {
        // check for error conditions
        if (!resource.isComplete()) {
            FaultHelper helper = new FaultHelper(new ProcessingNotCompleteFault());
            helper.addDescription("Processing is not complete");
            throw (ProcessingNotCompleteFault) helper.getFault();
        }
        if (resource.getProcessingException() != null) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.addDescription("Error processing the query");
            helper.addDescription(resource.getProcessingException().getMessage());
            helper.addFaultCause(resource.getProcessingException());
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
    }
    
    
    private static FederatedQueryResultsRetrievalResource getResource() throws ResourceException, ResourceContextException {
        FederatedQueryResultsRetrievalResource resource = (FederatedQueryResultsRetrievalResource) 
            ResourceContext.getResourceContext().getResource();
        return resource;
    }
}
