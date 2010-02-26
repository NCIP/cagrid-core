package org.cagrid.cql.utilities.iterator;

import gov.nih.nci.cagrid.common.ConfigurableObjectDeserializationContext;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.server.AxisServer;
import org.cagrid.cql2.results.CQLObjectResult;
import org.exolab.castor.types.AnyNode;
import org.xml.sax.InputSource;


/**
 * CQLObjectResultIterator
 * Iterator over CQL 2 Object Results
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Mar 20, 2006
 * @version $Id$
 */
public class CQL2ObjectResultIterator implements Iterator<Object> {
    private CQLObjectResult[] results;
    private int currentIndex;
    private String targetClassName;
    private Class<?> objectClass;
    private boolean xmlOnly;
    private InputStream wsddInputStream;
    private byte[] wsddBytes;
    private MessageContext messageContext;


    CQL2ObjectResultIterator(CQLObjectResult[] results, String targetName, boolean xmlOnly, InputStream wsdd) {
        this.targetClassName = targetName;
        this.results = results;
        this.currentIndex = -1;
        this.xmlOnly = xmlOnly;
        this.wsddInputStream = wsdd;
    }


    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported by " + getClass().getName());
    }


    public synchronized boolean hasNext() {
        return currentIndex + 1 < results.length;
    }


    public synchronized Object next() {
        if (currentIndex >= results.length - 1) {
            // works because on first call, currentIndex == -1
            throw new NoSuchElementException();
        }
        currentIndex++;
        AnyNode node = (AnyNode) results[currentIndex].get_any();
        try {
            String documentString = node.getStringValue();
            if (xmlOnly) {
                return documentString;
            }
            InputSource objectSource = new InputSource(new StringReader(documentString));
            ConfigurableObjectDeserializationContext desContext = new ConfigurableObjectDeserializationContext(
                getMessageContext(), objectSource, getTargetClass());
            return desContext.getValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }


    private InputStream getConsumableInputStream() throws IOException {
        if (wsddInputStream != null) {
            if (wsddBytes == null) {
                ByteArrayOutputStream tempBytes = new ByteArrayOutputStream();
                byte[] readBytes = new byte[1024];
                int len = -1;
                BufferedInputStream buffStream = new BufferedInputStream(wsddInputStream);
                while ((len = buffStream.read(readBytes)) != -1) {
                    tempBytes.write(readBytes, 0, len);
                }
                buffStream.close();
                wsddBytes = tempBytes.toByteArray();
            }
            return new ByteArrayInputStream(wsddBytes);
        }
        return null;
    }


    private Class<?> getTargetClass() {
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


    private MessageContext getMessageContext() throws IOException {
        if (messageContext == null) {
            AxisEngine engine = null;
            InputStream configStream = getConsumableInputStream();
            if (configStream != null) {
                // configure the axis engine to use the supplied wsdd file
                EngineConfiguration engineConfig = new FileProvider(configStream);
                engine = new AxisServer(engineConfig);
            } else {
                engine = new AxisServer();
            }

            messageContext = new MessageContext(engine);
            messageContext.setEncodingStyle("");
            messageContext.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
            // the following two properties prevent xsd types from appearing in
            // every single element in the serialized XML
            messageContext.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
            messageContext.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);
        }
        return messageContext;
    }
}
