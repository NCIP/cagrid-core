package gov.nih.nci.cagrid.metadata.xmi;

import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
  *  XMIParser
  *  Parses XMI into a Domain Model
  * 
  * @author Patrick McConnell
  * @author David Ervin
  * 
  * @created Oct 22, 2007 10:19:59 AM
  * @version $Id: XMIParser.java,v 1.5 2008-04-22 19:41:23 dervin Exp $
 */
public class XMIParser {
    DomainModel model;
    boolean filterPrimitiveClasses = true;
    String projectDescription;
    String projectLongName;
    String projectShortName;
    String projectVersion;
    float attributeVersion = 1.0f;
    boolean debug = false;

    // maps XMI data types to Java type class names
    static final Hashtable<String, String> DATATYPE_MAP = new Hashtable<String, String>();
    {
        DATATYPE_MAP.put("Date", "java.util.Date");
        DATATYPE_MAP.put("Short", "java.lang.Short");
        DATATYPE_MAP.put("Integer", "java.lang.Integer");
        DATATYPE_MAP.put("Long", "java.lang.Long");
        DATATYPE_MAP.put("Float", "java.lang.Float");
        DATATYPE_MAP.put("Double", "java.lang.Double");
        DATATYPE_MAP.put("Boolean", "java.lang.Boolean");
        DATATYPE_MAP.put("Byte", "java.lang.Byte");
        DATATYPE_MAP.put("String", "java.lang.String");
        DATATYPE_MAP.put("Character", "java.lang.Character");
    }


    public XMIParser(String projectShortName, String projectVersion) {
        super();
        this.projectShortName = projectShortName;
        this.projectVersion = projectVersion;
    }
    
    
    public DomainModel parse(InputStream xmiStream) throws SAXException, IOException, ParserConfigurationException {
        return parse(xmiStream, XmiFileType.SDK_32_EA);
    }
    
    
    public DomainModel parse(File file) throws SAXException, IOException, ParserConfigurationException {
        return parse(file, XmiFileType.SDK_32_EA);
    }
    
    
    public DomainModel parse(InputStream xmiStream, XmiFileType type) throws SAXException, IOException, ParserConfigurationException {
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        DefaultHandler handler = null;
        switch (type) {
            case SDK_32_EA:
                handler = new XMIHandler(this);
                break;
            case SDK_40_EA:
                handler = new Sdk4EaXMIHandler(this);
                break;
            case SDK_40_ARGO:
                handler = new Sdk4ArgoUMLXMIHandler(this);
        }
        parser.parse(xmiStream, handler);
        return model;
    }


    public DomainModel parse(File file, XmiFileType type) throws SAXException, IOException, ParserConfigurationException {
        FileInputStream fis = new FileInputStream(file);
        parse(fis, type);
        fis.close();
        return model;
    }


    public boolean isFilterPrimitiveClasses() {
        return filterPrimitiveClasses;
    }


    public void setFilterPrimitiveClasses(boolean filterPrimitiveClasses) {
        this.filterPrimitiveClasses = filterPrimitiveClasses;
    }


    public String getProjectDescription() {
        return projectDescription;
    }


    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }


    public String getProjectLongName() {
        return projectLongName;
    }


    public void setProjectLongName(String projectLongName) {
        this.projectLongName = projectLongName;
    }


    public String getProjectShortName() {
        return projectShortName;
    }


    public void setProjectShortName(String projectShortName) {
        this.projectShortName = projectShortName;
    }


    public String getProjectVersion() {
        return projectVersion;
    }


    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }


    public float getAttributeVersion() {
        return attributeVersion;
    }


    public void setAttributeVersion(float attributeVersion) {
        this.attributeVersion = attributeVersion;
    }


    public boolean isDebug() {
        return debug;
    }


    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
