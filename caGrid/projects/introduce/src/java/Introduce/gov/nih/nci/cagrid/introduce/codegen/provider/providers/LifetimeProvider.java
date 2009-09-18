package gov.nih.nci.cagrid.introduce.codegen.provider.providers;

import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeImportInformation;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeOutput;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeProviderInformation;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import javax.xml.namespace.QName;


public class LifetimeProvider implements Provider {

    public void addResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException {
        // create the two lifetime methods to add

        MethodType destroyMethod = new MethodType();
        destroyMethod.setName("Destroy");
        MethodTypeOutput destroyOutput = new MethodTypeOutput();
        destroyOutput.setIsArray(false);
        destroyOutput.setQName(new QName("", "void"));
        destroyMethod.setOutput(destroyOutput);
        MethodTypeImportInformation ii = new MethodTypeImportInformation();
        ii.setFromIntroduce(Boolean.FALSE);
        ii.setInputMessage(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.wsdl", "DestroyRequest"));
        ii.setOutputMessage(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.wsdl", "DestroyResponse"));
        ii.setNamespace("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.wsdl");
        ii.setPackageName("org.oasis.wsrf.lifetime");
        ii.setPortTypeName("ImmediateResourceTermination");
        ii.setWsdlFile("../wsrf/lifetime/WS-ResourceLifetime.wsdl");
        destroyMethod.setIsImported(true);
        destroyMethod.setImportInformation(ii);
        MethodTypeProviderInformation pi = new MethodTypeProviderInformation();
        pi.setProviderClass("DestroyProvider");
        destroyMethod.setIsProvided(true);
        destroyMethod.setProviderInformation(pi);

        MethodType sttMethod = new MethodType();
        sttMethod.setName("SetTerminationTime");
        MethodTypeOutput sttOutput = new MethodTypeOutput();
        sttOutput.setIsArray(false);
        sttOutput.setQName(new QName("", "void"));
        sttMethod.setOutput(sttOutput);
        ii = new MethodTypeImportInformation();
        ii.setFromIntroduce(new Boolean(false));
        ii.setInputMessage(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.wsdl",
            "SetTerminationTimeRequest"));
        ii.setOutputMessage(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.wsdl",
            "SetTerminationTimeResponse"));
        ii.setNamespace("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.wsdl");
        ii.setPackageName("org.oasis.wsrf.lifetime");
        ii.setPortTypeName("ScheduledResourceTermination");
        ii.setWsdlFile("../wsrf/lifetime/WS-ResourceLifetime.wsdl");
        sttMethod.setIsImported(true);
        sttMethod.setImportInformation(ii);
        pi = new MethodTypeProviderInformation();
        pi.setProviderClass("SetTerminationTimeProvider");
        sttMethod.setIsProvided(true);
        sttMethod.setProviderInformation(pi);

        // add the two lifetime methods
        CommonTools.addMethod(service, destroyMethod);
        CommonTools.addMethod(service, sttMethod);

        // need add the lifetime resource properties
        NamespaceType nsType = new NamespaceType();
        nsType.setNamespace("http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd");
        nsType.setLocation("../wsrf/lifetime/WS-ResourceLifetime.xsd");
        nsType.setPackageName("org.oasis.wsrf.lifetime");
        SchemaElementType ctel = new SchemaElementType();
        ctel.setType("CurrentTime");
        SchemaElementType ttel = new SchemaElementType();
        ttel.setType("TerminationTime");
        SchemaElementType tnel = new SchemaElementType();
        tnel.setType("TerminationNotification");
        SchemaElementType[] types = new SchemaElementType[3];
        types[0] = ctel;
        types[1] = ttel;
        types[2] = tnel;
        nsType.setSchemaElement(types);

        CommonTools.addNamespace(info.getServiceDescriptor(), nsType);

        ResourcePropertyType currentTime = new ResourcePropertyType();
        currentTime.setQName(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime"));
        currentTime.setRegister(false);
        currentTime.setPopulateFromFile(false);
        ResourcePropertyType terminationTime = new ResourcePropertyType();
        terminationTime.setQName(new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime"));
        terminationTime.setRegister(false);
        terminationTime.setPopulateFromFile(false);

        CommonTools.addResourcePropety(service, currentTime);
        CommonTools.addResourcePropety(service, terminationTime);

    }


    public void removeResourceProvider(ServiceType service, ServiceInformation info) throws ProviderException {
        CommonTools.removeMethod(service.getMethods(), CommonTools.getMethod(service.getMethods(), "Destroy"));
        CommonTools.removeMethod(service.getMethods(), CommonTools
            .getMethod(service.getMethods(), "SetTerminationTime"));
        // CommonTools.removeNamespace(info.getServiceDescriptor(),
        // "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd"
        // );
        CommonTools.removeResourceProperty(service, new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "TerminationTime"));
        CommonTools.removeResourceProperty(service, new QName(
            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-ResourceLifetime-1.2-draft-01.xsd", "CurrentTime"));

    }

}
