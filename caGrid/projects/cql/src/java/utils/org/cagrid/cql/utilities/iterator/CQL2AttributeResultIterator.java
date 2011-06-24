package org.cagrid.cql.utilities.iterator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.server.AxisServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.cql2.results.CQLAttributeResult;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.xml.sax.Attributes;

/** 
 *  CQL2AttributeResultIterator
 *  Iterator over attribute results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 20, 2006 
 * @version $Id$ 
 */
public class CQL2AttributeResultIterator implements Iterator<Object> {
    
    public static final QName CQL2_ATTRIBUTE_RESULT_QNAME = 
        new QName("http://CQL.caBIG/2/org.cagrid.cql2.results", "CQLAttributeResult");
    
    public static Log LOG = LogFactory.getLog(CQL2AttributeResultIterator.class);
    
	private CQLAttributeResult[] results;
    private boolean xmlOnly;
	private int currentIndex;
	private MessageContext xmlMessageContext = null;
	
	CQL2AttributeResultIterator(CQLAttributeResult[] results, boolean xmlOnly) {
		this.results = results;
        this.xmlOnly = xmlOnly;
		this.currentIndex = -1;
	}
	

	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
	}


	public boolean hasNext() {
		return currentIndex + 1 < results.length;
	}


	/**
	 * @return TargetAttribute[] unless xmlOnly == true, 
	 * then a serialized CQLAttributeResult
	 */
	public Object next() {
        if (currentIndex >= results.length - 1) {
            // works because on first call, currentIndex == -1
            throw new NoSuchElementException();
        }
        Object value = null;
		currentIndex++;
		CQLAttributeResult result = results[currentIndex];
        if (xmlOnly) {
            try {
                value = serializeAttibuteResult(result);
            } catch (Exception ex) {
                throw new RuntimeException("Error serializing attribute results: " + ex.getMessage(), ex);
            }
        } else {
            value = result.getAttribute();
        }
        return value;
	}
	
	
	private String serializeAttibuteResult(CQLAttributeResult result) throws Exception {
	    StringWriter writer = new StringWriter();
	    
	    // derive a message element for the object
        MessageElement element = (MessageElement) ObjectSerializer.toSOAPElement(result, CQL2_ATTRIBUTE_RESULT_QNAME);
        
        // create a serialization context to use the new message context
        SerializationContext serializationContext = new SerializationContext(writer, getMessageContext()) {
            public void serialize(QName elemQName, Attributes attributes, Object value)
                throws IOException {
                serialize(elemQName, attributes, value, null, Boolean.FALSE, null);
            }
        };
        serializationContext.setPretty(true);

        // output the message element through the serialization context
        element.output(serializationContext);
        writer.write("\n");
        writer.flush();
	    return writer.getBuffer().toString();
	}

	
	private MessageContext getMessageContext() {
	    if (xmlMessageContext == null) {
	        AxisEngine axisEngine = null;
	        InputStream cql2Wsdd = getClass().getResourceAsStream(
	            "/org/cagrid/cql2/mapping/client-config.wsdd");
	        if (cql2Wsdd != null) {
	            // configure the axis engine to use the supplied wsdd file
	            EngineConfiguration engineConfig = new FileProvider(cql2Wsdd);
	            axisEngine = new AxisServer(engineConfig);
	        } else {
	            // no wsdd, do the default
	            System.err.println("DANGER WILL ROBINSON");
	            axisEngine = new AxisServer();
	        }
	        xmlMessageContext = new MessageContext(axisEngine);
	        xmlMessageContext.setEncodingStyle("");
	        xmlMessageContext.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
	        // the following two properties prevent xsd types from appearing in
	        // every single element in the serialized XML
	        xmlMessageContext.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
	        xmlMessageContext.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);
	    }
        return xmlMessageContext;
    }
}
