package gov.nih.nci.cagrid.fqp.processor;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.dcqlresult.DCQLResult;
import gov.nih.nci.cagrid.fqp.common.FQPConstants;
import gov.nih.nci.cagrid.fqp.common.SerializationUtils;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.processor.exceptions.RemoteDataServiceException;

import java.io.StringWriter;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.fqp.execution.TargetDataServiceQueryBehavior;
import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.results.metadata.ResultsRange;
import org.globus.gsi.GlobusCredential;


/**
 * FederatedQueryEngine
 * Performs federated query processing, broadcasting, and aggregation
 * 
 * @author David Ervin
 */
public class FederatedQueryEngine {
    // default number of worker threads
    public static final int DEFAULT_POOL_SIZE = 5;

    private static Log LOG = LogFactory.getLog(FederatedQueryEngine.class.getName());
    
    private GlobusCredential credential = null;
    private QueryExecutionParameters executionParameters = null;
    private ExecutorService workExecutor = null;
    private List<FQPProcessingStatusListener> statusListeners = null;

    /**
     * Creates a new federated query engine instance.  A default thread pool 
     * will be used for query execution tasks. Either or both parameters of 
     * this constructor may be null.
     * 
     * @param credential
     *      The globus credential to be used when making queries against data services (may be null)
     * @param executionParameters
     *      The query execution parameters (may be null)
     */
    public FederatedQueryEngine(GlobusCredential credential, QueryExecutionParameters executionParameters) {
        this(credential, executionParameters, null);
    }
    
    
    /**
     * Creates a new federated query engine instance.
     * 
     * @param credential
     *      The globus credential to be used when making queries against data services (may be null)
     * @param executionParameters
     *      The query execution parameters (may be null)
     * @param workExecutor
     *      The executor instance which will handle scheduling and execution of query processing tasks
     */
    public FederatedQueryEngine(GlobusCredential credential, QueryExecutionParameters executionParameters, ExecutorService workExecutor) {
        this.credential = credential;
        if (executionParameters == null) {
            this.executionParameters = FQPConstants.DEFAULT_QUERY_EXECUTION_PARAMETERS;
        } else {
            this.executionParameters = executionParameters;
        }
        if (workExecutor == null) {
            LOG.debug("Creating threaded work executor with " + DEFAULT_POOL_SIZE + " threads");
            this.workExecutor = Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
        } else {
            this.workExecutor = workExecutor;
        }
        this.statusListeners = new LinkedList<FQPProcessingStatusListener>();
    }


    /**
     * Call Federated Query Processor, and send the generated CQLQuery to each
     * targeted service, placing each results into a single DCQLQueryResults
     * object.
     * 
     * @param dcqlQuery
     * @return The results of executing the DCQL query
     * @throws FederatedQueryProcessingException
     */
    public DCQLQueryResultsCollection execute(DCQLQuery dcqlQuery) throws FederatedQueryProcessingException {
        // determine if errors cause immediate failure
        boolean failFast = true;
        if (executionParameters.getTargetDataServiceQueryBehavior().getFailOnFirstError() != null) {
            failFast =
                executionParameters.getTargetDataServiceQueryBehavior().getFailOnFirstError().booleanValue();
        }
        
        fireProcessingStatusChanged(ProcessingStatus.Processing, "Begining query processing");
        
        // create a new processor instance and debug the query
        FederatedQueryProcessor processor = new FederatedQueryProcessor(credential);
        debugDCQLQuery("Beginning processing of DCQL", dcqlQuery);

        // allow the processor to convert the DCQL into a CQL query
        LOG.debug("Processing DCQL to single CQL query");
        CQLQuery cqlQuery = processor.processDCQLQuery(dcqlQuery.getTargetObject());
        
        fireProcessingStatusChanged(ProcessingStatus.Processing, "Broadcasting final CQL to target data services");
        
        // create tasks for each target data service
        FutureGroupFailureListener failureListener = new FutureGroupFailureListener();
        List<Callable<CQLQueryResults>> queryTasks = new ArrayList<Callable<CQLQueryResults>>();
        for (String serviceURL : dcqlQuery.getTargetServiceURL()) {
            QueryExecutionTask task = new QueryExecutionTask(serviceURL, cqlQuery, credential, executionParameters, failureListener);
            queryTasks.add(task);
        }
        // invoke all query tasks and wait for their completion
        LOG.debug("Invoking query tasks, awaiting completion");
        List<Future<CQLQueryResults>> queryFutures = null;
        try {
            queryFutures = workExecutor.invokeAll(queryTasks);
            for (Future<?> queryFuture : queryFutures) {
                failureListener.addFuture(queryFuture);
            }
        } catch (InterruptedException ex) {
            throw new FederatedQueryProcessingException("Unable to schedule query tasks: " + ex.getMessage(), ex);
        }
        LOG.debug("Work scheduled");
        
        // compile results from all the workers
        boolean targetServiceError = false;
        int totalObjectResults = 0;
        List<DCQLResult> dcqlResults = new LinkedList<DCQLResult>();
        for (int i = 0; i < queryFutures.size(); i++) {
            Future<CQLQueryResults> future = queryFutures.get(i);
            String serviceURL = dcqlQuery.getTargetServiceURL(i);
            CQLQueryResults results = null;
            try {
                results = future.get();
                // if results are null, it means some exception was thrown by the 
                DCQLResult dcqlResult = new DCQLResult();
                dcqlResult.setTargetServiceURL(serviceURL);
                dcqlResult.setCQLQueryResultCollection(results);
                dcqlResults.add(dcqlResult);
                int resultsCount = results.getObjectResult() != null ? results.getObjectResult().length : 0;
                // fire results range for target service
                ResultsRange range = new ResultsRange();
                range.setStartElementIndex(totalObjectResults);
                range.setEndElementIndex(totalObjectResults + resultsCount);
                fireServiceResultsRange(serviceURL, range);
                totalObjectResults += resultsCount;
            } catch (InterruptedException ex) {
                // should only be thrown because some query task died with failFast == true
                throw new FederatedQueryProcessingException(ex);
            } catch (ExecutionException ex) {
                targetServiceError = true;
                if (failFast) {
                    throw new FederatedQueryProcessingException(ex);
                }
            }
        }
        
        LOG.debug("Compiling results as DCQL query results collection");
        DCQLQueryResultsCollection resultsCollection = new DCQLQueryResultsCollection();
        DCQLResult[] resultArray = new DCQLResult[dcqlResults.size()];
        dcqlResults.toArray(resultArray);
        resultsCollection.setDCQLResult(resultArray);
        
        ProcessingStatus status = targetServiceError ? ProcessingStatus.Complete_With_Error : ProcessingStatus.Complete;
        fireProcessingStatusChanged(status, "Query processing complete");
        
        return resultsCollection;
    }


