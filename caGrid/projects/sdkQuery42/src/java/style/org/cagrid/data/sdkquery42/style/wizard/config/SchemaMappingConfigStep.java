package org.cagrid.data.sdkquery42.style.wizard.config;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.common.ExtensionDataManager;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.cagrid.data.sdkquery42.encoding.SDK42DeserializerFactory;
import org.cagrid.data.sdkquery42.encoding.SDK42SerializerFactory;
import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;

public class SchemaMappingConfigStep extends AbstractStyleConfigurationStep {
    
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
    
    
    public void setClassMapping(String packageName, String className, SchemaElementType element) throws Exception {
        modelInfoUtil.setMappedElementName(packageName, className, element.getType());
        element.setClassName(className);
        setSdkSerialization(element);
    }
    
    
    public void unsetClassMapping(String packageName, String className) throws Exception {
        modelInfoUtil.unsetMappedElementName(packageName, className);
    }
    
    
    public void mapFromSdkGeneratedSchemas() throws Exception {
        // locate the directory we'll copy schemas in to
        File schemaDir = getServiceSchemaDirectory();
        
        // the sdk generates an <applicationName>-schema.jar which contains the XSDs
        String applicationName = getCql1ProcessorPropertyValue(SDK42QueryProcessor.PROPERTY_APPLICATION_NAME);
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
                    automaticalyMapElementsToClasses(packageName, nsType);
                    break;
                }
            }
        }
        
        // throw out the temp XSD directory
        Utils.deleteDir(tempXsdDir);
    }
    
    
    private void automaticalyMapElementsToClasses(String packageName, NamespaceType nsType) throws Exception {
        List<ModelClass> mappings = dataManager.getClassMappingsInPackage(packageName);
        for (ModelClass clazz : mappings) {
            String className = clazz.getShortClassName();
            unsetClassMapping(packageName, className);
            for (SchemaElementType element : nsType.getSchemaElement()) {
                if (element.getType().equals(className)) {
                    setClassMapping(packageName, className, element);
                    // set the class name and serialization for this element
                    element.setClassName(className);
                    element.setPackageName(packageName);
                    setSdkSerialization(element);
                    break;
                }
            }
        }
    }
    
    
    private void setSdkSerialization(SchemaElementType element) {
        element.setDeserializer(SDK42DeserializerFactory.class.getName());
        element.setSerializer(SDK42SerializerFactory.class.getName());
    }
    
    
    private File getServiceSchemaDirectory() {
        File schemaDir = new File(getServiceInformation().getBaseDirectory(),
            "schema" + File.separator + getServiceInformation().getIntroduceServiceProperties()
                .getProperty(IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME));
        return schemaDir;
    }
}
