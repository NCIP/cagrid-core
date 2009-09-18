package gov.nih.nci.cagrid.sdkquery4.style.helper;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.style.StyleCodegenPreProcessor;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.DomainTypesInformationUtil;
import gov.nih.nci.cagrid.sdkquery4.processor.SDK4QueryProcessor;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryEvent;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryEventListener;
import gov.nih.nci.cagrid.sdkquery4.style.beanmap.BeanTypeDiscoveryMapper;
import gov.nih.nci.cagrid.sdkquery4.style.common.SDK4StyleConstants;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 *  SDK4PreCodegenHelper
 *  Helper for the caCORE SDK 4 Data Service style -- pre codegen
 * 
 * @author David Ervin
 * 
 * @created Jan 18, 2008 9:56:27 AM
 * @version $Id: SDK4PreCodegenHelper.java,v 1.4 2008-04-23 14:25:51 dervin Exp $ 
 */
public class SDK4PreCodegenHelper implements StyleCodegenPreProcessor {
    
    public static final String DOMAIN_TYPES_INFO_FILE_SUFFIX = "domainTypesInformation.xml";
    
    private static final Log LOG = LogFactory.getLog(SDK4PreCodegenHelper.class);
    

    public void codegenPreProcessStyle(ServiceExtensionDescriptionType desc, ServiceInformation info) throws Exception {
        generateDomainTypesInformation(info);
    }
    
    
    private void generateDomainTypesInformation(ServiceInformation info) throws Exception {
        ExtensionDataManager manager = getExtensionDataManager(info);
        
        if (!manager.isNoDomainModel()) {
            // get the beans jar
            String beansJarFilename = CommonTools.getServicePropertyValue(
                info.getServiceDescriptor(), SDK4StyleConstants.BEANS_JAR_FILENAME);
            File beansJar = new File(info.getBaseDirectory(), "lib" + File.separator + beansJarFilename);

            // get the domain model
            String domainModelFilename = null;
            ResourcePropertyType[] metadata = info.getServices().getService(0)
            .getResourcePropertiesList().getResourceProperty();
            for (ResourcePropertyType rp : metadata) {
                if (rp.getQName().equals(DataServiceConstants.DOMAIN_MODEL_QNAME)) {
                    domainModelFilename = rp.getFileLocation();
                    break;
                }
            }

            FileReader reader = new FileReader(new File(info.getBaseDirectory(), "etc" + File.separator + domainModelFilename));
            DomainModel domainModel = (DomainModel) Utils.deserializeObject(reader, DomainModel.class);

            // create the mapper
            BeanTypeDiscoveryMapper mapper = new BeanTypeDiscoveryMapper(beansJar, domainModel);
            mapper.addBeanTypeDiscoveryEventListener(new BeanTypeDiscoveryEventListener() {
                public void typeDiscoveryBegins(BeanTypeDiscoveryEvent e) {
                    String message = "Mapping class " + e.getBeanClassname() 
                    + " (" + (e.getCurrentBean() + 1) + " of " + e.getTotalBeans() + ")";
                    LOG.debug(message);
                }
            });

            // discover types information
            DomainTypesInformation typesInfo = mapper.discoverTypesInformation();

            // serialize the types info
            String applicationName = CommonTools.getServicePropertyValue(info.getServiceDescriptor(),
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + SDK4QueryProcessor.PROPERTY_APPLICATION_NAME);
            File typesInfoFile = new File(info.getBaseDirectory(), 
                "etc" + File.separator + applicationName + "-" + DOMAIN_TYPES_INFO_FILE_SUFFIX);
            FileWriter writer = new FileWriter(typesInfoFile);
            DomainTypesInformationUtil.serializeDomainTypesInformation(typesInfo, writer);
            writer.flush();
            writer.close();
            // set the query processor's config property
            CommonTools.setServiceProperty(info.getServiceDescriptor(),
                DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX 
                + SDK4QueryProcessor.PROPERTY_DOMAIN_TYPES_INFO_FILENAME,
                typesInfoFile.getName(), true);
        } else {
            LOG.warn("NO DOMAIN MODEL SPECIFIED: This will prevent the query processor " +
                    "from initializing correctly when the service container is started!!!");
        }
    }
    
    
    private ExtensionDataManager getExtensionDataManager(ServiceInformation serviceInfo) throws Exception {
        for (ExtensionType ext : serviceInfo.getExtensions().getExtension()) {
            if (ext.getName().equals("data")) {
                ExtensionTypeExtensionData extData = ext.getExtensionData();
                return new ExtensionDataManager(extData);
            }
        }
        return null;
    }
}
