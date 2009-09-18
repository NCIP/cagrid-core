package gov.nih.nci.cagrid.introduce.upgrade.model;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.ModelUpgraderBase;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class Model_1_2__1_3_Upgrader extends ModelUpgraderBase {

    public Model_1_2__1_3_Upgrader(IntroduceUpgradeStatus status, String servicePath) {
        super(status, servicePath, "1.2", "1.3");
    }


    protected void upgrade() throws Exception {
        getStatus().addDescriptionLine("Updating to the new Resource Framework Options");
        Document doc = XMLUtilities.fileNameToDocument(getServicePath() + File.separator + "introduce.xml");

        Element introducePDPElement = new Element("IntroducePDPAuthorization", Namespace
            .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
        Element extensions = new Element("Extensions", Namespace
            .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Extension"));
        Element ggextension = new Element("Extension", Namespace
            .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Extension"));
        ggextension.setAttribute("name", "gridgrouper_auth");
        ggextension.setAttribute("extensionType", "AUTHORIZATION");
        Element csmextension = new Element("Extension", Namespace
            .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Extension"));
        csmextension.setAttribute("name", "csm_auth");
        csmextension.setAttribute("extensionType", "AUTHORIZATION");
        Element extensionData = new Element("ExtensionData", Namespace
            .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Extension"));

        // walk through each service and methods and replace the old
        // authorization style
        // with the new extensions for grid grouper and csm

        Element servicesEl = doc.getRootElement().getChild("Services",
            Namespace.getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Services"));
        List serviceEls = servicesEl.getChildren();
        for (int i = 0; i < serviceEls.size(); i++) {
            Element service = (Element) serviceEls.get(i);
            Element serviceSecurity = service.getChild("ServiceSecurity", Namespace
                .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
            if (serviceSecurity != null) {
                Element serviceAuthorization = serviceSecurity.getChild("ServiceAuthorization", Namespace
                    .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
                if (serviceAuthorization != null) {
                    Element gridGrouperElement = serviceAuthorization.getChild("GridGrouperAuthorization", Namespace
                        .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
                    if (gridGrouperElement != null) {
                        serviceAuthorization.removeContent(gridGrouperElement);
                        serviceAuthorization.addContent((Element) introducePDPElement.clone());

                        gridGrouperElement.detach();
                        gridGrouperElement.setName("MembershipExpression");
                        gridGrouperElement.setNamespace(Namespace
                            .getNamespace("http://cagrid.nci.nih.gov/1/GridGrouper"));

                        Element extensionsEl = (Element) extensions.clone();
                        Element ggElementEl = (Element) ggextension.clone();
                        Element extensionDataEl = (Element) extensionData.clone();
                        extensionsEl.addContent(ggElementEl);
                        ggElementEl.addContent(extensionDataEl);
                        extensionDataEl.addContent((Element) gridGrouperElement.clone());

                        service.addContent(extensionsEl);
                    }
                    Element csmElement = serviceAuthorization.getChild("CSMAuthorization", Namespace
                        .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
                    if (csmElement != null) {
                        serviceAuthorization.removeContent(csmElement);
                        serviceAuthorization.addContent((Element) introducePDPElement.clone());

                        csmElement.detach();
                        csmElement.setName("CSMAuthorization");
                        csmElement.setNamespace(Namespace.getNamespace("http://org.cagrid.csm/1/CSMAuthorization"));
                        for (int childI = 0; childI < csmElement.getChildren().size(); childI++) {
                            ((Element) csmElement.getChildren().get(childI)).setNamespace(Namespace
                                .getNamespace("http://org.cagrid.csm/1/CSMAuthorization"));
                        }
                        Element extensionsEl = (Element) extensions.clone();
                        Element csmElementEl = (Element) csmextension.clone();
                        Element extensionDataEl = (Element) extensionData.clone();
                        extensionsEl.addContent(csmElementEl);
                        csmElementEl.addContent(extensionDataEl);
                        extensionDataEl.addContent((Element) csmElement.clone());

                        service.addContent(extensionsEl);
                    }
                }

            }

            Element methodsEl = service.getChild("Methods", Namespace
                .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Methods"));
            List methods = methodsEl.getChildren("Method", Namespace
                .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Methods"));
            for (int methodI = 0; methodI < methods.size(); methodI++) {
                Element method = (Element) methods.get(methodI);
                Element methodSecurity = method.getChild("MethodSecurity", Namespace
                    .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
                if (methodSecurity != null) {
                    Element methodAuthorization = methodSecurity.getChild("MethodAuthorization", Namespace
                        .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
                    if (methodAuthorization != null) {

                        Element gridGrouperElement = methodAuthorization.getChild("GridGrouperAuthorization", Namespace
                            .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
                        if (gridGrouperElement != null) {
                            methodAuthorization.removeContent(gridGrouperElement);
                            methodAuthorization.addContent((Element) introducePDPElement.clone());

                            gridGrouperElement.detach();
                            gridGrouperElement.setName("MembershipExpression");
                            gridGrouperElement.setNamespace(Namespace
                                .getNamespace("http://cagrid.nci.nih.gov/1/GridGrouper"));

                            Element extensionsEl = (Element) extensions.clone();
                            Element ggElementEl = (Element) ggextension.clone();
                            Element extensionDataEl = (Element) extensionData.clone();
                            extensionsEl.addContent(ggElementEl);
                            ggElementEl.addContent(extensionDataEl);
                            extensionDataEl.addContent((Element) gridGrouperElement.clone());

                            method.addContent(extensionsEl);

                        }
                        Element csmElement = methodAuthorization.getChild("CSMAuthorization", Namespace
                            .getNamespace("gme://gov.nih.nci.cagrid.introduce/1/Security"));
                        if (csmElement != null) {
                            methodAuthorization.removeContent(csmElement);
                            methodAuthorization.addContent((Element) introducePDPElement.clone());

                            csmElement.detach();
                            csmElement.setName("CSMAuthorization");
                            csmElement.setNamespace(Namespace.getNamespace("http://org.cagrid.csm/1/CSMAuthorization"));
                            for (int childI = 0; childI < csmElement.getChildren().size(); childI++) {
                                ((Element) csmElement.getChildren().get(childI)).setNamespace(Namespace
                                    .getNamespace("http://org.cagrid.csm/1/CSMAuthorization"));
                            }
                            Element extensionsEl = (Element) extensions.clone();
                            Element csmElementEl = (Element) csmextension.clone();
                            Element extensionDataEl = (Element) extensionData.clone();
                            extensionsEl.addContent(csmElementEl);
                            csmElementEl.addContent(extensionDataEl);
                            extensionDataEl.addContent((Element) csmElement.clone());

                            method.addContent(extensionsEl);
                        }
                    }

                }
            }
        }

        FileWriter writer = new FileWriter(getServicePath() + File.separator + "introduce.xml");
        writer.write(XMLUtilities.formatXML(XMLUtilities.documentToString(doc)));
        writer.close();
    }
}
