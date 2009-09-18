package gov.nih.nci.cagrid.introduce.codegen.utils;

import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.codegen.common.SynchronizationException;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ImportInformation;
import gov.nih.nci.cagrid.introduce.common.NamespaceInformation;
import gov.nih.nci.cagrid.introduce.common.SpecificServiceInformation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


/**
 * Templating Utility Functions
 * 
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @created Jun 8, 2005
 * @version $Id: mobiusEclipseCodeTemplates.xml,v 1.2 2005/04/19 14:58:02 oster
 *          Exp $
 */
public class SyncUtils {
    private static final Logger logger = Logger.getLogger(SyncUtils.class);


    public static Map buildMasterNamespaceInformationMap(ServiceDescription desc) {
        Map map = new HashMap();
        int namespaceCount = 0;
        if (desc.getNamespaces() != null && desc.getNamespaces().getNamespace() != null) {
            for (int i = 0; i < desc.getNamespaces().getNamespace().length; i++) {
                NamespaceType ntype = desc.getNamespaces().getNamespace(i);
                // add the ns=>prefix entry
                if (!map.containsKey(ntype.getNamespace())) {
                    if (ntype.getNamespace().equals(IntroduceConstants.W3CNAMESPACE)) {
                        map.put(ntype.getNamespace(), new NamespaceInformation(ntype,
                            IntroduceConstants.W3CNAMESPACE_PREFIX));
                    } else if (ntype.getNamespace().equals("http://www.w3.org/XML/1998/namespace")) {
                        map.put(ntype.getNamespace(), new NamespaceInformation(ntype, "xml"));
                    } else {
                        map.put(ntype.getNamespace(), new NamespaceInformation(ntype, "ns" + namespaceCount++));
                    }
                }
            }
        }

        return map;
    }