    /**
     * Call Federated Query Processor, and send the generated CQLQuery to each
     * targeted service, aggregating the results into a single CQLQueryResults
     * object.
     * 
     * @param dcqlQuery
     * @return Aggregated results of the DCQL query
     * @throws FederatedQueryProcessingException
     */
    public CQLQueryResults executeAndAggregateResults(DCQLQuery dcqlQuery) throws FederatedQueryProcessingException {
        // perform the DCQL query as normal, then aggregate
        DCQLQueryResultsCollection dcqlResults = execute(dcqlQuery);
        
        LOG.debug("Aggregating DCQL results");
        String targetClassname = dcqlQuery.getTargetObject().getName();
        CQLQueryResults aggregate = DCQLAggregator.aggregateDCQLResults(dcqlResults, targetClassname);
        
        return aggregate;
    }


    private void debugDCQLQuery(String logMessage, DCQLQuery dcqlQuery) {
        if (LOG.isDebugEnabled()) {
            try {
                StringWriter s = new StringWriter();
                SerializationUtils.serializeDCQLQuery(dcqlQuery, s);
                LOG.debug(logMessage + ":\n" + s.toString());
                s.close();
            } catch (Exception e) {
                LOG.error("Problem in debug printout of DCQL query:" + e.getMessage(), e);
            }
        }
    }
    
    
    // ------------------------------
    // status listener implementation
    // ------------------------------
    
    
    public void addStatusListener(FQPProcessingStatusListener listener) {
        this.statusListeners.add(listener);
    }
    
    
    public FQPProcessingStatusListener[] getStatusListeners() {
        FQPProcessingStatusListener[] listeners = new FQPProcessingStatusListener[statusListeners.size()];
        statusListeners.toArray(listeners);
        return listeners;
    }
    
    
    public boolean removeStatusListener(FQPProcessingStatusListener listener) {
        return statusListeners.remove(listener);
    }
    
    
    protected synchronized void fireStatusOk(String serviceURL) {
        LOG.debug("Fire status OK");
        for (FQPProcessingStatusListener listener : statusListeners) {
            listener.targetServiceOk(serviceURL);
        }
    }
    
    
    protected synchronized void fireServiceResultsRange(String serviceURL, ResultsRange range) {
        LOG.debug("Fire service results range");
        for (FQPProcessingStatusListener listener: statusListeners) {
            listener.targetServiceReturnedResults(serviceURL, range);
        }
    }
    
    
    protected synchronized void fireConnectionRefused(String serviceURL) {
        LOG.debug("Fire connection refused");
        for (FQPProcessingStatusListener listener : statusListeners) {
            listener.targetServiceConnectionRefused(serviceURL);
        }
    }
    
    
    protected synchronized void fireServiceExeption(String serviceURL, Exception ex) {
        LOG.debug("Fire service exception");
        for (FQPProcessingStatusListener listener : statusListeners) {
            listener.targetServiceThrowsException(serviceURL, ex);
        }
    }
    
    
    protected synchronized void fireInvalidResult(String serviceURL, FederatedQueryProcessingException ex) {
        LOG.debug("Fire invalid result");
        for (FQPProcessingStatusListener listener : statusListeners) {
            listener.targetServiceReturnedInvalidResult(serviceURL, ex);
        }
    }
    
    
    protected synchronized void fireProcessingStatusChanged(ProcessingStatus status, String message) {
        LOG.debug("Fire processing status changed");
        for (FQPProcessingStatusListener listener : statusListeners) {
            listener.processingStatusChanged(status, message);
        }
    }
    
    
    // ---------------------
    // query execution magic
    // ---------------------
    
    
    private class QueryExecutionTask implements Callable<CQLQueryResults> {
        
