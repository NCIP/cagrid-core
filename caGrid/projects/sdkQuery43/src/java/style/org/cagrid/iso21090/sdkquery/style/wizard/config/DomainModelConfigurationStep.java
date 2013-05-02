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
package org.cagrid.iso21090.sdkquery.style.wizard.config;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;
import gov.nih.nci.ncicb.xmiinout.handler.HandlerEnum;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.iso21090.model.tools.ISOSupportDomainModelGenerator;
import org.cagrid.iso21090.sdkquery.processor.SDK43QueryProcessor;
import org.cagrid.mms.domain.UMLProjectIdentifer;

/**
 * DomainModelConfigurationStep
 * Provides a central point for configuring the domain model.
 * 
 * This bean will actually store up information from all three means of
 * getting a domain model into a service (file system provided, cadsr, and SDK's XMI)
 * and on applyConfiguration() implement the method defined by setModelSource() 
 * 
 * @author David
 */
public class DomainModelConfigurationStep extends AbstractStyleConfigurationStep {
    
    private static Log LOG = LogFactory.getLog(DomainModelConfigurationStep.class);
    
    private DomainModelConfigurationSource configurationSource = null;
    
    // from local file system
    private File domainModelFile = null;
    
    // from a provided XMI
    private String projectShortName = null;
    private String projectVersion = null;
    private String excludePackages = null;
    private File xmiFile = null;
    private HandlerEnum xmiType = null;
    
