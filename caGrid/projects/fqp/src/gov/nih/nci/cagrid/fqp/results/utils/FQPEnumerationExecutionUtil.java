package gov.nih.nci.cagrid.fqp.results.utils;

import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.fqp.processor.exceptions.FederatedQueryProcessingException;
import gov.nih.nci.cagrid.wsenum.utils.EnumConfigDiscoveryUtil;
import gov.nih.nci.cagrid.wsenum.utils.EnumIteratorFactory;
import gov.nih.nci.cagrid.wsenum.utils.EnumerateResponseFactory;
import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPElement;

import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * FQPEnumerationExecutionUtil
 * Sets up a WS-Enumeration retrieval of CQL query results.
 * 
 * @author ervin
 */
public class FQPEnumerationExecutionUtil {
    
    private static Log LOG = LogFactory.getLog(FQPEnumerationExecutionUtil.class);
    
    private FQPEnumerationExecutionUtil() {
        // prevent instantiation
    }
    

    public static EnumerationResponseContainer setUpEnumeration(
        CQLQueryResults cqlResults) throws FederatedQueryProcessingException {
        
        // placeholder for result object QName
        QName resultTypeQname = null;
        
        // Aggregate results as a List of XML documents.
        // Using XML documents removes requirement that server be able
        // to deserialize every type of object it might potentially handle.
        LOG.debug("Creating XML documents from results");
        List<SOAPElement> aggregatedDocuments = new LinkedList<SOAPElement>();
        CQLQueryResultsIterator iterator = new CQLQueryResultsIterator(cqlResults, true);
        try {
            while (iterator.hasNext()) {
                String resultXml = (String) iterator.next();
                Document doc = XmlUtils.newDocument(new InputSource(new StringReader(resultXml)));
                MessageElement xmlAsElement = new MessageElement(doc.getDocumentElement());
                QName cqlElementQname = xmlAsElement.getQName();
                if (resultTypeQname == null) {
                    resultTypeQname = cqlElementQname;
                } else if (!resultTypeQname.equals(cqlElementQname)) {
                    throw new FederatedQueryProcessingException(
                        "Encountered a CQL query result of type " + cqlElementQname.toString() +
                        " but expected " + resultTypeQname.toString());
                }
                aggregatedDocuments.add(xmlAsElement);
            }
        } catch (IOException ex) {
            throw new FederatedQueryProcessingException(
                "Error reading XML query results: " + ex.getMessage(), ex);
        } catch (SAXException ex) {
            throw new FederatedQueryProcessingException(
                "Error parsing XML query results: " + ex.getMessage(), ex);
        } catch (ParserConfigurationException ex) {
            throw new FederatedQueryProcessingException(
                "Error parsing XML query results: " + ex.getMessage(), ex);
        }
        
        // create an EnumIterator instance
        LOG.debug("Creating EnumIterator instance");
        EnumIterator enumIter = null;
        try {
            IterImplType iterImplementation = EnumConfigDiscoveryUtil.getConfiguredIterImplType();
            enumIter = EnumIteratorFactory.createIterator(
                iterImplementation, aggregatedDocuments, resultTypeQname, null);
        } catch (Exception ex) {
            throw new FederatedQueryProcessingException("Error creating EnumIterator instance: " + ex.getMessage(), ex);
        }
        
        // create the response... this will properly initialize the Enumeration resource
        LOG.debug("Preparing enumeration resource and response");
        EnumerationResponseContainer responseContainer = null;
        try {
            responseContainer = EnumerateResponseFactory.createEnumerationResponse(enumIter);
        } catch (Exception ex) {
            throw new FederatedQueryProcessingException("Error creating Enumeration resource: " + ex.getMessage(), ex);
        }
        
        return responseContainer;
    }
}