    public static void addImportedOperationToService(MethodType method, SpecificServiceInformation serviceInfo)
        throws Exception {

        String fromDocFile = serviceInfo.getBaseDirectory().getAbsolutePath()
            + File.separator
            + "schema"
            + File.separator
            + serviceInfo.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME) + File.separator
            + method.getImportInformation().getWsdlFile();
        String toDocFile = serviceInfo.getBaseDirectory().getAbsolutePath()
            + File.separator
            + "schema"
            + File.separator
            + serviceInfo.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME) + File.separator
                + serviceInfo.getService().getName() + ".wsdl";
        if (!(new File(fromDocFile).exists())) {
            // try From Globus Location
            fromDocFile = CommonTools.getGlobusLocation() + File.separator + "share" + File.separator + "schema"
                + File.separator + "wsrf" + File.separator + method.getImportInformation().getWsdlFile();
            logger.info("Imported operation's WSDL not found in service directory, using WSDL from globus...");
        }
        if (!(new File(fromDocFile).exists())) {
            throw new Exception("Cannot locate WSDL file: " + fromDocFile + " to import from for Method: "
                + method.getName());
        }

        // parse the wsdl and get the operation text.....
        Document fromWsdl = null;
        Document toWsdl = null;
        try {
            // read the wsdl we're importing an operation FROM
            fromWsdl = XMLUtilities.fileNameToDocument(fromDocFile);
            // read the service's wsdl we're writing TO
            toWsdl = XMLUtilities.fileNameToDocument(toDocFile);
        } catch (IOException ex) {
            logger.error(ex);
            throw ex;
        }
        
        // get the port type we're importing from
        Element importPortType = getPortTypeElement(fromWsdl.getRootElement(), 
            method.getImportInformation().getPortTypeName());
        if (importPortType == null) {
            String message = "Unable to locate port type in imported WSDL (" 
                + method.getImportInformation().getPortTypeName() + ")";
            logger.error(message);
            throw new SynchronizationException(message);
        }
        // get the operation from the import's port type
        Element importOperation = getOperationElement(importPortType, method.getName());
        if (importOperation == null) {
            String message = "Unable to locate operation in imported port type (" 
                + method.getName() + ")";
            logger.error(message);
            throw new SynchronizationException(message);
        }
        // detach the operation element so we can add it to our own WSDL later
        Element copyOperation = (Element) importOperation.detach();
        // find the port type within the service we're importing a method INTO
        Element servicePortType = getPortTypeElement(toWsdl.getRootElement(),
            serviceInfo.getService().getName() + "PortType");
        if (servicePortType == null) {
            String message = "Unable to locate port type in service's WSDL (" 
                + serviceInfo.getService().getName() + "PortType)";
            logger.error(message);
            throw new SynchronizationException(message);
        }
        // fix up the namespaceing of the imported operation
        List copyElemChildren = copyOperation.getChildren();
        Iterator childIter = copyElemChildren.iterator();
        while (childIter.hasNext()) {
            Element copyChild = (Element) childIter.next();
            String messageString = copyChild.getAttributeValue("message");
            logger.debug("Looking for namespace prefix for message " + messageString);
            Namespace ns = null;
            String prefix = "";
            String message = "";
            if (messageString.indexOf(":") >= 0) {
                prefix = messageString.substring(0, messageString.indexOf(":"));
                message = messageString.substring(messageString.indexOf(":") + 1);
                ns = fromWsdl.getRootElement().getNamespace(prefix);
            } else {
                message = messageString;
                ns = fromWsdl.getRootElement().getNamespace();
            }
            List toNamespaces = toWsdl.getRootElement().getAdditionalNamespaces();
            for (int namespaceIndex = 0; namespaceIndex < toNamespaces.size(); namespaceIndex++) {
                Namespace tempns = (Namespace) toNamespaces.get(namespaceIndex);
                if (tempns.getURI().equals(ns.getURI())) {
                    logger.debug("Setting message " + message + " nsPrefix: "
                        + tempns.getPrefix());
                    copyChild.setAttribute("message", tempns.getPrefix() + ":" + message);
                    break;
                }
            }
        }
        // add the operation to our service's port type
        servicePortType.addContent(copyOperation);
        
        // write out the modified service WSDL
        try {
            FileWriter fw = new FileWriter(toDocFile);
            fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(toWsdl)));
            fw.close();
        } catch (IOException ex) {
            String message = "Error writing modified service WSDL: " + ex.getMessage();
            logger.error(message, ex);
            throw new SynchronizationException(message, ex);
        }
    }
    
    
    private static Element getPortTypeElement(Element wsdlRoot, String portTypeName) {
        List portTypeElements = wsdlRoot.getChildren("portType", wsdlRoot.getNamespace());
        Iterator portTypeIter = portTypeElements.iterator();
        while (portTypeIter.hasNext()) {
            Element portType = (Element) portTypeIter.next();
            if (portTypeName.equals(portType.getAttributeValue("name"))) {
                return portType;
            }
        }
        return null;
    }
    
    
    private static Element getOperationElement(Element portType, String operationName) {
        List operationElements = portType.getChildren("operation", portType.getNamespace());
        Iterator operationIter = operationElements.iterator();
        while (operationIter.hasNext()) {
            Element operation = (Element) operationIter.next();
            if (operationName.equals(operation.getAttributeValue("name"))) {
                return operation;
            }
        }
        return null;
    }


    public static Map buildWSDLImportMap(ServiceType service) {
        Map map = new HashMap();
        int namespaceCount = 0;
        if (service.getMethods() != null && service.getMethods().getMethod() != null) {
            for (int i = 0; i < service.getMethods().getMethod().length; i++) {
                MethodType method = service.getMethods().getMethod(i);
                if (method.isIsImported()) {
                    if (!map.containsKey(method.getImportInformation().getNamespace())) {
                        ImportInformation ii = new ImportInformation(method.getImportInformation(), 
                            "wns" + namespaceCount++);
                        map.put(method.getImportInformation().getNamespace(), ii);
                    }
                }
            }
        }

        return map;
    }


    /**
     * Walks a schema tree, following imports and placing namespaces in the
     * namespaces set
     * 
     * @param schemaFile
     *            The <i><b>FULLY QUALIFIED</i></b> file name of an XML schema
     * @param namespaces
     *            The set of namespaces to populate
     * @param visitedSchemas
     *            The set of schemas already visited by this method
     * @throws Exception
     */
    public static void walkSchemasGetNamespaces(String schemaFile, Set namespaces, Set excludedNamespaces,
        Set visitedSchemas) throws Exception {
        internalWalkSchemasGetNamespaces(schemaFile, namespaces, excludedNamespaces, visitedSchemas,
            new HashSet<String>(), new HashSet<String>());
    }


    private static void internalWalkSchemasGetNamespaces(String schemaFile, Set<String> namespaces,
        Set<String> excludedNamespaces, Set<String> visitedSchemas, Set<String> cycleSchemas,
        Set<String> selfImportSchemas) throws Exception {
        logger.debug("Getting namespaces from schema " + schemaFile);
        visitedSchemas.add(schemaFile);
        File currentPath = new File(schemaFile).getCanonicalFile().getParentFile();
        Document schema = XMLUtilities.fileNameToDocument(schemaFile);
        List importEls = schema.getRootElement().getChildren("import",
            schema.getRootElement().getNamespace(IntroduceConstants.W3CNAMESPACE));
        for (int i = 0; i < importEls.size(); i++) {
            // get the import element
            Element importEl = (Element) importEls.get(i);

            // get the location of the imported schema
            String location = importEl.getAttributeValue("schemaLocation");
            if (location != null && !location.equals("")) {
                // imports must be at or below current directory, or contain
                // appropriate '../' to reach path on file system
                File importedSchema = new File(currentPath + File.separator + location);

                // has the schema been visited yet?
                if (!visitedSchemas.contains(importedSchema.getCanonicalPath())) {
                    String namespace = importEl.getAttributeValue("namespace");
                    if (!excludedNamespaces.contains(namespace)) {
                        if (namespaces.add(namespace)) {
                            logger.debug("adding namepace " + namespace);
                        }
                        if (!schemaFile.equals(importedSchema.getCanonicalPath())) {
                            internalWalkSchemasGetNamespaces(importedSchema.getCanonicalPath(), namespaces,
                                excludedNamespaces, visitedSchemas, cycleSchemas, selfImportSchemas);
                        } else {
                            if (!selfImportSchemas.contains(schemaFile)) {
                                logger.debug("WARNING: Schema is importing itself. " + schemaFile);
                                selfImportSchemas.add(schemaFile);
                            }
                        }
                    }
                } else {
                    if (!cycleSchemas.contains(schemaFile)) {
                        logger.debug("WARNING: Schema imports contain circular references. " + schemaFile);
                        cycleSchemas.add(schemaFile);
                    }
                }
            }
        }
    }
}