        private String serviceURL = null;
        private CQLQuery query = null;
        private GlobusCredential clientCredential = null;
        private QueryExecutionParameters queryParameters = null;
        
        private FutureGroupFailureListener failListener = null;
        
        public QueryExecutionTask(String serviceUrl, CQLQuery query, GlobusCredential clientCredential, 
            QueryExecutionParameters queryParameters, FutureGroupFailureListener failListener) {
            this.serviceURL = serviceUrl;
            this.query = query;
            this.clientCredential = clientCredential;
            this.queryParameters = queryParameters;
            this.failListener = failListener;
        }


        public CQLQueryResults call() throws FederatedQueryProcessingException {
            // get the target service query behavior
            TargetDataServiceQueryBehavior behavior = 
                queryParameters.getTargetDataServiceQueryBehavior();
            boolean failFast = true;
            if (behavior.getFailOnFirstError() != null) {
                failFast = behavior.getFailOnFirstError().booleanValue();
            }
            
            // prepare the retry counters
            int maxRetries = 0;
            if (!failFast) {
                maxRetries = behavior.getRetries() != null ?
                behavior.getRetries().intValue() : 
                FQPConstants.DEFAULT_TARGET_QUERY_BEHAVIOR.getRetries().intValue();
            }
            int tryCount = 0;
            long retryTimeout = (behavior.getTimeoutPerRetry() != null ?
                behavior.getTimeoutPerRetry().intValue() :
                    FQPConstants.DEFAULT_TARGET_QUERY_BEHAVIOR.getTimeoutPerRetry().intValue())
                    * 1000; // milliseconds
            
            // run the query, accounting for the max number of retries
            CQLQueryResults results = null;
            RemoteDataServiceException queryException = null;
            do {
                tryCount++;
                try {
                    results = DataServiceQueryExecutor.queryDataService(
                        query, serviceURL, clientCredential);
                    // clear any previous exception state.  
                    // If this query succeeded and a previous one failed, 
                    // the exception is erroneous
                    queryException = null;
                } catch (RemoteDataServiceException ex) {
                    LOG.warn("Query failed to execute: " + ex.getMessage());
                    queryException = ex;
                    if (ex.getCause().getCause() instanceof ConnectException && tryCount < maxRetries) {
                        // connection refused, so we'll come back later
                        try {
                            LOG.debug("Waiting " + retryTimeout + "ms and trying again...");
                            Thread.sleep(retryTimeout);
                        } catch (InterruptedException iex) {
                            // these things happen
                        }
                    }
                }
            } while (results == null && tryCount < maxRetries);
            
            // verify we have Object results
            boolean invalidQueryResponse = false;
            if (results != null) {
                if (results.getAttributeResult() != null || results.getCountResult() != null || results.getIdentifierResult() != null) {
                    invalidQueryResponse = true;
                    queryException = new RemoteDataServiceException(
                        "Remote data service " + serviceURL + " returned non-object results");
                } else {
                    // verify the results type
                    if (!results.getTargetClassname().equals(query.getTarget().getName())) {
                        invalidQueryResponse = true;
                        queryException = new RemoteDataServiceException("Data service (" + serviceURL
                            + ") returned results of type (" + results.getTargetClassname() 
                            + ") when type (" + query.getTarget().getName() + ") was requested!");
                    } else {
                        // all is well
                        fireStatusOk(serviceURL);
                    }
                }
            } else if (queryException == null) {
                // not sure how we could have no results AND no exception, but...
                invalidQueryResponse = true;
                queryException = new RemoteDataServiceException(
                    "Remote data service " + serviceURL + " returned no results at all");
            }
            
            if (queryException != null) {
                boolean isConnectException = false;
                Throwable cause = queryException;
                while (cause != null && !isConnectException) {
                    isConnectException = cause instanceof ConnectException;
                    cause = cause.getCause();
                }
                if (isConnectException) {
                    // connect exception is a special case
                    fireConnectionRefused(serviceURL);
                } else if (invalidQueryResponse) {
                    // data service did something REALLY unexpected
                    fireInvalidResult(serviceURL, queryException);
                } else {
                    // non-specific query failure...
                    fireServiceExeption(serviceURL, queryException);
                }
                try {
                    throw queryException;
                } finally {
                    if (failFast) {
                        LOG.error("Failing entire query...");
                        failListener.failAllFutures();
                    }
                }
            }
            
            return results;
        }
    }
    
    
    private static class FutureGroupFailureListener {
        private List<Future<?>> futures = null;
        
        public FutureGroupFailureListener() {
            this.futures = new LinkedList<Future<?>>();
        }
        
        
        public synchronized void addFuture(Future<?> f) {
            this.futures.add(f);
        }
        
        
        public synchronized void failAllFutures() {
            for (Future<?> future : futures) {
                future.cancel(true);
            }
        }
    }
}
