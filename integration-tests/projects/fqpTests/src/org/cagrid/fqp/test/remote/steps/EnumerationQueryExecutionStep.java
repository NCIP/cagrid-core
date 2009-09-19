package org.cagrid.fqp.test.remote.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.dcqlresult.DCQLQueryResultsCollection;
import gov.nih.nci.cagrid.dcqlresult.DCQLResult;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;
import gov.nih.nci.cagrid.fqp.results.common.FederatedQueryResultsConstants;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.results.metadata.FederatedQueryExecutionStatus;
import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.test.common.QueryResultsVerifier;
import org.cagrid.fqp.test.common.UrlReplacer;
import org.cagrid.fqp.test.common.steps.BaseQueryExecutionStep;
import org.cagrid.notification.SubscriptionListener;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;

/**
 * EnumerationQueryExecutionStep
 * Executes a federated query using enumeration for results
 *  
 * @author ervin
 */
public class EnumerationQueryExecutionStep extends BaseQueryExecutionStep {
    
    private static Log LOG = LogFactory.getLog(EnumerationQueryExecutionStep.class);
    
    public static final String[] QUERY_URL_PLACEHOLDERS = {
        "DATA_SERVICE_1", "DATA_SERVICE_2"
    };
    
    // TODO: make this configurable, or implement some kind of 
    // wait which continues once processing success == true
    public static final int WAIT_TIME = 30; // secconds
    public static final int MAX_NO_SUCH_ELEMENT = 5;
    
    private FederatedQueryProcessorClient fqpClient;
    private String[] dataServiceUrls;
    
    public EnumerationQueryExecutionStep(String queryFilename, String goldFilename,
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
        LOG.debug("Waiting for success notification...");
        Thread.sleep(WAIT_TIME * 1000);
        */
        
        // wait for results
        long start = System.currentTimeMillis();
        boolean complete = resultsClient.isProcessingComplete();
        while (!complete && ((System.currentTimeMillis() - start) < (WAIT_TIME * 1000))) {
            try {
                Thread.sleep(500);
                System.out.print('.');
                complete = resultsClient.isProcessingComplete();
            } catch (Exception ex) {
                // ?
            }
        }
        
        assertTrue("Federated query processing did not complete in the allowed time of " + WAIT_TIME + " sec", complete);
                
        enumerateAndVerify(resultsClient);
        
        // release the results resource
        resultsClient.destroy();
        
        // check for success...
        /*
        assertTrue("Federated Query Processing status is unknown", info.success != null);
        assertTrue("Federated Query Processing was not successful", info.success.booleanValue());
        */
    }
    
    
    private SubscriptionListener createSubscriptionListener(
        final FederatedQueryResultsClient resultsClient, final InfoHolder info) {
        SubscriptionListener listener = new SubscriptionListener() {
            public void subscriptionValueChanged(ResourcePropertyValueChangeNotificationType notification) {
                try {
                    String newMetadataDocument = AnyHelper.toSingleString(notification.getNewValue().get_any());
                    FederatedQueryExecutionStatus status = (FederatedQueryExecutionStatus) Utils.deserializeObject(
                        new StringReader(newMetadataDocument), FederatedQueryExecutionStatus.class);
                    StringWriter writer = new StringWriter();
                    Utils.serializeObject(status, FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS, writer);
                    LOG.debug("GOT NOTIFICATION:");
                    LOG.debug(writer.getBuffer().toString());
                    if ((info.success == null || !info.success.booleanValue()) && ProcessingStatus.Complete.equals(status.getCurrentStatus())) {
                        enumerateAndVerify(resultsClient);
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
    
    
    private void enumerateAndVerify(FederatedQueryResultsClient resultsClient) {
        // if successfull, grab the response
        EnumerationResponseContainer enumerationResponse = null;
        try {
            enumerationResponse = resultsClient.enumerate();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error starting enumeration: " + ex.getMessage());
        } 
        
        LOG.debug("Creating client side enumeration iterator");
        ClientEnumIterator iterator = null;
        try {
            iterator = EnumerationResponseHelper.createClientIterator(enumerationResponse);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            fail("Error creating client side enumeration: " + ex.getMessage());
        }
        
        List<SOAPElement> resultElements = new ArrayList<SOAPElement>();
        int noSuchElementCount = 0;
        while (iterator.hasNext()) {
            try {
                SOAPElement element = (SOAPElement) iterator.next();
                resultElements.add(element);
            } catch (NoSuchElementException ex) {
                noSuchElementCount++;
                if (noSuchElementCount >= MAX_NO_SUCH_ELEMENT) {
                    ex.printStackTrace();
                    fail("Too many (" + MAX_NO_SUCH_ELEMENT + ") NoSuchElementExceptions thrown by client iterator!");
                }
            }
        }
        
        // validate
        verifyResults(resultElements);
        
        // release the enumeration resource
        iterator.release();
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
    
    
    private void verifyResults(List<SOAPElement> results) {
        LOG.debug("Verifying test results");
        LOG.debug("Loading gold results from " + getGoldFilenname());
        DCQLQueryResultsCollection goldResults = loadGoldDcqlResults();
        
        // convert the list of elements into DCQL results
        DCQLQueryResultsCollection testResults = new DCQLQueryResultsCollection();
        DCQLResult testResult = new DCQLResult();
        testResult.setTargetServiceURL("http://fake.service");
        CQLQueryResults cqlResults = new CQLQueryResults();
        // what's the target classname supposed to be?
        String targetName = deserializeQuery().getTargetObject().getName();
        LOG.debug("Setting test results target classname to " + targetName);
        cqlResults.setTargetClassname(targetName);
        CQLObjectResult[] objectResults = new CQLObjectResult[results.size()];
        for (int i = 0; i < results.size(); i++) {
            CQLObjectResult obj = new CQLObjectResult();
            obj.set_any(new MessageElement[] {new MessageElement(results.get(i))});
            objectResults[i] = obj;
        }
        cqlResults.setObjectResult(objectResults);
        testResult.setCQLQueryResultCollection(cqlResults);
        testResults.setDCQLResult(new DCQLResult[] {testResult});
        
        QueryResultsVerifier.verifyDcqlResults(testResults, goldResults);
    }
    
    
    private static class InfoHolder {
        public Boolean success;
        public Exception exception;
    }
}
