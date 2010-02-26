package org.cagrid.dcql.utilities.encoding;

import gov.nih.nci.cagrid.encoding.AxisContentHandler;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;
import org.apache.axis.wsdl.fromJava.Types;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

public class Dcql2Serializer implements Serializer {
    
    private static Log LOG = LogFactory.getLog(Dcql2Serializer.class);

    public void serialize(QName name, Attributes attributes, Object value, SerializationContext context)
        throws IOException {
        long start = System.currentTimeMillis();
        
        // load the mapping
        Mapping map = new Mapping();
        try {
            map.loadMapping(Dcql2SerialzationHelper.getMapping());
        } catch (MappingException ex) {
            ex.printStackTrace();
            LOG.error("Unable to load  DCQL 2 castor mapping: " + ex.getMessage(), ex);
        }
        
        AxisContentHandler hand = new AxisContentHandler(context);
        Marshaller marshaller = new Marshaller(hand);
        try {
            marshaller.setMapping(map);
        } catch (MappingException ex) {
            String error = "Error setting DCQL 2 castor mapping: " + ex.getMessage();
            LOG.error(error, ex);
            IOException ioe = new IOException(error);
            ioe.initCause(ex);
            throw ioe;
        }
        marshaller.setSuppressXSIType(true);
        marshaller.setValidation(true);
        
        try {
            marshaller.marshal(value);
        } catch (MarshalException e) {
            String message = "Problem using castor marshalling for DCQL 2: " + e.getMessage();
            LOG.error(message, e);
            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        } catch (ValidationException e) {
            String message = "Problem validating castor marshalling; " +
                "message doesn't comply with the associated XML schema: " + e.getMessage();
            LOG.error(message, e);
            IOException ioe = new IOException(message);
            ioe.initCause(e);
            throw ioe;
        }
        
        LOG.trace("Serialized " + name.getLocalPart() + " in " + (System.currentTimeMillis() - start) + " ms");
    }


    @SuppressWarnings("unchecked")
    public Element writeSchema(Class javaType, Types types) throws Exception {
        return null;
    }


    public String getMechanismType() {
        return Constants.AXIS_SAX;
    }
}
