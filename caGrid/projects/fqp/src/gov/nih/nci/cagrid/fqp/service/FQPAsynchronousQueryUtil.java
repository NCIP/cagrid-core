package gov.nih.nci.cagrid.fqp.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.fqp.common.FQPConstants;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.results.stubs.types.InternalErrorFault;
import gov.nih.nci.cagrid.fqp.resultsretrieval.service.globus.resource.FederatedQueryResultsRetrievalResource;
import gov.nih.nci.cagrid.fqp.resultsretrieval.service.globus.resource.FederatedQueryResultsRetrievalResourceHome;
import gov.nih.nci.cagrid.fqp.resultsretrieval.stubs.types.FederatedQueryResultsRetrievalReference;
import gov.nih.nci.cagrid.introduce.servicetools.security.SecurityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;

import org.apache.axis.MessageContext;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.gaards.cds.client.DelegatedCredentialUserClient;
import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;
import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.container.ServiceHost;
import org.globus.wsrf.security.SecurityManager;
import org.globus.wsrf.utils.AddressingUtils;

/**
 * FQPAsynchronousQueryUtil
 * Performs asynchronous execution of a DCQL 2 federated query in 
 * the context of a federated query results retrieval resource.
 * 
 * @author ervin
 */
public class FQPAsynchronousQueryUtil {
    
    private static final int DEFAULT_RESULT_LEASE_MINS = 30;
    
    private static Log LOG = LogFactory.getLog(FQPAsynchronousQueryUtil.class);

    private FederatedQueryResultsRetrievalResourceHome resourceHome;
    private int leaseDurration = DEFAULT_RESULT_LEASE_MINS;
    private ExecutorService workExecutor = null;
    
    public FQPAsynchronousQueryUtil(FederatedQueryResultsRetrievalResourceHome resourceHome, ExecutorService workExecutor) {
        this(resourceHome, workExecutor, DEFAULT_RESULT_LEASE_MINS);
    }
    
    
    public FQPAsynchronousQueryUtil(FederatedQueryResultsRetrievalResourceHome resourceHome, ExecutorService workExecutor, int leaseDurration) {
        this.resourceHome = resourceHome;
        this.workExecutor = workExecutor;
        this.leaseDurration = leaseDurration;
    }


    public synchronized FederatedQueryResultsRetrievalReference executeAsynchronousQuery(
        DCQLQuery query, DelegatedCredentialReference delegatedCredential, QueryExecutionParameters executionParameters) 
        throws InternalErrorFault, FederatedQueryProcessingException {
        // use the resource home to create an FQP results retrieval resource
        FederatedQueryResultsRetrievalResource resultsResource = null;
        ResourceKey key = null;
        try {
            key = resourceHome.createResource();
            resultsResource = (FederatedQueryResultsRetrievalResource) resourceHome.find(key);
        } catch (Exception ex) {
            String message = "Problem creating and accessing resource:" + ex.getMessage();
            LOG.error(message, ex);
            InternalErrorFault fault = new InternalErrorFault();
            fault.setFaultString(message);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }

        // configure security on the resource so only the creator of the
        // resource (whoever is executing this method) can operate on it
        try {
            // may be null if no current caller
            resultsResource.setSecurityDescriptor(SecurityUtils.createCreatorOnlyResourceSecurityDescriptor());
        } catch (Exception ex) {
            String message = "Problem configuring caller-only security on resource: " + ex.getMessage();
            LOG.error(message, ex);
            InternalErrorFault fault = new InternalErrorFault();
            fault.setFaultString(message);
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }

        LOG.info("Resource created for, and owned by: " + SecurityManager.getManager().getCaller());

        // set to terminate after lease expires
        Calendar termTime = Calendar.getInstance();
        termTime.add(Calendar.MINUTE, leaseDurration);
        resultsResource.setTerminationTime(termTime);

        // set the query
        resultsResource.setQuery(query);
        
        // set the credential (if any)
        if (delegatedCredential != null) {
            GlobusCredential clientCredential = validateAndRetrieveDelegatedCredential(delegatedCredential);
            resultsResource.setDelegatedCredential(clientCredential);
        }
        
        // set the query execution parameters
        resultsResource.setQueryExecutionParameters(executionParameters);
        
        // set the work manager for tasks the resource needs to do
        resultsResource.setWorkExecutor(workExecutor);
        
        // start the resource working on the query
        resultsResource.beginQueryProcessing();
        
        // return handle to resource
        FederatedQueryResultsRetrievalReference resultsReference = createEPR(key);
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
            String message = "Error obtaining delegated credential from CDS";
            LOG.error(message, ex);
            FaultHelper helper = new FaultHelper(new InternalErrorFault());
            helper.addDescription(message);
            helper.addDescription(ex.getMessage());
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }
        return userCredential;
    }
    

    private FederatedQueryResultsRetrievalReference createEPR(ResourceKey key) throws InternalErrorFault {
        MessageContext ctx = MessageContext.getCurrentContext();
        String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
        // modify URL to fix a bug https://gforge.nci.nih.gov/tracker/?func=detail&aid=22308&group_id=25&atid=174
        try {
            AttributedURI uri = new AttributedURI(transportURL);
            URL baseURL = ServiceHost.getBaseURL();
            String correctHost = baseURL.getHost();
            uri.setHost(correctHost);
            transportURL = uri.toString();
        } catch (IOException ex) {
            LOG.error("Problem returning reference to result:" + ex.getMessage(), ex);
            InternalErrorFault fault = new InternalErrorFault();
            fault.setFaultString("Problem returning reference to result:" + ex.getMessage());
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }
        transportURL = transportURL.substring(0, transportURL.lastIndexOf('/') + 1);
        transportURL += FQPConstants.RESULTS_RETRIEVAL_SERVICE_NAME;
        try {
            EndpointReferenceType epr = AddressingUtils.createEndpointReference(transportURL, key);
            return new FederatedQueryResultsRetrievalReference(epr);
        } catch (Exception ex) {
            LOG.error("Problem returning reference to result:" + ex.getMessage(), ex);
            InternalErrorFault fault = new InternalErrorFault();
            fault.setFaultString("Problem returning reference to result:" + ex.getMessage());
            FaultHelper helper = new FaultHelper(fault);
            helper.addFaultCause(ex);
            throw (InternalErrorFault) helper.getFault();
        }
    }
}
