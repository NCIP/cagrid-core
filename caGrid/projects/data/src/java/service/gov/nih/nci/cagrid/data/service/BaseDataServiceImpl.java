package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.AuditorConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.MetadataConstants;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.ServiceParametersConstants;
import gov.nih.nci.cagrid.data.auditing.AuditorConfiguration;
import gov.nih.nci.cagrid.data.auditing.DataServiceAuditors;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.data.cql2.Cql2ExtensionPoint;
import gov.nih.nci.cagrid.data.cql2.validation.StructureValidationException;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.service.auditing.DataServiceAuditor;
import gov.nih.nci.cagrid.data.service.auditing.QueryBeginAuditingEvent;
import gov.nih.nci.cagrid.data.service.auditing.QueryProcessingFailedAuditingEvent;
import gov.nih.nci.cagrid.data.service.auditing.QueryResultsAuditingEvent;
import gov.nih.nci.cagrid.data.service.auditing.ValidationAuditingEvent;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql.utilities.CQL1toCQL2Converter;
import org.cagrid.cql.utilities.CQL2ResultsToCQL1ResultsConverter;
import org.cagrid.cql.utilities.QueryConversionException;
import org.cagrid.cql.utilities.ResultsConversionException;
import org.cagrid.cql2.extensionsupport.SupportedExtensions;
import org.cagrid.dataservice.cql.support.Cql2SupportType;
import org.cagrid.dataservice.cql.support.QueryLanguageSupport;
import org.cagrid.dataservice.cql.support.QueryLanguageSupportCQL2Support;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.security.SecurityManager;
import org.oasis.wsrf.faults.BaseFaultType;

/**
 * BaseDataServiceImpl
 * Base class of data service implementations.  Handles common functionality
 * such as query processor instantiation, CQL query conversion, and basic
 * auditing support.
 * 
 * @author David
 */
public abstract class BaseDataServiceImpl {

    private static Log LOG = LogFactory.getLog(BaseDataServiceImpl.class);
    
    private byte[] serverConfigBytes = null;
    private Properties dataServiceConfig = null;
    private DomainModel domainModel = null;
    private Mappings classToQnameMappings = null;
    private List<DataServiceAuditor> auditors = null;
    
    private CQLQueryProcessor cql1QueryProcessor = null;
    private CQL2QueryProcessor cql2QueryProcessor = null;
    
    private Properties cql1ProcessorConfig = null;
    private Properties cql2ProcessorConfig = null;
    
    private CqlValidationUtil queryValidator = null;
    private CQL1toCQL2Converter cql1to2converter = null;
    
