package gov.nih.nci.cagrid.wsenum.utils;

import gov.nih.nci.cagrid.wsenum.common.WsEnumConstants;

import java.lang.reflect.Method;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.axis.MessageContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.wsrf.Constants;

/**
 * EnumConfigDiscoveryUtil
 * Utility for determining the runtime configuration of the caGrid
 * WS-Enumeration tools; generally based on service properties from JNDI
 *  
 * @author ervin
 */
public class EnumConfigDiscoveryUtil {
    private static Log LOG = LogFactory.getLog(EnumConfigDiscoveryUtil.class);
    
    public static IterImplType getConfiguredIterImplType() {
        IterImplType implType = null;
        try {
            String value = getConfigProperty(WsEnumConstants.ITER_IMPL_TYPE_PROPERTY);
            LOG.debug("IterImplType configured as " + value);
            implType = IterImplType.valueOf(value);
        } catch (Exception ex) {
            String message = "Error determining configured IterImplType; using default";
            System.err.println(message);
            LOG.info(message, ex);
        } finally {
            if (implType == null) {
                // use default
                implType = IterImplType.valueOf(WsEnumConstants.DEFAULT_ITER_IMPL_TYPE);
            }
        }
        return implType;
    }
    
    
    private static String getConfigProperty(String propertyName) throws Exception {
        // generate the name of the getter for this property
        String getterName = "get" + Character.toUpperCase(propertyName.charAt(0)) 
            + propertyName.substring(1);
        // try to find the getter and return it's value
        try {
            Object serviceConfig = getServiceConfigObject();
            Class configClass = serviceConfig.getClass();
            Method getter = configClass.getMethod(getterName, new Class[] {});
            return (String) getter.invoke(serviceConfig, new Object[] {});
        } catch (Exception ex) {
            throw new Exception("Unable to resolve property " + propertyName + ": " + ex.getMessage(), ex);
        }
    }
    
    
    private static Object getServiceConfigObject() throws NamingException {
        // get the current JNDI context
        MessageContext context = MessageContext.getCurrentContext();
        String servicePath = context.getTargetService();
        // build the JNDI service configuration name
        String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
        javax.naming.Context initialContext = new InitialContext();
        // grab the service configuration object
        Object serviceConfig = initialContext.lookup(jndiName);
        return serviceConfig;
    }
    

    private EnumConfigDiscoveryUtil() {
        // prevents instantiation of a static class
    }
}
