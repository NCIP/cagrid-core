package gov.nih.nci.cagrid.data;

import gov.nih.nci.cagrid.encoding.SDKDeserializerFactory;
import gov.nih.nci.cagrid.encoding.SDKSerializerFactory;

import javax.xml.namespace.QName;


/** 
 *  DataServiceConstants
 *  Assorted constants for data services
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 31, 2006 
 * @version $Id$ 
 */
public class DataServiceConstants {
	// some platforms have problems with backslashes in xsd locations
	public static final String FILE_SEPARATOR = "/";
	
	// metadata schema constants
	public static final String METADATA_SCHEMA_LOCATION = "xsd" + FILE_SEPARATOR + "cagrid" + FILE_SEPARATOR + "types";
	public static final String DATA_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + FILE_SEPARATOR + "data" + FILE_SEPARATOR + "data.xsd";
	public static final String COMMON_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + FILE_SEPARATOR + "common" + FILE_SEPARATOR + "common.xsd";
	public static final String CAGRID_METADATA_SCHEMA = METADATA_SCHEMA_LOCATION + FILE_SEPARATOR + "caGridMetadata.xsd";
	public static final String DATA_METADATA_URI = "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice";
	
	// sdk serializer constants
	public static final String SDK_SERIALIZER = SDKSerializerFactory.class.getName();
	public static final String SDK_DESERIALIZER = SDKDeserializerFactory.class.getName();

	// query schema constants
	public static final String CQL_QUERY_SCHEMA = "1_gov.nih.nci.cagrid.CQLQuery.xsd";
	public static final String CQL_RESULT_SET_SCHEMA = "1_gov.nih.nci.cagrid.CQLResultSet.xsd";
	public static final String CQL_QUERY_URI = "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery";
	public static final String CQL_RESULT_SET_URI = "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLResultSet";
	public static final String CQL_QUERY_TYPE = gov.nih.nci.cagrid.cqlquery.CQLQuery.class.getName();
	public static final String CQL_RESULT_SET_TYPE = gov.nih.nci.cagrid.cqlresultset.CQLQueryResults.class.getName();
	public static final QName CQL_QUERY_QNAME = new QName(CQL_QUERY_URI, "CQLQuery");
	public static final QName CQL_RESULT_SET_QNAME = new QName(CQL_RESULT_SET_URI, "CQLQueryResults");
	public static final QName CQL_RESULT_COLLECTION_QNAME = new QName(CQL_RESULT_SET_URI, "CQLQueryResultCollection");
	
	// query method constants
	public static final String QUERY_METHOD_NAME = "query";
	public static final String QUERY_METHOD_RETURN_TYPE = CQL_RESULT_SET_TYPE;
	public static final String QUERY_METHOD_PARAMETER_TYPE = CQL_QUERY_TYPE;
	public static final String QUERY_METHOD_PARAMETER_NAME = "cqlQuery";
	public static final String QUERY_METHOD_PARAMETER_DESCRIPTION = "The CQL query to be executed against the data source.";
	public static final String QUERY_IMPLEMENTATION_ADDED = "queryImplAdded";
	public static final String QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method.";
	public static final String QUERY_METHOD_OUTPUT_DESCRIPTION = "The result of executing the CQL query against the data source.";
	
	// data service naming constants
	public static final String DATA_SERVICE_PACKAGE = "gov.nih.nci.cagrid.data";
	public static final String DATA_SERVICE_SERVICE_NAME = "DataService";
	public static final String DATA_SERVICE_NAMESPACE = "http://" + DATA_SERVICE_PACKAGE + "/" + DATA_SERVICE_SERVICE_NAME;
	public static final String DATA_SERVICE_PORT_TYPE_NAME = DATA_SERVICE_SERVICE_NAME + "PortType";
	
	// the server-config.wsdd location
	public static final String SERVER_CONFIG_LOCATION = "serverConfigLocation";
	
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
	
