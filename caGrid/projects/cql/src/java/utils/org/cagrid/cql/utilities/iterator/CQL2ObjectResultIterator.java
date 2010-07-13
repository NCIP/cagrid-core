package org.cagrid.cql.utilities.iterator;

import gov.nih.nci.cagrid.common.Utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.cagrid.cql.utilities.AnyNodeHelper;
import org.cagrid.cql2.results.CQLObjectResult;
import org.exolab.castor.types.AnyNode;


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
        AnyNode node = results[currentIndex].get_any();
        try {
            String documentString = AnyNodeHelper.convertAnyNodeToString(node);
            if (xmlOnly) {
                return documentString;
            }
            Object value = Utils.deserializeObject(
                new StringReader(documentString), getTargetClass(), getConsumableInputStream());
            return value;
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
}
