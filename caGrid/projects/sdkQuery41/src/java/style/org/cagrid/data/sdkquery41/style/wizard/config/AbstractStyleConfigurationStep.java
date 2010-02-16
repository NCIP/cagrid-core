package org.cagrid.data.sdkquery41.style.wizard.config;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.QueryProcessorConstants;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;

/** 
 *  AbstractStyleConfigurationStep
 *  Base step for style configuration actions
 * 
 * @author David Ervin
 * 
 * @created Jan 18, 2008 3:21:28 PM
 * @version $Id: AbstractStyleConfigurationStep.java,v 1.1 2008-11-26 20:21:51 dervin Exp $ 
 */
public abstract class AbstractStyleConfigurationStep {

    private ServiceInformation serviceInfo = null;
    
    public AbstractStyleConfigurationStep(ServiceInformation serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
    
    
    public abstract void applyConfiguration() throws Exception;
    
    
    protected ServiceInformation getServiceInformation() {
        return this.serviceInfo;
    }
    
    
    protected void setCql1ProcessorProperty(String shortKey, String value, boolean fromEtc) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        CommonTools.setServiceProperty(desc,
            QueryProcessorConstants.QUERY_PROCESSOR_CONFIG_PREFIX + shortKey, value, fromEtc);
    }
    
    
    protected void setCql2ProcessorProperty(String shortKey, String value, boolean fromEtc) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        CommonTools.setServiceProperty(desc,
            QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CONFIG_PREFIX + shortKey, value, fromEtc);
    }
    
    
    protected String getCql1ProcessorPropertyValue(String shortKey) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        String longKey = QueryProcessorConstants.QUERY_PROCESSOR_CONFIG_PREFIX + shortKey;
        String value = null;
        if (CommonTools.servicePropertyExists(desc, longKey)) {
            try {
                value = CommonTools.getServicePropertyValue(desc, longKey);
            } catch (Exception ex) {
                System.err.println("Error retrieving service property: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return value;
    }
    
    
    protected String getCql2ProcessorPropertyValue(String shortKey) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        String longKey = QueryProcessorConstants.CQL2_QUERY_PROCESSOR_CONFIG_PREFIX + shortKey;
        String value = null;
        if (CommonTools.servicePropertyExists(desc, longKey)) {
            try {
                value = CommonTools.getServicePropertyValue(desc, longKey);
            } catch (Exception ex) {
                System.err.println("Error retrieving service property: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        return value;
    }
    
    
    protected Data getExtensionData() throws Exception {
        ServiceDescription serviceDesc = serviceInfo.getServiceDescriptor();
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
        Data extensionData = ExtensionDataUtils.getExtensionData(dataExtension.getExtensionData());
        return extensionData;
    }
    
    
    protected void storeExtensionData(Data data) throws Exception {
        File serviceModelFile = new File(serviceInfo.getBaseDirectory(), IntroduceConstants.INTRODUCE_XML_FILE);
        ServiceDescription serviceDesc = serviceInfo.getServiceDescriptor();
        
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
        ExtensionDataUtils.storeExtensionData(dataExtension.getExtensionData(), data);
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), serviceDesc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
}
