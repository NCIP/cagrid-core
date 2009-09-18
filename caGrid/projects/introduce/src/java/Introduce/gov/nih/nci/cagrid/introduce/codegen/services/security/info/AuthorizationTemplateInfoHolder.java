package gov.nih.nci.cagrid.introduce.codegen.services.security.info;

import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;

import java.util.Map;

public class AuthorizationTemplateInfoHolder {
    
    Map<String, String> authorizationExtensionClassNamesMap = null;
    SpecificServiceInformation info = null;
    
    public AuthorizationTemplateInfoHolder(Map<String, String> authorizationExtensionClassNamesMap, SpecificServiceInformation serviceInfo) {
        this.authorizationExtensionClassNamesMap = authorizationExtensionClassNamesMap;
        this.info = serviceInfo;
    }

    public Map<String, String> getAuthorizationExtensionClassNamesMap() {
        return authorizationExtensionClassNamesMap;
    }

    public SpecificServiceInformation getSpecificServiceInformation() {
        return info;
    }

    
}
