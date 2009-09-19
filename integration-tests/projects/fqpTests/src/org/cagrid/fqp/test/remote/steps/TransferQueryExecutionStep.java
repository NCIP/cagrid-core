package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;
import gov.nih.nci.cagrid.fqp.results.common.FederatedQueryResultsConstants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.results.metadata.FederatedQueryExecutionStatus;
import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.test.common.QueryResultsVerifier;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;
import org.cagrid.notification.SubscriptionListener;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataTransferDescriptor;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;

/**
 * TransferQueryExecutionStep
 * Executes a federated query using transfer for results
 *  
 * @author ervin
 */
public class TransferQueryExecutionStep extends BaseQueryExecutionStep {
    
    private static Log LOG = LogFactory.getLog(TransferQueryExecutionStep.class);
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    // TODO: make this configurable, or implement some kind of 
    // wait which continues once processing success == true
    public static final int WAIT_TIME = 30; // secconds
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] dataServiceUrls;
    
    public TransferQueryExecutionStep(String queryFilename, String goldFilename,
        FederatedQueryProcessorClient fqpClient, String[] serviceUrls) {
        super(queryFilename, goldFilename);
        this.fqpClient = fqpClient;
        this.dataServiceUrls = serviceUrls;
    }
    

    public void runStep() throws Throwable {
        DCQLQuery query = getCompletedQuery();
        
        LOG.debug("Begining non-blocking query");
        final FederatedQueryResultsClient resultsClient = fqpClient.query(query, null, null);
        /*
         * TODO: Turn this back on when I can get my data services to sleep long enough to actually get notifications
        final InfoHolder info = new InfoHolder();
        
        Thread worker = new Thread() {
            public void run() {
                SubscriptionListener listener = createSubscriptionListener(resultsClient, info);
                SubscriptionHelper subscriptionHelper = new SubscriptionHelper();
                try {
                    subscriptionHelper.subscribe(
                        resultsClient, FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS, listener);
                } catch (SubscriptionCreationException ex) {
                    ex.printStackTrace();
                    fail("Error subscribing to notifications: " + ex.getMessage());
                }
            }
        };
        worker.start();
        
        // wait for notification that the data is ready
        LOG.debug("Waiting " + WAIT_TIME + " secconds for success notification...");
        Thread.sleep(WAIT_TIME * 1000);
        */
        
        // wait for results
        long start = System.currentTimeMillis();
        boolean complete = resultsClient.isProcessingComplete();
        while (!complete && ((System.currentTimeMillis() - start) < (WAIT_TIME * 1000))) {
            try {
                Thread.sleep(500);
                System.out.print(".");
                complete = resultsClient.isProcessingComplete();
            } catch (Exception ex) {
                // ?
            }
        }
        
        assertTrue("Federated query processing did not complete in the allowed time of " + WAIT_TIME + " sec", complete);
        
        // verify the results
        transferAndVerify(resultsClient);
        
        // release the results resource
		LOG.debug("Trying to destroy transfer results resource");
        resultsClient.destroy();
        LOG.debug("Transfer results resource destroyed");
        
        // check for success...
        /*
        assertTrue("Federated Query Processing status is unknown", info.success != null);
        assertTrue("Federated Query Processing was not successful", info.success.booleanValue());
        */
    }
    
    
    private SubscriptionListener createSubscriptionListener(
        final FederatedQueryResultsClient resultsClient, final InfoHolder info) {
        SubscriptionListener listener = new SubscriptionListener() {
            private boolean completeDetected = false;
            
            public void subscriptionValueChanged(ResourcePropertyValueChangeNotificationType notification) {
                try {
                    String newMetadataDocument = AnyHelper.toSingleString(notification.getNewValue().get_any());
                    FederatedQueryExecutionStatus status = (FederatedQueryExecutionStatus) Utils.deserializeObject(
                        new StringReader(newMetadataDocument), FederatedQueryExecutionStatus.class);
                    StringWriter writer = new StringWriter();
                    Utils.serializeObject(status, FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS, writer);
                    if (LOG.isDebugEnabled())  {
                        LOG.debug("GOT NOTIFICATION:");
                        LOG.debug(writer.getBuffer().toString());
                    }
                    if (!completeDetected && ProcessingStatus.Complete.equals(status.getCurrentStatus())) {
                        completeDetected = true;
                        LOG.debug("EXECUTING TRANSFER AND VERIFY PROCESS");
                        transferAndVerify(resultsClient);
                        LOG.debug("SETTING SUCCESS STATUS TO TRUE");
                        info.success = Boolean.TRUE;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    info.success = Boolean.FALSE;
                    info.exception = ex;
                }
            }
        };
        return listener;
    }
    
    
    private void transferAndVerify(FederatedQueryResultsClient resultsClient) {
        LOG.debug("CREATING TRANSFER CONTEXT");
        TransferServiceContextReference transferRef = null;
        try {
            transferRef = resultsClient.transfer();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            fail("Error creating transfer context: " + ex.getMessage());
        }
        
        LOG.debug("CREATING TRANSFER CLIENT");
        TransferServiceContextClient transferClient = null;
        try {
            transferClient = new TransferServiceContextClient(transferRef.getEndpointReference());
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error creating transfer client: " + ex.getMessage());
        }
        
        /*
         * FIXME: This throws a no deserializer found if the metadata is populated
         */
        LOG.debug("GETTING DATA TRANSFER DESCRIPTOR");
        DataTransferDescriptor transferDescriptor = null;
        try {
            transferDescriptor = transferClient.getDataTransferDescriptor();
        } catch (RemoteException ex) {
            ex.printStackTrace();
            fail("Error getting data transfer descriptor: " + ex.getMessage());
        }
        /*
         * TODO: Re-enable the metadata check once I figure out what's wrong with the deserializer configuration
        Object metadata = transferDescriptor.getDataDescriptor().getMetadata();
        assertNotNull("Metadata from transfer data descriptor was null", metadata);
        try {
            metadata = resultsClient.getResourceProperty(FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            fail("Error getting federated query processing status metadata: " + ex.getMessage());
        }
        System.out.println("VERIFYING METADATA");
        assertTrue("Metadata was of unknown type (" + metadata.getClass().getName() + ")", 
            metadata instanceof FederatedQueryExecutionStatus);
        FederatedQueryExecutionStatus executionStatus = (FederatedQueryExecutionStatus) metadata;
        assertEquals("Unexpected processing status", ProcessingStatus.Complete, executionStatus.getCurrentStatus());
        */
        
        LOG.debug("LOADING DATA TRANSFER STREAM");
        LOG.debug("Transfering data from " + transferDescriptor.getUrl());
        InputStream dataStream = null;
        try {
            dataStream = TransferClientHelper.getData(transferDescriptor);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error getting data input stream from transfer context: " + ex.getMessage());
        }
        
        LOG.debug("STREAMING DATA");
        StringWriter textWriter = new StringWriter();
        InputStreamReader streamReader = new InputStreamReader(dataStream);
        char[] buffer = new char[1024];
        int charsRead = -1;
        try {
            while ((charsRead = streamReader.read(buffer)) != -1) {
                textWriter.write(buffer, 0, charsRead);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error reading data from transfer service: " + ex.getMessage());
        }
        
        LOG.debug("DATA LOADED, CLOSING STREAMS");
        try {
            streamReader.close();
            dataStream.close();
        } catch (IOException ex) {
            LOG.warn("Error closing data streams: " + ex.getMessage(), ex);
        }
        
        String xml = textWriter.getBuffer().toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got data from transfer context:");
            LOG.debug(xml);
        }
        LOG.debug("Deserializing text from transfer service to CQL Query Results");
        
        StringReader xmlReader = new StringReader(xml);
        DCQLQueryResultsCollection results = null;
        try {
            results = (DCQLQueryResultsCollection) Utils.deserializeObject(xmlReader, DCQLQueryResultsCollection.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing CQL query results: " + ex.getMessage());
        }
        
        // validate
        LOG.debug("VALIDATING RESULTS");
        DCQLQueryResultsCollection dcqlGoldResults = loadGoldDcqlResults();
        QueryResultsVerifier.verifyDcqlResults(results, dcqlGoldResults);
        
        // release the transfer resource
        try {
            LOG.debug("RELEASING TRANSFER CONTEXT");
            transferClient.destroy();
        } catch (RemoteException ex) {
            LOG.error("Error destroying transfer context: " + ex.getMessage(), ex);
        }
        LOG.debug("TRANSFER AND VALIDATE COMPLETE");
    }
    
    
    private DCQLQuery getCompletedQuery() {
        assertEquals("Unexpected number of service urls", 
            QUERY_URL_PLACEHOLDERS.length, dataServiceUrls.length);
        LOG.debug("Loading original DCQL query from " + getQueryFilename());
        DCQLQuery original = deserializeQuery();
        Map<String, String> urlReplacements = new HashMap<String, String>();
        for (int i = 0; i < QUERY_URL_PLACEHOLDERS.length; i++) {
            urlReplacements.put(QUERY_URL_PLACEHOLDERS[i], dataServiceUrls[i]);
        }
        LOG.debug("Filling placeholder URLs with real ones");
        DCQLQuery replaced = null;
        try {
            replaced = UrlReplacer.replaceUrls(original, urlReplacements);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unable to replace URL placeholders in DCQL query: " + ex.getMessage());
        }
        return replaced;
    }
    
    
    private static class InfoHolder {
        public Boolean success;
        public Exception exception;
    }
}
