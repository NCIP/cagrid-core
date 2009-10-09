package org.cagrid.data.sdkquery41.style.wizard.config;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.util.Properties;

public class SharedConfiguration {
    
    private static SharedConfiguration instance = null;
    
    private ServiceInformation serviceInfo = null;
    private File sdkDirectory = null;
    private Properties sdkDeployProperties = null;
    private File remoteConfigJar = null;
    private File localClientJar = null;
    
    private SharedConfiguration() {
        
    }
    
        
    public static SharedConfiguration getInstance() {
        if (instance == null) {
            instance = new SharedConfiguration();
        }
        return instance;
    }


    public Properties getSdkDeployProperties() {
        return sdkDeployProperties;
    }


    public void setSdkDeployProperties(Properties sdkDeployProperties) {
        this.sdkDeployProperties = sdkDeployProperties;
    }


    public ServiceInformation getServiceInfo() {
        return serviceInfo;
    }


    public void setServiceInfo(ServiceInformation serviceInfo) {
        this.serviceInfo = serviceInfo;
    }


    public File getSdkDirectory() {
        return sdkDirectory;
    }


    public void setSdkDirectory(File sdkDirectory) {
        this.sdkDirectory = sdkDirectory;
    }
    
    
    public File getRemoteConfigJarFile() {
        return this.remoteConfigJar;
    }
    
    
    public void setRemoteConfigJarFile(File file) {
        this.remoteConfigJar = file;
    }
    
    
    public void setLocalConfigJarFile(File file) {
        this.localClientJar = file;
    }
    
    
    public File getLocalConfigJarFile() {
        return this.localClientJar;
    }
}
