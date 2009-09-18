package gov.nih.nci.cagrid.sdkquery4.processor2;

import gov.nih.nci.cagrid.cql2.components.CQLQuery;
import gov.nih.nci.cagrid.cql2.results.CQLAggregateResult;
import gov.nih.nci.cagrid.cql2.results.CQLAttributeResult;
import gov.nih.nci.cagrid.cql2.results.CQLObjectResult;
import gov.nih.nci.cagrid.cql2.results.CQLQueryResults;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql2.CQL2QueryProcessor;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import org.apache.axis.message.MessageElement;
import org.cagrid.cacore.sdk4x.cql2.processor.CQL2ToParameterizedHQL;
import org.cagrid.cacore.sdk4x.cql2.processor.ParameterizedHqlQuery;
import org.cagrid.cacore.sdk4x.cql2.processor.QueryModifierProcessor;
import org.globus.wsrf.Resource;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.security.SecurityManager;

public class SDK4CQL2QueryProcessor extends CQL2QueryProcessor {
    // configuration property keys
    public static final String PROPERTY_APPLICATION_NAME = "applicationName";
    public static final String PROPERTY_USE_LOCAL_API = "useLocalApiFlag";
    public static final String PROPERTY_ORM_JAR_NAME = "ormJarName"; // only for local
    public static final String PROPERTY_HOST_NAME = "applicationHostName"; // only for remote
    public static final String PROPERTY_HOST_PORT = "applicationHostPort"; // only for remote
    public static final String PROPERTY_CASE_INSENSITIVE_QUERYING = "queryCaseInsensitive";
    public static final String PROPERTY_USE_LOGIN = "useServiceLogin";
    public static final String PROPERTY_USE_GRID_IDENTITY_LOGIN = "useGridIdentityLogin";
    public static final String PROPERTY_STATIC_LOGIN_USERNAME = "staticLoginUsername";
    public static final String PROPERTY_STATIC_LOGIN_PASSWORD = "staticLoginPassword";
    
    // default values for properties
    public static final String DEFAULT_USE_LOCAL_API = String.valueOf(false);
    public static final String DEFAULT_CASE_INSENSITIVE_QUERYING = String.valueOf(false);
    public static final String DEFAULT_USE_LOGIN = String.valueOf(false);
    public static final String DEFAULT_USE_GRID_IDENTITY_LOGIN = String.valueOf(false);
    
    
    private QNameResolver qnameResolver = null;
    private CQL2ToParameterizedHQL cqlTranslator = null;

