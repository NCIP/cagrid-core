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
}
