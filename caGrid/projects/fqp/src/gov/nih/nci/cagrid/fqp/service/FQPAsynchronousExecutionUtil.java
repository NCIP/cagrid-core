package gov.nih.nci.cagrid.fqp.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.common.FQPConstants;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.results.service.globus.resource.FederatedQueryResultsResource;
import gov.nih.nci.cagrid.fqp.results.service.globus.resource.FederatedQueryResultsResourceHome;
import gov.nih.nci.cagrid.fqp.results.stubs.types.FederatedQueryResultsReference;
import gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault;
import gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;

import org.apache.axis.MessageContext;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.security.SecurityManager;
import org.globus.wsrf.utils.AddressingUtils;

/**
 * FQPAsynchronousExecutionUtil
 * Performs asynchronous execution of a federated query in the context of 
 * a federated query results resource.
 * 
 * @author ervin
 */
public class FQPAsynchronousExecutionUtil {
    
    private static final int DEFAULT_RESULT_LEASE_MINS = 30;
    
    private static Log LOG = LogFactory.getLog(FQPAsynchronousExecutionUtil.class);

    private FederatedQueryResultsResourceHome resourceHome;
    private int leaseDurration = DEFAULT_RESULT_LEASE_MINS;
    private ExecutorService workExecutor = null;
    
    public FQPAsynchronousExecutionUtil(FederatedQueryResultsResourceHome resourceHome, ExecutorService workExecutor) {
        this(resourceHome, workExecutor, DEFAULT_RESULT_LEASE_MINS);
    }
    
    
    public FQPAsynchronousExecutionUtil(FederatedQueryResultsResourceHome resourceHome, ExecutorService workExecutor, int leaseDurration) {
        this.resourceHome = resourceHome;
        this.workExecutor = workExecutor;
        this.leaseDurration = leaseDurration;
    }


    public synchronized FederatedQueryResultsReference executeAsynchronousQuery(
        DCQLQuery query, DelegatedCredentialReference delegatedCredential, QueryExecutionParameters executionParameters) 
        throws InternalErrorFault, FederatedQueryProcessingException {
        // use the resource home to create an FQP result resource
        FederatedQueryResultsResource fqpResultResource = null;
        ResourceKey key = null;
        try {
            key = resourceHome.createResource();
            fqpResultResource = (FederatedQueryResultsResource) resourceHome.find(key);
        } catch (Exception e) {
            String message = "Problem creating and accessing resource:" + e.getMessage();
            LOG.error(message, e);
            InternalErrorFault fault = new InternalErrorFault();
            fault.setFaultString(message);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            throw (InternalErrorFault) helper.getFault();
        }

        // configure security on the resource so only the creator of the
        // resource (whoever is executing this method) can operate on it
        try {
            // may be null if no current caller
            fqpResultResource.setSecurityDescriptor(SecurityUtils.createCreatorOnlyResourceSecurityDescriptor());
        } catch (Exception e) {
            String message = "Problem configuring caller-only security on resource: " + e.getMessage();
            LOG.error(message, e);
            InternalErrorFault fault = new InternalErrorFault();
            fault.setFaultString(message);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            throw (InternalErrorFault) helper.getFault();
        }

        LOG.info("Resource created for, and owned by: " + SecurityManager.getManager().getCaller());

        // set to terminate after lease expires
        Calendar termTime = Calendar.getInstance();
        termTime.add(Calendar.MINUTE, leaseDurration);
        fqpResultResource.setTerminationTime(termTime);

        // set the query
        fqpResultResource.setQuery(query);
        
        // set the credential (if any)
        if (delegatedCredential != null) {
            GlobusCredential clientCredential = validateAndRetrieveDelegatedCredential(delegatedCredential);
            fqpResultResource.setDelegatedCredential(clientCredential);
        }
        
        // set the query execution parameters
        fqpResultResource.setQueryExecutionParameters(executionParameters);
        
        // set the work manager for tasks the resource needs to do
        fqpResultResource.setWorkExecutor(workExecutor);
        
        // start the resource working on the query
        fqpResultResource.beginQueryProcessing();
        
        // return handle to resource
        FederatedQueryResultsReference resultsReference = createEPR(key);
        return resultsReference;
    }
    
    
    private synchronized GlobusCredential validateAndRetrieveDelegatedCredential(DelegatedCredentialReference reference) throws InternalErrorFault {
        // get the caller's ID
        String callerID = null;
        try {
            callerID = SecurityUtils.getCallerIdentity();
        } catch (Exception ex) {
            LOG.error("Error obtaining caller identity: " + ex.getMessage(), ex);
            FaultHelper helper = new FaultHelper(new InternalErrorFault());
            helper.addDescription("Error obtaining caller identity");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }
        if (callerID == null) {
            // no null IDs with delegated credentials!
            FaultHelper helper = new FaultHelper(new InternalErrorFault());
            helper.setDescription("Caller identity found to be null while using a delegated credential!");
            throw (InternalErrorFault) helper.getFault();
        }
        GlobusCredential clientCredential = getDelegatedCredential(reference);
        // validate the caller's ID is the same as the credential they've delegated
        boolean valid = clientCredential.getIdentity().equals(callerID);
        if (!valid) {
            FaultHelper helper = new FaultHelper(new InternalErrorFault());
            helper.addDescription("Caller's identity and delegated credential identity did not match");
            throw (InternalErrorFault) helper.getFault();
        }
        return clientCredential;
    }
    
    
    private GlobusCredential getDelegatedCredential(DelegatedCredentialReference reference) throws InternalErrorFault {
        GlobusCredential userCredential = null;
        LOG.info("Retrieving delegated credential");
        try {
            DelegatedCredentialUserClient credentialClient = 
                new DelegatedCredentialUserClient(reference);
            userCredential = credentialClient.getDelegatedCredential();
        } catch (Exception ex) {
            // this too...
            FaultHelper helper = new FaultHelper(new InternalErrorFault());
            helper.addDescription("Error obtaining delegated credential from CDS");
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }
        return userCredential;
    }
    

    private FederatedQueryResultsReference createEPR(ResourceKey key) throws InternalErrorFault {
        MessageContext ctx = MessageContext.getCurrentContext();
        String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
        transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
        transportURL += FQPConstants.RESULTS_SERVICE_NAME;
        try {
            EndpointReferenceType epr = AddressingUtils.createEndpointReference(transportURL, key);
            return new FederatedQueryResultsReference(epr);
        } catch (Exception e) {
            LOG.error("Problem returning reference to result:" + e.getMessage(), e);
            InternalErrorFault fault = new InternalErrorFault();
            fault.setFaultString("Problem returning reference to result:" + e.getMessage());
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(e);
            throw (InternalErrorFault) helper.getFault();
        }
    }
}
