package gov.nih.nci.cagrid.data.service;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.ServiceParametersConstants;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;

/** 
 *  ServiceConfigUtil
 *  Utility to load a service configuration from JNDI and
 *  walk through it with reflection to locate service property
 *  values used to configure the data service infrastructure
 *  and query processors
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jun 16, 2006 
 * @version $Id$ 
 */
public class ServiceConfigUtil {
    
    public static final String DATA_GETTER_PREFIX = "get" 
        + Character.toUpperCase(ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX.charAt(0)) 
        + ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX.substring(1);    
    public static final String CQL_GETTER_PREFIX = "get" 
        + Character.toUpperCase(QueryProcessorConstants.QUERY_PROCESSOR_CONFIG_PREFIX.charAt(0)) 
        + QueryProcessorConstants.QUERY_PROCESSOR_CONFIG_PREFIX.substring(1);
    public static final String CQL2_GETTER_PREFIX = "get"
        + Character.toUpperCase(QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CONFIG_PREFIX.charAt(0))
        + QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CONFIG_PREFIX.substring(1);

	public static Properties getQueryProcessorConfigurationParameters() throws Exception {
		Properties configParams = null;
		try {
		    configParams = getPropertiesByPrefix(CQL_GETTER_PREFIX);
		} catch (Exception e) {
			throw new Exception("Unable to convert service config to map: " 
			    + e.getMessage(), e);
		}
		return configParams;
	}
	
	
	public static Properties getCql2QueryProcessorConfigurationParameters() throws Exception {
	    Properties configParams = null;
	    try {
            configParams = getPropertiesByPrefix(CQL2_GETTER_PREFIX);
        } catch (Exception e) {
            throw new Exception("Unable to convert service config to map: " 
                + e.getMessage(), e);
        }
        return configParams;
	}
	
	
	public static String getCqlQueryProcessorClassName() throws Exception {
	    String className = null;
	    try {
	        className = getConfigProperty(QueryProcessorConstants.QUERY_PROCESSOR_CLASS_PROPERTY);
	    } catch (Exception ex) {
		    throw new Exception("Unable to extract query processor class name from config: " 
		        + ex.getMessage(), ex);
		}
		return className;
	}
	
	
	public static String getCql2QueryProcessorClassName() throws Exception {
	    String className = null;
        try {
            className = getConfigProperty(QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY);
        } catch (Exception ex) {
            throw new Exception("Unable to extract query processor class name from config: " 
                + ex.getMessage(), ex);
        }
        return className;
	}
	
	
	public static Properties getDataServiceParams() throws Exception {
		Properties props = null;
		try {
		    Properties nonPrefixed = getPropertiesByPrefix(DATA_GETTER_PREFIX);
		    // put the data prefix back on each property
		    props = new Properties();
		    for (Object key : nonPrefixed.keySet()) {
		        String name = (String) key;
		        String value = nonPrefixed.getProperty(name);
		        name = ServiceParametersConstants.DATA_SERVICE_PARAMS_PREFIX + name;
		        props.setProperty(name, value);
		    }
		} catch (Exception ex) {
			throw new Exception("Unable to extract data service config parameters: " + ex.getMessage(), ex);
		}
		return props;
	}
	
	
	public static String getClassToQnameMappingsFile() throws Exception {
	    String value = null;
	    try {
	        value = getConfigProperty(ServiceParametersConstants.CLASS_MAPPINGS_FILENAME);
	    } catch (Exception ex) {
			throw new Exception("Unable to get class mappings filename: " + ex.getMessage(), ex);
		}
	    return value;
	}
	
	
	public static String getConfigProperty(String propertyName) throws Exception {
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) 
            + propertyName.substring(1);
        try {
            Object serviceConfig = getServiceConfigObject();
            Class<?> configClass = serviceConfig.getClass();
            Method getter = configClass.getMethod(getterName, new Class[] {});
            return (String) getter.invoke(serviceConfig, new Object[] {});
        } catch (Exception ex) {
            throw new Exception("Unable to resolve property " + propertyName 
                + ": " + ex.getMessage(), ex);
        }
    }
	
	
	public static boolean hasConfigProperty(String propertyName) throws Exception {
	    String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) 
	        + propertyName.substring(1);
	    Object serviceConfig = getServiceConfigObject();
	    Class<?> configClass = serviceConfig.getClass();
	    Method getter = configClass.getMethod(getterName, new Class[] {});
	    return getter != null;
	}
	
	
	private static Properties getPropertiesByPrefix(String prefix) throws Exception {
        Properties props = new Properties();
        Object serviceConfig = getServiceConfigObject();
        Class<?> configClass = serviceConfig.getClass();
        Method[] configMethods = configClass.getMethods();
        for (int i = 0; i < configMethods.length; i++) {
            Method current = configMethods[i];
            if (current.getName().startsWith(prefix) 
                && current.getReturnType().equals(String.class)
                && Modifier.isPublic(current.getModifiers())) {
                String value = (String) current.invoke(serviceConfig, new Object[] {});
                String key = current.getName().substring(prefix.length());
                props.setProperty(key, value);
            }
        }
        return props;
    }
	
	
	private static Object getServiceConfigObject() throws NamingException {
		MessageContext context = MessageContext.getCurrentContext();
		String servicePath = context.getTargetService();
		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		javax.naming.Context initialContext = new InitialContext();
		Object serviceConfig = initialContext.lookup(jndiName);
		return serviceConfig;
	}
}
