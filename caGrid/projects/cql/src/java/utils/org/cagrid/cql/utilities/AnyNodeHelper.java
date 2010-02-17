package org.cagrid.cql.utilities;

import java.io.StringReader;

import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.axis.message.MessageElement;
import org.exolab.castor.types.AnyNode;
import org.exolab.castor.xml.util.SAX2ANY;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
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

    
    public static String convertAnyNodeToString(AnyNode node) {
        String raw = node.toString();
        // will have XML declaration that we need to throw away
        int start = raw.indexOf("<?");
        int end = raw.indexOf("?>", start);
        return raw.substring(end + 2);
    }
}
