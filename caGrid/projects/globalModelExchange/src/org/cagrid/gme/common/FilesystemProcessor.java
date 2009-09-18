package org.cagrid.gme.common;

import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.cagrid.gme.common.exceptions.SchemaParsingException;
import org.jdom.Document;
import org.jdom.Element;


public class FilesystemProcessor {
    protected static final String XSD_NAMESPACE_ATTRIBUTE_NAME = "namespace";
    protected static final String XSD_TARGET_NAMESPACE_ATTRIBUTE_NAME = "targetNamespace";

    protected static final String XSD_SCHEMALOCATION_ATTRIBUTE_NAME = "schemaLocation";

    protected static final String XSD_REDEFINE_ELEMENT_NAME = "redefine";
    protected static final String XSD_INCLUDE_ELEMENT_NAME = "include";
    protected static final String XSD_IMPORT_ELEMENT_NAME = "import";


    protected List<Element> getImportElements(Document doc) {
        List<Element> imports = doc.getRootElement().getChildren(FilesystemProcessor.XSD_IMPORT_ELEMENT_NAME,
            doc.getRootElement().getNamespace());
        return imports;
    }


    protected List<Element> getIncludeElements(Document doc) {
        doc.getRootElement().getChildren();
        List<Element> includes = doc.getRootElement().getChildren(FilesystemProcessor.XSD_INCLUDE_ELEMENT_NAME,
            doc.getRootElement().getNamespace());
        return includes;
    }


    protected List<Element> getRedefineElements(Document doc) {
        List<Element> redefines = doc.getRootElement().getChildren(FilesystemProcessor.XSD_REDEFINE_ELEMENT_NAME,
            doc.getRootElement().getNamespace());
        return redefines;
    }


    protected URI getTargetNamespace(Document doc) throws URISyntaxException {
        String nsValue = doc.getRootElement().getAttributeValue(XSD_TARGET_NAMESPACE_ATTRIBUTE_NAME);
        if (nsValue == null) {
            throw new URISyntaxException("", "No targetNamespace attribute found!");
        }
        return new URI(nsValue);
    }


    protected Document createDocumentFromText(String text) throws SchemaParsingException {
        Document schemaDoc = null;

        try {
            schemaDoc = XMLUtilities.stringToDocument(text);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SchemaParsingException(e.getMessage());
        }
        return schemaDoc;
    }


    protected Document createDocumentFromFile(File file) throws SchemaParsingException {
        Document schemaDoc = null;

        try {
            schemaDoc = XMLUtilities.fileNameToDocument(file.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new SchemaParsingException(e.getMessage());
        }
        return schemaDoc;
    }


    protected void writeDocumentToFile(File file, Document schemaDoc) throws IOException {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(XMLUtilities.formatXML(XMLUtilities.documentToString(schemaDoc)));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }


    protected class XSDDocument {
        URI namespace;
        String systemID;


        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.namespace == null) ? 0 : this.namespace.hashCode());
            result = prime * result + ((this.systemID == null) ? 0 : this.systemID.hashCode());
            return result;
        }


        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            XSDDocument other = (XSDDocument) obj;
            if (this.namespace == null) {
                if (other.namespace != null) {
                    return false;
                }
            } else if (!this.namespace.equals(other.namespace)) {
                return false;
            }
            if (this.systemID == null) {
                if (other.systemID != null) {
                    return false;
                }
            } else if (!this.systemID.equals(other.systemID)) {
                return false;
            }
            return true;
        }

    }

}