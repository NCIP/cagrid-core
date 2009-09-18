package org.cagrid.cacore.sdk4x.cql2.processor;

import gov.nih.nci.cagrid.common.XMLUtilities;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;

/**
 * HBMTool
 * Tool which can read Hibernate Mapping XML documents and 
 * determine what the class identifier type and value should be.
 * 
 * @author David
 */
public class HBMTool {
    
    private Map<String, Object> subclassIdentifiers = null;
    private Map<String, String> instanceIdentifiers = null;
    
    private Filter discriminatorFilter = null;
    private Filter joinedSubclassFilter = null;

    public HBMTool() {
        subclassIdentifiers = new HashMap<String, Object>();
        instanceIdentifiers = new HashMap<String, String>();
        // filter for discriminator elements
        this.discriminatorFilter = new ElementFilter("discriminator");
        // filter for joined-subclass elements
        this.joinedSubclassFilter = new ElementFilter("joined-subclass");
    }
    
    
    /**
     * Hibernate allows for configurable identifier fields for each class.  Typically,
     * this is just "id", but custom mappings could be different.  This method reads
     * the HMB file for the class in question and figures out the identifier field.
     * 
     * @param className
     *      The name of the base class for which an identifier field name is to be determined.
     *      This CANNOT be a subclass within your domain model and MUST be the base class.
     * @return
     */
    public String getInstanceIdentifierField(String className) throws Exception {
        // TODO: deal with subclasses being passed in here... and use the base class name?
        String identifier = instanceIdentifiers.get(className);
        if (identifier == null) {
            Element hbmElem = getHbmElement(className);
            Element classElem = hbmElem.getChild("class", hbmElem.getNamespace());
            Element idElem = classElem.getChild("id", classElem.getNamespace());
            identifier = idElem.getAttributeValue("name");
            instanceIdentifiers.put(className, identifier);
        }
        return identifier;
    }
    
    
    /**
     * Hibernate identifies subclasses stored in separate DB tables from their parent class by an Integer
     * value, indexed by the order of appearance in the HBM document.  It stores such identifiers
     * for subclasses stores in the SAME database table by a String value which is the short classname.
     * 
     * Yay abstraction!
     * 
     * @param parentClassName
     * @param subclassName
     * @return
     * @throws Exception
     */
    public Object getClassFieldIdentifierValue(String parentClassName, String subclassName) throws Exception {
        Object identifier = subclassIdentifiers.get(subclassName);
        if (identifier == null) {
            Element hbmElem = getHbmElement(parentClassName);
            
            // if there's a discriminator element, we're using a String
            String shortClassname = getShortClassName(subclassName);
            Iterator<?> discriminatorElements = hbmElem.getDescendants(discriminatorFilter);
            if (discriminatorElements.hasNext()) {
                identifier = shortClassname;
            } else {
                // using an integer, but we need to know which one
                Element classElem = hbmElem.getChild("class", hbmElem.getNamespace());
                // TODO: is the search for subclass index ID breadth or depth first?  
                // I'm assuming depth, since I have nothing else to go on
                // JDom's getDescendents() operation works depth first
                Iterator<?> subclassIter = classElem.getDescendants(joinedSubclassFilter);
                int index = 0;
                while (subclassIter.hasNext()) {
                    index++; // first subclass is index 1, so this is fine
                    Element subclassElem = (Element) subclassIter.next();
                    String name = subclassElem.getAttributeValue("name");
                    if (shortClassname.equals(name)) {
                        identifier = Integer.valueOf(index);
                        break;
                    }
                }
                
                // handle the case of checking the base class
                if (identifier == null && parentClassName.equals(subclassName)) {
                    identifier = Integer.valueOf(0);
                }
            }
            
            subclassIdentifiers.put(subclassName, identifier);
        }
        return identifier;
    }
    
    
    private Element getHbmElement(String className) throws Exception {
        // load the HBM XML document from the classpath
        String hbmResourceName = getHbmResourceName(className);
        InputStream hbmStream = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(hbmResourceName);
        Element hbmElem = XMLUtilities.streamToDocument(hbmStream).getRootElement();
        hbmStream.close();
        return hbmElem;
    }
    
    
    private String getHbmResourceName(String className) {
        String slashified = className.replace('.', '/');
        return slashified + ".hbm.xml";
    }
    
    
    private String getShortClassName(String className) {
        int dotIndex = className.lastIndexOf('.');
        return className.substring(dotIndex + 1);
    }
}
