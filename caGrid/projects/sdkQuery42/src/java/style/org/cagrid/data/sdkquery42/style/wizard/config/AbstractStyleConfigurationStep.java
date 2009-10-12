package org.cagrid.data.sdkquery42.style.wizard.config;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/** 
 *  AbstractStyleConfigurationStep
 *  Base step for style configuration actions
 * 
 * @author David Ervin
 * 
 * @created Jan 18, 2008 3:21:28 PM
 * @version $Id: AbstractStyleConfigurationStep.java,v 1.2 2008-01-23 19:58:20 dervin Exp $ 
 */
public abstract class AbstractStyleConfigurationStep {
    
    public static final String STYLE_PROPERTIES_FILE = "style.configuration.properties";

    private ServiceInformation serviceInfo = null;
    
    public AbstractStyleConfigurationStep(ServiceInformation serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
    
    
    public abstract void applyConfiguration() throws Exception;
    
    
    protected ServiceInformation getServiceInformation() {
        return this.serviceInfo;
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
    
    
    protected void setServiceProperty(String shortKey, String value, boolean fromEtc) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        CommonTools.setServiceProperty(desc,
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + shortKey, value, fromEtc);
    }
    
    
    protected String getServicePropertyValue(String shortKey) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        String longKey = DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + shortKey;
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
        
    
    protected String getStyleProperty(String key) throws IOException {
        return getStyleProperties().getProperty(key);
    }
    
    
    protected synchronized void setStyleProperty(String key, String value) throws IOException {
        Properties props = getStyleProperties();
        props.setProperty(key, value);
        FileOutputStream propsOut = new FileOutputStream(
            new File(getServiceInformation().getBaseDirectory(), STYLE_PROPERTIES_FILE));
        props.store(propsOut, "Written by " + getClass().getName());
        propsOut.close();
    }
    
    
    private synchronized Properties getStyleProperties() throws IOException {
        Properties props = new Properties();
        File propsFile = new File(getServiceInformation().getBaseDirectory(), STYLE_PROPERTIES_FILE);
        if (propsFile.exists()) {
            FileInputStream propsIn = new FileInputStream(propsFile);
            props.load(propsIn);
            propsIn.close();
        } else {
            propsFile.createNewFile();
        }
        return props;
    }
}
