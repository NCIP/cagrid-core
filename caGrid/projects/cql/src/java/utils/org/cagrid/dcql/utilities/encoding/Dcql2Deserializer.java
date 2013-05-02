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
package org.cagrid.dcql.utilities.encoding;

import javax.xml.namespace.QName;

import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.message.MessageElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Dcql2Deserializer extends DeserializerImpl implements Deserializer {
    public QName xmlType;
    public Class<?> javaType;
    
    private Locator locator = null;
    
    protected static Log LOG = LogFactory.getLog(Dcql2Deserializer.class.getName());


    public Dcql2Deserializer(Class<?> javaType, QName xmlType) {
        super();
        this.xmlType = xmlType;
        this.javaType = javaType;
    }
    
    
    /**
     * If the SAX parser supports a Locator, this will get invoked before startDocument()
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }
    
    
    public void onEndElement(String namespace, String localName, DeserializationContext context) throws SAXException {
        long start = System.currentTimeMillis();
        
        
        Unmarshaller unmarshall = new Unmarshaller(javaType);
        try {
            unmarshall.setResolver(Dcql2SerialzationHelper.getResolver());
        } catch (Exception ex) {
            String error = "Error setting DCQL 2 castor mapping: " + ex.getMessage();
            LOG.error(error, ex);
            throw new SAXException(error, ex);
        }

        MessageElement msgElem = context.getCurElement();
        Element asDOM = null;
        try {
            asDOM = msgElem.getAsDOM();
        } catch (Exception ex) {
            String error = "Problem extracting message type! Result will be null! " + ex.getMessage();
            LOG.error(error, ex);
            throw new SAXException(error, ex);
        }
        if (asDOM != null) {
            try {
                value = unmarshall.unmarshal(asDOM);
            } catch (MarshalException e) {
                String message = "Problem with DCQL 2 castor unmarshalling! " + e.getMessage();
                LOG.error(message, e);
                throw new SAXParseException(message, locator, e);
            } catch (ValidationException e) {
                String message = "DCQL 2 XML does not match schema! " + e.getMessage();
                LOG.error(message, e);
                throw new SAXParseException(message, locator, e);
            }
        }
        LOG.trace("Derialized " + localName + " in " + (System.currentTimeMillis() - start) + " ms");
    }
}