    public DomainModelConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }


    public void applyConfiguration() throws Exception {
        if (configurationSource == null) {
            throw new IllegalStateException(
                "The domain model configuration source has not been defined!!!");
        }
        switch (configurationSource) {
            case XMI:
                applySdkModelConfiguration();
                break;
            case FILE_SYSTEM:
                applyFileSystemModelConfiguration();
                break;
            default:
                throw new IllegalStateException("Unknown model configuration source");
        }
    }
    
    
    private void applySdkModelConfiguration() throws Exception {
        ISOSupportDomainModelGenerator generator = new ISOSupportDomainModelGenerator(getXmiType());
        generator.setAttributeVersion(1.0f);
        generator.setPackageExcludeRegex(getExcludePackages());
        generator.setProjectShortName(getProjectShortName());
        generator.setProjectVersion(getProjectVersion());
        DomainModel generatedModel = generator.generateDomainModel(getXmiFile().getAbsolutePath());
        
        // save the domain model to the service's etc dir
        File etcDir = new File(
            getServiceInformation().getBaseDirectory(), "etc");
        String applicationName = getCql1ProcessorPropertyValue(SDK43QueryProcessor.PROPERTY_APPLICATION_NAME);
        File generatedModelFile = new File(etcDir, applicationName + "_domainModel.xml");
        FileWriter modelWriter = new FileWriter(generatedModelFile);
        MetadataUtils.serializeDomainModel(generatedModel, modelWriter);
        modelWriter.flush();
        modelWriter.close();
        LOG.debug("Wrote domain model to " + generatedModelFile.getAbsolutePath());
        
        // get / create the domain model resource property
        ResourcePropertyType domainModelResourceProperty = null;
        ResourcePropertyType[] domainModelProps = CommonTools.getResourcePropertiesOfType(
            getServiceInformation().getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
        if (domainModelProps.length != 0) {
            domainModelResourceProperty = domainModelProps[0];
            // if old file exists, delete it
            File oldFile = new File(
                etcDir, domainModelResourceProperty.getFileLocation());
            if (oldFile.exists()) {
                LOG.debug("Deleting " + oldFile.getAbsolutePath());
                oldFile.delete();
            }
        } else {
            domainModelResourceProperty = new ResourcePropertyType();
            domainModelResourceProperty.setPopulateFromFile(true);
            domainModelResourceProperty.setRegister(true);
            domainModelResourceProperty.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
        }
        
        // set value of resource property
        domainModelResourceProperty.setFileLocation(generatedModelFile.getName());
        
        // possibly put the resource property in the service for the first time
        if (domainModelProps.length == 0) {
            CommonTools.addResourcePropety(
                getServiceInformation().getServices().getService(0), domainModelResourceProperty);
        }
        
        // deserialize the domain model
        DomainModel model = null;
        FileReader reader = new FileReader(generatedModelFile);
        model = MetadataUtils.deserializeDomainModel(reader);
        reader.close();
        
        // set the model information to NOT generate a new model
        ModelInformation modelInfo = new ModelInformation();
        // use provided model
        modelInfo.setSource(ModelSourceType.preBuilt);
        // TODO: this is a caDSR style identifier, make it MMS-like
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
            ModelClass[] classArray = new ModelClass[modelClasses.size()];
            modelClasses.toArray(classArray);
            pack.setModelClass(classArray);
            modelPackages.add(pack);
        }
        ModelPackage[] packageArray = new ModelPackage[modelPackages.size()];
        modelPackages.toArray(packageArray);
        modelInfo.setModelPackage(packageArray);
        
        storeModelInformation(modelInfo);
    }
    
    
    private void applyFileSystemModelConfiguration() throws Exception {
        // copy the domain model to the service's etc dir
        File etcDir = new File(
            getServiceInformation().getBaseDirectory(), "etc");
        File serviceModelFile = new File(etcDir, domainModelFile.getName());
        Utils.copyFile(domainModelFile, serviceModelFile);
        
        // get / create the domain model resource property
        ResourcePropertyType domainModelResourceProperty = null;
        ResourcePropertyType[] domainModelProps = CommonTools.getResourcePropertiesOfType(
            getServiceInformation().getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
        if (domainModelProps.length != 0) {
            domainModelResourceProperty = domainModelProps[0];
            // if old file exists, delete it
            File oldFile = new File(
                etcDir, domainModelResourceProperty.getFileLocation());
            if (oldFile.exists()) {
                LOG.debug("Deleting " + oldFile.getAbsolutePath());
                oldFile.delete();
            }
        } else {
            domainModelResourceProperty = new ResourcePropertyType();
            domainModelResourceProperty.setPopulateFromFile(true);
            domainModelResourceProperty.setRegister(true);
            domainModelResourceProperty.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
        }
        
        // set value of resource property
        domainModelResourceProperty.setFileLocation(serviceModelFile.getName());
        
        // possibly put the resource property in the service for the first time
        if (domainModelProps.length == 0) {
            CommonTools.addResourcePropety(
                getServiceInformation().getServices().getService(0), domainModelResourceProperty);
        }
        
        // deserialize the domain model
        DomainModel model = null;
        FileReader reader = new FileReader(serviceModelFile);
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
                LOG.debug("Class " + packageName + "." + clazz.getClassName() + " targetable? " + clazz.isAllowableAsTarget());
            }
            ModelClass[] mappingArray = new ModelClass[modelClasses.size()];
            modelClasses.toArray(mappingArray);
            pack.setModelClass(mappingArray);
            modelPackages.add(pack);
        }
        ModelPackage[] packageArray = new ModelPackage[modelPackages.size()];
        modelPackages.toArray(packageArray);
        modelInfo.setModelPackage(packageArray);
        storeModelInformation(modelInfo);
    }
    
    
    private void storeModelInformation(ModelInformation modelInfo) throws Exception {
        // set the cadsr info on the extension data model
        Data data = getExtensionData();
        data.setModelInformation(modelInfo);
        storeExtensionData(data);
    }
    
    
    public void setModelSource(DomainModelConfigurationSource source) {
        this.configurationSource = source;
    }
    
    
    public DomainModelConfigurationSource getCurrentModelSource() {
        return configurationSource;
    }
    
    
    public void setDomainModelLocalFile(File file) {
        this.domainModelFile = file;
    }
    
    
    public void setModelFromConfigInformation(String shortName, String version) {
        this.projectShortName = shortName;
        this.projectVersion = version;
    }
    

    public String getProjectShortName() {
        return projectShortName;
    }


    public void setProjectShortName(String projectShortName) {
        this.projectShortName = projectShortName;
    }


    public String getProjectVersion() {
        return projectVersion;
    }


    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }
        
    
    public String getExcludePackages() {
        return excludePackages;
    }
    
    
    public void setExcludePackages(String excludePackages) {
        this.excludePackages = excludePackages;
    }


    public File getXmiFile() {
        return xmiFile;
    }


    public void setXmiFile(File xmiFile) {
        this.xmiFile = xmiFile;
    }


    public HandlerEnum getXmiType() {
        return xmiType;
    }


    public void setXmiType(HandlerEnum xmiType) {
        this.xmiType = xmiType;
    }
    
    
    public enum DomainModelConfigurationSource {
        XMI, FILE_SYSTEM
    }
}
