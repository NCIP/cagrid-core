/**
*============================================================================
*  Copyright The Ohio State University Research Foundation, The University of Chicago - 
*	Argonne National Laboratory, Emory University, SemanticBits LLC, and 
*	Ekagra Software Technologies Ltd.
*
*  Distributed under the OSI-approved BSD 3-Clause License.
*  See http://ncip.github.com/cagrid-core/LICENSE.txt for details.
*============================================================================
**/
package org.cagrid.gaards.websso.utils;

import gov.nih.nci.cagrid.common.XMLUtilities;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cagrid.gaards.websso.exception.AuthenticationConfigurationException;
import org.jdom.Document;
import org.jdom.input.DOMBuilder;
import org.springframework.core.io.Resource;

public class FileHelper {
    public static final String DOCUMENT_BUILDER_FACTORY_IMPL = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
    
	public FileHelper() {
	}
	public Document validateXMLwithSchema(Resource propertiesFileResource,
			Resource schemaFileResource) throws AuthenticationConfigurationException {
		org.w3c.dom.Document document = null;
		DocumentBuilderFactory documentBuilderFactory = XMLUtilities.getDocumentBuilderFactory();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(true);
		documentBuilderFactory.setAttribute(
				"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
				"http://www.w3.org/2001/XMLSchema");
		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			documentBuilderFactory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaSource",
					schemaFileResource.getInputStream());
			documentBuilderFactory.newDocumentBuilder();
			document = (org.w3c.dom.Document) documentBuilder.parse(propertiesFileResource.getInputStream());
		} catch (Exception e) {
			throw new AuthenticationConfigurationException(
					"Error in reading the " + propertiesFileResource + " file: " + e.getMessage(), e);
		}
		DOMBuilder builder = new DOMBuilder();
		org.jdom.Document jdomDocument = builder.build(document);

		return jdomDocument;
	}
}