	// exceptions
	public static final String DATA_SERVICE_EXCEPTIONS_NAMESPACE = "http://gov.nih.nci.cagrid.data/DataServiceExceptions";
	public static final String DATA_SERVICE_EXCEPTIONS_SCHEMA = "DataServiceExceptions.xsd";
	public static final String QUERY_PROCESSING_EXCEPTION_NAME = "QueryProcessingException";
	public static final String QUERY_PROCESSING_EXCEPTION_DESCRIPTION = "Thrown when an error occurs in processing a CQL query";
	public static final String MALFORMED_QUERY_EXCEPTION_NAME = "MalformedQueryException";
	public static final String MALFORMED_QUERY_EXCEPTION_DESCRIPTION = "Thrown when a query is found to be improperly formed";
	public static final QName QUERY_PROCESSING_EXCEPTION_QNAME = new QName(DATA_SERVICE_EXCEPTIONS_NAMESPACE, QUERY_PROCESSING_EXCEPTION_NAME);
	public static final QName MALFORMED_QUERY_EXCEPTION_QNAME = new QName(DATA_SERVICE_EXCEPTIONS_NAMESPACE, MALFORMED_QUERY_EXCEPTION_NAME);
	
	// data service specific service parameters
	public static final String DATA_SERVICE_PARAMS_PREFIX = "dataService";
	public static final String CLASS_MAPPINGS_FILENAME = DATA_SERVICE_PARAMS_PREFIX + "_classMappingsFilename";
	public static final String CLASS_TO_QNAME_XML = "classToQname.xml";
	
	// validation constants
	public static final String CQL_VALIDATOR_CLASS = DATA_SERVICE_PARAMS_PREFIX + "_cqlValidatorClass";
	public static final String DOMAIN_MODEL_VALIDATOR_CLASS = DATA_SERVICE_PARAMS_PREFIX + "_domainModelValidatorClass";
	public static final String CQL2_DOMAIN_MODEL_VALIDATOR = DATA_SERVICE_PARAMS_PREFIX + "_cql2DomainModelValidator";
	public static final String CQL2_STRUCTURE_VALIDATOR = DATA_SERVICE_PARAMS_PREFIX + "_cql2StructureValidator";
	public static final String VALIDATE_CQL_FLAG = DATA_SERVICE_PARAMS_PREFIX + "_validateCqlFlag";
	public static final String VALIDATE_DOMAIN_MODEL_FLAG = DATA_SERVICE_PARAMS_PREFIX + "_validateDomainModelFlag";
    public static final String DEFAULT_VALIDATE_CQL_FLAG = Boolean.TRUE.toString();
    public static final String DEFAULT_VALIDATE_DOMAIN_MODEL_FLAG = Boolean.TRUE.toString();
	
	// DomainModel QName
	public static final QName DOMAIN_MODEL_QNAME = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	
	// service metadata QName
	public static final QName SERVICE_METADATA_QNAME = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
	// class to qname mapping QName
	public static final QName MAPPING_QNAME = new QName("http://gov.nih.nci.cagrid.data", "ClassMappings");
	
	// service URL parameter constants
	public static final String CADSR_SERVICE_URL = "CADSR_DATA_URL";
	public static final String GME_SERVICE_URL = "GME_URL";
    public static final String MMS_URL = "MMS_URL";
	
	// castor mapping constants
	public static final String CACORE_CASTOR_MAPPING_FILE = "xml-mapping.xml";
	public static final String CASTOR_MAPPING_WSDD_PARAMETER = "castorMapping";

	// enumeration query method constants
	public static final String ENUMERATION_QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method which begins an Enumeration";
	public static final String ENUMERATION_QUERY_METHOD_NAME = "enumerationQuery";
	public static final String ENUMERATION_DATA_SERVICE_PACKAGE = DATA_SERVICE_PACKAGE + ".enumeration";
	public static final String ENUMERATION_DATA_SERVICE_NAMESPACE = "http://gov.nih.nci.cagrid.data.enumeration/EnumerationDataService";
    public static final String ENUMERATION_QUERY_METHOD_OUTPUT_DESCRIPTION = "The enumerate response containing the EPR of the enumeration resource";
    public static final String ENUMERATION_RESPONSE_NAMESPACE = "http://gov.nih.nci.cagrid.enumeration/EnumerationResponseContainer";
    public static final QName ENUMERATION_QUERY_METHOD_OUTPUT_TYPE = new QName(ENUMERATION_RESPONSE_NAMESPACE, "EnumerationResponseContainer");
    
