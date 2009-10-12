package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql2.CQL1toCQL2Converter;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.data.cql2.validation.Cql2DomainValidator;
import gov.nih.nci.cagrid.data.cql2.validation.Cql2StructureValidator;
import gov.nih.nci.cagrid.data.mapping.Mappings;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseCQL2DataServiceImpl extends BaseServiceImpl {
    
    private static Log LOG = LogFactory.getLog(BaseCQL2DataServiceImpl.class);
    
    private CQL1toCQL2Converter queryConverter = null;
    private Cql2StructureValidator queryStructureValidator = null;
    private Cql2DomainValidator queryDomainValidator = null;
    private Properties queryProcessorConfiguration = null;
    private CQL2QueryProcessor queryProcessorInstance = null;

    public BaseCQL2DataServiceImpl() throws DataServiceInitializationException {
        super();
        try {
            queryConverter = new CQL1toCQL2Converter(getDomainModel());
        } catch (Exception ex) {
            String message = "Error setting up CQL 1 to CQL 2 converter: " + ex.getMessage();
            LOG.error(message, ex);
            throw new DataServiceInitializationException(message, ex);
        }
    }
    
    
    protected CQL1toCQL2Converter getQueryConverter() {
        return queryConverter;
    }
    
    
    protected Cql2StructureValidator getCqlStructureValidator() throws QueryProcessingException {
        if (queryStructureValidator == null) {
            try {
                String validatorClassName = getDataServiceConfig().getProperty(DataServiceConstants.CQL2_STRUCTURE_VALIDATOR);
                LOG.debug("Loading CQL 2 structure validator class " + validatorClassName);
                Class<?> validatorClass = Class.forName(validatorClassName);
                queryStructureValidator = (Cql2StructureValidator) validatorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting CQL 2 structure validator: " + ex.getMessage(), ex);
            }
        }
        return queryStructureValidator;
    }
    
    
    protected Cql2DomainValidator getCqlDomainValidator() throws QueryProcessingException {
        if (queryDomainValidator == null) {
            try {
                String validatorClassName = getDataServiceConfig().getProperty(DataServiceConstants.CQL2_DOMAIN_MODEL_VALIDATOR);
                LOG.debug("Loading CQL 2 Domain Model validator class " + validatorClassName);
                Class<?> validatorClass = Class.forName(validatorClassName);
                queryDomainValidator = (Cql2DomainValidator) validatorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting CQL 2 domain validator: " + ex.getMessage(), ex);
            }
        }
        return queryDomainValidator;
    }
    
    
    protected CQL2QueryProcessor getCql2QueryProcessorInstance() throws QueryProcessingException {
        if (queryProcessorInstance == null) {
            LOG.debug("Instantiating CQL 2 query processor");
            // get the query processor's class
            String qpClassName = null;
            try {
                qpClassName = ServiceConfigUtil.getCql2QueryProcessorClassName();
                LOG.debug("CQL 2 Query Processor class name is " + qpClassName);
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error determining CQL 2 query processor class name: " + ex.getMessage(), ex);
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
                queryProcessorInstance = (CQL2QueryProcessor) cqlQueryProcessorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error creating CQL 2 query processor instance: " + ex.getMessage(), ex);
            }
            // configure the instance
            LOG.debug("Configuring CQL 2 query processor");
            try {
                String serverConfigLocation = ServiceConfigUtil.getConfigProperty(
                    DataServiceConstants.SERVER_CONFIG_LOCATION);
                InputStream configStream = new FileInputStream(serverConfigLocation);
                Properties configuredProperties = getCqlQueryProcessorConfig();
                Properties defaultProperties = queryProcessorInstance.getRequiredParameters();
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
                queryProcessorInstance.configure(unionProperties, configStream, mappings);
            } catch (Exception ex) {
                throw new QueryProcessingException("Error initializing CQL 2 query processor: " + ex.getMessage(), ex);
            }
        }
        return queryProcessorInstance;
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
    protected Properties getCqlQueryProcessorConfig() throws QueryProcessingException {
        if (queryProcessorConfiguration == null) {
            LOG.debug("Loading CQL 2 query processor configuration properties");
            try {
                queryProcessorConfiguration = ServiceConfigUtil.getCql2QueryProcessorConfigurationParameters();
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error getting CQL 2 query processor configuration parameters: " + ex.getMessage(), ex);
            }
        }
        // clone the query processor config instance 
        // (in case they get modified by the Query Processor implementation)
        Properties clone = new Properties();
        Enumeration<?> keyEnumeration = queryProcessorConfiguration.keys();
        while (keyEnumeration.hasMoreElements()) {
            String key = (String) keyEnumeration.nextElement();
            String value = queryProcessorConfiguration.getProperty(key);
            clone.setProperty(key, value);
        }
        return clone;
    }
}