    public SDK4CQL2QueryProcessor() {
        // TODO Auto-generated constructor stub
    }
    
    
    public Properties getRequiredParameters() {
        Properties props = new Properties();
        props.setProperty(PROPERTY_APPLICATION_NAME, "");
        props.setProperty(PROPERTY_HOST_NAME, "");
        props.setProperty(PROPERTY_HOST_PORT, "");
        props.setProperty(PROPERTY_ORM_JAR_NAME, "");
        props.setProperty(PROPERTY_USE_LOCAL_API, DEFAULT_USE_LOCAL_API);
        props.setProperty(PROPERTY_CASE_INSENSITIVE_QUERYING, DEFAULT_CASE_INSENSITIVE_QUERYING);
        props.setProperty(PROPERTY_USE_LOGIN, DEFAULT_USE_LOGIN);
        props.setProperty(PROPERTY_USE_GRID_IDENTITY_LOGIN, DEFAULT_USE_GRID_IDENTITY_LOGIN);
        props.setProperty(PROPERTY_STATIC_LOGIN_USERNAME, "");
        props.setProperty(PROPERTY_STATIC_LOGIN_PASSWORD, "");
        return props;
    }

    
    public CQLQueryResults processQuery(CQLQuery query) throws QueryProcessingException {
        // get an instance of the caCORE SDK ApplicationService
        ApplicationService sdkService = getApplicationService();
        
        // empty results object
        CQLQueryResults queryResults = new CQLQueryResults();
        queryResults.setTargetClassname(query.getCQLTargetObject().getClassName());
        
        // convert the CQL to HQL
        ParameterizedHqlQuery hql = null;
        try {
            hql = getCqlTranslator().convertToHql(query);
        } catch (QueryProcessingException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new QueryProcessingException("Error processing query: " + ex.getMessage(), ex);
        }
        
        HQLCriteria criteria = new HQLCriteria(hql.getHql(), hql.getParameters());
        
        // query the SDK
        List<Object> rawResults = null;
        try {
            rawResults = sdkService.query(criteria);
        } catch (Exception ex) {
            throw new QueryProcessingException("Error querying caCORE service: " + ex.getMessage(), ex);
        }
        
        // see if there is further processing to be done
        if (query.getCQLQueryModifier() != null) {
            Object moddedResults = QueryModifierProcessor.applyQueryModifiers(
                query.getCQLTargetObject().getClassName(), rawResults, query.getCQLQueryModifier());
            // either aggregate results or attribute results
            if (moddedResults instanceof CQLAggregateResult) {
                queryResults.setAggregationResult((CQLAggregateResult) moddedResults);
            } else {
                queryResults.setAttributeResult((CQLAttributeResult[]) moddedResults);
            }
        } else {
            QName targetQName = null;
            try {
                targetQName = getQNameResolver().getQName(query.getCQLTargetObject().getClassName());
            } catch (Exception ex) {
                throw new QueryProcessingException("Error obtaining QName for target data type: " + ex.getMessage(), ex);
            }
            CQLObjectResult[] objectResults = createObjectResults(rawResults, targetQName);
            queryResults.setObjectResult(objectResults);
        }
        
        return queryResults;
    }
    
    
    private CQL2ToParameterizedHQL getCqlTranslator() throws Exception {
        if (cqlTranslator == null) {
            cqlTranslator = new CQL2ToParameterizedHQL(
                getDomainModel(), useCaseInsensitiveQueries());
        }
        return cqlTranslator;
    }
    
    
    private CQLObjectResult[] createObjectResults(List<Object> rawObjects, QName targetQName) throws QueryProcessingException {
        CQLObjectResult[] objectResults = new CQLObjectResult[rawObjects.size()];
        try {
            for (int i = 0; i < rawObjects.size(); i++) {
                MessageElement elem = null;
                if (targetQName != null) {
                    elem = new MessageElement(targetQName, rawObjects.get(i));
                } else {
                    elem = new MessageElement();
                    elem.setObjectValue(rawObjects.get(i));
                }
                objectResults[i] = new CQLObjectResult(new MessageElement[] {elem});
            }
        } catch (SOAPException ex) {
            throw new QueryProcessingException("Error creating object message elements: " + ex.getMessage(), ex);
        }
        return objectResults;
    }
    
    
    private ApplicationService getApplicationService() throws QueryProcessingException {
        ApplicationService service = null;
        
        boolean useLocal = useLocalApplicationService();
        boolean useLogin = useServiceLogin();
        boolean useStaticLogin = useStaticLogin();
        try {
            String username = null;
            String passwd = null;
            if (useLogin) {
                if (useStaticLogin) {
                    username = getConfiguredParameters().getProperty(PROPERTY_STATIC_LOGIN_USERNAME);
                    passwd = username = getConfiguredParameters().getProperty(PROPERTY_STATIC_LOGIN_PASSWORD);
                } else {
                    SecurityManager securityManager = SecurityManager.getManager();
                    username = securityManager.getCaller();
                    passwd = ""; // empty string so the application service doesn't NPE
                }
            }
            
            if (useLocal) {
                if (useLogin) {
                    service = ApplicationServiceProvider.getApplicationService(username, passwd);
                } else {
                    service = ApplicationServiceProvider.getApplicationService();   
                }
            } else {
                String url = getRemoteApplicationUrl();
                if (useLogin) {
                    service = ApplicationServiceProvider.getApplicationServiceFromUrl(url, username, passwd);
                } else {
                    service = ApplicationServiceProvider.getApplicationServiceFromUrl(url);   
                }
            }
        } catch (Exception ex) {
            throw new QueryProcessingException("Error obtaining application service instance: " + ex.getMessage(), ex);
        }
        
        return service;
    }
    
    
    private QNameResolver getQNameResolver() throws Exception {
        if (qnameResolver == null) {
            qnameResolver = new MappingFileQNameResolver(getClassToQnameMappings());
        }
        return qnameResolver;
    }
    
    
    private String getRemoteApplicationUrl() {
        String hostname = getConfiguredParameters().getProperty(PROPERTY_HOST_NAME);
        String port = getConfiguredParameters().getProperty(PROPERTY_HOST_PORT);
        while (hostname.endsWith("/")) {
            hostname = hostname.substring(0, hostname.length() - 1);
        }
        String urlPart = hostname + ":" + port;
        urlPart += "/";
        urlPart += getConfiguredParameters().getProperty(PROPERTY_APPLICATION_NAME);
        return urlPart;
    }
    
    
    private boolean useCaseInsensitiveQueries() throws QueryProcessingException {
        String caseInsensitiveValue = getConfiguredParameters().getProperty(PROPERTY_CASE_INSENSITIVE_QUERYING);
        try {
            return Boolean.parseBoolean(caseInsensitiveValue);
        } catch (Exception ex) {
            throw new QueryProcessingException("Error determining case insensitivity: " + ex.getMessage(), ex);
        }
    }
    
    
    private boolean useLocalApplicationService() throws QueryProcessingException {
        String useLocalValue = getConfiguredParameters().getProperty(PROPERTY_USE_LOCAL_API);
        try {
            return Boolean.parseBoolean(useLocalValue);
        } catch (Exception ex) {
            throw new QueryProcessingException("Error determining local application service use: " + ex.getMessage(), ex);
        }
    }
    
    
    private boolean useServiceLogin() throws QueryProcessingException {
        String useLoginValue = getConfiguredParameters().getProperty(PROPERTY_USE_LOGIN);
        try {
            return Boolean.parseBoolean(useLoginValue);
        } catch (Exception ex) {
            throw new QueryProcessingException("Error determining login use flag: " + ex.getMessage(), ex);
        }
    }
    
    
    private boolean useStaticLogin() throws QueryProcessingException {
        String useGridIdentLogin = getConfiguredParameters().getProperty(PROPERTY_USE_GRID_IDENTITY_LOGIN);
        try {
            return !Boolean.parseBoolean(useGridIdentLogin);
        } catch (Exception ex) {
            throw new QueryProcessingException("Error determining use of static login: " + ex.getMessage(), ex);
        }
    }
    
    
    private DomainModel getDomainModel() throws Exception {
        DomainModel domainModel = null;
        Resource serviceBaseResource = ResourceContext.getResourceContext().getResource();
        Method[] resourceMethods = serviceBaseResource.getClass().getMethods();
        for (int i = 0; i < resourceMethods.length; i++) {
            if (resourceMethods[i].getReturnType() != null 
                && resourceMethods[i].getReturnType().equals(DomainModel.class)) {
                domainModel = (DomainModel) resourceMethods[i].invoke(serviceBaseResource, new Object[] {});
                break;
            }
        }
        return domainModel;
    }
}
