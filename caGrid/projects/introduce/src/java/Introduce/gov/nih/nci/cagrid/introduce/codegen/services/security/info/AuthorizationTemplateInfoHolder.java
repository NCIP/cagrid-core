/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
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
