package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql.validation.CqlDomainValidator;
import gov.nih.nci.cagrid.data.cql.validation.CqlStructureValidator;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseCQL1DataServiceImpl extends BaseServiceImpl {
    
    private static Log LOG = LogFactory.getLog(BaseCQL1DataServiceImpl.class);
    
    private CqlStructureValidator cqlStructureValidator = null;
    private CqlDomainValidator cqlDomainValidator = null;
    private Properties cqlQueryProcessorConfig = null;
    private CQLQueryProcessor queryProcessorInstance = null;
    
    public BaseCQL1DataServiceImpl() throws DataServiceInitializationException {
        super();
        // force initialization of the query processor at creation
        try {
            getCqlQueryProcessorInstance();
        } catch (Exception ex) {
            throw new DataServiceInitializationException("Error initializing query processor instance: " + ex.getMessage(), ex);
        }
    }
    
    
    protected void preProcess(CQLQuery cqlQuery) throws QueryProcessingException, MalformedQueryException {
        // validation for cql structure
        if (shouldValidateCqlStructure()) {
            CqlStructureValidator validator = getCqlStructureValidator();
            LOG.debug("Validating CQL structure");
            try {
                validator.validateCqlStructure(cqlQuery);
            } catch (gov.nih.nci.cagrid.data.MalformedQueryException ex) {
                fireAuditValidationFailure(cqlQuery, ex, null);
                throw ex;
            }
        }
        
        // validation for domain model
        if (shouldValidateDomainModel()) {
            CqlDomainValidator validator = getCqlDomainValidator();
            LOG.debug("Validating CQL against domain model");
            try {
                DomainModel model = getDomainModel();
                if (model != null) {
                    validator.validateDomainModel(cqlQuery, model);
                } else {
                    LOG.warn("Domain model validation enabled, but no domain model was found!");
                }
            } catch (gov.nih.nci.cagrid.data.MalformedQueryException ex) {
                fireAuditValidationFailure(cqlQuery, null, ex);
                throw ex;
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting domain model for validation: " + ex.getMessage(), ex);
            }
        }
    }
        
    
    protected CqlStructureValidator getCqlStructureValidator() throws QueryProcessingException {
        if (cqlStructureValidator == null) {
            try {
                String validatorClassName = getDataServiceConfig().getProperty(DataServiceConstants.CQL_VALIDATOR_CLASS);
                LOG.debug("Loading CQL structure validator class " + validatorClassName);
                Class<?> validatorClass = Class.forName(validatorClassName);
                cqlStructureValidator = (CqlStructureValidator) validatorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting CQL structure validator: " + ex.getMessage(), ex);
            }
        }
        return cqlStructureValidator;
    }
    
    
    protected CqlDomainValidator getCqlDomainValidator() throws QueryProcessingException {
        if (cqlDomainValidator == null) {
            try {
                String validatorClassName = getDataServiceConfig().getProperty(DataServiceConstants.DOMAIN_MODEL_VALIDATOR_CLASS);
                LOG.debug("Loading CQL Domain Model validator class " + validatorClassName);
                Class<?> validatorClass = Class.forName(validatorClassName);
                cqlDomainValidator = (CqlDomainValidator) validatorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting CQL domain validator: " + ex.getMessage(), ex);
            }
        }
        return cqlDomainValidator;
    }
    
    
    protected CQLQueryProcessor getCqlQueryProcessorInstance() throws QueryProcessingException {
        if (queryProcessorInstance == null) {
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
                queryProcessorInstance = (gov.nih.nci.cagrid.data.cql.CQLQueryProcessor) cqlQueryProcessorClass.newInstance();
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
                queryProcessorInstance.initialize(unionProperties, configStream);
            } catch (Exception ex) {
                throw new QueryProcessingException("Error initializing query processor: " + ex.getMessage(), ex);
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
        if (cqlQueryProcessorConfig == null) {
            LOG.debug("Loading CQL query processor configuration properties");
            try {
                cqlQueryProcessorConfig = ServiceConfigUtil.getQueryProcessorConfigurationParameters();
            } catch (Exception ex) {
                throw new QueryProcessingException(
                    "Error getting query processor configuration parameters: " + ex.getMessage(), ex);
            }
        }
        // clone the query processor config instance 
        // (in case they get modified by the Query Processor implementation)
        Properties clone = new Properties();
        Enumeration<?> keyEnumeration = cqlQueryProcessorConfig.keys();
        while (keyEnumeration.hasMoreElements()) {
            String key = (String) keyEnumeration.nextElement();
            String value = cqlQueryProcessorConfig.getProperty(key);
            clone.setProperty(key, value);
        }
        return clone;
    }
}
