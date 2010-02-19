package org.cagrid.data.test.system.enumeration;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.enumeration.client.EnumerationDataServiceClient;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.testing.system.deployment.ServiceContainer;
import gov.nih.nci.cagrid.testing.system.haste.Step;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.apache.axis.message.PrefixedQName;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI;
import org.apache.axis.utils.ClassUtils;
import org.cagrid.cql2.AttributeValue;
import org.cagrid.cql2.BinaryPredicate;
import org.cagrid.cql2.CQLAttribute;
import org.cagrid.cql2.CQLGroup;
import org.cagrid.cql2.CQLQuery;
import org.cagrid.cql2.CQLTargetObject;
import org.cagrid.cql2.GroupLogicalOperator;
import org.cagrid.cql2.UnaryPredicate;
import org.cagrid.data.test.system.TestQueryResultsGenerator;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.projectmobius.bookstore.Book;
import org.xmlsoap.schemas.ws._2004._09.enumeration.DataSource;
import org.xmlsoap.schemas.ws._2004._09.enumeration.Release;

/** 
 *  InvokeCql2EnumerationDataServiceStep
 *  Testing step to use CQL 2 to invoke an enumeration data service
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>  * 
 * @created Nov 23, 2006 
 * @version $Id: InvokeEnumerationDataServiceStep.java,v 1.3 2009-04-17 19:41:52 dervin Exp $ 
 */
public class InvokeCql2EnumerationDataServiceStep extends Step {
	
    private ServiceContainer container;
	private String serviceName;
	
	public InvokeCql2EnumerationDataServiceStep(ServiceContainer container, String serviceName) {
        this.container = container;
		this.serviceName = serviceName;
	}
	
    
    private DataSource createDataSource(EndpointReferenceType epr) throws RemoteException {
        InputStream resourceAsStream = ClassUtils.getResourceAsStream(
            InvokeCql2EnumerationDataServiceStep.class, "client-config.wsdd");
        DataSource port = EnumerationResponseHelper.createDataSource(epr, resourceAsStream);
        return port;
    }
    

