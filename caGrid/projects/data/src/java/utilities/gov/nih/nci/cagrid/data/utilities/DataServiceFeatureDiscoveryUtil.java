package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.EnumerationMethodConstants;
import gov.nih.nci.cagrid.data.MetadataConstants;
import gov.nih.nci.cagrid.data.TransferMethodConstants;
import gov.nih.nci.cagrid.data.utilities.validation.WSDLUtils;
import gov.nih.nci.cagrid.metadata.ResourcePropertyHelper;

import java.io.StringReader;
import java.util.Iterator;

import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.dataservice.cql.support.Cql2SupportType;
import org.cagrid.dataservice.cql.support.QueryLanguageSupport;
import org.globus.wsrf.utils.XmlUtils;
import org.w3c.dom.Element;

/**
 * DataServiceFeatureDiscoveryUtil
 * Discover what features a data service supports
 * FIXME: needs to drill through port types and operations to find
 * the query operations, not just rely on the port type being there
 * 
 * @author David
 *
 */
public class DataServiceFeatureDiscoveryUtil {
    
    private static Log LOG = LogFactory.getLog(DataServiceFeatureDiscoveryUtil.class);

    private DataServiceFeatureDiscoveryUtil() {
        
    }
    
    
    public static boolean serviceSupportsCql2(EndpointReferenceType epr) throws Exception {
        Element resourceProperty = null;
        resourceProperty = ResourcePropertyHelper.getResourceProperty(
            epr, MetadataConstants.QUERY_LANGUAGE_SUPPORT_QNAME);
        if (resourceProperty != null) {
            // deserialize the resource property
            QueryLanguageSupport support = Utils.deserializeObject(
                new StringReader(XmlUtils.toString(resourceProperty)), QueryLanguageSupport.class);
            return !(support.getCQL2Support() != null && 
                support.getCQL2Support().equals(Cql2SupportType.ImplementationNotProvided));
        }
        return false;
    }
    
    
    public static PortType getPortType(EndpointReferenceType epr, QName name) throws WSDLException {
        String wsdlLocation = WSDLUtils.getWSDLLocation(epr);
        LOG.debug("Loading WSDL from " + wsdlLocation);
        Definition wsdlDef = WSDLUtils.parseServiceWSDL(wsdlLocation);
        LOG.debug("Checking for port type " + name.toString());
        PortType portType = wsdlDef.getPortType(name);
        return portType;
    }
    
    
    public static Operation getOperation(EndpointReferenceType epr, QName inputMessage, QName outputMessage, String operationName) throws WSDLException {
        String wsdlLocation = WSDLUtils.getWSDLLocation(epr);
        LOG.debug("Loading WSDL from " + wsdlLocation);
        Definition wsdlDef = WSDLUtils.parseServiceWSDL(wsdlLocation);
        Operation foundOperation = null;
        Iterator<?> serviceIter = wsdlDef.getServices().values().iterator();
        while (serviceIter.hasNext() && foundOperation == null) {
            Service service = (Service) serviceIter.next();
            Iterator<?> portIter = service.getPorts().values().iterator();
            while (portIter.hasNext() && foundOperation == null) {
                Port port = (Port) portIter.next();
                PortType portType = port.getBinding().getPortType();
                Iterator<?> opIter = portType.getOperations().iterator();
                while (opIter.hasNext() && foundOperation == null) {
                    Operation op = (Operation) opIter.next();
                    if (op.getName().equals(operationName)) {
                        LOG.debug("Found operation " + operationName + ", checking input / output messages");
                        Message input = op.getInput().getMessage();
                        Message output = op.getOutput().getMessage();
                        if (input.getQName().equals(inputMessage) &&
                            output.getQName().equals(outputMessage)) {
                            foundOperation = op;
                            LOG.debug("Input / output messages match");
                            break;
                        }
                    }
                }
            }
        }
        return foundOperation;
    }
    
    
    public static boolean serviceHasCql2TransferOperation(EndpointReferenceType epr) throws WSDLException {
        Operation transferOp = getOperation(epr, 
            TransferMethodConstants.CQL2_TRANSFER_QUERY_METHOD_INPUT_MESSAGE,
            TransferMethodConstants.CQL2_TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE,
            TransferMethodConstants.CQL2_TRANSFER_QUERY_METHOD_NAME);
        return transferOp != null;
    }
    
    
    public static boolean serviceHasCql1TransferOperation(EndpointReferenceType epr) throws WSDLException {
        Operation transferOp = getOperation(epr,
            TransferMethodConstants.TRANSFER_QUERY_METHOD_INPUT_MESSAGE,
            TransferMethodConstants.TRANSFER_QUERY_METHOD_OUTPUT_MESSAGE, 
            TransferMethodConstants.TRANSFER_QUERY_METHOD_NAME);
        return transferOp != null;
    }
    
    
    public static boolean serviceHasCql2EnumerationOperation(EndpointReferenceType epr) throws WSDLException {
        Operation enumerationOp = getOperation(epr, 
            EnumerationMethodConstants.CQL2_ENUMERATION_QUERY_INPUT_MESSAGE,
            EnumerationMethodConstants.CQL2_ENUMERATION_QUERY_OUTPUT_MESSAGE,
            EnumerationMethodConstants.CQL2_ENUMERATION_QUERY_METHOD_NAME);
        return enumerationOp != null;
    }
    
    
    public static boolean serviceHasCql1EnumerationOperation(EndpointReferenceType epr) throws WSDLException {
        Operation enumerationOp = getOperation(epr, 
            EnumerationMethodConstants.ENUMERATION_QUERY_INPUT_MESSAGE,
            EnumerationMethodConstants.ENUMERATION_QUERY_OUTPUT_MESSAGE,
            EnumerationMethodConstants.ENUMERATION_QUERY_METHOD_NAME);
        return enumerationOp != null;
    }
    
    
    public static void main(String[] args) {
        try {
            EndpointReferenceType epr = new EndpointReferenceType(new Address(
                "http://localhost:8080/wsrf/services/cagrid/DataEnum"));
            System.out.println("Has enum? " + serviceHasCql2EnumerationOperation(epr));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
