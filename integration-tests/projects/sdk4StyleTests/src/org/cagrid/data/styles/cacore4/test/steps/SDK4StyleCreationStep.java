package org.cagrid.data.styles.cacore4.test.steps;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;

import org.cagrid.data.styles.cacore4.test.SDK4ServiceStyleSystemTestConstants;
import org.cagrid.data.test.creation.CreationStep;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  SDK4StyleCreationStep
 *  Test step to create a new caGrid data service
 *  using the caCORE SDK 4.0 service style
 * 
 * @author David Ervin
 * 
 * @created Jan 29, 2008 1:18:44 PM
 * @version $Id: SDK4StyleCreationStep.java,v 1.1 2008-06-05 18:02:23 dervin Exp $ 
 */
public class SDK4StyleCreationStep extends CreationStep {
    
    private ServiceInformation serviceInformation = null;

    public SDK4StyleCreationStep(DataTestCaseInfo serviceInfo, String introduceDir) {
        super(serviceInfo, introduceDir);
    }
    
    
    /**
     * Extended to turn on the cacore 4.0 style in the service model
     */
    protected void postSkeletonCreation() throws Throwable {
        setServiceStyle();
        SDK4StyleConfigurationStep step = new SDK4StyleConfigurationStep(new File(serviceInfo.getDir()));
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
        features.setServiceStyle(SDK4ServiceStyleSystemTestConstants.STYLE_NAME);
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
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), serviceDesc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
    
    
    private ServiceInformation getServiceInformation() throws Exception {
        if (serviceInformation == null) {
            serviceInformation = new ServiceInformation(new File(serviceInfo.getDir()));
        }
        return serviceInformation;
    }
}
