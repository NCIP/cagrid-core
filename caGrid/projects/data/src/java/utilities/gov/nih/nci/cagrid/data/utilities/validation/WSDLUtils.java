package gov.nih.nci.cagrid.data.utilities.validation;

import gov.nih.nci.cagrid.common.Utils;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Types;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.UnknownExtensibilityElement;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Element;


/**
 * @author oster
 */
public class WSDLUtils {

	protected static Log LOG = LogFactory.getLog(WSDLUtils.class.getName());


	public static Definition parseServiceWSDL(String wsdlLocation) throws WSDLException {
		WSDLFactory factory = WSDLFactory.newInstance();
		WSDLReader wsdlReader = factory.newWSDLReader();
		wsdlReader.setFeature("javax.wsdl.verbose", LOG.isDebugEnabled());
		wsdlReader.setFeature("javax.wsdl.importDocuments", true);

		Definition mainDefinition = wsdlReader.readWSDL(wsdlLocation);

		return mainDefinition;
	}


	public static String getWSDLLocation(EndpointReferenceType epr) {
		return epr.getAddress().toString() + "?wsdl";
	}


	public static void walkWSDLFindingSchema(Definition mainDefinition, Map<String, org.jdom.Element> schemas) {
		LOG.debug("Looking at WSDL at:" + mainDefinition.getDocumentBaseURI());
		org.jdom.Element schema = extractTypesSchema(mainDefinition);
		if (schema != null) {
			LOG.debug("Found types schema.");
			schemas.put(mainDefinition.getDocumentBaseURI(), schema);
		} else {
			LOG.debug("No types schema found.");
		}

		LOG.debug("Looking for imports...");
		Map imports = mainDefinition.getImports();
		if (imports != null) {
			Iterator iter = imports.values().iterator();
			while (iter.hasNext()) {
				LOG.debug("Found imports...");
				List wsdlImports = (List) iter.next();
				for (int i = 0; i < wsdlImports.size(); i++) {
					Import wsdlImport = (Import) wsdlImports.get(i);
					Definition importDefinition = wsdlImport.getDefinition();
					if (importDefinition != null) {
						LOG.debug("Looking at imported WSDL at:" + importDefinition.getDocumentBaseURI());
						walkWSDLFindingSchema(importDefinition, schemas);
					}
				}

			}
		}

	}


	public static org.jdom.Element extractTypesSchema(Definition wsdlDefinition) {
		org.jdom.Element typesSchemaElm = null;
		if (wsdlDefinition != null) {
			Types types = wsdlDefinition.getTypes();
			if (types != null) {
			    List extensibilityElements = types.getExtensibilityElements();
				for (int i = 0; i < extensibilityElements.size(); i++) {
					ExtensibilityElement schemaExtElem = (ExtensibilityElement) extensibilityElements.get(i);
					if (schemaExtElem != null) {
						QName elementType = schemaExtElem.getElementType();
						if (elementType.getLocalPart().equals("schema")
							&& (schemaExtElem instanceof UnknownExtensibilityElement)) {
							Element element = ((UnknownExtensibilityElement) schemaExtElem).getElement();
							DOMBuilder domBuilder = new DOMBuilder();
							typesSchemaElm = domBuilder.build(element);
						}
					}
				}
			}
		}
		return typesSchemaElm;
	}


	public static URI determineSchemaLocation(Map<String, org.jdom.Element> schemas, String namespace) {
		LOG.debug("Trying to find XSD location of namespace:" + namespace);
		if (schemas != null) {
			Iterator<String> iterator = schemas.keySet().iterator();

			while (iterator.hasNext()) {
				String mainURI = iterator.next();
				org.jdom.Element schema = schemas.get(mainURI);
				Iterator<?> childIter = schema.getChildren("import", schema.getNamespace()).iterator();
				while (childIter.hasNext()) {
					org.jdom.Element importElm = (org.jdom.Element) childIter.next();
					String ns = importElm.getAttributeValue("namespace");
					if (ns.equals(namespace)) {
						String location = importElm.getAttributeValue("schemaLocation");
						LOG.debug("Found relative XSD location of namespace (" + namespace + ")=" + location);
						URI schemaURI = URI.create(Utils.encodeUrl(mainURI));
						URI importURI = schemaURI.resolve(location);
						LOG.debug("Converted complete location of namespace (" + namespace + ") to: "
							+ importURI.toString());
						return importURI;
					}
				}
			}
		}
		return null;
	}
}
