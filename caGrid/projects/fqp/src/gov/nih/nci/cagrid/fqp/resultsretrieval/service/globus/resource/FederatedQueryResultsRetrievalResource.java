package gov.nih.nci.cagrid.fqp.resultsretrieval.service.globus.resource;

import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.results.metadata.ResultsRange;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.ResourceException;


/** 
 * The implementation of this FederatedQueryResultsRetrievalResource type.
 * 
 * @created by Introduce Toolkit version 1.4
 * 
 */
public class FederatedQueryResultsRetrievalResource extends FederatedQueryResultsRetrievalResourceBase {
    protected static Log LOG = LogFactory.getLog(FederatedQueryResultsRetrievalResource.class.getName());
    
    // resource property manager
    private ResourcePropertyManager resourcePropertyManager = null;
    
    // initialization values of this resource
    private DCQLQuery query;
    private ExecutorService workExecutor;
    private GlobusCredential delegatedCredential;
    private QueryExecutionParameters executionParameters;
    
    // pending query execution
    private Future queryFuture = null;

    // result values of this resource
    private String statusMessage = null;
    private Exception processingException = null;
    private DCQLQueryResultsCollection queryResults = null;
    
    public FederatedQueryResultsRetrievalResource() {
        super();
    }
    
    
    public void initialize(Object resourceBean,
        QName resourceElementQName,
        Object id) throws ResourceException {
        super.initialize(resourceBean, resourceElementQName, id);
        resourcePropertyManager = new ResourcePropertyManager(this);
    }
    
    
    // ----------------------------------
    // Setters to initialize the resource
    // ----------------------------------
    
    
    /**
     * Sets the query to be executed by this resource
     */
    public void setQuery(DCQLQuery query) {
        this.query = query;
    }
    
    
    /**
     * Sets the ExecutionService instance which will handle the threaded
     * execution of the query
     * @param workExecutor
     */
    public void setWorkExecutor(ExecutorService workExecutor) {
        this.workExecutor = workExecutor;
    }
    
    
    /**
     * <b>OPTIONAL</b>
     * Sets the (delegated) client credential credential.  If supplied, the credential
     * will be used to execute queries against the data services referenced in the query
     * @param credentialRef
     */
    public void setDelegatedCredential(GlobusCredential clientCredential) {
        this.delegatedCredential = clientCredential;
    }
    
    
    /**
     * <b>OPTIONAL</b>
     * Sets the query execution parameters to be used by the federated query engine.
     * @param parameters
     */
    public void setQueryExecutionParameters(QueryExecutionParameters parameters) {
        this.executionParameters = parameters;
    }
    
    
    // -----------------------
    // starts query processing
    // -----------------------
    
    
    public void beginQueryProcessing() throws FederatedQueryProcessingException {
        // the set up for this needs to run synchronously so we can report
        // misconfigurations and setup problems back to the caller
        setStatusMessage("Verifying proper state for query processing");
        // verify the resource is in a state where it can run the query
        if (query == null) {
            throw new IllegalStateException(
                "DCQL query was not set before begining query processing!");
        }
        if (workExecutor == null) {
            throw new IllegalStateException(
                "No work manager set to handle query processing tasks!");
        }
        
        // set up a query processing listener to change resource property status
        AsynchronousFQPProcessingStatusListener listener = getStatusListener();
        
        Runnable queryWork = 
            new QueryExecutionTask(query, delegatedCredential, executionParameters, workExecutor, listener);
        try {
            listener.processingStatusChanged(ProcessingStatus.Waiting_To_Begin, "Scheduling query for execution");
            this.queryFuture = workExecutor.submit(queryWork);
        } catch (RejectedExecutionException ex) {
            String message = "Error scheduling query: " + ex.getMessage();
            listener.processingStatusChanged(ProcessingStatus.Complete_With_Error, message);
            FederatedQueryProcessingException fqpException = new FederatedQueryProcessingException(message, ex);
            setProcessingException(fqpException);
            throw fqpException;
        }
    }
    
    
    /**
     * Additional implementation cancels the query execution task if it hasn't
     * already begun executing
     */
    public void remove() throws ResourceException {
        LOG.debug("Removing resource");
        super.remove();
        if (queryFuture != null) {
            LOG.debug("Canceling query execution work");
            queryFuture.cancel(true);
        }
    }
    
    
    // ------------------------
    // public interface methods
    // ------------------------
    
    
    /**
     * Returns the DCQL query results of the query.
     * @return
     */
    public DCQLQueryResultsCollection getResults() {
        // assumes that results already exist, and isComplete() == true
        return queryResults;
    }
    
    
    /**
     * Indicates if processing of the query is complete
     */
    public boolean isComplete() {
        return this.queryResults != null || this.processingException != null;
    }
    
    
    // ---------------------------------------
    // logging, debugging, and error reporting
    // ---------------------------------------
    
    
    private void setStatusMessage(String message) {
        LOG.debug(message);
        this.statusMessage = message;
        try {
            resourcePropertyManager.setExecutionDetailMessage(message);
        } catch (ResourceException ex) {
            LOG.warn("Error setting execution detail message on resource property: "
                + ex.getMessage(), ex);
        }
    }
    
    
    private void setProcessingException(Exception ex) {
        LOG.error("Error during federated query processing: " + ex.getMessage(), ex);
        this.processingException = ex;
    }


