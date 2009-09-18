package org.cagrid.csm.authorization.extension.sync;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.authorization.AuthorizationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.authorization.AuthorizationExtensionManager;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.cagrid.csm.authorization.extension.beans.CSMAuthorizationDescription;
import org.cagrid.csm.authorization.extension.beans.CSMMethodAuthorization;
import org.cagrid.csm.authorization.extension.beans.CSMServiceAuthorization;
import org.cagrid.csm.authorization.extension.common.Constants;
import org.globus.wsrf.utils.AnyHelper;


public class CSMAuthorizationManager implements AuthorizationExtensionManager{

    public String generateAuthorizationExtension(AuthorizationExtensionDescriptionType arg0,
        SpecificServiceInformation serviceInfo) throws AuthorizationExtensionException {
        
        // write out authorization configuration file for this service....
        CSMServiceAuthorization ggauth = new CSMServiceAuthorization();
        ggauth.setServiceName(serviceInfo.getService().getName());

        // process the service
        if (serviceInfo.getService().getExtensions() != null
            && serviceInfo.getService().getExtensions().getExtension() != null) {
            for (int i = 0; i < serviceInfo.getService().getExtensions().getExtension().length; i++) {
                if (serviceInfo.getService().getExtensions().getExtension(i).getExtensionType().equals(
                    ExtensionsLoader.AUTHORIZATION_EXTENSION)
                    && serviceInfo.getService().getExtensions().getExtension(i).getName().equals(
                        Constants.CSM_EXTENSION_NAME)) {
                    try {
                        String memexpString = AnyHelper.toSingleString(serviceInfo.getService().getExtensions()
                            .getExtension(i).getExtensionData().get_any());
                        StringReader reader = new StringReader(memexpString);
                        CSMAuthorizationDescription exp = (CSMAuthorizationDescription) Utils.deserializeObject(reader,
                            CSMAuthorizationDescription.class);
                        ggauth.setCSMAuthorizationDescription(exp);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new AuthorizationExtensionException("Cannot retrieve CSM Authorization Description: "
                            + e.getMessage(), e);
                    }
                }
            }
        }

        // process the methods
        List<CSMMethodAuthorization> methodAuths = new ArrayList<CSMMethodAuthorization>();
        if (serviceInfo.getService().getMethods() != null && serviceInfo.getService().getMethods().getMethod() != null) {
            for (int i = 0; i < serviceInfo.getService().getMethods().getMethod().length; i++) {
                MethodType method = serviceInfo.getService().getMethods().getMethod(i);
                if (method.getExtensions() != null && method.getExtensions().getExtension() != null) {
                    for (int j = 0; j < method.getExtensions().getExtension().length; j++) {
                        if (method.getExtensions().getExtension(j).getExtensionType().equals(
                            ExtensionsLoader.AUTHORIZATION_EXTENSION)
                            && method.getExtensions().getExtension(j).getName().equals(
                                Constants.CSM_EXTENSION_NAME)) {
                            try {
                                String memexpString = AnyHelper.toSingleString(method.getExtensions()
                                    .getExtension(j).getExtensionData().get_any());
                                StringReader reader = new StringReader(memexpString);
                                CSMAuthorizationDescription exp = (CSMAuthorizationDescription) Utils.deserializeObject(reader,
                                    CSMAuthorizationDescription.class);
                                CSMMethodAuthorization methodAuth = new CSMMethodAuthorization();
                                methodAuth.setMethodName(method.getName());
                                methodAuth.setCSMAuthorizationDescription(exp);
                                methodAuths.add(methodAuth);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new AuthorizationExtensionException("Cannot retrieve CSM Authorization Description: "
                                    + e.getMessage(), e);
                            }
                        }
                    }
                }
            }
        }

        CSMMethodAuthorization[] methodAuthArray = new CSMMethodAuthorization[methodAuths.size()];
        methodAuths.toArray(methodAuthArray);
        ggauth.setCSMMethodAuthorization(methodAuthArray);

        try {
            FileWriter authWriter = new FileWriter(serviceInfo.getBaseDirectory().getAbsolutePath() + File.separator
                + "etc" + File.separator + serviceInfo.getService().getName() + Constants.CSM_AUTH_FILE_SUFFIX);
            Utils.serializeObject(ggauth, new QName("gme://org.cagrid.CSM/1/CSMAuthorizationDescription",
                "CSMServiceAuthorization"), authWriter);
            authWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new AuthorizationExtensionException(e1.getMessage(), e1);
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new AuthorizationExtensionException(e1.getMessage(), e1);
        }

        // copy in the required jars
        FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.getName().endsWith(".jar");
            }

        };

        File authExtensionLib = new File("extensions" + File.separator + Constants.CSM_EXTENSION_NAME
            + File.separator + "lib");
        File[] jars = authExtensionLib.listFiles(filter);
        for (int i = 0; i < jars.length; i++) {
            File in = jars[i];
            File out = new File(serviceInfo.getBaseDirectory().getAbsolutePath() + File.separator + "lib"
                + File.separator + in.getName());
            try {
                Utils.copyFile(in, out);
            } catch (IOException e) {
                throw new AuthorizationExtensionException(e.getMessage(), e);

            }
        }

        // resync the eclipse classpath doc with what is in the lib
        // directory
        try {
            ExtensionUtilities.resyncWithLibDir(new File(serviceInfo.getBaseDirectory().getAbsolutePath()
                + File.separator + ".classpath"));
        } catch (Exception e) {
            throw new AuthorizationExtensionException("Unable to resync the eclipse .classpath file:", e);
        }

        // return the csm authorization class
        return "org.cagrid.csm.authorization.extension.service.CSMAuthorization";

    }

    public void removeAuthorizationExtension(AuthorizationExtensionDescriptionType arg0,
        SpecificServiceInformation serviceInfo) throws AuthorizationExtensionException {
        File file = new File(serviceInfo.getBaseDirectory().getAbsolutePath() + File.separator
            + "etc" + File.separator + serviceInfo.getService().getName() + Constants.CSM_AUTH_FILE_SUFFIX);
        file.delete();
    }

}
