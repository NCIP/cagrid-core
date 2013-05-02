/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.data.sdkquery44.style.upgrade;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleVersionUpgrader;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgradeStatus;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery44.encoding.SDK44DeserializerFactory;
import org.cagrid.data.sdkquery44.encoding.SDK44SerializerFactory;
import org.cagrid.data.sdkquery44.processor.cql2.SDK44CQL2QueryProcessor;
import org.jdom.Element;
import org.jdom.JDOMException;

public class UpgradeFrom1pt4to1pt6 implements StyleVersionUpgrader {
    
    private static Log LOG = LogFactory.getLog(UpgradeFrom1pt4to1pt6.class);

    public void upgradeStyle(ServiceInformation serviceInformation, ExtensionTypeExtensionData extensionData,
        ExtensionUpgradeStatus status, String serviceFromVersion, String serviceToVersion) throws Exception {
        // load the style
        ServiceStyleContainer styleContainer = ServiceStyleLoader.getStyle("caCORE SDK v 4.4");
        
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
                String message = "Deleted old library: " + oldExactMatch.getName();
                LOG.debug(message);
                status.addDescriptionLine(message);
            }
            if (upgradeLib.getName().startsWith("caGrid-")) {
                int versionIndex = upgradeLib.getName().indexOf(StyleUpgradeConstants.LATEST_JAR_SUFFIX);
                File oldCagridMatch = new File(serviceLibDir, 
                    upgradeLib.getName().substring(0, versionIndex) + "-1.4.jar");
                LOG.debug("Looking for old caGrid 1.4 library " + oldCagridMatch.getName());
                if (oldCagridMatch.exists()) {
                    oldCagridMatch.delete();
                    removedLibs.add(oldCagridMatch.getName());
                    LOG.debug("Deleted old library: " + oldCagridMatch.getName());
                }
            }
            File copyLib = new File(serviceLibDir, upgradeLib.getName());
            Utils.copyFile(upgradeLib, copyLib);
            addedLibs.add(copyLib.getName());
            String message = "Copied new library: " + upgradeLib.getName();
            LOG.debug(message);
            status.addDescriptionLine(message);
        }
        // the names of the ISO 21090 analytical service extension jars changed from 1.3 to 1.5
        File[] iso21090analyticalLibs = new File("extensions" + File.separator + "lib").listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.startsWith("caGrid-iso21090-analytical") && name.endsWith(".jar");
            }
        });
        for (File isoLib : iso21090analyticalLibs) {
            File copyLib = new File(serviceLibDir, isoLib.getName());
            Utils.copyFile(isoLib, copyLib);
            String message = "Copied ISO 21090 support library " + isoLib.getName();
            LOG.debug(message);
            status.addDescriptionLine(message);
        }
        File[] neededIntroduceLibs = new File("build" + File.separator + "jars").listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return name.startsWith("caGrid-Introduce-core") && name.endsWith(".jar");
            }
        });
        for (File introduceLib : neededIntroduceLibs) {
            File copyLib = new File(serviceLibDir, introduceLib.getName());
            Utils.copyFile(introduceLib, copyLib);
            String message = "Copied Introduce support library " + introduceLib.getName();
            LOG.debug(message);
            status.addDescriptionLine(message);
        }
        
        // set CQL 2 query processor classname property
        CommonTools.setServiceProperty(serviceInformation.getServiceDescriptor(),
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CLASS_PROPERTY, 
            SDK44CQL2QueryProcessor.class.getName(), false);
        status.addDescriptionLine("Set CQL 2 query processor class service property to " 
            + SDK44CQL2QueryProcessor.class.getName());
        
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
        status.addDescriptionLine("Reconfigured searchable jars for query processor discovery panel");
        
        // update the type mappings to use the renamed serializer / deserializer classes
        NamespacesType namespaces = serviceInformation.getNamespaces();
        for (NamespaceType ns : namespaces.getNamespace()) {
            boolean serializerChanged = false;
            if (ns.getSchemaElement() != null) {
                for (SchemaElementType type : ns.getSchemaElement()) {
                    String deserializer = type.getDeserializer();
                    String serializer = type.getSerializer();
                    if (StyleUpgradeConstants.OLD_DESERIALIZER_FACTORY_NAME.equals(deserializer)) {
                        type.setDeserializer(SDK44DeserializerFactory.class.getName());
                        serializerChanged = true;
                    }
                    if (StyleUpgradeConstants.OLD_SERIALIZER_FACTORY_NAME.equals(serializer)) {
                        type.setSerializer(SDK44SerializerFactory.class.getName());
                        serializerChanged = true;
                    }
                }
                if (serializerChanged) {
                    String message = "Updated the serialization for namespace " + ns.getNamespace();
                    LOG.debug(message);
                    status.addDescriptionLine(message);
                }
            }
        }
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
