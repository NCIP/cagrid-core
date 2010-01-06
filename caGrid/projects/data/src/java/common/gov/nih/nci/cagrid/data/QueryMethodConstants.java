package gov.nih.nci.cagrid.data;

import javax.xml.namespace.QName;

public interface QueryMethodConstants {
    
    // base method naming
    public static final String QUERY_METHOD_NAME = "query";
    public static final String QUERY_METHOD_RETURN_TYPE = CqlSchemaConstants.CQL_RESULT_SET_TYPE;
    public static final String QUERY_METHOD_PARAMETER_TYPE = CqlSchemaConstants.CQL_QUERY_TYPE;
    public static final String QUERY_METHOD_PARAMETER_NAME = "cqlQuery";
    public static final String QUERY_METHOD_PARAMETER_DESCRIPTION = "The CQL query to be executed against the data source.";
    public static final String QUERY_IMPLEMENTATION_ADDED = "queryImplAdded";
    public static final String QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method.";
    public static final String QUERY_METHOD_OUTPUT_DESCRIPTION = "The result of executing the CQL query against the data source.";
    
    // CQL 2 execute query method
    public static final String CQL2_QUERY_METHOD_NAME = "executeQuery";
    public static final String CQL2_QUERY_METHOD_PARAMETER_TYPE = CqlSchemaConstants.CQL2_QUERY_QNAME.getLocalPart();
    public static final String CQL2_QUERY_METHOD_PARAMETER_NAME = "query";
    public static final String CQL2_QUERY_METHOD_RETURN_TYPE = CqlSchemaConstants.CQL2_RESULTS_QNAME.getLocalPart();
    public static final String CQL2_QUERY_METHOD_PARAMETER_DESCRIPTION = "The CQL 2 query to be executed against the data source.";
    public static final String CQL2_QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method.";
    public static final String CQL2_QUERY_METHOD_OUTPUT_DESCRIPTION = "The result of executing the CQL 2 query against the data source.";    
    
    // exceptions
    public static final String DATA_SERVICE_EXCEPTIONS_NAMESPACE = "http://gov.nih.nci.cagrid.data/DataServiceExceptions";
    public static final String DATA_SERVICE_EXCEPTIONS_SCHEMA = "DataServiceExceptions.xsd";
    public static final String QUERY_PROCESSING_EXCEPTION_NAME = "QueryProcessingException";
    public static final String QUERY_PROCESSING_EXCEPTION_DESCRIPTION = "Thrown when an error occurs in processing a CQL query";
    public static final String MALFORMED_QUERY_EXCEPTION_NAME = "MalformedQueryException";
    public static final String MALFORMED_QUERY_EXCEPTION_DESCRIPTION = "Thrown when a query is found to be improperly formed";
    public static final QName QUERY_PROCESSING_EXCEPTION_QNAME = new QName(DATA_SERVICE_EXCEPTIONS_NAMESPACE, QUERY_PROCESSING_EXCEPTION_NAME);
    public static final QName MALFORMED_QUERY_EXCEPTION_QNAME = new QName(DATA_SERVICE_EXCEPTIONS_NAMESPACE, MALFORMED_QUERY_EXCEPTION_NAME);
}
