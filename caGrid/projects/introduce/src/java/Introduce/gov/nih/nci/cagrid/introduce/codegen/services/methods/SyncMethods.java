package gov.nih.nci.cagrid.introduce.codegen.services.methods;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.common.SyncTool;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.codegen.provider.ProviderTools;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.util.JavaParser;
import org.jdom.Document;
import org.jdom.Element;


/**
 * SyncMethodsOnDeployment
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncMethods extends SyncTool {
    private static final Logger logger = Logger.getLogger(SyncMethods.class);
    private List additions;
    private List removals;
    private List modifications;
    private ServiceType service;


    public SyncMethods(File baseDirectory, ServiceInformation info, ServiceType service) {
        super(baseDirectory, info);
        this.service = service;
        this.additions = new ArrayList();
        this.removals = new ArrayList();
        this.modifications = new ArrayList();
    }


    public void sync() throws SynchronizationException {

        // sync up the service.wsdd with respect to this particular service....
        try {
            syncServiceDeploymentDescriptor(new SpecificServiceInformation(getServiceInformation(), service));
        } catch (Exception e1) {
            throw new SynchronizationException(e1.getMessage(), e1);
        }

        this.lookForUpdates();

        // sync the methods files

        try {
            SyncSource methodSync = new SyncSource(getBaseDirectory(), getServiceInformation(), service);
            // remove methods
            methodSync.removeMethods(this.removals);
            // add new methods
            methodSync.addMethods(this.additions);
            // modify method signatures
            methodSync.modifyMethods(this.modifications);
        } catch (Exception e) {
            throw new SynchronizationException(e.getMessage(), e);
        }
    }


    public void lookForUpdates() throws SynchronizationException {
        JavaSource sourceI = null;
        JavaSource sourceImpl = null;
        JavaSourceFactory jsf;
        JavaParser jp;

        jsf = new JavaSourceFactory();
        jp = new JavaParser(jsf);

        String serviceInterface = getBaseDirectory().getAbsolutePath() + File.separator + "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator + "common" + File.separator + service.getName()
            + "I.java";
        String serviceI = getBaseDirectory().getAbsolutePath() + File.separator + "src" + File.separator
            + CommonTools.getPackageDir(service) + File.separator + "service" + File.separator + service.getName()
            + "Impl.java";

        try {
            jp.parse(new File(serviceInterface));
            jp.parse(new File(serviceI));

        } catch (Exception e) {
            throw new SynchronizationException("Error parsing service interface:" + e.getMessage(), e);
        }
        Iterator it = jsf.getJavaSources();
        while (it.hasNext()) {
            JavaSource source = (JavaSource) it.next();
            if (source.getQName().getClassName().endsWith("I")) {
                sourceI = source;
                
                sourceI.setForcingFullyQualifiedName(true);
            } else if (source.getQName().getClassName().endsWith("Impl")) {
                sourceImpl = source;
                sourceImpl.setForcingFullyQualifiedName(true);
            }
        }
        JavaMethod[] methods = sourceI.getMethods();
        JavaMethod[] implMethods = sourceImpl.getMethods();

        // look at doc and compare to interface
        if (service.getMethods() != null && service.getMethods().getMethod() != null) {
            for (int methodIndex = 0; methodIndex < service.getMethods().getMethod().length; methodIndex++) {
                MethodType mel = service.getMethods().getMethod(methodIndex);
                boolean found = false;
                for (int i = 0; i < methods.length; i++) {
                    String methodName = methods[i].getName();
                    if (CommonTools.lowerCaseFirstCharacter(mel.getName()).equals(methodName)) {
                        found = true;
                        // get the impl method as well....
                        JavaMethod implMethod = null;
                        for (int j = 0; j < implMethods.length; j++) {
                            String implMethodName = implMethods[j].getName();
                            if (CommonTools.lowerCaseFirstCharacter(mel.getName()).equals(implMethodName)) {
                                implMethod = implMethods[j];

                                break;
                            }
                        }
                        this.modifications.add(new Modification(mel, methods[i], implMethod));
                        break;
                    }
                }
                if (!found
                    && !CommonTools.lowerCaseFirstCharacter(mel.getName()).equals(
                        IntroduceConstants.SERVICE_SECURITY_METADATA_METHOD)) {
                    logger.debug("Found a method for addition: " + mel.getName());
                    this.additions.add(mel);
                }
            }
        }

        // look at interface and compare to doc
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();
            boolean found = false;
            if (service.getMethods().getMethod() != null) {
                for (int methodIndex = 0; methodIndex < service.getMethods().getMethod().length; methodIndex++) {
                    MethodType mel = service.getMethods().getMethod(methodIndex);
                    if (CommonTools.lowerCaseFirstCharacter(mel.getName()).equals(methodName)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                logger.debug("Found a method for removal: " + methodName);
                this.removals.add(methods[i]);

                String introduceXML = getBaseDirectory() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE
                    + ".prev";
                File introduceXMLFile = new File(introduceXML);
                if (introduceXMLFile.exists() || introduceXMLFile.canRead()) {

                    try {
                        ServiceDescription oldIntroServiceDesc = (ServiceDescription) Utils.deserializeDocument(
                            introduceXML, ServiceDescription.class);
                        ServiceType oldService = CommonTools.getService(oldIntroServiceDesc.getServices(), service
                            .getName());
                        MethodType oldMethod = CommonTools.getMethod(oldService.getMethods(), methodName);
                        if (oldMethod.isIsProvided()) {
                            ProviderTools.removeProviderFromServiceConfig(service, oldMethod.getProviderInformation().getProviderClass(),
                                getServiceInformation());
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }

                }
            }
        }
    }


    private void syncServiceDeploymentDescriptor(SpecificServiceInformation ssi) throws Exception {

        // need to add in any new services into the service.wsdd
        File serverConfigF = new File(getBaseDirectory().getAbsolutePath() + File.separator + "server-config.wsdd");

        Document serverConfigDoc = XMLUtilities.fileNameToDocument(serverConfigF.getAbsolutePath());

        // we need to now find the "service" element so that we can update any
        // parameters we need to update
        List serviceEls = serverConfigDoc.getRootElement().getChildren("service",
            serverConfigDoc.getRootElement().getNamespace());
        Element serviceConfigEl = null;
        for (int serviceElI = 0; serviceElI < serviceEls.size(); serviceElI++) {
            Element serviceEl = (Element) serviceEls.get(serviceElI);
            if (serviceEl.getAttributeValue("name").endsWith("/" + ssi.getService().getName())) {
                serviceConfigEl = serviceEl;
                break;
            }
        }

        Element parameterEl = null;
        if (serviceConfigEl != null) {
            List parameters = serviceConfigEl.getChildren("parameter", serviceConfigEl.getNamespace());
            for (int parameterI = 0; parameterI < parameters.size(); parameterI++) {
                Element tparameterEl = (Element) parameters.get(parameterI);
                if (tparameterEl.getAttributeValue("name").equals("providers")) {
                    parameterEl = tparameterEl;
                    break;
                }
            }
        } else {
            throw new Exception("could not find the \"service\" element in the service.wsdd for the service: "
                + ssi.getService().getName());
        }

        String providerParamString = "";
        if (parameterEl != null) {
            providerParamString = parameterEl.getAttributeValue("value");
            StringTokenizer strtok = new StringTokenizer(providerParamString, " ", false);
            List providers = new ArrayList();
            while (strtok.hasMoreTokens()) {
                providers.add(strtok.nextToken());
            }

            // walk the methods and add any providers that need to be added.....
            if (ssi.getService().getMethods() != null && ssi.getService().getMethods().getMethod() != null) {
                for (int methodI = 0; methodI < ssi.getService().getMethods().getMethod().length; methodI++) {
                    MethodType method = ssi.getService().getMethods().getMethod(methodI);
                    if (method.isIsProvided() && method.getProviderInformation() != null) {
                        if (!providers.contains(method.getProviderInformation().getProviderClass())) {
                            providers.add(method.getProviderInformation().getProviderClass());
                            providerParamString += " " + method.getProviderInformation().getProviderClass();
                        }

                    }
                }
            }

            parameterEl.setAttribute("value", providerParamString);

        } else {
            throw new Exception("could not find the \"providers\" parameter in the service.wsdd for the service: "
                + ssi.getService().getName());
        }

        String serverConfigS = XMLUtilities.formatXML(XMLUtilities.documentToString(serverConfigDoc));
        FileWriter serverConfigFW = new FileWriter(serverConfigF);
        serverConfigFW.write(serverConfigS);
        serverConfigFW.close();

    }

}
