package gov.nih.nci.cagrid.introduce.codegen.provider.providers;

import javax.xml.namespace.QName;

import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.security.MethodAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.MethodSecurity;
import gov.nih.nci.cagrid.introduce.beans.security.NoAuthorization;
import gov.nih.nci.cagrid.introduce.beans.security.SecuritySetting;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

public class GetMultipeResourcePropertiesResourceProvider implements Provider {

    public void addResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException {
        MethodType rpMethod = new MethodType();
        rpMethod.setName("GetMultipleResourceProperties");
        rpMethod.setOutput(new MethodTypeOutput());
        rpMethod.getOutput().setIsArray(false);
        rpMethod.getOutput().setQName(new QName("", "void"));

        MethodTypeImportInformation ii = new MethodTypeImportInformation();
        ii.setFromIntroduce(Boolean.FALSE);
        ii.setInputMessage(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.wsdl",
            "GetMultipleResourcePropertiesRequest"));
        ii.setOutputMessage(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.wsdl",
            "GetMultipleResourcePropertiesResponse"));
        ii.setNamespace("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceProperties-1.2-draft-01.wsdl");
        ii.setPackageName("org.oasis.wsrf.properties");
        ii.setPortTypeName("GetMultipleResourceProperties");
        ii.setWsdlFile("../wsrf/properties/WS-ResourceProperties.wsdl");
        rpMethod.setImportInformation(ii);
        rpMethod.setIsImported(true);
        
        MethodSecurity sec = new MethodSecurity();
        sec.setSecuritySetting(SecuritySetting.None);
        MethodAuthorization auth = new MethodAuthorization();
        auth.setNoAuthorization(new NoAuthorization());
        
        rpMethod.setMethodSecurity(sec);

        MethodTypeProviderInformation pi = new MethodTypeProviderInformation();
        pi.setProviderClass("GetMRPProvider");
        rpMethod.setProviderInformation(pi);
        rpMethod.setIsProvided(true);

        CommonTools.addMethod(service, rpMethod);
    }


    public void removeResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException {
        CommonTools.removeMethod(service.getMethods(), CommonTools.getMethod(service.getMethods(),
        "GetMultipleResourceProperties"));

    }

}
