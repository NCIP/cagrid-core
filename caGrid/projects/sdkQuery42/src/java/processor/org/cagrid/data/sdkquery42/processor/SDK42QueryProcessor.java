package org.cagrid.data.sdkquery42.processor;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.security.SecurityManager;

public class SDK42QueryProcessor extends CQLQueryProcessor {
    
    private static Log LOG = LogFactory.getLog(SDK42QueryProcessor.class);
    
    // general configuration options
    public static final String PROPERTY_APPLICATION_NAME = "applicationName";
    public static final String PROPERTY_USE_LOCAL_API = "useLocalApiFlag";
    
    // remote service configuration properties
    public static final String PROPERTY_HOST_NAME = "applicationHostName";
    public static final String PROPERTY_HOST_PORT = "applicationHostPort";
    public static final String PROPERTY_HOST_HTTPS = "useHttpsUrl";
    
    // security configuration properties
    public static final String PROPERTY_USE_GRID_IDENTITY_LOGIN = "useGridIdentityLogin";
    public static final String PROPERTY_USE_STATIC_LOGIN = "useStaticLogin";
    public static final String PROPERTY_STATIC_LOGIN_USER = "staticLoginUser";
    public static final String PROPERTY_STATIC_LOGIN_PASS = "staticLoginPass";
    
    // default values for properties
    public static final String DEFAULT_USE_LOCAL_API = String.valueOf(false);
    public static final String DEFAULT_HOST_HTTPS = String.valueOf(false);
    public static final String DEFAULT_USE_GRID_IDENTITY_LOGIN = String.valueOf(false);
    public static final String DEFAULT_USE_STATIC_LOGIN = String.valueOf(false);
    
    // the "empty" password passed to the SDK when using CSM / grid identity login
    public static final String EMPTY_PASSWORD = "EMPTYPASSWORD";
    
    public SDK42QueryProcessor() {
        super();
    }
    
    
    public Properties getRequiredParameters() {
        Properties props = super.getRequiredParameters();
        props.setProperty(PROPERTY_APPLICATION_NAME, "");
        props.setProperty(PROPERTY_USE_LOCAL_API, DEFAULT_USE_LOCAL_API);
        props.setProperty(PROPERTY_HOST_NAME, "");
        props.setProperty(PROPERTY_HOST_PORT, "");
        props.setProperty(PROPERTY_HOST_HTTPS, DEFAULT_HOST_HTTPS);
        props.setProperty(PROPERTY_USE_GRID_IDENTITY_LOGIN, DEFAULT_USE_GRID_IDENTITY_LOGIN);
        props.setProperty(PROPERTY_USE_STATIC_LOGIN, DEFAULT_USE_STATIC_LOGIN);
        props.setProperty(PROPERTY_STATIC_LOGIN_USER, "");
        props.setProperty(PROPERTY_STATIC_LOGIN_PASS, "");
        return props;
    }
    
    
    public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
        super.initialize(parameters, wsdd);
        // verify that if we're using grid identity login, we're also using the Local API
        
    }
    

    public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    private boolean isUseLocalApi() {
        boolean useLocal = Boolean.parseBoolean(DEFAULT_USE_LOCAL_API);
        String useLocalApiValue = getConfiguredParameters().getProperty(PROPERTY_USE_LOCAL_API);
        try {
            useLocal = Boolean.parseBoolean(useLocalApiValue);
        } catch (Exception ex) {
            LOG.error("Error parsing property " + PROPERTY_USE_LOCAL_API 
                + ".  Value was " + useLocalApiValue, ex);
        }
        return useLocal;
    }
    
    
    private boolean isUseGridIdentLogin() {
        boolean useGridIdent = Boolean.parseBoolean(DEFAULT_USE_GRID_IDENTITY_LOGIN);
        String useGridIdentValue = getConfiguredParameters().getProperty(PROPERTY_USE_GRID_IDENTITY_LOGIN);
        try {
            useGridIdent = Boolean.parseBoolean(useGridIdentValue);
        } catch (Exception ex) {
            LOG.error("Error parsing property " + PROPERTY_USE_GRID_IDENTITY_LOGIN
                + ".  Value was " + useGridIdentValue, ex);
        }
        return useGridIdent;
    }
    
    
    private boolean isUseStaticLogin() {
        boolean useStatic = false;
        String useStaticValue = getConfiguredParameters().getProperty(PROPERTY_USE_STATIC_LOGIN);
        try {
            useStatic = Boolean.parseBoolean(useStaticValue);
        } catch (Exception ex) {
            LOG.error("Error parsing property " + PROPERTY_USE_STATIC_LOGIN
                + ".  Value was " + useStaticValue, ex);
        }
        return useStatic;
    }
    
    
    private boolean isUseHttps() {
        boolean useHttps = Boolean.parseBoolean(DEFAULT_HOST_HTTPS);
        String useHttpsValue = getConfiguredParameters().getProperty(PROPERTY_HOST_HTTPS);
        try {
            useHttps = Boolean.parseBoolean(PROPERTY_HOST_HTTPS);
        } catch (Exception ex) {
            LOG.error("Error parsing property " + PROPERTY_HOST_HTTPS
                + ".  Value was " + useHttpsValue, ex);
        }
        return useHttps;
    }
    
    
    private String getStaticLoginUser() {
        return getConfiguredParameters().getProperty(PROPERTY_STATIC_LOGIN_USER);
    }
    
    
    private String getStaticLoginPass() {
        return getConfiguredParameters().getProperty(PROPERTY_STATIC_LOGIN_PASS);
    }
    
    
    private String getRemoteApplicationUrl() {
        StringBuffer url = new StringBuffer();
        if (isUseHttps()) {
            url.append("https://");
        } else {
            url.append("http://");
        }
        url.append(getConfiguredParameters().getProperty(PROPERTY_HOST_NAME));
        url.append(":");
        url.append(getConfiguredParameters().getProperty(PROPERTY_HOST_PORT));
        url.append("/");
        url.append(getConfiguredParameters().getProperty(PROPERTY_APPLICATION_NAME));
        String completedUrl = url.toString();
        LOG.debug("Application Service remote URL determined to be: " + completedUrl);
        return completedUrl;
    }
    
    
    private ApplicationService getApplicationService() throws QueryProcessingException {
        ApplicationService service = null;
        try {
            if (isUseLocalApi()) {
                if (isUseGridIdentLogin()) {
                    SecurityManager securityManager = SecurityManager.getManager();
                    String username = securityManager.getCaller();
                    service = ApplicationServiceProvider.getApplicationService(username, EMPTY_PASSWORD);
                } else {
                    service = ApplicationServiceProvider.getApplicationService();
                }
            } else {
                String url = getRemoteApplicationUrl();
                if (isUseStaticLogin()) {
                    String username = getStaticLoginUser();
                    String password = getStaticLoginPass();
                    service = ApplicationServiceProvider.getApplicationServiceFromUrl(url, username, password);
                } else {
                    service = ApplicationServiceProvider.getApplicationServiceFromUrl(url);
                }
            }
        } catch (Exception ex) {
            throw new QueryProcessingException("Error obtaining application service: " + ex.getMessage(), ex);
        }
        return service;
    }
}
