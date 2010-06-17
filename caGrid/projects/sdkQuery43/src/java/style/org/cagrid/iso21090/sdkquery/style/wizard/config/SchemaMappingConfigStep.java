package org.cagrid.iso21090.sdkquery.style.wizard.config;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.portal.MultiEventProgressBar;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.configuration.NamespaceReplacementPolicy;
import gov.nih.nci.cagrid.introduce.beans.extension.DiscoveryExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.portal.modification.discovery.NamespaceTypeDiscoveryComponent;
import gov.nih.nci.iso21090.grid.ser.JaxbDeserializerFactory;
import gov.nih.nci.iso21090.grid.ser.JaxbSerializerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.sdkquery.encoding.SDK43DeserializerFactory;
import org.cagrid.iso21090.sdkquery.encoding.SDK43SerializerFactory;
import org.cagrid.iso21090.sdkquery.processor.SDK43QueryProcessor;

public class SchemaMappingConfigStep extends AbstractStyleConfigurationStep {
    
    private static Log LOG = LogFactory.getLog(SchemaMappingConfigStep.class);
    
    private ExtensionDataManager dataManager = null;
    private ModelInformationUtil modelInfoUtil = null;
    
    public SchemaMappingConfigStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
        modelInfoUtil = new ModelInformationUtil(serviceInfo.getServiceDescriptor());
        for (ExtensionType extension : serviceInfo.getExtensions().getExtension()) {
            if (extension.getName().equals("data")) {
                dataManager = new ExtensionDataManager(extension.getExtensionData());
                break;
            }
        }
        if (dataManager == null) {
            throw new IllegalStateException("No data extension found... " +
                    "this really shouldn't be possible, but apparently it happened anyway");
        }
    }


    public void applyConfiguration() throws Exception {
        // TODO: implement me
    }
    
    
    public ModelInformation getCurrentModelInformation() throws Exception {
        return dataManager.getModelInformation();
    }
    
    
    public void setPackageNamespace(String packageName, String namespace) throws Exception {
        modelInfoUtil.setMappedNamespace(packageName, namespace);
    }
    
    
    public void setClassMapping(String packageName, String className, SchemaElementType element, boolean setSerialization) throws Exception {
        modelInfoUtil.setMappedElementName(packageName, className, element.getType());
        element.setClassName(className);
        if (setSerialization) {
            setSdkSerialization(element, className, packageName);
        }
    }
    
    
    public void unsetClassMapping(String packageName, String className) throws Exception {
        modelInfoUtil.unsetMappedElementName(packageName, className);
    }
    
    
    public void mapFromSdkGeneratedSchemas() throws Exception {
        // locate the directory we'll copy schemas in to
        File schemaDir = getServiceSchemaDirectory();
        
        // the sdk generates an <applicationName>-schema.jar which contains the XSDs
        String applicationName = getCql1ProcessorPropertyValue(SDK43QueryProcessor.PROPERTY_APPLICATION_NAME);
        File serviceLibDir = new File(getServiceInformation().getBaseDirectory(), "lib");
        File schemasJarFile = new File(serviceLibDir, applicationName + "-schema.jar");
        JarFile schemasJar = new JarFile(schemasJarFile);
        
        // copy all the XSDs into a temporary location.  Must do this so 
        // schemas which reference each other can be resolved
        File tempXsdDir = File.createTempFile("TempCaCoreXSDs", "dir");
        tempXsdDir.delete();
        tempXsdDir.mkdirs();
        Enumeration<JarEntry> entries = schemasJar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".xsd")) {
                StringBuffer schemaText = JarUtilities.getFileContents(schemasJar, entry.getName());
                File schemaFile = new File(tempXsdDir, new File(entry.getName()).getName());
                Utils.stringBufferToFile(schemaText, schemaFile.getAbsolutePath());
            }
        }
        schemasJar.close();
        
        // iterate the schemas and try to map them to domain packages
        boolean isoPackagePresent = false;
        for (File xsdFile : tempXsdDir.listFiles()) {
            String schemaPackageName = xsdFile.getName();
            schemaPackageName = schemaPackageName.substring(0, schemaPackageName.length() - 4);
            // find the package of this name
            for (String packageName : dataManager.getCadsrPackageNames()) {
                if (packageName.equals(schemaPackageName)) {
                    // create the namespace type of the XSD
                    NamespaceType nsType = CommonTools.createNamespaceType(xsdFile.getAbsolutePath(), schemaDir);
                    
                    // add the namespace to the service definition
                    CommonTools.addNamespace(getServiceInformation().getServiceDescriptor(), nsType);
                    
                    // copy the XSD in to the service's schema dir
                    File xsdOut = new File(schemaDir, xsdFile.getName());
                    Utils.copyFile(xsdFile, xsdOut);
                    nsType.setLocation(xsdOut.getName());
                    
                    // get cadsr package, set namespace, automagic mapping
                    modelInfoUtil.setMappedNamespace(packageName, nsType.getNamespace());
                    automaticalyMapElementsToClasses(packageName, nsType, true);
                    break;
                } else if (StyleProperties.ISO_PACKAGE_NAME.equals(packageName)) {
                    isoPackagePresent = true;
                }
            }
        }
        
        if (isoPackagePresent) {
            // map the ISO package to the ISO schema
            // start by loading up and runing the ISO types discovery extension
            DiscoveryExtensionDescriptionType isoDiscoveryDescriptor = ExtensionsLoader.getInstance().getDiscoveryExtension("ISO21090-discovery");
            String discoveryClassName = isoDiscoveryDescriptor.getDiscoveryPanelExtension();
            Class<?> discoveryClass = Class.forName(discoveryClassName);
            Constructor<?> discoveryComponentCons = discoveryClass.getConstructor(
                DiscoveryExtensionDescriptionType.class, NamespacesType.class);
            NamespaceTypeDiscoveryComponent isoDiscoveryTool = 
                (NamespaceTypeDiscoveryComponent) discoveryComponentCons.newInstance(
                    isoDiscoveryDescriptor, getServiceInformation().getNamespaces());
            
            NamespaceType[] isoNamespaces = isoDiscoveryTool.createNamespaceType(
                schemaDir, NamespaceReplacementPolicy.IGNORE, new MultiEventProgressBar(true));
            NamespaceType extIsoNamespace = null;
            for (NamespaceType ns : isoNamespaces) {
                CommonTools.addNamespace(getServiceInformation().getServiceDescriptor(), ns);
                if (ns.getLocation().endsWith("_extensions.xsd")) {
                    extIsoNamespace = ns;
                }
            }
            
            modelInfoUtil.setMappedNamespace(StyleProperties.ISO_PACKAGE_NAME, extIsoNamespace.getNamespace());
            for (SchemaElementType schemaElement : extIsoNamespace.getSchemaElement()) {
                // the ISO datatypes tool assigns the class name from the org.iso._21090 package, (EN, II, etc)
                // while technically correct, the SDK is talking about the localized gov.nih.nci.iso21090 types,
                // which don't use those names (En, Ii, etc)... gah
                String type = schemaElement.getType();
                if (type.startsWith("Ivl")) {
                    schemaElement.setClassName("Ivl");
                } else if (type.startsWith("DSet")) {
                    schemaElement.setClassName("DSet");
                } else {
                    schemaElement.setClassName(type);
                }
            }
        }
        
        // throw out the temp XSD directory
        Utils.deleteDir(tempXsdDir);
    }
    
    
    private void automaticalyMapElementsToClasses(String packageName, NamespaceType nsType, boolean setSerialization) throws Exception {
        List<ModelClass> mappings = dataManager.getClassMappingsInPackage(packageName);
        for (ModelClass clazz : mappings) {
            String className = clazz.getShortClassName();
            unsetClassMapping(packageName, className);
            for (SchemaElementType element : nsType.getSchemaElement()) {
                if (element.getType().equals(className)) {
                    setClassMapping(packageName, className, element, setSerialization);
                    break;
                }
            }
        }
    }
    
    
    private void setSdkSerialization(SchemaElementType element, String className, String packageName) {
        LOG.info("Setting SDK serialization for " + packageName + "." + className + " [" + element.getType() + "]");
        boolean useJaxbSerializers = false;
        try {
            useJaxbSerializers = Boolean.parseBoolean(getStyleProperty(StyleProperties.USE_JAXB_SERIALIZERS));
        } catch (IOException e) {
            e.printStackTrace();
        }
        element.setClassName(className);
        element.setPackageName(packageName);
        if (useJaxbSerializers) {
            LOG.info("\tUsing JaxB serializer");
            element.setDeserializer(JaxbDeserializerFactory.class.getName());
            element.setSerializer(JaxbSerializerFactory.class.getName());
        } else {
            LOG.info("\tUsing Castor serializer");
            element.setDeserializer(SDK43DeserializerFactory.class.getName());
            element.setSerializer(SDK43SerializerFactory.class.getName());
        }
    }
    
    
    private File getServiceSchemaDirectory() {
        File schemaDir = new File(getServiceInformation().getBaseDirectory(),
            "schema" + File.separator + getServiceInformation().getIntroduceServiceProperties()
                .getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        return schemaDir;
    }
}
