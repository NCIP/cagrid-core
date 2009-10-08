package org.cagrid.data.sdkquery42.style.wizard.config;

import org.cagrid.data.sdkquery42.processor.SDK42QueryProcessor;

import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

public class SecurityConfigurationStep extends AbstractStyleConfigurationStep {
    
    private boolean useCsmGridIdent = false;
    private boolean useStaticLogin = false;
    private String staticLoginUser = null;
    private String staticLoginPass = null;

    public SecurityConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }


    public void applyConfiguration() throws Exception {
        setServiceProperty(SDK42QueryProcessor.PROPERTY_USE_STATIC_LOGIN, 
            String.valueOf(isUseStaticLogin()), false);
        setServiceProperty(SDK42QueryProcessor.PROPERTY_USE_GRID_IDENTITY_LOGIN, 
            String.valueOf(isUseCsmGridIdent()), false);
        setServiceProperty(SDK42QueryProcessor.PROPERTY_STATIC_LOGIN_PASS, 
            getStaticLoginPass(), false);
        setServiceProperty(SDK42QueryProcessor.PROPERTY_STATIC_LOGIN_USER, 
            getStaticLoginUser(), false);
    }


    public boolean isUseCsmGridIdent() {
        return useCsmGridIdent;
    }


    public void setUseCsmGridIdent(boolean useCsmGridIdent) {
        this.useCsmGridIdent = useCsmGridIdent;
    }


    public boolean isUseStaticLogin() {
        return useStaticLogin;
    }


    public void setUseStaticLogin(boolean useStaticLogin) {
        this.useStaticLogin = useStaticLogin;
    }


    public String getStaticLoginUser() {
        return staticLoginUser;
    }


    public void setStaticLoginUser(String staticLoginUser) {
        this.staticLoginUser = staticLoginUser;
    }


    public String getStaticLoginPass() {
        return staticLoginPass;
    }


    public void setStaticLoginPass(String staticLoginPass) {
        this.staticLoginPass = staticLoginPass;
    }
    
    
    public boolean isUsingLocalApi() {
        boolean usingLocal = false;
        try {
            String usingLocalValue = getServicePropertyValue(SDK42QueryProcessor.PROPERTY_USE_LOCAL_API);
            usingLocal = Boolean.parseBoolean(usingLocalValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return usingLocal;
    }
}
