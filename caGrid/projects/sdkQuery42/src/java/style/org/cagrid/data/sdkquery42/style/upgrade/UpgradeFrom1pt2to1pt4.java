package org.cagrid.data.sdkquery42.style.upgrade;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleVersionUpgrader;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;
import org.cagrid.data.sdkquery42.processor2.SDK42CQL2QueryProcessor;
import org.jdom.Element;
import org.jdom.JDOMException;

public class UpgradeFrom1pt2to1pt4 implements StyleVersionUpgrader {

    private static Log LOG = LogFactory.getLog(UpgradeFrom1pt2to1pt4.class);

    public void upgradeStyle(ServiceInformation serviceInformation, ExtensionTypeExtensionData extensionData,
        String serviceFromVersion, String serviceToVersion) throws Exception {
        // load the style
        ServiceStyleContainer styleContainer = ServiceStyleLoader.getStyle("caCORE SDK v 4.2");
        
        // update style libraries
        File[] upgradeLibs = styleContainer.getStyleCopyLibs();
        File serviceLibDir = new File(serviceInformation.getBaseDirectory(), "lib");
        List<String> removedLibs = new ArrayList<String>();
        List<String> addedLibs = new ArrayList<String>();
        // copy in new libs, remove old ones
        for (File upgradeLib : upgradeLibs) {
            File oldExactMatch = new File(serviceLibDir, upgradeLib.getName());
            LOG.debug("Looking for old non-caGrid library: " + oldExactMatch.getName());
            if (oldExactMatch.exists()) {
                oldExactMatch.delete();
                removedLibs.add(oldExactMatch.getName());
                LOG.debug("Deleted old library: " + oldExactMatch.getName());
            }
            if (upgradeLib.getName().startsWith("caGrid-")) {
                int versionIndex = upgradeLib.getName().indexOf(StyleUpgradeConstants.LATEST_JAR_SUFFIX);
                File oldCagridMatch = new File(serviceLibDir, 
                    upgradeLib.getName().substring(0, versionIndex) + "-1.2.jar");
                LOG.debug("Looking for old caGrid 1.2 library " + oldCagridMatch.getName());
                if (oldCagridMatch.exists()) {
                    oldCagridMatch.delete();
                    removedLibs.add(oldCagridMatch.getName());
                    LOG.debug("Deleted old library: " + oldCagridMatch.getName());
                }
            }
            File copyLib = new File(serviceLibDir, upgradeLib.getName());
            Utils.copyFile(upgradeLib, copyLib);
            addedLibs.add(copyLib.getName());
            LOG.debug("Copied new library: " + upgradeLib.getName());
        }
        
        // set CQL 2 query processor classname property
        CommonTools.setServiceProperty(serviceInformation.getServiceDescriptor(),
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY, 
            SDK42CQL2QueryProcessor.class.getName(), false);
        
        // add CQL 2 query processor properties
        SDK42CQL2QueryProcessor processor = new SDK42CQL2QueryProcessor();
        Properties processorProperties = processor.getRequiredParameters();
        Set<String> fromEtc = processor.getParametersFromEtc();
        for (Object key : processorProperties.keySet()) {
            String propName = (String) key;
            String def = processorProperties.getProperty(propName);
            CommonTools.setServiceProperty(serviceInformation.getServiceDescriptor(),
                QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CONFIG_PREFIX + propName,
                def, fromEtc.contains(propName));
        }

        // copy values from CQL 1 query processor properties
        SDK42QueryProcessor cql1processor = new SDK42QueryProcessor();
        Properties oldProperties = cql1processor.getRequiredParameters();
        for (Object key : oldProperties.keySet()) {
            String propName = (String) key;
            String oldPrefixedName = QueryProcessorConstants.QUERY_PROCESSOR_CONFIG_PREFIX + propName;
            String newPrefixedName = QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CONFIG_PREFIX + propName;
            if (processorProperties.containsKey(key) &&
                CommonTools.servicePropertyExists(serviceInformation.getServiceDescriptor(), oldPrefixedName)) {
                String copyValue = CommonTools.getServicePropertyValue(
                    serviceInformation.getServiceDescriptor(), oldPrefixedName);
                CommonTools.setServiceProperty(serviceInformation.getServiceDescriptor(), 
                    newPrefixedName, copyValue, fromEtc.contains(propName));
            }            
        }
        
        // edit data service extension data's "additional jars" to point to the ones we added instead of the old ones
        Element data = getExtensionDataElement(extensionData);
        Element libsElement = data.getChild("AdditionalLibraries", data.getNamespace());
        if (libsElement != null) {
            List<?> jarNameElements = libsElement.getChildren("JarName", libsElement.getNamespace());
            if (jarNameElements != null && jarNameElements.size() != 0) {
                Iterator<?> jarNameElemIter = jarNameElements.iterator();
                while (jarNameElemIter.hasNext()) {
                    Element jarNameElement = (Element) jarNameElemIter.next();
                    if (removedLibs.contains(jarNameElement.getText())) {
                        jarNameElemIter.remove();
                    }
                }
            }
            for (String added : addedLibs) {
                Element jarNameElement = new Element("JarName", libsElement.getNamespace());
                jarNameElement.setText(added);
                libsElement.addContent(jarNameElement);
            }
        }
        storeExtensionDataElement(extensionData, data);
    }
    
    
    private Element getExtensionDataElement(ExtensionTypeExtensionData extensionData) throws Exception {
        MessageElement[] anys = extensionData.get_any();
        MessageElement rawDataElement = null;
        for (int i = 0; (anys != null) && (i < anys.length); i++) {
            if (anys[i].getQName().equals(Data.getTypeDesc().getXmlType())) {
                rawDataElement = anys[i];
                break;
            }
        }
        if (rawDataElement == null) {
            throw new Exception("No extension data was found for the data service extension");
        }
        Element extensionDataElement = AxisJdomUtils.fromMessageElement(rawDataElement);
        return extensionDataElement;
    }
    
    
    private void storeExtensionDataElement(ExtensionTypeExtensionData extensionData, Element elem) throws Exception {
        MessageElement[] anys = extensionData.get_any();
        for (int i = 0; (anys != null) && (i < anys.length); i++) {
            if (anys[i].getQName().equals(Data.getTypeDesc().getXmlType())) {
                // remove the old extension data
                anys = (MessageElement[]) Utils.removeFromArray(anys, anys[i]);
                break;
            }
        }
        // create a message element from the JDom element
        MessageElement data = null;
        try {
            data = AxisJdomUtils.fromElement(elem);
        } catch (JDOMException ex) {
            throw new Exception(
                "Error converting extension data to Axis message element: " + ex.getMessage(), ex);
        }
        anys = (MessageElement[]) Utils.appendToArray(anys, data);
        extensionData.set_any(anys);
    }
}
