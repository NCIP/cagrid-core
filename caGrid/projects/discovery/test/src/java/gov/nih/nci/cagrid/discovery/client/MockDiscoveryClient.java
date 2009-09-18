package gov.nih.nci.cagrid.discovery.client;

import gov.nih.nci.cagrid.metadata.exceptions.QueryInvalidException;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;

import javax.xml.transform.TransformerException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XObject;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Acts as the discovery client, and replaces the actual call to the Index
 * Service with an execution of the XPath against the specified local file.
 * 
 * @author oster
 */
public class MockDiscoveryClient extends DiscoveryClient {
    private Node xml;


    /**
     * @throws MalformedURIException
     */
    public MockDiscoveryClient(Node xml) throws MalformedURIException {
        super();
        this.xml = xml;
    }


    @Override
    protected EndpointReferenceType[] discoverByFilter(String xpathPredicate) throws ResourcePropertyRetrievalException {
        XObject result;
        try {
            result = XPathAPI.eval(getRootNode(), translateXPath(xpathPredicate));
        } catch (TransformerException e) {
            throw new QueryInvalidException("Problem with query: " + xpathPredicate, e);
        }
        EndpointReferenceType[] resultsList = null;

        if (result instanceof XNodeSet) {
            XNodeSet set = (XNodeSet) result;
            NodeList list;
            try {
                list = set.nodelist();
            } catch (TransformerException e) {
                throw new QueryInvalidException("Problem with query: " + xpathPredicate, e);

            }

            resultsList = new EndpointReferenceType[list.getLength()];
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node instanceof Document) {
                    Object obj;
                    try {
                        obj = ObjectDeserializer.toObject(((Document) node).getDocumentElement(),
                            EndpointReferenceType.class);
                    } catch (DeserializationException e) {
                        throw new ResourcePropertyRetrievalException(
                            "Problem deserializing results: " + e.getMessage(), e);
                    }
                    resultsList[i] = (EndpointReferenceType) obj;
                } else if (node instanceof Element) {
                    Object obj;
                    try {
                        obj = ObjectDeserializer.toObject((Element) node, EndpointReferenceType.class);
                    } catch (DeserializationException e) {
                        throw new ResourcePropertyRetrievalException(
                            "Problem deserializing results: " + e.getMessage(), e);
                    }
                    resultsList[i] = (EndpointReferenceType) obj;
                } else {
                    throw new QueryInvalidException("Unexpected query result!");
                }
            }
        } else {
            throw new QueryInvalidException("Unexpected query result!");
        }

        return resultsList;
    }


    public void setRootNode(Node node) {
        this.xml = node;
    }


    private Node getRootNode() {
        return xml;
    }

}
