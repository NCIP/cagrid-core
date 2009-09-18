package gov.nih.nci.cagrid.wsenum;

import gov.nih.nci.cagrid.common.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import junit.framework.TestCase;

import org.apache.axis.AxisEngine;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.MessageContext;
import org.apache.axis.configuration.FileProvider;
import org.apache.axis.server.AxisServer;
import org.apache.axis.types.Duration;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;
import org.projectmobius.bookstore.Book;

/** 
 *  CompleteEnumIteratorBaseTest
 *  Test case for complete enumeration iterator implementations
 *  
 * @author David Ervin
 * 
 * @created Apr 10, 2007 12:16:58 PM
 * @version $Id: CompleteEnumIteratorBaseTest.java,v 1.4 2008-11-04 15:27:15 dervin Exp $ 
 */
public abstract class CompleteEnumIteratorBaseTest extends TestCase {
    
    private List<Object> objectList;
    private EnumIterator enumIterator;
    private String iteratorClassName;
    
    public CompleteEnumIteratorBaseTest(String iteratorClassName) {
        super("EnumIter testing for " + iteratorClassName);
        this.iteratorClassName = iteratorClassName;
    }
    
    
    protected List<Object> getObjectList() {
        return objectList;
    }
    
    
    protected EnumIterator getEnumIterator() {
        return enumIterator;
    }
    

