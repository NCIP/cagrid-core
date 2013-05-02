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
package org.cagrid.cql.utilities;

import gov.nih.nci.cagrid.encoding.AxisContentHandler;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.exolab.castor.types.AnyNode;
import org.exolab.castor.xml.util.AnyNode2SAX2;
import org.exolab.castor.xml.util.SAX2ANY;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class AnyNodeHelper {
    
    public static AnyNode convertMessageElementToAnyNode(MessageElement elem) throws Exception {
        return convertStringToAnyNode(XmlUtils.toString(elem));
    }
    
    
    public static MessageElement convertAnyNodeToMessageElement(AnyNode node) throws Exception {
        String text = convertAnyNodeToString(node);
        Document tempDoc = XmlUtils.newDocument(new InputSource(new StringReader(text)));
        MessageElement elem = new MessageElement(tempDoc.getDocumentElement());
        QName name = new QName(node.getNamespaceURI(), node.getLocalName());
        elem.setQName(name);
        return elem;
    }
    

    public static AnyNode convertStringToAnyNode(String text) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        SAXParser saxParser = factory.newSAXParser();
        XMLReader reader = saxParser.getXMLReader();
        
        SAX2ANY handler = new SAX2ANY();
        
        reader.setContentHandler(handler);
        reader.setErrorHandler(handler);
        InputSource source = new InputSource(new StringReader(text));
        reader.parse(source);

        AnyNode anyNode = handler.getStartingNode();
        return anyNode;
    }


    /**
     * AnyNode.toString doesn't preserve really important things like xsi:type when it
     * serializes itself.  This method, however, does!
     * 
     * @param node
     * @return
     * @throws SAXException
     */
    public static String convertAnyNodeToString(AnyNode node) throws SAXException {
        AnyNode2SAX2 converter = new AnyNode2SAX2(node);
        StringWriter writer = new StringWriter();
        SerializationContext context = new SerializationContext(writer);
        AxisContentHandler handler = new AxisContentHandler(context);
        converter.setContentHandler(handler);
        converter.start();
        return writer.getBuffer().toString();
    }
}
