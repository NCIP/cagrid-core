package gov.nih.nci.cagrid.sdkquery4.style.wizard.config;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.sdkquery4.processor.SDK4QueryProcessor;

/** 
 *  QueryProcessorSecurityConfigurationStep
 *  Configuration for the query processor security configuration
 * 
 * @author David Ervin
 * 
 * @created Jan 22, 2008 9:19:29 AM
 * @version $Id: QueryProcessorSecurityConfigurationStep.java,v 1.1 2008-01-22 14:44:39 dervin Exp $ 
 */
public class QueryProcessorSecurityConfigurationStep extends AbstractStyleConfigurationStep {
    
    private boolean useLogin;
    private boolean useGridIdentLogin;
    private String staticLoginUsername;
    private String staticLoginPassword;
    
    public QueryProcessorSecurityConfigurationStep(ServiceInformation serviceInfo) {
        super(serviceInfo);
    }
    

    public void applyConfiguration() throws Exception {
        setConfigurationProperty(
            SDK4QueryProcessor.PROPERTY_USE_LOGIN, String.valueOf(useLogin));
        setConfigurationProperty(
            SDK4QueryProcessor.PROPERTY_USE_GRID_IDENTITY_LOGIN, 
            useLogin? String.valueOf(useGridIdentLogin) : "");
        setConfigurationProperty(
            SDK4QueryProcessor.PROPERTY_STATIC_LOGIN_USERNAME,
            useLogin && !useGridIdentLogin ? staticLoginUsername : "");
        setConfigurationProperty(
            SDK4QueryProcessor.PROPERTY_STATIC_LOGIN_PASSWORD,
            useLogin && !useGridIdentLogin ? staticLoginPassword : "");
    }


    public void setStaticLoginPassword(String staticLoginPassword) {
        this.staticLoginPassword = staticLoginPassword;
    }


    public void setStaticLoginUsername(String staticLoginUsername) {
        this.staticLoginUsername = staticLoginUsername;
    }


    public void setUseGridIdentLogin(boolean useGridIdentLogin) {
        this.useGridIdentLogin = useGridIdentLogin;
    }


    public void setUseLogin(boolean useLogin) {
        this.useLogin = useLogin;
    }
    
    
    private void setConfigurationProperty(String rawKey, String value) {
        ServiceDescription desc = getServiceInformation().getServiceDescriptor();
        String paddedKey = DataServiceConstants.QUERY_PROCESSOR_CONFIG_PREFIX + rawKey;
        CommonTools.setServiceProperty(desc, paddedKey, value, false);
    }
}
