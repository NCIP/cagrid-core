package gov.nih.nci.cagrid.introduce.extension;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.extension.Properties;
import gov.nih.nci.cagrid.introduce.beans.extension.PropertiesProperty;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.authorization.AuthorizationExtensionManager;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.MessageElement;
import org.apache.log4j.Logger;


/**
 * ExtensionTools Tools to handle extension management
 * 
 * @author David Ervin
 * @created May 10, 2007 10:10:00 AM
 * @version $Id$
 */
public class ExtensionTools {

    private static final Logger logger = Logger.getLogger(ExtensionTools.class);


    /**
     * Gets a named creation post processor
     * 
     * @param extensionName
     *            The name of the extension
     * @return The CreationExtensionPostProcessor of the named extension, or
     *         <code>null</code> if none is present
     * @throws Exception
     */
    public static CreationExtensionPostProcessor getCreationPostProcessor(String extensionName) throws Exception {
        ServiceExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getServiceExtension(
            extensionName);
        if (extensionDesc != null && extensionDesc.getCreationPostProcessor() != null
            && !extensionDesc.getCreationPostProcessor().equals("")) {
            Class c = Class.forName(extensionDesc.getCreationPostProcessor());
            Object obj = c.newInstance();
            return (CreationExtensionPostProcessor) obj;
        }
        return null;
    }


    /**
     * Gets a named codegen post processor
     * 
     * @param extensionName
     *            The name of the extension
     * @return The CodegenExtensionPostProcessor or <code>null</code> if none
     *         is present
     * @throws Exception
     */
    public static CodegenExtensionPostProcessor getCodegenPostProcessor(String extensionName) throws Exception {
        ServiceExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getServiceExtension(
            extensionName);
        if (extensionDesc != null && extensionDesc.getCodegenPostProcessor() != null
            && !extensionDesc.getCodegenPostProcessor().equals("")) {
            Class c = Class.forName(extensionDesc.getCodegenPostProcessor());
            Object obj = c.newInstance();
            return (CodegenExtensionPostProcessor) obj;
        }
        return null;
    }


    /**
     * Gets a named authorization codegen post processor
     * 
     * @param extensionName
     *            The name of the extension
     * @return The AuthorizationCodegenExtensionPostProcessor or
     *         <code>null</code> if none is present
     * @throws Exception
     */
    public static AuthorizationExtensionManager getAuthorizationExtensionCodegenPostProcessor(String extensionName)
        throws Exception {
        AuthorizationExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getAuthorizationExtension(
            extensionName);
        if (extensionDesc != null && extensionDesc.getAuthorizationExtensionManager() != null
            && !extensionDesc.getAuthorizationExtensionManager().equals("")) {
            Class c = Class.forName(extensionDesc.getAuthorizationExtensionManager());
            Object obj = c.newInstance();
            return (AuthorizationExtensionManager) obj;
        }
        return null;
    }


    /**
     * Gets a named codegen extension pre processor
     * 
     * @param extensionName
     *            The name of the extension
     * @return The CodegenExtensionPreProcessor for the named extension, or
     *         <code>null</code> if none is found
     * @throws Exception
     */
    public static CodegenExtensionPreProcessor getCodegenPreProcessor(String extensionName) throws Exception {
        ServiceExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getServiceExtension(
            extensionName);
        if (extensionDesc != null && extensionDesc.getCodegenPreProcessor() != null
            && !extensionDesc.getCodegenPreProcessor().equals("")) {
            Class c = Class.forName(extensionDesc.getCodegenPreProcessor());
            Object obj = c.newInstance();
            return (CodegenExtensionPreProcessor) obj;
        }
        return null;
    }


