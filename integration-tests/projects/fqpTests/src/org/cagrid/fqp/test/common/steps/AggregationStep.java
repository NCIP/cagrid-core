package org.cagrid.fqp.test.common.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.CqlSchemaConstants;
import gov.nih.nci.cagrid.dcql.DCQLQuery;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.fqp.stubs.types.FederatedQueryProcessingFault;

import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.fqp.test.common.FQPTestingConstants;
import org.cagrid.fqp.test.common.FederatedQueryProcessorHelper;
import org.cagrid.fqp.test.common.QueryResultsVerifier;

/** 
 *  AggregationStep
 *  Performs a simple FQP aggregation and compare the result to Gold
 * 
 * @author David Ervin
 * 
 * @created Jul 10, 2008 12:17:40 PM
 * @version $Id: AggregationStep.java,v 1.7 2008-08-27 16:16:28 dervin Exp $ 
 */
public class AggregationStep extends BaseQueryExecutionStep {
    
    private static Log LOG = LogFactory.getLog(AggregationStep.class);
    
    private FederatedQueryProcessorHelper queryProcessor;
    private String[] testServiceUrls;
    
    public AggregationStep(String queryFilename, String goldFilename, 
        FederatedQueryProcessorHelper queryProcessorHelper, String[] testServiceUrls) {
        super(queryFilename, goldFilename);
        this.queryProcessor = queryProcessorHelper;
        this.testServiceUrls = testServiceUrls;
    }
    

    public void runStep() throws Throwable {
        if (LOG.isTraceEnabled()) {
            URL[] classpath = ((URLClassLoader) getClass().getClassLoader()).getURLs();
            LOG.trace("Your classpath:\n");
            for (URL u : classpath) {
                LOG.trace(u.getPath());
            }
        }
        LOG.debug("Testing with query " + getQueryFilename());
        DCQLQuery query = deserializeQuery();
        query.setTargetServiceURL(testServiceUrls);
        CQLQueryResults testResults = performAggregation(query);
        LOG.debug("Verifying against " + getGoldFilenname());
        CQLQueryResults goldResults = loadGoldCqlResults();
        StringWriter writer = new StringWriter();
        writer.write("Test results:\n");
        Utils.serializeObject(testResults, CqlSchemaConstants.CQL_RESULT_SET_QNAME, writer, 
            QueryResultsVerifier.class.getResourceAsStream(FQPTestingConstants.CLIENT_WSDD));
        writer.write("\nGoldResults:\n");
        Utils.serializeObject(goldResults, CqlSchemaConstants.CQL_RESULT_SET_QNAME, writer, 
            QueryResultsVerifier.class.getResourceAsStream(FQPTestingConstants.CLIENT_WSDD));
        System.out.println(writer.getBuffer().toString());
        QueryResultsVerifier.verifyCqlResults(testResults, goldResults);
    }
    
    
    private CQLQueryResults performAggregation(DCQLQuery query) throws RemoteException, 
        FederatedQueryProcessingException, FederatedQueryProcessingFault {
        return queryProcessor.executeAndAggregateResults(query);
    }
}
