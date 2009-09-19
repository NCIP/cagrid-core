package gov.nci.nih.cagrid.tests.core.util;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class SchemaInfo {
    private Hashtable<String, String> importTable = new Hashtable<String, String>();


    public SchemaInfo(File schema) throws ParserConfigurationException, SAXException, IOException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        SchemaHandler handler = new SchemaHandler();
        parser.parse(schema, handler);
    }


    public Hashtable<String, String> getImports() {
        return importTable;
    }


    private class SchemaHandler extends DefaultHandler {
        public void startElement(String uri, String lname, String qname, Attributes atts) {
            if (qname.equals("xs:import")) {
                importTable.put(atts.getValue("namespace"), atts.getValue("schemaLocation"));
            }
        }
    }
}