    /**
     * Gets a named service extension remover
     * 
     * @param extensionName
     *            The name of the extension
     * @return The ServiceExtensionRemover of the named extension, or
     *         <code>null</code> if none is present
     * @throws Exception
     */
    public static ServiceExtensionRemover getServiceExtensionRemover(String extensionName) throws Exception {
        ServiceExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getServiceExtension(
            extensionName);
        if (extensionDesc != null && extensionDesc.getServiceExtensionRemover() != null
            && !extensionDesc.getServiceExtensionRemover().equals("")) {
            Class c = Class.forName(extensionDesc.getServiceExtensionRemover());
            Object obj = c.newInstance();
            return (ServiceExtensionRemover) obj;
        }
        return null;
    }


    /**
     * Extracts a property value from an Introduce Properties bean
     * 
     * @param properties
     *            The properties
     * @param key
     *            The key name
     * @return The value associated with the key, or <code>null</code>
     */
    public static String getProperty(Properties properties, String key) {
        String value = null;
        if (properties != null && properties.getProperty() != null) {
            for (int i = 0; i < properties.getProperty().length; i++) {
                if (properties.getProperty(i).getKey().equals(key)) {
                    return properties.getProperty(i).getValue();
                }
            }
        }
        return value;
    }


    /**
     * Extracts a property object from an Introduce Properties bean
     * 
     * @param properties
     *            The properties
     * @param key
     *            The key name
     * @return The property object associated with the key
     */
    public static PropertiesProperty getPropertyObject(Properties properties, String key) {
        if (properties != null && properties.getProperty() != null) {
            for (int i = 0; i < properties.getProperty().length; i++) {
                if (properties.getProperty(i).getKey().equals(key)) {
                    return properties.getProperty(i);
                }
            }
        }

        return null;
    }


    /**
     * Gets the extension type extension data for a service extension in a
     * service model
     * 
     * @param desc
     *            The service extension description
     * @param info
     *            The service model
     * @return The extension data, or <code>null</code> if no extension is
     *         found
     */
    public static ExtensionTypeExtensionData getExtensionData(ServiceExtensionDescriptionType desc,
        ServiceInformation info) {
        String extensionName = desc.getName();
        ExtensionType[] extensions = info.getServiceDescriptor().getExtensions().getExtension();
        return getExtensionData(extensions, extensionName);
    }


    /**
     * Gets the extension type extension data for a particular service
     * 
     * @param desc
     *            The service extension description
     * @param serviceType
     *            The service type model
     * @return The extension data, or <code>null</code> if no extension is
     *         found
     */
    public static ExtensionTypeExtensionData getExtensionData(ServiceExtensionDescriptionType desc,
        ServiceType serviceType) {
        String extensionName = desc.getName();
        ExtensionType[] extensions = serviceType.getExtensions().getExtension();
        return getExtensionData(extensions, extensionName);
    }


    /**
     * Gets the extension type extension data for a particular method
     * 
     * @param desc
     *            The service extension description
     * @param methodType
     *            The method type model
     * @return The extension data, or <code>null</code> if no extension is
     *         found
     */
    public static ExtensionTypeExtensionData getExtensionData(ServiceExtensionDescriptionType desc,
        MethodType methodType) {
        String extensionName = desc.getName();
        ExtensionType[] extensions = methodType.getExtensions().getExtension();
        return getExtensionData(extensions, extensionName);
    }


    private static ExtensionTypeExtensionData getExtensionData(ExtensionType[] extensions, String extensionName) {
        for (int i = 0; extensions != null && i < extensions.length; i++) {
            if (extensions[i].getName().equals(extensionName)) {
                if (extensions[i].getExtensionData() == null) {
                    extensions[i].setExtensionData(new ExtensionTypeExtensionData());
                }
                return extensions[i].getExtensionData();
            }
        }
        return null;
    }


    /**
     * Gets an extension data element with a given local name
     * 
     * @param extensionData
     *            The extension data
     * @param dataElement
     *            The name of the data element to extract
     * @return The data element
     */
    public static MessageElement getExtensionDataElement(ExtensionTypeExtensionData extensionData, String dataElement) {
        MessageElement[] dataEntries = extensionData.get_any();
        for (int i = 0; dataEntries != null && i < dataEntries.length; i++) {
            if (dataEntries[i].getLocalName().equals(dataElement)) {
                return dataEntries[i];
            }
        }
        return null;
    }


