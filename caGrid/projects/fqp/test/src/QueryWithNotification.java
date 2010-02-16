import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.client.FederatedQueryProcessorClient;
import gov.nih.nci.cagrid.fqp.results.client.FederatedQueryResultsClient;
import gov.nih.nci.cagrid.fqp.results.common.FederatedQueryResultsConstants;

import java.io.StringReader;
import java.io.StringWriter;

import org.cagrid.fqp.results.metadata.FederatedQueryExecutionStatus;
import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.notification.SubscriptionHelper;
import org.cagrid.notification.SubscriptionListener;
import org.globus.wsrf.impl.notification.SubscriptionCreationException;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.properties.ResourcePropertyValueChangeNotificationType;


public class QueryWithNotification {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String globusEnv = System.getenv("GLOBUS_LOCATION");
        // MUST POINT TO $GLOBUS_LOCATION
        if (globusEnv == null) {
            System.err.println("GLOBUS_LOCATION environment variable must be set!");
            System.exit(-1);
        }
        // GLOBUS_LOCATION system property has to point to $GLOBUS_LOCATION
        // note system property != environment variable
        System.setProperty("GLOBUS_LOCATION", globusEnv != null ? globusEnv : "w:/Projects/dev-lib/ws-core-4.0.3");
        try {
            FederatedQueryProcessorClient fqpClient = new FederatedQueryProcessorClient("http://localhost:8080/wsrf/services/cagrid/FederatedQueryProcessor");
            DCQLQuery query = Utils.deserializeDocument("exampleDistributedJoin1.xml", DCQLQuery.class);
            System.out.println("Starting query");
            final FederatedQueryResultsClient resultsClient = fqpClient.query(query, null, null);
            
            // basic subscribe
            System.out.println("Subscribing to results client for " + FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS);
            resultsClient.subscribe(FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS);

            // advanced subscribe
            SubscriptionHelper subscriptionHelper = new SubscriptionHelper();
            SubscriptionListener listener = new SubscriptionListener() {
                public void subscriptionValueChanged(ResourcePropertyValueChangeNotificationType notification) {
                    System.out.println("RECIEVED NOTIFICATION...");
                    try {
                        String newMetadataDocument = AnyHelper.toSingleString(notification.getNewValue().get_any());
                        FederatedQueryExecutionStatus status = Utils.deserializeObject(
                            new StringReader(newMetadataDocument), FederatedQueryExecutionStatus.class);
                        StringWriter writer = new StringWriter();
                        Utils.serializeObject(status, FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS, writer);
                        System.out.println("XML:");
                        System.out.println(writer.getBuffer().toString());
                        if (ProcessingStatus.Complete.equals(status.getCurrentStatus())) {
                            System.out.println("QUERY COMPLETE, STATUS OK!!!");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.err.println("UH OH...");
                    }
                }
            };
            try {
                subscriptionHelper.subscribe(resultsClient, FederatedQueryResultsConstants.FEDERATEDQUERYEXECUTIONSTATUS, listener);
            } catch (SubscriptionCreationException ex) {
                System.out.println("UNABLE TO SUBSCRIBE: " + ex.getMessage());
                ex.printStackTrace();
            }
            
            System.out.print("Subscribed; waiting for processing to complete");
            while (!resultsClient.isProcessingComplete()) {
                Thread.sleep(500);
                System.out.print(".");
            }
            CQLQueryResults results = resultsClient.getAggregateResults();
            CQLQueryResultsIterator iterator = new CQLQueryResultsIterator(results, true);
            int resultCount = 0;
            while (iterator.hasNext()) {
                System.out.println("=====RESULT [" + resultCount++ + "] =====");
                System.out.println(iterator.next());
                System.out.println("=====END RESULT=====\n\n");
            }
            System.out.println("DONE");
            // gives the notification listener time to receive and process the events
            Thread.sleep(5000);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
