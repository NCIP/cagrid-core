package gov.nih.nci.cagrid.data.style.sdkstyle.helpers;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.common.ModelInformationUtil;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ModelInformation;
import gov.nih.nci.cagrid.data.extension.ModelPackage;
import gov.nih.nci.cagrid.data.style.StyleCodegenPostProcessor;
import gov.nih.nci.cagrid.data.utilities.WsddUtil;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * PostCodegenHelper 
 * Post-processor for the SDK 3.1 data service style
 * 
 * @author David Ervin
 * 
 * @created Jul 10, 2007 2:24:14 PM
 * @version $Id: PostCodegenHelper.java,v 1.5 2009-04-09 16:28:01 dervin Exp $
 */
public class PostCodegenHelper implements StyleCodegenPostProcessor {
    
    private static final Log LOG = LogFactory.getLog(PostCodegenHelper.class);

    public void codegenPostProcessStyle(ServiceExtensionDescriptionType desc, ServiceInformation info) throws Exception {
        Data extensionData = getExtensionData(desc, info);
        rebuildCastorMappings(extensionData, info);
    }


    private Data getExtensionData(ServiceExtensionDescriptionType desc, ServiceInformation info)
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


    private void rebuildCastorMappings(Data extensionData, ServiceInformation info) throws CodegenExtensionException {
        ModelInformationUtil modelInfoUtil = new ModelInformationUtil(info.getServiceDescriptor());
        // generate a list of castor mapping files we *might* have and need to edit
        List<File> mappingFiles = new LinkedList<File>();
        mappingFiles.add(new File(CastorMappingUtil.getMarshallingCastorMappingFileName(info)));
        mappingFiles.add(new File(CastorMappingUtil.getEditedMarshallingCastorMappingFileName(info)));
        mappingFiles.add(new File(CastorMappingUtil.getUnmarshallingCastorMappingFileName(info)));
        mappingFiles.add(new File(CastorMappingUtil.getEditedUnmarshallingCastorMappingFileName(info)));
        // read each mapping file and fix up the namespaces
        for (File mappingFile : mappingFiles) {
            if (mappingFile.exists()) {
                // read in the file
                LOG.debug("Editing namespaces in Castor mapping file " + mappingFile.getAbsolutePath());
                String mappingText = null;
                try {
                    mappingText = Utils.fileToStringBuffer(mappingFile).toString();
                } catch (Exception ex) {
                    throw new CodegenExtensionException("Error reading castor mapping file: " + ex.getMessage(), ex);
                }
                // for each package in the extension data, fix the namespace mapping
                ModelInformation modelInfo = extensionData.getModelInformation();
                if (modelInfo != null) {
                    ModelPackage[] packages = modelInfo.getModelPackage();
                    try {
                        for (int i = 0; packages != null && i < packages.length; i++) {
                            String packName = packages[i].getPackageName();
                            NamespaceType mappedNamespace = modelInfoUtil.getMappedNamespace(packName);
                            mappingText = CastorMappingUtil.changeNamespaceOfPackage(
                                mappingText, packages[i].getPackageName(),
                                mappedNamespace.getNamespace());
                        }
                    } catch (Exception ex) {
                        throw new CodegenExtensionException("Error changing namespaces in castor mapping: " 
                            + ex.getMessage(), ex);
                    }
                }
                // write the mapping back out to disk
                try {
                    Utils.stringBufferToFile(new StringBuffer(mappingText), mappingFile.getAbsolutePath());
                } catch (Exception ex) {
                    throw new CodegenExtensionException("Error saving castor mapping " + mappingFile.getAbsolutePath() 
                        + " to disk: " + ex.getMessage(), ex);
                }
            } else if (mappingFile.getName().equals(CastorMappingUtil.CASTOR_MARSHALLING_MAPPING_FILE)) {
                // this one is required!
                throw new CodegenExtensionException("Castor mapping file " + mappingFile.getAbsolutePath() + " was not found!");
            }
        }
        
        // change the castor mapping property in the client-config.wsdd and
        // the server-config.wsdd files.
        String mainServiceName = info.getIntroduceServiceProperties().getProperty(
            IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
        ServiceType mainService = CommonTools.getService(info.getServices(), mainServiceName);
        String servicePackageName = mainService.getPackageName();
        String packageDir = servicePackageName.replace('.', File.separatorChar);
        // find the client source directory, where the client-config will be
        // located
        File clientConfigFile = new File(info.getBaseDirectory().getAbsolutePath() + File.separator + "src"
            + File.separator + packageDir + File.separator + "client" + File.separator + "client-config.wsdd");
        if (!clientConfigFile.exists()) {
            throw new CodegenExtensionException("Client config file " + clientConfigFile.getAbsolutePath()
                + " not found!");
        }
        // fine the server-config.wsdd, located in the service's root directory
        File serverConfigFile = new File(info.getBaseDirectory().getAbsolutePath() + File.separator
            + "server-config.wsdd");
        if (!serverConfigFile.exists()) {
            throw new CodegenExtensionException("Server config file " + serverConfigFile.getAbsolutePath()
                + " not found!");
        }

        // edit the client-config.wsdd file
        try {
            WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
                DataServiceConstants.CASTOR_MAPPING_WSDD_PARAMETER, CastorMappingUtil.getMarshallingCastorMappingName(info));
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error setting castor mapping parameter in client-config.wsdd: "
                + ex.getMessage(), ex);
        }
        // edit the server-config.wsdd file
        try {
            WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(),
                info.getServices().getService(0).getName(), DataServiceConstants.CASTOR_MAPPING_WSDD_PARAMETER,
                CastorMappingUtil.getMarshallingCastorMappingName(info));
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error setting castor mapping parameter in server-config.wsdd: "
                + ex.getMessage(), ex);
        }
    }
}