	public void runStep() throws Throwable {
		System.out.println("Running step " + getClass().getName());
		String serviceUrl = getServiceUrl();
		System.out.println("Invoking service at URL " + serviceUrl);
		// create the generic enumeration client
		EnumerationDataServiceClient client = new EnumerationDataServiceClient(serviceUrl);
		
		// iterate over an enumeration response
		iterateEnumeration(client);
		
        // make sure invalid classes still throw exceptions
		queryForInvalidClass(client);
		
        // make sure malformed queries behave as expected
		submitMalformedQuery(client);
    }
	
	
	private void queryForInvalidClass(EnumerationDataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		CQLTargetObject target = new CQLTargetObject();
		target.setClassName("non.existant.class");
		query.setCQLTargetObject(target);
		EnumerationResponseContainer enumContainer = null;
		try {
			enumContainer = client.executeEnumerationQuery(query);
		} catch (QueryProcessingExceptionType ex) {
			assertTrue("Query Processing Exception Type thrown", true);
		} catch (Exception ex) {
		    ex.printStackTrace();
		    fail("Unexpected exception thrown: " + ex.getClass().getName());
		} finally {
			if (enumContainer != null && enumContainer.getContext() != null) {
				Release release = new Release();
				release.setEnumerationContext(enumContainer.getContext());
                createDataSource(enumContainer.getEPR()).releaseOp(release);
			}
		}
	}
	
	
	private void submitMalformedQuery(EnumerationDataServiceClient client) throws Exception {
		CQLQuery query = new CQLQuery();
		CQLTargetObject target = new CQLTargetObject();
		target.setClassName(Book.class.getName());
		CQLAttribute attrib = new CQLAttribute();
		attrib.setName("name");
		attrib.setBinaryPredicate(BinaryPredicate.LIKE);
		AttributeValue val1 = new AttributeValue();
		val1.setStringValue("E%");
		attrib.setAttributeValue(val1);
		target.setCQLAttribute(attrib);
		CQLGroup group = new CQLGroup();
		group.setLogicalOperation(GroupLogicalOperator.AND);
		CQLAttribute a1 = new CQLAttribute();
		a1.setName("author");
		a1.setUnaryPredicate(UnaryPredicate.IS_NOT_NULL);
		CQLAttribute a2 = new CQLAttribute();
		a2.setName("ISBN");
		a2.setUnaryPredicate(UnaryPredicate.IS_NULL);
		group.setCQLAttribute(new CQLAttribute[] {a1, a2});
		target.setCQLGroup(group);
		query.setCQLTargetObject(target);
		EnumerationResponseContainer enumContainer = null;
		try {
			enumContainer = client.executeEnumerationQuery(query);
		} catch (MalformedQueryExceptionType ex) {
			assertTrue("Malformed Query Exception Type thrown", true);
		} finally {
			if (enumContainer != null && enumContainer.getContext() != null) {
				Release release = new Release();
				release.setEnumerationContext(enumContainer.getContext());
				createDataSource(enumContainer.getEPR()).releaseOp(release);
			}
		}		
	}
	
	
	private EnumerationResponseContainer queryForBooks(EnumerationDataServiceClient client) throws Exception {
	    CQLQuery query = new CQLQuery();
	    CQLTargetObject target = new CQLTargetObject();
	    target.setClassName(Book.class.getName());
	    query.setCQLTargetObject(target);
		EnumerationResponseContainer enumContainer = null;
		try {
			enumContainer = client.executeEnumerationQuery(query);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return enumContainer;
	}
	
	
	private void iterateEnumeration(EnumerationDataServiceClient client) throws Exception {
		EnumerationResponseContainer enumContainer = queryForBooks(client);
        
        DataSource dataSource = createDataSource(enumContainer.getEPR());
        
		/*
		 * This is the preferred way to access an enumeration, but the client enum iterator hides
		 * remote exceptions from the user and throws an empty NoSuchElement exception.
		 */
		ClientEnumIterator iter = new ClientEnumIterator(dataSource, enumContainer.getContext());
        List<Book> returnedObjects = new LinkedList<Book>();
        try {
            while (iter.hasNext()) {
                SOAPElement elem = (SOAPElement) iter.next();
                String elemText = elem.toString();
                // ensure 'Book' at least appears in the text
                int bookIndex = elemText.indexOf("Book");
                if (bookIndex == -1) {
                    throw new NoSuchElementException("Element returned was not of the type Book!");
                }
                // CQL 2 enumerations return CQLResult instances, so inspect the element to find the Book
                QName bookQName = new QName("gme://projectmobius.org/1/BookStore", "Book");
                // Java 6 only... won't compile on 5.  Need a javax.xml.soap.Name impl
                // Iterator<?> bookElemIter = elem.getChildElements(bookName);
                PrefixedQName bookName = new PrefixedQName(bookQName);
                Iterator<?> bookElemIter = elem.getChildElements(bookName);
                assertTrue("No elements of type " + bookName + 
                    " found in result", bookElemIter.hasNext());
                SOAPElement bookElement = (SOAPElement) bookElemIter.next();
                assertFalse("More than one element of type " + bookName + 
                    " found in result", bookElemIter.hasNext());
                String bookText = bookElement.toString();
                Book book = null;
                try {
                    book = Utils.deserializeObject(new StringReader(bookText), Book.class);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fail("Error deserializing result from enumeration: " + ex.getMessage());
                }
                returnedObjects.add(book);
            }
        } catch (NoSuchElementException ex) {
            if (returnedObjects.size() == 0) {
                throw ex;
            }
        } finally {
            iter.release();
            try {
                iter.next();
                fail("Call to next() after release should have failed!");
            } catch (NoSuchElementException ex) {
                // expected
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("Exception other than NoSuchElementException thrown: " + ex.getClass().getName());
            }
        }
        List<Book> goldObjects = TestQueryResultsGenerator.getResultBooks();
        // same number of results?
        assertEquals("Unexpected number of results returned from the enumeration", goldObjects.size(), returnedObjects.size());
        // verify each returned object matches one of the expected objects
        Iterator<?> returnedObjectIter = returnedObjects.iterator();
        while (returnedObjectIter.hasNext()) {
            Book returnedBook = (Book) returnedObjectIter.next();
            boolean bookFound = goldObjects.contains(returnedBook);
            if (!bookFound) {
                // serialize it so we can see what happened
                StringWriter writer = new StringWriter();
                Utils.serializeObject(returnedBook, Book.getTypeDesc().getXmlType(), writer);
                System.err.println("Unexpected object found in results:");
                System.err.println(writer.getBuffer().toString());
                fail("Unexpected object found in results");
            }
        }
        // verify every gold object exists in the results
        Iterator<Book> goldObjectIter = goldObjects.iterator();
        while (goldObjectIter.hasNext()) {
            Book goldBook = goldObjectIter.next();
            boolean bookFound = returnedObjects.contains(goldBook);
            if (!bookFound) {
                // serialize it so we can see what happened
                StringWriter writer = new StringWriter();
                Utils.serializeObject(goldBook, Book.getTypeDesc().getXmlType(), writer);
                System.err.println("Expected object NOT found in results:");
                System.err.println(writer.getBuffer().toString());
                fail("Expected object NOT found in results");
            }
        }
	}
	
	
	private String getServiceUrl() throws Exception {
        URI baseUri = container.getContainerBaseURI();
        return baseUri.toString() + "cagrid/" + serviceName;
	}
}
