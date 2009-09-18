package org.cagrid.data.style.test.cacore32;

import gov.nih.nci.cagrid.common.JarUtilities;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.FileFilters;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.jar.JarFile;

import org.cagrid.data.test.creation.CreationStep;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  CreateSDK32StyleServiceStep
 *  Step to test creation of a data service using cacore32 style
 * 
 * @author David Ervin
 * 
 * @created Jul 18, 2007 2:53:54 PM
 * @version $Id: CreateSDK32StyleServiceStep.java,v 1.5 2009-01-06 20:07:20 dervin Exp $ 
 */
public class CreateSDK32StyleServiceStep extends CreationStep {
    
    // directory for SDK query processor libraries
    public static final String SDK_32_LIB_DIR = ExtensionsLoader.getInstance().getExtensionsDir().getAbsolutePath() 
        + File.separator + "data" + File.separator + "sdk32" + File.separator + "lib";
        
    private File sdkPackageDir = null;

    public CreateSDK32StyleServiceStep(DataTestCaseInfo serviceInfo, String introduceDir, File sdkPackageDir) {
        super(serviceInfo, introduceDir);
        this.sdkPackageDir = sdkPackageDir;
    }
    
    
    /**
     * Extended to turn on the cacore32 style in the service model
     */
    protected void postSkeletonCreation() throws Throwable {
        setServiceStyle();
        extractCastorMappingFiles();
        addQueryProcessorJar();
    }
    
    
    private void setServiceStyle() throws Throwable {
        Data extensionData = getExtensionData();
        ServiceFeatures features = extensionData.getServiceFeatures();
        if (features == null) {
            features = new ServiceFeatures();
            extensionData.setServiceFeatures(features);
        }
        features.setServiceStyle(Sdk32TestConstants.STYLE_NAME);
        storeExtensionData(extensionData);
    }
    
    
    private void addQueryProcessorJar() {
        File processorJar = null;
        File libDir = new File(SDK_32_LIB_DIR);
        File[] jars = libDir.listFiles(new FileFilters.JarFileFilter());
        if (jars.length != 1) {
            StringBuffer detail = new StringBuffer();
            detail.append("Expected to find a single jar file in the directory\n");
            detail.append(libDir.getAbsolutePath()).append("\n");
            detail.append("Found the following libs instead:\n");
            for (File f : jars) {
                detail.append("\t").append(f.getName()).append("\n");
            }
            fail(detail.toString());
        }
        processorJar = jars[0];
        
        File jarDestination = new File(serviceInfo.getDir(), "lib" + File.separator + processorJar.getName());
        try {
            Utils.copyFile(processorJar, jarDestination);
        } catch (IOException ex) {
            ex.printStackTrace();
            fail("Error copying SDK query processor jar: " + ex.getMessage());
        }
    }
    
    
    private void extractCastorMappingFiles() throws Throwable {
        ServiceInformation info = new ServiceInformation(new File(serviceInfo.getDir()));
        String marshallingMappingDestination = CastorMappingUtil.getMarshallingCastorMappingFileName(info);
        String unmarshallingMappingDestination = CastorMappingUtil.getUnmarshallingCastorMappingFileName(info);
        
        // find the client SDK jar
        File clientLibDir = new File(sdkPackageDir, "sdk3.2.1" + File.separator + "client" + File.separator + "lib");
        File[] clientJars = clientLibDir.listFiles(new FileFilter() {
            public boolean accept (File path) {
                return path.getName().endsWith("-client.jar");
            }
        });
        assertEquals("Unexpected number of client jars found in SDK package", 1, clientJars.length);
        
        // grab the contents of the mapping files
        JarFile clientJar = new JarFile(clientJars[0]);
        StringBuffer marshallingContents = JarUtilities.getFileContents(clientJar, CastorMappingUtil.CASTOR_MARSHALLING_MAPPING_FILE);
        StringBuffer unmarshallingContents = JarUtilities.getFileContents(clientJar, CastorMappingUtil.CASTOR_UNMARSHALLING_MAPPING_FILE);
        
        // write the mappings out to disk
        Utils.stringBufferToFile(marshallingContents, marshallingMappingDestination);
        Utils.stringBufferToFile(unmarshallingContents, unmarshallingMappingDestination);
    }
    
    
    private ServiceDescription getServiceDescription() throws Throwable {
        // verify the service model exists
        System.out.println("Verifying the service model file exists");
        File serviceModelFile = new File(serviceInfo.getDir(), IntroduceConstants.INTRODUCE_XML_FILE);
        assertTrue("Service model file did not exist: " + serviceModelFile.getAbsolutePath(), serviceModelFile.exists());
        assertTrue("Service model file cannot be read: " + serviceModelFile.getAbsolutePath(), serviceModelFile.canRead());
        
        // deserialize the service model
        System.out.println("Deserializing service description from introduce.xml");
        ServiceDescription serviceDesc = (ServiceDescription) Utils.deserializeDocument(
            serviceModelFile.getAbsolutePath(), ServiceDescription.class);
        return serviceDesc;
    }
    
    
    private Data getExtensionData() throws Throwable {
        ServiceDescription serviceDesc = getServiceDescription();
        // get the extension data, set service style to cacore31
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        assertNotNull("Data service extension was not found in the service model", dataExtension);
        Data extensionData = ExtensionDataUtils.getExtensionData(dataExtension.getExtensionData());
        return extensionData;
    }
    
    
    private void storeExtensionData(Data data) throws Throwable {
        File serviceModelFile = new File(serviceInfo.getDir(), IntroduceConstants.INTRODUCE_XML_FILE);
        ServiceDescription serviceDesc = getServiceDescription();
        
        // get the extension data, set service style to cacore31
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        assertNotNull("Data service extension was not found in the service model", dataExtension);
        ExtensionDataUtils.storeExtensionData(dataExtension.getExtensionData(), data);
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), serviceDesc, 
            IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
}