    // ----------------------------
    // things I'm changing around via notification and resource properties
    // ----------------------------


    public String getStatusMessage() {
        return this.statusMessage;
    }
    

    public Exception getProcessingException() {
        return this.processingException;
    }
    
    
    // --------------------------------------
    // listens for status changes and updates the resource properties
    // --------------------------------------
    
    
    private AsynchronousFQPProcessingStatusListener getStatusListener() {
        AsynchronousFQPProcessingStatusListener listener = new AsynchronousFQPProcessingStatusListener() {
            
            public void processingStatusChanged(ProcessingStatus status, String message) {
                try {
                    resourcePropertyManager.setProcessingStatus(status);
                    resourcePropertyManager.setExecutionDetailMessage(message);
                } catch (ResourceException ex) {
                    handleResourceException(ex);
                }
            }
            
            
            public void targetServiceOk(String serviceURL) {
                try {
                    resourcePropertyManager.setTargetServiceConnectionStatusOk(serviceURL);
                } catch (InternalErrorFault ex) {
                    handleInternalError(ex);
                } catch (ResourceException ex) {
                    handleResourceException(ex);
                }
            }
            
            
            public void targetServiceReturnedResults(String serviceURL, ResultsRange range) {
                try {
                    resourcePropertyManager.setServiceResultsRange(serviceURL, range);
                } catch (InternalErrorFault ex) {
                    handleInternalError(ex);
                } catch (ResourceException ex) {
                    handleResourceException(ex);
                }
            }
            
            
            public void targetServiceConnectionRefused(String serviceURL) {
                try {
                    resourcePropertyManager.setTargetServiceConnectionStatusRefused(serviceURL);
                } catch (InternalErrorFault ex) {
                    handleInternalError(ex);
                } catch (ResourceException ex) {
                    handleResourceException(ex);
                }
            }
            
            
            public void targetServiceThrowsException(String serviceURL, Exception exception) {
                try {
                    resourcePropertyManager.setTargetServiceConnectionStatusException(serviceURL, exception);
                } catch (InternalErrorFault ex) {
                    handleInternalError(ex);
                } catch (ResourceException ex) {
                    handleResourceException(ex);
                }
            }
            
            
            public void targetServiceReturnedInvalidResult(String serviceURL, FederatedQueryProcessingException exception) {
                try {
                    resourcePropertyManager.setExecutionDetailMessage("Invalid result returned from target data service " + serviceURL);
                    resourcePropertyManager.setTargetServiceConnectionStatusException(serviceURL, exception);
                } catch (InternalErrorFault ex) {
                    handleInternalError(ex);
                } catch (ResourceException ex) {
                    handleResourceException(ex);
                }
            }
            
            
            private void handleInternalError(InternalErrorFault ex) {
                ex.printStackTrace();
                setProcessingException(ex);
            }
            
            
            private void handleResourceException(ResourceException ex) {
                ex.printStackTrace();
                setProcessingException(ex);
            }
            
            
            public void queryResultsGenerated(DCQLQueryResultsCollection results) {
                queryResults = results;
            }
            
            
            public void queryProcessingException(FederatedQueryProcessingException ex) {
                setProcessingException(ex);
            }
        };
        return listener;
    }
}
