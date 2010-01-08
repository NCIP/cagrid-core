package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.MetadataConstants;
import gov.nih.nci.cagrid.data.codegen.templates.StubCQLQueryProcessorTemplate;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleCodegenPreProcessor;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * DataServiceCodegenPreProcessor 
 * Preprocessor for data service codegen
 * operations.
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created May 11, 2006
 * @version $Id: DataServiceCodegenPreProcessor.java,v 1.7 2009-05-29 19:57:47 dervin Exp $
 */
public class DataServiceCodegenPreProcessor implements CodegenExtensionPreProcessor {
    
	private static final Log LOG = LogFactory.getLog(DataServiceCodegenPreProcessor.class);


	public void preCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
		throws CodegenExtensionException {
        // check for and potentially create the stub query processor java file
        if (!stubQueryProcessorExists(info)) {
            createStubQueryProcessor(info);
        }
        
		addDomainModelResourceProperty(desc, info);
        
		addQueryLanguageSupportResourceProperty(info);
		
        // execute the service style's pre codegen processor
        ExtensionTypeExtensionData extData = ExtensionTools.getExtensionData(desc, info);
        Data data = null;
        try {
            data = ExtensionDataUtils.getExtensionData(extData);
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error getting extension data: " + ex.getMessage(), ex);
        }
        ServiceFeatures features = data.getServiceFeatures();
        if (features != null && features.getServiceStyle() != null) {
            try {
                ServiceStyleContainer container = ServiceStyleLoader.getStyle(features.getServiceStyle().getName());
                if (container == null) {
                    throw new CodegenExtensionException(
                        "Could not load service style " + features.getServiceStyle());
                }
                StyleCodegenPreProcessor preProcessor = container.loadCodegenPreProcessor();
                if (preProcessor != null) {
                    preProcessor.codegenPreProcessStyle(desc, info);
                }
            } catch (Exception ex) {
                throw new CodegenExtensionException(
                    "Error executing style codegen post processor: " + ex.getMessage(), ex);
            }
        }
	}
    
    
    private boolean stubQueryProcessorExists(ServiceInformation info) {
        String stubName = ExtensionDataUtils.getQueryProcessorStubClassName(info);
        File stubJavaFile = new File(info.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + stubName.replace('.', File.separatorChar) + ".java");
        return stubJavaFile.exists();
    }
    
    
    private void createStubQueryProcessor(ServiceInformation info) throws CodegenExtensionException {
        String stubName = ExtensionDataUtils.getQueryProcessorStubClassName(info);
        File stubJavaFile = new File(info.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + stubName.replace('.', File.separatorChar) + ".java");
        stubJavaFile.getParentFile().mkdirs();
        StubCQLQueryProcessorTemplate stubTemplate = new StubCQLQueryProcessorTemplate();
        String stubJavaCode = stubTemplate.generate(info);
        try {
            FileWriter writer = new FileWriter(stubJavaFile);
            writer.write(stubJavaCode);
            writer.close();
        } catch (IOException ex) {
            throw new CodegenExtensionException("Error creating stub query processor: " 
                + ex.getMessage(), ex);
        }
    }
	
	
	private ModelInformation getModelInformation(
        ServiceExtensionDescriptionType desc, ServiceInformation info) throws Exception {
		ExtensionTypeExtensionData extData = ExtensionTools.getExtensionData(desc, info);
		Data data = ExtensionDataUtils.getExtensionData(extData);
		ModelInformation modelInfo = data.getModelInformation();
		if (modelInfo == null) {
            LOG.warn("NO MODEL INFORMATION FOUND, USING DEFAULTS");
			modelInfo = new ModelInformation();
            modelInfo.setSource(ModelSourceType.none);
			data.setModelInformation(modelInfo);
			ExtensionDataUtils.storeExtensionData(extData, data);
		}
		return modelInfo;
	}