    /**
     * Updates an extension data element
     * 
     * @param data
     *            The extension data
     * @param element
     *            The updated element
     */
    public static void updateExtensionDataElement(ExtensionTypeExtensionData data, MessageElement element) {
        MessageElement[] anys = data.get_any();
        if (anys == null) {
            anys = new MessageElement[]{element};
        } else {
            // find the existing element of the same name, if it exists
            boolean valueSet = false;
            for (int i = 0; i < anys.length; i++) {
                if (anys[i].getName().equals(element.getName())) {
                    anys[i] = element;
                    valueSet = true;
                    break;
                }
            }
            if (!valueSet) {
                MessageElement[] newAnys = new MessageElement[anys.length + 1];
                System.arraycopy(anys, 0, newAnys, 0, anys.length);
                newAnys[newAnys.length - 1] = element;
                anys = newAnys;
            }
        }
        data.set_any(anys);
    }


    /**
     * Removes an extension data element
     * 
     * @param data
     *            The extension data
     * @param dataElementName
     *            The name of the data element to be removed
     */
    public static void removeExtensionDataElement(ExtensionTypeExtensionData data, String dataElementName) {
        MessageElement[] dataEntries = data.get_any();
        if (dataEntries != null) {
            List cleanedEntries = new ArrayList(dataEntries.length);
            for (int i = 0; i < dataEntries.length; i++) {
                if (!dataEntries[i].getName().equals(dataElementName)) {
                    cleanedEntries.add(dataEntries[i]);
                }
            }
            dataEntries = new MessageElement[cleanedEntries.size()];
            cleanedEntries.toArray(dataEntries);
            data.set_any(dataEntries);
        }
    }


    /**
     * Adds an extension's functionality to an <b><i>existing</i></b>
     * service. The extension will be added to the extensions list, and its
     * creation processes invoked against the service.
     * 
     * @param service
     *            The service to extend
     * @param extensionName
     *            The name of the service extension
     */
    public static void addExtensionToService(ServiceInformation service, String extensionName)
        throws CreationExtensionException {
        // locate the extension
        ExtensionDescription extensionDescription = ExtensionsLoader.getInstance().getExtension(extensionName);
        ServiceExtensionDescriptionType serviceExtensionDescription = ExtensionsLoader.getInstance()
            .getServiceExtension(extensionName);
        if (extensionDescription == null || serviceExtensionDescription == null) {
            logger.warn("Extension description NOT FOUND for " + extensionName);
            throw new CreationExtensionException("No service extension named " + extensionName
                + " was able to be loaded");
        }
        // add the extension to the service information
        logger.info("Creating new extension type");
        ExtensionType addedExtension = new ExtensionType();
        addedExtension.setName(extensionName);
        addedExtension.setVersion(extensionDescription.getVersion());
        addedExtension.setExtensionType(extensionDescription.getExtensionType());
        logger.info("Appending extension to extensions list");
        if (service.getExtensions() == null) {
            service.setExtensions(new ExtensionsType());
        }
        ExtensionType[] serviceExtensions = service.getExtensions().getExtension();
        if (serviceExtensions == null) {
            serviceExtensions = new ExtensionType[]{addedExtension};
        } else {
            serviceExtensions = (ExtensionType[]) Utils.appendToArray(serviceExtensions, addedExtension);
        }
        service.getExtensions().setExtension(serviceExtensions);
        // invoke the creation post processor
        CreationExtensionPostProcessor creationPostProcessor = null;
        try {
            creationPostProcessor = getCreationPostProcessor(extensionName);
        } catch (Exception ex) {
            logger.error("ERROR LOADING EXTENSION POST PROCESSOR");
            throw new CreationExtensionException("Error loading post processor for extension: " + ex.getMessage(), ex);
        }
        if (creationPostProcessor != null) {
            logger.info("Invoking extension creation post processor");
            creationPostProcessor.postCreate(serviceExtensionDescription, service);
        }
    }
}