    public void setUp() {
        // need a list of data objects
        objectList = new ArrayList<Object>();
        for (int i = 0; i < 10; i++) {
            Book b = new Book();
            b.setAuthor("caGrid Testing Book " + i);
            b.setISBN(String.valueOf(i));
            objectList.add(b);
        }
        // set up the enum iterator
        try {
            Class iterClass = Class.forName(iteratorClassName);
            Method createIteratorMethod = iterClass.getDeclaredMethod(
                "createIterator", new Class[] {List.class, QName.class});
            Object[] args = {objectList, TestingConstants.BOOK_QNAME};
            enumIterator = (EnumIterator) createIteratorMethod.invoke(null, args);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error initializing the Concurrent Iterator: " + ex.getMessage());
        }           
    }
    
    
    public void tearDown() {
        enumIterator.release();
        try {
            enumIterator.next(new IterationConstraints());
            fail("Enumeration released, but did not throw exception on next() call");
        } catch (Exception ex) {
            assertEquals("Unexpected exception thrown", 
                NoSuchElementException.class.getName(), ex.getClass().getName());
            enumIterator = null;
        }
    }
    
    
    public void testRetrieveSingleResult() {
        IterationConstraints cons = new IterationConstraints(1, -1, null);
        IterationResult result = enumIterator.next(cons);
        SOAPElement[] rawElements = result.getItems();
        assertTrue("No elements were returned", rawElements != null);
        assertEquals("Unexpected number of results returned", 1, rawElements.length);
        // deserialize the result
        try {
            String xml = rawElements[0].toString();
            Book b = (Book) deserializeDocumentString(xml, Book.class);
            boolean found = bookInOriginalList(b);
            assertTrue("Returned book was not found in original object list", found);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error deserializing result: " + ex.getMessage());
        }
    }
    
    
    public void testRetrieveMultipleResults() {
        IterationConstraints cons = new IterationConstraints(3, -1, null);
        IterationResult result = enumIterator.next(cons);
        SOAPElement[] rawElements = result.getItems();
        assertTrue("No elements were returned", rawElements != null);
        assertEquals("Unexpected number of results returned", 3, rawElements.length);
        for (int i = 0; i < rawElements.length; i++) {
            // deserialize the result
            try {
                Book b = (Book) deserializeDocumentString(
                    rawElements[i].toString(), Book.class);
                boolean found = bookInOriginalList(b);
                assertTrue("Returned book not found in original object list", found);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error deserializing result: " + ex.getMessage());
            }
        }
    }
    
    
    public void testRetrieveAllResults() {
        // ask for more results than we actually have
        IterationConstraints cons = new IterationConstraints(objectList.size() + 1, -1, null);
        IterationResult result = enumIterator.next(cons);
        SOAPElement[] rawElements = result.getItems();
        assertTrue("No elements were returned", rawElements != null);
        assertEquals("Unexpected number of results returned", 
            objectList.size(), rawElements.length);
        assertTrue("End of was not sequence reached", result.isEndOfSequence());
        for (int i = 0; i < rawElements.length; i++) {
            // deserialize the result
            try {
                Book b = (Book) deserializeDocumentString(
                    rawElements[i].toString(), Book.class);
                boolean found = bookInOriginalList(b);
                assertTrue("Returned book not found in original object list", found);
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Error deserializing result: " + ex.getMessage());
            }
        }
    }
    
    
    public void testResultsTimeout() {
        slowDownIterator();
        // this duration (1 sec) should time out
        Duration maxWait = new Duration(false, 0, 0, 0, 0, 0, 1);
        // ask for all the results
        IterationConstraints cons = new IterationConstraints(
            objectList.size(), -1, maxWait);
        try {
            // this should timeout
            enumIterator.next(cons);
            fail("Query did not time out");
        } catch (TimeoutException ex) {
            // expected
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Unexpected exception of type " 
                + ex.getClass().getName() + " occured: " + ex.getMessage());
        }
    }
    
    
    public void testCharLimitExceded() {
        // ask for all the results, but only enough chars for the first element
        int charCount = -1;
        StringWriter writer = new StringWriter();
        try {
            Utils.serializeObject(objectList.get(0), 
                TestingConstants.BOOK_QNAME, writer);
            charCount = (writer.getBuffer().length() * 2) - 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error determining object char count: " + ex.getMessage());
        }
        IterationConstraints cons = new IterationConstraints(
            objectList.size(), charCount, null);
        IterationResult result = enumIterator.next(cons);
        SOAPElement[] rawResults = result.getItems();
        assertTrue("Enumeration did not return results", rawResults != null);
        assertFalse("Enumeration mistakenly returned all results", 
            rawResults.length == objectList.size());
        assertEquals("Unexpected number of results returned", 1, rawResults.length);
        // verify content
        Book original = null;
        Book returned = null;
        try {
            original = (Book) deserializeDocumentString(
                writer.getBuffer().toString(), Book.class);
            returned = (Book) deserializeDocumentString(
                rawResults[0].toString(), Book.class);
        } catch (Exception ex) {
            fail("Error deserializing objects: " + ex.getMessage());
        }
        boolean equal = original.getAuthor().equals(returned.getAuthor()) 
            && original.getISBN().equals(returned.getISBN());
        assertTrue("Expected book and returned book do not match", equal);
    }
    
    
    protected boolean bookInOriginalList(Book g) {
        // verify the book is part of the original object list
        for (int i = 0; i < objectList.size(); i++) {
            Book current = (Book) objectList.get(i);
            if (current.getAuthor().equals(g.getAuthor()) 
                && current.getISBN().equals(g.getISBN())) {
                return true;
            }
        }
        return false;
    }
    
    
    protected Object deserializeDocumentString(String xmlDocument, Class objectClass) throws Exception {
        return Utils.deserializeObject(new StringReader(xmlDocument), objectClass);
    }
    
    
    protected MessageContext createMessageContext(InputStream configStream) {
        EngineConfiguration engineConfig = new FileProvider(configStream);
        AxisEngine engine = new AxisServer(engineConfig);
        
        MessageContext context = new MessageContext(engine);
        context.setEncodingStyle("");
        context.setProperty(AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        // the following two properties prevent xsd types from appearing in
        // every single element in the serialized XML
        context.setProperty(AxisEngine.PROP_EMIT_ALL_TYPES, Boolean.FALSE);
        context.setProperty(AxisEngine.PROP_SEND_XSI, Boolean.FALSE);
        return context;
    }
    
    
    /**
     * "Fixes" the reader for xml data to wait 500ms every time it reads a line
     * from disk.  This effectively slows down the calls to next() inside 
     * the iterator to the point that timeouts are a real possibility
     */
    protected void slowDownIterator() {
        // parent class has the file reader
        Class iterClass = enumIterator.getClass().getSuperclass();
        try {
            Field readerField = iterClass.getDeclaredField("fileReader");
            readerField.setAccessible(true);
            BufferedReader originalReader = (BufferedReader) readerField.get(enumIterator);
            BufferedReader slowReader = new BufferedReader(originalReader) {
                public String readLine() throws IOException {
                    try {
                        Thread.sleep(500);
                    } catch (Exception ex) {
                        // whatever
                    }
                    return super.readLine();
                }
            };
            readerField.set(enumIterator, slowReader);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Error slowing down the iterator: " + ex.getMessage());
        }
    }
}
