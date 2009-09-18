package org.cagrid.data.sdkquery41.style.wizard.config;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;


/**
 * LoginConfigurationStep Manages and applies application service login
 * configuration to a caCORE SDK 4.1 backed data service
 * 
 * @author David
 */
public class LoginConfigurationStep extends AbstractStyleConfigurationStep {

    private Boolean useLogin = null;
    private String username = null;
    private String password = null;


    public LoginConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }


    public void applyConfiguration() throws Exception {
        // TODO Auto-generated method stub

    }


    public Boolean getUseLogin() {
        return useLogin;
    }


    public void setUseLogin(Boolean useLogin) {
        this.useLogin = useLogin;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }
}
