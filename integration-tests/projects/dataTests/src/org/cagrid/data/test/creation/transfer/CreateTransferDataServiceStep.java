package org.cagrid.data.test.creation.transfer;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;

import java.io.File;

import org.cagrid.data.test.creation.CreationStep;
import org.cagrid.data.test.creation.DataTestCaseInfo;

/** 
 *  CreateTransferDataServiceStep
 *  Creates a new caGrid Data Service with caGrid Transfer support enabled
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 30, 2006 
 * @version $Id: CreateTransferDataServiceStep.java,v 1.1 2009-04-10 17:30:10 dervin Exp $ 
 */
public class CreateTransferDataServiceStep extends CreationStep {
	
	public CreateTransferDataServiceStep(DataTestCaseInfo info, String introduceDir) {
		super(info, introduceDir);
	}
    
    
    public void postSkeletonCreation() throws Throwable {
        // verify the service model exists
        System.out.println("Verifying the service model file exists");
        File serviceModelFile = new File(serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE);
        assertTrue("Service model file does not exist: " + serviceModelFile.getAbsolutePath(), serviceModelFile.exists());
        assertTrue("Service model file cannot be read: " + serviceModelFile.getAbsolutePath(), serviceModelFile.canRead());
        
        // deserialize the service model
        System.out.println("Deserializing service description from introduce.xml");
        ServiceDescription serviceDesc = (ServiceDescription) Utils.deserializeDocument(
            serviceModelFile.getAbsolutePath(), ServiceDescription.class);      
        
        // verify the data extension is in there
        assertTrue("Service description has no extensions", 
            serviceDesc.getExtensions() != null 
            && serviceDesc.getExtensions().getExtension() != null
            && serviceDesc.getExtensions().getExtension().length != 0);
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        assertNotNull("Data service extension not found", dataExtension);
        ExtensionTypeExtensionData extData = new ExtensionTypeExtensionData();
        dataExtension.setExtensionData(extData);
        
        // enable the transfer support feature
        System.out.println("Setting transfer feature enabled");
        Data data = ExtensionDataUtils.getExtensionData(extData);
        ServiceFeatures features = data.getServiceFeatures();
        if (features == null) {
            features = new ServiceFeatures();
            data.setServiceFeatures(features);
        }
        features.setUseTransfer(true);
        ExtensionDataUtils.storeExtensionData(extData, data);
        
        // serialize the edited model to disk
        System.out.println("Serializing service model to disk");
        Utils.serializeDocument(serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE,
            serviceDesc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
}
