package gov.nih.nci.cagrid.introduce.codegen.provider.providers;

import javax.xml.namespace.QName;

import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

public class SubscribeProvider implements Provider {

    public void addResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException {
        MethodType subscribeMethod = new MethodType();
        subscribeMethod.setName("Subscribe");
        subscribeMethod.setOutput(new MethodTypeOutput());
        subscribeMethod.getOutput().setIsArray(false);
        subscribeMethod.getOutput().setQName(new QName("", "void"));

        MethodTypeImportInformation ii = new MethodTypeImportInformation();
        ii.setFromIntroduce(Boolean.FALSE);
        ii.setInputMessage(new QName(
            "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.wsdl", "SubscribeRequest"));
        ii.setOutputMessage(new QName(
            "http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.wsdl", "SubscribeResponse"));
        ii.setNamespace("http://docs.oasis-open.org/wsn/2004/06/wsn-WS-BaseNotification-1.2-draft-01.wsdl");
        ii.setPackageName("org.oasis.wsn");
        ii.setPortTypeName("NotificationProducer");
        ii.setWsdlFile("../wsrf/notification/WS-BaseN.wsdl");
        subscribeMethod.setImportInformation(ii);
        subscribeMethod.setIsImported(true);

        MethodTypeProviderInformation pi = new MethodTypeProviderInformation();
        pi.setProviderClass("SubscribeProvider");
        subscribeMethod.setProviderInformation(pi);
        subscribeMethod.setIsProvided(true);

        CommonTools.addMethod(service, subscribeMethod);
    }


    public void removeResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException {
        CommonTools.removeMethod(service.getMethods(), CommonTools.getMethod(service.getMethods(), "Subscribe"));
        
    }

}
