package org.cagrid.fqp.test.common.steps.dcql2;

import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.utilities.QueryConversionException;
import org.cagrid.cql2.results.CQLQueryResults;
import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.fqp.test.common.FederatedQueryProcessorHelper;
import org.cagrid.fqp.test.common.QueryResultsVerifier;

/** 
 *  Dcql2AggregationStep
 *  Performs a simple FQP aggregation using DCQL 2 and compares
 *  the result to a known-good result
 * 
 * @author David Ervin
 * 
 * @created Jul 10, 2008 12:17:40 PM
 * @version $Id: AggregationStep.java,v 1.7 2008-08-27 16:16:28 dervin Exp $ 
 */
public class Dcql2AggregationStep extends BaseDcql2QueryExecutionStep {
    
    private static Log LOG = LogFactory.getLog(Dcql2AggregationStep.class);
    
    private FederatedQueryProcessorHelper queryProcessor;
    private String[] testServiceUrls;
    private boolean expectQueryConversionException = false;
    
    public Dcql2AggregationStep(String queryFilename, String goldFilename, 
        FederatedQueryProcessorHelper queryProcessorHelper, String[] testServiceUrls, 
        boolean expcectQueryConversionException) {
        super(queryFilename, goldFilename);
        this.queryProcessor = queryProcessorHelper;
        this.testServiceUrls = testServiceUrls;
        this.expectQueryConversionException = expcectQueryConversionException;
    }
    

    public void runStep() throws Throwable {
        LOG.debug("Testing with query " + getQueryFilename());
        DCQLQuery query = deserializeQuery();
        query.setTargetServiceURL(testServiceUrls);
        CQLQueryResults testResults = performAggregation(query);
        if (testResults != null) {
            LOG.debug("Verifying against " + getGoldFilenname());
            CQLQueryResults goldResults = loadGoldCqlResults();
            QueryResultsVerifier.verifyCql2Results(testResults, goldResults);
        }
    }
    
    
    private CQLQueryResults performAggregation(DCQLQuery query) throws RemoteException, 
        FederatedQueryProcessingException, FederatedQueryProcessingFault {
        CQLQueryResults results = null;
        try {
            results = queryProcessor.executeAndAggregateResults(query);
        } catch (FederatedQueryProcessingException ex) {
            if (expectQueryConversionException && causedByQueryConversionException(ex)) {
                LOG.info("Caught query conversion exception, as was expected");
            } else {
                throw ex;
            }
        }
        return results;
    }
    
    
    private boolean causedByQueryConversionException(Exception ex) {
        Set<Exception> seenExceptions = new HashSet<Exception>();
        Exception cause = ex;
        while (cause != null && !seenExceptions.contains(cause)) {
            seenExceptions.add(cause);
            if (cause instanceof QueryConversionException) {
                return true;
            }
            cause = (Exception) cause.getCause();
        }
        return false;
    }
}