    public BaseDataServiceImpl() throws DataServiceInitializationException {
        initialize();
    }
    
    
    private void initialize() throws DataServiceInitializationException {
        // load the core data service infrastructure configuration
        LOG.debug("Loading data service configuration properties");
        try {
            dataServiceConfig = ServiceConfigUtil.getDataServiceParams();
        } catch (Exception ex) {
            throw new DataServiceInitializationException(
                "Error getting data service configuration parameters: " + ex.getMessage(), ex);
        }
        // load the domain model
        LOG.debug("Attempting to locate Domain Model in base resource");
        Resource serviceBaseResource = null;
        try {
            serviceBaseResource = ResourceContext.getResourceContext().getResource();
        } catch (Exception ex) {
            throw new DataServiceInitializationException("Error obtaining base resource: " + ex.getMessage(), ex);
        }
        Method[] resourceMethods = serviceBaseResource.getClass().getMethods();
        for (Method method : resourceMethods) {
            if (method.getReturnType() != null 
                && method.getReturnType().equals(DomainModel.class)) {
                try {
                    domainModel = (DomainModel) method.invoke(serviceBaseResource, new Object[] {});
                } catch (Exception ex) {
                    throw new DataServiceInitializationException("Error invoking method " 
                        + method.getName() + " of base resource to obtain the Domain Model: " 
                        + ex.getMessage(), ex);
                }
                break;
            }
        }
        if (domainModel != null) {
            LOG.debug("Domain Model found in base resource");
        } else {
            LOG.error("Domain Model NOT FOUND in base resource");
        }
        // load the class to QName mappings
        LOG.debug("Loading class to QName mappings information");
        try {
            // get the mapping file name
            String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
            LOG.debug("Loading class to QName mappings from " + filename);
            // deserialize the mapping file
            classToQnameMappings = Utils.deserializeDocument(filename, Mappings.class);
        } catch (Exception ex) {
            throw new DataServiceInitializationException("Error loading class to QName mappings: " + ex.getMessage(), ex);
        }
        // initialize the query validator
        LOG.debug("Initializing query validator");
        queryValidator = new CqlValidationUtil(dataServiceConfig, domainModel);
        // set up the query conversion tools
        LOG.debug("Setting up query conversion tools");
        cql1to2converter = new CQL1toCQL2Converter(domainModel);
        // load the server-config.wsdd into memory
        try {
            String serverConfigLocation = ServiceConfigUtil.getConfigProperty(
                ServiceParametersConstants.SERVER_CONFIG_LOCATION);
            LOG.debug("Loading server side wsdd from " + serverConfigLocation);
            InputStream configStream = new FileInputStream(serverConfigLocation);
            serverConfigBytes = Utils.inputStreamToStringBuffer(configStream).toString().getBytes();
        } catch (Exception ex) {
            throw new DataServiceInitializationException("Error loading server config wsdd: " + ex.getMessage(), ex);
        }
        LOG.debug("Initializing data service auditors");
        auditors = new LinkedList<DataServiceAuditor>();
        String configFileName = getDataServiceConfig().getProperty(
            AuditorConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY);
        if (configFileName != null) {
            try {
                DataServiceAuditors auditorConfig = 
                    Utils.deserializeDocument(configFileName, DataServiceAuditors.class);
                if (auditorConfig.getAuditorConfiguration() != null) {
                    for (AuditorConfiguration config : auditorConfig.getAuditorConfiguration()) {
                        DataServiceAuditor auditor = createAuditor(config);
                        auditors.add(auditor);
                    }
                }
            } catch (Exception ex) {
                throw new DataServiceInitializationException("Error initializing data service auditors: " + ex.getMessage(), ex);
            }
        }
        // set the query language support metadata
        LOG.debug("Creating the language support resource property");
        try {
            // build up the language support bean
            QueryLanguageSupport languageSupport = new QueryLanguageSupport();
            if (!hasNativeCql2Processor()) {
                LOG.error("*******************************************************************");
                LOG.error("* NO CQL 2 QUERY PROCESSOR WAS PROVIDED IN THE SERVICE PROPERTIES *");
                LOG.error("*   A CQL 2 QUERY PROCESSOR IS REQUIRED FOR CAGRID 1.4 AND NEWER  *");
                LOG.error("*      QUERIES TO ANY CQL 2 METHOD ON THIS SERVICE WILL FAIL      *");
                LOG.error("*******************************************************************");
                languageSupport.setCQL2NotSupported(Cql2SupportType.ImplementationNotProvided);
            } else {
                // CQL 2 fully supported, set the extensions on the resource property
                QueryLanguageSupportCQL2Support cql2Support = new QueryLanguageSupportCQL2Support();
                SupportedExtensions extSupport = new SupportedExtensions();
                extSupport.setAttributeExtension(
                    getCql2QueryProcessor().getSupportedExtensions(
                        Cql2ExtensionPoint.ATTRIBUTE).toArray(new QName[0]));
                extSupport.setModifierExtension(
                    getCql2QueryProcessor().getSupportedExtensions(
                        Cql2ExtensionPoint.MODIFIER).toArray(new QName[0]));
                extSupport.setObjectExtension(
                    getCql2QueryProcessor().getSupportedExtensions(
                        Cql2ExtensionPoint.OBJECT).toArray(new QName[0]));
                extSupport.setResultExtension(
                    getCql2QueryProcessor().getSupportedExtensions(
                        Cql2ExtensionPoint.RESULT).toArray(new QName[0]));
                cql2Support.setSupportedExtensions(extSupport);
                languageSupport.setCQL2Support(cql2Support);
            }
            // locate the setter method on the base resource
            boolean foundMethod = false;
            for (Method method : resourceMethods) {
                if (method.getName().equals(MetadataConstants.QUERY_LANGUAGE_SUPPORT_RESOURCE_GETTER_METHOD_NAME)) {
                    method.invoke(serviceBaseResource, languageSupport);
                    LOG.debug("Set the query language support resource property");
                    foundMethod = true;
                    break;
                }
            }
            if (!foundMethod) {
                LOG.error("Could not locate '" + 
                    MetadataConstants.QUERY_LANGUAGE_SUPPORT_RESOURCE_GETTER_METHOD_NAME + 
                    "()' method on service base resource.  " +
                    "Query language support resource property WAS NOT SET");
            }
        } catch (Exception ex) {
            throw new DataServiceInitializationException(
                "Error setting query language support resource property: " + ex.getMessage(), ex);
        }
    }
    
    
    protected Properties getDataServiceConfig() {
        return dataServiceConfig;
    }
    
    
    protected DomainModel getDomainModel() {
        return domainModel;
    }
    
    
    protected Mappings getClassToQnameMappings() {
        return classToQnameMappings;
    }
    
    
    protected InputStream getServerConfigWsddStream() {
        return new ByteArrayInputStream(serverConfigBytes);
    }
    
    
    protected QName getQNameForClass(String clazz) {
        QName name = null;
        LOG.debug("Checking class to qname mappings for " + clazz);
        Mappings mappings = getClassToQnameMappings();
        if (mappings.getMapping() != null) {
            for (ClassToQname c2q : mappings.getMapping()) {
                if (c2q.getClassName().equals(clazz)) {
                    name = QName.valueOf(c2q.getQname());
                    LOG.debug("Found mapping");
                    break;
                }
            }
        }
        if (name == null) {
            LOG.debug("QName not found in class to qname mappings, checking Axis types registry");
            try {
                name = Utils.getRegisteredQName(Class.forName(clazz));
            } catch (ClassNotFoundException e) {
                LOG.error("UNABLE TO LOAD CLASS " + clazz + " TO DETERMINE QNAME: " + e.getMessage(), e);
            }
        }
        if (name == null) {
            LOG.warn("Unable to determine a QName for class " + clazz);
        }
        return name;
    }
    
    
    /**
     * Helper method to easily and consistently create a typed exception
     * 
     * @param cause
     * @param fault
     * @return
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    @SuppressWarnings("unchecked")
    protected <T extends BaseFaultType> T getTypedException(Exception cause, T faultType) {
        FaultHelper helper = new FaultHelper(faultType);
        helper.addFaultCause(cause);
        helper.setDescription(cause.getClass().getSimpleName() + " -- " + cause.getMessage());
        return (T) helper.getFault();
    }
        
    
    /**
     * Gets the cql query processor configuration properties specified in
     * the service's deploy.properties and therefore JNDI at runtime.
     * 
     * This will NOT return the default properties for the query processor
     * 
     * @return
     *      The configured cql query processor properties available
     *      at runtime in the JNDI
     * @throws QueryProcessingException
     */
    protected Properties getCql1QueryProcessorConfig() throws QueryProcessingException {
        if (cql1ProcessorConfig == null) {
            LOG.debug("Loading CQL query processor configuration properties");
            try {
                cql1ProcessorConfig = ServiceConfigUtil.getQueryProcessorConfigurationParameters();
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error getting query processor configuration parameters: " + ex.getMessage(), ex);
            }
        }
        // clone the query processor config instance 
        // (in case they get modified by the Query Processor implementation)
        Properties clone = new Properties();
        Enumeration<?> keyEnumeration = cql1ProcessorConfig.keys();
        while (keyEnumeration.hasMoreElements()) {
            String key = (String) keyEnumeration.nextElement();
            String value = cql1ProcessorConfig.getProperty(key);
            clone.setProperty(key, value);
        }
        return clone;
    }
    
    
    /**
     * Gets the cql 2 query processor configuration properties specified in
     * the service's deploy.properties and therefore JNDI at runtime.
     * 
     * This will NOT return the default properties for the query processor
     * 
     * @return
     *      The configured cql query processor properties available
     *      at runtime in the JNDI
     * @throws QueryProcessingException
     */
    protected Properties getCql2QueryProcessorConfig() throws QueryProcessingException {
        if (cql2ProcessorConfig == null) {
            LOG.debug("Loading CQL query processor configuration properties");
            try {
                cql2ProcessorConfig = ServiceConfigUtil.getCql2QueryProcessorConfigurationParameters();
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error getting query processor configuration parameters: " + ex.getMessage(), ex);
            }
        }
        // clone the query processor config instance 
        // (in case they get modified by the Query Processor implementation)
        Properties clone = new Properties();
        Enumeration<?> keyEnumeration = cql2ProcessorConfig.keys();
        while (keyEnumeration.hasMoreElements()) {
            String key = (String) keyEnumeration.nextElement();
            String value = cql2ProcessorConfig.getProperty(key);
            clone.setProperty(key, value);
        }
        return clone;
    }
    
    
    /**
     * Processes a CQL query using the CQL query processor, performing any
     * necessary conversions to / from CQL 2
     * 
     * @param query
     * @return
     * @throws QueryProcessingException
     * @throws MalformedQueryException
     */
    public CQLQueryResults processCql1Query(CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        // 1. Validate query
        // 2. If can process native, process and return
        // 3. Else, convert, process w/ CQL 2, convert results, return
        fireAuditQueryBegins(query);
        try {
            queryValidator.validateCql1Query(query);
        } catch (MalformedQueryException ex) {
            // log the failure and rethrow
            if (ex instanceof StructureValidationException) {
                fireAuditValidationFailure(query, ex, null);
            } else {
                fireAuditValidationFailure(query, null, ex);
            }
            throw ex;
        }
        CQLQueryResults results = null;
        boolean processNative = true;
        try {
            processNative = hasNativeCql1Processor();
        } catch (Exception ex) {
            throw new QueryProcessingException(
                "Error determining if a native CQL 1 query processor has been configured: " + ex.getMessage(), ex);
        }
        if (processNative) {
            LOG.debug("Processing CQL 1 query with native query processor");
            try {
                results = getCql1QueryProcessor().processQuery(query);
            } catch (QueryProcessingException ex) {
                // log the failure and rethrow
                fireAuditQueryProcessingFailure(query, ex);
                throw ex;
            }
        } else {
            LOG.debug("Converting CQL 1 to CQL 2 for non-native processing");
            org.cagrid.cql2.CQLQuery cql2Query;
            try {
                cql2Query = cql1to2converter.convertToCql2Query(query);
            } catch (QueryConversionException ex) {
                throw new QueryProcessingException(ex.getMessage(), ex);
            }
            org.cagrid.cql2.results.CQLQueryResults cql2Results = processCql2Query(cql2Query);
            try {
                LOG.debug("Converting CQL 2 results to CQL 1 results");
                results = CQL2ResultsToCQL1ResultsConverter.convertResults(cql2Results);
            } catch (ResultsConversionException ex) {
                throw new QueryProcessingException(ex.getMessage(), ex);
            }
        }
        fireAuditQueryResults(query, results);
        return results;
    }
    
    
    public Iterator<?> processCql1QueryAndIterate(CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        Iterator<?> resultsIterator = null;
        boolean processNative = true;
        try {
            processNative = hasNativeCql1Processor();
        } catch (Exception ex) {
            throw new QueryProcessingException(
                "Error determining if a native CQL 1 query processor has been configured: " + ex.getMessage(), ex);
        }
        if (processNative && getCql1QueryProcessor() instanceof LazyCQLQueryProcessor) {
            fireAuditQueryBegins(query);
            // if we can natively handle the query with a lazy implementation, do so
            try {
                queryValidator.validateCql1Query(query);
            } catch (MalformedQueryException ex) {
                // log the failure and rethrow
                if (ex instanceof StructureValidationException) {
                    fireAuditValidationFailure(query, ex, null);
                } else {
                    fireAuditValidationFailure(query, null, ex);
                }
                throw ex;
            }
            try {
                resultsIterator = ((LazyCQLQueryProcessor) getCql1QueryProcessor()).processQueryLazy(query);
            } catch (QueryProcessingException ex) {
                // log the failure and rethrow
                fireAuditQueryProcessingFailure(query, ex);
                throw ex;
            }
        } else {
            // process normally and wrap the results with an iterator
            CQLQueryResults results = processCql1Query(query);
            resultsIterator = new CQLQueryResultsIterator(results, getServerConfigWsddStream());
        }
        return resultsIterator;
    }
    
    
    public org.cagrid.cql2.results.CQLQueryResults processCql2Query(org.cagrid.cql2.CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        // 1. Validate query
        // 2. If can process native, process and return
        // 3. Else, convert, process w/ CQL 1, convert results, return
        queryValidator.validateCql2Query(query);
        org.cagrid.cql2.results.CQLQueryResults results = null;
        boolean processNative = true;
        try {
            processNative = hasNativeCql2Processor();
        } catch (Exception ex) {
            throw new QueryProcessingException(
                "Error determining if a native CQL 2 query processor has been configured: " + ex.getMessage(), ex);
        }
        if (!processNative) {
            // to handle CQL 2 you MUST have a CQL 2 query processor.  The data service publishes 
            // a resource property that warns if there is no implementation provided, and 
            // enumerates the supported extension points if any are available
            throw new QueryProcessingException(
                "No implementation of CQL 2 is provided by this service", new UnsupportedOperationException());
        }
        LOG.debug("Processing CQL 2 query with native query processor");
        results = getCql2QueryProcessor().processQuery(query);
        return results;
    }
    
    
    public Iterator<org.cagrid.cql2.results.CQLResult> processCql2QueryAndIterate(org.cagrid.cql2.CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        queryValidator.validateCql2Query(query);
        Iterator<org.cagrid.cql2.results.CQLResult> resultsIterator = null;
        boolean processNative = true;
        try {
            processNative = hasNativeCql2Processor();
        } catch (Exception ex) {
            throw new QueryProcessingException(
                "Error determining if a native CQL 2 query processor has been configured: " + ex.getMessage(), ex);
        }
        if (!processNative) {
            // to handle CQL 2 you MUST have a CQL 2 query processor.  The data service publishes 
            // a resource property that warns if there is no implementation provided, and 
            // enumerates the supported extension points if any are available
            throw new QueryProcessingException(
                "No implementation of CQL 2 is provided by this service", new UnsupportedOperationException());
        }
        LOG.debug("Processing CQL 2 query with native query processor");
        resultsIterator = getCql2QueryProcessor().processQueryAndIterate(query);
        return resultsIterator;
    }
    
    
    private CQLQueryProcessor getCql1QueryProcessor() throws QueryProcessingException {
        if (cql1QueryProcessor == null) {
            LOG.debug("Instantiating CQL query processor");
            // get the query processor's class
            String qpClassName = null;
            try {
                qpClassName = ServiceConfigUtil.getCqlQueryProcessorClassName();
                LOG.debug("CQL Query Processor class name is " + qpClassName);
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error determining query processor class name: " + ex.getMessage(), ex);
            }
            Class<?> cqlQueryProcessorClass = null;
            try {
                cqlQueryProcessorClass = Class.forName(qpClassName);
            } catch (ClassNotFoundException ex) {
                throw new QueryProcessingException(
                    "Error loading query processor class: " + ex.getMessage(), ex);
            }
            // create a new instance of the query processor
            try {
                cql1QueryProcessor = (CQLQueryProcessor) cqlQueryProcessorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error creating query processor instance: " + ex.getMessage(), ex);
            }
            // configure the instance
            LOG.debug("Configuring CQL query processor");
            try {
                String serverConfigLocation = ServiceConfigUtil.getConfigProperty(
                    ServiceParametersConstants.SERVER_CONFIG_LOCATION);
                InputStream configStream = new FileInputStream(serverConfigLocation);
                Properties configuredProperties = getCql1QueryProcessorConfig();
                Properties defaultProperties = cql1QueryProcessor.getRequiredParameters();
                Properties unionProperties = new Properties();
                Enumeration<?> defaultKeys = defaultProperties.keys();
                while (defaultKeys.hasMoreElements()) {
                    String key = (String) defaultKeys.nextElement();
                    String value = null;
                    if (configuredProperties.keySet().contains(key)) {
                        value = configuredProperties.getProperty(key);
                    } else {
                        value = defaultProperties.getProperty(key);
                    }
                    unionProperties.setProperty(key, value);
                }
                cql1QueryProcessor.initialize(unionProperties, configStream);
            } catch (Exception ex) {
                throw new QueryProcessingException("Error initializing query processor: " + ex.getMessage(), ex);
            }
        }
        return cql1QueryProcessor;
    }
    
    
    private CQL2QueryProcessor getCql2QueryProcessor() throws QueryProcessingException {
        if (cql2QueryProcessor == null) {
            LOG.debug("Instantiating CQL 2 query processor");
            // get the query processor's class
            String qpClassName = null;
            try {
                qpClassName = ServiceConfigUtil.getCql2QueryProcessorClassName();
                LOG.debug("CQL 2 Query Processor class name is " + qpClassName);
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error determining query processor class name: " + ex.getMessage(), ex);
            }
            Class<?> cqlQueryProcessorClass = null;
            try {
                cqlQueryProcessorClass = Class.forName(qpClassName);
            } catch (ClassNotFoundException ex) {
                throw new QueryProcessingException(
                    "Error loading query processor class: " + ex.getMessage(), ex);
            }
            // create a new instance of the query processor
            try {
                cql2QueryProcessor = (CQL2QueryProcessor) cqlQueryProcessorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error creating query processor instance: " + ex.getMessage(), ex);
            }
            // configure the instance
            LOG.debug("Configuring CQL 2 query processor");
            try {
                String serverConfigLocation = ServiceConfigUtil.getConfigProperty(
                    ServiceParametersConstants.SERVER_CONFIG_LOCATION);
                InputStream configStream = new FileInputStream(serverConfigLocation);
                Properties configuredProperties = getCql2QueryProcessorConfig();
                Properties defaultProperties = cql2QueryProcessor.getRequiredParameters();
                Properties unionProperties = new Properties();
                Enumeration<?> defaultKeys = defaultProperties.keys();
                while (defaultKeys.hasMoreElements()) {
                    String key = (String) defaultKeys.nextElement();
                    String value = null;
                    if (configuredProperties.keySet().contains(key)) {
                        value = configuredProperties.getProperty(key);
                    } else {
                        value = defaultProperties.getProperty(key);
                    }
                    unionProperties.setProperty(key, value);
                }
                cql2QueryProcessor.configure(unionProperties, configStream, getClassToQnameMappings());
            } catch (Exception ex) {
                throw new QueryProcessingException("Error initializing query processor: " + ex.getMessage(), ex);
            }
        }
        return cql2QueryProcessor;
    }
    
    
    private boolean hasNativeCql1Processor() throws Exception {
        boolean hasProcessor = false;
        if (ServiceConfigUtil.hasConfigProperty(QueryProcessorConstants.QUERY_PROCESSOR_CLASS_PROPERTY)) {
            String processorClassname = ServiceConfigUtil.getCqlQueryProcessorClassName();
            hasProcessor = processorClassname != null && processorClassname.length() != 0;
        }
        return hasProcessor;
    }
    
    
    private boolean hasNativeCql2Processor() throws Exception {
        boolean hasProcessor = false;
        if (ServiceConfigUtil.hasConfigProperty(QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY)) {
            String processorClassname = ServiceConfigUtil.getCql2QueryProcessorClassName();
            hasProcessor = processorClassname != null && processorClassname.length() != 0;
        }
        return hasProcessor;
    }
    
    
    // ----------
    // Auditor support
    // ----------
    
    
    private DataServiceAuditor createAuditor(AuditorConfiguration config) throws Exception {
        String auditorClassName = config.getClassName();
        Class<?> auditorClass = Class.forName(auditorClassName);
        DataServiceAuditor auditor = (DataServiceAuditor) auditorClass.newInstance();
        auditor.setAuditorConfiguration(config);
        return auditor;
    }
    
    
    /**
     * Fires a query begins auditing event
     * 
     * @param query
     * @throws RemoteException
     */
    protected void fireAuditQueryBegins(CQLQuery query) {
        if (auditors.size() != 0) {
            String callerIdentity = SecurityManager.getManager().getCaller();
            QueryBeginAuditingEvent event = new QueryBeginAuditingEvent(query, callerIdentity);
            for (DataServiceAuditor auditor : auditors) {
                if (auditor.getAuditorConfiguration()
                    .getMonitoredEvents().isQueryBegin()) {
                    auditor.auditQueryBegin(event);
                }
            }
        }
    }
    
    
    /**
     * Fires a validation failure auditing event
     * 
     * @param query
     * @param structureException
     * @param domainException
     * @throws RemoteException
     */
    protected void fireAuditValidationFailure(CQLQuery query, 
        MalformedQueryException structureException, MalformedQueryException domainException) {
        if (auditors.size() != 0) {
            String callerIdentity = SecurityManager.getManager().getCaller();
            ValidationAuditingEvent event = 
                new ValidationAuditingEvent(query, callerIdentity, structureException, domainException);
            for (DataServiceAuditor auditor : auditors) {
                if (auditor.getAuditorConfiguration()
                    .getMonitoredEvents().isValidationFailure()) {
                    auditor.auditValidation(event);
                }
            }
        }
    }
    
    
    /**
     * Fires a query processing failure auditing event
     * 
     * @param query
     * @param qpException
     * @throws RemoteException
     */
    protected void fireAuditQueryProcessingFailure(CQLQuery query,
        QueryProcessingException qpException) {
        if (auditors.size() != 0) {
            String callerIdentity = SecurityManager.getManager().getCaller();
            QueryProcessingFailedAuditingEvent event = 
                new QueryProcessingFailedAuditingEvent(query, callerIdentity, qpException);
            for (DataServiceAuditor auditor : auditors) {
                if (auditor.getAuditorConfiguration()
                    .getMonitoredEvents().isQueryProcessingFailure()) {
                    auditor.auditQueryProcessingFailed(event);
                }
            }
        }
    }
    
    
    /**
     * Fires a query results auditing event
     * 
     * @param query
     * @param results
     * @throws RemoteException
     */
    protected void fireAuditQueryResults(CQLQuery query, CQLQueryResults results) {
        if (auditors.size() != 0) {
            String callerIdentity = SecurityManager.getManager().getCaller();
            QueryResultsAuditingEvent event = new QueryResultsAuditingEvent(query, callerIdentity, results);
            for (DataServiceAuditor auditor : auditors) {
                if (auditor.getAuditorConfiguration()
                    .getMonitoredEvents().isQueryResults()) {
                    auditor.auditQueryResults(event);
                }
            }
        }
    }
}
