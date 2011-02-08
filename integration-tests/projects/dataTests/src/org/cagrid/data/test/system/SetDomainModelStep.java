package org.cagrid.data.test.system;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.cagrid.testing.system.haste.Step;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cagrid.data.test.creation.DataTestCaseInfo;
import org.cagrid.mms.domain.UMLProjectIdentifer;


/**
 * SetDomainModelStep
 * 
 * Sets the domain model of the data service
 * 
 * @author David
 */
public class SetDomainModelStep extends Step {

    public static final String DOMAIN_MODEL_FILENAME = "bookstoreDomainModel.xml";

    private DataTestCaseInfo serviceInfo = null;


    public SetDomainModelStep(DataTestCaseInfo info) {
        this.serviceInfo = info;
    }


    public void runStep() throws Throwable {
        System.out.println("Running step: " + getClass().getName());
        String serviceModelFile = serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE;
        ServiceDescription desc = null;
        try {
            desc = Utils.deserializeDocument(serviceModelFile, ServiceDescription.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error loading service description: " + ex.getMessage());
        }

        String bookstoreModelName = "/resources/bookstore/" + DOMAIN_MODEL_FILENAME;

        // copy the domain model into the service's etc dir
        File serviceEtcDir = new File(serviceInfo.getDir(), "etc");
        File domainModelOut = new File(serviceEtcDir, DOMAIN_MODEL_FILENAME);
        InputStream domainModelIn = getClass().getResourceAsStream(bookstoreModelName);
        assertNotNull("Could not load domain model resource from classpath!", domainModelIn);
        StringBuffer modelText = Utils.inputStreamToStringBuffer(domainModelIn);
        domainModelIn.close();
        Utils.stringBufferToFile(modelText, domainModelOut);

        // create the domain model resource property
        ResourcePropertyType domainModelResourceProperty = new ResourcePropertyType();
        domainModelResourceProperty.setPopulateFromFile(true);
        domainModelResourceProperty.setRegister(true);
        domainModelResourceProperty.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);

        // set value of resource property
        domainModelResourceProperty.setFileLocation(DOMAIN_MODEL_FILENAME);

        // there can be only one domain model...
        ResourcePropertyType[] domainModelProperties = CommonTools.getResourcePropertiesOfType(
            desc.getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
        while (domainModelProperties != null && domainModelProperties.length != 0) {
            CommonTools.removeResourceProperty(
                desc.getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
            domainModelProperties = CommonTools.getResourcePropertiesOfType(
                desc.getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
        }
        CommonTools.addResourcePropety(desc.getServices().getService(0),
            domainModelResourceProperty);

        // deserialize the domain model
        DomainModel model = null;
        FileReader reader = new FileReader(domainModelOut);
        model = MetadataUtils.deserializeDomainModel(reader);
        reader.close();

        // set the cadsr information to NOT generate a new model
        ModelInformation modelInfo = new ModelInformation();
        // model from filesystem
        modelInfo.setSource(ModelSourceType.preBuilt);
        // mms identifier
        UMLProjectIdentifer id = new UMLProjectIdentifer();
        id.setIdentifier(model.getProjectShortName());
        id.setVersion(model.getProjectVersion());
        modelInfo.setUMLProjectIdentifer(id);

        // map classes by packages
        Map<String, List<UMLClass>> classesByPackage = new HashMap<String, List<UMLClass>>();
        for (UMLClass modelClass : model.getExposedUMLClassCollection().getUMLClass()) {
            List<UMLClass> packageClasses = classesByPackage.get(modelClass.getPackageName());
            if (packageClasses == null) {
                packageClasses = new LinkedList<UMLClass>();
                classesByPackage.put(modelClass.getPackageName(), packageClasses);
            }
            packageClasses.add(modelClass);
        }

        List<ModelPackage> modelPackages = new ArrayList<ModelPackage>();
        for (String packageName : classesByPackage.keySet()) {
            List<UMLClass> classes = classesByPackage.get(packageName);
            ModelPackage pack = new ModelPackage();
            pack.setPackageName(packageName);
            List<ModelClass> modelClasses = new ArrayList<ModelClass>();
            for (UMLClass clazz : classes) {
                ModelClass modelClass = new ModelClass();
                modelClass.setShortClassName(clazz.getClassName());
                // NOT populating element names until Schema Mapping Panel
                modelClass.setSelected(true);
                modelClass.setTargetable(clazz.isAllowableAsTarget());
                modelClasses.add(modelClass);
            }
            ModelClass[] mappingArray = new ModelClass[modelClasses.size()];
            modelClasses.toArray(mappingArray);
            pack.setModelClass(mappingArray);
            modelPackages.add(pack);
        }
        ModelPackage[] packageArray = new ModelPackage[modelPackages.size()];
        modelPackages.toArray(packageArray);
        modelInfo.setModelPackage(packageArray);
        storeModelInformation(desc, modelInfo);
    }
    
    
    private void storeModelInformation(ServiceDescription desc, ModelInformation modelInfo) throws Exception {
        // set the cadsr info on the extension data model
        Data data = getExtensionData(desc);
        data.setModelInformation(modelInfo);
        storeExtensionData(desc, data);
    }
    
    
    protected Data getExtensionData(ServiceDescription desc) throws Exception {
        ExtensionType[] extensions = desc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        Data extensionData = ExtensionDataUtils.getExtensionData(dataExtension.getExtensionData());
        return extensionData;
    }
    
    
    protected void storeExtensionData(ServiceDescription desc, Data data) throws Exception {
        File serviceModelFile = new File(serviceInfo.getDir(), IntroduceConstants.INTRODUCE_XML_FILE);
        
        ExtensionType[] extensions = desc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        ExtensionDataUtils.storeExtensionData(dataExtension.getExtensionData(), data);
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), desc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
}
