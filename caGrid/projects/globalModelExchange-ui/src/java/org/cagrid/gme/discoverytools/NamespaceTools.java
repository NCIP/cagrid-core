package org.cagrid.gme.discoverytools;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;


public class NamespaceTools {

    private static final Log logger = LogFactory.getLog(NamespaceTools.class);


    // This processes includes and redefines, but assumes that imports are being
    // handled elsewhere (which is how the GME discovery works)
    public static NamespaceType createNamespaceTypeForFile(String xsdFilename, File serviceSchemaDir) throws Exception {
        NamespaceType namespaceType = new NamespaceType();
        File xsdFile = new File(xsdFilename);
        String location;
        try {
            location = "./" + Utils.getRelativePath(serviceSchemaDir, xsdFile).replace('\\', '/');
        } catch (IOException e) {
            logger.error(e);
            throw new Exception("Problem getting relative path of XSD.", e);
        }
        namespaceType.setLocation(location);
        Document schemaDoc = XMLUtilities.fileNameToDocument(xsdFilename);

        String rawNamespace = schemaDoc.getRootElement().getAttributeValue("targetNamespace");
        // TODO this should be looked up in the caDSR, or provided to this
        // utility as a parameter
        String packageName = CommonTools.getPackageName(rawNamespace);
        namespaceType.setPackageName(packageName);
        namespaceType.setNamespace(rawNamespace);

        // get the types from the root document itself
        List<SchemaElementType> schemaTypesList = new ArrayList<SchemaElementType>();
        createSchemaElementsForDocument(schemaDoc, schemaTypesList);

        // extract any types from included documents
        List<Element> includeElements = schemaDoc.getRootElement().getChildren("include",
            Namespace.getNamespace(IntroduceConstants.W3CNAMESPACE));
        for (Element include : includeElements) {
            String includeLocation = include.getAttributeValue("schemaLocation");
            if (includeLocation == null || includeLocation.length() <= 0) {
                throw new Exception(
                    "Schema does not appear to be valid: an include does not contain a schemaLocation attribute.");
            }
            // load the included document and load those type too
            Document includedDoc = XMLUtilities.fileNameToDocument(xsdFilename);
            createSchemaElementsForDocument(includedDoc, schemaTypesList);
        }

        // merge all the types
        SchemaElementType[] schemaTypes = new SchemaElementType[schemaTypesList.size()];
        schemaTypes = schemaTypesList.toArray(schemaTypes);

        namespaceType.setSchemaElement(schemaTypes);
        return namespaceType;
    }


    private static void createSchemaElementsForDocument(Document schemaDoc, List<SchemaElementType> schemaTypesList)
        throws Exception {
        List<Element> elementTypes = schemaDoc.getRootElement().getChildren("element",
            Namespace.getNamespace(IntroduceConstants.W3CNAMESPACE));

        for (int i = 0; i < elementTypes.size(); i++) {
            Element element = elementTypes.get(i);
            SchemaElementType type = new SchemaElementType();
            String elementName = element.getAttributeValue("name");
            if (elementName == null || elementName.length() <= 0) {
                throw new Exception("Schema does not appear to be valid: an element does not contain a name attribute");
            }
            type.setType(elementName);
            schemaTypesList.add(type);
        }
    }
}
