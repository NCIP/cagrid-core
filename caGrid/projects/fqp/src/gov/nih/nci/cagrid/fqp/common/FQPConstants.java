package gov.nih.nci.cagrid.fqp.common;

import org.cagrid.fqp.execution.QueryExecutionParameters;
import org.cagrid.fqp.execution.TargetDataServiceQueryBehavior;

public interface FQPConstants {

    /**
     * The name of the results service context
     */
    public static final String RESULTS_SERVICE_NAME = "FederatedQueryResults";
    
    /**
     * The default target data service query behavior.  Derived from the XSD
     * definition, since Axis doesn't set the defaults with the no-arg constructor
     */
    public static final TargetDataServiceQueryBehavior DEFAULT_TARGET_QUERY_BEHAVIOR = 
        new TargetDataServiceQueryBehavior(Boolean.TRUE, Integer.valueOf(0), Integer.valueOf(5));
    
    /**
     * The default Query Execution Parameters.  Derived from the XSD
     * definition, since Axis doesn't set the defaults with the no-arg constructor
     */
    public static final QueryExecutionParameters DEFAULT_QUERY_EXECUTION_PARAMETERS = 
        new QueryExecutionParameters(Boolean.FALSE, DEFAULT_TARGET_QUERY_BEHAVIOR);
}
