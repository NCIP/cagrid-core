package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelClass;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.extension.ModelSourceType;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/** 
 *  BaseCodegenPostProcessorExtension
 *  Base class for the DS codegen post processor extension
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jun 16, 2006 
 * @version $Id: BaseCodegenPostProcessorExtension.java,v 1.5 2009-01-13 15:55:19 dervin Exp $ 
 */
public abstract class BaseCodegenPostProcessorExtension implements CodegenExtensionPostProcessor {
	private static final Log logger = LogFactory.getLog(DataServiceOperationProviderCodegenPostProcessor.class);
    
    public void postCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CodegenExtensionException {
        modifyEclipseClasspath(desc, info);
        generateClassToQnameMapping(getExtensionData(desc, info), info);
        writeOutAuditorConfiguration(getExtensionData(desc, info), info);
        performCodegenProcess(desc, info);
    }
    
    
    protected abstract void performCodegenProcess(ServiceExtensionDescriptionType desc, ServiceInformation info) throws CodegenExtensionException;
    

	protected void modifyEclipseClasspath(ServiceExtensionDescriptionType desc, ServiceInformation info) throws CodegenExtensionException {
		// get the eclipse classpath document
		File classpathFile = new File(info.getBaseDirectory(), ".classpath");
		if (classpathFile.exists()) {
			logger.info("Modifying eclipse .classpath file");
			Set<File> libs = new HashSet<File>();
			ExtensionTypeExtensionData data = ExtensionTools.getExtensionData(desc, info);
			AdditionalLibraries additionalLibs = null;
			try {
				additionalLibs = ExtensionDataUtils.getExtensionData(data).getAdditionalLibraries();
			} catch (Exception ex) {
				throw new CodegenExtensionException("Error retrieving extension data");
			}
			if (additionalLibs != null && additionalLibs.getJarName() != null) {
				for (int i = 0; i < additionalLibs.getJarName().length; i++) {
					String jarFilename = additionalLibs.getJarName(i);
					libs.add(new File(info.getBaseDirectory(), "lib" + File.separator + jarFilename));
				}
			}
			File[] libFiles = new File[libs.size()];
			libs.toArray(libFiles);
			try {
				logger.info("Adding libraries to classpath file:");
				for (int i = 0; i < libFiles.length; i++) {
					logger.info("\t" + libFiles[i].getAbsolutePath());
				}
				ExtensionUtilities.syncEclipseClasspath(classpathFile, libFiles);
			} catch (Exception ex) {
				throw new CodegenExtensionException("Error modifying Eclipse .classpath file: " + ex.getMessage(), ex);
			}
		} else {
			logger.warn("Eclipse .classpath file " + classpathFile.getAbsolutePath() + " not found!");
		}
	}
	
	
	protected void generateClassToQnameMapping(Data extData, ServiceInformation info)
		throws CodegenExtensionException {
		try {
            Mappings mappings = new Mappings();
            List<ClassToQname> classMappings = new LinkedList<ClassToQname>();
            // the first placeto look for mappings is in the model information, which 
            // is derived from the data service Domain Model.  If no domain model is to be used,
            // the mappings are still required to do anything with caCORE SDK beans, or BDT in general
            ModelInformation modelInfo = extData.getModelInformation();
            if (modelInfo != null && !ModelSourceType.none.equals(modelInfo.getSource())
                && modelInfo.getModelPackage() != null) {
                logger.debug("Model information / domain model found in service model.");
                logger.debug("Generating class to qname mapping from the information");
                logger.debug("stored in the service model");
                // walk packages in the model
                for (ModelPackage pack : modelInfo.getModelPackage()) {
                    // locate a NamsepaceType in the service model mapped to this package
                    NamespaceType mappedNamespace = null;
                    for (NamespaceType nsType : info.getServiceDescriptor().getNamespaces().getNamespace()) {
                        if (pack.getPackageName().equals(nsType.getPackageName())) {
                            mappedNamespace = nsType;
                            break;
                        }
                    }
                    if (mappedNamespace != null && pack.getModelClass() != null) {
                        // walk classes in this package, if any exist
                        for (ModelClass clazz : pack.getModelClass()) {
                            if (clazz.isTargetable()) {
                                // find a schema element type mapped to this class
                                for (SchemaElementType element : mappedNamespace.getSchemaElement()) {
                                    if (clazz.getShortClassName().equals(element.getClassName())) {
                                        // found it!  Create a mapping
                                        QName qname = new QName(mappedNamespace.getNamespace(), element.getType());
                                        String fullClassname = pack.getPackageName() + "." + clazz.getShortClassName();
                                        ClassToQname map = new ClassToQname(fullClassname, qname.toString());
                                        classMappings.add(map);
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        logger.warn("Model package " + pack.getPackageName() + " is not mapped to any namespace!");
                    }
                }
                ClassToQname[] mapArray = new ClassToQname[classMappings.size()];
                classMappings.toArray(mapArray);
                mappings.setMapping(mapArray);
            } else {
                logger.warn("No model information / domain model found in service model.");
                logger.warn("Falling back to schema information for class to qname mapping.");
                NamespaceType[] namespaces = info.getNamespaces().getNamespace();
                // a set of namespaces to ignore
                Set<String> nsIgnores = new HashSet<String>();
                nsIgnores.add(IntroduceConstants.W3CNAMESPACE);
                nsIgnores.add(info.getServices().getService(0).getNamespace());
                nsIgnores.add(DataServiceConstants.BDT_DATA_SERVICE_NAMESPACE);
                nsIgnores.add(DataServiceConstants.ENUMERATION_DATA_SERVICE_NAMESPACE);
                nsIgnores.add(DataServiceConstants.DATA_SERVICE_NAMESPACE);
                nsIgnores.add(DataServiceConstants.CQL_QUERY_URI);
                nsIgnores.add(DataServiceConstants.CQL_RESULT_SET_URI);
                
                for (int nsIndex = 0; nsIndex < namespaces.length; nsIndex++) {
                    NamespaceType currentNs = namespaces[nsIndex];
                    // ignore any unneeded namespaces
                    if (!nsIgnores.contains(currentNs.getNamespace())) {
                        SchemaElementType[] schemaElements = currentNs.getSchemaElement();
                        for (int elemIndex = 0; schemaElements != null && elemIndex < schemaElements.length; elemIndex++) {
                            SchemaElementType currentElement = schemaElements[elemIndex];
                            ClassToQname toQname = new ClassToQname();
                            QName qname = new QName(currentNs.getNamespace(), currentElement.getType());
                            String shortClassName = currentElement.getClassName();
                            if (shortClassName == null) { 
                                shortClassName = currentElement.getType();
                            }
                            String fullClassName = currentNs.getPackageName() + "." + shortClassName;
                            toQname.setQname(qname.toString());
                            toQname.setClassName(fullClassName);
                            classMappings.add(toQname);
                        }
                    }
                }
            }
            
            ClassToQname[] mapArray = new ClassToQname[classMappings.size()];
            classMappings.toArray(mapArray);
            mappings.setMapping(mapArray);
            // create the filename where the mapping will be stored
            String mappingFilename = new File(info.getBaseDirectory(), 
                "etc" + File.separator + DataServiceConstants.CLASS_TO_QNAME_XML).getAbsolutePath();
            // serialize the mapping to that file
            Utils.serializeDocument(mappingFilename, mappings, DataServiceConstants.MAPPING_QNAME);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CodegenExtensionException(
                "Error generating class to QName mapping: " + ex.getMessage(), ex);
		}		
	}
    
    
    protected void writeOutAuditorConfiguration(Data extData, ServiceInformation serviceInfo) throws CodegenExtensionException {
        if (CommonTools.servicePropertyExists(
            serviceInfo.getServiceDescriptor(), 
            DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY)) {
            try {
                // get the name of the auditor config file
                String filename = CommonTools.getServicePropertyValue(
                    serviceInfo.getServiceDescriptor(), 
                    DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY);
                if (extData.getDataServiceAuditors() != null) {
                    File outFile = new File(serviceInfo.getBaseDirectory().getAbsolutePath() 
                        + File.separator + "etc" + File.separator + filename);
                    FileWriter writer = new FileWriter(outFile);
                    Utils.serializeObject(extData.getDataServiceAuditors(), 
                        DataServiceConstants.DATA_SERVICE_AUDITORS_QNAME, writer);
                    writer.flush();
                    writer.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CodegenExtensionException(
                    "Error writing auditor configuration: " + ex.getMessage(), ex);
            }
        }
    }
    
    
    protected Data getExtensionData(ServiceExtensionDescriptionType desc, ServiceInformation info) 
        throws CodegenExtensionException {
        ExtensionTypeExtensionData extData = ExtensionTools.getExtensionData(desc, info);
        Data data = null;
        try {
            data = ExtensionDataUtils.getExtensionData(extData);
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error getting extension data: " + ex.getMessage(), ex);
        }       
        return data;
    }
}
