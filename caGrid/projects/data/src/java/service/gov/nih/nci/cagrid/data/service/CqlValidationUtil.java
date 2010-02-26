package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.ValidatorConstants;
import gov.nih.nci.cagrid.data.cql.validation.CqlDomainValidator;
import gov.nih.nci.cagrid.data.cql.validation.CqlStructureValidator;
import gov.nih.nci.cagrid.data.cql.validation.DomainConformanceException;
import gov.nih.nci.cagrid.data.cql.validation.MalformedStructureException;
import gov.nih.nci.cagrid.data.cql2.validation.walker.BaseCustomCql2WalkerHandler;
import gov.nih.nci.cagrid.data.cql2.validation.walker.Cql2Walker;
import gov.nih.nci.cagrid.data.cql2.validation.walker.Cql2WalkerDomainModelValidationHandler;
import gov.nih.nci.cagrid.data.cql2.validation.walker.Cql2WalkerExtensionCompatibilityValidationHandler;
import gov.nih.nci.cagrid.data.cql2.validation.walker.Cql2WalkerHandler;
import gov.nih.nci.cagrid.data.cql2.validation.walker.Cql2WalkerStructureValidationHandler;
import gov.nih.nci.cagrid.data.cql2.validation.walker.ExtensionValidationException;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CqlValidationUtil {
    
    private static Log LOG = LogFactory.getLog(CqlValidationUtil.class);
    
    private Properties dataServiceConfiguration = null;
    private DomainModel domainModel = null;
    private CqlStructureValidator cql1StructureValidator = null;
    private CqlDomainValidator cql1DomainValidator = null;
    private Cql2Walker cql2Walker = null;
        
    CqlValidationUtil(Properties dataServiceConfiguration, DomainModel domainModel) throws DataServiceInitializationException {
        this.dataServiceConfiguration = dataServiceConfiguration;
        this.domainModel = domainModel;
        this.cql2Walker = new Cql2Walker();
        // initialize the CQL 2 walker handlers
        initializeCql2WalkerHandlers();
    }
    
    
    private void initializeCql2WalkerHandlers() throws DataServiceInitializationException {
        if (shouldValidateCqlStructure()) {
            this.cql2Walker.addListener(new Cql2WalkerStructureValidationHandler());
        }
        if (shouldValidateDomainModel()) {
            this.cql2Walker.addListener(new Cql2WalkerDomainModelValidationHandler(domainModel));
        }
        // temporary empty extensions compatibility handler
        Collection<QName> empty = Collections.emptyList();
        this.cql2Walker.addListener(new Cql2WalkerExtensionCompatibilityValidationHandler(empty));
        // load up any custom CQL 2 validators
        List<BaseCustomCql2WalkerHandler> customValidators = getCustomCql2WalkerHandlers();
        for (BaseCustomCql2WalkerHandler handler : customValidators) {
            this.cql2Walker.addListener(handler);
        }
    }
    
    
    void setSupportedExtensions(Collection<QName> supported) {
        Cql2WalkerHandler[] handlers = this.cql2Walker.getListeners();
        for (Cql2WalkerHandler handler : handlers) {
            if (handler instanceof Cql2WalkerExtensionCompatibilityValidationHandler) {
                this.cql2Walker.removeListener(handler);
            }
        }
        cql2Walker.addListener(new Cql2WalkerExtensionCompatibilityValidationHandler(supported));
    }
    
    
    public void validateCql1Query(CQLQuery query) throws QueryProcessingException, MalformedQueryException {
        if (shouldValidateCqlStructure()) {
            getCqlStructureValidator().validateCqlStructure(query);
        }
        if (shouldValidateDomainModel()) {
            getCqlDomainValidator().validateDomainModel(query, domainModel);
        }
    }
    
    
    public void validateCql2Query(org.cagrid.cql2.CQLQuery query) 
        throws DomainConformanceException, MalformedStructureException, ExtensionValidationException, Exception {
        try {
            cql2Walker.walkCql(query);
        } catch (Exception ex) {
            if (ex instanceof DomainConformanceException) {
                throw (DomainConformanceException) ex;
            } else if (ex instanceof MalformedStructureException) {
                throw (MalformedStructureException) ex;
            } else if (ex instanceof ExtensionValidationException) {
                throw (ExtensionValidationException) ex;
            }
            // unknown exception
            throw ex;
        }
    }    
    
    
    protected boolean shouldValidateCqlStructure() {
        return dataServiceConfiguration.getProperty(ValidatorConstants.VALIDATE_CQL_FLAG) != null 
            && Boolean.valueOf(dataServiceConfiguration.getProperty(
                DataServiceConstants.VALIDATE_CQL_FLAG)).booleanValue();
    }
    
    
    protected boolean shouldValidateDomainModel() {
        return dataServiceConfiguration.getProperty(ValidatorConstants.VALIDATE_DOMAIN_MODEL_FLAG) != null
            && Boolean.valueOf(dataServiceConfiguration.getProperty(
                DataServiceConstants.VALIDATE_DOMAIN_MODEL_FLAG)).booleanValue();
    }
        
    
    private CqlStructureValidator getCqlStructureValidator() throws QueryProcessingException {
        if (cql1StructureValidator == null) {
            try {
                String validatorClassName = dataServiceConfiguration.getProperty(ValidatorConstants.CQL_VALIDATOR_CLASS);
                LOG.debug("Loading CQL structure validator class " + validatorClassName);
                Class<?> validatorClass = Class.forName(validatorClassName);
                cql1StructureValidator = (CqlStructureValidator) validatorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting CQL structure validator: " + ex.getMessage(), ex);
            }
        }
        return cql1StructureValidator;
    }
    
    
    private CqlDomainValidator getCqlDomainValidator() throws QueryProcessingException {
        if (cql1DomainValidator == null) {
            try {
                String validatorClassName = dataServiceConfiguration.getProperty(ValidatorConstants.DOMAIN_MODEL_VALIDATOR_CLASS);
                LOG.debug("Loading CQL Domain Model validator class " + validatorClassName);
                Class<?> validatorClass = Class.forName(validatorClassName);
                cql1DomainValidator = (CqlDomainValidator) validatorClass.newInstance();
            } catch (Exception ex) {
                throw new QueryProcessingException("Error getting CQL domain validator: " + ex.getMessage(), ex);
            }
        }
        return cql1DomainValidator;
    }
    
    
    private List<BaseCustomCql2WalkerHandler> getCustomCql2WalkerHandlers() throws DataServiceInitializationException {
        List<BaseCustomCql2WalkerHandler> handlers = new LinkedList<BaseCustomCql2WalkerHandler>();
        String classNames = dataServiceConfiguration.getProperty(ValidatorConstants.CQL2_VALIDATOR_CLASSES);
        if (classNames != null && classNames.length() != 0) {
            LOG.debug("Custom CQL 2 validators: " + classNames);
            StringTokenizer tokenizer = new StringTokenizer(classNames, ",");
            while (tokenizer.hasMoreTokens()) {
                String className = tokenizer.nextToken();
                LOG.debug("Trying to load " + className);
                try {
                    Class<?> clazz = Class.forName(className);
                    LOG.debug("Class loaded, verifying hierarchy");
                    if (!BaseCustomCql2WalkerHandler.class.isAssignableFrom(clazz)) {
                        throw new DataServiceInitializationException(
                            clazz.getName() + " is not a subclass of " + 
                            BaseCustomCql2WalkerHandler.class.getName());
                    }
                    LOG.debug("Getting constructor");
                    Constructor<?> constructor = clazz.getConstructor(DomainModel.class);
                    LOG.debug("Invoking constructor");
                    BaseCustomCql2WalkerHandler handlerInstance = 
                        (BaseCustomCql2WalkerHandler) constructor.newInstance(domainModel);
                    LOG.debug("Custom CQL 2 validator " + className + " instantiated");
                    handlers.add(handlerInstance);
                } catch (Exception ex) {
                    String message = "Could not load custom CQL 2 validator: " + ex.getMessage();
                    LOG.error(message, ex);
                    throw new DataServiceInitializationException(message, ex);
                }
            }
        } else {
            LOG.debug("No Custom CQL 2 validators found");
        }
        return handlers;
    }
}
