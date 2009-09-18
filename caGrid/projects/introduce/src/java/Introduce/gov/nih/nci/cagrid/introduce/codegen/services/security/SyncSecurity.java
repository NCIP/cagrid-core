package gov.nih.nci.cagrid.introduce.codegen.services.security;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.codegen.services.security.info.AuthorizationTemplateInfoHolder;
import gov.nih.nci.cagrid.introduce.codegen.services.security.tools.SecurityMetadataGenerator;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.authorization.AuthorizationExtensionManager;
import gov.nih.nci.cagrid.introduce.templates.etc.SecurityDescTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.ServiceAuthorizationTemplate;
import gov.nih.nci.cagrid.metadata.security.ServiceSecurityMetadata;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;


/**
 * SyncMethodsOnDeployment
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncSecurity extends SyncTool {
    
    private static final Logger logger = Logger.getLogger(SyncSecurity.class);

    private ServiceType service;


    public SyncSecurity(File baseDirectory, ServiceInformation info, ServiceType service) {
        super(baseDirectory, info);
        this.service = service;
    }


    public void sync() throws SynchronizationException {
        SpecificServiceInformation ssi = new SpecificServiceInformation(getServiceInformation(), service);
        try {
            // regenerate the services security descriptor
            SecurityDescTemplate secDescT = new SecurityDescTemplate();
            String secDescS = secDescT.generate(ssi);
            File secDescF = new File(getBaseDirectory() + File.separator + "etc" + File.separator + service.getName()
                + "-security-desc.xml");
            FileWriter secDescFW = new FileWriter(secDescF);
            secDescFW.write(secDescS);
            secDescFW.close();

            // regenerate the services security metadata
            ServiceSecurityMetadata metadata = SecurityMetadataGenerator.getSecurityMetadata(ssi);
            File meta = new File(getBaseDirectory() + File.separator + "etc" + File.separator + service.getName()
                + "-security-metadata.xml");
            QName qn = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.security",
                "ServiceSecurityMetadata");
            Utils.serializeDocument(meta.getAbsolutePath(), metadata, qn);

            
            // determine which authorization extension are being used in this
            // service
            List<String> usedExtensions = new ArrayList<String>();
            if (service.getExtensions() != null && service.getExtensions().getExtension() != null) {
                for (int i = 0; i < service.getExtensions().getExtension().length; i++) {
                    if (service.getExtensions().getExtension(i).getExtensionType().equals(
                        ExtensionsLoader.AUTHORIZATION_EXTENSION)) {
                        if (!usedExtensions.contains(service.getExtensions().getExtension(i).getName())) {
                            usedExtensions.add(service.getExtensions().getExtension(i).getName());
                        }
                    }
                }
            }
            if (service.getMethods() != null && service.getMethods().getMethod() != null) {
                for (int i = 0; i < service.getMethods().getMethod().length; i++) {
                    if (service.getMethods().getMethod(i).getExtensions() != null
                        && service.getMethods().getMethod(i).getExtensions().getExtension() != null) {
                        for (int j = 0; j < service.getMethods().getMethod(i).getExtensions().getExtension().length; j++) {
                            if (service.getMethods().getMethod(i).getExtensions().getExtension(j).getExtensionType()
                                .equals(ExtensionsLoader.AUTHORIZATION_EXTENSION)) {
                                if (!usedExtensions.contains(service.getMethods().getMethod(i).getExtensions()
                                    .getExtension(j).getName())) {
                                    usedExtensions.add(service.getMethods().getMethod(i).getExtensions()
                                        .getExtension(j).getName());
                                }
                            }

                        }
                    }
                }
            }

            Map<String, String> authExtensionToAuthClasses = new HashMap<String, String>();
            // process all the required authorization extensions for this
            // service
            for (Iterator iterator = usedExtensions.iterator(); iterator.hasNext();) {
                String extensionName = (String) iterator.next();
                AuthorizationExtensionDescriptionType authorizationExtensionDescriptionType = ExtensionsLoader
                    .getInstance().getAuthorizationExtension(extensionName);
                AuthorizationExtensionManager processor = ExtensionTools
                    .getAuthorizationExtensionCodegenPostProcessor(authorizationExtensionDescriptionType.getName());
                String authorizationClassname = processor.generateAuthorizationExtension(authorizationExtensionDescriptionType, new SpecificServiceInformation(
                    getServiceInformation(), service));
                authExtensionToAuthClasses.put(extensionName, authorizationClassname);
            }
            
            AuthorizationTemplateInfoHolder holder = new AuthorizationTemplateInfoHolder(authExtensionToAuthClasses, new SpecificServiceInformation(getServiceInformation(),ssi.getService()));
            
            // regenerate the authorization class for the service
            // if authz method is set to none this class will not be used
            ServiceAuthorizationTemplate authorizationT = new ServiceAuthorizationTemplate();
            String authorizationS = authorizationT.generate(holder);
            File authorizationF = new File(getBaseDirectory().getAbsolutePath() + File.separator + "src"
                + File.separator + CommonTools.getPackageDir(ssi.getService()) + File.separator + "service"
                + File.separator + "globus" + File.separator + ssi.getService().getName() + "Authorization.java");

            FileWriter authorizationFW = new FileWriter(authorizationF);
            authorizationFW.write(authorizationS);
            authorizationFW.close();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new SynchronizationException(e.getMessage(), e);
        }

    }

}
