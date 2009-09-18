package gov.nih.nci.cagrid.fqp.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.fqp.processor.FederatedQueryEngine;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.results.service.globus.resource.FederatedQueryResultsResourceHome;
import gov.nih.nci.cagrid.fqp.results.stubs.types.FederatedQueryResultsReference;
import gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault;
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class FederatedQueryProcessorImpl extends FederatedQueryProcessorImplBase {
    private static final int DEFAULT_POOL_SIZE = 10;

    protected static Log LOG = LogFactory.getLog(FederatedQueryProcessorImpl.class.getName());

    private FQPAsynchronousExecutionUtil asynchronousExecutor = null;
    private ThreadPoolExecutor workManager = null;
    private QueryConstraintsValidator queryConstraintsValidator = null;


    public FederatedQueryProcessorImpl() throws RemoteException {
        super();
        FederatedQueryProcessorConfiguration fqpConfig = null;
        try {
            fqpConfig = getConfiguration();
        } catch (Exception ex) {
            throw new RemoteException("Error initializing federated query processor configuration object: "
                + ex.getMessage());
        }
        queryConstraintsValidator = new QueryConstraintsValidator(fqpConfig);
    }


    public gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection execute(gov.nih.nci.cagrid.dcql.DCQLQuery query)
        throws RemoteException, gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault {
        // validate the query constraints before trying to do anything else
        try {
            queryConstraintsValidator.validateAgainstConstraints(query, null);
        } catch (FederatedQueryProcessingException ex) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.addDescription("Query or query execution parameters violate this service's query constraints");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
        FederatedQueryEngine engine = new FederatedQueryEngine(null, null, getWorkExecutorService());
        DCQLQueryResultsCollection results = null;
        try {
            results = engine.execute(query);
        } catch (FederatedQueryProcessingException e) {
            LOG.error("Problem executing query: " + e.getMessage());
            FederatedQueryProcessingFault fault = new FederatedQueryProcessingFault();
            fault.setFaultString("Problem executing query: " + e.getMessage());
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            throw helper.getFault();
        }
        return results;
    }


    public gov.nih.nci.cagrid.cqlresultset.CQLQueryResults executeAndAggregateResults(
        gov.nih.nci.cagrid.dcql.DCQLQuery query) throws RemoteException,
        gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault {
        // validate the query constraints before trying to do anything else
        try {
            queryConstraintsValidator.validateAgainstConstraints(query, null);
        } catch (FederatedQueryProcessingException ex) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.addDescription("Query or query execution parameters violate this service's query constraints");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
        FederatedQueryEngine engine = new FederatedQueryEngine(null, null, getWorkExecutorService());
        CQLQueryResults results = null;
        try {
            results = engine.executeAndAggregateResults(query);
        } catch (FederatedQueryProcessingException e) {
            LOG.error("Problem executing query: " + e.getMessage(), e);
            FederatedQueryProcessingFault fault = new FederatedQueryProcessingFault();
            fault.setFaultString("Problem executing query: " + e.getMessage());
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            throw helper.getFault();
        }
        return results;
    }


    public gov.nih.nci.cagrid.fqp.results.stubs.types.FederatedQueryResultsReference executeAsynchronously(
        gov.nih.nci.cagrid.dcql.DCQLQuery query) throws RemoteException {
        // validate the query constraints before trying to do anything else
        try {
            queryConstraintsValidator.validateAgainstConstraints(query, null);
        } catch (FederatedQueryProcessingException ex) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.addDescription("Query or query execution parameters violate this service's query constraints");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
        FederatedQueryResultsReference ref = null;
        try {
            ref = getAsynchronousExecutor().executeAsynchronousQuery(query, null, null);
        } catch (FederatedQueryProcessingException ex) {
            throw new RemoteException("Error setting up resource: " + ex.getMessage(), ex);
        }
        return ref;
    }


    public gov.nih.nci.cagrid.fqp.results.stubs.types.FederatedQueryResultsReference query(
        gov.nih.nci.cagrid.dcql.DCQLQuery query,
        org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference delegatedCredentialReference,
        org.cagrid.fqp.execution.QueryExecutionParameters queryExecutionParameters) throws RemoteException,
        gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault,
        gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault {
        // validate the query constraints before trying to do anything else
        try {
            queryConstraintsValidator.validateAgainstConstraints(query, queryExecutionParameters);
        } catch (FederatedQueryProcessingException ex) {
            FaultHelper helper = new FaultHelper(new FederatedQueryProcessingFault());
            helper.addDescription("Query or query execution parameters violate this service's query constraints");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (FederatedQueryProcessingFault) helper.getFault();
        }
        // execute the query
        FederatedQueryResultsReference ref = null;
        try {
            ref = getAsynchronousExecutor().executeAsynchronousQuery(
                query, delegatedCredentialReference, queryExecutionParameters);
        } catch (FederatedQueryProcessingException ex) {
            throw new RemoteException("Error setting up resource: " + ex.getMessage(), ex);
        }
        return ref;
    }


    public synchronized ExecutorService getWorkExecutorService() {
        if (this.workManager == null) {
            int poolSize = DEFAULT_POOL_SIZE;
            try {
                String poolString = getConfiguration().getThreadPoolSize();
                LOG.debug("ThreadPoolSize property was:" + poolString);
                poolSize = Integer.parseInt(poolString);
            } catch (Exception e) {
                LOG.error("Problem determing pool size, using default(" + poolSize + ").", e);
            }
            // thread factory creates daemon threads so the executor shuts down
            // with the JVM
            ThreadFactory threadFactory = new ThreadFactory() {
                ThreadFactory base = Executors.defaultThreadFactory();
                private int numThreads = 0;

                public Thread newThread(Runnable runnable) {
                    LOG.debug("CREATING THREAD #" + numThreads + " FOR THE POOL");
                    Thread t = base.newThread(runnable);
                    t.setDaemon(true);
                    numThreads++;
                    return t;
                }
            };
            this.workManager = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize, threadFactory);
            // adds shutdown hook to the JVM to force work manager shutdown
            LOG.debug("Adding JVM shutdown hook to terminate federated query execution thread pool");
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    LOG.info("Running FQP execution shutdown hook.");
                    workManager.setKeepAliveTime(0, TimeUnit.SECONDS);
                    List<Runnable> remainingTasks = workManager.shutdownNow();
                    LOG.info("There were " + remainingTasks.size() + " tasks left in the worker queue at shutdown.");
                    LOG.info("FQP work manager has been shut down, awaiting termination.");
                    try {
                        workManager.awaitTermination(15, TimeUnit.SECONDS);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        LOG.error("Execption caught while waiting for FQP execution shutdown", ex);
                    }
                    LOG.info("Done waiting for FQP work manager to terminate.");
                    if (workManager.isShutdown()) {
                        LOG.info("FQP execution pool has been shut down.");
                    } else {
                        LOG.error("FQP execution pool was NOT shut down.");
                    }
                    if (workManager.isTerminated()) {
                        LOG.info("FQP execution pool has been terminated.");
                    } else {
                        LOG.error("FQP execution pool was NOT terminated.");
                    }
                }
            });
        }

        return this.workManager;
    }


    private synchronized FQPAsynchronousExecutionUtil getAsynchronousExecutor() throws InternalErrorFault {
        if (asynchronousExecutor == null) {
            // get FQP result resource home
            FederatedQueryResultsResourceHome resultHome = null;
            try {
                resultHome = getFederatedQueryResultsResourceHome();
            } catch (Exception e) {
                LOG.error("Problem locating result home: " + e.getMessage(), e);
                InternalErrorFault fault = new InternalErrorFault();
                fault.setFaultString("Problem locating result home:" + e.getMessage());
                FaultHelper helper = new FaultHelper(fault);
                helper.addFaultCause(e);
                throw (InternalErrorFault) helper.getFault();
            }

            // determine the resource lease time
            int leaseMinutes = -1;
            try {
                String leaseProp = getConfiguration().getInitialResultLeaseInMinutes();
                LOG.debug("Result Lease Minutes property was:" + leaseProp);
                leaseMinutes = Integer.parseInt(leaseProp);
            } catch (Exception e) {
                LOG.error("Problem determing result lease duration, using default");
            }

            // create the executor instance
            if (leaseMinutes == -1) {
                asynchronousExecutor = new FQPAsynchronousExecutionUtil(
                    resultHome, getWorkExecutorService());
            } else {
                asynchronousExecutor = new FQPAsynchronousExecutionUtil(
                    resultHome, getWorkExecutorService(), leaseMinutes);
            }
        }
        return asynchronousExecutor;
    }
}