	private void addDomainModelResourceProperty(ServiceExtensionDescriptionType desc, ServiceInformation info)
		throws CodegenExtensionException {
        // determine the name for the domain model document
        String localDomainModelFilename = getDestinationDomainModelFilename(info);
        
        // find the service's etc directory, where the domain model goes on the file system
        String domainModelFile = new File(info.getBaseDirectory(), 
            "etc" + File.separator + localDomainModelFilename).getAbsolutePath();
        
		// get the model information
		ModelInformation modelInfo = null;
		try {
			modelInfo = getModelInformation(desc, info);
		} catch (Exception ex) {
			throw new CodegenExtensionException("Error loading Model Information from extension data", ex);
		}
		
		// get the resource property for the domain model
		ResourcePropertyType dmResourceProp = getDomainModelResourceProp(info);
        
        LOG.debug("Domain model source determined to be " + modelInfo.getSource());
		if (ModelSourceType.preBuilt.equals(modelInfo.getSource())) {
			// the model is already in the service's etc dir with the name specified
			// in the resource property.  Make sure the resource property has
			// the filename and populate from file flag set
			dmResourceProp.setPopulateFromFile(true);
            dmResourceProp.setFileLocation(localDomainModelFilename);
		} else if (ModelSourceType.mms.equals(modelInfo.getSource())) {
            // the domain model is to be generated from the MMS
            LOG.info("Generating domain model from MMS");
            
            // set the domain model file name
            dmResourceProp.setFileLocation(localDomainModelFilename);
            
            // generate the domain model document
			generateDomainModel(modelInfo, info, domainModelFile);
		}

		// if the domain model XML fiel doesn't exist, don't try to 
        // populate the domain model metadata on service startup
		File dmFile = new File(domainModelFile);
		dmResourceProp.setPopulateFromFile(dmFile.exists());
	}


	/**
	 * Gets the local part of the filename from which the Domain Model resource
	 * property (metadata) will be populated at runtime.  This value is configured
	 * by the user when selecting a pre-built domain model in the data service
	 * creation GUI in Introduce, or the default (domainModel.xml) value
	 * if a domain model is to be generated from the caDSR
	 * 
	 * @param info
	 * 		The service information
	 * @return
	 * 		The local domain model xml file name
	 */
	private String getDestinationDomainModelFilename(ServiceInformation info) {
        ResourcePropertyType domainModelResourceProperty = getDomainModelResourceProp(info);
        String filename = domainModelResourceProperty.getFileLocation();
        if (filename == null || filename.trim().length() == 0) {
            filename = "domainModel.xml";
        }
        return filename;
	}


	private void generateDomainModel(ModelInformation modelInfo, 
        ServiceInformation info, String domainModelFile) throws CodegenExtensionException {		
		if (modelInfo != null) {
			DomainModel model = null;
            try {
                model = DomainModelCreationUtil.createDomainModel(modelInfo);
            } catch (Exception ex) {
                throw new CodegenExtensionException("Error creating domain model: " + ex.getMessage(), ex);
            }
            
            System.out.println("Created data service Domain Model");
            LOG.info("Created data service Domain Model");
            
			// get the data service's description
            ServiceType dataService = null;
            String serviceName = info.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
            ServiceType[] services = info.getServices().getService();
            for (int i = 0; i < services.length; i++) {
                if (services[i].getName().equals(serviceName)) {
                    dataService = services[i];
                    break;
                }
            }
            if (dataService == null) {
                // this REALLY should never happen...
                throw new CodegenExtensionException("No data service found in service information!!");
            }

			LOG.debug("Serializing domain model to file " + domainModelFile);
			try {
				FileWriter domainModelFileWriter = new FileWriter(domainModelFile);
				MetadataUtils.serializeDomainModel(model, domainModelFileWriter);
				domainModelFileWriter.flush();
				domainModelFileWriter.close();
				LOG.debug("Serialized domain model to file " + domainModelFile);
			} catch (Exception ex) {
				throw new CodegenExtensionException("Error serializing the domain model to disk: " 
					+ ex.getMessage(), ex);
			}
		}
	}
	
	
	private ResourcePropertyType getDomainModelResourceProp(ServiceInformation info) {
		ServiceType baseService = info.getServices().getService(0);
		
		ResourcePropertyType[] typedProps = CommonTools.getResourcePropertiesOfType(
			info.getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
		if (typedProps == null || typedProps.length == 0) {
			ResourcePropertyType dmProp = new ResourcePropertyType();
			dmProp.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
			dmProp.setRegister(true);
			CommonTools.addResourcePropety(baseService, dmProp);
            LOG.debug("Created new resource property for domain model");
			return dmProp;
		} else {
            LOG.debug("Found existing domain model resource property");
			return typedProps[0];
		}
	}
	
	
	private void addQueryLanguageSupportResourceProperty(ServiceInformation info) {
	    ServiceType baseService = info.getServices().getService(0);
	    
	    if (CommonTools.getResourcePropertiesOfType(baseService, MetadataConstants.QUERY_LANGUAGE_SUPPORT_QNAME).length == 0) {
	        ResourcePropertyType supportRp = new ResourcePropertyType();
	        supportRp.setRegister(true);
	        supportRp.setDescription(MetadataConstants.QUERY_LANGUAGE_SUPPORT_DESCRIPTION);
	        supportRp.setQName(MetadataConstants.QUERY_LANGUAGE_SUPPORT_QNAME);
	        CommonTools.addResourcePropety(baseService, supportRp);
	    }
	}
}
