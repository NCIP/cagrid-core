package gov.nih.nci.cagrid.sdkquery4.processor;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainType;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.DomainTypesInformation;
import gov.nih.nci.cagrid.sdkquery4.beans.domaininfo.TypeAttribute;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

/** 
 *  DomainTypesInformationUtil
 *  Utility for manipulating a domain types information instance
 * 
 * @author David Ervin
 * 
 * @created Jan 16, 2008 11:30:36 AM
 * @version $Id: DomainTypesInformationUtil.java,v 1.1 2008-01-18 15:13:29 dervin Exp $ 
 */
public class DomainTypesInformationUtil {
    
    public static final QName DOMAIN_TYPES_INFORMATION_QNAME = 
        new QName("http://SDKQuery4.caBIG/1/gov.nih.nci.cagrid.sdkquery4.beans.domaininfo", "DomainTypesInformation");
    
    private DomainTypesInformation info = null;
    private Map<String, String> attributeJavaTypes = null;
    
    public DomainTypesInformationUtil(DomainTypesInformation info) {
        this.info = info;
        attributeJavaTypes = new HashMap<String, String>();
    }
    
    
    /**
     * Gets the java type of an attribute
     * 
     * @param classname
     *      The fully qualified name of the class to which the attribute belongs
     * @param attributeName
     *      The name of the attribute
     * @return
     *      The java type name, or <code>null</code> if no class / attribute were found
     */
    public String getAttributeJavaType(String classname, String attributeName) {
        String qualifiedName = classname + "." + attributeName;
        if (!attributeJavaTypes.containsKey(qualifiedName)) {
            String javaType = null;
            for (DomainType domainType : info.getDomainType()) {
                if (domainType.getJavaClassName().equals(classname)) {
                    TypeAttribute[] attribs = domainType.getTypeAttribute();
                    if (attribs != null) {
                        for (TypeAttribute attrib : attribs) {
                            if (attrib.getAttributeName().equals(attributeName)) {
                                javaType = attrib.getJavaTypeName();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
            // MAY put null in the map, indicating no attribute was found
            attributeJavaTypes.put(qualifiedName, javaType);
        }
        return attributeJavaTypes.get(qualifiedName);
    }
    
    
    public List<String> getSubclasses(String classname) {
        List<String> subclasses = new ArrayList<String>();
        internalGetSubclasses(classname, subclasses);
        return subclasses;
    }
    
    
    private void internalGetSubclasses(String classname, List<String> subclasses) {
        for (DomainType type : info.getDomainType()) {
            if (type.getJavaClassName().equals(classname)) {
                String[] subs = type.getSubclassName();
                if (subs != null) {
                    Collections.addAll(subclasses, subs);
                    for (String sub : subs) {
                        internalGetSubclasses(sub, subclasses);
                    }
                }
            }
        }
    }
    
    
    /**
     * Deserializes a domain types information document from XML to the Java object model
     * 
     * @param reader
     *      A reader to the XML content of the document
     * @return
     *      The object representation of the information document
     * @throws Exception
     */
    public static DomainTypesInformation deserializeDomainTypesInformation(Reader reader) throws Exception {
        return (DomainTypesInformation) Utils.deserializeObject(reader, DomainTypesInformation.class);
    }
    
    
    /**
     * Serializes a domain types information object model instance to XML
     * 
     * @param info
     *      The domain types information instance
     * @param writer
     *      A writer to which the XML of the domain types document will be sent
     * @throws Exception
     */
    public static void serializeDomainTypesInformation(DomainTypesInformation info, Writer writer) throws Exception {
        Utils.serializeObject(info, DOMAIN_TYPES_INFORMATION_QNAME, writer);
    }
}
