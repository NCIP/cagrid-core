package gov.nih.nci.cagrid.data.common;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.common.XMLUtilities;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/** 
 *  CastorMappingUtil
 *  Utility for making edits to a castor mapping xml document
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 26, 2006 
 * @version $Id: CastorMappingUtil.java,v 1.8 2009-04-09 16:28:01 dervin Exp $ 
 */
public class CastorMappingUtil {
    /**
     * Setting this feature controls how xerxes handles external DTDs
     */
    public static final String XERXES_LOAD_DTD_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    
    public static final String CASTOR_MARSHALLING_MAPPING_FILE = "xml-mapping.xml";
    public static final String EDITED_CASTOR_MARSHALLING_MAPPING_FILE = "edited-" + CASTOR_MARSHALLING_MAPPING_FILE;
    public static final String CASTOR_UNMARSHALLING_MAPPING_FILE = "unmarshaller-xml-mapping.xml";
    public static final String EDITED_CASTOR_UNMARSHALLING_MAPPING_FILE = "edited-" + CASTOR_UNMARSHALLING_MAPPING_FILE;

	/**
	 * Edits a castor mapping XML file to change the namespace of all classes in a package
	 * 
	 * @param mapping
	 * 		The text of the castor mapping file
	 * @param packageName
	 * 		The name of the package to change the namespace mapping of
	 * @param namespace
	 * 		The namespace to remap the package's classes to
	 * @return
	 * 		The modified text of the castor mapping file
	 * @throws Exception
	 */
	public static String changeNamespaceOfPackage(String mapping, String packageName, String namespace) throws Exception {
        Element mappingRoot = stringToElementNoDoctypes(mapping);
		// get class elements
		String oldNamespace = null;
		String oldPrefix = null;
		Iterator classElemIter = mappingRoot.getChildren("class", mappingRoot.getNamespace()).iterator();
		while (classElemIter.hasNext()) {
			Element classElem = (Element) classElemIter.next();
			String className = classElem.getAttributeValue("name");
			int dotIndex = className.lastIndexOf('.');
			String classPackage = className.substring(0, dotIndex);
			if (classPackage.equals(packageName)) {
				Element mapToElem = classElem.getChild("map-to", classElem.getNamespace());
				if (oldNamespace == null) {
					// keep a record of the old namespace for the package
					oldNamespace = mapToElem.getAttributeValue("ns-uri");
					oldPrefix = mapToElem.getAttributeValue("ns-prefix");
				}
				// change the namespace in the map-to element
				mapToElem.setAttribute("ns-uri", namespace);
			}
		}
		if (oldNamespace != null) {
			// re-walk every class in the mapping, this time looking for attributes
			// of those classes which bind to the old namespace
			classElemIter = mappingRoot.getChildren("class", mappingRoot.getNamespace()).iterator();
			while (classElemIter.hasNext()) {
				Element classElem = (Element) classElemIter.next();
				Iterator fieldElemIter = classElem.getChildren("field", classElem.getNamespace()).iterator();
				while (fieldElemIter.hasNext()) {
					Element fieldElem = (Element) fieldElemIter.next();
					Element bindXmlElement = fieldElem.getChild("bind-xml", fieldElem.getNamespace());
					Namespace elemNamespace = bindXmlElement.getNamespace(oldPrefix);
					if (elemNamespace != null && elemNamespace.getURI().equals(oldNamespace)) {
						// TODO: This is probably horribly inefficient, see if it can be improved
						String elementString = XMLUtilities.elementToString(bindXmlElement);
						int nsStart = elementString.indexOf(oldNamespace);
						int nsEnd = nsStart + oldNamespace.length();
						String changedString = elementString.substring(0, nsStart) + namespace + elementString.substring(nsEnd);
						Element changedElement = stringToElementNoDoctypes(changedString);
						fieldElem.removeContent(bindXmlElement);
						fieldElem.addContent(changedElement);
					}
				}
			}
		}
		return XMLUtilities.elementToString(mappingRoot);
	}
    
    
    /**
     * Walks through a castor mapping text document and removes all
     * bindings to associations, so that serialization only converts
     * the top level object of an object tree to XML
     * 
     * @param mappingText
     *      The text of the original castor mapping
     * @return
     *      The edited text of the castor mapping
     */
    public static String removeAssociationMappings(String mappingText) throws Exception {
        Element mappingRoot = stringToElementNoDoctypes(mappingText);
        // <mapping>
        List classElements = mappingRoot.getChildren("class", mappingRoot.getNamespace());
        Iterator classElemIter = classElements.iterator();
        while (classElemIter.hasNext()) {
            Element classElement = (Element) classElemIter.next(); // <class>
            List fieldElements = classElement.getChildren("field", classElement.getNamespace());
            Iterator fieldElemIter = fieldElements.iterator();
            while (fieldElemIter.hasNext()) {
                Element fieldElement = (Element) fieldElemIter.next();
                Element bindElement = fieldElement.getChild("bind-xml", fieldElement.getNamespace());
                String nodeType = bindElement.getAttributeValue("node");
                // remove non-atttibutes
                if (!nodeType.equals("attribute")) {
                    fieldElemIter.remove();
                }
            }
        }
        String rawXml = XMLUtilities.elementToString(mappingRoot);
        return XMLUtilities.formatXML(rawXml);
    }
	
	
	public static String getCustomCastorMappingFileName(ServiceInformation serviceInfo) {
		return getMarshallingCastorMappingFileName(serviceInfo);
	}
	
	
	public static String getCustomCastorMappingName(ServiceInformation serviceInfo) {
		return getMarshallingCastorMappingName(serviceInfo);
	}
    
    
    public static String getMarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getMarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }
    
    
    public static String getMarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName() 
            + '-' + CASTOR_MARSHALLING_MAPPING_FILE;
        return mappingName;
    }
    
    
    public static String getUnmarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getUnmarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }
    
    
    public static String getUnmarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName() 
            + '-' + CASTOR_UNMARSHALLING_MAPPING_FILE;
        return mappingName;
    }
    
    
    /**
     * Gets the fully qualified file name of the edited marshalling castor mapping file
     * 
     * @param serviceInfo
     * @return
     *      The fully qualified file name
     */
    public static String getEditedMarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getEditedMarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }
    
    
    public static String getEditedMarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName()
            + '-' + EDITED_CASTOR_MARSHALLING_MAPPING_FILE;
        return mappingName;
    }
    
    
    /**
     * Gets the fully qualified file name of the edited unmarshalling castor mapping file
     * 
     * @param serviceInfo
     * @return
     *      The fully qualified file name
     */
    public static String getEditedUnmarshallingCastorMappingFileName(ServiceInformation serviceInfo) {
        String mappingOut = serviceInfo.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + getEditedUnmarshallingCastorMappingName(serviceInfo);
        return mappingOut;
    }
    
    
    public static String getEditedUnmarshallingCastorMappingName(ServiceInformation serviceInfo) {
        String mappingName = serviceInfo.getServices().getService(0)
            .getPackageName().replace('.', '/')
            + '/' + serviceInfo.getServices().getService(0).getName()
            + '-' + EDITED_CASTOR_UNMARSHALLING_MAPPING_FILE;
        return mappingName;
    }
    
    
    private static Element stringToElementNoDoctypes(String string) throws Exception {
        Document doc = null;
        try {
            SAXBuilder builder = new SAXBuilder(false);
            builder.setFeature(
                XERXES_LOAD_DTD_FEATURE, false);
            doc = builder.build(new ByteArrayInputStream(string.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Document construction failed:" + e.getMessage(), e);
        }
        Element root = doc.detachRootElement();
        return root;
    }
    
    
    public static void main(String[] args) {
        try {
            String castorMapping = Utils.fileToStringBuffer(new File("RemoteSDK321-unmarshaller-xml-mapping.xml")).toString();
            String edited = removeAssociationMappings(castorMapping);
            System.out.println(edited);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
