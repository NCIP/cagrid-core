/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package gov.nih.nci.cagrid.fqp.resultsretrieval.service.globus.resource;

import gov.nih.nci.cagrid.fqp.processor.FederatedQueryEngine;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;

import java.util.concurrent.ExecutorService;

import org.cagrid.data.dcql.DCQLQuery;
import org.cagrid.data.dcql.results.DCQLQueryResultsCollection;
import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.globus.gsi.GlobusCredential;

/**
 * Encapsulates execution of a DCQL 2 query in a thread so that
 * status of the resource may be maintained and other operations
 * do not block while waiting on the query to complete.
 * 
 * @author ervin
 */
class QueryExecutionTask implements Runnable {
    private DCQLQuery query = null;
    private GlobusCredential credential = null;
    private QueryExecutionParameters executionParameters = null;
    private ExecutorService workExecutor = null;
    private AsynchronousFQPProcessingStatusListener statusListener = null;
    
    public QueryExecutionTask(
        DCQLQuery query, GlobusCredential credential, 
        QueryExecutionParameters executionParameters, ExecutorService workManager,
        AsynchronousFQPProcessingStatusListener statusListener) {
        this.query = query;
        this.credential = credential;
        this.executionParameters = executionParameters;
        this.workExecutor = workManager;
        this.statusListener = statusListener;
    }
    
    
    public void run() {
        FederatedQueryEngine engine =
            new FederatedQueryEngine(credential, executionParameters, workExecutor);
        engine.addStatusListener(statusListener);
        try {
            DCQLQueryResultsCollection results = engine.execute(query);
            statusListener.queryResultsGenerated(results);
        } catch (FederatedQueryProcessingException ex) {
            statusListener.queryProcessingException(ex);
        }
    }
}
