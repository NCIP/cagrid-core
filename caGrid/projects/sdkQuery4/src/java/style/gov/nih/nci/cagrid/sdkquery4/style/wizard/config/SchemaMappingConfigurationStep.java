package gov.nih.nci.cagrid.sdkquery4.style.wizard.config;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespacesType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.sdkquery4.encoding.SDK40DeserializerFactory;
import gov.nih.nci.cagrid.sdkquery4.encoding.SDK40SerializerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.cagrid.gme.discoverytools.NamespaceTools;

/** 
 *  SchemaMappingConfigurationStep
 *  Configuration for domain model to schema mapping
 * 
 * @author David Ervin
 * 
 * @created Jan 22, 2008 11:25:57 AM
 * @version $Id: SchemaMappingConfigurationStep.java,v 1.6 2009-01-15 00:25:24 dervin Exp $ 
 */
public class SchemaMappingConfigurationStep extends AbstractStyleConfigurationStep {
    private Map<String, File> packageToSourceSchemaFile = null;
    private Map<String, NamespaceType> packageToNamespace = null;
    private ModelInformationUtil modelInfoUtil = null;

    public SchemaMappingConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
        this.packageToSourceSchemaFile = new HashMap<String, File>();
        this.packageToNamespace = new HashMap<String, NamespaceType>();
        this.modelInfoUtil = new ModelInformationUtil(serviceInfo.getServiceDescriptor());
    }


    public void applyConfiguration() throws Exception {
        // copy all source schemas in to the service, adding namespace types as well
        for (String packageName : packageToSourceSchemaFile.keySet()) {
            File sourceSchema = packageToSourceSchemaFile.get(packageName);
            NamespaceType nsType = packageToNamespace.get(packageName);
            File destinationSchema = new File(getServiceSchemaDir(), sourceSchema.getName());
            Utils.copyFile(sourceSchema, destinationSchema);
            nsType.setLocation(destinationSchema.getName());
            System.out.println("Adding namespace type " + nsType.getNamespace());
            addNamespaceToService(nsType);
        }
        
        // set the mapped namespace information for each package in the extension data
        Data extensionData = getExtensionData();
        ModelInformation modelInfo = extensionData.getModelInformation();
        if (modelInfo != null && modelInfo.getModelPackage() != null) {
            ModelPackage[] domainPackages = modelInfo.getModelPackage();
            for (ModelPackage pack : domainPackages) {
                String packageName = pack.getPackageName();
                NamespaceType namespace = packageToNamespace.get(packageName);
                modelInfoUtil.setMappedNamespace(packageName, namespace.getNamespace());
                namespace.setPackageName(packageName);
            }
        }
        storeExtensionData(extensionData);
    }
    
    
    public String mapPackageToSchema(String packageName, File sourceSchemaFile) throws Exception {
        packageToSourceSchemaFile.put(packageName, sourceSchemaFile);
        NamespaceType nsType = NamespaceTools.createNamespaceTypeForFile(
            sourceSchemaFile.getAbsolutePath(), getServiceSchemaDir());
        packageToNamespace.put(packageName, nsType);
        return nsType.getNamespace();
    }
    
    
    // maybe a "keep just these packages" method?
    public void removeMapping(String pack) {
        if (packageToSourceSchemaFile.containsKey(pack)) {
            // find the file and delete
            File internalSchemaFile = new File(getServiceSchemaDir(), 
                packageToSourceSchemaFile.get(pack).getName());
            if (internalSchemaFile.exists()) {
                internalSchemaFile.delete();
            }
            packageToSourceSchemaFile.remove(pack);
        }
        if (packageToNamespace.containsKey(pack)) {
            // remove the namespace from the service model
            String ns = packageToNamespace.get(pack).getNamespace();
            NamespacesType namespacesType = getServiceInformation().getNamespaces();
            List<NamespaceType> namespaces = new ArrayList<NamespaceType>();
            Collections.addAll(namespaces, namespacesType.getNamespace());
            Iterator<NamespaceType> nsIter = namespaces.iterator();
            while (nsIter.hasNext()) {
                NamespaceType nsType = nsIter.next();
                if (nsType.getNamespace().equals(ns)) {
                    nsIter.remove();
                    break;
                }
            }
            namespacesType.setNamespace(namespaces.toArray(new NamespaceType[0]));
            packageToNamespace.remove(pack);
        }
    }
    
    
    private File getServiceSchemaDir() {
        String part = "schema" + File.separator 
            + getServiceInformation().getServices().getService(0).getName();
        return new File(getServiceInformation().getBaseDirectory(), part);
    }
    
    
    private void addNamespaceToService(NamespaceType nsType) {
        // if the namespace already exists in the service, ignore it
        if (CommonTools.getNamespaceType(
            getServiceInformation().getNamespaces(), nsType.getNamespace()) == null) {
            // set the package name
            String packName = CommonTools.getPackageName(nsType.getNamespace());
            nsType.setPackageName(packName);
            // fix the serialization / deserialization on the namespace types
            setSdkSerialization(nsType);
            // set the namespace to not generate beans
            nsType.setGenerateStubs(Boolean.FALSE);
            CommonTools.addNamespace(getServiceInformation().getServiceDescriptor(), nsType);
            // add the namespace to the introduce namespace excludes list so
            // that beans will not be built for these data types
            String excludes = getServiceInformation().getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_NS_EXCLUDES);
            if (!excludes.contains(nsType.getNamespace())) {
                excludes += " -x " + nsType.getNamespace();
                getServiceInformation().getIntroduceServiceProperties().setProperty(
                    IntroduceConstants.INTRODUCE_NS_EXCLUDES, excludes);
            }
        }
    }
    
    
    private void setSdkSerialization(NamespaceType namespace) {
        for (SchemaElementType type : namespace.getSchemaElement()) {
            type.setClassName(type.getType());
            type.setSerializer(SDK40SerializerFactory.class.getName());
            type.setDeserializer(SDK40DeserializerFactory.class.getName());
        }
    }
}
