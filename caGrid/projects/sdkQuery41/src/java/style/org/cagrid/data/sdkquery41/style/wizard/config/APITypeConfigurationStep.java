package org.cagrid.data.sdkquery41.style.wizard.config;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import org.cagrid.data.sdkquery41.processor.SDK41QueryProcessor;

public class APITypeConfigurationStep extends AbstractStyleConfigurationStep {
    
    private ApiType apiType;
    private String hostname;
    private Integer portNumber;
    private Boolean useHttps;

    public APITypeConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }


    public void applyConfiguration() throws Exception {
        // set service properties
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        CommonTools.setServiceProperty(desc, 
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + SDK41QueryProcessor.PROPERTY_USE_LOCAL_API, 
            apiType != null ? String.valueOf(apiType == ApiType.LOCAL_API) : SDK41QueryProcessor.DEFAULT_USE_LOCAL_API, false);
        CommonTools.setServiceProperty(desc,
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + SDK41QueryProcessor.PROPERTY_HOST_NAME,
            hostname != null ? hostname : "", false);
        CommonTools.setServiceProperty(desc,
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + SDK41QueryProcessor.PROPERTY_HOST_PORT,
            portNumber != null ? String.valueOf(portNumber) : "", false);
        CommonTools.setServiceProperty(desc,
            DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + SDK41QueryProcessor.PROPERTY_HOST_HTTPS,
            useHttps != null ? String.valueOf(useHttps) : SDK41QueryProcessor.DEFAULT_HOST_HTTPS, false);
    }
    
        
    public void setApiType(ApiType apiType) {
        this.apiType = apiType;
    }
    
    
    public ApiType getApiType() {
        return apiType;
    }


    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    
    public String getHostname() {
        return hostname;
    }


    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }
    
    
    public Integer getPortNumber() {
        return portNumber;
    }


    public void setUseHttps(Boolean useHttps) {
        this.useHttps = useHttps;
    }
    
    
    public Boolean getUseHttps() {
        return useHttps;
    }
    
    
    public static enum ApiType {
        LOCAL_API, REMOTE_API
    }
}
