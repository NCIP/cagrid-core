package gov.nih.nci.cagrid.introduce.upgrade.introduce;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.ResourcePropertyManagement;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;
import gov.nih.nci.cagrid.introduce.creator.SkeletonSecurityOperationProviderCreator;
import gov.nih.nci.cagrid.introduce.templates.client.ClientConfigTemplate;
import gov.nih.nci.cagrid.introduce.templates.client.ServiceClientBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.client.ServiceClientTemplate;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceConstantsBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.common.ServiceConstantsTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.ServiceConfigurationTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceBaseTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceHomeTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.ResourceTemplate;
import gov.nih.nci.cagrid.introduce.templates.service.globus.resource.SingletonResourceHomeTemplate;
import gov.nih.nci.cagrid.introduce.upgrade.common.IntroduceUpgradeStatus;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.x.IntroduceUpgraderBase;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class Introduce_1_1__1_3_Upgrader extends IntroduceUpgraderBase {

    public Introduce_1_1__1_3_Upgrader(IntroduceUpgradeStatus status, ServiceInformation serviceInformation,
        String servicePath) throws Exception {
        super(status, serviceInformation, servicePath, "1.1", "1.3");
    }


    @Override
    protected void upgrade() throws Exception {
        // need to make sure to save a copy of hte introduce.xml to a prev file
        // so that the
        // sync tools can pick up any service changes i make here.....
        // make a copy of the model to compae with next time
        Utils.copyFile(new File(getServicePath() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE), new File(
            getServicePath() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE + ".prev"));

        // make a copy of the properties to compare with next time
        Utils.copyFile(new File(getServicePath() + File.separator + IntroduceConstants.INTRODUCE_PROPERTIES_FILE),
            new File(getServicePath() + File.separator + IntroduceConstants.INTRODUCE_PROPERTIES_FILE + ".prev"));

        // add the resource property management to the main service
        ServiceType mainService = getServiceInformation().getServices().getService(0);
        mainService.getResourceFrameworkOptions().setResourcePropertyManagement(new ResourcePropertyManagement());

        // need to replace the build.xml
        Utils.copyFile(new File(getServicePath() + File.separator + "build.xml"), new File(getServicePath()
            + File.separator + "build.xml.OLD"));
        Utils.copyFile(new File(getServicePath() + File.separator + "build-deploy.xml"), new File(getServicePath()
            + File.separator + "build-deploy.xml.OLD"));
        Utils.copyFile(new File("." + File.separator + "skeleton" + File.separator + "build.xml"), new File(
            getServicePath() + File.separator + "build.xml"));
        Utils.copyFile(new File("." + File.separator + "skeleton" + File.separator + "build-deploy.xml"), new File(
            getServicePath() + File.separator + "build-deploy.xml"));
        getStatus().addDescriptionLine("replaced build.xml and build-deploy.xml with new version");

        StringBuffer devsb = Utils.fileToStringBuffer(new File(getServicePath() + File.separator
            + "dev-build-deploy.xml"));
        int eof = devsb.indexOf("</project>");
        String addition = "\n\n" + "\t<!-- ============================================================== -->\n"
            + "\t<!-- Post Undeploy Tomcat                                           -->\n"
            + "\t<!-- ============================================================== -->\n"
            + "\t<target name=\"postUndeployTomcat\">\n" + "\t</target>\n\n"
            + "\t<!-- ============================================================== -->\n"
            + "\t<!-- Post Undeploy Globus                                           -->\n"
            + "\t<!-- ============================================================== -->\n"
            + "\t<target name=\"postUndeployGlobus\">\n" + "\t</target>\n\n"
            + "\t<!-- ============================================================== -->\n"
            + "\t<!-- Post Undeploy JBOSS                                            -->\n"
            + "\t<!-- ============================================================== -->\n"
            + "\t<target name=\"postUndeployJBoss\">\n" + "\t</target>\n\n";
        devsb.insert(eof, addition);
        FileWriter fw = new FileWriter(new File(getServicePath() + File.separator + "dev-build-deploy.xml"));
        fw.write(devsb.toString());
        fw.close();

        getStatus().addDescriptionLine("updated build-deploy.xml with new version");

        Utils.copyFile(new File("." + File.separator + "skeleton" + File.separator + "build-stubs.xml"), new File(
            getServicePath() + File.separator + "build-stubs.xml"));
        getStatus().addDescriptionLine("added build-stubs.xml");

        // Copy over the new ServiceSecurity.wsdl
        File newWLocation = new File("ext" + File.separator + "dependencies" + File.separator + "wsdl" + File.separator
            + "ServiceSecurity.wsdl");
        File oldWLocation = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator
            + "schema" + File.separator + getServiceInformation().getServices().getService(0).getName()
            + File.separator + "ServiceSecurity.wsdl");
        Utils.copyFile(newWLocation, oldWLocation);

        // change the location of the services security.xsd
        NamespaceType nsType = CommonTools.getNamespaceType(getServiceInformation().getNamespaces(),
            "gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.security");
        String oldLocation = nsType.getLocation();
        String newLocation = "./xsd/cagrid/types/security/security.xsd";

        if (!oldLocation.equals(newLocation)) {
            nsType.setLocation(newLocation);
            // move to this new location
            File newLocationF = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator
                + "schema" + File.separator + getServiceInformation().getServices().getService(0).getName()
                + File.separator + "xsd" + File.separator + "cagrid" + File.separator + "types" + File.separator
                + "security" + File.separator + "security.xsd");
            File oldLocationF = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator
                + "schema" + File.separator + getServiceInformation().getServices().getService(0).getName()
                + File.separator + "xsd" + File.separator + "security.xsd");
            Utils.copyFile(oldLocationF, newLocationF);
            oldLocationF.delete();
            getStatus()
                .addIssue(
                    "Moved security.xsd to new location xsd/cagrid/types/security/security.xsd",
                    "Please make sure that if you are importing this schema in any other location that you make sure to edit the imports and use the new security.xsd location in xsd/cagrid/types/security/security.xsd.");
        }

        // foreach service.....
        File srcDir = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator + "src");
        for (int i = 0; i < getServiceInformation().getServices().getService().length; i++) {
            ServiceType service = getServiceInformation().getServices().getService(i);

            // replace the get service security metadata method in the
            // introduce.xml
            CommonTools.removeMethod(service.getMethods(), CommonTools.getMethod(service.getMethods(),
                "getServiceSecurityMetadata"));
            SkeletonSecurityOperationProviderCreator secCreator = new SkeletonSecurityOperationProviderCreator();
            secCreator.createSkeleton(new SpecificServiceInformation(getServiceInformation(), service));

            ServiceClientTemplate clientT = new ServiceClientTemplate();
            String clientS = clientT.generate(new SpecificServiceInformation(getServiceInformation(), service));
            File clientF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
                + File.separator + "client" + File.separator + service.getName() + "Client.java");

            FileWriter clientFW = new FileWriter(clientF);
            clientFW.write(clientS);
            clientFW.close();

            ServiceClientBaseTemplate clientBaseT = new ServiceClientBaseTemplate();
            String clientBaseS = clientBaseT.generate(new SpecificServiceInformation(getServiceInformation(), service));
            File clientBaseF = new File(srcDir.getAbsolutePath() + File.separator + CommonTools.getPackageDir(service)
                + File.separator + "client" + File.separator + service.getName() + "ClientBase.java");

            FileWriter clientBaseFW = new FileWriter(clientBaseF);
            clientBaseFW.write(clientBaseS);
            clientBaseFW.close();

            ClientConfigTemplate clientConfigT = new ClientConfigTemplate();
            String clientConfigS = clientConfigT.generate(new SpecificServiceInformation(getServiceInformation(),
                service));
            File clientConfigF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "client" + File.separator
                + "client-config.wsdd");
            FileWriter clientConfigFW = new FileWriter(clientConfigF);
            clientConfigFW.write(clientConfigS);
            clientConfigFW.close();

            File oldConstantsFile = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                + File.separator + "resource" + File.separator + "ResourceConstants.java");
            oldConstantsFile.delete();

            ServiceConstantsTemplate resourceContanstsT = new ServiceConstantsTemplate();
            String resourceContanstsS = resourceContanstsT.generate(new SpecificServiceInformation(
                getServiceInformation(), service));
            File resourceContanstsF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + File.separator
                + service.getName() + "Constants.java");

            FileWriter resourceContanstsFW = new FileWriter(resourceContanstsF);
            resourceContanstsFW.write(resourceContanstsS);
            resourceContanstsFW.close();

            ServiceConstantsBaseTemplate resourcebContanstsT = new ServiceConstantsBaseTemplate();
            String resourcebContanstsS = resourcebContanstsT.generate(new SpecificServiceInformation(
                getServiceInformation(), service));
            File resourcebContanstsF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + service.getName()
                + "ConstantsBase.java");

            FileWriter resourcebContanstsFW = new FileWriter(resourcebContanstsF);
            resourcebContanstsFW.write(resourcebContanstsS);
            resourcebContanstsFW.close();

            if (service.getResourceFrameworkOptions().getMain() != null) {

                File oldServiceConfF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator
                    + "ServiceConfiguration.java");
                oldServiceConfF.delete();

                ServiceConfigurationTemplate serviceConfT = new ServiceConfigurationTemplate();
                String serviceConfS = serviceConfT.generate(new SpecificServiceInformation(getServiceInformation(),
                    service));
                File serviceConfF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator
                    + service.getName() + "Configuration.java");
                if (serviceConfF.exists()) {
                    throw new Exception(
                        "Introduce is trying to create the class "
                            + serviceConfF.getAbsolutePath()
                            + " but this file already exists.  You will need to rename the existing class to something else before you try to update to this new version.");
                }
                FileWriter serviceConfFW = new FileWriter(serviceConfF);
                serviceConfFW.write(serviceConfS);
                serviceConfFW.close();
            }

            if (service.getResourceFrameworkOptions().getCustom() == null) {
                // delete the old base resource
                File oldbaseResourceF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "Resource.java");
                File oldbaseResourceFRename = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "ResourceOLD.java.txt");
                Utils.copyFile(oldbaseResourceF, oldbaseResourceFRename);
                oldbaseResourceF.delete();
                getStatus().addIssue(
                    "Generated a new Resource implementation",
                    "The old resource implementation has been written to " + oldbaseResourceFRename.getAbsolutePath()
                        + ". Be sure to copy back over any modified code back into the new file.");

                File oldDaseResourceHomeF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + "BaseResourceHome.java");
                oldDaseResourceHomeF.delete();

                File oldBaseResourceBaseF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + "BaseResourceBase.java");
                oldBaseResourceBaseF.delete();

                File oldResourceConfigurationF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + "ResourceConfiguration.java");
                oldResourceConfigurationF.delete();

                ResourceBaseTemplate baseResourceBaseT = new ResourceBaseTemplate();
                String baseResourceBaseS = baseResourceBaseT.generate(new SpecificServiceInformation(
                    getServiceInformation(), service));
                File baseResourceBaseF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "ResourceBase.java");

                FileWriter baseResourceBaseFW = new FileWriter(baseResourceBaseF);
                baseResourceBaseFW.write(baseResourceBaseS);
                baseResourceBaseFW.close();

                ResourceTemplate baseResourceT = new ResourceTemplate();
                String baseResourceS = baseResourceT.generate(new SpecificServiceInformation(getServiceInformation(),
                    service));
                File baseResourceF = new File(srcDir.getAbsolutePath() + File.separator
                    + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                    + File.separator + "resource" + File.separator + service.getName() + "Resource.java");

                FileWriter baseResourceFW = new FileWriter(baseResourceF);
                baseResourceFW.write(baseResourceS);
                baseResourceFW.close();

                if (service.getResourceFrameworkOptions().getSingleton() != null) {
                    SingletonResourceHomeTemplate baseResourceHomeT = new SingletonResourceHomeTemplate();
                    String baseResourceHomeS = baseResourceHomeT.generate(new SpecificServiceInformation(
                        getServiceInformation(), service));
                    File baseResourceHomeF = new File(srcDir.getAbsolutePath() + File.separator
                        + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                        + File.separator + "resource" + File.separator + service.getName() + "ResourceHome.java");

                    FileWriter baseResourceHomeFW = new FileWriter(baseResourceHomeF);
                    baseResourceHomeFW.write(baseResourceHomeS);
                    baseResourceHomeFW.close();

                } else {

                    ResourceHomeTemplate baseResourceHomeT = new ResourceHomeTemplate();
                    String baseResourceHomeS = baseResourceHomeT.generate(new SpecificServiceInformation(
                        getServiceInformation(), service));
                    File baseResourceHomeF = new File(srcDir.getAbsolutePath() + File.separator
                        + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + "globus"
                        + File.separator + "resource" + File.separator + service.getName() + "ResourceHome.java");

                    FileWriter baseResourceHomeFW = new FileWriter(baseResourceHomeF);
                    baseResourceHomeFW.write(baseResourceHomeS);
                    baseResourceHomeFW.close();

                }

                getStatus().addDescriptionLine("Updated many source files to be better editable and extendable");
            }
        }

        fixTypesSchemas();
        upgradeJars();
        fixJNDI();
        fixConstants();
        fixWSDD();

        getStatus().setStatus(StatusBase.UPGRADE_OK);
    }


    private void fixTypesSchemas() throws Exception {
        for (int i = 0; i < getServiceInformation().getServices().getService().length; i++) {
            ServiceType service = getServiceInformation().getServices().getService(i);
            File typesFile = new File(getServiceInformation().getBaseDirectory() + File.separator + "schema"
                + File.separator + getServiceInformation().getServices().getService(0).getName() + File.separator
                + service.getName() + "Types.xsd");
            Document doc = XMLUtilities.fileNameToDocument(typesFile.getAbsolutePath());
            boolean needtoAddFaultsImports = true;
            boolean needtoAddAdressingImports = true;
            List importsl = doc.getRootElement().getChildren("import",
                Namespace.getNamespace(IntroduceConstants.W3CNAMESPACE));
            if (importsl != null && importsl.size() > 0) {
                Iterator it = importsl.iterator();
                while (it.hasNext()) {
                    Element el = (Element) it.next();
                    if (el.getAttributeValue("namespace") != null
                        && el.getAttributeValue("namespace").equals("http://schemas.xmlsoap.org/ws/2004/03/addressing")) {
                        needtoAddAdressingImports = false;
                    } else if (el.getAttributeValue("namespace") != null
                        && el.getAttributeValue("namespace").equals(
                            "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-BaseFaults-1.2-draft-01.xsd")) {
                        needtoAddFaultsImports = false;
                    }
                }
            }
            if (needtoAddAdressingImports) {
                Element addressingImport = new Element("import", Namespace
                    .getNamespace(IntroduceConstants.W3CNAMESPACE));
                addressingImport.setAttribute("namespace", "http://schemas.xmlsoap.org/ws/2004/03/addressing");
                addressingImport.setAttribute("schemaLocation", "../ws/addressing/WS-Addressing.xsd");
                doc.getRootElement().addContent(0, addressingImport);
            }
            if (needtoAddFaultsImports) {
                Element faultImport = new Element("import", Namespace.getNamespace(IntroduceConstants.W3CNAMESPACE));
                faultImport.setAttribute("namespace",
                    "http://docs.oasis-open.org/wsrf/2004/06/wsrf-WS-BaseFaults-1.2-draft-01.xsd");
                faultImport.setAttribute("schemaLocation", "../wsrf/faults/WS-BaseFaults.xsd");
                doc.getRootElement().addContent(0, faultImport);
            }

            FileWriter writer = new FileWriter(typesFile);
            writer.write(XMLUtilities.formatXML(XMLUtilities.documentToString(doc)));
            writer.close();
        }
    }


    private void fixJNDI() throws Exception {

        // change the jndi to use the new classes names for resource home and
        // resource configuration and service configuration
        Document jndiDoc = XMLUtilities.fileNameToDocument(getServicePath() + File.separator + "jndi-config.xml");
        List services = jndiDoc.getRootElement().getChildren("service",
            Namespace.getNamespace("http://wsrf.globus.org/jndi/config"));
        Iterator serviceI = services.iterator();
        while (serviceI.hasNext()) {
            Element service = (Element) serviceI.next();
            String serviceName = service.getAttributeValue("name");
            serviceName = serviceName.substring(serviceName.lastIndexOf("/") + 1);
            List resources = service.getChildren("resource", Namespace
                .getNamespace("http://wsrf.globus.org/jndi/config"));
            Iterator resourceI = resources.iterator();
            while (resourceI.hasNext()) {
                Element resource = (Element) resourceI.next();
                if (resource.getAttributeValue("name").equals("home")) {
                    String type = resource.getAttributeValue("type");
                    if (type.endsWith(".BaseResourceHome")) {
                        StringBuffer sb = new StringBuffer(type);
                        sb.delete(sb.lastIndexOf(".") + 1, sb.length());
                        sb.insert(sb.lastIndexOf(".") + 1, serviceName + "ResourceHome");
                        resource.setAttribute("type", sb.toString());
                    }
                } else if (resource.getAttributeValue("name").equals("configuration")) {
                    String type = resource.getAttributeValue("type");
                    StringBuffer sb = new StringBuffer(type);
                    sb.delete(sb.lastIndexOf(".") + 1, sb.length());
                    sb.insert(sb.lastIndexOf(".") + 1, serviceName + "ResourceConfiguration");
                    resource.setAttribute("type", sb.toString());
                } else if (resource.getAttributeValue("name").equals("serviceconfiguration")) {
                    String type = resource.getAttributeValue("type");
                    StringBuffer sb = new StringBuffer(type);
                    sb.delete(sb.lastIndexOf(".") + 1, sb.length());
                    sb.insert(sb.lastIndexOf(".") + 1, serviceName + "ServiceConfiguration");
                    resource.setAttribute("type", sb.toString());
                }
            }
        }
        FileWriter writer = new FileWriter(new File(getServicePath() + File.separator + "jndi-config.xml"));
        writer.write(XMLUtilities.formatXML(XMLUtilities.documentToString(jndiDoc)));
        writer.close();
        getStatus().addDescriptionLine(
            "changed jndi file to use new names of the resource home and configureation classes");

    }


    private final class OldJarsFilter implements FileFilter {
        boolean hadGridGrouperJars = false;
        boolean hadCSMJars = false;


        public boolean accept(File name) {
            String filename = name.getName();
            boolean core = filename.startsWith("caGrid-1.1-core") && filename.endsWith(".jar");
            boolean advertisement = filename.startsWith("caGrid-1.1-advertisement") && filename.endsWith(".jar");
            boolean metadata = filename.startsWith("caGrid-1.1-metadata-common") && filename.endsWith(".jar");
            boolean introduce = filename.startsWith("caGrid-1.1-Introduce") && filename.endsWith(".jar");
            boolean security = (filename.startsWith("caGrid-1.1-ServiceSecurityProvider") || filename
                .startsWith("caGrid-1.1-metadata-security"))
                && filename.endsWith(".jar");
            boolean gridGrouper = (filename.startsWith("caGrid-1.1-gridgrouper")) && filename.endsWith(".jar");
            if (gridGrouper) {
                this.hadGridGrouperJars = true;
            }
            boolean csm = (filename.startsWith("caGrid-1.1-authz-common")) && filename.endsWith(".jar");
            if (csm) {
                this.hadCSMJars = true;
            }

            boolean otherSecurityJarsNotNeeded = (filename.startsWith("caGrid-1.1-gridca"))
                && filename.endsWith(".jar");

            boolean wsrf = (filename.startsWith("globus_wsrf_mds") || filename.startsWith("globus_wsrf_servicegroup"))
                && filename.endsWith(".jar");
            boolean mobius = filename.startsWith("mobius") && filename.endsWith(".jar");

            return core || advertisement || metadata || introduce || security || gridGrouper || csm || wsrf || mobius
                || otherSecurityJarsNotNeeded;
        }

    };


    private void upgradeJars() throws Exception {

        OldJarsFilter oldDkeletonLibFilter = new OldJarsFilter();

        // locate the old libs in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] serviceLibs = serviceLibDir.listFiles(oldDkeletonLibFilter);
        // delete the old libraries
        for (File serviceLib : serviceLibs) {
            boolean deleted = serviceLib.delete();
            if (deleted) {
                getStatus().addDescriptionLine(serviceLib.getName() + " removed");
            } else {
                getStatus().addDescriptionLine(serviceLib.getName() + " could not be removed");
            }
        }

        FileFilter srcSkeletonLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return filename.endsWith(".jar");
            }
        };

        File skeletonLibDir = new File("skeleton" + File.separator + "lib");

        // copy new libraries in (every thing in skeleton/lib)
        File[] skeletonLibs = skeletonLibDir.listFiles(srcSkeletonLibFilter);
        for (File skeletonLib : skeletonLibs) {
            File out = new File(serviceLibDir.getAbsolutePath() + File.separator + skeletonLib.getName());
            try {
                Utils.copyFile(skeletonLib, out);
                getStatus().addDescriptionLine(skeletonLib.getName() + " added");
            } catch (IOException ex) {
                throw new Exception("Error copying library (" + skeletonLib + ") to service: " + ex.getMessage(), ex);
            }
        }

        getStatus().addDescriptionLine("updating service with the new version of the jars");

        // replacing the soap fix jar with the new service tasks jar
        File oldSoapJar = new File(getServicePath() + File.separator + "tools" + File.separator + "lib"
            + File.separator + "caGrid-1.1-Introduce-1.1-soapBindingFix.jar");
        if (oldSoapJar.exists() && oldSoapJar.canRead()) {
            oldSoapJar.delete();
        } else {
            throw new Exception("Cannot remove old soap fix jar: " + oldSoapJar.delete());
        }

        FileFilter srcSkeletonToolsLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return filename.endsWith(".jar");
            }
        };
        // copy new libraries into tools (every thing in skeleton/tool/lib)
        File serviceToolsLibDir = new File(getServicePath() + File.separator + "tools" + File.separator + "lib");
        File skeletonToolsLibDir = new File("skeleton" + File.separator + "tools" + File.separator + "lib");
        File[] skeletonToolsLibs = skeletonToolsLibDir.listFiles(srcSkeletonToolsLibFilter);
        for (File skeletonToolsLib : skeletonToolsLibs) {
            File out = new File(serviceToolsLibDir.getAbsolutePath() + File.separator + skeletonToolsLib.getName());
            try {
                Utils.copyFile(skeletonToolsLib, out);
                getStatus().addDescriptionLine(skeletonToolsLib.getName() + " added");
            } catch (IOException ex) {
                throw new Exception("Error copying library (" + skeletonToolsLib + ") to service: " + ex.getMessage(),
                    ex);
            }
        }

    }


    protected void fixConstants() throws Exception {
        File srcDir = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator + "src");
        for (int serviceI = 0; serviceI < getServiceInformation().getServices().getService().length; serviceI++) {
            ServiceType service = getServiceInformation().getServices().getService(serviceI);
            // add new constants base class and new constants class
            ServiceConstantsTemplate resourceContanstsT = new ServiceConstantsTemplate();
            String resourceContanstsS = resourceContanstsT.generate(new SpecificServiceInformation(
                getServiceInformation(), service));
            File resourceContanstsF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + File.separator
                + service.getName() + "Constants.java");

            FileWriter resourceContanstsFW = new FileWriter(resourceContanstsF);
            resourceContanstsFW.write(resourceContanstsS);
            resourceContanstsFW.close();

            ServiceConstantsBaseTemplate resourcebContanstsT = new ServiceConstantsBaseTemplate();
            String resourcebContanstsS = resourcebContanstsT.generate(new SpecificServiceInformation(
                getServiceInformation(), service));
            File resourcebContanstsF = new File(srcDir.getAbsolutePath() + File.separator
                + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + service.getName()
                + "ConstantsBase.java");

            FileWriter resourcebContanstsFW = new FileWriter(resourcebContanstsF);
            resourcebContanstsFW.write(resourcebContanstsS);
            resourcebContanstsFW.close();
        }

        getStatus().addDescriptionLine("Refactored Constants file to now be developer editable");
    }


    protected void fixWSDD() throws Exception {
        Document doc = XMLUtilities.fileNameToDocument(getServiceInformation().getBaseDirectory() + File.separator
            + "server-config.wsdd");
        List servicesEls = doc.getRootElement().getChildren("service",
            Namespace.getNamespace("http://xml.apache.org/axis/wsdd/"));
        for (int serviceI = 0; serviceI < servicesEls.size(); serviceI++) {
            Element serviceEl = (Element) servicesEls.get(serviceI);
            ServiceType service = CommonTools.getService(getServiceInformation().getServices(), serviceEl
                .getAttributeValue("name").substring(serviceEl.getAttributeValue("name").lastIndexOf("/") + 1));

            // need to add the service name att and the etc path att for each
            // service
            Element serviceName = new Element("parameter", Namespace.getNamespace("http://xml.apache.org/axis/wsdd/"));
            serviceName.setAttribute("name", service.getName().toLowerCase() + "-serviceName");
            serviceName.setAttribute("value", service.getName());

            Element serviceETC = new Element("parameter", Namespace.getNamespace("http://xml.apache.org/axis/wsdd/"));
            serviceETC.setAttribute("name", service.getName().toLowerCase() + "-etcDirectoryPath");
            serviceETC.setAttribute("value", "ETC-PATH");

            serviceEl.addContent(serviceETC);
            serviceEl.addContent(serviceName);

        }

        FileWriter fw = new FileWriter(getServiceInformation().getBaseDirectory() + File.separator
            + "server-config.wsdd");
        fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(doc)));
        fw.close();

        getStatus().addDescriptionLine("Regenerated service-config.wsdd");
    }

}
