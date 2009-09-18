package gov.nih.nci.cagrid.wsenum;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.wsenum.utils.SimplePersistantObjectIterator;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.soap.SOAPElement;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.axis.types.Duration;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.projectmobius.bookstore.Book;

/** 
 *  SimpleEnumIterTestCase
 *  Test case to test the simple enumeration iterator
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Nov 3, 2006 
 * @version $Id$ 
 */
public class SimpleEnumIterTestCase extends TestCase {
	
	private List<Object> objectList;
	private EnumIterator enumIterator;
	
	public SimpleEnumIterTestCase(String name) {
		super(name);
	}
	
	
	public void setUp() {
		// need a list of data objects
		objectList = new ArrayList<Object>();
		for (int i = 0; i < 10; i++) {
			Book b = new Book();
			b.setAuthor("caGrid Book Author " + i);
			b.setISBN("Fake book Number " + i);
			objectList.add(b);
		}
		// set up the enum iterator
		try {
			enumIterator = SimplePersistantObjectIterator.createIterator(
                objectList, TestingConstants.BOOK_QNAME, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Error initializing the Simple Iterator: " + ex.getMessage());
		}			
	}
	
	
	public void tearDown() {
		enumIterator.release();
		try {
			enumIterator.next(new IterationConstraints());
			fail("Enumeration released, but did not throw exception on next() call");
		} catch (Exception ex) {
			assertTrue("Enumeration released, threw " + NoSuchElementException.class.getName() + " on next()", 
				ex instanceof NoSuchElementException);
		}
	}
	
	
	public void testRetrieveSingleResult() {
		// the simple iterator ignores this anyway
		Duration maxWait = new Duration();
		maxWait.setSeconds(10d);
		IterationConstraints cons = new IterationConstraints(1, -1, maxWait);
		IterationResult result = enumIterator.next(cons);
		SOAPElement[] rawElements = result.getItems();
		assertTrue("Some elements were returned", rawElements != null);
		assertTrue("Only one result was returned", rawElements.length == 1);
		// deserialize the result
		try {
            String xml = rawElements[0].toString();
            Book b = (Book) deserializeDocumentString(
				xml, Book.class);
			boolean found = bookInOriginalList(b);
			assertTrue("Returned book found in original object list", found);
		} catch (Exception ex) {
			ex.printStackTrace();
			fail("Error deserializing result: " + ex.getMessage());
		}
	}
	
	
	public void testRetrieveMultipleResults() {
		// the simple iterator ignores this anyway
		Duration maxWait = new Duration();
		maxWait.setSeconds(10d);
		IterationConstraints cons = new IterationConstraints(3, -1, maxWait);
		IterationResult result = enumIterator.next(cons);
		SOAPElement[] rawElements = result.getItems();
		assertTrue("Some elements were returned", rawElements != null);
		assertTrue("Three results were returned", rawElements.length == 3);
		for (int i = 0; i < rawElements.length; i++) {
			// deserialize the result
			try {
                Book b = (Book) deserializeDocumentString(
					rawElements[i].toString(), Book.class);
				boolean found = bookInOriginalList(b);
				assertTrue("Returned book found in original object list", found);
			} catch (Exception ex) {
				ex.printStackTrace();
				fail("Error deserializing result: " + ex.getMessage());
			}
		}
	}
	
	
	public void testRetrieveAllResults() {
		// the simple iterator ignores this anyway
		Duration maxWait = new Duration();
		maxWait.setSeconds(10d);
		// ask for more results than we actually have
		IterationConstraints cons = new IterationConstraints(objectList.size() + 1, -1, maxWait);
		IterationResult result = enumIterator.next(cons);
		SOAPElement[] rawElements = result.getItems();
		assertTrue("Some elements were returned", rawElements != null);
		assertTrue(String.valueOf(objectList.size()) + " results were returned", 
			rawElements.length == objectList.size());
		assertTrue("End of sequence reached", result.isEndOfSequence());
		for (int i = 0; i < rawElements.length; i++) {
			// deserialize the result
			try {
                Book b = (Book) deserializeDocumentString(
					rawElements[i].toString(), Book.class);
				boolean found = bookInOriginalList(b);
				assertTrue("Returned book found in original object list", found);
			} catch (Exception ex) {
				ex.printStackTrace();
				fail("Error deserializing result: " + ex.getMessage());
			}
		}
	}
	
	
	private boolean bookInOriginalList(Book b) {
		// verify the book is part of the original object list
		for (int i = 0; i < objectList.size(); i++) {
            Book current = (Book) objectList.get(i);
			if (current.getAuthor().equals(b.getAuthor()) 
				&& current.getISBN().equals(b.getISBN())) {
				return true;
			}
		}
		return false;
	}
	
	
	private Object deserializeDocumentString(String xmlDocument, Class objectClass) throws Exception {
		return Utils.deserializeObject(new StringReader(xmlDocument), objectClass);
	}
	

	public static void main(String[] args) {
		TestRunner runner = new TestRunner();
		TestResult result = runner.doRun(new TestSuite(SimpleEnumIterTestCase.class));
		System.exit(result.errorCount() + result.failureCount());
	}
}