    // service style constants
    public static final String SERVICE_STYLES_DIR_NAME = "styles";
    public static final String SERVICE_STYLE_FILE_NAME = "style.xml";
	
	// bdt query method constants
    @Deprecated
	public static final String BDT_QUERY_METHOD_NAME = "bdtQuery";
    @Deprecated
	public static final String BDT_QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method which returns results handled by Bulk Data Transfer.";
    @Deprecated
	public static final String BDT_DATA_SERVICE_NAMESPACE = "http://gov.nih.nci.cagrid.data.bdt/BDTDataService";
    @Deprecated
	public static final String BDT_DATA_SERVICE_PACKAGE_NAME = DATA_SERVICE_PACKAGE + ".bdt";
    @Deprecated
	public static final String BDT_HANDLER_CLIENT_CLASSNAME = "gov.nih.nci.cagrid.bdt.client.BulkDataHandlerClient";
    @Deprecated
    public static final QName BDT_HANDLER_REFERENCE_QNAME = new QName("http://cagrid.nci.nih.gov/BulkDataHandlerReference", "BulkDataHandlerReference");
    @Deprecated
    public static final QName BDT_QUERY_METHOD_INPUT_MESSAGE = new QName(BDT_DATA_SERVICE_NAMESPACE, "BdtQueryRequest");
    @Deprecated
    public static final QName BDT_QUERY_METHOD_OUTPUT_MESSAGE = new QName(BDT_DATA_SERVICE_NAMESPACE, "BdtQueryResponse");
    
    // transfer query method constants
    public static final String TRANSFER_QUERY_METHOD_NAME = "transferQuery";
    public static final String TRANSFER_QUERY_METHOD_DESCRIPTION = "The standard caGrid Data Service query method which returns results via the caGrid Transfer framework.";
    public static final String TRANSFER_QUERY_METHOD_OUTPUT_DESCRIPTION = "The transfer context reference containing the EPR of the transfer resource";
    public static final String TRANSFER_DATA_SERVICE_NAMESPACE = "http://gov.nih.nci.cagrid.data.transfer/TransferDataService";
    public static final String TRANSFER_DATA_SERVICE_PACKAGE_NAME = DATA_SERVICE_PACKAGE + ".transfer";
    public static final String TRANSFER_CONTEXT_CLIENT = "org.cagrid.transfer.context.client.TransferServiceContextClient";
    public static final QName TRANSFER_CONTEXT_REFERENCE_QNAME = new QName("http://transfer.cagrid.org/TransferService/Context/types", "TransferServiceContextReference");
    public static final QName TRANSFER_QUERY_METHOD_INPUT_MESSAGE = new QName(TRANSFER_DATA_SERVICE_NAMESPACE, "TransferQueryRequest");
    public static final QName TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE = new QName(TRANSFER_DATA_SERVICE_NAMESPACE, "TransferQueryResponse");
    
    // auditor related constants
    public static final String DATA_SERVICE_AUDITING_NAMESPACE = "http://gov.nih.nci.cagrid.data/Auditing";
	public static final String DATA_SERVICE_AUDITORS_NAME = "DataServiceAuditors";
    public static final QName DATA_SERVICE_AUDITORS_QNAME = new QName(DATA_SERVICE_AUDITING_NAMESPACE, DATA_SERVICE_AUDITORS_NAME);
    public static final String DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY = DATA_SERVICE_PARAMS_PREFIX + "_auditorsConfigFile";
    public static final String DATA_SERVICE_AUDITORS_CONFIG_FILE_NAME = "dataServiceAuditorsConfiguration.xml";
    
    // Local SDK api related
    public static final String LOCAL_SDK_CONF_JAR_POSTFIX = "_LocalSdkConfiguration";
    
	private DataServiceConstants() {
		
	}
}
