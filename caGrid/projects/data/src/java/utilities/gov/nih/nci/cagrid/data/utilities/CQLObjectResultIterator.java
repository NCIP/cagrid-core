package gov.nih.nci.cagrid.data.utilities;

import gov.nih.nci.cagrid.common.ConfigurableObjectDeserializationContext;
import gov.nih.nci.cagrid.cqlresultset.CQLObjectResult;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.server.AxisServer;
import org.xml.sax.InputSource;
/** 
 *  CQLObjectResultIterator
 *  Iterator over CQL Object Results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 20, 2006 
 * @version $Id$ 
 */
public class CQLObjectResultIterator implements Iterator {
	private CQLObjectResult[] results;
	private int currentIndex;
	private String targetClassName;
	private Class objectClass;
	private boolean xmlOnly;
	private InputStream wsddInputStream;
	private byte[] wsddBytes;
	private MessageContext messageContext;
	
	CQLObjectResultIterator(CQLObjectResult[] results, String targetName, 
		boolean xmlOnly, InputStream wsdd) {
		this.targetClassName = targetName;
		this.results = results;
		this.currentIndex = -1;
		this.xmlOnly = xmlOnly;
		this.wsddInputStream = wsdd;
	}
	

	public void remove() {
		throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
	}


	public boolean hasNext() {
		return currentIndex + 1 < results.length;
	}


	public Object next() {
        if (currentIndex >= results.length - 1) {
            // works because on first call, currentIndex == -1
            throw new NoSuchElementException();
        }
		currentIndex++;
		MessageElement element = results[currentIndex].get_any()[0];
		try {
			if (messageContext == null) {
				messageContext = createMessageContext(getConsumableInputStream());
			}
			String documentString = serializeMessageElement(element, messageContext);
	        if (xmlOnly) {
				return documentString;
			}
			InputSource objectSource = new InputSource(new StringReader(documentString));
			ConfigurableObjectDeserializationContext desContext	= 
				new ConfigurableObjectDeserializationContext(messageContext, objectSource, getTargetClass());
			return desContext.getValue();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}
	
	
	private InputStream getConsumableInputStream() throws IOException {
		if (wsddInputStream != null) {
			if (wsddBytes == null) {
				wsddBytes = new byte[0];
				byte[] readBytes = new byte[1024];
				int len = -1;
				BufferedInputStream buffStream = new BufferedInputStream(wsddInputStream);
				while ((len = buffStream.read(readBytes)) != -1) {
					byte[] tmpContent = new byte[wsddBytes.length + len];
					System.arraycopy(wsddBytes, 0, tmpContent, 0, wsddBytes.length);
					System.arraycopy(readBytes, 0, tmpContent, wsddBytes.length, len);
					wsddBytes = tmpContent;
				}
				buffStream.close();
			}
			return new ByteArrayInputStream(wsddBytes);
		}
		return null;
	}
	
	
	private Class getTargetClass() {
		if (objectClass == null) {
			try {
				objectClass = Class.forName(targetClassName);
			} catch (ClassNotFoundException ex) {
				NoSuchElementException nse = new NoSuchElementException(ex.getMessage());
				nse.setStackTrace(ex.getStackTrace());
				throw nse;
			}
		}
		return objectClass;
	}
	
	
	private String serializeMessageElement(MessageElement element, MessageContext context) throws Exception {
		StringWriter writer = new StringWriter();
		// create a serialization context to use the new message context
		SerializationContext serializationContext = new SerializationContext(writer, context);
		
		serializationContext.setPretty(false);
		
		element.output(serializationContext);
		
        return writer.getBuffer().toString();
	}
	
	
	private MessageContext createMessageContext(InputStream configStream) {
		AxisEngine engine = null;
		if (configStream != null) {
			// configure the axis engine to use the supplied wsdd file
			EngineConfiguration engineConfig = new FileProvider(configStream);
			engine = new AxisServer(engineConfig);
		} else {
			engine = new AxisServer();
		}
		
		MessageContext context = new MessageContext(engine);
		context.setEncodingStyle("");
		context.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
		// the following two properties prevent xsd types from appearing in
		// every single element in the serialized XML
		context.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
		context.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);
		return context;
	}
}
