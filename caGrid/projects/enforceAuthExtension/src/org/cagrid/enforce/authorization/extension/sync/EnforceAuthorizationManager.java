package org.cagrid.enforce.authorization.extension.sync;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.AuthorizationExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.authorization.AuthorizationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.authorization.AuthorizationExtensionManager;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;


public class EnforceAuthorizationManager implements AuthorizationExtensionManager {

    public String generateAuthorizationExtension(AuthorizationExtensionDescriptionType extensionDesc,
        SpecificServiceInformation serviceInfo) throws AuthorizationExtensionException {

        
        // copy in the required jars
        FileFilter filter = new FileFilter() {

            public boolean accept(File pathname) {
                return !pathname.isDirectory() && pathname.getName().endsWith(".jar");
            }

        };

        File authExtensionLib = new File("extensions" + File.separator + org.cagrid.enforce.authorization.extension.common.Constants.ENFORCE_EXTENSION_NAME
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
        
        return "org.cagrid.enforce.authorization.extension.service.EnforceAuthorization";
    }

    public void removeAuthorizationExtension(AuthorizationExtensionDescriptionType arg0,
        SpecificServiceInformation serviceInfo) throws AuthorizationExtensionException {
    
    }

}
