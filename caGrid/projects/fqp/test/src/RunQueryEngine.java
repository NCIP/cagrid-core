import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.results.metadata.ResultsRange;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.processor.FQPProcessingStatusListener;
import gov.nih.nci.cagrid.fqp.processor.FederatedQueryEngine;
import gov.nih.nci.cagrid.fqp.processor.FederatedQueryEngine;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;


/**
 * @author oster
 * 
 */
public class RunQueryEngine {

	public static void main(String[] args) {
		try {
			FederatedQueryEngine fqp = new FederatedQueryEngine(null, null);
            fqp.addStatusListener(new Listener());
			DCQLQuery dcql = (DCQLQuery) Utils.deserializeDocument(args[0], DCQLQuery.class);
			CQLQueryResults results = fqp.executeAndAggregateResults(dcql);
			CQLQueryResultsIterator iterator = new CQLQueryResultsIterator(results, true);
			int resultCount = 0;
			while (iterator.hasNext()) {
				System.out.println("=====RESULT [" + resultCount++ + "] =====");
				System.out.println(iterator.next());
				System.out.println("=====END RESULT=====\n\n");
			}
            System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    public static class Listener implements FQPProcessingStatusListener {
        
        public void processingStatusChanged(ProcessingStatus status, String message) {
            System.out.println("Processing status changed to " + status.getValue());
            System.out.println("\tMessage: " + message);
        }
        

        public void targetServiceConnectionRefused(String serviceURL) {
            System.out.println("Connection refused by " + serviceURL);            
        }

        
        public void targetServiceOk(String serviceURL) {
            System.out.println("Service OK: " + serviceURL);
        }
        
        
        public void targetServiceReturnedResults(String serviceURL, ResultsRange range) {
            System.out.println("Service " + serviceURL + " returned range: " + range.getStartElementIndex() + " to " + range.getEndElementIndex());
        }
        

        public void targetServiceReturnedInvalidResult(String serviceURL, FederatedQueryProcessingException ex) {
            System.err.println("Invalid Result: " + serviceURL);
            ex.printStackTrace();
        }

        
        public void targetServiceThrowsException(String serviceURL, Exception ex) {
            System.err.println("Service Exception: " + serviceURL);
            ex.printStackTrace();
        }
    }
}
