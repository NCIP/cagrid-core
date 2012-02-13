package gov.nih.nci.cagrid.introduce.portal.extension.tools;

import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.DeploymentExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionsType;
import gov.nih.nci.cagrid.introduce.beans.extension.Properties;
import gov.nih.nci.cagrid.introduce.beans.extension.PropertiesProperty;
import gov.nih.nci.cagrid.introduce.beans.extension.ResourcePropertyEditorExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.discoverytools.NamespaceTypeToolsComponent;
import gov.nih.nci.cagrid.introduce.portal.extension.AbstractMethodAuthorizationPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.AbstractServiceAuthorizationPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.CreationExtensionUIDialog;
import gov.nih.nci.cagrid.introduce.portal.extension.DeploymentUIPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.ResourcePropertyEditorPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.ServiceDeploymentUIPanel;
import gov.nih.nci.cagrid.introduce.portal.extension.ServiceModificationUIPanel;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;

import java.awt.Frame;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;


public class ExtensionTools {
    public static CreationExtensionUIDialog getCreationUIDialog(Frame owner, String extensionName,
        gov.nih.nci.cagrid.introduce.common.ServiceInformation info) throws Exception {
        ServiceExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getServiceExtension(
            extensionName);
        if ((extensionDesc != null) && (extensionDesc.getCreationUIDialog() != null)
            && !extensionDesc.getCreationUIDialog().equals("")) {
            Class c = Class.forName(extensionDesc.getCreationUIDialog());
            Constructor con = c.getConstructor(new Class[]{Frame.class, ServiceExtensionDescriptionType.class,
                    ServiceInformation.class});
            Object obj = con.newInstance(new Object[]{owner, extensionDesc, info});
            return (CreationExtensionUIDialog) obj;
        }
        return null;
    }


    public static ServiceModificationUIPanel getServiceModificationUIPanel(String extensionName,
        gov.nih.nci.cagrid.introduce.common.ServiceInformation info) throws Exception {
        ServiceExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getServiceExtension(
            extensionName);
        if ((extensionDesc != null) && (extensionDesc.getServiceModificationUIPanel() != null)
            && !extensionDesc.getServiceModificationUIPanel().equals("")) {
            Class c = Class.forName(extensionDesc.getServiceModificationUIPanel());
            Constructor con = c.getConstructor(new Class[]{ServiceExtensionDescriptionType.class,
                    ServiceInformation.class});
            Object obj = con.newInstance(new Object[]{extensionDesc, info});
            return (ServiceModificationUIPanel) obj;
        }
        return null;
    }


    public static ServiceDeploymentUIPanel getServiceDeploymentUIPanel(String extensionName,
        gov.nih.nci.cagrid.introduce.common.ServiceInformation info) throws Exception {
        ServiceExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getServiceExtension(
            extensionName);
        if ((extensionDesc != null) && (extensionDesc.getServiceDeploymentUIPanel() != null)
            && !extensionDesc.getServiceDeploymentUIPanel().equals("")) {
            Class c = Class.forName(extensionDesc.getServiceDeploymentUIPanel());
            Constructor con = c.getConstructor(new Class[]{ServiceExtensionDescriptionType.class,
                    ServiceInformation.class});
            Object obj = con.newInstance(new Object[]{extensionDesc, info});
            return (ServiceDeploymentUIPanel) obj;
        }
        return null;
    }
    
    
    public static DeploymentUIPanel getDeploymentUIPanel(String extensionName,
        gov.nih.nci.cagrid.introduce.common.ServiceInformation info) throws Exception {
        DeploymentExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getDeploymentExtension(
            extensionName);
        if ((extensionDesc != null) && (extensionDesc.getDeploymentUIPanel() != null)
            && !extensionDesc.getDeploymentUIPanel().equals("")) {
            Class c = Class.forName(extensionDesc.getDeploymentUIPanel());
            Constructor con = c.getConstructor(new Class[]{DeploymentExtensionDescriptionType.class,
                    ServiceInformation.class});
            Object obj = con.newInstance(new Object[]{extensionDesc, info});
            return (DeploymentUIPanel) obj;
        }
        return null;
    }


    public static AbstractServiceAuthorizationPanel getServiceAuthorizationPanel(String extensionName,
        ServiceInformation info, ServiceType service) throws Exception {
        AuthorizationExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getAuthorizationExtension(
            extensionName);
        if ((extensionDesc != null) && (extensionDesc.getServiceAuthorizationPanel() != null)
            && !extensionDesc.getServiceAuthorizationPanel().equals("")) {
            Class c = Class.forName(extensionDesc.getServiceAuthorizationPanel());
            Constructor con = c.getConstructor(new Class[]{AuthorizationExtensionDescriptionType.class,
                    ServiceInformation.class, ServiceType.class});
            Object obj = con.newInstance(new Object[]{extensionDesc, info, service});
            return (AbstractServiceAuthorizationPanel) obj;
        }
        return null;
    }


