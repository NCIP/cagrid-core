package org.cagrid.fqp.test.remote.secure.steps;

import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;
import gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault;
import gov.nih.nci.cagrid.testing.system.deployment.SecureContainer;

import java.io.File;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.test.common.QueryResultsVerifier;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;

public class DelegatedCredentialQueryStep extends BaseQueryExecutionStep {
    
    private static final Log LOG = LogFactory.getLog(DelegatedCredentialQueryStep.class);
    
    public static final int QUERY_EXECUTION_WAIT_TIME = 20; // seconds
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    public static final String VALID_USER_CERT = "user_cert.pem";
    public static final String VALID_USER_KEY = "user_key.pem";
    public static final String VALID_USER_PROXY = "user.proxy";
    public static final String INVALID_USER_CERT = "user2_cert.pem";
    public static final String INVALID_USER_KEY = "user2_key.pem";
    public static final String INVALID_USER_PROXY = "user2.proxy";
    
    private SecureContainer secureServiceContainer;
    private EndpointReferenceType fqpEPR;
    private DelegatedCredentialReference delegationRef;
    private String[] serviceUrls;
    
    public DelegatedCredentialQueryStep(String queryFilename, String goldFilename,
        EndpointReferenceType fqpEPR, DelegatedCredentialReference delegationRef,
        String[] serviceUrls, SecureContainer secureServiceContainer) {
        super(queryFilename, goldFilename);
        this.secureServiceContainer = secureServiceContainer;
        this.fqpEPR = fqpEPR;
        this.delegationRef = delegationRef;
        this.serviceUrls = serviceUrls;
    }
    

