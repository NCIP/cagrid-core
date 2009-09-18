package org.cagrid.metrics.extension;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;


public class MetricsExtensionCreationPostProcessor implements CreationExtensionPostProcessor {
    private File extensionDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "caGrid_Metrics");
    private File extensionSchemaDir = new File(extensionDir.getAbsolutePath() + File.separator + "schema");
    private File extensionLibDir = new File(extensionDir.getAbsolutePath() + File.separator + "lib");
    
    public void postCreate(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CreationExtensionException {

        // copy in the transfer jars
        try {
            copyLibraries(info);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }
    
    private void copyLibraries(ServiceInformation info) throws Exception {
        String toDir = getServiceLibDir(info);
        File directory = new File(toDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // from the lib directory
        File[] libs = extensionLibDir.listFiles();
        File[] copiedLibs = new File[libs.length];
        if (libs != null) {
            for (int i = 0; i < libs.length; i++) {
                File outFile = new File(toDir + File.separator + libs[i].getName());
                copiedLibs[i] = outFile;
                Utils.copyFile(libs[i], outFile);
            }
        }
        modifyClasspathFile(copiedLibs, info);
    }
    
    private void modifyClasspathFile(File[] libs, ServiceInformation info) throws Exception {
        File classpathFile = new File(info.getBaseDirectory().getAbsolutePath()
            + File.separator + ".classpath");
        ExtensionUtilities.syncEclipseClasspath(classpathFile, libs);
    }


    private String getServiceSchemaDir(ServiceInformation info) {
        return info.getBaseDirectory().getAbsolutePath() + File.separator + "schema" + File.separator
            + info.getServices().getService(0).getName();
    }


    private String getServiceLibDir(ServiceInformation info) {
        return info.getBaseDirectory().getAbsolutePath() + File.separator + "lib";
    }

}