    public static AbstractMethodAuthorizationPanel getMethodAuthorizationPanel(String extensionName,
        ServiceInformation info, ServiceType service, MethodType method) throws Exception {
        AuthorizationExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance().getAuthorizationExtension(
            extensionName);
        if ((extensionDesc != null) && (extensionDesc.getMethodAuthorizationPanel() != null)
            && !extensionDesc.getMethodAuthorizationPanel().equals("")) {
            Class c = Class.forName(extensionDesc.getMethodAuthorizationPanel());
            Constructor con = c.getConstructor(new Class[]{AuthorizationExtensionDescriptionType.class,
                    ServiceInformation.class, ServiceType.class, MethodType.class});
            Object obj = con.newInstance(new Object[]{extensionDesc, info, service, method});
            return (AbstractMethodAuthorizationPanel) obj;
        }
        return null;
    }


    public static ResourcePropertyEditorPanel getMetadataEditorComponent(String extensionName, ResourcePropertyType prop, String rpData,
        File schemaFile, File schemaDir) throws Exception {
        ResourcePropertyEditorExtensionDescriptionType extensionDesc = ExtensionsLoader.getInstance()
            .getResourcePropertyEditorExtension(extensionName);
        if ((extensionDesc != null) && (extensionDesc.getResourcePropertyEditorPanel() != null)
            && !extensionDesc.getResourcePropertyEditorPanel().equals("")) {
            Class c = Class.forName(extensionDesc.getResourcePropertyEditorPanel());
            Constructor con = c.getConstructor(new Class[]{ResourcePropertyType.class, String.class, File.class, File.class});
            Object obj = con.newInstance(new Object[]{prop, rpData, schemaFile, schemaDir});

            return (ResourcePropertyEditorPanel) obj;
        }
        return null;
    }


    public static NamespaceTypeToolsComponent getNamespaceTypeToolsComponent(String extensionName) throws Exception {
        DiscoveryExtensionDescriptionType extensionD = ExtensionsLoader.getInstance().getDiscoveryExtension(
            extensionName);
        if ((extensionD != null) && (extensionD.getDiscoveryToolsPanelExtension() != null)
            && !extensionD.getDiscoveryToolsPanelExtension().equals("")) {
            Class c = Class.forName(extensionD.getDiscoveryToolsPanelExtension());
            Constructor con = c.getConstructor(new Class[]{DiscoveryExtensionDescriptionType.class});
            Object obj = con.newInstance(new Object[]{extensionD});
            return (NamespaceTypeToolsComponent) obj;
        }
        return null;
    }


    public static NamespaceTypeDiscoveryComponent getNamespaceTypeDiscoveryComponent(String extensionName,
        NamespacesType currentNamespaces) throws Exception {
        DiscoveryExtensionDescriptionType extensionD = ExtensionsLoader.getInstance().getDiscoveryExtension(
            extensionName);
        if ((extensionD != null) && (extensionD.getDiscoveryPanelExtension() != null)
            && !extensionD.getDiscoveryPanelExtension().equals("")) {
            Class c = Class.forName(extensionD.getDiscoveryPanelExtension());
            Constructor con = c.getConstructor(new Class[]{DiscoveryExtensionDescriptionType.class,
                    NamespacesType.class});
            Object obj = con.newInstance(new Object[]{extensionD, currentNamespaces});
            return (NamespaceTypeDiscoveryComponent) obj;
        }
        return null;
    }


    public static ExtensionType getServiceExtension(String extensionName, ServiceInformation info) throws Exception {
        return getExtension(extensionName, ExtensionsLoader.SERVICE_EXTENSION, info);
    }


    public static ExtensionType getAuthorizationExtension(String extensionName, ServiceInformation info)
        throws Exception {
        return getExtension(extensionName, ExtensionsLoader.AUTHORIZATION_EXTENSION, info);
    }


    public static ExtensionType getDiscoveryExtension(String extensionName, ServiceInformation info) throws Exception {
        return getExtension(extensionName, ExtensionsLoader.DISCOVERY_EXTENSION, info);
    }


    public static ExtensionType getExtension(String extensionName, String extensionType, ServiceInformation info)
        throws Exception {
        ExtensionsType list = info.getExtensions();
        if (list != null) {
            ExtensionType[] exts = list.getExtension();
            if (exts != null) {
                for (int i = 0; i < exts.length; i++) {
                    if ((extensionName.equals(exts[i].getName())) && (extensionType.equals(exts[i].getExtensionType()))) {
                        return exts[i];
                    }
                }
            }
        }
        return null;
    }


