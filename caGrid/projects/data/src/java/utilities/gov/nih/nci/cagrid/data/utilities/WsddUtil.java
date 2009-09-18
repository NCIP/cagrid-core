package gov.nih.nci.cagrid.data.utilities;

import java.io.FileWriter;
import java.util.Iterator;

import org.jdom.Element;
import gov.nih.nci.cagrid.common.XMLUtilities;

/** 
 *  WsddUtil
 *  Utility for making small changes to a wsdd
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Oct 25, 2006 
 * @version $Id$ 
 */
public class WsddUtil {

	/**
	 * Sets a global parameter on the client side wsdd file
	 * 
	 * @param clientWsddFile
	 * 		The filename of the wsdd file to edit
	 * @param key
	 * 		The key of the parameter
	 * @param value
	 * 		The value to assign to the parameter
	 * @throws Exception
	 */
	public static void setGlobalClientParameter(String clientWsddFile, String key, String value) throws Exception {
		Element wsddRoot = XMLUtilities.fileNameToDocument(clientWsddFile).getRootElement();
		Element configElement = wsddRoot.getChild("globalConfiguration", wsddRoot.getNamespace());
		
		setParameter(configElement, key, value);
		String editedWsdd = XMLUtilities.formatXML(XMLUtilities.elementToString(wsddRoot));
		FileWriter writer = new FileWriter(clientWsddFile);
		writer.write(editedWsdd);
		writer.flush();
		writer.close();
	}
	
	
	/**
	 * Sets a parameter on the service configuration stored in a server side wsdd file
	 * 
	 * @param serverWsddFile
	 * 		The filename of the server side wsdd to edit
	 * @param serviceName
	 * 		The name of the service to set the parameter for
	 * @param key
	 * 		The key of the parameter
	 * @param value
	 * 		The value of the parameter
	 * @throws Exception
	 */
	public static void setServiceParameter(String serverWsddFile, String serviceName, String key, String value) throws Exception {
		Element wsddRoot = XMLUtilities.fileNameToDocument(serverWsddFile).getRootElement();
		Iterator serviceElemIter = wsddRoot.getChildren("service", wsddRoot.getNamespace()).iterator();
        boolean found = false;
		while (serviceElemIter.hasNext() && !found) {
			Element serviceElement = (Element) serviceElemIter.next();
			String name = serviceElement.getAttributeValue("name");
			if (name.endsWith("/" + serviceName)) {
				setParameter(serviceElement, key, value);
				found = true;
			}
		}
        if (!found) {
            throw new Exception("Service " + serviceName + " was not found for adding wsdd parameter!");
        }
		String editedWsdd = XMLUtilities.elementToString(wsddRoot);
		FileWriter writer = new FileWriter(serverWsddFile);
		writer.write(editedWsdd);
		writer.flush();
		writer.close();
	}
	
	
	private static void setParameter(Element parentElem, String key, String value) {
		Iterator parameterElemIter = parentElem.getChildren("parameter", parentElem.getNamespace()).iterator();
		boolean parameterFound = false;
		while (parameterElemIter.hasNext()) {
			Element paramElement = (Element) parameterElemIter.next();
			if (paramElement.getAttributeValue("name").equals(key)) {
				paramElement.setAttribute("value", value);
				parameterFound = true;
				System.out.println("Parameter found and changed");
				break;
			}
		}
		if (!parameterFound) {
			System.out.println("Parameter was new!");
			Element paramElement = new Element("parameter", parentElem.getNamespace());
			paramElement.setAttribute("name", key);
			paramElement.setAttribute("value", value);
			parentElem.addContent(paramElement);
		}
	}
	
	
	public static void main(String[] args) {
		try {
			setGlobalClientParameter("client-config.wsdd", "OBVIOUS", "CHANGE");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
