package gov.nih.nci.cagrid.data.upgrades;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.upgrade.common.ExtensionUpgradeStatus;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.cadsr.UMLModelService;
import org.cagrid.cadsr.client.CaDSRUMLModelService;
import org.cagrid.mms.domain.UMLProjectIdentifer;
import org.jdom.Element;

public class ModelInformationConverter {
    
    public static final QName MODEL_INFO_QNAME = new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.data.extension", "ModelInformation");
    
    private ModelInformationConverter() {
        // no
    }

    
    public static Element convertModelInformation(
        ServiceInformation serviceInfo, ExtensionUpgradeStatus status, Element cadsrInformationElement) 
        throws UpgradeException {
        // get started
        ModelInformation modelInfo = new ModelInformation();
        
        // determine model source
        if (isNoDomainModel(cadsrInformationElement)) {
            modelInfo.setSource(ModelSourceType.none);
        } else if (isSuppliedModel(cadsrInformationElement)) {
            modelInfo.setSource(ModelSourceType.preBuilt);
        } else if (cadsrInformationElement.getAttribute("serviceUrl") != null) {
            modelInfo.setSource(ModelSourceType.mms);
        } else {
            modelInfo.setSource(ModelSourceType.none);
        }
        status.addDescriptionLine("Determined domain model source to be " + modelInfo.getSource().getValue());
        
        // determine project identifier
        UMLProjectIdentifer projectIdentifier = null;
        if (ModelSourceType.mms.equals(modelInfo.getSource())) {
            projectIdentifier = getCadsrProjectIdentifier(cadsrInformationElement);
            status.addDescriptionLine("UML Project Identifier derived from caDSR");
        } else if (ModelSourceType.preBuilt.equals(modelInfo.getSource())) {
            projectIdentifier = new UMLProjectIdentifer();
            // get the domain model itself and figure out the identifier
            DomainModel domainModel = null;
            try {
                domainModel = getDomainModel(serviceInfo);
            } catch (Exception ex) {
                throw new UpgradeException("Error reading domain model file: " + ex.getMessage(), ex);
            }
            projectIdentifier.setIdentifier(domainModel.getProjectShortName());
            projectIdentifier.setVersion(domainModel.getProjectVersion());
            status.addDescriptionLine("UML Project Identifier derived from existing domain model");
        }
        modelInfo.setUMLProjectIdentifer(projectIdentifier);
        
        // convert packages
        Iterator packageElementIterator = cadsrInformationElement.getChildren(
            "Packages", cadsrInformationElement.getNamespace()).iterator();
        List<ModelPackage> modelPackages = new LinkedList<ModelPackage>();
        while (packageElementIterator.hasNext()) {
            Element packageElement = (Element) packageElementIterator.next();
            String packageName = packageElement.getAttributeValue("name");
            ModelPackage pack = new ModelPackage();
            pack.setPackageName(packageName);
            // convert classes
            Iterator classElementIterator = packageElement.getChildren(
                "CadsrClass", packageElement.getNamespace()).iterator();
            List<ModelClass> modelClasses = new LinkedList<ModelClass>();
            while (classElementIterator.hasNext()) {
                Element classElement = (Element) classElementIterator.next();
                String shortClassName = classElement.getAttributeValue("className");
                boolean isTargetable = Boolean.parseBoolean(classElement.getAttributeValue("targetable"));
                boolean isSelected = Boolean.parseBoolean(classElement.getAttributeValue("selected"));
                ModelClass clazz = new ModelClass();
                clazz.setShortClassName(shortClassName);
                clazz.setTargetable(isTargetable);
                clazz.setSelected(isSelected);
                modelClasses.add(clazz);
            }
            ModelClass[] classArray = new ModelClass[modelClasses.size()];
            modelClasses.toArray(classArray);
            pack.setModelClass(classArray);
        }
        ModelPackage[] packageArray = new ModelPackage[modelPackages.size()];
        modelPackages.toArray(packageArray);
        modelInfo.setModelPackage(packageArray);
        status.addDescriptionLine("Converted old style CadsrInformation extension data to new ModelInformation");
        
        Element modelInfoElement = null;
        try {
            // serialize the model information
            StringWriter writer = new StringWriter();
            Utils.serializeObject(modelInfo, MODEL_INFO_QNAME, writer);
            String xmlText = writer.getBuffer().toString();
            // convert to JDom element
            modelInfoElement = XMLUtilities.stringToDocument(xmlText).getRootElement();
        } catch (Exception ex) {
            throw new UpgradeException("Error serializing model information: " + ex.getMessage(), ex);
        }
        
        return modelInfoElement;
    }
    
    
    private static boolean isNoDomainModel(Element cadsrInformation) {
        String value = cadsrInformation.getAttributeValue("noDomainModel");
        return value != null && Boolean.parseBoolean(value);
    }
    
    
    private static boolean isSuppliedModel(Element cadsrInformation) {
        String value = cadsrInformation.getAttributeValue("useSuppliedModel");
        return value != null && Boolean.parseBoolean(value);
    }
    
    
    private static DomainModel getDomainModel(ServiceInformation serviceInfo) throws Exception {
        DomainModel model = null;
        // get resource properties of the main service (the data service itself)
        ResourcePropertyType[] resourceProperties = 
            serviceInfo.getServiceDescriptor().getServices().getService(0)
                .getResourcePropertiesList().getResourceProperty();
        for (ResourcePropertyType rp : resourceProperties) {
            if (rp.getQName().equals(DataServiceConstants.DOMAIN_MODEL_QNAME)) {
                String filename = rp.getFileLocation();
                if (filename != null) {
                    // get the domain model file
                    File modelFile = new File(serviceInfo.getBaseDirectory(), "etc" + File.separator + filename);
                    FileReader reader = new FileReader(modelFile);
                    model = MetadataUtils.deserializeDomainModel(reader);
                    reader.close();
                    break;
                }
            }
        }
        return model;
    }
    
    
    private static UMLProjectIdentifer getCadsrProjectIdentifier(Element cadsrInfo) throws UpgradeException {
        String projectLongName = cadsrInfo.getAttributeValue("projectLongName");
        String projectVersion = cadsrInfo.getAttributeValue("projectVersion");
        String cadsrUrl = cadsrInfo.getAttributeValue("serviceUrl");
        Project cadsrProject = null;
        try {
            UMLModelService cadsrService = new CaDSRUMLModelService(cadsrUrl);
            Project[] allProjects = cadsrService.findAllProjects();
            for (Project proj : allProjects) {
                if (projectLongName.equals(proj.getLongName()) && projectVersion.equals(proj.getVersion())) {
                    cadsrProject = proj;
                    break;
                }
            }
        } catch (Exception ex) {
            throw new UpgradeException("Error contacting caDSR for project information: " + ex.getMessage(), ex);
        }
        if (cadsrProject == null) {
            throw new UpgradeException("No project (" + projectLongName 
                + " (" + projectVersion + ") found in caDSR " + cadsrUrl);
        }
        UMLProjectIdentifer identifier = new UMLProjectIdentifer();
        identifier.setIdentifier(cadsrProject.getShortName());
        identifier.setVersion(cadsrProject.getVersion());
        identifier.setSourceIdentifier(cadsrUrl);
        return identifier;
    }
}
