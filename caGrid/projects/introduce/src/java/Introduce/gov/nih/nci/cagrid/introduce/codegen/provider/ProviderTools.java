package gov.nih.nci.cagrid.introduce.codegen.provider;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.provider.providers.GetMultipeResourcePropertiesResourceProvider;
import gov.nih.nci.cagrid.introduce.codegen.provider.providers.GetResourcePropertyProvider;
import gov.nih.nci.cagrid.introduce.codegen.provider.providers.LifetimeProvider;
import gov.nih.nci.cagrid.introduce.codegen.provider.providers.ProviderException;
import gov.nih.nci.cagrid.introduce.codegen.provider.providers.QueryResourcePropertiesResourceProvider;
import gov.nih.nci.cagrid.introduce.codegen.provider.providers.SubscribeProvider;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public final class ProviderTools {

    public ProviderTools() {

    }


    public static void addLifetimeResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new LifetimeProvider().addResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }

    }


    public static void removeLifetimeResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new LifetimeProvider().removeResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    public static void addSubscribeResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new SubscribeProvider().addResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    public static void removeSubscribeResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new SubscribeProvider().removeResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    private static void addGetResourcePropertyResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new GetResourcePropertyProvider().addResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    private static void removeGetResourcePropertyResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new GetResourcePropertyProvider().removeResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    private static void addGetMultipeResourcePropertiesResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new GetMultipeResourcePropertiesResourceProvider().addResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    private static void removeGetMultipeResourcePropertiesResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new GetMultipeResourcePropertiesResourceProvider().removeResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    private static void addQueryResourcePropertiesResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new QueryResourcePropertiesResourceProvider().addResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    private static void removeQueryResourcePropertiesResourceProvider(ServiceType service, ServiceInformation info) {
        try {
            new QueryResourcePropertiesResourceProvider().removeResourceProvider(service, info);
        } catch (ProviderException ex) {
            ex.printStackTrace();
        }
    }


    public static void removeProviderFromServiceConfig(ServiceType service, String providerClass,
        ServiceInformation info) throws Exception, IOException {

        Document doc = XMLUtilities.fileNameToDocument(info.getBaseDirectory() + File.separator + "server-config.wsdd");
        List servicesEls = doc.getRootElement().getChildren("service",
            Namespace.getNamespace("http://xml.apache.org/axis/wsdd/"));
        for (int serviceI = 0; serviceI < servicesEls.size(); serviceI++) {
            Element serviceEl = (Element) servicesEls.get(serviceI);
            if (serviceEl.getAttribute("name").getValue().equals("SERVICE-INSTANCE-PREFIX/" + service.getName())) {
                List paramsEls = serviceEl.getChildren("parameter", Namespace
                    .getNamespace("http://xml.apache.org/axis/wsdd/"));
                for (int paramsI = 0; paramsI < paramsEls.size(); paramsI++) {
                    Element paramEl = (Element) paramsEls.get(paramsI);
                    if (paramEl.getAttributeValue("name").equals("providers")) {
                        String value = paramEl.getAttributeValue("value");
                        String newValue = "";
                        StringTokenizer strtok = new StringTokenizer(value, " ", false);
                        while (strtok.hasMoreElements()) {
                            String nextTok = strtok.nextToken();
                            if (!nextTok.equals(providerClass)) {
                                newValue = newValue + " " + nextTok;
                            }
                        }
                        paramEl.setAttribute("value", newValue);
                    }
                }

            }
        }

        FileWriter fw = new FileWriter(info.getBaseDirectory() + File.separator + "server-config.wsdd");
        fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(doc)));
        fw.close();
    }


    public static void addResourcePropertiesManagementResourceFrameworkOption(ServiceType service,
        ServiceInformation info) {
        addGetMultipeResourcePropertiesResourceProvider(service, info);
        addGetResourcePropertyResourceProvider(service, info);
        addQueryResourcePropertiesResourceProvider(service, info);
    }


    public static void removeResourcePropertiesManagementResourceFrameworkOption(ServiceType service,
        ServiceInformation info) {
        removeGetMultipeResourcePropertiesResourceProvider(service, info);
        removeGetResourcePropertyResourceProvider(service, info);
        removeQueryResourcePropertiesResourceProvider(service, info);
    }

}