    public static ExtensionType removeAuthorizationServiceExtensios(ServiceInformation info) throws Exception {
        List desc = ExtensionsLoader.getInstance().getServiceExtensions();
        Map toBeRemoved = new HashMap();
        for (int i = 0; i < desc.size(); i++) {
            ServiceExtensionDescriptionType sed = (ServiceExtensionDescriptionType) desc.get(i);
            Properties props = sed.getProperties();
            if (props != null) {
                PropertiesProperty[] pp = props.getProperty();
                if (pp != null) {
                    for (int j = 0; j < pp.length; j++) {
                        if ((pp[j].getKey().equals("isAuthorizationExtension"))
                            && (pp[j].getValue().equalsIgnoreCase("true"))) {
                            toBeRemoved.put(sed.getName(), sed);
                            break;
                        }
                    }
                }
            }
        }
        List toBeSaved = new ArrayList();
        ExtensionsType list = info.getExtensions();
        if (list != null) {
            ExtensionType[] exts = list.getExtension();
            if (exts != null) {
                for (int i = 0; i < exts.length; i++) {
                    if (!((exts[i].getExtensionType().equals(ExtensionsLoader.SERVICE_EXTENSION)) && (toBeRemoved
                        .containsKey(exts[i].getName())))) {
                        toBeSaved.add(exts[i]);
                    }
                }
                if (toBeSaved.size() > 0) {
                    ExtensionType[] newexts = new ExtensionType[toBeSaved.size()];
                    for (int i = 0; i < toBeSaved.size(); i++) {
                        newexts[i] = (ExtensionType) toBeSaved.get(i);
                    }
                    list.setExtension(newexts);
                } else {
                    list.setExtension(null);
                }
            }
        }
        return null;
    }


    public static ExtensionType getAddServiceExtension(String extensionName, ServiceInformation info) throws Exception {
        return getAddExtension(extensionName, ExtensionsLoader.SERVICE_EXTENSION, info);
    }


    public static ExtensionType getAddAuthorizationExtension(String extensionName, ServiceInformation info)
        throws Exception {
        return getAddExtension(extensionName, ExtensionsLoader.AUTHORIZATION_EXTENSION, info);
    }


    public static ExtensionType getAddDiscoveryExtension(String extensionName, ServiceInformation info)
        throws Exception {
        return getAddExtension(extensionName, ExtensionsLoader.DISCOVERY_EXTENSION, info);
    }


    public static ExtensionType getAddExtension(String extensionName, String extensionType, ServiceInformation info)
        throws Exception {

        ExtensionType ext = getExtension(extensionName, extensionType, info);
        if (ext != null) {
            return ext;
        } else {

            // Not Found add it.
            ExtensionsType list = info.getExtensions();
            if (list == null) {
                list = new ExtensionsType();
                info.setExtensions(list);
            }

            ext = new ExtensionType();
            ext.setName(extensionName);
            ext.setExtensionType(extensionType);
            ext.setExtensionData(new ExtensionTypeExtensionData());

            ExtensionType[] exts = list.getExtension();
            ExtensionType[] exts2 = null;
            if (exts != null) {
                exts2 = new ExtensionType[exts.length + 1];
                for (int i = 0; i < exts.length; i++) {
                    exts2[i] = exts[i];
                }
            } else {
                exts2 = new ExtensionType[1];
            }
            exts2[(exts2.length - 1)] = ext;
            list.setExtension(exts2);
            return ext;
        }
    }


    /**
     * Reads the schema and sets the SchemaElements of the NamespaceType for
     * each.
     * 
     * @param namespace
     *            the namespace to populate
     * @param schemaContents
     *            the schema's text contents
     * @throws Exception
     */
    public static void setSchemaElements(NamespaceType namespace, Document schemaContents) throws Exception {
        List elementTypes = schemaContents.getRootElement().getChildren("element",
            schemaContents.getRootElement().getNamespace());
        SchemaElementType[] schemaTypes = new SchemaElementType[elementTypes.size()];
        for (int i = 0; i < elementTypes.size(); i++) {
            Element element = (Element) elementTypes.get(i);
            SchemaElementType type = new SchemaElementType();
            type.setType(element.getAttributeValue("name"));
            schemaTypes[i] = type;
        }
        namespace.setSchemaElement(schemaTypes);
    }


    public static ResourcePropertyEditorExtensionDescriptionType getResourcePropertyEditorExtensionDescriptor(
        javax.xml.namespace.QName qname) {
        ResourcePropertyEditorExtensionDescriptionType mde = null;
        List metadataExtensions = ExtensionsLoader.getInstance().getResourcePropertyEditorExtensions();
        for (int i = 0; i < metadataExtensions.size(); i++) {
            ResourcePropertyEditorExtensionDescriptionType tmde = (ResourcePropertyEditorExtensionDescriptionType) metadataExtensions
                .get(i);
            if ((tmde != null) && (tmde.getQname() != null) && tmde.getQname().equals(qname)) {
                mde = tmde;
                break;
            }
        }
        return mde;
    }

}
