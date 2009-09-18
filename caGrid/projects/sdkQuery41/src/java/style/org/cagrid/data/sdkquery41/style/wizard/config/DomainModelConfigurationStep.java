package org.cagrid.data.sdkquery41.style.wizard.config;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cadsr.umlproject.domain.UMLClassMetadata;
import gov.nih.nci.cadsr.umlproject.domain.UMLPackageMetadata;
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
import gov.nih.nci.cagrid.metadata.xmi.XMIParser;
import gov.nih.nci.cagrid.metadata.xmi.XmiFileType;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cagrid.cadsr.UMLModelService;
import org.cagrid.cadsr.client.CaDSRUMLModelService;
import org.cagrid.data.sdkquery41.style.common.SDK41StyleConstants;
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
    
    private DomainModelConfigurationSource configurationSource = null;
    
    // from local file system
    private File domainModelFile = null;
    
    // from SDK's config
    private String projectShortName = null;
    private String projectVersion = null;
    private File xmiFile = null;
    private XmiFileType xmiType = null;
    
    // from cadsr
    // TODO: from mms??
    private String cadsrUrl = null;
    private Project selectedProject = null;
    private List<UMLPackageMetadata> selectedPackages = null;

    public DomainModelConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
        selectedPackages = new ArrayList<UMLPackageMetadata>();
    }


    public void applyConfiguration() throws Exception {
        if (configurationSource == null) {
            throw new IllegalStateException(
                "The domain model configuration source has not been defined!!!");
        }
        switch (configurationSource) {
            case CADSR:
                applyCadsrModelConfiguration();
                break;
            case SDK_CONFIG_XMI:
                applySdkModelConfiguration();
                break;
            case FILE_SYSTEM:
                applyFileSystemModelConfiguration();
                break;
            default:
                throw new IllegalStateException("Unknown model configuration source");
        }
    }
    
    
    private void applyCadsrModelConfiguration() throws Exception {
        ModelInformation modelInfo = new ModelInformation();
        // default to model from mms
        modelInfo.setSource(ModelSourceType.mms);
        UMLProjectIdentifer id = new UMLProjectIdentifer();
        id.setIdentifier(selectedProject.getShortName());
        id.setVersion(selectedProject.getVersion());
        modelInfo.setUMLProjectIdentifer(id);
        
        // packages
        UMLModelService cadsrClient = new CaDSRUMLModelService(cadsrUrl);
        ModelPackage[] packages = new ModelPackage[selectedPackages.size()];
        int index = 0;
        for (UMLPackageMetadata umlPackage : selectedPackages) {
            ModelPackage pack = new ModelPackage();
            pack.setPackageName(umlPackage.getName());
            UMLClassMetadata[] classMetadata = cadsrClient.findClassesInPackage(
                selectedProject, umlPackage.getName());
            ModelClass[] classes = new ModelClass[classMetadata.length];
            for (int j = 0; j < classMetadata.length; j++) {
                ModelClass clazz = new ModelClass();
                // NOT setting element name until schema mapping panel
                clazz.setShortClassName(classMetadata[j].getName());
                clazz.setSelected(true);
                clazz.setTargetable(true);
                classes[j] = clazz;
            }
            pack.setModelClass(classes);
            packages[index] = pack;
            index++;
        }
        modelInfo.setModelPackage(packages);
        storeModelInformation(modelInfo);
    }
    
    
    private void applySdkModelConfiguration() throws Exception {
        // create a domain model from the XMI
        XMIParser parser = new XMIParser(projectShortName, projectVersion);
        DomainModel generatedModel = parser.parse(xmiFile, xmiType);
        // save the domain model to the service's etc dir
        File etcDir = new File(
            SharedConfiguration.getInstance().getServiceInfo().getBaseDirectory(), "etc");
        String applicationName = SharedConfiguration.getInstance()
            .getSdkDeployProperties().getProperty(
                SDK41StyleConstants.DeployProperties.PROJECT_NAME);
        File generatedModelFile = new File(etcDir, applicationName + "_domainModel.xml");
        FileWriter modelWriter = new FileWriter(generatedModelFile);
        MetadataUtils.serializeDomainModel(generatedModel, modelWriter);
        modelWriter.flush();
        modelWriter.close();
        
        // get / create the domain model resource property
        ServiceInformation serviceInfo = SharedConfiguration.getInstance().getServiceInfo();
        ResourcePropertyType domainModelResourceProperty = null;
        ResourcePropertyType[] domainModelProps = CommonTools.getResourcePropertiesOfType(
            serviceInfo.getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
        if (domainModelProps.length != 0) {
            domainModelResourceProperty = domainModelProps[0];
            // if old file exists, delete it
            File oldFile = new File(
                etcDir, domainModelResourceProperty.getFileLocation());
            if (oldFile.exists()) {
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
                serviceInfo.getServices().getService(0), domainModelResourceProperty);
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
                modelClass.setTargetable(true);
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
            SharedConfiguration.getInstance().getServiceInfo().getBaseDirectory(), "etc");
        String applicationName = SharedConfiguration.getInstance()
            .getSdkDeployProperties().getProperty(
                SDK41StyleConstants.DeployProperties.PROJECT_NAME);
        File serviceModelFile = new File(etcDir, applicationName + "_domainModel.xml");
        Utils.copyFile(domainModelFile, serviceModelFile);
        
        // get / create the domain model resource property
        ServiceInformation serviceInfo = SharedConfiguration.getInstance().getServiceInfo();
        ResourcePropertyType domainModelResourceProperty = null;
        ResourcePropertyType[] domainModelProps = CommonTools.getResourcePropertiesOfType(
            serviceInfo.getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
        if (domainModelProps.length != 0) {
            domainModelResourceProperty = domainModelProps[0];
            // if old file exists, delete it
            File oldFile = new File(
                etcDir, domainModelResourceProperty.getFileLocation());
            if (oldFile.exists()) {
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
                serviceInfo.getServices().getService(0), domainModelResourceProperty);
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
        // TODO: mms identifier
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
                modelClass.setTargetable(true);
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
    
    
    public void setCadsrUrl(String url) {
        this.cadsrUrl = url;
    }
    
    
    public String getCadsrUrl() {
        return this.cadsrUrl;
    }
    
    
    public void setCadsrProject(Project proj) {
        this.selectedProject = proj;
    }
    
    
    public void addCadsrPackage(UMLPackageMetadata pack) {
        for (UMLPackageMetadata p : selectedPackages) {
            if (p.getName().equals(pack)) {
                throw new IllegalStateException("Package " + p.getName() + " is already selected");
            }
        }
        this.selectedPackages.add(pack);
    }
    
    
    public boolean removeCadsrPackage(String name) {
        Iterator<UMLPackageMetadata> iter = selectedPackages.iterator();
        while (iter.hasNext()) {
            UMLPackageMetadata pack = iter.next();
            if (name.equals(pack.getName())) {
                iter.remove();
                return true;
            }
        }
        return false;
    }
    
    
    public List<UMLPackageMetadata> getCadsrPacakges() {
        return this.selectedPackages;
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


    public File getXmiFile() {
        return xmiFile;
    }


    public void setXmiFile(File xmiFile) {
        this.xmiFile = xmiFile;
    }


    public XmiFileType getXmiType() {
        return xmiType;
    }


    public void setXmiType(XmiFileType xmiType) {
        this.xmiType = xmiType;
    }
    
    
    public enum DomainModelConfigurationSource {
        CADSR, SDK_CONFIG_XMI, FILE_SYSTEM
    }
}
