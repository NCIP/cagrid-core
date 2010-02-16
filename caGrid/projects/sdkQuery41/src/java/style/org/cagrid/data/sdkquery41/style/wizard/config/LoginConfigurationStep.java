package org.cagrid.data.sdkquery41.style.wizard.config;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import org.cagrid.data.sdkquery41.processor.SDK41QueryProcessor;
import org.cagrid.data.sdkquery41.processor2.SDK41CQL2QueryProcessor;


/**
 * LoginConfigurationStep
 * Manages and applies application service login
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
        if (useLogin != null && useLogin.booleanValue()) {
            setCql1ProcessorProperty(SDK41QueryProcessor.PROPERTY_USE_LOGIN, useLogin.toString(), false);
            setCql2ProcessorProperty(SDK41CQL2QueryProcessor.PROPERTY_USE_LOGIN, useLogin.toString(), false);
        }
        if (getPassword() != null) {
            setCql1ProcessorProperty(SDK41QueryProcessor.PROPERTY_STATIC_LOGIN_PASSWORD, getPassword(), false);
            setCql2ProcessorProperty(SDK41CQL2QueryProcessor.PROPERTY_STATIC_LOGIN_PASSWORD, getPassword(), false);
        }
        if (getUsername() != null) {
            setCql1ProcessorProperty(SDK41QueryProcessor.PROPERTY_STATIC_LOGIN_USERNAME, getPassword(), false);
            setCql2ProcessorProperty(SDK41CQL2QueryProcessor.PROPERTY_STATIC_LOGIN_USERNAME, getPassword(), false);
        }
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
