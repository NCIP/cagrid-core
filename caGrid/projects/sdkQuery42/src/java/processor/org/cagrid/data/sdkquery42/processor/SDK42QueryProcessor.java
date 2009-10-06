package org.cagrid.data.sdkquery42.processor;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;

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
    
    // default values for properties
    public static final String DEFAULT_USE_LOCAL_API = String.valueOf(false);
    public static final String DEFAULT_HOST_HTTPS = String.valueOf(false);
    public static final String DEFAULT_USE_GRID_IDENTITY_LOGIN = String.valueOf(false);
    
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
}
