package org.cagrid.data.sdkquery42.style.helpers;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery42.encoding.PojoProxyHelperImpl;
import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;


/**
 * SDK42PostCodegenHelper 
 * Edits castor mapping files to prevent it from
 * following associations when serializing and deserializing SDK beans
 * 
 * @author David Ervin
 * 
 * @created Jan 8, 2008 12:54:15 PM
 * @version $Id: SDK41PostCodegenHelper.java,v 1.1 2008/04/17 15:25:51 dervin
 *          Exp $
 */
public class PostCodegenHelper extends gov.nih.nci.cagrid.data.style.sdkstyle.helpers.PostCodegenHelper {

    // need to edit the config to use my proxy helper implementation
    public static final String REMOTE_CONFIG_FILENAME = "application-config-client-info.xml";
    public static final String LOCAL_CONFIG_FILENAME = "application-config-client.xml";
    
    // SDK provided proxy helper
    public static final String SDK_PROXY_HELPER = "gov.nih.nci.system.client.proxy.ProxyHelperImpl";
    
    // my proxy helper
    public static final String POJO_PROXY_HELPER = PojoProxyHelperImpl.class.getName();

    private static final Log LOG = LogFactory.getLog(PostCodegenHelper.class);


    public void codegenPostProcessStyle(ServiceExtensionDescriptionType desc, ServiceInformation info) throws Exception {
        super.codegenPostProcessStyle(desc, info);
        editApplicationSpringConfigFile(info);
    }


    private void editApplicationSpringConfigFile(ServiceInformation info) throws Exception {
        LOG.debug("Locating config jar");
        String applicationName = CommonTools.getServicePropertyValue(info.getServiceDescriptor(),
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + SDK42QueryProcessor.PROPERTY_APPLICATION_NAME);
        boolean isLocal = false;
        String isLocalValue = CommonTools.getServicePropertyValue(info.getServiceDescriptor(),
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + SDK42QueryProcessor.PROPERTY_USE_LOCAL_API);
        isLocal = Boolean.parseBoolean(isLocalValue);
        String jarFilename = applicationName + "-config.jar";
        File configJar = new File(info.getBaseDirectory(), "lib" + File.separator + jarFilename);
        LOG.debug("Config jar found to be " + configJar.getAbsolutePath());
        
        // extract the configuration
        String configFilename = isLocal ? LOCAL_CONFIG_FILENAME : REMOTE_CONFIG_FILENAME;
        StringBuffer configContents = JarUtilities.getFileContents(new JarFile(configJar), configFilename);

        // replace the default bean proxy class with mine
        LOG.debug("Replacing references to bean proxy class");
        int start = -1;
        while ((start = configContents.indexOf(SDK_PROXY_HELPER)) != -1) {
            configContents.replace(start, start + SDK_PROXY_HELPER.length(), POJO_PROXY_HELPER);
        }

        // add the edited config to the config jar file
        LOG.debug("Inserting edited config in jar");
        byte[] configData = configContents.toString().getBytes();
        JarUtilities.insertEntry(configJar, configFilename, configData);
    }
}
