package gov.nih.nci.cagrid.data.style.cacore32.helpers;

import java.io.File;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.style.StyleCreationPostProcessor;
import gov.nih.nci.cagrid.data.style.cacore32.encoding.SDK32EncodingUtils;
import gov.nih.nci.cagrid.data.utilities.WsddUtil;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;

/** 
 *  SDK32PostCreationProcessor
 *  Deletes the old castor jar from the service's lib dir
 * 
 * @author David Ervin
 * 
 * @created Aug 31, 2007 11:17:40 AM
 * @version $Id: SDK32PostCreationProcessor.java,v 1.1 2009-01-06 17:29:28 dervin Exp $ 
 */
public class SDK32PostCreationProcessor implements StyleCreationPostProcessor {

    public void creationPostProcessStyle(ServiceExtensionDescriptionType desc, 
        ServiceInformation serviceInfo) throws Exception {
        deleteOldCastorJar(serviceInfo);
        editWsddForCastorMappings(serviceInfo);
    }
    
    
    private void deleteOldCastorJar(ServiceInformation info) {
        File castorLib = new File(info.getBaseDirectory().getAbsolutePath() 
            + File.separator + "lib" + File.separator + "castor-0.9.9.jar");
        castorLib.delete();
    }
    
    
    private void editWsddForCastorMappings(ServiceInformation info) throws Exception {
        String mainServiceName = info.getIntroduceServiceProperties().getProperty(
            IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
        ServiceType mainService = CommonTools.getService(info.getServices(), mainServiceName);
        String servicePackageName = mainService.getPackageName();
        String packageDir = servicePackageName.replace('.', File.separatorChar);
        // find the client source directory, where the client-config will be located
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
        
        // edit the marshalling castor mapping to avoid serializing associations
        String marshallingXmlText = Utils.fileToStringBuffer(
            new File(CastorMappingUtil.getMarshallingCastorMappingFileName(info))).toString();
        String editedMarshallingText = CastorMappingUtil.removeAssociationMappings(marshallingXmlText);
        String editedMarshallingFileName = CastorMappingUtil.getEditedMarshallingCastorMappingFileName(info);
        Utils.stringBufferToFile(new StringBuffer(editedMarshallingText), editedMarshallingFileName);
        
        // edit the UNmarshalling castor mapping to avoid DEserializing associations
        String unmarshallingXmlText = Utils.fileToStringBuffer(
            new File(CastorMappingUtil.getUnmarshallingCastorMappingFileName(info))).toString();
        String editedUnmarshallingText = CastorMappingUtil.removeAssociationMappings(unmarshallingXmlText);
        String editedUnmarshallingFileName = CastorMappingUtil.getEditedUnmarshallingCastorMappingFileName(info);
        Utils.stringBufferToFile(new StringBuffer(editedUnmarshallingText), editedUnmarshallingFileName);
        
        // set properties in the client to use the edited marshaller
        WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
            SDK32EncodingUtils.CASTOR_MARSHALLER_PROPERTY, 
            CastorMappingUtil.getEditedMarshallingCastorMappingName(info));
        // and the edited unmarshaller
        WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
            SDK32EncodingUtils.CASTOR_UNMARSHALLER_PROPERTY, 
            CastorMappingUtil.getEditedUnmarshallingCastorMappingName(info));
        
        // set properties in the server to use the edited marshaller
        WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(),
            info.getServices().getService(0).getName(),
            SDK32EncodingUtils.CASTOR_MARSHALLER_PROPERTY,
            CastorMappingUtil.getEditedMarshallingCastorMappingName(info));
        // and the edited unmarshaller
        WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(),
            info.getServices().getService(0).getName(),
            SDK32EncodingUtils.CASTOR_UNMARSHALLER_PROPERTY,
            CastorMappingUtil.getEditedUnmarshallingCastorMappingName(info));
    }
}
