package org.cagrid.data.styles.cacore41.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.data.extension.ServiceStyle;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;

import org.cagrid.data.styles.cacore41.test.SDK41ServiceStyleSystemTestConstants;
import org.cagrid.data.test.creation.CreationStep;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  SDK41StyleCreationStep
 *  Test step to create a new caGrid data service
 *  using the caCORE SDK 4.1 service style
 * 
 * @author David Ervin
 * 
 * @created Jan 29, 2008 1:18:44 PM
 * @version $Id: SDK41StyleCreationStep.java,v 1.2 2009-04-10 15:15:24 dervin Exp $ 
 */
public class SDK41StyleCreationStep extends CreationStep {
    
    private ServiceInformation serviceInformation = null;

    public SDK41StyleCreationStep(DataTestCaseInfo serviceInfo, String introduceDir) {
        super(serviceInfo, introduceDir);
    }
    
    
    /**
     * Extended to turn on the cacore 4.1 style in the service model
     */
    protected void postSkeletonCreation() throws Throwable {
        setServiceStyle();
        SDK41StyleConfigurationStep step = new SDK41StyleConfigurationStep(new File(serviceInfo.getDir()));
        step.runStep();
    }
    
    
    protected void postSkeletonPostCreation() throws Throwable {
        // service style config here?
    }
    
    
    private void setServiceStyle() throws Throwable {
        Data extensionData = getExtensionData();
        ServiceFeatures features = extensionData.getServiceFeatures();
        if (features == null) {
            features = new ServiceFeatures();
            extensionData.setServiceFeatures(features);
        }
        features.setServiceStyle(new ServiceStyle(SDK41ServiceStyleSystemTestConstants.STYLE_NAME, "1.4"));
        storeExtensionData(extensionData);
    }
    
    
    private Data getExtensionData() throws Throwable {
        ServiceDescription serviceDesc = getServiceInformation().getServiceDescriptor();
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
        File serviceModelFile = new File(serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE);
        ServiceDescription serviceDesc = getServiceInformation().getServiceDescriptor();
        
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        assertNotNull("Data service extension was not found in the service model", dataExtension);
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        ExtensionDataUtils.storeExtensionData(dataExtension.getExtensionData(), data);
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), serviceDesc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
    
    
    private ServiceInformation getServiceInformation() throws Exception {
        if (serviceInformation == null) {
            serviceInformation = new ServiceInformation(new File(serviceInfo.getDir()));
        }
        return serviceInformation;
    }
}
