package org.cagrid.data.sdkquery42.style.helpers;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.style.StyleCreationPostProcessor;
import gov.nih.nci.cagrid.data.utilities.WsddUtil;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.data.sdkquery42.encoding.SDK42EncodingUtils;


/** 
 *  SDK42PostCreationHelper
 *  Post-creation helper for caCORE SDK 4.2 data service style
 * 
 * @author David Ervin
 * 
 * @created Jan 18, 2008 12:38:01 PM
 * @version $Id: SDK41PostCreationHelper.java,v 1.1 2008-12-22 21:02:54 dervin Exp $ 
 */
public class PostCreationHelper implements StyleCreationPostProcessor {
    
    private static final Log LOG = LogFactory.getLog(PostCreationHelper.class);

    public void creationPostProcessStyle(ServiceExtensionDescriptionType desc, 
        ServiceInformation serviceInfo) throws Exception {
        editWsddForCastorMappings(serviceInfo);
    }
    
    
    private void editWsddForCastorMappings(ServiceInformation info) throws Exception {
        String mainServiceName = info.getIntroduceServiceProperties().getProperty(
            IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
        ServiceType mainService = CommonTools.getService(info.getServices(), mainServiceName);
        String servicePackageName = mainService.getPackageName();
        String packageDir = servicePackageName.replace('.', File.separatorChar);
        // find the client source directory, where the client-config will be located
        File clientConfigFile = new File(info.getBaseDirectory(), 
            "src" + File.separator + packageDir + File.separator + "client" + File.separator + "client-config.wsdd");
        LOG.debug("Editing client config wsdd at " + clientConfigFile.getAbsolutePath());
        if (!clientConfigFile.exists()) {
            throw new CodegenExtensionException("Client config file " + clientConfigFile.getAbsolutePath()
                + " not found!");
        }
        // fine the server-config.wsdd, located in the service's root directory
        File serverConfigFile = new File(info.getBaseDirectory(), "server-config.wsdd");
        LOG.debug("Editing server config wsdd at " + serverConfigFile.getAbsolutePath());
        if (!serverConfigFile.exists()) {
            throw new CodegenExtensionException("Server config file " 
                + serverConfigFile.getAbsolutePath() + " not found!");
        }
        
        // edit the marshaling castor mapping to avoid serializing associations
        String marshallingXmlText = Utils.fileToStringBuffer(
            new File(CastorMappingUtil.getMarshallingCastorMappingFileName(info))).toString();
        String editedMarshallingText = CastorMappingUtil.removeAssociationMappings(marshallingXmlText);
        String editedMarshallingFileName = CastorMappingUtil.getEditedMarshallingCastorMappingFileName(info);
        Utils.stringBufferToFile(new StringBuffer(editedMarshallingText), editedMarshallingFileName);
        
        // edit the unmarshaling castor mapping to avoid deserializing associations
        String unmarshallingXmlText = Utils.fileToStringBuffer(
            new File(CastorMappingUtil.getUnmarshallingCastorMappingFileName(info))).toString();
        String editedUnmarshallingText = CastorMappingUtil.removeAssociationMappings(unmarshallingXmlText);
        String editedUnmarshallingFileName = CastorMappingUtil.getEditedUnmarshallingCastorMappingFileName(info);
        Utils.stringBufferToFile(new StringBuffer(editedUnmarshallingText), editedUnmarshallingFileName);
        
        // set properties in the client to use the edited marshaler
        WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
            SDK42EncodingUtils.CASTOR_MARSHALLER_PROPERTY, 
            CastorMappingUtil.getEditedMarshallingCastorMappingName(info));
        // and the edited unmarshaler
        WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
            SDK42EncodingUtils.CASTOR_UNMARSHALLER_PROPERTY, 
            CastorMappingUtil.getEditedUnmarshallingCastorMappingName(info));
        
        // set properties in the server to use the edited marshaler
        WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(),
            info.getServices().getService(0).getName(),
            SDK42EncodingUtils.CASTOR_MARSHALLER_PROPERTY,
            CastorMappingUtil.getEditedMarshallingCastorMappingName(info));
        // and the edited unmarshaler
        WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(),
            info.getServices().getService(0).getName(),
            SDK42EncodingUtils.CASTOR_UNMARSHALLER_PROPERTY,
            CastorMappingUtil.getEditedUnmarshallingCastorMappingName(info));
    }
}