    public void runStep() throws Throwable {
        LOG.debug("Testing with query " + getQueryFilename());
        DCQLQuery query = getCompletedQuery();
        
        queryWithAnonymousCallerIdentity(query);
        
        queryWithWrongCallerIdentity(query);
        
        queryWithValidIdentity(query);
    }
    
    
    private void queryWithAnonymousCallerIdentity(DCQLQuery query) throws Exception {
        LOG.debug("Starting query with no caller identity");
        FederatedQueryProcessorClient fqpClient = new FederatedQueryProcessorClient(fqpEPR);
        fqpClient.setAnonymousPrefered(true);
        
        try {
            queryAndWait(query, fqpClient);
            fail("Expected failure due to use of delegated credential with no caller identity, but no execption was thrown!");
        } catch (Exception ex) {
            boolean internalError = ex instanceof InternalErrorFault;
            if (!internalError) {
                ex.printStackTrace();
                fail("Unexpected exception; should have been an " + InternalErrorFault.class.getSimpleName());
            }
        }
    }
    
    
    private void queryWithWrongCallerIdentity(DCQLQuery query) throws Exception {
        LOG.debug("Starting query with wrong caller identity");
        GlobusCredential cred = getGlobusProxyCredential(INVALID_USER_PROXY);
        FederatedQueryProcessorClient fqpClient = new FederatedQueryProcessorClient(fqpEPR, cred);
        fqpClient.setAnonymousPrefered(false);
        
        try {
            queryAndWait(query, fqpClient);
            fail("Expected failure due to use of delegated credential with different caller identity, but no execption was thrown!");
        } catch (Exception ex) {
            boolean internalError = ex instanceof InternalErrorFault;
            if (!internalError) {
                ex.printStackTrace();
                fail("Unexpected exception; should have been an " + InternalErrorFault.class.getSimpleName());
            }
        }
    }
    
    
    private void queryWithValidIdentity(DCQLQuery query) throws Exception {
        LOG.debug("Starting query with correct caller identity");
        GlobusCredential cred = getGlobusProxyCredential(VALID_USER_PROXY);
        FederatedQueryProcessorClient fqpClient = new FederatedQueryProcessorClient(fqpEPR, cred);
        fqpClient.setAnonymousPrefered(false);
        
        try {
            DCQLQueryResultsCollection results = queryAndWait(query, fqpClient);
            LOG.debug("Verifying results against " + getGoldFilenname());
            DCQLQueryResultsCollection gold = loadGoldDcqlResults();
            QueryResultsVerifier.verifyDcqlResults(results, gold);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception while querying: " + ex.getMessage());
        }
    }
    
    
    private DCQLQuery getCompletedQuery() {
        assertEquals("Unexpected number of service urls", QUERY_URL_PLACEHOLDERS.length, serviceUrls.length);
        LOG.debug("Filling placeholder URLs with real ones");
        DCQLQuery original = deserializeQuery();
        Map<String, String> urlReplacements = new HashMap<String, String>();
        for (int i = 0; i < QUERY_URL_PLACEHOLDERS.length; i++) {
            urlReplacements.put(QUERY_URL_PLACEHOLDERS[i], serviceUrls[i]);
        }
        DCQLQuery replaced = null;
        try {
            replaced = UrlReplacer.replaceUrls(original, urlReplacements);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to replace URL placeholders in DCQL query: " + ex.getMessage());
        }
        return replaced;
    }
    
    
    private DCQLQueryResultsCollection queryAndWait(DCQLQuery query, FederatedQueryProcessorClient fqpClient)
        throws FederatedQueryProcessingException, InternalErrorFault, MalformedURIException, RemoteException {
        LOG.debug("Starting work executor service");
        ThreadFactory deamonThreadFactory = new ThreadFactory() {
            private ThreadFactory base = Executors.defaultThreadFactory();
            
            public Thread newThread(Runnable run) {
                Thread t = base.newThread(run);
                t.setDaemon(true);
                return t;
            }
        };
        Executor exec = Executors.newSingleThreadExecutor(deamonThreadFactory);
        
        LOG.debug("Awaiting results");
        final FederatedQueryResultsClient resultsClient = fqpClient.query(query, delegationRef, null);
        FutureTask<Boolean> queryCompletionTask = new FutureTask<Boolean>(new Callable<Boolean>() {
            public Boolean call() throws RemoteException, InterruptedException {
                boolean complete = false;
                while (!complete) {
                    complete = resultsClient.isProcessingComplete();
                    Thread.sleep(500);
                }
                return Boolean.valueOf(complete);
            }
        });
        exec.execute(queryCompletionTask);
        
        LOG.debug("Checking for completion");
        Boolean complete = Boolean.FALSE;
        try {
            complete = queryCompletionTask.get(QUERY_EXECUTION_WAIT_TIME, TimeUnit.SECONDS);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error while waiting for query to complete: " + ex.getMessage());
        } finally {
            if (!queryCompletionTask.isDone()) {
                queryCompletionTask.cancel(true);
            }
        }
        assertTrue("Query did not complete in the allotted time (" + QUERY_EXECUTION_WAIT_TIME + " sec)", 
            complete != null && complete.booleanValue());
        
        DCQLQueryResultsCollection result = resultsClient.getResults();
        return result;
    }
    
    
    private GlobusCredential getGlobusProxyCredential(String proxy) {
        GlobusCredential cred = null;
        try {
            String fullProxy = new File(secureServiceContainer.getCertificatesDirectory(), proxy).getAbsolutePath();
            cred = new GlobusCredential(fullProxy);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading proxy: " + ex.getMessage());
        }
        return cred;
    }
    
    
    private GlobusCredential getGlobusCredential(String cert, String key) {
        GlobusCredential cred = null;
        try {
            String fullCert = new File(secureServiceContainer.getCertificatesDirectory(), cert).getAbsolutePath();
            String fullKey = new File(secureServiceContainer.getCertificatesDirectory(), key).getAbsolutePath();
            cred = new GlobusCredential(fullCert, fullKey);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading credential: " + ex.getMessage());
        }
        return cred;
    }
}
