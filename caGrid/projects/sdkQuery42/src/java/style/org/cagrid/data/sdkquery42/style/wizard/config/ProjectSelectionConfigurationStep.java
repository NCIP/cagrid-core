package org.cagrid.data.sdkquery42.style.wizard.config;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

/**
 * ProjectSelectionConfigurationStep
 * Configures basic aspects of the service such as application name, local / remote
 * API and directories, and service URL
 * 
 * @author David
 */
public class ProjectSelectionConfigurationStep extends AbstractStyleConfigurationStep {
    
    private String applicationName = null;
    private boolean isLocalApi = false;
    private String localClientDir = null;
    private String remoteClientDir = null;
    private String applicationHostname = null;
    private Integer applicationPort = null;
    private boolean useHttps = false;

    public ProjectSelectionConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }


    public void applyConfiguration() throws Exception {
        // TODO Auto-generated method stub

    }
    
    
    public boolean isLocalClientDirValid() {
        return false;
    }
    
    
    public boolean isRemoteClientDirValid() {
        return false;
    }


    public String getApplicationName() {
        return applicationName;
    }


    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }


    public boolean isLocalApi() {
        return isLocalApi;
    }


    public void setLocalApi(boolean isLocalApi) {
        this.isLocalApi = isLocalApi;
    }


    public String getLocalClientDir() {
        return localClientDir;
    }


    public void setLocalClientDir(String localClientDir) {
        this.localClientDir = localClientDir;
    }


    public String getRemoteClientDir() {
        return remoteClientDir;
    }


    public void setRemoteClientDir(String remoteClientDir) {
        this.remoteClientDir = remoteClientDir;
    }


    public String getApplicationHostname() {
        return applicationHostname;
    }


    public void setApplicationHostname(String applicationHostname) {
        this.applicationHostname = applicationHostname;
    }


    public Integer getApplicationPort() {
        return applicationPort;
    }


    public void setApplicationPort(Integer applicationPort) {
        this.applicationPort = applicationPort;
    }


    public boolean isUseHttps() {
        return useHttps;
    }


    public void setUseHttps(boolean useHttps) {
        this.useHttps = useHttps;
    }
}
