package gov.nih.nci.cagrid.fqp.processor;

import org.cagrid.fqp.results.metadata.ProcessingStatus;
import org.cagrid.fqp.results.metadata.ResultsRange;

import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;

/**
 * Listens for changes in FQP's (D)CQL broadcasting status
 * 
 * @author ervin
 */
public interface FQPProcessingStatusListener {
    
    
    /**
     * Indicates a change in the overall Federated Query Processing status
     * 
     * @param status
     *      The new status of query processing
     * @param message
     *      <b>OPTIONAL</b> Detail status message.  May be null.
     */
    public void processingStatusChanged(ProcessingStatus status, String message);
    

    /**
     * Indicates that the target service successfully processed the query
     * @param serviceURL
     */
    public void targetServiceOk(String serviceURL);
    
    
    /**
     * Indicates that the target service returned results, and when aggregated,
     * they're in the given range of indices.
     * @param serviceURL
     * @param range
     */
    public void targetServiceReturnedResults(String serviceURL, ResultsRange range);
    
    
    /**
     * Indicates that the target service did not respond to connections
     * @param serviceURL
     */
    public void targetServiceConnectionRefused(String serviceURL);
    
    
    /**
     * Indicates that the target service threw some exception while processing
     * @param serviceURL
     * @param ex
     */
    public void targetServiceThrowsException(String serviceURL, Exception ex);
    
    
    /**
     * Indicates that the target service returned some invalid result
     * @param serviceURL
     * @param ex
     */
    public void targetServiceReturnedInvalidResult(String serviceURL, FederatedQueryProcessingException ex);
}
