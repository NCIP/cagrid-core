package gov.nih.nci.cagrid.introduce.extension;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.DeploymentExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.IntroduceGDEExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.Properties;
import gov.nih.nci.cagrid.introduce.beans.extension.PropertiesProperty;
import gov.nih.nci.cagrid.introduce.beans.extension.ResourcePropertyEditorExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ConfigurationUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class ExtensionsLoader {
    private static final Logger logger = Logger.getLogger(ExtensionsLoader.class);

    private static ExtensionsLoader loader = null;

    public static final String EXTENSIONS_DIRECTORY = "." + File.separator + "extensions";

    public static final String DISCOVERY_EXTENSION = "DISCOVERY";

    public static final String SERVICE_EXTENSION = "SERVICE";

    public static final String DEPLOYMENT_EXTENSION = "DEPLOYMENT";

    public static final String RESOURCE_PROPERTY_EDITOR_EXTENSION = "RESOURCE_PROPERTY_EDITOR";

    public static final String AUTHORIZATION_EXTENSION = "AUTHORIZATION";

    public static final String INTRODUCE_GDE_EXTENSION = "INTRODUCE_GDE";

    private List serviceExtensionDescriptors;

    private List discoveryExtensionDescriptors;

    private List deploymentExtensionDescriptors;

    private List resourcePropertyEditorExtensionDescriptors;

    private List authorizationExtensionDescriptors;

    private List introduceGDEExtensionDescriptors;

    private List extensions;

    private File extensionsDir;


    private ExtensionsLoader() {
        this.extensionsDir = new File(EXTENSIONS_DIRECTORY);
        serviceExtensionDescriptors = new ArrayList();
        discoveryExtensionDescriptors = new ArrayList();
        resourcePropertyEditorExtensionDescriptors = new ArrayList();
        authorizationExtensionDescriptors = new ArrayList();
        deploymentExtensionDescriptors = new ArrayList();
        introduceGDEExtensionDescriptors = new ArrayList();
        extensions = new ArrayList();
        try {
            this.load();
        } catch (Exception e) {
            logger.error(e);
        }
    }


    public static ExtensionsLoader getInstance() {
        if (loader == null) {
            loader = new ExtensionsLoader();
        }
        return loader;
    }


    private synchronized void load() throws Exception {

        if (extensionsDir.isDirectory()) {
            final File[] dirs = extensionsDir.listFiles();
            for (int i = 0; i < dirs.length; i++) {
                final int count = i;
                if (dirs[i].isDirectory()) {
                    if (new File(dirs[i].getAbsolutePath() + File.separator + "extension.xml").exists()) {

                        logger.info("Loading extension: " + dirs[count].getAbsolutePath() + File.separator
                            + "extension.xml");
                        ExtensionDescription extDesc = null;

                        try {
                            extDesc = (ExtensionDescription) Utils.deserializeDocument(new File(dirs[count]
                                .getAbsolutePath()
                                + File.separator + "extension.xml").getAbsolutePath(), ExtensionDescription.class);

                            // proces the extension properties and add the ones
                            // that
                            // are desired to be made global if they do not
                            // exist yet

                            extensions.add(extDesc);

                            if (extDesc.getExtensionType().equals(DISCOVERY_EXTENSION)) {
                                discoveryExtensionDescriptors.add(extDesc.getDiscoveryExtensionDescription());
                                processExtensionProperties(extDesc.getDiscoveryExtensionDescription().getProperties());
                            } else if (extDesc.getExtensionType().equals(SERVICE_EXTENSION)) {
                                serviceExtensionDescriptors.add(extDesc.getServiceExtensionDescription());
                                processExtensionProperties(extDesc.getServiceExtensionDescription().getProperties());
                            } else if (extDesc.getExtensionType().equals(RESOURCE_PROPERTY_EDITOR_EXTENSION)) {
                                resourcePropertyEditorExtensionDescriptors.add(extDesc
                                    .getResourcePropertyEditorExtensionDescription());
                                processExtensionProperties(extDesc.getResourcePropertyEditorExtensionDescription()
                                    .getProperties());
                            } else if (extDesc.getExtensionType().equals(AUTHORIZATION_EXTENSION)) {
                                authorizationExtensionDescriptors.add(extDesc.getAuthorizationExtensionDescription());
                                processExtensionProperties(extDesc.getAuthorizationExtensionDescription()
                                    .getProperties());
                            } else if (extDesc.getExtensionType().equals(DEPLOYMENT_EXTENSION)) {
                                deploymentExtensionDescriptors.add(extDesc.getDeploymentExtensionDescription());
                                processExtensionProperties(extDesc.getDeploymentExtensionDescription().getProperties());
                            } else if (extDesc.getExtensionType().equals(INTRODUCE_GDE_EXTENSION)) {
                                introduceGDEExtensionDescriptors.add(extDesc.getIntroduceGDEExtensionDescriptionType());
                                processExtensionProperties(extDesc.getIntroduceGDEExtensionDescriptionType()
                                    .getProperties());
                            } else {
                                logger.warn("Unsupported Extension Type: " + extDesc.getExtensionType());
                            }// TODO Auto-generated method stub
                        } catch (Exception e) {
                            logger.error(e);
                        }

                    }

                }
            }
        }

    }


    private void processExtensionProperties(Properties properties) throws Exception {

        if (properties != null && properties.getProperty() != null) {
            for (int i = 0; i < properties.getProperty().length; i++) {
                PropertiesProperty prop = properties.getProperty(i);
                try {
                    if (prop != null && prop.getMakeGlobal() != null && prop.getMakeGlobal().booleanValue()) {

                        Properties globalExtensionProperties = ConfigurationUtil.getGlobalExtensionProperties();

                        if (globalExtensionProperties != null && globalExtensionProperties.getProperty() != null) {
                            boolean found = false;
                            for (int propi = 0; propi < globalExtensionProperties.getProperty().length; propi++) {
                                PropertiesProperty checkProp = globalExtensionProperties.getProperty(propi);
                                if (checkProp.getKey().equals(prop.getKey())) {
                                    found = true;
                                }
                            }
                            if (!found) {
                                PropertiesProperty[] props = new PropertiesProperty[globalExtensionProperties
                                    .getProperty().length + 1];
                                System.arraycopy(globalExtensionProperties.getProperty(), 0, props, 0,
                                    globalExtensionProperties.getProperty().length);
                                props[globalExtensionProperties.getProperty().length] = prop;
                                globalExtensionProperties.setProperty(props);
                            }
                        } else if (globalExtensionProperties != null) {
                            PropertiesProperty[] props = new PropertiesProperty[1];
                            props[0] = prop;
                            globalExtensionProperties.setProperty(props);
                        }
                        ConfigurationUtil.saveConfiguration();

                    }
                } catch (Exception e) {
                    logger.warn("WARNING: extension property " + prop.getKey() + " could not be processed");
                }
            }
        }
    }


    public List getAuthorizationExtensions() {
        return this.authorizationExtensionDescriptors;
    }


    public List getServiceExtensions() {
        return this.serviceExtensionDescriptors;
    }


    public List getIntroduceGDEExtensions() {
        return this.introduceGDEExtensionDescriptors;
    }


    public List getResourcePropertyEditorExtensions() {
        return this.resourcePropertyEditorExtensionDescriptors;
    }


    public ExtensionDescription getExtension(String name) {
        for (int i = 0; i < extensions.size(); i++) {
            ExtensionDescription ex = (ExtensionDescription) extensions.get(i);
            if (ex.getDiscoveryExtensionDescription() != null
                && ex.getDiscoveryExtensionDescription().getName().equals(name)) {
                return ex;
            } else if (ex.getServiceExtensionDescription() != null
                && ex.getServiceExtensionDescription().getName().equals(name)) {
                return ex;
            } else if (ex.getResourcePropertyEditorExtensionDescription() != null
                && ex.getResourcePropertyEditorExtensionDescription().getName().equals(name)) {
                return ex;
            } else if (ex.getAuthorizationExtensionDescription() != null
                && ex.getAuthorizationExtensionDescription().getName().equals(name)) {
                return ex;
            } else if (ex.getDeploymentExtensionDescription() != null
                && ex.getDeploymentExtensionDescription().getName().equals(name)) {
                return ex;
            } else if (ex.getIntroduceGDEExtensionDescriptionType() != null
                && ex.getIntroduceGDEExtensionDescriptionType().getName().equals(name)) {
                return ex;
            }
        }
        return null;
    }


    public ServiceExtensionDescriptionType getServiceExtension(String name) {
        for (int i = 0; i < serviceExtensionDescriptors.size(); i++) {
            ServiceExtensionDescriptionType ex = (ServiceExtensionDescriptionType) serviceExtensionDescriptors.get(i);
            if (ex.getName().equals(name)) {
                return ex;
            }
        }
        return null;
    }


    public DeploymentExtensionDescriptionType getDeploymentExtension(String name) {
        for (int i = 0; i < deploymentExtensionDescriptors.size(); i++) {
            DeploymentExtensionDescriptionType ex = (DeploymentExtensionDescriptionType) deploymentExtensionDescriptors
                .get(i);
            if (ex.getName().equals(name)) {
                return ex;
            }
        }
        return null;
    }


    public AuthorizationExtensionDescriptionType getAuthorizationExtension(String name) {
        for (int i = 0; i < authorizationExtensionDescriptors.size(); i++) {
            AuthorizationExtensionDescriptionType ex = (AuthorizationExtensionDescriptionType) authorizationExtensionDescriptors
                .get(i);
            if (ex.getName().equals(name)) {
                return ex;
            }
        }
        return null;
    }


    public ResourcePropertyEditorExtensionDescriptionType getResourcePropertyEditorExtension(String name) {
        for (int i = 0; i < resourcePropertyEditorExtensionDescriptors.size(); i++) {
            ResourcePropertyEditorExtensionDescriptionType ex = (ResourcePropertyEditorExtensionDescriptionType) resourcePropertyEditorExtensionDescriptors
                .get(i);
            if (ex.getName().equals(name)) {
                return ex;
            }
        }
        return null;
    }


    public IntroduceGDEExtensionDescriptionType getIntroduceGDEExtension(String name) {
        for (int i = 0; i < introduceGDEExtensionDescriptors.size(); i++) {
            IntroduceGDEExtensionDescriptionType ex = (IntroduceGDEExtensionDescriptionType) introduceGDEExtensionDescriptors
                .get(i);
            if (ex.getName().equals(name)) {
                return ex;
            }
        }
        return null;
    }


    public ServiceExtensionDescriptionType getServiceExtensionByDisplayName(String displayName) {
        for (int i = 0; i < serviceExtensionDescriptors.size(); i++) {
            ServiceExtensionDescriptionType ex = (ServiceExtensionDescriptionType) serviceExtensionDescriptors.get(i);
            if (ex.getDisplayName().equals(displayName)) {
                return ex;
            }
        }
        return null;
    }


    public DiscoveryExtensionDescriptionType getDiscoveryExtension(String name) {
        for (int i = 0; i < discoveryExtensionDescriptors.size(); i++) {
            DiscoveryExtensionDescriptionType ex = (DiscoveryExtensionDescriptionType) discoveryExtensionDescriptors
                .get(i);
            if (ex.getName().equals(name)) {
                return ex;
            }
        }
        return null;
    }


    public DiscoveryExtensionDescriptionType getDiscoveryExtensionByDisplayName(String displayName) {
        for (int i = 0; i < discoveryExtensionDescriptors.size(); i++) {
            DiscoveryExtensionDescriptionType ex = (DiscoveryExtensionDescriptionType) discoveryExtensionDescriptors
                .get(i);
            if (ex.getDisplayName().equals(displayName)) {
                return ex;
            }
        }
        return null;
    }


    public AuthorizationExtensionDescriptionType getAuthorizationExtensionByDisplayName(String displayName) {
        for (int i = 0; i < authorizationExtensionDescriptors.size(); i++) {
            AuthorizationExtensionDescriptionType ex = (AuthorizationExtensionDescriptionType) authorizationExtensionDescriptors
                .get(i);
            if (ex.getDisplayName().equals(displayName)) {
                return ex;
            }
        }
        return null;
    }


    public List getDiscoveryExtensions() {
        return this.discoveryExtensionDescriptors;
    }


    public List getDeploymentExtensions() {
        return this.deploymentExtensionDescriptors;
    }


    public File getExtensionsDir() {
        return extensionsDir;
    }
}
