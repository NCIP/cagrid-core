package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql2.CQL1toCQL2Converter;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
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
        Resource serviceBaseResource;
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
            LOG.warn("Domain Model NOT FOUND in base resource");
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
                DataServiceConstants.SERVER_CONFIG_LOCATION);
            LOG.debug("Loading server side wsdd from " + serverConfigLocation);
            InputStream configStream = new FileInputStream(serverConfigLocation);
            serverConfigBytes = Utils.inputStreamToStringBuffer(configStream).toString().getBytes();
        } catch (Exception ex) {
            throw new DataServiceInitializationException("Error loading server config wsdd: " + ex.getMessage(), ex);
        }
    }
    
    
    protected Properties getDataServiceConfig() {
        return dataServiceConfig;
    }
    
    
    protected DomainModel getDomainModel() {
        return domainModel;
    }
    
    
    /**
     * Helper method to easily and consistently create a typed exception
     * 
     * @param cause
     * @param fault
     * @return
     */
    protected Exception getTypedException(Exception cause, BaseFaultType fault) {
        FaultHelper helper = new FaultHelper(fault);
        helper.addFaultCause(cause);
        helper.setDescription(cause.getClass().getSimpleName() + " -- " + cause.getMessage());
        return helper.getFault();
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
        // TODO:
        // 1. Validate query
        // 2. If can process native, process and return
        // 3. Else, convert, process w/ CQL 2, convert results, return
        queryValidator.validateCql1Query(query);
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
            results = getCql1QueryProcessor().processQuery(query);
        } else {
            LOG.debug("Converting CQL 1 to CQL 2 for non-native processing");
            org.cagrid.cql2.CQLQuery cql2Query = cql1to2converter.convertToCql2Query(query);
            org.cagrid.cql2.results.CQLQueryResults cql2Results = processCql2Query(cql2Query);
            // TODO: converter to turn CQL 2 results into CQL 1
        }
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
            // if we can natively handle the query with a lazy implementation, do so
            queryValidator.validateCql1Query(query);
            resultsIterator = ((LazyCQLQueryProcessor) getCql1QueryProcessor()).processQueryLazy(query);
        } else {
            // process normally and wrap the results with an iterator
            CQLQueryResults results = processCql1Query(query);
            resultsIterator = new CQLQueryResultsIterator(results, new ByteArrayInputStream(serverConfigBytes));
        }
        return resultsIterator;
    }
    
    
    public org.cagrid.cql2.results.CQLQueryResults processCql2Query(org.cagrid.cql2.CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        // TODO:
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
        if (processNative) {
            LOG.debug("Processing CQL 2 query with native query processor");
            results = getCql2QueryProcessor().processQuery(query);
        } else {
            LOG.debug("Converting CQL 2 to CQL 1 for non-native processing");
            // TODO: convert query and results
        }
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
        if (processNative) {
            LOG.debug("Processing CQL 2 query with native query processor");
            resultsIterator = getCql2QueryProcessor().processQueryAndIterate(query);
        } else {
            LOG.debug("Converting CQL 2 to CQL 1 for non-native processing");
            // TODO: convert query and results
        }
        return resultsIterator;
    }
    
    
    private CQLQueryProcessor getCql1QueryProcessor() throws QueryProcessingException {
        if (cql1QueryProcessor != null) {
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
                    DataServiceConstants.SERVER_CONFIG_LOCATION);
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
        if (cql2QueryProcessor != null) {
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
                    DataServiceConstants.SERVER_CONFIG_LOCATION);
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
                // get the mapping file name
                String filename = ServiceConfigUtil.getClassToQnameMappingsFile();
                // deserialize the mapping file
                Mappings mappings = Utils.deserializeDocument(filename, Mappings.class);
                cql2QueryProcessor.configure(unionProperties, configStream, mappings);
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
            String processorClassname = getDataServiceConfig().getProperty(QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY);
            hasProcessor = processorClassname != null && processorClassname.length() != 0;
        }
        return hasProcessor;
    }
}
