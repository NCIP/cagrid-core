package org.cagrid.transfer.extension;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CreationExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;
import java.io.IOException;


public class TransferExtensionCreationPostProcessor implements CreationExtensionPostProcessor {
    private File extensionDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "caGrid_Transfer");
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

        // copy in the caGrid transfer schema and add the namespace types for it
        File transferSchema = new File(extensionSchemaDir + File.separator + "TransferServiceContextTypes.xsd");
        try {
            Utils
                .copyFile(transferSchema, new File(getServiceSchemaDir(info) + File.separator + "TransferServiceContextTypes.xsd"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        NamespaceType transferServiceNamespace = null;
        try {
            transferServiceNamespace = CommonTools.createNamespaceType(getServiceSchemaDir(info) + File.separator
                + "TransferServiceContextTypes.xsd", new File(getServiceSchemaDir(info)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        transferServiceNamespace.setGenerateStubs(new Boolean(false));
        transferServiceNamespace.setPackageName("org.cagrid.transfer.context.stubs.types");
        CommonTools.addNamespace(info.getServiceDescriptor(), transferServiceNamespace);
        
        File transferDescSchema = new File(extensionSchemaDir + File.separator + "caGrid_Transfer.xsd");
        try {
            Utils
                .copyFile(transferDescSchema, new File(getServiceSchemaDir(info) + File.separator + "caGrid_Transfer.xsd"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        NamespaceType transferDescNamespace = null;
        try {
            transferDescNamespace = CommonTools.createNamespaceType(getServiceSchemaDir(info) + File.separator
                + "caGrid_Transfer.xsd", new File(getServiceSchemaDir(info)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        transferDescNamespace.setGenerateStubs(new Boolean(false));
        transferDescNamespace.setPackageName("org.cagrid.transfer.descriptor");
        CommonTools.addNamespace(info.getServiceDescriptor(), transferDescNamespace);
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
