package gov.nih.nci.cagrid.data;

public interface QueryProcessorConstants {
    
    // query processor constants
    public static final String QUERY_PROCESSOR_CLASS_PROPERTY = "queryProcessorClass";
    public static final String CQL2_QUERY_PROCESSOR_CLASS_PROPERTY = "cql2QueryProcessorClass";
    public static final String QUERY_PROCESSOR_STUB_NAME = "StubCQLQueryProcessor";
    public static final String QUERY_PROCESSOR_STUB_DEPRICATED_MESSAGE = 
        "/*\n" +
        " * This class is no longer used by the Data Service,\n" +
        " * and should be removed if the implementation it\n" +
        " * contains is no longer needed.\n" +
        " */\n\n";
    
    // query processor config constants
    public static final String QUERY_PROCESSOR_CONFIG_PREFIX = "cqlQueryProcessorConfig_";
    public static final String CQL2_QUERY_PROCESSOR_CONFIG_PREFIX = "cql2QueryProcessorConfig_";
}
